package com.blackrio.apprilia.Server;

/**
 * Created by Stefan on 31.05.2015.
 */
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.blackrio.apprilia.Bean.ServiceRecord;
import com.blackrio.apprilia.Bean.User;
import com.blackrio.apprilia.Bean.Vehicle;
import com.blackrio.apprilia.Callback.GetServiceRecordCallback;
import com.blackrio.apprilia.Callback.GetUpdatedKilometerCallback;
import com.blackrio.apprilia.Callback.GetUserCallback;
import com.blackrio.apprilia.Callback.GetVehicleCallback;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class ServerRequests {
    ProgressDialog progressDialog;
    public static final int CONNECTION_TIMEOUT = 1000 * 15;
    //public static final String SERVER_ADDRESS = "http://wi-project.technikum-wien.at/ss15/ss15-bvz4-moc-1/MOCG/";
    public static final String SERVER_ADDRESS = "http://wi-project.technikum-wien.at/ss15/ss15-bvz4-moc-1/apprilia/";
    private static final String SERVER_UNAME = "blackrio";
    private static final String SERVER_PW = "andr0id";

    public ServerRequests(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait...");
    }

//region Methoden fuer die GUI
    //METHODE USER REGISTRIEREN
    public void storeUserDataInBackground(User user, GetUserCallback userCallBack) {
        progressDialog.show();
        new StoreUserDataAsyncTask(user, userCallBack).execute();
    }

    //METHODE USER AUSLESEN
    public void fetchUserDataAsyncTask(User user, GetUserCallback userCallBack) {
        progressDialog.show();
        new FetchUserDataAsyncTask(user, userCallBack).execute();
    }

    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! hat am sonntag gefehlt
    //METHODE VEHICLES AUSLESEN
    public void fetchVehicleDataAsyncTask(GetVehicleCallback vehicleCallback){
        //progressDialog.show();
        new FetchVehicleDataAsyncTask(vehicleCallback).execute();
    }

    //METHODE KILOMETER UPDATEN
    public void updateKilometerDataInBackground(String username, int kilometer, GetUpdatedKilometerCallback kilometerCallback){ //CALLBACK FEHLT NOCH
        progressDialog.show();
        new UpdateKilometerDataAsyncTask(username, kilometer, kilometerCallback).execute(); //CALLBACK FEHLT NOCH
    }

    public void fetchServiceRecordDataAsyncTask(GetServiceRecordCallback serviceRecordCallback){
        //progressDialog.show();
        new FetchServiceRecordDataAsyncTask(serviceRecordCallback).execute();
    }

    public void updateMadeDataInBackground(ServiceRecord serviceRecord){
        //progressdialog
        Log.v("SERVER REQUESTS: ", "first method erreicht");
        new UpdateServiceRecordAsyncTask(serviceRecord).execute();
    }
//endregion

//region REGISTER storeUser
    /**
     * KLASSEN BEGINN
     * USER REGISTRIEREN
     */
    public class StoreUserDataAsyncTask extends AsyncTask<Void, Void, Void> {
        User user;
        GetUserCallback userCallBack;

        public StoreUserDataAsyncTask(User user, GetUserCallback userCallBack) {
            this.user = user;
            this.userCallBack = userCallBack;
        }

        @Override
        protected Void doInBackground(Void... params) {
            String phpFile = "Register.php";

            // Daten fuer Uebergabe vorbereiten
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("username", user.getUsername()));
            dataToSend.add(new BasicNameValuePair("password", user.getPassword()));
            dataToSend.add(new BasicNameValuePair("firstname", user.getFirstname()));
            dataToSend.add(new BasicNameValuePair("lastname", user.getLastname()));
            dataToSend.add(new BasicNameValuePair("vehicleType", user.getVehicleType()));
            dataToSend.add(new BasicNameValuePair("kilometer", user.getKilometer() + ""));
            dataToSend.add(new BasicNameValuePair("registrationDate", user.getRegistrationDate()));

            //TESTAUSGABE???
            for(NameValuePair item : dataToSend) {
                Log.v("Userdaten:", item.getValue());
            }

            //HTTP request ausfuehren (liefert einen HttpResponse zurueck der bei Insert & Update nicht benoetigt wird
            createHttpConnection(dataToSend, phpFile);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            userCallBack.done(null);
        }

    }
//endregion

//region LOGIN (fetchUserData)
    /**
     * KLASSEN BEGINN
     * USER AUSLESEN (LOGIN)
     */
    public class FetchUserDataAsyncTask extends AsyncTask<Void, Void, User> {
        User user;
        GetUserCallback userCallBack;

        public FetchUserDataAsyncTask(User user, GetUserCallback userCallBack) {
            this.user = user;
            this.userCallBack = userCallBack;
        }

        @Override
        protected User doInBackground(Void... params) {
            String phpFile = "FetchUserData.php";

            //Daten fuer Uebergabe vorbereiten
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("username", user.getUsername()));
            dataToSend.add(new BasicNameValuePair("password", user.getPassword()));

            User returnedUser = null;

            try{
                //HTTP REQUEST ausfuehren & RESPONE zurueck bekommen (String result = Ergebnis vom Response)
                HttpResponse httpResponse = createHttpConnection(dataToSend, phpFile);
                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);

                //Ergebnis des RESPONSE auslesen und in User Objekt speichern
                JSONObject jObject = new JSONObject(result);
                if (jObject.length() != 0){
                    Log.v("fetchUser DOIB", "USEROBJEKT ERHALTEN"); //???

                //Result String(JSON) aus DB in USER Objekt speichern
                    // username und password werden schon als User objekt uebergeben
                    String firstname = jObject.getString("firstname");
                    String lastname = jObject.getString("lastname");
                    String vehicleType = jObject.getString("vehicleType");
                    int kilometer = jObject.getInt("kilometerstand");
                    String registrationDate = jObject.getString("registrationdate");


                    returnedUser = new User(user.getUsername(), user.getPassword(), firstname, lastname, vehicleType, kilometer, registrationDate);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.v("FetchUser", "erledigt"); //TESTAUSGABE???
            return returnedUser;
        }

        @Override
        protected void onPostExecute(User returnedUser) {
            super.onPostExecute(returnedUser);
            progressDialog.dismiss();
            userCallBack.done(returnedUser);
        }
    }
//endregion

//region REGISTER fetchVehicleData
    /**
     * KLASSEN BEGINN
     * Motorrad Liste auslesen
     */
    public class FetchVehicleDataAsyncTask extends AsyncTask<Void, Void, ArrayList<Vehicle>> {
        GetVehicleCallback vehicleCallback;

        public FetchVehicleDataAsyncTask(GetVehicleCallback vehicleCallback) {
            this.vehicleCallback = vehicleCallback;
        }

        @Override
        protected ArrayList<Vehicle> doInBackground(Void... params) {

            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            //dataToSend.add(new BasicNameValuePair("username", user.username));
            //dataToSend.add(new BasicNameValuePair("password", user.password));

            String phpFile = "FetchVehicleData.php";
            ArrayList<Vehicle> returnedVehicleList = new ArrayList<Vehicle>();

            //HTTP REQUEST ausfuehren & RESPONE zurueck bekommen
            HttpResponse httpResponse = createHttpConnection(dataToSend, phpFile);
            HttpEntity entity = httpResponse.getEntity();

            try {
                //RESPONSE in einen result String convertieren weil mehrere lines zurueck kommen (mehrere Vehicles)
                InputStream is = entity.getContent(); // Create an InputStream with the response
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) // Read line by line
                    sb.append(line + "\n");

                String result = sb.toString(); // Result is here
                is.close(); // Close the stream
                Log.v("FetchVehicle", result); //TESTAUSGABE???


                if (result != null) { //Wenn das result nicht leer ist, Convertiere den String in eine VehicleListe
                    Log.v("happened", "2");
                    //Json Objekt in Vehicle liste parsen

                    JSONArray jsonArray = new JSONArray(result);
                    JSONObject[] jsonObjects = new JSONObject[jsonArray.length()]; //array dass JSONObjects behinhaltet

                    //Fuelle das Array jsonObjects mit den einzelnen Objekten des jsonArray's
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObjects[i] = jsonArray.getJSONObject(i);
                    }

                    //Fuer jedes JSONObject im jsonObjects Array erstelle ein neues Vehicle
                    //... und adde es zur vehicleList
                    for (JSONObject item : jsonObjects) {
                        Vehicle vehicleTemp = new Vehicle(
                                item.getInt("vehicleId"),
                                item.getString("brand"),
                                item.getString("type"),
                                item.getInt("price")
                        );
                        returnedVehicleList.add(vehicleTemp);
                    }

                } else {
                    Log.d("DOIB FetchVehicle:", "result string null"); //TESTAUSGABE???
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return returnedVehicleList;
        }


        @Override
        protected void onPostExecute (ArrayList<Vehicle> returnedVehicleList){
            super.onPostExecute(returnedVehicleList);
            progressDialog.dismiss();
            vehicleCallback.done(returnedVehicleList);
        }

    }



//endregion

//region PROFIL Update Kilometer
    public class UpdateKilometerDataAsyncTask extends AsyncTask<Void, Void, String> {
        int kilometer;
        String username;
        GetUpdatedKilometerCallback kilometerCallback;

        public UpdateKilometerDataAsyncTask(String username, int kilometer, GetUpdatedKilometerCallback kilometerCallback) {
            this.username = username;
            this.kilometer = kilometer;
            this.kilometerCallback = kilometerCallback;
        }

        @Override
        protected String doInBackground(Void... params) {
            String phpFile = "UpdateKm.php";

            // Daten fuer Uebergabe vorbereiten
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("username", this.username));
            dataToSend.add(new BasicNameValuePair("kilometer", this.kilometer + ""));

            //HTTP request ausfuehren (liefert einen HttpResponse zurueck der bei Insert & Update nicht benoetigt wird
            createHttpConnection(dataToSend,phpFile);

            return Integer.toString(kilometer);
        }

        @Override
        protected void onPostExecute(String kilometer) {
            super.onPostExecute(kilometer);
            progressDialog.dismiss();
            kilometerCallback.done(kilometer);
        }
    }
//endregion

//region FetchServiceRecords
    public class FetchServiceRecordDataAsyncTask extends AsyncTask<Void, Void, ArrayList<ServiceRecord>> {
        GetServiceRecordCallback serviceRecordCallback;

        public FetchServiceRecordDataAsyncTask(GetServiceRecordCallback serviceRecordCallback) {
            this.serviceRecordCallback = serviceRecordCallback;
        }

        @Override
        protected ArrayList<ServiceRecord> doInBackground(Void... params) {
            ArrayList <NameValuePair> dataToSend = new ArrayList<>();

            String phpFile = "FetchServiceRecords.php";
            ArrayList<ServiceRecord> returnedServiceRecords = new ArrayList<ServiceRecord>();

            //HTTP REQUEST ausfuehren & RESPONE zurueck bekommen
            HttpResponse httpResponse = createHttpConnection(dataToSend,phpFile);
            HttpEntity entity = httpResponse.getEntity();

            try {
                //RESPONSE in einen result String convertieren weil mehrere lines zurueck kommen (mehrere Vehicles)
                InputStream is = null; // Create an InputStream with the response
                is = entity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) // Read line by line
                    sb.append(line + "\n");

                String result = sb.toString(); // Result is here
                is.close(); // Close the stream
                Log.v("FetchServiceRecords: ","result String: " + result); //TESTAUSGABE???

                if (result != null) { //Wenn das result nicht leer ist, Convertiere den String in eine VehicleListe
                    Log.v("DOIB FetchServRec: ", "ServicesRecords erhalten als result string");
                    //Json Objekt in Vehicle liste parsen

                    JSONArray jsonArray = new JSONArray(result);
                    JSONObject[] jsonObjects = new JSONObject[jsonArray.length()]; //array dass JSONObjects behinhaltet

                    //Fuelle das Array jsonObjects mit den einzelnen Objekten des jsonArray's
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObjects[i] = jsonArray.getJSONObject(i);
                    }

                    //Fuer jedes JSONObject im jsonObjects Array erstelle ein neues Vehicle
                    //... und adde es zur vehicleList
                    boolean myBoolean = false;
                    //myBoolean =(item.getInt("made") != 0)
                    for (JSONObject item : jsonObjects) {
                        ServiceRecord serviceRecordTemp = new ServiceRecord(
                                item.getInt("serviceRecordId"),
                                item.getInt("kilometer"),
                                item.getString("action"),
                                myBoolean = (item.getInt("made") != 0), //Konvertiert int zu Bool if 0 --> false, else --> true
                                item.getInt("vehicleId"),
                                item.getInt("expectedPrice"),
                                item.getInt("actualPrice")
                        );
                        returnedServiceRecords.add(serviceRecordTemp);


                        //Log.v("BOOLEAN: ", String.valueOf(myBoolean)); //TESTAUSGABE???
                    }

                } else {
                    Log.d("DOIB FetchServRec:", "result string null"); //TESTAUSGABE???
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return returnedServiceRecords;
        }

        @Override
        protected void onPostExecute(ArrayList<ServiceRecord> returnedServiceRecords) {
            super.onPostExecute(returnedServiceRecords);
            progressDialog.dismiss();
            serviceRecordCallback.done(returnedServiceRecords);
        }
}


//endregion

//region UpdateServiceRecord (MADE)
    private class UpdateServiceRecordAsyncTask extends AsyncTask<Void,Void,ServiceRecord>{
        ServiceRecord serviceRecord;

        public UpdateServiceRecordAsyncTask(ServiceRecord serviceRecord) {
            this.serviceRecord = serviceRecord;
        }

        @Override
        protected ServiceRecord doInBackground(Void... params) {
            String phpFile = "UpdateSR.php";
            int made;

            //Daten fuer Uebergabe formatieren
            //WENN MADE VON serviceRecord = true update ihn in der DB zu 0

            if(serviceRecord.isMade()){
                made = 0;
            }else{
                made = 1;
            }

            //Daten fure Uebergabe vorbereiten
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("serviceRecordId", this.serviceRecord.getServiceRecordId() + ""));
            dataToSend.add(new BasicNameValuePair("made", made + ""));

            //HTTP request ausfuehren (liefert einen HttpResponse zurueck der bei Insert & Update nicht benoetigt wird
            createHttpConnection(dataToSend, phpFile);
            Log.v("SERVER REQUEST: ", "MADE GEUPDATET VON ALT: "+ this.serviceRecord.getServiceRecordId() +" AUF NEU: " + made);
            return null;
        }
    }
//endregion


//region HTTP AUFBAU
    //Setzt HTTP Parameter & Connected --> liefer einen httpResponse zurueck der den result String beinhaltet
    private HttpResponse createHttpConnection(ArrayList<NameValuePair> dataToSend, String phpFile){

        String result="";
        try {
            //authentication
            String authorizationString = "Basic " + Base64.encodeToString(
                    (SERVER_UNAME + ":" + SERVER_PW).getBytes(), Base64.NO_WRAP);


            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + phpFile);
            post.setHeader("Authorization", authorizationString);
            post.setEntity(new UrlEncodedFormEntity(dataToSend));
            HttpResponse httpResponse = client.execute(post);
            return httpResponse;

        } catch (Exception e) {
            e.printStackTrace();
            return null; //im Fehlerfall
        }
    }




//endregion


}

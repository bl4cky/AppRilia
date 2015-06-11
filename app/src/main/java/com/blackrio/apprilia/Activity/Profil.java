package com.blackrio.apprilia.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blackrio.apprilia.Bean.ServiceRecord;
import com.blackrio.apprilia.Callback.GetServiceRecordCallback;
import com.blackrio.apprilia.Callback.GetUpdatedKilometerCallback;
import com.blackrio.apprilia.Callback.GetVehicleCallback;
import com.blackrio.apprilia.LocalStore.ServiceRecordLocalStore;
import com.blackrio.apprilia.LocalStore.UserLocalStore;
import com.blackrio.apprilia.LocalStore.VehicleLocalStore;
import com.blackrio.apprilia.R;
import com.blackrio.apprilia.Server.ServerRequests;
import com.blackrio.apprilia.Bean.User;
import com.blackrio.apprilia.Bean.Vehicle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class Profil extends AppCompatActivity implements View.OnClickListener{
    Context context;

    //LOCALSTORE USER & VEHICLE
    private UserLocalStore userLocalStore;
    private VehicleLocalStore vehicleLocalStore;
    private ServiceRecordLocalStore serviceRecordLocalStore;
    private ServerRequests serverRequests;
    private Vehicle curVehicle;

    //VIEW OBJEKTE
    private TextView tvHeader, tvVehicle, tvCurrentKilometer, tvRegistrationDate, tvCurrentValue;
    private EditText etUpdatedKilometer;
    private Button bLogout, bUpdateKilometer, bToDo, bDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        this.context = this;

        //OBJEKTE aus VIEW mit ACTIVITY bekannt machen
        tvHeader = (TextView) findViewById(R.id.tvHeader);
        tvVehicle = (TextView) findViewById(R.id.tvVehicle);
        tvCurrentKilometer = (TextView) findViewById(R.id.tvCurrentKilometer);
        etUpdatedKilometer = (EditText) findViewById(R.id.etUpdatedKilometer);
        tvRegistrationDate = (TextView) findViewById(R.id.tvRegistrationDate);
        tvCurrentValue = (TextView) findViewById(R.id.tvCurrentValue);
        bLogout = (Button) findViewById(R.id.bLogout);
        bUpdateKilometer = (Button) findViewById(R.id.bUpdateKilometer);
        bToDo = (Button) findViewById(R.id.bToDo);
        bDone = (Button) findViewById(R.id.bDone);

        //BUTTON onClickListener setzen
        bLogout.setOnClickListener(this);
        bUpdateKilometer.setOnClickListener(this);
        bToDo.setOnClickListener(this);
        bDone.setOnClickListener(this);

        //LOCALSTOREs instanzieren
        userLocalStore = new UserLocalStore(this);
        serviceRecordLocalStore = new ServiceRecordLocalStore(this);


        //LOCALSTORE's löschen
        serviceRecordLocalStore.clearServiceRecordData();

        //LOCALSTORE's neu laden aus DB:

        storeServiceRecordsFromDb();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //VehicleListe aus DB holen und in localstore laden
        vehicleLocalStore = new VehicleLocalStore(this);
        vehicleLocalStore.clearVehicleData();
        storeVehiclesFromDB();


        if (authenticate() == true) {
            displayUserDetails();
        }
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){

            //LOGOUT CLICK
            case R.id.bLogout:
                //LOCALSTORE LÖSCHEN (bis auf Motorrad weil das wird in Register gebraucht und nur beim neustart der APP neu geladen)
                userLocalStore.clearUserData();
                userLocalStore.setUserLoggedIn(false);
                serviceRecordLocalStore.clearServiceRecordData();

                Intent loginIntent = new Intent(this, Login.class);
                startActivity(loginIntent);
                break;

            //UPDATE KILOMETER CLICK
            case R.id.bUpdateKilometer:
                //Falls eingabe leer
                if(etUpdatedKilometer.getText().toString().matches("")){
                    showErrorMessage("Please don't try to bother us! Fill in a value!");
                }else {
                    //Wenn eingabe nicht leer
                    int updatedKm = Integer.parseInt(etUpdatedKilometer.getText().toString());
                    String username = userLocalStore.getLoggedInUser().getUsername();
                    int curKilometer = userLocalStore.getLoggedInUser().getKilometer();

                    //Wenn Usereingabe fuer neuen Kilometerstand korrekt
                    if (updatedKm >= curKilometer && updatedKm <= 1000000) {

                        //UPDATE Kilometerstand in DB
                        ServerRequests serverRequests = new ServerRequests(this);
                        serverRequests.updateKilometerDataInBackground(username, updatedKm, new GetUpdatedKilometerCallback() {
                            @Override
                            public void done(String kilometer) {
                                //Speicher den neuen Kilometerstand in Localstore
                                int updatedkm = Integer.parseInt(kilometer);
                                userLocalStore.updateKilometer(updatedkm);
                                Log.v("LOCALSTORE KM NEU: ", Integer.toString(userLocalStore.getLoggedInUser().getKilometer()));//TESTAUSGABE???

                                tvCurrentKilometer.setText("Driven Kilometers: " + updatedkm);
                                showToastMessage("kilometer update successful");
                                etUpdatedKilometer.setText("");
                                etUpdatedKilometer.setHint("update kilometer here");
                                //wenn
                                if(!serviceRecordLocalStore.getUserServiceRecords(context,"todo",updatedkm , vehicleLocalStore.getVehicleIdFromType(context, userLocalStore.getLoggedInUser().getVehicleType())).isEmpty()){
                                    showToastMessage("There are Services in your todo-list!");
                                }
                                //temp int damit calculateValue übergabeparameter funktioniert
                                int temp = new Integer(kilometer);
                                tvCurrentValue.setText("Current Value of Motorcycle: " + calculateValue(temp, curVehicle.getPrice() )+"€");
                                //kleines easter egg
                                if(userLocalStore.getLoggedInUser().getKilometer() >= 300000){
                                    tvCurrentValue.setText("Bring your Motorcycle to the 'Ludolfs' and hope to get a scrap price!" );
                                }
                            }
                        });
                    } else {
                        //Inkorrekte User eingabe fuer neuen Kilometerstand
                        showErrorMessage("Please insert a milage bigger than your current one!");
                    }
                }
                break;

            //CLICK TODO_
            case R.id.bToDo:
                Intent toDoIntent = new Intent(this, ToDo.class);
                toDoIntent.putExtra("filter", "todo");
                startActivity(toDoIntent);
                break;

            //CLICK DONE
            case R.id.bDone:
                Intent doneIntent = new Intent(this, ToDo.class);
                doneIntent.putExtra("filter", "done");
                startActivity(doneIntent);
                break;
        }
    }


//region STARTEN DES APP'S (onCreate)

    //Hol Alle Motorraeder aus der DB und storeVehiclesLocal
    private void storeVehiclesFromDB(){
        ServerRequests serverRequest = new ServerRequests(this);
        serverRequest.fetchVehicleDataAsyncTask(new GetVehicleCallback() {
            @Override
            public void done(ArrayList<Vehicle> returnedVehicleList) {
                //Error Message wenn keine Motorraeder in DB gefunden
                if (returnedVehicleList == null) {
                    showErrorMessage("No Vehicles in db");

                    //Wenn Motorraeder in DB gefunden
                } else {
                    storeVehiclesLocal(returnedVehicleList);
                }
            }
        });
    }

    //METHODE FUER TESTAUSGABE???
    private void storeVehiclesLocal(ArrayList<Vehicle> returnedVehicleList) {
        vehicleLocalStore.storeVehicleData(returnedVehicleList);

        //TESTAUGSABE???
        ArrayList<Vehicle> testList = vehicleLocalStore.getVehicleData(this);
        String testString = testList.get(0).getBrand() + testList.get(0).getType();
        Log.v("LOGINACTIVITY", "...hat gefunkt " + testString);
    }
//endregion



//region Starten der Activity (onStart)

    //UEBERPRUEFEN ob User eingeloggt bei false --> Login Activity
    private boolean authenticate() {
        if (userLocalStore.getLoggedInUser() == null) {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            return false;
        }
        return true;
    }

    //Anzeige beim Starten des Profils (USER spezifische Daten (WENN USER EINGELOGGT)
    private void displayUserDetails() {
        User curUser = userLocalStore.getLoggedInUser();
        curVehicle = getCurVehicle(curUser.getVehicleType());

        tvHeader.setText("Welcome to APPrilia " + curUser.getFirstname() + " " + curUser.getLastname());
        tvVehicle.setText("Motorcycle: "+ curVehicle.getBrand()+ " " + curVehicle.getType());
        tvCurrentKilometer.setText("Driven Kilometers: " + curUser.getKilometer());
        tvRegistrationDate.setText("Motorcycle Registration Date: " + curUser.getRegistrationDate());

        //neupreis ist statisch daher in der class als private int abgespeichert!
        tvCurrentValue.setText("Current Value of Motorcycle: " + calculateValue(userLocalStore.getLoggedInUser().getKilometer(), curVehicle.getPrice() )+"€");
        //kleines easter egg
        if(userLocalStore.getLoggedInUser().getKilometer() >= 300000){
            tvCurrentValue.setText("Bring your Motorcycle to the 'Ludolfs' and hope to get a scrap price!" );
        }
    }

    //Hol das Aktuelle Motorrad aus dem LocalStore
    private Vehicle getCurVehicle(String vehicleType) {
        Vehicle tempVehicle = null;

        for(Vehicle item : vehicleLocalStore.getVehicleData(this)){
            if(item.getType().equals(vehicleType)){
                tempVehicle = item;
            }
        }
        return tempVehicle;
    }


    //SERVICERECORDS aus DB holen und in LocalStore speichern (WENN USER EINGELOGGT)
    private void storeServiceRecordsFromDb() {
        ServerRequests sr = new ServerRequests(this);
        sr.fetchServiceRecordDataAsyncTask(new GetServiceRecordCallback() {
            @Override
            public void done(ArrayList<ServiceRecord> serviceRecordList) {
                storeServiceRecordsLocal(serviceRecordList);

            }
        });
    }
    //METHODE FUER TESTAUSGABE???
    private void storeServiceRecordsLocal(ArrayList<ServiceRecord> serviceRecordList) {
        serviceRecordLocalStore.storeServiceRecordData(serviceRecordList);

        //TESTAUSGABE???
        ArrayList<ServiceRecord> testSRList = serviceRecordLocalStore.getServiceRecordData(this);
        for(ServiceRecord item : testSRList){
            //Log.v("SR LOKAL LIST: ", item.getAction() + " " + item.getKilometer());
        }
    }
//endregion




//region MESSAGES & TOASTS

    //ERROR MESSAGE erwartet String mit error Message
    private void showErrorMessage(String msg) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Profil.this);
        dialogBuilder.setMessage(msg);
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();
    }

    //TOAST MESSAGE erwartet String mit benachrichtigungs Message
    private void showToastMessage(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
//endregion

    //region CALCULATIONS

    private int calculateValue(int km, int originalPrice)
    {
        //region CALC1 -> "verlust anhand km"
        //es wird von einer durchschnittlichen lebensdauer von 100.000 km bei einem motorrad ausgegangen
        int avgLifeCycle = 100000;
        int startPrice = originalPrice;

        //wertverlust = neupreis/100.000*gefahrenen kilometer
        float deterioration = ((float)originalPrice/(float)avgLifeCycle)*(float)km;

        //temp gewichtung der ersten berechnung -> wertverlust anhang km auf basis 100.000km
        double gewichtungTemp = 0.40;

        //neuer wert des motorrad = neupreis - wertverlust
        double deteriorationGewichtet = (double) deterioration * gewichtungTemp;
        int deteriorationGewichtetInt = (int) deteriorationGewichtet;
        int newValueOfMotorcycle = startPrice - deteriorationGewichtetInt;
        //endregion

        //region CALC2 -> "verlust anhand alter"
        //bei einer neuanmeldung verliert ein fahrzeug 25% im ersten jahr
        String regDate = userLocalStore.getLoggedInUser().getRegistrationDate();
        //Jahreszahl abschneiden
        String regDateYear = regDate.substring(0,regDate.indexOf('-'));
        //mach einen int aus dem string
        int registrationYear = new Integer(regDateYear);

        //Aktuelles Jahr der Systemzeit holen
        Date date = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        int actualYear = calendar.get(Calendar.YEAR);

        int ageOfMotorcycle = actualYear - registrationYear;
        float tempFactor;

        switch (ageOfMotorcycle){
            case 0:
                tempFactor = (originalPrice * 25/100);
                newValueOfMotorcycle = newValueOfMotorcycle - (int)tempFactor;
                break;
            case 1:
                tempFactor = (originalPrice * 26/100);
                newValueOfMotorcycle = newValueOfMotorcycle  - (int)tempFactor;;
                break;
            case 2:
                tempFactor = (originalPrice * 28/100);
                newValueOfMotorcycle = newValueOfMotorcycle  - (int)tempFactor;;
                break;
            case 3:
                tempFactor = (originalPrice * 32/100);
                newValueOfMotorcycle = newValueOfMotorcycle - (int)tempFactor;
                break;
            case 4:
                tempFactor = (originalPrice * 36/100);
                newValueOfMotorcycle = newValueOfMotorcycle  - (int)tempFactor;;
                break;
            case 5:
                tempFactor = (originalPrice * 40/100);
                newValueOfMotorcycle = newValueOfMotorcycle - (int)tempFactor;
                break;
            case 6:
                tempFactor = (originalPrice * 42/100);
                newValueOfMotorcycle = newValueOfMotorcycle - (int)tempFactor;
                break;
            case 7:
                tempFactor = (originalPrice * 43/100);
                newValueOfMotorcycle = newValueOfMotorcycle - (int)tempFactor;
                break;
            case 8:
                tempFactor = (originalPrice * 46/100);
                newValueOfMotorcycle = newValueOfMotorcycle - (int)tempFactor;
                break;
            case 9:
                tempFactor = (originalPrice * 49/100);
                newValueOfMotorcycle = newValueOfMotorcycle - (int)tempFactor;
                break;
            case 10:
                tempFactor = (originalPrice * 52/100);
                newValueOfMotorcycle = newValueOfMotorcycle - (int)tempFactor;
                break;
            default: //SCHROTTWERT alle über 10 Jahre hat einen Schrottwert von 1000€
                tempFactor = (originalPrice * 55/100);
                newValueOfMotorcycle = newValueOfMotorcycle - (int)tempFactor;
                break;
        }
        //endregion "

        //region CALC3 -> "wertsteigerung wenn alle services gemacht"




        //getUserServiceRecords(Context context, String filter, int curkm, int vehicleId)
        ArrayList<ServiceRecord> tempList = serviceRecordLocalStore.getUserServiceRecords(this, "todo" , km, curVehicle.getId());
        int temp = tempList.size();

        //wenn alle services "brav" gemacht sind ist das fahrzeug 5% mehr wert
        if(temp == 0){
            newValueOfMotorcycle = (int) (newValueOfMotorcycle * 1.05);
        }


        //endregion

        //endueberpruefung falls wert unter 1000 dann setzte ihn auf Schrottwert bzw Teilespenderwert von 1000€
        if (newValueOfMotorcycle <= 1000){
            newValueOfMotorcycle = 1000;
        }
        return newValueOfMotorcycle;
    }


//endregion



}


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


public class Profil extends AppCompatActivity implements View.OnClickListener{
    //LOCALSTORE USER & VEHICLE
    private UserLocalStore userLocalStore;
    private VehicleLocalStore vehicleLocalStore;
    private ServiceRecordLocalStore serviceRecordLocalStore;
    private ServerRequests serverRequests;

    //VIEW OBJEKTE
    private TextView tvHeader, tvVehicle, tvCurrentKilometer, tvRegistrationDate;
    private EditText etUpdatedKilometer;
    private Button bLogout, bUpdateKilometer, bToDo, bDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        //OBJEKTE aus VIEW mit ACTIVITY bekannt machen
        tvHeader = (TextView) findViewById(R.id.tvHeader);
        tvVehicle = (TextView) findViewById(R.id.tvVehicle);
        tvCurrentKilometer = (TextView) findViewById(R.id.tvCurrentKilometer);
        etUpdatedKilometer = (EditText) findViewById(R.id.etUpdatedKilometer);
        tvRegistrationDate = (TextView) findViewById(R.id.tvRegistrationDate);
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
        vehicleLocalStore = new VehicleLocalStore(this);

        //LOCALSTORE's löschen
        vehicleLocalStore.clearVehicleData();
        serviceRecordLocalStore.clearServiceRecordData();

        //LOCALSTORE's neu laden aus DB:
        storeVehiclesFromDB();
        storeServiceRecordsFromDb();
    }

    @Override
    protected void onStart() {
        super.onStart();

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
                int updatedKm = Integer.parseInt(etUpdatedKilometer.getText().toString());
                String username = userLocalStore.getLoggedInUser().getUsername();
                int curKilometer = userLocalStore.getLoggedInUser().getKilometer();

                //Wenn Usereingabe fuer neuen Kilometerstand korrekt
                if(updatedKm >= curKilometer && updatedKm <=1000000) {

                    //UPDATE Kilometerstand in DB
                    ServerRequests serverRequests = new ServerRequests(this);
                    serverRequests.updateKilometerDataInBackground(username, updatedKm, new GetUpdatedKilometerCallback() {
                        @Override
                        public void done(String kilometer) {
                            //Speicher den neuen Kilometerstand in Localstore
                            userLocalStore.updateKilometer(Integer.parseInt(kilometer));
                            Log.v("LOCALSTORE KM NEU: ", Integer.toString(userLocalStore.getLoggedInUser().getKilometer()));//TESTAUSGABE???

                            tvCurrentKilometer.setText("Driven Kilometers: " + kilometer);
                            showToastMessage("kilometer update successful");
                            etUpdatedKilometer.setText("");
                            etUpdatedKilometer.setHint("update kilometer here");
                        }
                    });
                }else{
                    //Inkorrekte User eingabe fuer neuen Kilometerstand
                    showErrorMessage("Please insert a milage bigger than your current one!");
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
        Log.v("getVehicles", "methoden beginn"); //TESTAUSGABE
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
        Vehicle curVehicle = getCurVehicle(curUser.getVehicleType());

        tvHeader.setText("Welcome to APPrilia " + curUser.getFirstname() + " " + curUser.getLastname());
        tvVehicle.setText("Motorcycle: "+ curVehicle.getBrand()+ " " + curVehicle.getType());
        tvCurrentKilometer.setText("Driven Kilometers: " + curUser.getKilometer());
        tvRegistrationDate.setText("Motorcycle Registration Date: " + curUser.getRegistrationDate());
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
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }
//endregion

}


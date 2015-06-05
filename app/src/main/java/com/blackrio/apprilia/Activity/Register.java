package com.blackrio.apprilia.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.blackrio.apprilia.Callback.GetUserCallback;
import com.blackrio.apprilia.LocalStore.VehicleLocalStore;
import com.blackrio.apprilia.R;
import com.blackrio.apprilia.Server.ServerRequests;
import com.blackrio.apprilia.Bean.User;
import com.blackrio.apprilia.Bean.Vehicle;

import java.util.ArrayList;
import java.util.List;


public class Register extends ActionBarActivity implements View.OnClickListener{

    private EditText etFirstname, etLastname, etUsername, etPassword, etKilometer, etRegistrationDate;
    private Spinner spVehicleList;
    private Button bRegister;
    private ArrayAdapter<String> vehicleListAdapter;
    private List<String> listArray;
    private String selectedVehicle;
    //ListView aList;


    private VehicleLocalStore vehicleLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



        //View und Java Objekte bekannt machen
        etFirstname = (EditText) findViewById(R.id.etFirstname);
        etLastname = (EditText) findViewById(R.id.etLastname);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        spVehicleList = (Spinner) findViewById(R.id.spVehicleList);
        etKilometer = (EditText) findViewById(R.id.etKilometer);
        etRegistrationDate = (EditText) findViewById(R.id.etRegistrationDate);
        bRegister = (Button) findViewById(R.id.bRegister);

        bRegister.setOnClickListener(this);

        //Vehicle localstore loeschen und neu laden
        vehicleLocalStore = new VehicleLocalStore(this);
        ArrayList<Vehicle> localVehicleList = vehicleLocalStore.getVehicleData(this);

//region SPINNER FUER MOTORRAEDER BEFUELLEN
        listArray =  new ArrayList<>();
        // BEFUELLEN DES SPINNERS
        for(Vehicle item : localVehicleList){
            listArray.add(item.getBrand() + " " + item.getType());
        }
        //vehicleListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listArray);
        vehicleListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, listArray);
        spVehicleList.setAdapter(vehicleListAdapter);
        spVehicleList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int spinnerPosition = spVehicleList.getSelectedItemPosition();

                Log.v("Spinner vehicle:", listArray.get(spinnerPosition));

                Log.v("Spinner vehicle type ", listArray.get(spinnerPosition).split(" ")[1]);
                //selectedVehicle = listArray.get(spinnerPosition).split(" ")[1];
                selectedVehicle = listArray.get(spinnerPosition).split(" ", 2)[1];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//endregion
        //aList = (ListView) findViewById(R.id.lvVehicleList);
        //aList.setAdapter(vehicleListAdapter);
        //aList.setClickable(true);




        //test um ueberpruefen zu koennen ob vehicleLocalStore Werte enthaelt
        String testString = localVehicleList.get(0).getBrand() + localVehicleList.get(0).getType();
        Log.v("REGISTERACTIVITY", "...hat gefunkt " + testString);



    }

    //region BUTTON CLICKS
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bRegister:
                String firstname = etFirstname.getText().toString();
                String lastname = etLastname.getText().toString();
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String vehicleType = selectedVehicle;
                Log.v("REgister:",selectedVehicle);
                int kilometer = Integer.parseInt(etKilometer.getText().toString());
                String registrationDate = etRegistrationDate.getText().toString();


                //int age = Integer.parseInt(etAge.getText().toString());

                User user = new User(username, password, firstname, lastname, vehicleType, kilometer, registrationDate);
                registerUser(user);

                break;
        }
    }
    //endregion

    //region USER IN DB SPEICHERN
    private void registerUser(User user) {
        ServerRequests serverRequest = new ServerRequests(this);
        serverRequest.storeUserDataInBackground(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser) {
                Intent loginIntent = new Intent(Register.this, Login.class);
                startActivity(loginIntent);
            }
        });
    }
    //endregion






}

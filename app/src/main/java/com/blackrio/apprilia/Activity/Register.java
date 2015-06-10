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
import com.blackrio.apprilia.Helper.DatePickerFragment;
import com.blackrio.apprilia.LocalStore.VehicleLocalStore;
import com.blackrio.apprilia.R;
import com.blackrio.apprilia.Server.ServerRequests;
import com.blackrio.apprilia.Bean.User;
import com.blackrio.apprilia.Bean.Vehicle;

import java.util.ArrayList;


public class Register extends ActionBarActivity implements View.OnClickListener{

    private EditText etFirstname, etLastname, etUsername, etPassword, etKilometer, etRegistrationDate;
    private Spinner spVehicleList;
    private ArrayAdapter<Vehicle> listAdpapter;
    private Button bRegister;
    private String selectedVehicle;
    private ArrayList<Vehicle> vehicleList;


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

        //BUTTON onClickListener setzen
        bRegister.setOnClickListener(this);
        etRegistrationDate.setOnClickListener(this);

        //VehicleListe aus Localstore holen
        vehicleLocalStore = new VehicleLocalStore(this);
        vehicleList = vehicleLocalStore.getVehicleData(this);


//region SPINNER FUER MOTORRAEDER BEFUELLEN
        listAdpapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item, vehicleList);
        spVehicleList.setAdapter(listAdpapter);

        spVehicleList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int spinnerPosition = spVehicleList.getSelectedItemPosition();
                selectedVehicle = vehicleList.get(spinnerPosition).getType();

                Log.v("Spinner vehicle: ", selectedVehicle);

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
        String testString = vehicleList.get(0).getBrand() + vehicleList.get(0).getType();
        Log.v("REGISTERACTIVITY", "...hat gefunkt " + testString);
    }

    //region BUTTON CLICKS
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //REGISTER CLICK
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

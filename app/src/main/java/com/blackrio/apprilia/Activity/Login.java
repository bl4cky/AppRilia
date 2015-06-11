package com.blackrio.apprilia.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blackrio.apprilia.Callback.GetUserCallback;
import com.blackrio.apprilia.Callback.GetVehicleCallback;
import com.blackrio.apprilia.LocalStore.UserLocalStore;
import com.blackrio.apprilia.LocalStore.VehicleLocalStore;
import com.blackrio.apprilia.R;
import com.blackrio.apprilia.Server.ServerRequests;
import com.blackrio.apprilia.Bean.User;
import com.blackrio.apprilia.Bean.Vehicle;

import java.util.ArrayList;


public class Login extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "";
    private Button bLogin;
    private TextView registerLink, tvServiceInfo;
    private EditText etUsername, etPassword;

    private UserLocalStore userLocalStore;
    private boolean _doubleBackToExitPressedOnce;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login");

        bLogin = (Button) findViewById(R.id.bLogin);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        registerLink = (TextView) findViewById(R.id.tvRegisterLink);
        tvServiceInfo = (TextView) findViewById(R.id.tvServiceInfo);



        bLogin.setOnClickListener(this);
        registerLink.setOnClickListener(this);
        tvServiceInfo.setOnClickListener(this);

        userLocalStore = new UserLocalStore(this);

        //Resette Lokale Vehicle Liste und hol Vehicles neu aus DB
//        vehicleLocalStore.clearVehicleData();
// storeAllVehicles();

        //Wenn noch keine Motorraeder Local abgespeichert sind hol sie aus DB



    }

    //region BUTTON CLICKS
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            //Login click
            case R.id.bLogin:
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                User user = new User(username, password);

                authenticate(user);
                break;

            //Register click
            case R.id.tvRegisterLink:
                Intent registerIntent = new Intent(Login.this, Register.class);
                startActivity(registerIntent);
                break;

            //Service Info
            case R.id.tvServiceInfo:
                Intent serviceInfoIntent = new Intent(this, ServiceList.class);
                startActivity(serviceInfoIntent);
                break;
        }
    }
    //endregion

    //Hol Userdaten aus der DB und logUserIn
    private void authenticate(User user) {
        ServerRequests serverRequest = new ServerRequests(this);
        serverRequest.fetchUserDataAsyncTask(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser) {
                if (returnedUser == null) { //Wenn User nicht existiert
                    showErrorMessage("Incorrect user details");

                } else {                    //Wenn User existiert
                    logUserIn(returnedUser);
                }
            }
        });
    }

    //Speicher den User Local
    private void logUserIn(User returnedUser) {
        userLocalStore.storeUserData(returnedUser);
        userLocalStore.setUserLoggedIn(true);
        startActivity(new Intent(this, Profil.class));
    }


    //Error Message falls User nicht existiert
    private void showErrorMessage(String msg) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Login.this);
        dialogBuilder.setMessage(msg);
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();
    }

    /* Fuer das schließen der APP mit doppel zurück click --> funkt allerdings nicht da (ich denke) von der Login auf die Profil gesprungen wird,
        da diese ja die start activity ist
    @Override
    public void onBackPressed(){
        Log.i(TAG, "onBackPressed--");
        if (_doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this._doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to quit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                _doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
*/

}

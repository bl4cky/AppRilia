package com.blackrio.apprilia.LocalStore;

/**
 * Created by Stefan on 31.05.2015.
 */
import android.content.Context;
import android.content.SharedPreferences;

import com.blackrio.apprilia.Bean.User;

public class UserLocalStore {

    public static final String SP_NAME = "userDetails";

    SharedPreferences userLocalDatabase;

    public UserLocalStore(Context context) {
        userLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
    }

    public void storeUserData(User user) {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.putString("username", user.getUsername());
        userLocalDatabaseEditor.putString("password", user.getPassword());
        userLocalDatabaseEditor.putString("firstname", user.getFirstname());
        userLocalDatabaseEditor.putString("lastname", user.getLastname());
        userLocalDatabaseEditor.putString("vehicleType", user.getVehicleType());
        userLocalDatabaseEditor.putInt("kilometer", user.getKilometer());
        userLocalDatabaseEditor.putString("registrationDate", user.getRegistrationDate());
        userLocalDatabaseEditor.commit();
    }

    public void setUserLoggedIn(boolean loggedIn) {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.putBoolean("loggedIn", loggedIn);
        userLocalDatabaseEditor.commit();
    }

    public void clearUserData() {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.clear();
        userLocalDatabaseEditor.commit();
    }

    public User getLoggedInUser() {
        if (userLocalDatabase.getBoolean("loggedIn", false) == false) {
            return null;
        }
        String username = userLocalDatabase.getString("username", "");
        String password = userLocalDatabase.getString("password", "");
        String firstname = userLocalDatabase.getString("firstname", "");
        String lastname = userLocalDatabase.getString("lastname", "");
        String vehicleType = userLocalDatabase.getString("vehicleType", "");
        int kilometer = userLocalDatabase.getInt("kilometer", -1);
        String registrationDate = userLocalDatabase.getString("registrationDate", "");

        User user = new User(username, password, firstname, lastname, vehicleType, kilometer, registrationDate);
        return user;
    }

    public void updateKilometer(int kilometer){
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.putInt("kilometer",kilometer);
        userLocalDatabaseEditor.commit();
    }
}

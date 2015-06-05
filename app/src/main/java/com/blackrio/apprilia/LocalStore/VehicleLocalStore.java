package com.blackrio.apprilia.LocalStore;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.blackrio.apprilia.Bean.Vehicle;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Stefan on 01.06.2015.
 */
public class VehicleLocalStore {

    //name des LocalStores
    public static final String SP_Name ="allVehicles";
    SharedPreferences vehicleLocalDatabase;


    public VehicleLocalStore(Context context) {
        this.vehicleLocalDatabase = context.getSharedPreferences(SP_Name,0);
    }


    public void storeVehicleData(ArrayList<Vehicle> vehicleList){
        SharedPreferences.Editor vehicleLocalDatabaseEditor = vehicleLocalDatabase.edit();
        Gson gson = new Gson();
        String jsonVehicleList =gson.toJson(vehicleList);
        vehicleLocalDatabaseEditor.putString("VEHICLELIST",jsonVehicleList);
        vehicleLocalDatabaseEditor.commit();
        }

    public ArrayList<Vehicle> getVehicleData(Context context){
        List<Vehicle> vehicleList;
        vehicleLocalDatabase = context.getSharedPreferences(SP_Name,0);

        //wenn eine Liste von Vehicles in der SharedPreference vorhanden ist
        if(vehicleLocalDatabase.contains("VEHICLELIST")){
            String jsonVehicleList = vehicleLocalDatabase.getString("VEHICLELIST",null);
            Gson gson = new Gson();
            Vehicle[] items = gson.fromJson(jsonVehicleList, Vehicle[].class);
            vehicleList = Arrays.asList(items);
            vehicleList = new ArrayList<>(vehicleList);
            Log.v("VEHICLELIST", " ist nicht null"); //TESTAUSGABE???
        }else{
            Log.v("VEHICLELIST", " ist null"); //TESTAUSGABE???
            return null;
        }

    return (ArrayList) vehicleList;
    }

    public void clearVehicleData(){
        vehicleLocalDatabase.edit().remove("VEHICLELIST");
        Log.v("VehicleLocalStore", "vehicle list removed"); //TESTAUSGABE???
    }



}











/*
    //region Singleton
    private static VehicleLocalStore _instance;

    public static VehicleLocalStore getInstance(){
        if (_instance == null){
            _instance = new VehicleLocalStore();
        }

        return _instance;
    }

    private VehicleLocalStore(){
        this.getStoredVehicleList();
    }

    //endregion

    private ArrayList<Vehicle> storedVehicleList = new ArrayList<Vehicle>();


    //region GETTER / SETTER
    public void setStoredVehicleList(ArrayList<Vehicle> storedVehicleList) {
        this.storedVehicleList = storedVehicleList;
    }

    public ArrayList<Vehicle> getStoredVehicleList() {
        return storedVehicleList;
    }
    //endregion


*/

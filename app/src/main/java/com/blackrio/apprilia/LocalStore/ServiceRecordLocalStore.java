package com.blackrio.apprilia.LocalStore;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.blackrio.apprilia.Bean.ServiceRecord;
import com.blackrio.apprilia.Bean.Vehicle;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Stefan on 04.06.2015.
 */
public class ServiceRecordLocalStore {

    //name des LocalStores
    public static final String SP_Name ="allServiceRequests";
    SharedPreferences serviceRecordLocalDatabase;


    public ServiceRecordLocalStore(Context context) {
        this.serviceRecordLocalDatabase = context.getSharedPreferences(SP_Name,0);
    }

    //Lokale ServiceRecord liste erstellen
    public void storeServiceRecordData(ArrayList<ServiceRecord> serviceRecordList){
        SharedPreferences.Editor vehicleLocalDatabaseEditor = serviceRecordLocalDatabase.edit();
        Gson gson = new Gson();
        String jsonServiceRecordList =gson.toJson(serviceRecordList);
        vehicleLocalDatabaseEditor.putString("SERVICERECORDLIST",jsonServiceRecordList);
        vehicleLocalDatabaseEditor.commit();
    }

    //Lokale ServiceRecord liste auslesen
    public ArrayList<ServiceRecord> getServiceRecordData(Context context){
        List<ServiceRecord> serviceRecordList;
        serviceRecordLocalDatabase = context.getSharedPreferences(SP_Name,0);

        //wenn eine Liste von ServiceRecords in der SharedPreference vorhanden ist
        if(serviceRecordLocalDatabase.contains("SERVICERECORDLIST")){
            String jsonServiceRecordList = serviceRecordLocalDatabase.getString("SERVICERECORDLIST",null);
            Gson gson = new Gson();
            ServiceRecord[] items = gson.fromJson(jsonServiceRecordList, ServiceRecord[].class);
            serviceRecordList = Arrays.asList(items);
            serviceRecordList = new ArrayList<>(serviceRecordList);
            Log.v("LocalStore SR", " ist nicht null"); //TESTAUSGABE???
        }else{
            Log.v("LocalStore SR", " ist null"); //TESTAUSGABE???
            return null;
        }

        return (ArrayList) serviceRecordList;
    }

    //Lokale ServiceRecord liste löschen
    public void clearServiceRecordData(){
        serviceRecordLocalDatabase.edit().remove("SERVICERECORDLIST");
        Log.v("LocalStore SR", "vehicle list removed"); //TESTAUSGABE???
    }


}


package com.blackrio.apprilia.LocalStore;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.blackrio.apprilia.Bean.ServiceRecord;
import com.blackrio.apprilia.Bean.Vehicle;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Stefan on 04.06.2015.
 */
public class ServiceRecordLocalStore {

    //name des LocalStores
    public static final String SP_Name = "allServiceRequests";
    SharedPreferences serviceRecordLocalDatabase;


    public ServiceRecordLocalStore(Context context) {
        this.serviceRecordLocalDatabase = context.getSharedPreferences(SP_Name, 0);
    }

    //Lokale ServiceRecord liste erstellen
    public void storeServiceRecordData(ArrayList<ServiceRecord> serviceRecordList) {
        SharedPreferences.Editor vehicleLocalDatabaseEditor = serviceRecordLocalDatabase.edit();
        Gson gson = new Gson();
        String jsonServiceRecordList = gson.toJson(serviceRecordList);
        vehicleLocalDatabaseEditor.putString("SERVICERECORDLIST", jsonServiceRecordList);
        vehicleLocalDatabaseEditor.commit();
    }

    //Lokale ServiceRecord liste auslesen
    public ArrayList<ServiceRecord> getServiceRecordData(Context context) {
        List<ServiceRecord> serviceRecordList;
        serviceRecordLocalDatabase = context.getSharedPreferences(SP_Name, 0);

        //wenn eine Liste von ServiceRecords in der SharedPreference vorhanden ist
        if (serviceRecordLocalDatabase.contains("SERVICERECORDLIST")) {

            //ServiceRecords in ArrayListe von LocalStore
            String jsonServiceRecordList = serviceRecordLocalDatabase.getString("SERVICERECORDLIST", null);
            Gson gson = new Gson();
            ServiceRecord[] items = gson.fromJson(jsonServiceRecordList, ServiceRecord[].class);
            serviceRecordList = Arrays.asList(items);
            serviceRecordList = new ArrayList<>(serviceRecordList);
            //Log.v("LocalStore SR", " ist nicht null"); //TESTAUSGABE???

        } else {
            Log.v("LocalStore SR", " ist null"); //TESTAUSGABE???
            return null;
        }

        return (ArrayList) serviceRecordList;
    }

    //Lokale ServiceRecord liste löschen
    public void clearServiceRecordData() {
        serviceRecordLocalDatabase.edit().remove("SERVICERECORDLIST");
        Log.v("LocalStore SR", "vehicle list removed"); //TESTAUSGABE???
    }

    //ServiceRecords auslesen die zu dem User ghoeren --> filter unterscheidet ob todo_ oder done
    public ArrayList<ServiceRecord> getUserServiceRecords(Context context, String filter, int curkm, int vehicleId) {

        //GESAMTE SERVICE LISTE
        ArrayList<ServiceRecord> userSRList = getServiceRecordData(context);
        if (userSRList == null) {
            return null;
        }
        Log.v("FILTER: ", filter);

        switch (filter) {

            //ZU ERLEDIGENDE SERVICES
            case ("todo"):
                for(Iterator<ServiceRecord> it = userSRList.iterator(); it.hasNext();){
                    ServiceRecord sr = it.next();
                    //REMOVE Service welches: zu dem Motorrad gehoeren & kilometer < als gefahrene & noch nicht erledigt sind
                    if (sr.getVehicleId() != vehicleId || sr.getKilometer() > curkm || sr.isMade()) {
                        //Log.v("SR to remove TODO: ", sr.toString());
                        it.remove();
                    }
                }
                break;

            //ERLEDIGTE SERVICES
            case ("done"):
                for(Iterator<ServiceRecord> it = userSRList.iterator(); it.hasNext();){
                    ServiceRecord sr = it.next();
                    //REMOVE Service welches: zu dem Motorrad gehoeren & kilometer < als gefahrene & noch nicht erledigt sind
                    if (sr.getVehicleId() != vehicleId || sr.getKilometer() > curkm || sr.isMade() == false) {
                        //Log.v("SR to remove DONE: ", sr.toString());
                        it.remove();
                    }
                }
                    break;
        }
        return (ArrayList) userSRList;
    }

    public void updateUserServiceRecord(ServiceRecord serviceRecord, Context context){
        ArrayList<ServiceRecord> userSRList = getServiceRecordData(context);
        serviceRecord.toString();
        for(Iterator<ServiceRecord> it = userSRList.iterator(); it.hasNext();){
            ServiceRecord sr = it.next();
            //REMOVE Service welches: zu dem Motorrad gehoeren & kilometer < als gefahrene & noch nicht erledigt sind
            if (sr.getServiceRecordId() == serviceRecord.getServiceRecordId()) {
                sr.setMade(!serviceRecord.isMade());
                Log.v("LOCAL STORE serviceRec ", "MADE GEUPDATET VON ALT: "+ serviceRecord.getServiceRecordId() +" AUF NEU: " + sr.isMade());

            }
        }

        storeServiceRecordData(userSRList);
    }

}


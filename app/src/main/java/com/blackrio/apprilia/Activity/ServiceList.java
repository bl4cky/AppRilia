package com.blackrio.apprilia.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.blackrio.apprilia.Bean.ServiceRecord;
import com.blackrio.apprilia.Bean.Vehicle;
import com.blackrio.apprilia.LocalStore.ServiceRecordLocalStore;
import com.blackrio.apprilia.LocalStore.VehicleLocalStore;
import com.blackrio.apprilia.R;

import java.util.ArrayList;

public class ServiceList extends AppCompatActivity implements View.OnClickListener{
    Context context;

    private VehicleLocalStore vehicleLocalStore;
    private ServiceRecordLocalStore serviceRecordLocalStore;

    ListView lvServiceList;
    Spinner spHeader;
    Button bFooter;
    private ArrayAdapter<Vehicle> spVehicleAdpapter;
    private ArrayAdapter<ServiceRecord> lvServiceAdapter;
    private Vehicle selectedVehicle;
    private ArrayList<Vehicle> vehicleList;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sercive_list);
        context = this;

        //View und Java Objekte bekannt machen
        lvServiceList = (ListView) findViewById(R.id.lvServiceList);
        spHeader = (Spinner) findViewById(R.id.spHeader);
        bFooter = (Button) findViewById(R.id.bFooter);

        //Localstore instanzieren
        vehicleLocalStore = new VehicleLocalStore(this);
        serviceRecordLocalStore = new ServiceRecordLocalStore(this);

        //VehicleListe aus Localstore holen
        vehicleList = vehicleLocalStore.getVehicleData(this);


        spVehicleAdpapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item, vehicleList);
        spHeader.setAdapter(spVehicleAdpapter);

        bFooter.setOnClickListener(this);
        spHeader.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedVehicle = (Vehicle) parent.getItemAtPosition(position);
                //Services zu diesem Vehicle in listview geben
                ArrayList<ServiceRecord> vehicleServiceRecords = serviceRecordLocalStore.getVehicleServiceServiceRecords(selectedVehicle, context);
                lvServiceAdapter = new ArrayAdapter<ServiceRecord>(context, android.R.layout.simple_list_item_1, vehicleServiceRecords);
                lvServiceList.setAdapter(lvServiceAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            //BACK CLICK
            case R.id.bFooter:
                Intent loginIntent = new Intent(this, Login.class);
                startActivity(loginIntent);
                break;

        }
    }

}

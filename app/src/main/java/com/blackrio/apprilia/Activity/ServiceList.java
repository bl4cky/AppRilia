package com.blackrio.apprilia.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    TextView tvFooter;
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
        tvFooter = (TextView) findViewById(R.id.tvFooter);

        //Localstore instanzieren
        vehicleLocalStore = new VehicleLocalStore(this);
        serviceRecordLocalStore = new ServiceRecordLocalStore(this);

        //VehicleListe aus Localstore holen
        vehicleList = vehicleLocalStore.getVehicleData(this);


        spVehicleAdpapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item, vehicleList);
        spHeader.setAdapter(spVehicleAdpapter);

        tvFooter.setOnClickListener(this);
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
            case R.id.tvFooter:
                Intent loginIntent = new Intent(this, Login.class);
                startActivity(loginIntent);
                break;

        }
    }











    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sercive_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

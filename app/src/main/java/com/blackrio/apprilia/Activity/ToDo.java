package com.blackrio.apprilia.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.blackrio.apprilia.Bean.ServiceRecord;
import com.blackrio.apprilia.LocalStore.ServiceRecordLocalStore;
import com.blackrio.apprilia.LocalStore.UserLocalStore;
import com.blackrio.apprilia.LocalStore.VehicleLocalStore;
import com.blackrio.apprilia.R;
import com.blackrio.apprilia.Server.ServerRequests;

import java.util.ArrayList;

public class ToDo extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener{

    //View und Java Objekte bekannt machen
    TextView tvHeader;
    Button bFooter;
    ListView lvUserServiceRecords; // meaning: lvToDoServiceRecords
    ArrayAdapter<ServiceRecord> serviceRecordAdapter;
    ArrayList<ServiceRecord> userServiceRecordList;
    int curKilometer;
    int userVehicleId;
    String filter;
    ServiceRecord selectedSR;
    Context context;

    //LOCALSTORE
    ServiceRecordLocalStore serviceRecordLocalStore;
    UserLocalStore userLocalStore;
    VehicleLocalStore vehicleLocalStore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);
        context= this;

        //Unterscheiden zwischen todo_ und done
        this.filter = getIntent().getCharSequenceExtra("filter").toString();
        Log.v("FILTER:???? ", filter);

        //OBJEKTE aus VIEW mit ACTIVITY bekannt machen
        tvHeader = (TextView) findViewById(R.id.tvHeader);
        bFooter = (Button) findViewById(R.id.bFooter);
        lvUserServiceRecords = (ListView) findViewById(R.id.lvUserServiceRecords);

        bFooter.setOnClickListener(this);

        //LOCALSTORE
        serviceRecordLocalStore = new ServiceRecordLocalStore(this);
        userLocalStore = new UserLocalStore(this);
        vehicleLocalStore = new VehicleLocalStore(this);

        //START
        if(filter.equals("todo")) {
            setTitle("My To-do list");
        } else {
            setTitle("My service history");
        }
        curKilometer = userLocalStore.getLoggedInUser().getKilometer();
        userVehicleId = vehicleLocalStore.getVehicleIdFromType(this, userLocalStore.getLoggedInUser().getVehicleType());


        userServiceRecordList = serviceRecordLocalStore.getUserServiceRecords(this, filter , curKilometer, userVehicleId);


        serviceRecordAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userServiceRecordList);
        lvUserServiceRecords.setAdapter(serviceRecordAdapter);
        lvUserServiceRecords.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectedSR = (ServiceRecord) parent.getItemAtPosition(position);
        Log.v("ITEM: ", selectedSR.toString());

        //BESTATIGUNGS DIALOG (bei YES --> UPDATE 'made' DB)
        confirmServiceRecord();
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){

            //FOOTER CLICK (BACK)
            case R.id.bFooter:
                Intent profileIntent = new Intent(this, Profil.class);
                startActivity(profileIntent);
        }
    }


    private void confirmServiceRecord() {

        String msg="";
        if(filter.equals("todo")){
            msg = "Mark this Service as DONE and Store it in History ?";
        }else{
            msg= "Do you want to reset this Service ?";
        }
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ToDo.this);
        dialogBuilder.setMessage(msg);
        dialogBuilder.setCancelable(true);
        dialogBuilder.setNegativeButton("CANCEL",null);
        dialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // UPDATE MADE IN DATENBANK!
                ServerRequests serverRequests = new ServerRequests(context);
                serverRequests.updateMadeDataInBackground(selectedSR);
                // UPDATE MADE IN LOCALSTORE
                serviceRecordLocalStore.updateUserServiceRecord(selectedSR, context);


                //ACTIVITY NEU STARTEN
                Intent intent = getIntent();
                finish();
                startActivity(intent);

            }
        });
        dialogBuilder.show();
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_to_do, menu);
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
            userLocalStore.clearUserData();
            userLocalStore.setUserLoggedIn(false);
            serviceRecordLocalStore.clearServiceRecordData();

            Intent loginIntent = new Intent(this, Login.class);
            startActivity(loginIntent);
        }

        return super.onOptionsItemSelected(item);
    }



}

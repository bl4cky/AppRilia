package com.blackrio.apprilia.Activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.blackrio.apprilia.R;

public class ValueAnalyse extends ActionBarActivity {

    private TextView tvHeader, tvResidualValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_value_analyse);

        tvHeader = (TextView) findViewById(R.id.tvHeader);
        tvResidualValue = (TextView) findViewById(R.id.tvResidualValue);


    }


}

package com.blackrio.apprilia.Callback;

import com.blackrio.apprilia.Bean.Vehicle;

import java.util.ArrayList;

/**
 * Created by Stefan on 31.05.2015.
 */
public interface GetVehicleCallback {

    public abstract void done(ArrayList<Vehicle> returnedVehicleList);
}
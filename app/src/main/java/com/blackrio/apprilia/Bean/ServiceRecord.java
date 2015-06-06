package com.blackrio.apprilia.Bean;

import com.blackrio.apprilia.LocalStore.UserLocalStore;

/**
 * Created by Stefan on 04.06.2015.
 */
public class ServiceRecord {

    private int serviceRecordId;
    private int kilometer;
    private String action;
    private boolean made;
    private int vehicleId;
    private int expectedPrice;
    private int actualPrice;

//region GETTER & SETTTER
    public int getServiceRecordId() {
        return serviceRecordId;
    }

    public void setServiceRecordId(int serviceRecordId) {
        this.serviceRecordId = serviceRecordId;
    }

    public int getKilometer() {
        return kilometer;
    }

    public void setKilometer(int kilometer) {
        this.kilometer = kilometer;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean isMade() {
        return made;
    }

    public void setMade(boolean made) {
        this.made = made;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public int getExpectedPrice() {
        return expectedPrice;
    }

    public void setExpectedPrice(int expectedPrice) {
        this.expectedPrice = expectedPrice;
    }

    public int getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(int actualPrice) {
        this.actualPrice = actualPrice;
    }
//endregion


    public ServiceRecord(int serviceRecordId, int kilometer, String action, boolean made, int vehicleId, int expectedPrice, int actualPrice) {
        this.serviceRecordId = serviceRecordId;
        this.kilometer = kilometer;
        this.action = action;
        this.made = made;
        this.vehicleId = vehicleId;
        this.expectedPrice = expectedPrice;
        this.actualPrice = actualPrice;
    }

    @Override
    public String toString() {
        return String.format(action + " \n\t     at " + kilometer + "km");

    }


}

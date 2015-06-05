package com.blackrio.apprilia.POJO;

/**
 * Created by Stefan on 01.06.2015.
 */
public class ServiceRecordPOJO {
    private int recordId;       //fix aus DB
    private int kilometer;      //fix aus DB
    private String action;      //fix aus DB
    private boolean made;       //wird vom User staetig gesetzt aber immer nur einmal pro Record!

//region GETTER & SETTER
    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
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
//endregion

//region CONSTRUCOTR
    public ServiceRecordPOJO(int recordId, int kilometer, String action, boolean made) {
        this.recordId = recordId;
        this.kilometer = kilometer;
        this.action = action;
        this.made = made;
    }
//endregion
}

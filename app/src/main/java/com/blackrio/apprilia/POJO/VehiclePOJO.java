package com.blackrio.apprilia.POJO;

import java.util.ArrayList;

/**
 * Created by Stefan on 01.06.2015.
 */
public class VehiclePOJO {

    public int id;              //fix aus DB
    public String brand;        //fix aus DB
    public String type;         //fix aus DB
    public int price;           //fix aus DB
    int millage;                //User eingabe beim registrieren
    ArrayList<ServiceRecordPOJO> serviceRecords;    //fx aus DB

}

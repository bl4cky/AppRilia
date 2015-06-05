package com.blackrio.apprilia.Bean;

/**
 * Created by Stefan on 31.05.2015.
 */
public class Vehicle {

    private int id;
    private String brand;
    private String type;
    private int price;

//region GETTER & SETTER
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
//endregion

    public Vehicle(int id, String brand, String type, int price){
        this.id = id;
        this.brand = brand;
        this.type = type;
        this.price = price;

    }


}

package com.blackrio.apprilia.Bean;

/**
 * Created by Stefan on 31.05.2015.
 */
public class User {

    private String username;    //wird beim Registrieren oder Login eingegeben
    private String password;    //wird beim Registrieren oder Login eingegeben
    private String firstname;   //Registrieren oder fix aus DB
    private String lastname;    //Registrieren oder fix aus DB
    private String vehicleType;   //Registrieren oder fix aus DB
    private int kilometer;
    private String registrationDate;




    //region GETTER & SETTER

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public int getKilometer() {
        return kilometer;
    }

    public void setKilometer(int kilometer) {
        this.kilometer = kilometer;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    //endregion



    public User(String username, String password, String firstname, String lastname, String vehicleType, int kilometer, String registrationDate) {
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.vehicleType = vehicleType;
        this.kilometer = kilometer;
        this.registrationDate = registrationDate;
    }


    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.firstname = "";
        this.lastname = "";
        this.vehicleType = "";
        this.kilometer = -1;
        this.registrationDate = "";
    }

}
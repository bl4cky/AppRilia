package com.blackrio.apprilia.POJO;

/**
 * Created by Stefan on 01.06.2015.
 */
public class UserPOJO {

    private String username;    //wird beim Registrieren oder Login eingegeben
    private String password;    //wird beim Registrieren oder Login eingegeben
    private String firstname;   //Registrieren oder fix aus DB
    private String lastname;    //Registrieren oder fix aus DB
    private String vehicleType;   //Registrieren oder fix aus DB


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

    //endregion

//region CONSTRUCTOR
    public UserPOJO(String username, String password, String firstname, String lastname, String vehicleType) {
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.vehicleType = vehicleType;
    }
//endregion
}

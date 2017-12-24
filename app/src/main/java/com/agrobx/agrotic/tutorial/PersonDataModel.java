package com.agrobx.agrotic.tutorial;

import java.io.Serializable;

public class PersonDataModel implements Serializable {
    private String firstName;
    private String lastName;
    byte[] displayPicture;
    private int id;

    public PersonDataModel(String firstName, String lastName, byte[] displayPicture) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.displayPicture = displayPicture;
    }

    public String toString() {
        return firstName + ", " + lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public byte[] getDisplayPicture() {
        return displayPicture;
    }

    public void setDisplayPicture(byte[] displayPicture) {
        this.displayPicture = displayPicture;
    }
}
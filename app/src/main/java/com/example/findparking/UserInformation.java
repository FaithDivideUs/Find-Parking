package com.example.findparking;

public class UserInformation {

    public String name;
    public String address;

    public UserInformation(){

    }

    public UserInformation(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }
}

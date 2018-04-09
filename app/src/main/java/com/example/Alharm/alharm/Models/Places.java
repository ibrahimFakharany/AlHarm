package com.example.Alharm.alharm.Models;


import java.io.Serializable;

public class Places implements Serializable {

    private  String name;
    private  Double Longitude;
    private  Double Latitude;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }
}

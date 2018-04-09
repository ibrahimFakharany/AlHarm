package com.example.Alharm.alharm.Models;


import java.io.Serializable;
import java.util.HashMap;

public class Groups implements Serializable {
    private  String group_name;
    private  String guide_phone;
    private  Double Longitude;
    private  Double Latitude;


    public String getGuide_phone() {
        return guide_phone;
    }

    public void setGuide_phone(String guide_phone) {
        this.guide_phone = guide_phone;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
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

    public HashMap toMap(){
        HashMap<String, Object> HashMap = new HashMap<String, Object>();
        HashMap.put("group_name",group_name);
        HashMap.put("guide_phone",guide_phone);
        HashMap.put("Longitude",Longitude);
        HashMap.put("Latitude",Latitude);

        return HashMap;
    }
}

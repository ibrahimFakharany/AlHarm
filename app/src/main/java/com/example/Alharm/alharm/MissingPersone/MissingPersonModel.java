package com.example.Alharm.alharm.MissingPersone;

/**
 * Created by 450 G1 on 25/03/2018.
 */

public class MissingPersonModel {
    private String name;
    private String imgUrl;
    private double lat;
    private double lang;
    private int id;
    private String firebaseKey;
    private String state;
    private double confidence = -1;

    public MissingPersonModel() {
    }

    public MissingPersonModel(String firebaseKey, double confidence) {
        this.firebaseKey = firebaseKey;
        this.confidence = confidence;
    }


    public MissingPersonModel(String firebaseKey, String name, String image){

        this.firebaseKey = firebaseKey;
        this.name = name;
        this.imgUrl = image;
        this.confidence = -1;

    }

    public MissingPersonModel(String firebaseKey, String name, String image, double confidence){

        this.firebaseKey = firebaseKey;
        this.name = name;
        this.imgUrl = image;
        this.confidence = confidence;
    }

    public MissingPersonModel(String name, String imgUrl, double lat, double lang, String state) {
        this.name = name;
        this.imgUrl = imgUrl;
        this.lat = lat;
        this.lang = lang;
        this.state = state;
        this.confidence = -1;

    }

    public MissingPersonModel(String firebaseKey, String name, String imgUrl, double lat, double lang) {
        this.name = name;
        this.imgUrl = imgUrl;
        this.lat = lat;
        this.lang = lang;
        this.firebaseKey = firebaseKey;
        this.confidence = -1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLang() {
        return lang;
    }

    public void setLang(double lang) {
        this.lang = lang;
    }

    public String getFirebaseKey() {
        return firebaseKey;
    }

    public void setFirebaseKey(String firebaseKey) {
        this.firebaseKey = firebaseKey;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
}

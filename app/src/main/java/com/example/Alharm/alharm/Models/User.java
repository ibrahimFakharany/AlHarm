package com.example.Alharm.alharm.Models;


import java.io.Serializable;
import java.util.HashMap;

public class User implements Serializable {
    private String name;
    private String phone;
    private String email;
    private String password;
    private String documentUrl;
    private String userType;
    private String state;
    private String code;
    private String firebaseKey;
    private User user;
    public User(String firebaseKey,
            String name,
                String phone,
                String email,
                String password,
                String documentUrl,
                String userType,
                String state,
                String code) {

        this.firebaseKey = firebaseKey;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.documentUrl = documentUrl;
        this.userType = userType;
        this.state = state;
        this.code = code;
    }
    public User(
                String name,
                String phone,
                String email,
                String password,
                String documentUrl,
                String userType,
                String state,
                String code) {

        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.documentUrl = documentUrl;
        this.userType = userType;
        this.state = state;
        this.code = code;
    }

    public User(String firebaseKey, User user){
        this.firebaseKey = firebaseKey;
        this.user = user;
    }

    public User() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFirebaseKey() {
        return firebaseKey;
    }

    public void setFirebaseKey(String firebaseKey) {
        this.firebaseKey = firebaseKey;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public HashMap toMap() {
        HashMap<String, Object> HashMap = new HashMap<String, Object>();
        HashMap.put("name", name);
        HashMap.put("phone", phone);
        HashMap.put("email", email);
        HashMap.put("password", password);

        return HashMap;
    }
}

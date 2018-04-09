package com.example.Alharm.alharm.authentication;


import android.content.Context;
import android.content.SharedPreferences;

public class CustomSharedPreference {

    // ا class خاص بال  SharedPreference لوضع بة بيانات المستخدم ونوع المستخدم وحالة تسجيل الدخول .. حتي نستطيع استدعائهم في اي مكان داخل التطبيق


    private SharedPreferences sharedPref;

    public CustomSharedPreference(Context context) {
        sharedPref = context.getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE);
    }

    public SharedPreferences getInstanceOfSharedPreference(){
        return sharedPref;
    }

    //Save user information
    public void setUserData(String userData){
        sharedPref.edit().putString("USER", userData).apply();
    }
    public String getUserData(){
        return sharedPref.getString("USER", null);
    }


    public void setUserLogInState(String state){
        sharedPref.edit().putString("isLoggedIn", state).apply();
    }
    public String getUserLogInState(){
        return sharedPref.getString("isLoggedIn","false");
    }

    public void setUserType(String type){
        sharedPref.edit().putString("userType", type).apply();
    }
    public String getUserType(){return sharedPref.getString("userType",null);}


    public void setUserID(String userID){
        sharedPref.edit().putString("userID", userID).apply();
    }
    public String getUserID(){return sharedPref.getString("userID",null);}

}

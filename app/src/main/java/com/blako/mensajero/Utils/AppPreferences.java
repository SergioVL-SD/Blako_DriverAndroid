package com.blako.mensajero.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferences {

    private SharedPreferences sharedPreferences;

    private final String HUBS_LAST_CHECK  = "hubsLastCheck";
    private final String HUBS_REVISION  = "hubsRevision";
    private final String SYNC_TIME  = "syncTime";
    private final String FIREBASE_TOKEN  = "firebaseToken";

    public AppPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences("sharedData",Context.MODE_PRIVATE);
    }

    public void setHubsLastCheck(String date){
        sharedPreferences.edit().putString(HUBS_LAST_CHECK,date).apply();
    }

    public String getHubsLastCheck(){
        return sharedPreferences.getString(HUBS_LAST_CHECK,"");
    }

    public void setHubsRevision(int value){
        sharedPreferences.edit().putInt(HUBS_REVISION,value).apply();
    }

    public int getHubsRevision(){
        return sharedPreferences.getInt(HUBS_REVISION,0);
    }

    public void setSycTime(long value){
        sharedPreferences.edit().putLong(SYNC_TIME,value).apply();
    }

    public long getSycTime(){
        return sharedPreferences.getLong(SYNC_TIME,0);
    }

    public void setFirebaseToken(String token){
        sharedPreferences.edit().putString(FIREBASE_TOKEN,token).apply();
    }

    public String getFirebaseToken(){
        return sharedPreferences.getString(FIREBASE_TOKEN,"");
    }
}

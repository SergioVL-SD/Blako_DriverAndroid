package com.blako.mensajero.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferences {

    private SharedPreferences sharedPreferences;

    private final String HUBS_LAST_CHECK  = "hubsLastCheck";
    private final String HUBS_REVISION  = "hubsRevision";
    private final String HUBS_UPDATE  = "hubsUpdate";
    private final String SYNC_TIME  = "syncTime";
    private final String FIREBASE_TOKEN  = "firebaseToken";
    private final String HUBS_REGION_ID  = "hubsRegionId";
    private final String LAST_HUB_TIMESTAMP = "lastHubTimestamp";
    private final String TERMS_AND_CONDITIONS  = "termsAndConditions";

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

    public void setHubsUpdate(boolean value){
        sharedPreferences.edit().putBoolean(HUBS_UPDATE,value).apply();
    }

    public boolean getHubsUpdate(){
        return sharedPreferences.getBoolean(HUBS_UPDATE,true);
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

    public void setHubsRegionId(int id){
        sharedPreferences.edit().putInt(HUBS_REGION_ID,id).apply();
    }

    public int getHubsRegionId(){
        return sharedPreferences.getInt(HUBS_REGION_ID,0);
    }

    public void setLastHubTimestamp(Long timestamp){
        sharedPreferences.edit().putLong(LAST_HUB_TIMESTAMP,timestamp).apply();
    }

    public Long getLastHubTimestamp(){
        return sharedPreferences.getLong(LAST_HUB_TIMESTAMP,0);
    }

    public void setTermsAndConditions(boolean status){
        sharedPreferences.edit().putBoolean(TERMS_AND_CONDITIONS,status).apply();
    }

    public boolean getTermsAndConditions(){
        return sharedPreferences.getBoolean(TERMS_AND_CONDITIONS,false);
    }
}

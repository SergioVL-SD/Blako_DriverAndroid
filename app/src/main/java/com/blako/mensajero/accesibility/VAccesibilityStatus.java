package com.blako.mensajero.accesibility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.google.gson.Gson;

/**
 * Created by franciscotrinidad on 08/12/17.
 */

public class VAccesibilityStatus {
    public static int BUSY = 10;
    //////////////
    public static long lastTimeSended = 0;


    public static String getCurrentAppOnService(Context context) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        String app = sp.getString("appOnService", null);
        return sp.getString("appOnService", null);
    }

    public static void setCurrentAppOnService(Context context, String appOnService) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("appOnService", appOnService);
        editor.apply();
    }

    public static int getVictorStatus(Context context) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        int status = sp.getInt("status", 1);
        if (status != 0) {

        }
        return sp.getInt("status", 1);
    }




    public static long getLastTimeSended() {
        return lastTimeSended;
    }

    public static void setLastTimeSended(Context context, long _lastTimeSended) {
        lastTimeSended = _lastTimeSended;
    }


    public static VRequest getLastRequestTimeSended(Context context) {

        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        String json = sp.getString("LastRequestTimeSended", "");
        if (json == null || json.length() == 0)
            return null;

        Gson gson = new Gson();
        VRequest vRequest = gson.fromJson(json, VRequest.class);
        if (vRequest != null) {
            return vRequest;
        }

        return vRequest;
    }

    public static void setLastRequestTimeSended(Context context, VRequest vRequest) {
        Gson gson = new Gson();
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (vRequest == null) {
            editor.putString("LastRequestTimeSended", "");
        } else {
            String json = gson.toJson(vRequest, VRequest.class);
            editor.putString("LastRequestTimeSended", json);
        }
        editor.apply();
    }

    public static boolean getLastRequestOnWaySended(Context context) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        boolean status = sp.getBoolean("LastRequestOnWaySended", false);
        return status;
    }

    public static void setLastRequestOnWaySended(Context context, boolean statusConnection) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("LastRequestOnWaySended", statusConnection);
        editor.apply();
    }


    public static boolean getLastRequestArrivedSended(Context context) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        boolean status = sp.getBoolean("LastRequestArrived", false);
        return status;
    }

    public static void setLastRequestArrivedSended(Context context, boolean statusConnection) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("LastRequestArrived", statusConnection);
        editor.apply();
    }

    public static boolean getLastRequestStartedSended(Context context) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        boolean status = sp.getBoolean("LastRequestStarted", false);
        return status;
    }

    public static void setLastRequestStartedSended(Context context, boolean statusConnection) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("LastRequestStarted", statusConnection);
        editor.apply();
    }

    public static boolean getLastRequestFinishedSended(Context context) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        boolean status = sp.getBoolean("LastRequestFinished", false);
        return status;
    }

    public static void setLastRequestFinishedSended(Context context, boolean statusConnection) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("LastRequestFinished", statusConnection);
        editor.apply();
    }







    public static Location getCurrentUserLocation(Context context) {
        SharedPreferences sp = context.getSharedPreferences("VPreferences", Activity.MODE_PRIVATE);
        String latitude = sp.getString("currentUserLatitude", "");
        String longitude = sp.getString("currentUserLongitude", "");
        String acurancy = sp.getString("currentUserAcurancy", "");
        Location location = new Location("");

        if (latitude.length() > 0 && longitude.length() > 0) {

            try {
                location.setLatitude(Double.valueOf(latitude));
                location.setLongitude(Double.valueOf(longitude));
                location.setAccuracy(Float.parseFloat(acurancy));
            } catch (Exception e) {

            }
        } else {
            location = null;
        }
        return location;
    }

    public static void setCurrentUserLocation(Location currentUserLocation, Context context) {
        SharedPreferences sp = context.getSharedPreferences("VPreferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (currentUserLocation != null) {
            editor.putString("currentUserLatitude", "" + currentUserLocation.getLatitude());
            editor.putString("currentUserLongitude", "" + currentUserLocation.getLongitude());
            editor.putString("currentUserAcurancy", "" + currentUserLocation.getAccuracy());
        } else {
            editor.putString("currentUserLatitude", "");
            editor.putString("currentUserLongitude", "");
            editor.putString("currentUserAcurancy", "");
        }
        editor.apply();
    }

    public static String getLastKeyService(Context context) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        return sp.getString("LastKeyService", null);
    }


}

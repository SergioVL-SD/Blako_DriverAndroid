package com.blako.mensajero;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import com.blako.mensajero.VO.BkoChildTripVO;
import com.blako.mensajero.VO.BkoOffer;
import com.blako.mensajero.VO.BkoPushRequest;
import com.blako.mensajero.VO.BkoRecoverStatusVO;
import com.blako.mensajero.VO.BkoTripVO;
import com.blako.mensajero.VO.BkoTrips;
import com.blako.mensajero.VO.BkoVehicleVO;
import com.blako.mensajero.VO.BkoWareHouseVO;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class BkoDataMaganer {
    private static Location currentUserLocation;
    private static Integer statusService;
    private static String requestId;
    private static BkoPushRequest statusRequest;
    private static BkoRecoverStatusVO recoverStatusVO;

    // URLS
    private static String enviromentUrl;
    private static BkoVehicleVO currentVehicle;
    private static BkoTrips currenntTrips;
    private static BkoChildTripVO currenntTrip;
    private static int hideGPSSended;
    private static Boolean autoNavigate;


    public static boolean getOnDemand(Context context) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        return sp.getBoolean("OnDemand", false);
    }

    public static void setOnDemand(Context context, boolean OnDemand) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("OnDemand", OnDemand);
        editor.apply();
    }

    public static boolean getAutoNavigate(Context context) {

        if (autoNavigate != null)
            return autoNavigate;

        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        autoNavigate = sp.getBoolean("autoNavigate", false);
        return autoNavigate;
    }

    public static void setAutoNavigate(Context context, boolean _autoNavigate) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("autoNavigate", _autoNavigate);
        editor.apply();
        autoNavigate = _autoNavigate;
    }


    public static int gethideGPSSended(Context context) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        hideGPSSended = sp.getInt("hideGPSSended", 0);
        return hideGPSSended;
    }

    public static void sethideGPSSended(Context context, boolean reset) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (reset) {

            editor.putInt("hideGPSSended", 0);
        } else {
            int totalHide = gethideGPSSended(context);
            editor.putInt("hideGPSSended", (totalHide + 1));
        }
        editor.apply();
    }


    public static BkoTripVO getCuurentTemporaryTrip() {
        return cuurentTemporaryTrip;
    }

    public static void setCuurentTemporaryTrip(BkoTripVO cuurentTemporaryTrip) {
        BkoDataMaganer.cuurentTemporaryTrip = cuurentTemporaryTrip;
    }


    private static BkoTripVO cuurentTemporaryTrip;


    public static BkoChildTripVO getCurrentTrip(Context context) {

        if (currenntTrip != null)
            return currenntTrip;

        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        String json = sp.getString("currentTrip", "");
        if (json == null || json.length() == 0)
            return null;

        Gson gson = new Gson();
        BkoDataMaganer.currenntTrip = gson.fromJson(json, BkoChildTripVO.class);
        return currenntTrip;
    }

    public static void setCurrentTrip(BkoChildTripVO trip, Context context) {
        Gson gson = new Gson();
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (trip == null) {
            editor.putString("currentTrip", "");
        } else {
            String json = gson.toJson(trip, BkoChildTripVO.class);
            editor.putString("currentTrip", json);
        }
        editor.commit();
        BkoDataMaganer.currenntTrip = trip;
    }


    public static void setWareHouseSelected(BkoWareHouseVO wareHouseVO, Context context) {
        Gson gson = new Gson();
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (wareHouseVO == null) {
            editor.putString("wareHouseSelected", "");
        } else {
            String json = gson.toJson(wareHouseVO, BkoWareHouseVO.class);
            editor.putString("wareHouseSelected", json);
        }
        editor.commit();
    }


    public static BkoTrips getCurrentTrips(Context context) {

        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        String json = sp.getString("trips", "");
        if (json == null || json.length() == 0)
            return null;

        Gson gson = new Gson();
        BkoDataMaganer.currenntTrips = gson.fromJson(json, BkoTrips.class);
        return currenntTrips;
    }

    public static void setCurrentTrips(BkoTrips trips, Context context) {
        Gson gson = new Gson();
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (trips == null) {
            editor.putString("trips", "");
        } else {
            String json = gson.toJson(trips, BkoTrips.class);
            editor.putString("trips", json);
        }
        editor.commit();
        BkoDataMaganer.currenntTrips = trips;
    }


    public static BkoOffer.BkoAnnoucement getCurrentOffer(Context context) {

        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        String json = sp.getString("currentOffer", "");
        if (json == null || json.length() == 0)
            return null;

        Gson gson = new Gson();
        BkoOffer.BkoAnnoucement offer = gson.fromJson(json, BkoOffer.BkoAnnoucement.class);
        return offer;
    }

    public static void setCurrentOffer(BkoOffer.BkoAnnoucement statusRequest, Context context) {
        Gson gson = new Gson();
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (statusRequest == null) {
            editor.putString("currentOffer", "");
        } else {
            String json = gson.toJson(statusRequest, BkoOffer.BkoAnnoucement.class);
            editor.putString("currentOffer", json);
        }
        editor.commit();
        // BkoDataMaganer.statusRequest = statusRequest;
    }

    public static BkoPushRequest getCurrentStatusRequest(Context context) {

        if (BkoDataMaganer.statusRequest != null)
            return statusRequest;

        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        String json = sp.getString("statusRquest", "");
        if (json == null || json.length() == 0)
            return null;


        Gson gson = new Gson();
        BkoDataMaganer.statusRequest = gson.fromJson(json, BkoPushRequest.class);
        return statusRequest;
    }

    public static void setStatusRequest(BkoPushRequest statusRequest, Context context) {
        Gson gson = new Gson();
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (statusRequest == null) {
            editor.putString("statusRquest", "");
        } else {
            String json = gson.toJson(statusRequest, BkoPushRequest.class);
            editor.putString("statusRquest", json);
        }
        editor.commit();
        BkoDataMaganer.statusRequest = statusRequest;
    }

    //-------------------------------------------------------------------->

    public static BkoRecoverStatusVO getAllRecoverStatus(Context context) {

        if (BkoDataMaganer.recoverStatusVO != null)
            return recoverStatusVO;

        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        String json = sp.getString("allRecoverStatus", "");
        if (json == null || json.length() == 0)
            return null;


        Gson gson = new Gson();
        BkoDataMaganer.recoverStatusVO = gson.fromJson(json, BkoRecoverStatusVO.class);
        return recoverStatusVO;
    }

    public static void setAllRecoverStatus(BkoRecoverStatusVO recoverStatusVO, Context context) {
        Gson gson = new Gson();
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (recoverStatusVO == null) {
            editor.putString("allRecoverStatus", "");
        } else {
            String json = gson.toJson(recoverStatusVO, BkoRecoverStatusVO.class);
            editor.putString("allRecoverStatus", json);
        }
        editor.commit();
        BkoDataMaganer.recoverStatusVO = recoverStatusVO;
    }

    //--------------------------------------------------------------------<


    public static BkoVehicleVO getCurrentVehicle(Context context) {

        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        String json = sp.getString("currentVehicle", "");
        if (json == null || json.length() == 0)
            return null;

        Gson gson = new Gson();
        BkoDataMaganer.currentVehicle = gson.fromJson(json, BkoVehicleVO.class);
        return currentVehicle;
    }

    public static void setCurrentVehicle(BkoVehicleVO currentVehicle, Context context) {
        Gson gson = new Gson();
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (currentVehicle == null) {
            editor.putString("currentVehicle", "");
        } else {
            String json = gson.toJson(currentVehicle, BkoVehicleVO.class);
            editor.putString("currentVehicle", json);
        }

        editor.commit();
        BkoDataMaganer.currentVehicle = currentVehicle;
    }


    public static boolean getPolicyReaded(Context context) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
       return sp.getBoolean("policy",false);

    }

    public static void setPolicyReaded(boolean policy, Context context) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("policy", policy);
        editor.apply();

    }


    public static void setEnviromentId(String enviromentId, Context context) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("enviromentId", enviromentId);
        editor.apply();

    }

    public static String getEnviromentUrl(Context context) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        enviromentUrl = sp.getString("enviromentUrl", "");
        return enviromentUrl;
    }

    public static void setEnviromentUrl(String enviromentUrl, Context context) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("enviromentUrl", enviromentUrl);
        editor.apply();
        BkoDataMaganer.enviromentUrl = enviromentUrl;
    }


    public static String getRequestId(Context context) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        requestId = sp.getString("requestId", "");
        return requestId;
    }

    public static void setRequestId(String requestId, Context context) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("requestId", requestId);
        editor.commit();
        BkoDataMaganer.requestId = requestId;
    }


    public static boolean isHasOtherSessionActive(Context context) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        boolean hasOtherSessionActive = sp.getBoolean("hasOtherSessionActive", false);
        return hasOtherSessionActive;
    }

    public static void setHasOtherSessionActive(boolean hasOtherSessionActive, Context context) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("hasOtherSessionActive", hasOtherSessionActive);
        editor.commit();

    }


    public static Location getCurrentUserLocation(Context context) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        String latitude = sp.getString("currentUserLatitude", "");
        String longitude = sp.getString("currentUserLongitude", "");
        String acurancy = sp.getString("currentUserAcurancy", "");
        String currentUserBearing = sp.getString("currentUserBearing", "");
        String time = sp.getString("currentUserTime", "");

        String mock = sp.getString("mock", "");

        String speed = sp.getString("currentUserTime", "currentUserSpeed");
        Location location = new Location("");

        if (latitude != null && latitude.length() > 0 && longitude != null && longitude.length() > 0) {

            try {
                location.setLatitude(Double.valueOf(latitude));
                location.setLongitude(Double.valueOf(longitude));
                location.setAccuracy(Float.parseFloat(acurancy));
                location.setBearing(Float.parseFloat(currentUserBearing));
                location.setTime(Long.parseLong(time));
                location.setSpeed(Float.parseFloat(speed));


                if (mock.equals("1"))
                    if (android.os.Build.VERSION.SDK_INT >= 18) {

                        location.setProvider("mock");
                    } else {
                        location.setProvider("");
                    }

            } catch (Exception e) {

            }
        } else {
            location = null;
        }
        currentUserLocation = location;
        return currentUserLocation;
    }

    public static void setCurrentUserLocation(Location currentUserLocation, Context context) {

        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (currentUserLocation != null) {
            editor.putString("currentUserLatitude", "" + currentUserLocation.getLatitude());
            editor.putString("currentUserLongitude", "" + currentUserLocation.getLongitude());
            editor.putString("currentUserAcurancy", "" + currentUserLocation.getAccuracy());
            editor.putString("currentUserBearing", "" + currentUserLocation.getBearing());
            editor.putString("currentUserSpeed", "" + currentUserLocation.getSpeed());
            editor.putString("currentUserTime", "" + currentUserLocation.getTime());
            if (android.os.Build.VERSION.SDK_INT >= 18) {

                if (currentUserLocation.isFromMockProvider() || (currentUserLocation.getProvider() != null && currentUserLocation.getProvider().equals("mock")))
                    editor.putString("mock", "" + "1");
                else
                    editor.putString("mock", "" + "0");
            } else {
                if ((currentUserLocation.getProvider() != null && currentUserLocation.getProvider().equals("mock")))
                    editor.putString("mock", "" + "1");
                else
                    editor.putString("mock", "" + "0");
            }


        } else {
            editor.putString("currentUserLatitude", "");
            editor.putString("currentUserLongitude", "");
            editor.putString("currentUserAcurancy", "");
            editor.putString("currentUserBearing", "");
            editor.putString("currentUserSpeed", "");
            editor.putString("currentUserTime", "");
            editor.putString("mock", "");
        }

        // TODO: 23/07/2018 Trying apply insted of commit to resolve problem
        editor.commit();
        BkoDataMaganer.currentUserLocation = currentUserLocation;

        if (currentUserLocation != null)
            Log.i("current Location : ", "lon" + currentUserLocation.getLongitude() + " lat" + currentUserLocation.getLatitude());
    }


    public static void setLastTimeSendedUserLocation(Long currentUserTimeLocation, Context context) {

        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("currentUserSTime",  currentUserTimeLocation);
        editor.apply();
    }

    public static long getLastTimeSendedUserLocation(Context context) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        long currentUserTimeLocation = sp.getLong("currentUserSTime", 0);
        return currentUserTimeLocation;
    }

    // TODO: 13/07/2018 Revisar cercania del usuario a la locación para habilitar el botón de aceptar de acuerdo a la cercania
    public static int getStatusService(Context context) {
        if (BkoDataMaganer.statusService != null)
            return BkoDataMaganer.statusService;

        try{
            SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
            int status = sp.getInt("statusService", 0);
            return status;
        }catch (NullPointerException e){
            e.printStackTrace();
            return 0;
        }
    }

    public static void setStatusService(int statusService, Context context) {

        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("statusService", statusService);
        editor.commit();
        BkoDataMaganer.statusService = statusService;
    }


    public static void clearData(Context context) {
        try {

            BkoDataMaganer.setCurrentOffer(null, context);
            BkoDataMaganer.setStatusRequest(null, context);
            BkoDataMaganer.setCurrentTrips(null, context);
            BkoDataMaganer.setCurrentTrip(null, context);
            BkoDataMaganer.setCuurentTemporaryTrip(null);
            BkoDataMaganer.setWareHouseSelected(null, context);
            BkoDataMaganer.setCurrentVehicle(null, context);
            BkoDataMaganer.setRequestId(null, context);
            BkoDataMaganer.setStatusService(Constants.SERVICE_STATUS_FREE, context);
            BkoDataMaganer.setHasOtherSessionActive(false, context);
            BkoDataMaganer.setOnDemand(context,false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static boolean getSendedFirebaseToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        return sp.getBoolean("sendedFirebaseToken", false);
    }

    public static void setSendedFirebaseToken(Context context, boolean sendedFirebaseToken) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("sendedFirebaseToken", sendedFirebaseToken);
        editor.apply();

    }


    public static boolean getDeadNotification(Context context) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        return sp.getBoolean("deadNotification", true);
    }

    public static void setDeadNotification(Context context, boolean status) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("deadNotification", status);
        editor.apply();
    }

    public static boolean isGpsDead(Context context) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        return sp.getBoolean("gpsDead", true);
    }

    public static void setGpsDead(Context context, boolean status) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("gpsDead", status);
        editor.apply();
    }


    public static Long getLastLocationTimeStamp(Context context) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        return sp.getLong("lastLocationTimeStamp", 0);
    }

    public static void setLastLocationTimeStamp(Context context, Long ts) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("lastLocationTimeStamp", ts);
        editor.apply();
    }

    public static String getRestaurantPhone(Context context) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        return sp.getString("restaurantPhone", "");
    }

    public static void setRestaurantPhone(Context context, String phone) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("restaurantPhone", phone);
        editor.apply();
    }

    public static String getActualDeliveryZone(Context context) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        return sp.getString("actualDeliveryZone", "");
    }

    public static void setActualDeliveryZone(Context context, String zone) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("actualDeliveryZone", zone);
        editor.apply();
    }

    public static int getActualDeliveryZoneId(Context context) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        return sp.getInt("actualDeliveryZoneId", 1);
    }

    public static void setActualDeliveryZoneId(Context context, int zoneId) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("actualDeliveryZoneId", zoneId);
        editor.apply();
    }

    public static String getWorkerId(Context context) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        return sp.getString("workerId", "85");
    }

    public static void setWorkerId(Context context, String workerId) {
        SharedPreferences sp = context.getSharedPreferences("blakoPreferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("workerId", workerId);
        editor.apply();
    }

}

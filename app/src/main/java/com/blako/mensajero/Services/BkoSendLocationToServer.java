package com.blako.mensajero.Services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.blako.mensajero.BkoDataMaganer;
import com.blako.mensajero.Constants;
import com.blako.mensajero.Dao.BkoUserDao;
import com.blako.mensajero.Utils.BkoUtilities;
import com.blako.mensajero.Utils.DeliveryZoneCheck;
import com.blako.mensajero.Utils.HttpRequest;
import com.blako.mensajero.VO.BkoChildTripVO;
import com.blako.mensajero.VO.BkoOrderVO;
import com.blako.mensajero.VO.BkoPushRequest;
import com.blako.mensajero.VO.BkoRecoverStatusVO;
import com.blako.mensajero.VO.BkoRequestResponse;
import com.blako.mensajero.VO.BkoTripVO;
import com.blako.mensajero.VO.BkoUser;
import com.blako.mensajero.Views.BkoMainActivity;
import com.blako.mensajero.firebase.BkoFirebaseDatabase;
import com.blako.mensajero.firebase.BkoFirebaseStorage;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlPlacemark;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by franciscotrinidad on 2/23/16.
 */
public class BkoSendLocationToServer extends IntentService {

    private static final String TAG = "BkoSendLocationToServer";

    public BkoSendLocationToServer() {
        super(TAG);
    }

    private boolean currentlyProcessingLocation = false;
    //private FirebaseDatabase firebaseDatabase;
    //private DatabaseReference locationDatabaseReference;

    @Override
    protected void onHandleIntent(Intent intent) {


        if (currentlyProcessingLocation) {
            Log.d(TAG, "RETURN: " + "BkoSendLocationToServer**");

            return;
        }

        Location location = BkoDataMaganer.getCurrentUserLocation(this);

        /*firebaseDatabase= BkoFirebaseDatabase.getDatabase();

        try {
            BkoUser user = BkoUserDao.Consultar(this);
            if (user!=null){
                Log.d("FB_DB_User",user.getWorkerId());
                locationDatabaseReference= firebaseDatabase.getReference().child("worker").child(user.getWorkerId());
                locationDatabaseReference.keepSynced(true);
            }

        } catch (Exception e) {
            e.fillInStackTrace();
        }*/

        if (location!=null){
            if (BkoDataMaganer.getOnDemand(this) || BkoDataMaganer.getCurrentOffer(this)!=null) {
                Log.d(TAG, "SENDING: " + "(gps) BkoSendLocationToServer**");
                Log.d("SendingLocation","Ok");
                sendLocationDataToWebsite(location);
            }
        }
    }


    protected void sendLocationDataToWebsite(final Location location) {

        /*if (locationDatabaseReference!=null){
            Log.d("FB_location","Ok");
            String schema;
            double lat= location.getLatitude();
            double lng= location.getLongitude();
            locationDatabaseReference.child("currentLocation").child("lat").setValue(lat);
            locationDatabaseReference.child("currentLocation").child("lng").setValue(lng);
            if(BkoDataMaganer.getCurrentOffer(this)!=null){
                schema = "1";
            }  else if(BkoDataMaganer.getOnDemand(this)){
                schema = "2";
            } else {
                schema= "0";
            }
            locationDatabaseReference.child("currentLocation").child("schemaID").setValue(schema);
            String ts = BkoUtilities.getTimeStamp().toString();
            locationDatabaseReference.child("currentLocation").child("ts").setValue(ts);
            locationDatabaseReference.child("locations").child(ts).child("lat").setValue(lat);
            locationDatabaseReference.child("locations").child(ts).child("lng").setValue(lng);
            String zone= BkoDataMaganer.getActualDeliveryZone(this);
            if (!zone.equals("")){
                locationDatabaseReference.child("locations").child(ts).child("zone").setValue(zone);
            }
            BkoRecoverStatusVO recoverStatusVO= BkoDataMaganer.getAllRecoverStatus(BkoSendLocationToServer.this);
            if (recoverStatusVO!=null){
                for (int i=0;i<recoverStatusVO.getOrder().size();i++){
                    BkoOrderVO orderVO= recoverStatusVO.getOrder().get(i);
                    locationDatabaseReference.child("currentLocation").child("orders").child(String.valueOf(i)).setValue(orderVO.getBko_orders_id());
                    locationDatabaseReference.child("locations").child(ts).child("orders").child(String.valueOf(i)).setValue(orderVO.getBko_orders_id());
                }
            }
        }*/

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getDefault());
        location.setTime(Calendar.getInstance().getTime().getTime());
        currentlyProcessingLocation = true;


        try {

            final BkoUser user = BkoUserDao.Consultar(this);


            if (user != null) {
                String ts = BkoUtilities.nowCurrent();
                String file = "v";
                String customerId = "0";
                String oId = "0";
                String banderaEta = "0";
                String tripId = "";
                String schema_id = "";
                String LatitudCustomer = null, LongitudCustomer = null;

                try {

                    BkoMainActivity.updateMyActivity(this, "", "location");
                    int status = BkoDataMaganer.getStatusService(BkoSendLocationToServer.this);
                    if (status == Constants.SERVICE_STATUS_INCOMING_REQUEST || status == Constants.SERVICE_STATUS_REQUEST_ACEPTED)
                        file = "b";

                    BkoPushRequest serviceClienteStatus = BkoDataMaganer.getCurrentStatusRequest(BkoSendLocationToServer.this);

                    if (serviceClienteStatus != null) {
                        customerId = serviceClienteStatus.getCustomerId();
                        oId = serviceClienteStatus.getOid();
                        banderaEta = "1";
                        file = "bs";
                        LatitudCustomer = serviceClienteStatus.getLat();
                        LongitudCustomer = serviceClienteStatus.getLng();
                        if (status >= Constants.SERVICE_STATUS_WORKER_ARRIVED) {
                            LatitudCustomer = null;
                            LongitudCustomer = null;

                        }


                        if (BkoDataMaganer.getCurrentTrips(BkoSendLocationToServer.this) != null && BkoDataMaganer.getCurrentTrips(BkoSendLocationToServer.this).getTrips() != null &&
                                BkoDataMaganer.getCurrentTrips(BkoSendLocationToServer.this).getTrips().size() != 0) {

                            try {
                                BkoTripVO parentTrip = BkoDataMaganer.getCuurentTemporaryTrip();
                                if (parentTrip != null) {
                                    BkoChildTripVO trip;
                                    if (parentTrip.getOrigen().get(0).getBko_orders_trips_item_confirm() == null || parentTrip.getOrigen().get(0).getBko_orders_trips_item_confirm().length() == 0) {
                                        trip = parentTrip.getOrigen().get(0);
                                    } else {
                                        trip = parentTrip.getDestino().get(0);

                                        if (trip.getBko_orders_trips_startdatetime() != null && trip.getBko_deliverystatus_id() != null && trip.getBko_deliverystatus_id().equals("8")) {


                                            if (trip.getBko_customeraddress_latitude() != null && trip.getBko_customeraddress_longitude() != null) {


                                                try {
                                                    Location locationClient = new Location("");
                                                    locationClient.setLatitude(Double.parseDouble(trip.getBko_customeraddress_latitude()));
                                                    locationClient.setLongitude(Double.parseDouble(trip.getBko_customeraddress_longitude()));


                                                    if (location.distanceTo(locationClient) <= 100) {
                                                        Map<String, String> mapVisible = new HashMap<String, String>();
                                                        mapVisible.put("workerId", user.getWorkerId());
                                                        mapVisible.put("tripIdO", parentTrip.getOrigen().get(0).getBko_orders_trips_id());
                                                        mapVisible.put("tripIdD", parentTrip.getDestino().get(0).getBko_orders_trips_id());
                                                        mapVisible.put("tripType", "destino");
                                                        mapVisible.put("orderId", parentTrip.getBko_orders_id());

                                                        String arriveResponse = HttpRequest.get(Constants.GET_REPORT_CHECKIN(this), mapVisible, true).connectTimeout(5000).readTimeout(5000).body();

                                                        if (arriveResponse != null) {
                                                            Gson gson = new Gson();
                                                            BkoRequestResponse response = gson.fromJson(arriveResponse, BkoRequestResponse.class);
                                                            if (response.isResponse()) {
                                                                BkoDataMaganer.getCuurentTemporaryTrip().getDestino().get(0).setBko_deliverystatus_id("6");
                                                            }
                                                        }

                                                    }


                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }


                                        }
                                    }


                                    tripId = "&tripId=" + trip.getBko_orders_trips_id();
                                    LatitudCustomer = trip.getBko_customeraddress_latitude();
                                    LongitudCustomer = trip.getBko_customeraddress_longitude();

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        } else {
                            file = "b";

                        }

                    }

                    if (!user.isAvailable() || (BkoDataMaganer.getCurrentOffer(this) == null && !BkoDataMaganer.getOnDemand(this))) {
                        file = "h";
                        LatitudCustomer = null;
                        LongitudCustomer = null;
                    }

                    if (file.equals("h")) {
                        int total = BkoDataMaganer.gethideGPSSended(BkoSendLocationToServer.this);
                        if (total == 3) {
                            return;
                        }
                    } else {
                        BkoDataMaganer.sethideGPSSended(BkoSendLocationToServer.this, true);
                    }


                    String partnerId = user.getWorkerParentId();
                    if (partnerId == null || partnerId.equals("0"))
                        partnerId = user.getWorkerId();
                    else
                        partnerId = user.getWorkerParentId();

                    String responseUploadPostion;
                    Map<String, String> mapComplete = new HashMap<String, String>();
                    mapComplete.put("workerEmail", user.getEmail());
                    mapComplete.put("name", user.getName());
                    mapComplete.put("lastname", user.getLastname());
                    mapComplete.put("status", "available");
                    mapComplete.put("file", file);
                    mapComplete.put("Latitud", "" + location.getLatitude());
                    mapComplete.put("Longitud", "" + location.getLongitude());
                    mapComplete.put("oid", oId);
                    mapComplete.put("banderaEta", banderaEta);
                    mapComplete.put("workerId", user.getWorkerId());
                    mapComplete.put("hasStarted", "0");
                    mapComplete.put("customerId", customerId);
                    mapComplete.put("networkLocation", location.getProvider());
                    mapComplete.put("partnerId", partnerId);
                    mapComplete.put("visibilityType", user.getVisibilitytype());
                    mapComplete.put("dateTimeGPS", ts);

                    if (LatitudCustomer != null) {
                        mapComplete.put("LatitudCustomer", LatitudCustomer);
                        mapComplete.put("LongitudCustomer", LongitudCustomer);
                    }
                    if(BkoDataMaganer.getCurrentOffer(this)!=null){
                        schema_id = "1";
                    }  else if(BkoDataMaganer.getOnDemand(this)){
                        schema_id = "2";
                    }

                    if(schema_id.length()!= 0){
                        mapComplete.put("schema_id", schema_id);
                    }

                    if (tripId.length() > 0)
                        mapComplete.put("tripId", tripId);

                    String accurancyExact = "0";

                    if (location.getAccuracy() < 500.0f) {

                        accurancyExact = "1";
                    }

                    mapComplete.put("accurate", accurancyExact);

                    responseUploadPostion = HttpRequest
                            .get(Constants.GET_UPLOAD_GPS_POSITION(BkoSendLocationToServer.this), mapComplete, true)
                            .connectTimeout(3500).body();

                    if (responseUploadPostion != null) {
                        BkoDataMaganer.setLastTimeSendedUserLocation(location.getTime(), this);
                        BkoMainActivity.updateMyActivity(this, "", "gpsSended");
                        Log.d(TAG, "Upload Response: " + responseUploadPostion);
                        if (tripId.length() > 0 && BkoDataMaganer.getCurrentTrip(BkoSendLocationToServer.this) != null) {


                            BkoUtilities.createTxt(oId, ts + "," + location.getLatitude() + "," + location.getLongitude(), BkoDataMaganer.getCurrentTrip(BkoSendLocationToServer.this).getBko_orders_trips_id());

                        }

                        if (file.equals("h")) {
                            BkoDataMaganer.sethideGPSSended(BkoSendLocationToServer.this, false);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                    long timeLocation = location.getTime();
                    long lastTimeSendedLocation = BkoDataMaganer.getLastTimeSendedUserLocation(this);

                    if (lastTimeSendedLocation != 0) {


                        long difference = timeLocation - lastTimeSendedLocation;

                        if (difference > 300000) {
                            if (!BkoUtilities.isActivityRunning(BkoMainActivity.class, this)) {
                                Intent intentone = new Intent().setClass(this, BkoMainActivity.class);
                                intentone.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intentone.putExtra("withOutSended", "1");
                                //startActivity(intentone);
                            }
                            BkoMainActivity.updateMyActivity(this, "" + difference / 1000, "withOutSended");
                        }
                    }
                    if (tripId.length() > 0 && BkoDataMaganer.getCurrentTrip(BkoSendLocationToServer.this) != null) {

                        BkoUtilities.createTxt(oId, ts + "," + location.getLatitude() + "," + location.getLongitude(), BkoDataMaganer.getCurrentTrip(BkoSendLocationToServer.this).getBko_orders_trips_id());

                    }
                }


            } else {
                stopSelf();
            }
        } catch (Exception e) {
            e.printStackTrace();


        }


        currentlyProcessingLocation = false;
    }


    public static boolean oneLocationDataToWebsite(final Location location, Context context) {

        if (location == null)
            return true;

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getDefault());
        location.setTime(Calendar.getInstance().getTime().getTime());

        boolean sended = false;


        try {

            final BkoUser user = BkoUserDao.Consultar(context);


            if (user != null) {
                String ts = BkoUtilities.nowCurrent();
                String file = "v";
                String customerId = "0";
                String oId = "0";
                String banderaEta = "0";
                String tripId = "";
                String schema_id = "";
                String LatitudCustomer = null, LongitudCustomer = null;

                try {

                    BkoMainActivity.updateMyActivity(context, "", "location");


                    int status = BkoDataMaganer.getStatusService(context);

                    if (status == Constants.SERVICE_STATUS_INCOMING_REQUEST || status == Constants.SERVICE_STATUS_REQUEST_ACEPTED)
                        file = "b";

                    BkoPushRequest serviceClienteStatus = BkoDataMaganer.getCurrentStatusRequest(context);

                    if (serviceClienteStatus != null) {
                        customerId = serviceClienteStatus.getCustomerId();
                        oId = serviceClienteStatus.getOid();
                        banderaEta = "1";
                        file = "bs";


                        LatitudCustomer = serviceClienteStatus.getLat();
                        LongitudCustomer = serviceClienteStatus.getLng();
                        if (status >= Constants.SERVICE_STATUS_WORKER_ARRIVED) {
                            LatitudCustomer = null;
                            LongitudCustomer = null;

                        }

                        if (BkoDataMaganer.getCurrentTrips(context) != null && BkoDataMaganer.getCurrentTrips(context).getTrips() != null &&
                                BkoDataMaganer.getCurrentTrips(context).getTrips().size() != 0) {

                            try {
                                BkoTripVO parentTrip = BkoDataMaganer.getCuurentTemporaryTrip();

                                BkoTripVO firstTrip =BkoDataMaganer.getCurrentTrips(context).getTrips().get(0);
                                BkoChildTripVO firstOriginDesTrip = null;

                                if (parentTrip != null) {
                                    BkoChildTripVO trip;
                                    if (parentTrip.getOrigen().get(0).getBko_orders_trips_item_confirm() == null || parentTrip.getOrigen().get(0).getBko_orders_trips_item_confirm().length() == 0) {
                                        trip = parentTrip.getOrigen().get(0);
                                    } else {
                                        trip = parentTrip.getDestino().get(0);
                                        if (trip.getBko_orders_trips_startdatetime() != null && trip.getBko_deliverystatus_id() != null && trip.getBko_deliverystatus_id().equals("8")) {


                                            if (trip.getBko_customeraddress_latitude() != null && trip.getBko_customeraddress_longitude() != null) {


                                                try {
                                                    Location locationClient = new Location("");
                                                    locationClient.setLatitude(Double.parseDouble(trip.getBko_customeraddress_latitude()));
                                                    locationClient.setLongitude(Double.parseDouble(trip.getBko_customeraddress_longitude()));


                                                    if (location.distanceTo(locationClient) <= 100) {
                                                        Map<String, String> mapVisible = new HashMap<String, String>();
                                                        mapVisible.put("workerId", user.getWorkerId());
                                                        mapVisible.put("tripIdO", parentTrip.getOrigen().get(0).getBko_orders_trips_id());
                                                        mapVisible.put("tripIdD", parentTrip.getDestino().get(0).getBko_orders_trips_id());
                                                        mapVisible.put("tripType", "destino");
                                                        mapVisible.put("orderId", parentTrip.getBko_orders_id());

                                                        String arriveResponse = HttpRequest.get(Constants.GET_REPORT_CHECKIN(context), mapVisible, true).connectTimeout(5000).readTimeout(5000).body();

                                                        if (arriveResponse != null) {
                                                            Gson gson = new Gson();
                                                            BkoRequestResponse response = gson.fromJson(arriveResponse, BkoRequestResponse.class);
                                                            if (response.isResponse()) {
                                                                BkoDataMaganer.getCuurentTemporaryTrip().getDestino().get(0).setBko_deliverystatus_id("6");
                                                            }
                                                        }

                                                    }


                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }


                                        }
                                    }



                                }

                                if (firstTrip.getOrigen().get(0).getBko_orders_trips_item_confirm() == null || firstTrip.getOrigen().get(0).getBko_orders_trips_item_confirm().length() == 0) {
                                    firstOriginDesTrip = firstTrip.getOrigen().get(0);
                                } else {
                                    firstOriginDesTrip = firstTrip.getDestino().get(0);

                                }

                                tripId = "&tripId=" + firstOriginDesTrip.getBko_orders_trips_id();
                                LatitudCustomer = firstOriginDesTrip.getBko_customeraddress_latitude();
                                LongitudCustomer = firstOriginDesTrip.getBko_customeraddress_longitude();


                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        } else {
                            file = "b";

                        }

                    }

                    if (!user.isAvailable()|| (BkoDataMaganer.getCurrentOffer(context) == null && !BkoDataMaganer.getOnDemand(context))) {
                        file = "h";
                        LatitudCustomer = null;
                        LongitudCustomer = null;
                    }

                    if (file.equals("h")) {
                        int total = BkoDataMaganer.gethideGPSSended(context);
                        if (total == 3) {
                            return true;
                        }
                    } else {
                        BkoDataMaganer.sethideGPSSended(context, true);
                    }


                    String partnerId = user.getWorkerParentId();
                    if (partnerId == null || partnerId.equals("0"))
                        partnerId = user.getWorkerId();
                    else
                        partnerId = user.getWorkerParentId();

                    String responseUploadPostion;
                    Map<String, String> mapComplete = new HashMap<String, String>();
                    mapComplete.put("workerEmail", user.getEmail());
                    mapComplete.put("name", user.getName());
                    mapComplete.put("lastname", user.getLastname());
                    mapComplete.put("status", "available");
                    mapComplete.put("file", file);
                    mapComplete.put("Latitud", "" + location.getLatitude());
                    mapComplete.put("Longitud", "" + location.getLongitude());
                    mapComplete.put("oid", oId);
                    mapComplete.put("banderaEta", banderaEta);
                    mapComplete.put("workerId", user.getWorkerId());
                    mapComplete.put("hasStarted", "0");
                    mapComplete.put("customerId", customerId);
                    mapComplete.put("networkLocation", location.getProvider());
                    mapComplete.put("partnerId", partnerId);
                    mapComplete.put("visibilityType", user.getVisibilitytype());
                    mapComplete.put("dateTimeGPS", ts);


                    if(BkoDataMaganer.getCurrentOffer(context)!=null){
                        schema_id = "1";
                    }  else if(BkoDataMaganer.getOnDemand(context)){
                        schema_id = "2";
                    }

                    if(schema_id.length()!= 0){
                        mapComplete.put("schema_id", schema_id);
                    }


                    if (LatitudCustomer != null) {
                        mapComplete.put("LatitudCustomer", LatitudCustomer);
                        mapComplete.put("LongitudCustomer", LongitudCustomer);
                    }


                    if (tripId.length() > 0)
                        mapComplete.put("tripId", tripId);

                    String accurancyExact = "0";

                    if (location.getAccuracy() < 500.0f) {

                        accurancyExact = "1";
                    }

                    mapComplete.put("accurate", accurancyExact);

                    responseUploadPostion = HttpRequest
                            .get(Constants.GET_UPLOAD_GPS_POSITION(context), mapComplete, true)
                            .connectTimeout(3500).body();

                    sended = true;
                    if (responseUploadPostion != null) {
                        BkoDataMaganer.setLastTimeSendedUserLocation(location.getTime(), context);
                        BkoMainActivity.updateMyActivity(context, "", "gpsSended");
                        Log.d(TAG, "Upload Response: " + responseUploadPostion);
                        if (tripId.length() > 0 && BkoDataMaganer.getCurrentTrip(context) != null) {


                            BkoUtilities.createTxt(oId, ts + "," + location.getLatitude() + "," + location.getLongitude(), BkoDataMaganer.getCurrentTrip(context).getBko_orders_trips_id());

                        }

                        if (file.equals("h")) {
                            BkoDataMaganer.sethideGPSSended(context, false);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                    long timeLocation = location.getTime();
                    long lastTimeSendedLocation = BkoDataMaganer.getLastTimeSendedUserLocation(context);

                    if (lastTimeSendedLocation != 0) {


                        long difference = timeLocation - lastTimeSendedLocation;

                        if (difference > 10000) {
                            if (!BkoUtilities.isActivityRunning(BkoMainActivity.class, context)) {
                                Intent intentone = new Intent().setClass(context, BkoMainActivity.class);
                                intentone.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intentone.putExtra("withOutSended", "1");
                                //startActivity(intentone);
                            }
                            BkoMainActivity.updateMyActivity(context, "" + difference / 1000, "withOutSended");

                        }

                    }

                    if (tripId.length() > 0 && BkoDataMaganer.getCurrentTrip(context) != null) {

                        BkoUtilities.createTxt(oId, ts + "," + location.getLatitude() + "," + location.getLongitude(), BkoDataMaganer.getCurrentTrip(context).getBko_orders_trips_id());

                    }
                }


            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();


        }

        return sended;

    }
}


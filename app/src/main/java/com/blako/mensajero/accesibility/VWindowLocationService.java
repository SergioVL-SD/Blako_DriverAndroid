package com.blako.mensajero.accesibility;

/**
 * Created by franciscotrinidad on 1/28/16.
 */

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Map;


public class VWindowLocationService extends IntentService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    //AccesibilityPresenter presenter;
    private static final String TAG = "LocationService";
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private String source;
    private ArrayList<AccessibilityNodeInfo> nodes;
    public static ArrayList<AccessibilityNodeInfo> lastNodes;
    public static AccessibilityNodeInfo root;

    public VWindowLocationService() {
        super("SimpleWakefulService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    private void startTracking() {
        Log.d(TAG, "startTracking");

        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {

            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            if (!googleApiClient.isConnected() || !googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        } else {
            Log.e(TAG, "unable to connect to google play services.");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        startTracking();
        if (intent != null && intent.getExtras() != null && intent.getAction() != null) {
            Bundle bundle = intent.getExtras();
            source = intent.getExtras().getString("source");
            nodes = lastNodes;
            // presenter = new AccesibilityPresenter();
            Location location = VAccesibilityStatus.getCurrentUserLocation(this);
            if (location == null) {
                location = new Location("");
                location.setLatitude(0);
                location.setLongitude(0);
            }

            sendData(location);

        }
    }


    private void sendData(Location location) {


        if (nodes != null && nodes.size() != 0) {

            if ((VAccesibilityStatus.getVictorStatus(this) == VAccesibilityStatus.BUSY) && source.equals(VAccesibilityStatus.getCurrentAppOnService(this))) {
                //presenter.getLastRequestItem(ROOM_SERVICE, this);
                VRequest lastRequest = VAccesibilityStatus.getLastRequestTimeSended(this);
                if (lastRequest == null || lastRequest.getChildId() == null) {
                    String node = "requestempty/";
                    if (lastRequest != null && lastRequest.getChildId() == null) {
                        node = "childidempty/";
                        if (VAccesibilityStatus.getLastKeyService(this) != null) {
                            lastRequest.setChildId(VAccesibilityStatus.getLastKeyService(this));
                            VAccesibilityStatus.setLastRequestTimeSended(this, lastRequest);
                            return;
                        }
                    }


                    if (VAccesibilityActions.getIsOnScreen(source, nodes, VAppData.FLAG_ONWAY)) {
                        Map<String, Object> map = VAccesibilityActions.getRequestStep(nodes, source, location, VAppData.onWayData.get(source));
                        if (map != null) {
                            //presenter.sendItemWithOutKey(map, node + VDataManager.getIdUser(this) + "/onWay");
                        }
                    } else if (VAccesibilityActions.getIsOnScreen(source, nodes, VAppData.FLAG_ARRIVED)) {
                        Map<String, Object> map = VAccesibilityActions.getRequestStep(nodes, source, location, VAppData.onArrivedData.get(source));
                        if (map != null) {
                            //presenter.sendItemWithOutKey(map, node + VDataManager.getIdUser(this) + "/onArrived");
                        }
                    } else if (VAccesibilityActions.getIsOnScreen(source, nodes, VAppData.FLAG_STARTED)) {
                        Map<String, Object> map = VAccesibilityActions.getRequestStep(nodes, source, location, VAppData.onStartedData.get(source));
                        if (map != null) {
                            //presenter.sendItemWithOutKey(map, node + VDataManager.getIdUser(this) + "/onStarted");
                        }
                    } else if (VAccesibilityActions.getIsOnScreen(source, nodes, VAppData.FLAG_FINISHED)) {
                        Map<String, Object> map = VAccesibilityActions.getRequestStep(nodes, source, location, VAppData.onFinishedData.get(source));
                        if (map != null) {
                            //presenter.sendItemWithOutKey(map, node + VDataManager.getIdUser(this) + "/onFinished");
                        }
                    }
                    return;
                }

                String childId = lastRequest.getChildId();
                if (!VAccesibilityStatus.getLastRequestOnWaySended(this) && VAccesibilityActions.getIsOnScreen(source, nodes, VAppData.FLAG_ONWAY)) {
                    Map<String, Object> map = VAccesibilityActions.getRequestStep(nodes, source, location, VAppData.onWayData.get(source));
                    if (map != null) {
                        VAccesibilityStatus.setLastRequestOnWaySended(this, true);
                        //presenter.sendItemWithOutKey(map, VAppData.ROOM_SERVICE + "/" + childId + "/onWay");

                    }
                } else if (!VAccesibilityStatus.getLastRequestArrivedSended(this) && VAccesibilityActions.getIsOnScreen(source, nodes, VAppData.FLAG_ARRIVED)) {
                    Map<String, Object> map = VAccesibilityActions.getRequestStep(nodes, source, location, VAppData.onArrivedData.get(source));
                    if (map != null) {
                        VAccesibilityStatus.setLastRequestArrivedSended(this, true);
                        //presenter.sendItemWithOutKey(map, VAppData.ROOM_SERVICE + "/" + childId + "/onArrived");
                    }
                } else if (!VAccesibilityStatus.getLastRequestStartedSended(this) && VAccesibilityActions.getIsOnScreen(source, nodes, VAppData.FLAG_STARTED)) {
                    Map<String, Object> map = VAccesibilityActions.getRequestStep(nodes, source, location, VAppData.onStartedData.get(source));
                    if (map != null) {
                        VAccesibilityStatus.setLastRequestStartedSended(this, true);
                        //presenter.sendItemWithOutKey(map, VAppData.ROOM_SERVICE + "/" + childId + "/onStarted");
                    }
                } else if (!VAccesibilityStatus.getLastRequestFinishedSended(this) && VAccesibilityActions.getIsOnScreen(source, nodes, VAppData.FLAG_FINISHED)) {
                    Map<String, Object> map = VAccesibilityActions.getRequestStep(nodes, source, location, VAppData.onFinishedData.get(source));
                    if (map != null) {
                        VAccesibilityStatus.setLastRequestFinishedSended(this, true);
                        //presenter.sendItemWithOutKey(map, VAppData.ROOM_SERVICE + "/" + childId + "/onFinished");
                    }
                }

            } else {
                if (VAccesibilityActions.getIsOnScreen(source, nodes, VAppData.FLAG_MAIN)) {
                    //Map<String, Object> connectionMap = VAccesibilityActions.getIsConnected(this, nodes, source, location, root);
                    //if (connectionMap != null) {
                      //  if (!isValid())
                        //    return;
                        //presenter.sendItem(connectionMap, VAppData.ROOM_CONNECTIONS);
                      //  VAccesibilityStatus.setLastTimeSended(this, System.currentTimeMillis());
                    //}
                } else {

                    if (VAccesibilityActions.getIsOnScreen(source, nodes, VAppData.FLAG_ONWAY)) {
                        Map<String, Object> map = VAccesibilityActions.getRequestStep(nodes, source, location, VAppData.onWayData.get(source));
                        if (map != null) {
                            //  presenter.sendItemWithOutKey(map, "others/" + VDataManager.getIdUser(this) + "/onWay");
                        }
                    } else if (VAccesibilityActions.getIsOnScreen(source, nodes, VAppData.FLAG_ARRIVED)) {
                        Map<String, Object> map = VAccesibilityActions.getRequestStep(nodes, source, location, VAppData.onArrivedData.get(source));
                        if (map != null) {

                            //presenter.sendItemWithOutKey(map, "others/" + VDataManager.getIdUser(this) + "/onArrived");
                        }
                    } else if (VAccesibilityActions.getIsOnScreen(source, nodes, VAppData.FLAG_STARTED)) {
                        Map<String, Object> map = VAccesibilityActions.getRequestStep(nodes, source, location, VAppData.onStartedData.get(source));
                        if (map != null) {
                            //presenter.sendItemWithOutKey(map, "others/" + VDataManager.getIdUser(this) + "/onStarted");
                        }
                    } else if (VAccesibilityActions.getIsOnScreen(source, nodes, VAppData.FLAG_FINISHED)) {
                        Map<String, Object> map = VAccesibilityActions.getRequestStep(nodes, source, location, VAppData.onFinishedData.get(source));
                        if (map != null) {
                            //presenter.sendItemWithOutKey(map, "others/" + VDataManager.getIdUser(this) + "/onFinished");
                        }
                    }
                }
            }


        }
    }


    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            stopLocationUpdates();
            VAccesibilityStatus.setCurrentUserLocation(location, this);
            try {

            } catch (Exception e) {

            }


            stopSelf();
        }
    }


    private boolean isValid() {
        long lasTime = VAccesibilityStatus.getLastTimeSended();

        long difference = System.currentTimeMillis() - lasTime;
        if ((System.currentTimeMillis() - lasTime) < 500) {
            return false;
        }
        Log.e("time ", "" + difference);
        return true;
    }

    private void stopLocationUpdates() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(12000); // milliseconds
        locationRequest.setFastestInterval(12000); // the fastest rate in milliseconds at which your app can handle location updates
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (googleApiClient.isConnected()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;

            }

            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        googleApiClient, locationRequest, this);
            } catch (Exception e) {

            }

        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed");
        stopLocationUpdates();
        stopSelf();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "GoogleApiClient connection has been suspend");
    }
}

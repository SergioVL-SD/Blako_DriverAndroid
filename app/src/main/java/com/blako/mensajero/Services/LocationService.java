package com.blako.mensajero.Services;

/**
 * Created by franciscotrinidad on 1/28/16.
 */

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GnssStatus;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.blako.mensajero.App;
import com.blako.mensajero.BkoDataMaganer;
import com.blako.mensajero.Constants;
import com.blako.mensajero.Dao.BkoUserDao;
import com.blako.mensajero.R;
import com.blako.mensajero.Services.location.FusedLocationService;
import com.blako.mensajero.Services.location.OnLocationServiceListener;
import com.blako.mensajero.Utils.BkoUtilities;
import com.blako.mensajero.VO.BkoUser;
import com.blako.mensajero.Views.BkoMainActivity;
import com.blako.mensajero.firebase.BkoFirebaseDatabase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class LocationService extends Service implements GpsStatus.Listener {

    private FusedLocationService fusedLocationService;
    private LocationManager locationManager;

    private static final String TAG = "LocationService";
    private static final int DEAD_NOTIFICATION_ID = 78634;
    private static final long DEAD_TIME = 1000 * 60 * 10; //--> Minutes
    private int status=0;
    private int checkEvery= 10;
    private boolean gpsDead= false;

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationService = App.getInstance().getLocationService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        generalSetup();
        registerGpsStatusListener();
        return START_STICKY;
    }

    private void generalSetup(){

        fusedLocationService.requestLocationUpdates();
        fusedLocationService.setOnLocationServiceListener(new OnLocationServiceListener() {
            @Override
            public void onLocationUpdate(Location location) {

                if (location != null) {

                    Log.d("ActualLocation",String.format("%s, %s",location.getLatitude(),location.getLongitude()));
                    BkoDataMaganer.setLastLocationTimeStamp(LocationService.this,System.currentTimeMillis());
                    BkoDataMaganer.setDeadNotification(LocationService.this,true);

                    try {
                        final BkoUser user = BkoUserDao.Consultar(LocationService.this);
                        if ((user == null || !user.isAvailable()) || BkoDataMaganer.getCurrentVehicle(LocationService.this) == null){
                            shutdown();
                        }
                    } catch (Exception e) {
                        e.fillInStackTrace();
                    }

                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    dateFormat.setTimeZone(TimeZone.getDefault());
                    BkoDataMaganer.setCurrentUserLocation(location, LocationService.this);

                    Intent localIntent= new Intent(Constants.ACTION_SERVICE_LOCATION);
                    LocalBroadcastManager.getInstance(LocationService.this).sendBroadcast(localIntent);
                }
            }
        });
    }

    public void shutdown(){
        if(fusedLocationService != null) {
            fusedLocationService.removeLocationUpdates();
        }
        if (locationManager!=null){
            locationManager.removeGpsStatusListener(this);
        }
        stopSelf();
    }

    @Override
    public void onDestroy() {
        shutdown();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void registerGpsStatusListener() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.addGpsStatusListener(this);
        }catch (SecurityException e){
            e.fillInStackTrace();
        }
    }

    @Override
    public void onGpsStatusChanged(int event) {
        Log.e(TAG, "(gps) onGpsStatusChanged " + String.valueOf((LocationService.this).hashCode()));
        switch (event) {
            case GpsStatus.GPS_EVENT_STARTED:
                Log.e(TAG, "onGpsStatusChanged started");
                break;

            case GpsStatus.GPS_EVENT_STOPPED:
                Log.e(TAG, "onGpsStatusChanged stopped");
                break;

            case GpsStatus.GPS_EVENT_FIRST_FIX:
                Log.e(TAG, "onGpsStatusChanged first fix");
                gpsDead= false;
                break;

            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                Log.e(TAG, "onGpsStatusChanged status");
                status++;
                if (BkoDataMaganer.getLastLocationTimeStamp(this)!=0 && checkEvery==status && (BkoDataMaganer.getOnDemand(this) || BkoDataMaganer.getCurrentOffer(this)!=null)){
                    status= 0;
                    gpsDead= (Math.abs(System.currentTimeMillis()-BkoDataMaganer.getLastLocationTimeStamp(this))>DEAD_TIME);
                    if (gpsDead){
                        Log.d("GPS_DEAD_Status","Con Problemas");
                        if (BkoDataMaganer.getDeadNotification(this)){
                            sendNotification(getString(R.string.notification_dead),true,null);
                            BkoDataMaganer.setDeadNotification(this,false);
                        }
                    }else {
                        Log.d("GPS_DEAD_Status","Todo Bien");
                    }
                }
                break;
        }
    }

    private void sendNotification(String message, boolean sound, String title) {

        Intent notificationIntent = new Intent(getApplicationContext(), BkoMainActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        String titleNotification = "Blako";

        if (title != null)
            titleNotification = title;

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        DEAD_NOTIFICATION_ID,
                        notificationIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.motorcycle)
                .setContentTitle(titleNotification)
                .setContentText(message)
                .setContentIntent(resultPendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true);
        if (sound) {
            defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.dead);

        }

        notificationBuilder.setSound(defaultSoundUri);
        notificationBuilder.setLights(0xff152949, 1000, 2000);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = notificationBuilder.build();
        notification.flags |= Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(DEAD_NOTIFICATION_ID, notification);


    }
}

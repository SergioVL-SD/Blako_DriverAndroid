package com.blako.mensajero.Services;

/**
 * Created by franciscotrinidad on 1/28/16.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.blako.mensajero.App;
import com.blako.mensajero.BkoDataMaganer;
import com.blako.mensajero.Constants;
import com.blako.mensajero.Dao.BkoUserDao;
import com.blako.mensajero.R;
import com.blako.mensajero.Services.location.FusedLocationService;
import com.blako.mensajero.Services.location.OnLocationServiceListener;
import com.blako.mensajero.Services.mqtt.MqttService;
import com.blako.mensajero.Utils.LocationUtils;
import com.blako.mensajero.Utils.LogUtils;
import com.blako.mensajero.VO.BkoUser;
import com.blako.mensajero.Views.BkoMainActivity;
import com.blako.mensajero.models.PingResponse;
import com.blako.mensajero.models.RemoteCommand;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;


public class LocationService extends Service implements GpsStatus.Listener {

    private FusedLocationService fusedLocationService;
    private LocationManager locationManager;
    private MqttService mqttService;

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
        mqttService= App.getInstance().getMqttService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        generalSetup();
        registerGpsStatusListener();
        //doMqttSetup();
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
        /*if (mqttService!=null){
            doMqttDestroy();
        }*/
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

    public final static String mqttTopic1 = "hello";
    public final static String mqttTopic2 = "colonia";

    public void doMqttSetup() {
        mqttService.setOnMqttConnectionListener(new MqttService.OnMqttConnectionListener() {
            @Override
            public void onConnectComplete(boolean complete) {

            }

            @Override
            public void onConnectionLost() {

            }

            @Override
            public void postConnectionSuccess() {
                mqttService.subscribeToTopic(mqttTopic1);
                mqttService.subscribeToTopic(mqttTopic2);
            }

            @Override
            public void postConnectionFailure() {

            }
        });

        mqttService.setOnMqttMessageListener(new MqttService.OnMqttMessageListener() {
            @Override
            public void onMessageArrived(String topic, String payload) {
                LogUtils.debug("MQTT", "(mqtt) topic: " + topic + " message: " + payload);

                Gson gson = new Gson();
                RemoteCommand remoteCommand = gson.fromJson(payload, RemoteCommand.class);

                LogUtils.debug("MQTT", "(mqtt) remote command: " + remoteCommand.getCommand());

                switch(remoteCommand.getCommand())
                {
                    case "ping":
                        PingResponse pingResponse = new PingResponse();
                        pingResponse.setTopic("PingResponse");
                        pingResponse.setVersion(Constants.VERSION_NAME);
                        pingResponse.setTimestamp(System.currentTimeMillis());
                        pingResponse.setUid(BkoDataMaganer.getWorkerId(LocationService.this));
                        pingResponse.setConnected(BkoDataMaganer.getOnDemand(LocationService.this));
                        if(BkoDataMaganer.getCurrentUserLocation(LocationService.this) != null) {
                            pingResponse.setGeohash(LocationUtils.getGeoHash(BkoDataMaganer.getCurrentUserLocation(LocationService.this), 9));
                        } else {
                            pingResponse.setGeohash("s0000");
                        }

                        if(mqttService != null) {
                            String outbound = gson.toJson(pingResponse);
                            mqttService.publishToTopic(mqttTopic1, outbound, 0);
                        }
                        break;

                    case "monitor":
                        if(remoteCommand.getPar0() != null && remoteCommand.getPar1() != null) {
                            Integer par0 = Integer.valueOf(remoteCommand.getPar0());
                            if(par0 != null && remoteCommand.getPar1().contentEquals(BkoDataMaganer.getWorkerId(LocationService.this))) {
                                LogUtils.debug("MQTT", "(monitor) monitor configured for " + String.valueOf(par0) + " occurrences");
                            } else {
                                LogUtils.error("MQTT", "(monitor) error parsing remote command, wrong uid?");
                            }

                        } else {
                            LogUtils.error("MQTT", "(monitor) error parsing remote command, missing parameter?");
                        }

                        PingResponse monitorCommandResponse = new PingResponse();
                        monitorCommandResponse.setTopic("MonitorMode");
                        monitorCommandResponse.setVersion(Constants.VERSION_NAME);
                        monitorCommandResponse.setTimestamp(System.currentTimeMillis());
                        monitorCommandResponse.setUid(BkoDataMaganer.getWorkerId(LocationService.this));
                        if(BkoDataMaganer.getCurrentUserLocation(LocationService.this) != null) {
                            monitorCommandResponse.setGeohash(LocationUtils.getGeoHash(BkoDataMaganer.getCurrentUserLocation(LocationService.this), 9));
                        } else {
                            monitorCommandResponse.setGeohash("s0000");
                        }

                        if(mqttService != null) {
                            String outbound = gson.toJson(monitorCommandResponse);
                            mqttService.publishToTopic(mqttTopic1, outbound, 0);
                        }
                        break;
                }
            }

            @Override
            public void onDeliveryComplete(String mid) {

            }
        });

        mqttService.setOnMqttSubscriptionsListener(new MqttService.OnMqttSubscriptionsListener() {
            @Override
            public void onSubscribeSuccess(String topic) {

            }

            @Override
            public void onSubscribeFailure(String topic) {

            }

            @Override
            public void onUnsubscribeSuccess(String topic) {

            }

            @Override
            public void onUnsubscribeFailure(String topic) {

            }
        });

        mqttService.connect();
    }

    public void doMqttDestroy()
    {
        mqttService.unsubscribeFromTopic(mqttTopic1);
        mqttService.unsubscribeFromTopic(mqttTopic2);
    }
}

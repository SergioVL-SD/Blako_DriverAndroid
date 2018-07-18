package com.blako.mensajero.Services;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.blako.mensajero.BkoDataMaganer;
import com.blako.mensajero.Dao.BkoUserDao;
import com.blako.mensajero.VO.BkoUser;

import java.util.Calendar;

/**
 * Created by franciscotrinidad on 12/21/15.
 */
public class BkoGpsTrackerAlarmReceiver extends WakefulBroadcastReceiver {
    private static final String TAG = "GpsTrackerAlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "BkoGpsTrackerAlarmReceiver BROADCAST");
        Log.d("ReceivedABroadcast", "Received A Broadcast");
        if (intent != null && intent.getAction() != null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.d("ReceivedBootCompleted", "Received BOOT_COMPLETED broadcast");
        } else if (intent != null && intent.getAction() != null) {
            Log.d("***OTHER INTENNT ALARM", intent.getAction());
        }
        try {
            BkoUser user = BkoUserDao.Consultar(context);
            if (((user == null || !user.isAvailable()) && BkoDataMaganer.getCurrentVehicle(context) == null ||BkoDataMaganer.getCurrentOffer(context)==null)  && !BkoDataMaganer.getOnDemand(context) ){

                Log.d("GpsTrackerAlarmReceiver", "USER NULL OR NOT AVAILABLE");
                return;
            }

            setAlarm(context);

            if (!isMyServiceRunning(LocationService.class, context)) {
                Intent service = new Intent(context, LocationService.class);
                context.startService(service);
                Log.d("*********START SERVICE ", "LOCATION***");
            } else {
                Log.d("*******RUNNING SERVICE ", "LOCATION***");
            }

            Intent serviceIntent = new Intent(context, BkoSendLocationToServer.class);
            context.startService(serviceIntent);
        } catch (Exception e) {
            setAlarm(context);
            e.printStackTrace();
            Log.d("GpsTrackerAlarmReceiver", "EXCEPTION");
        }

    }

    private void setAlarm(Context context) {
        Log.d(TAG, "CREATING ALARM");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent gpsTrackerIntent = new Intent(context, BkoGpsTrackerAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, gpsTrackerIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 12
        );
        Log.d("Calendar miliseconds", " " + cal.getTimeInMillis());


        Log.d(TAG, "CREATING A" +
                "" +
                "" +
                "" +
                "LARM SET EXACT");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            alarmManager.set(AlarmManager.RTC_WAKEUP,
                    cal.getTimeInMillis(), pendingIntent);
            Log.d(TAG, "call alarmManager.set() ");


        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                    cal.getTimeInMillis(), pendingIntent);
            Log.d(TAG, "call alarmManager.setExact() ");

        }
    }


    private boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}

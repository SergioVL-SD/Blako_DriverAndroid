package com.blako.mensajero.Services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.blako.mensajero.BkoDataMaganer;
import com.blako.mensajero.Dao.BkoUserDao;
import com.blako.mensajero.VO.BkoUser;

import java.util.Calendar;

/**
 * Created by franciscotrinidad on 2/16/16.
 */
public class BkoHeartBeatPushAliveReceiver extends BroadcastReceiver {
    private static final String TAG = "HeartBeatPusheceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "HEARTBEAT PUSH BROADCAST");

        try {
            BkoUser user = BkoUserDao.Consultar(context);
            if (((user == null || !user.isAvailable()) && BkoDataMaganer.getCurrentVehicle(context) == null ||BkoDataMaganer.getCurrentOffer(context)==null)  && !BkoDataMaganer.getOnDemand(context) ){

                Log.d("GpsTrackerAlarmReceiver", "USER NULL OR NOT AVAILABLE");
                return;
            }
            setAlarm(context);
            context.sendBroadcast(new Intent("com.google.android.intent.action.GTALK_HEARTBEAT"));
            context.sendBroadcast(new Intent("com.google.android.intent.action.MCS_HEARTBEAT"));
        } catch (Exception e) {

            e.printStackTrace();
            Log.d("HEARTBEAT PUSH", "EXCEPTION");

            setAlarm(context);
        }

    }


    private void setAlarm(Context context) {
        Log.d(TAG, "CREATING ALARM PUSH");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent gpsTrackerIntent = new Intent(context, BkoHeartBeatPushAliveReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 9, gpsTrackerIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE,
                10);
        Log.d("Calendar seconds", " " + cal.getTimeInMillis());


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            alarmManager.set(AlarmManager.RTC_WAKEUP,
                    cal.getTimeInMillis(), pendingIntent);
            Log.d(TAG, "call alarmManager.set() PUSH");


        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                    cal.getTimeInMillis(), pendingIntent);
            Log.d(TAG, "call alarmManager.setExact() PUSH");

        }


    }


}
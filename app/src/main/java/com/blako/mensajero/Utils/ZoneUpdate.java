package com.blako.mensajero.Utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ZoneUpdate {

    public static Long delay(int hourOfDay, int minuteOfDay){
        Long actualTime= System.currentTimeMillis();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(actualTime);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minuteOfDay);

        return Math.abs(calendar.getTimeInMillis()-actualTime);
    }

    public static Long delay(long syncTime){
        Calendar calendar= Calendar.getInstance();
        calendar.setTimeInMillis(syncTime*1000);
        Log.d("Kml_Sync_Hour",new SimpleDateFormat("hh:mm:ss", Locale.getDefault()).format(calendar.getTimeInMillis()));
        Long actualTime= System.currentTimeMillis();
        return Math.abs((syncTime*1000)-actualTime);
    }
}

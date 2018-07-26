package com.blako.mensajero.Utils;

import android.util.Log;

import java.util.Calendar;

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
        Long actualTime= System.currentTimeMillis();
        return Math.abs(syncTime-actualTime);
    }
}

package com.blako.mensajero.Utils;

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
}

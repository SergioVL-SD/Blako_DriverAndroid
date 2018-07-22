package com.blako.mensajero.Utils;

import android.location.Location;

import ch.hsr.geohash.GeoHash;

public class LocationUtils {
    public static double distance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }

    public static String getGeoHash(Location location)
    {
       return GeoHash.withCharacterPrecision(location.getLatitude(), location.getLongitude(), 7).toBase32();
    }

    public static String getGeoHash(Location location, int precision)
    {
        return GeoHash.withCharacterPrecision(location.getLatitude(), location.getLongitude(), precision).toBase32();
    }
}

package com.blako.mensajero.DB.models;

import android.content.ContentValues;
import android.database.Cursor;
import com.blako.mensajero.DB.DbContracts.PointEntry;

public class HubPoints {

    private String id;
    private String hubId;
    private Double lat;
    private Double lon;

    public HubPoints(String hubId, Double lat, Double lon) {
        this.hubId = hubId;
        this.lat = lat;
        this.lon = lon;
    }

    public HubPoints(Cursor cursor) {
        this.id = cursor.getString(cursor.getColumnIndexOrThrow(PointEntry._ID));
        this.hubId = cursor.getString(cursor.getColumnIndexOrThrow(PointEntry.HUB_ID));
        this.lat = cursor.getDouble(cursor.getColumnIndexOrThrow(PointEntry.LAT));
        this.lon = cursor.getDouble(cursor.getColumnIndexOrThrow(PointEntry.LON));
    }

    public ContentValues toContentValues(){
        ContentValues values= new ContentValues();
        values.put(PointEntry.HUB_ID,hubId);
        values.put(PointEntry.LAT,lat);
        values.put(PointEntry.LON,lon);
        return values;
    }

    public static String getTable(){
        return "CREATE TABLE "+PointEntry.TABLE_NAME+" ("+PointEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +PointEntry.HUB_ID+" TEXT,"
                +PointEntry.LAT+" REAL,"
                +PointEntry.LON+" REAL)";
    }

    public String getId() {
        return id;
    }

    public String getHubId() {
        return hubId;
    }

    public void setHubId(String hubId) {
        this.hubId = hubId;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }
}

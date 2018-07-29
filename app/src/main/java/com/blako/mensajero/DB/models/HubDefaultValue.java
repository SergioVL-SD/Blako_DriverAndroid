package com.blako.mensajero.DB.models;

import android.content.ContentValues;
import android.database.Cursor;
import com.blako.mensajero.DB.DbContracts.DefaultValueEntry;

public class HubDefaultValue {

    private String id;
    private String hubId;
    private Integer value;
    private Double rate;
    private Integer regionId;

    public HubDefaultValue(String hubId, Integer value, Double rate, Integer regionId) {
        this.hubId = hubId;
        this.value = value;
        this.rate = rate;
        this.regionId = regionId;
    }

    public HubDefaultValue(Cursor cursor) {
        this.id = cursor.getString(cursor.getColumnIndexOrThrow(DefaultValueEntry._ID));
        this.hubId = cursor.getString(cursor.getColumnIndexOrThrow(DefaultValueEntry.HUB_ID));
        this.value = cursor.getInt(cursor.getColumnIndexOrThrow(DefaultValueEntry.VALUE));
        this.rate = cursor.getDouble(cursor.getColumnIndexOrThrow(DefaultValueEntry.RATE));
        this.regionId = cursor.getInt(cursor.getColumnIndexOrThrow(DefaultValueEntry.REGION_ID));
    }

    public ContentValues toContentValues(){
        ContentValues values= new ContentValues();
        values.put(DefaultValueEntry.HUB_ID,hubId);
        values.put(DefaultValueEntry.VALUE,value);
        values.put(DefaultValueEntry.RATE,rate);
        values.put(DefaultValueEntry.REGION_ID,regionId);
        return values;
    }

    public static String getTable(){
        return "CREATE TABLE "+DefaultValueEntry.TABLE_NAME+" ("+DefaultValueEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +DefaultValueEntry.HUB_ID+" TEXT,"
                +DefaultValueEntry.VALUE+" INTEGER,"
                +DefaultValueEntry.RATE+" REAL,"
                +DefaultValueEntry.REGION_ID+" INTEGER)";
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

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Integer getRegionId() {
        return regionId;
    }

    public void setRegionId(Integer regionId) {
        this.regionId = regionId;
    }
}

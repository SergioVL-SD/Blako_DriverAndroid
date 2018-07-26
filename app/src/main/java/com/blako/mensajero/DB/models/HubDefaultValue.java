package com.blako.mensajero.DB.models;

import android.content.ContentValues;
import android.database.Cursor;
import com.blako.mensajero.DB.DbContracts.DefaultValueEntry;

public class HubDefaultValue {

    private String id;
    private String hubId;
    private Integer value;
    private Double rate;

    public HubDefaultValue(String hubId, Integer value, Double rate) {
        this.hubId = hubId;
        this.value = value;
        this.rate = rate;
    }

    public HubDefaultValue(Cursor cursor) {
        this.id = cursor.getString(cursor.getColumnIndexOrThrow(DefaultValueEntry._ID));
        this.hubId = cursor.getString(cursor.getColumnIndexOrThrow(DefaultValueEntry.HUB_ID));
        this.value = cursor.getInt(cursor.getColumnIndexOrThrow(DefaultValueEntry.VALUE));
        this.rate = cursor.getDouble(cursor.getColumnIndexOrThrow(DefaultValueEntry.RATE));
    }

    public ContentValues toContentValues(){
        ContentValues values= new ContentValues();
        values.put(DefaultValueEntry.HUB_ID,hubId);
        values.put(DefaultValueEntry.VALUE,value);
        values.put(DefaultValueEntry.RATE,rate);
        return values;
    }

    public static String getTable(){
        return "CREATE TABLE "+DefaultValueEntry.TABLE_NAME+" ("+DefaultValueEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +DefaultValueEntry.HUB_ID+" TEXT,"
                +DefaultValueEntry.VALUE+" INTEGER,"
                +DefaultValueEntry.RATE+" REAL)";
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
}

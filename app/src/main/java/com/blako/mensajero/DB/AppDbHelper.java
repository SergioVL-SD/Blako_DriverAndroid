package com.blako.mensajero.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.blako.mensajero.DB.models.Hub;
import com.blako.mensajero.DB.models.HubDefaultValue;
import com.blako.mensajero.DB.models.HubPoints;
import com.blako.mensajero.DB.DbContracts.HubEntry;
import com.blako.mensajero.DB.DbContracts.PointEntry;
import com.blako.mensajero.DB.DbContracts.DefaultValueEntry;

import java.util.ArrayList;

public class AppDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME= "db_blako";
    private static final int DATABASE_VERSION= 1;

    public AppDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Hub.getTable());
        db.execSQL(HubPoints.getTable());
        db.execSQL(HubDefaultValue.getTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + HubEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PointEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DefaultValueEntry.TABLE_NAME);
        onCreate(db);
    }


    /*Hubs*/
    public long saveHub(Hub hub){
        return getWritableDatabase().insert(HubEntry.TABLE_NAME, null, hub.toContentValues());
    }

    public ArrayList<Hub> getAllHubs(){
        ArrayList<Hub> hubs= new ArrayList<>();
        Cursor cursor= getReadableDatabase().query(HubEntry.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            hubs.add(new Hub(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return hubs;
    }

    public Hub getHubById(String id){
        Cursor cursor= getReadableDatabase().query(HubEntry.TABLE_NAME, null, HubEntry._ID+" LIKE ?", new String[] {id}, null, null, null);
        if (cursor.moveToFirst()){
            Hub hub= new Hub(cursor);
            cursor.close();
            return hub;
        }
        return null;
    }

    public Hub getHubByHubId(String hubId){
        Cursor cursor= getReadableDatabase().query(HubEntry.TABLE_NAME, null, HubEntry.HUB_ID+" LIKE ?", new String[] {hubId}, null, null, null);
        if (cursor.moveToFirst()){
            Hub hub= new Hub(cursor);
            cursor.close();
            return hub;
        }
        return null;
    }

    public long updateHub(Hub hub, String id){
        return getWritableDatabase().update(HubEntry.TABLE_NAME, hub.toContentValues(), HubEntry._ID+" LIKE ?", new String[]{id});
    }

    public long deleteHub(String id){
        return getWritableDatabase().delete(HubEntry.TABLE_NAME, HubEntry._ID+" LIKE ?", new String[]{id});
    }

    public long deleteAllHubs(){
        return getWritableDatabase().delete(HubEntry.TABLE_NAME, null, null);
    }

    /*Points*/
    public long savePoint(HubPoints points){
        return getWritableDatabase().insert(PointEntry.TABLE_NAME, null, points.toContentValues());
    }

    public ArrayList<HubPoints> getAllPointsByHubId(String hubId){
        ArrayList<HubPoints> points= new ArrayList<>();
        Cursor cursor= getReadableDatabase().query(PointEntry.TABLE_NAME, null, PointEntry.HUB_ID+" LIKE ?", new String[] {hubId}, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            points.add(new HubPoints(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return points;
    }

    public long deleteAllPointsByHubId(String hubId){
        return getWritableDatabase().delete(HubEntry.TABLE_NAME, HubEntry.HUB_ID+" LIKE ?", new String[]{hubId});
    }

    public long deleteAllPoints(){
        return getWritableDatabase().delete(PointEntry.TABLE_NAME, null, null);
    }

    /*HubDefaultValues*/
    public long saveHubDefaultValue(HubDefaultValue hubDefaultValue){
        return getWritableDatabase().insert(DefaultValueEntry.TABLE_NAME, null, hubDefaultValue.toContentValues());
    }

    public ArrayList<HubDefaultValue> getAllHubDefaultValues(){
        ArrayList<HubDefaultValue> hubDefaultValues= new ArrayList<>();
        Cursor cursor= getReadableDatabase().query(DefaultValueEntry.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            hubDefaultValues.add(new HubDefaultValue(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return hubDefaultValues;
    }

    public HubDefaultValue getHubDefaultValueById(String id){
        Cursor cursor= getReadableDatabase().query(DefaultValueEntry.TABLE_NAME, null, DefaultValueEntry._ID+" LIKE ?", new String[] {id}, null, null, null);
        if (cursor.moveToFirst()){
            HubDefaultValue hubDefaultValue= new HubDefaultValue(cursor);
            cursor.close();
            return hubDefaultValue;
        }
        return null;
    }

    public HubDefaultValue getHubDefaultValueByHubId(String hubId){
        Cursor cursor= getReadableDatabase().query(DefaultValueEntry.TABLE_NAME, null, DefaultValueEntry.HUB_ID+" LIKE ?", new String[] {hubId}, null, null, null);
        if (cursor.moveToFirst()){
            HubDefaultValue hubDefaultValue= new HubDefaultValue(cursor);
            cursor.close();
            return hubDefaultValue;
        }
        return null;
    }

    public long updateHubDefaultValue(HubDefaultValue hubDefaultValue, String id){
        return getWritableDatabase().update(DefaultValueEntry.TABLE_NAME, hubDefaultValue.toContentValues(), DefaultValueEntry._ID+" LIKE ?", new String[]{id});
    }

    public long deleteHubDefaultValue(String id){
        return getWritableDatabase().delete(DefaultValueEntry.TABLE_NAME, DefaultValueEntry._ID+" LIKE ?", new String[]{id});
    }

    public long deleteAllHubDefaultValues(){
        return getWritableDatabase().delete(DefaultValueEntry.TABLE_NAME, null, null);
    }
}

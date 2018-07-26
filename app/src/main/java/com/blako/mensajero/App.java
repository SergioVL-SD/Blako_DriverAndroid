package com.blako.mensajero;

import android.app.Application;

import com.blako.mensajero.DB.AppDbHelper;
import com.blako.mensajero.Services.location.FusedLocationService;
import com.blako.mensajero.Utils.AppPreferences;
import com.blako.mensajero.Utils.LogUtils;

public class App extends Application {

    final String LOG_SOURCE = "App";

    private static App instance;

    private AppPreferences preferences;
    private AppDbHelper dbHelper;
    private FusedLocationService fusedLocationService;

    @Override
    public void onCreate() {
        super.onCreate();

        LogUtils.debug(LOG_SOURCE, "onCreate()");
        instance= this;
    }

    public static App getInstance(){
        return instance;
    }


    public AppPreferences getPreferences(){
        if (preferences == null){
            preferences= new AppPreferences(this);
        }
        return preferences;
    }

    public AppDbHelper getDbHelper(){
        if (dbHelper == null){
            dbHelper= new AppDbHelper(this);
        }
        return dbHelper;
    }

    public FusedLocationService getLocationService() {
        LogUtils.debug(LOG_SOURCE, "fetching location service...");

        if (fusedLocationService == null) {
            fusedLocationService = new FusedLocationService(this);
        }

        return fusedLocationService;
    }
}

package com.blako.mensajero;

import android.app.Application;

import com.blako.mensajero.Services.location.FusedLocationService;
import com.blako.mensajero.Utils.LogUtils;

public class App extends Application {

    final String LOG_SOURCE = "App";

    private static App instance;

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


    public FusedLocationService getLocationService() {
        LogUtils.debug(LOG_SOURCE, "fetching location service...");

        if (fusedLocationService == null) {
            fusedLocationService = new FusedLocationService(this);
        }

        return fusedLocationService;
    }
}

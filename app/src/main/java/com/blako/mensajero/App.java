package com.blako.mensajero;

import android.app.Application;

import com.blako.mensajero.Services.location.FusedLocationService;
import com.blako.mensajero.Utils.LogUtils;
import com.blako.mensajero.repositories.Repository;
import com.google.firebase.messaging.FirebaseMessaging;

public class App extends Application {

    final String LOG_SOURCE = "App";

    private static App instance;

    private FusedLocationService fusedLocationService;

    @Override
    public void onCreate() {
        super.onCreate();

        LogUtils.debug(LOG_SOURCE, "onCreate()");
        instance= this;

        //  TODO: place elsewhere
        FirebaseMessaging.getInstance().subscribeToTopic("remote");
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









    private Repository repository;
    public Repository getRepository()
    {
        LogUtils.debug(LOG_SOURCE, "fetching repository...");

        if (repository == null) {
            repository = Repository.create(this);
            repository.setUp();
        }

        return repository;
    }
}

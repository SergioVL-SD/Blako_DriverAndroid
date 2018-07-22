package com.blako.mensajero.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.location.Location;
import android.os.Handler;

import com.blako.mensajero.App;
import com.blako.mensajero.Services.api.ZonesServiceImplementation;
import com.blako.mensajero.Services.location.FusedLocationService;
import com.blako.mensajero.Utils.DateTimeUtils;
import com.blako.mensajero.Utils.LocationUtils;
import com.blako.mensajero.Utils.LogUtils;
import com.blako.mensajero.models.Fence;
import com.blako.mensajero.models.inbound.ZonesResponse;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {
    private static final String LOG_SOURCE = Repository.class.getName();

    //  dependencies
    FusedLocationService fusedLocationService = App.getInstance().getLocationService();

    public void setUp()
    {
        LogUtils.debug(LOG_SOURCE, "setUp()");


    }

    //  TODO: check if singleton!!!
    public static Repository create(Context context)
    {
        final String LOG_SOURCE = "Repository";
        LogUtils.debug(LOG_SOURCE, "create()");

        FirebaseMessaging.getInstance().subscribeToTopic("highScores");
        FirebaseMessaging.getInstance().subscribeToTopic("use_cases");
        FirebaseMessaging.getInstance().subscribeToTopic("config");
        FirebaseMessaging.getInstance().subscribeToTopic("remote");

        Repository repository = new Repository();//restFulService);

        return repository;
    }

    //  ------------------------------------------------------------------------------------





    private MutableLiveData<List<Fence>> fences;
    public LiveData<List<Fence>> getFences()
    {
        if(fences == null)
        {
            fences = new MutableLiveData<>();
            fences.setValue(new ArrayList<Fence>());
        }
        return fences;
    }

    private MutableLiveData<Location> currentLocation;
    public LiveData<Location> getCurrentLocation()
    {
        if(currentLocation == null)
        {
            currentLocation = new MutableLiveData<>();
        }
        return currentLocation;
    }
    public void postCurrentLocation(Location location)
    {
        getCurrentLocation();
        currentLocation.postValue(location);
    }







    //  ------------------------------------------------------------------------------------
    //                                      API CALLS

    //  TODO: implement support to starting a a new delayed fetch before the previous one is finished
    //  TODO: move to a tidy up service :)
    final Handler timeHandler = new Handler();
    final Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            LogUtils.debug(LOG_SOURCE, "(fetch) fetching zones...");
            fetchZones();
        }
    };
    public void fetchZonesDelayed(int jitter)
    {
        int delayValue = ThreadLocalRandom.current().nextInt(0, jitter + 1);
        LogUtils.debug(LOG_SOURCE, "(fetch) delayed zones fetch for " + String.valueOf(delayValue) + "s");

        timeHandler.postDelayed(timeRunnable, delayValue*1000);
    }

    public void fetchZones()
    {
        String wid = "nn";
        String geohash = "d0000";

        Location location = getCurrentLocation().getValue();
        if(location != null)
            geohash = LocationUtils.getGeoHash(location, 9);

        LogUtils.debug(LOG_SOURCE, "(fetch) fetching zones for wid: " + wid + " and geohash: " + geohash + " ...");

        final long rtt_stamp = DateTimeUtils.getFullMillis();

        Call<ZonesResponse> call = ZonesServiceImplementation.create().zones("/zones/" + wid + "/" + geohash);
        call.enqueue(new Callback<ZonesResponse>() {
            @Override
            public void onResponse(Call<ZonesResponse> call, Response<ZonesResponse> response) {
//                long new_rtt_stamp = DateTimeUtils.getFullMillis();
//                addRTT(new_rtt_stamp - rtt_stamp);

                if(response.code() == 200)
                {
                    ZonesResponse zonesResponse = response.body();

                    if(zonesResponse.getSuccess())
                    {
                        LogUtils.debug(LOG_SOURCE, "(fetch) loading " + String.valueOf(zonesResponse.getZones().getFences().size()) + " geofences...");
                        getFences();
                        fences.setValue(zonesResponse.getZones().getFences());
                    }
                    else
                    {
                        LogUtils.error(LOG_SOURCE, "(fetch) useCaseResponse success is false");
                    }
                }
                else
                {
                    LogUtils.error(LOG_SOURCE, "(fetch) response was " + String.valueOf(response.code()) + " while fetching zones");
                }
            }

            @Override
            public void onFailure(Call<ZonesResponse> call, Throwable t) {
                LogUtils.debug(LOG_SOURCE, "(fetch) onFailure(): " + t.getLocalizedMessage());
            }
        });
    }
    //  ------------------------------------------------------------------------------------
}

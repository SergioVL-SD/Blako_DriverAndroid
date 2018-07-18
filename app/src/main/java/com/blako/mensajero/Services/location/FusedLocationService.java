package com.blako.mensajero.Services.location;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.blako.mensajero.Utils.LogUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class FusedLocationService {

    final String LOG_SOURCE = FusedLocationService.class.getSimpleName();

    private final int FASTEST_INTERVAL = 12000;

    Context context;

    private GoogleApiClient googleApiClient;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;

    private boolean fixed;

    private OnLocationServiceListener listener;

    public boolean isFixed() {
        return fixed;
    }

    public void setOnLocationServiceListener(OnLocationServiceListener listener) {
        this.listener = listener;
    }


    public FusedLocationService(Context context) {
        this.context = context;

        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        LogUtils.debug(LOG_SOURCE, "(gps) connection success");
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        LogUtils.error(LOG_SOURCE, "(gps) connection suspended");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        LogUtils.error(LOG_SOURCE, "(gps) connection failed: " + connectionResult.getErrorMessage());
                    }
                })
                .build();

        if (googleApiClient != null) {
            googleApiClient.connect();
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        createLocationRequest(FASTEST_INTERVAL);
        createLocationCallback();

        fixed = false;

        requestLastLocation();
    }

    protected void createLocationRequest(long refresh) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setFastestInterval(refresh);
        mLocationRequest.setInterval(refresh);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mCurrentLocation = locationResult.getLastLocation();

                LogUtils.debug(LOG_SOURCE, "(gps) location updated");
                if(listener != null){
                    listener.onLocationUpdate(mCurrentLocation);
                }
            }
        };
    }


    public void requestLocationUpdates() {
        LogUtils.debug(LOG_SOURCE, "(gps) Requesting location updates");

        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        } catch (SecurityException unlikely) {
            LogUtils.error(LOG_SOURCE, "(gps) Lost location permission. Could not request updates. " + unlikely);
        }
    }

    public void removeLocationUpdates() {
        LogUtils.debug(LOG_SOURCE, "(gps) Removing location updates");
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        } catch (SecurityException unlikely) {
            LogUtils.error(LOG_SOURCE, "(gps) Lost location permission. Could not remove updates. " + unlikely);
        }
    }


    public void requestLastLocation() {
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                mCurrentLocation = task.getResult();
                                if(listener != null){
                                    listener.onLocationUpdate(mCurrentLocation);
                                }

                                LogUtils.debug(LOG_SOURCE, "(gps) got latest location");
                                fixed = true;
                            } else {
                                LogUtils.debug(LOG_SOURCE, "(gps) Failed to get location.");
                                fixed = false;
                            }
                        }
                    });
        } catch (SecurityException unlikely) {
            LogUtils.error(LOG_SOURCE, "(gps) Lost location permission." + unlikely);
        }
    }
}

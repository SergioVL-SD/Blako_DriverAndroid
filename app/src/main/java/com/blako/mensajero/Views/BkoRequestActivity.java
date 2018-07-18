package com.blako.mensajero.Views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blako.mensajero.Adapters.BkoTripsAdapter;
import com.blako.mensajero.BkoDataMaganer;
import com.blako.mensajero.Constants;
import com.blako.mensajero.Dao.BkoUserDao;
import com.blako.mensajero.R;
import com.blako.mensajero.Services.BkoSendLocationToServer;
import com.blako.mensajero.Utils.BkoUtilities;
import com.blako.mensajero.Utils.HttpRequest;
import com.blako.mensajero.VO.BkoCheckoutResponse;
import com.blako.mensajero.VO.BkoChildTripVO;
import com.blako.mensajero.VO.BkoRequestResponse;
import com.blako.mensajero.VO.BkoTripVO;
import com.blako.mensajero.VO.BkoUser;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by franciscotrinidad on 12/28/15.
 */
public class BkoRequestActivity extends BaseActivity implements OnMapReadyCallback {
    boolean  loaded = false;
    float actVolume, maxVolume, volume;
    AudioManager audioManager;
    private TextView aliasBt, addressBt, tvTripDistance;
    private SoundPool soundPool;
    private View touchAreaView;
    private int soundID;
    private SupportMapFragment map;
    private ProgressBar progressBarPb;
    private long mLastClickTime = 0;
    public static BkoTripVO tripP;
    public static BkoChildTripVO trip;
    public static BkoChildTripVO tripDelivery;
    public static boolean onRequest= false;
    private String awaiteServiceResponse;
    protected GoogleMap gMap = null;
    private String polylineResponse = null;
    Handler spriteHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bko_request_activity);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        BkoRequestActivity.stop = false;
        aliasBt = (TextView) findViewById(R.id.aliasBt);
        addressBt = (TextView) findViewById(R.id.addressBt);
        tvTripDistance = (TextView) findViewById(R.id.tvTripDistance);
        progressBarPb = (ProgressBar) findViewById(R.id.progressBarPb);
        touchAreaView= (View) findViewById(R.id.touchAreaView);
        map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);

        progressBarPb.setIndeterminate(true);
/**-----------------------sound-----------------------**/
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = actVolume / maxVolume;

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
            }
        });
        soundID = soundPool.load(getApplicationContext(), R.raw.beep, 1);
/**-----------------------sound-----------------------**/




        touchAreaView.setOnClickListener(new View.OnClickListener()

                                   {
                                       public void onClick(View view) {
                                           if (SystemClock.elapsedRealtime() - mLastClickTime < 2500) {
                                               return;
                                           }
                                           mLastClickTime = SystemClock.elapsedRealtime();
                                           touchAreaView.setEnabled(false);
                                           onReportAwaite(trip,tripDelivery);


                                       }

                                   }

        );

        setData();

        /****************************************/
        spriteHandler = new Handler(); // Keep this global to the scope of the class. You only need one.

        /**ESTA VARIABLE TENDRA QUE SE IGUAL A LA DEL TIEMPO DE CAMBIO DE PANTALLA*/


            spriteHandler.postDelayed(new Runnable() {
                public void run() {


                    if(!stop){
                        ejecutar();
                        spriteHandler.postDelayed(this, 1000);
                    }

                    else {
                        spriteHandler.removeCallbacks(this);
                    }

                }
            }, 1000);


    }
    protected void onDestroy(){
        super.onDestroy();
       stop = true;
    }
    @Override
    public void onBackPressed() {

    }


    private void setData() {
        try {

            if (BkoDataMaganer.getStatusService(BkoRequestActivity.this) == Constants.SERVICE_STATUS_WITH_OFFER_CHECKIN_FINISH) {
                BkoDataMaganer.setStatusRequest(null, BkoRequestActivity.this);
            }

            String numberIntExt = " Ext." + trip.getBko_customeraddress_numexterior();
            if (trip.getBko_customeraddress_numinterior() != null && trip.getBko_customeraddress_numinterior().length() > 0) {
                numberIntExt = numberIntExt + " Int." + trip.getBko_customeraddress_numinterior();
            }
            addressBt.setText(BkoUtilities.ensureNotNullString(trip.getBko_customeraddress_street() + numberIntExt + ", " + trip.getBko_customeraddress_colony() + ", " + trip.getBko_customeraddress_delegation()));
            //aliasOfferTv.setText(BkoUtilities.ensureNotNullString(trip.getBko_customeraddress_alias()));
            //addressBt.setText(statusRequest.getStreet() + ", " + statusRequest.getColony() + ", " + statusRequest.getDelegation());

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(BkoRequestActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();
        }




    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        try {
            //statusRequest = BkoDataMaganer.getCurrentStatusRequest(BkoRequestActivity.this);
            ejecutar();
            double latitudMap = Double.parseDouble(trip.getBko_customeraddress_latitude());
            double longitudMap = Double.parseDouble(trip.getBko_customeraddress_longitude());

            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    new LatLng(latitudMap, longitudMap)).zoom(18).build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            googleMap.setMyLocationEnabled(true); // false to disable
            /*************************************************************************/
            googleMap.getUiSettings().setZoomControlsEnabled(false); // true to enable

            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(
                            new LatLng(latitudMap, longitudMap))
                    .draggable(true).visible(true));


            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.markerorigin));

            // TODO: 29/06/2018 añadiendo tiempo de espera antes de mandar a pintar la ruta y marcadores
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setDataZoom();
                }
            },1000);

        } catch (Exception e) {

        }
    }


    private void getPoly(final double latA, final double lngA, final double latB, final double lngB) {
        Log.d("Poly_Ok","Enter getPoly method");
        executeInBackground(new Runnable() {
            public void run() {

                try {

                    polylineResponse = null;
                    polylineResponse = HttpRequest.get("https://maps.googleapis.com/maps/api/directions/json?" +
                            "origin=" + latA + "," + lngA +
                            "&destination=" + latB + "," + lngB +
                            "&sensor=false&key=AIzaSyD7sVTV3wFfBBZoaomK7Ncm81kYXB1jIYo ").connectTimeout(5000).readTimeout(5000).body();

                    Log.d("Poly_Response",polylineResponse);

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("Poly_Error",e.getLocalizedMessage());
                }

            }
        }, new Runnable() {
            public void run() {
                if (polylineResponse != null) {
                    drawPath(polylineResponse);
                }
            }
        });

    }

    public void drawPath(String result) {
        try {
            Log.d("Poly_Ok","Drawing poly line");

            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            //System.out.print("carlos_array: ");



            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes
                    .getJSONObject("overview_polyline");



            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = BkoUtilities.decodePoly(encodedString);

            for (int z = 0; z < list.size() - 1; z++) {
                LatLng src = list.get(z);
                LatLng dest = list.get(z + 1);

               Polyline line = gMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(src.latitude, src.longitude),
                                new LatLng(dest.latitude, dest.longitude))
                        .width(10).color(Color.rgb(23, 32, 45)).geodesic(true));

            }

            JSONArray legsArray = routes.getJSONArray("legs");
            JSONObject legs= legsArray.getJSONObject(0);
            JSONObject distance = legs
                    .getJSONObject("distance");
            String distanceText = distance
                    .getString("text");
            JSONObject duration = legs
                    .getJSONObject("duration");
            String durationText = duration
                    .getString("text");


            aliasBt.setText("" + durationText.toUpperCase() );
            tvTripDistance.setText(distanceText);
            Log.d("Poly_distance",distanceText);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Poly_Error_Drawing",e.getLocalizedMessage());
        }
    }

    public static  boolean stop = false;
    public void ejecutar() {
        soundPool.play(soundID, volume, volume, 2, 0, 1f);
    }
    @Override
    public void onStop() {
        super.onStop();


    }


    @Override
    public void onStart() {
        super.onStart();


    }

    private void onReportAwaite(final BkoChildTripVO childTripVO, final BkoChildTripVO childDeliveryTripVO) {
        showWaitDialogWhileExecuting("Aceptando...", new Runnable() {
            public void run() {
                try {


                    BkoUser user = BkoUserDao.Consultar(BkoRequestActivity.this);
                    awaiteServiceResponse = null;

                    String queuedtasksrequestId, tripIdOrigen, warehouseLatitude, warehouseLongitude, workerLatitude = null, workerLongitude = null;


                    queuedtasksrequestId = childTripVO.getBko_queuedtasksrequest_id();
                    tripIdOrigen = childTripVO.getBko_orders_trips_id();
                    warehouseLatitude = childTripVO.getBko_customeraddress_latitude();
                    warehouseLongitude = childTripVO.getBko_customeraddress_longitude();


                    Location userLocation = BkoDataMaganer.getCurrentUserLocation(BkoRequestActivity.this);


                    String request = Constants.GET_CONFIRM_AWAIT(BkoRequestActivity.this) +
                            "workerId=" + user.getWorkerId() +
                            "&tripIdOrigen=" + tripIdOrigen +
                            "&queuedtasksrequestId=" + queuedtasksrequestId +
                            "&warehouseLatitude=" + warehouseLatitude +
                            "&warehouseLongitude=" + warehouseLongitude + "&queuedtasksId=" + childTripVO.getBko_queuedtasks_id() + "&tripIdDestino=" + childDeliveryTripVO.getBko_orders_trips_id();

                    if (userLocation != null) {
                        workerLatitude = "" + userLocation.getLatitude();

                        workerLongitude = "" + userLocation.getLongitude();
                        request += "&workerLatitude=" + workerLatitude +
                                "&workerLongitude=" + workerLongitude;
                    }


                    if (BkoSendLocationToServer.oneLocationDataToWebsite(BkoDataMaganer.getCurrentUserLocation(BkoRequestActivity.this), BkoRequestActivity.this))
                        awaiteServiceResponse = HttpRequest.get(request).connectTimeout(3000).readTimeout(3000).body();


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Runnable() {
            public void run() {
                BkoRequestActivity.stop = true;
                touchAreaView.setEnabled(true);
                if (awaiteServiceResponse != null) {

                    try {

                        Gson gson = new Gson();
                        BkoRequestResponse response = gson.fromJson(awaiteServiceResponse, BkoCheckoutResponse.class);

                        if (response != null) {
                            if (response.isResponse()) {
                                BkoRequestActivity.onRequest = false;
                                if (BkoTripsAdapter.mp != null && BkoTripsAdapter.mp.isPlaying())
                                    BkoTripsAdapter.mp.stop();

                                BkoTripsInProgressFragment.acepted = true;
                                finish();

                                BkoTripDetailActivity.confirmed = true;
                                BkoDataMaganer.setCuurentTemporaryTrip(tripP);
                                Intent intent = new Intent(BkoRequestActivity.this, BkoTripDetailActivity.class);
                                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivityForResult(intent, 1);

                                //Intent intentMap = new Intent(BkoRequestActivity.this, BkoMainActivity.class);
                                //startActivity(intentMap);
                            } else {
                                Toast.makeText(BkoRequestActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();


                            }
                        } else {
                            Toast.makeText(BkoRequestActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(BkoRequestActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(BkoRequestActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    private void setDataZoom() {

        try {
            if(trip== null ||trip.getBko_customeraddress_latitude()==null || trip.getBko_customeraddress_latitude().length()== 0){
                return;
            }
            ArrayList<LatLng> locations = new ArrayList<>();
            Location userLocation = BkoDataMaganer.getCurrentUserLocation(this);
            if (userLocation != null) {
                locations.add(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()));
            }

            double latitudMap = Double.parseDouble(trip.getBko_customeraddress_latitude());
            double longitudMap = Double.parseDouble(trip.getBko_customeraddress_longitude());
            Log.d("Poly_Cus_Lat",String.valueOf(latitudMap));
            Log.d("Poly_Cus_Long",String.valueOf(longitudMap));
            locations.add(new LatLng(latitudMap, longitudMap));
            if (locations.size() > 1) {
                Log.d("Poly_Ok","Sending to track");
                setZoomForCurrentMarkers(locations);
                getPoly(userLocation.getLatitude(),userLocation.getLongitude(),latitudMap,longitudMap);
            }


        }catch (Exception e){

        }



    }

    protected void setZoomForCurrentMarkers(ArrayList<LatLng> locations) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng location : locations) {
            builder.include(location);
        }
        LatLngBounds bounds = builder.build();
        int padding = 100;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        gMap.animateCamera(cu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("GeneralFlux","Activity: "+BkoRequestActivity.this.getLocalClassName());
    }

}
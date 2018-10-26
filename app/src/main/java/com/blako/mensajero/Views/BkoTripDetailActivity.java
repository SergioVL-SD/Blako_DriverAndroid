package com.blako.mensajero.Views;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blako.mensajero.BkoDataMaganer;
import com.blako.mensajero.Constants;
import com.blako.mensajero.Custom.BkoSeekBar;
import com.blako.mensajero.Custom.BkoWorkaroundMapFragment;
import com.blako.mensajero.Dao.BkoUserDao;
import com.blako.mensajero.R;
import com.blako.mensajero.Services.BkoSendLocationToServer;
import com.blako.mensajero.Utils.BkoUtilities;
import com.blako.mensajero.Utils.DeliveryZoneCheck;
import com.blako.mensajero.Utils.HttpRequest;
import com.blako.mensajero.VO.BkoChildTripVO;
import com.blako.mensajero.VO.BkoOffer;
import com.blako.mensajero.VO.BkoPushRequest;
import com.blako.mensajero.VO.BkoRequestResponse;
import com.blako.mensajero.VO.BkoTripVO;
import com.blako.mensajero.VO.BkoUser;
import com.blako.mensajero.VO.BkoVehicleVO;
import com.blako.mensajero.firebase.BkoFirebaseStorage;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlPlacemark;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by franciscotrinidad on 1/19/16.
 */
public class BkoTripDetailActivity extends BaseActivity implements OnMapReadyCallback, GpsStatus.Listener {
    private GoogleMap map = null;
    private SeekBar startSB, completeSB;
    private boolean isDeliveryAddress;
    private String startingResponse = null;
    private View seekLl;
    private String latitude, longitude;
    private TextView mTitle;
    private boolean sending = false;
    private View offerNavV;
    private AppCompatImageView okComfirmTv, phoneDeliveryIv, phoneRestaurantIv;
    private AppCompatImageView arrowComfirmTv;
    private TextView confirmTv, pickDeliveryAddresTv, aliasOfferTv, addressOfferTv, pickDeliveryTitleTv, phoneDeliveryTv, tvNoConnection;
    private Button btnNotifyProblem;
    private View seekFLl;
    public static boolean confirmed;
    private boolean sendingCompelte = false;
    private long mLastClickTime = 0;
    private TextView showItemsTv,timeTripTv;
    private BkoSeekBar arriveSB;
    private View seekALl;
    private String arriveResponse;
    ///
    private BkoChildTripVO trip;
    private BkoTripVO parentTrip;
    private boolean onConfirmedDelivery = false;
    CountDownTimer timer;

    private static final long NO_CONECTION_TIME = 1000 * 60 * 3; //--> Minutes
    private int status=0;
    private int checkEvery= 10;

    private FirebaseStorage firebaseStorage;
    private KmlLayer kmlLayer;
    private List<KmlPlacemark> placemarkList;
    private Location originLocation;
    private Location destinationLocation;

    private ReceiveServiceLocation receiveServiceLocation;
    private IntentFilter filterLocation;

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bko_trip_detail_activity);
        Toolbar mToolbarView = (Toolbar) findViewById(R.id.toolbar);

        if (mToolbarView != null) {
            setSupportActionBar(mToolbarView);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            mTitle = (TextView) mToolbarView.findViewById(R.id.toolbar_title);
            mTitle.setTextColor(ContextCompat.getColor(this, R.color.blako_white));

        }

        isDeliveryAddress = getIntent().getBooleanExtra("isDeliveryAddress", false);
        BkoWorkaroundMapFragment mapFrag = (BkoWorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapaFt);
        mapFrag.getMapAsync(this);
        startSB = (SeekBar) findViewById(R.id.startSB);
        completeSB = (SeekBar) findViewById(R.id.completeSB);
        arriveSB = (BkoSeekBar) findViewById(R.id.arriveSB);
        seekLl = findViewById(R.id.seekLl);
        confirmTv = (TextView) findViewById(R.id.confirmTv);
        pickDeliveryAddresTv = (TextView) findViewById(R.id.pickDeliveryAddresTv);
        offerNavV = findViewById(R.id.offerNavV);
        seekFLl = findViewById(R.id.seekFLl);
        pickDeliveryTitleTv = (TextView) findViewById(R.id.pickDeliveryTitleTv);
        aliasOfferTv = (TextView) findViewById(R.id.aliasOfferTv);
        addressOfferTv = (TextView) findViewById(R.id.addressOfferTv);
        btnNotifyProblem= (Button) findViewById(R.id.btnNotifyProblem);
        okComfirmTv = (AppCompatImageView) findViewById(R.id.okComfirmTv);
        phoneDeliveryIv = (AppCompatImageView) findViewById(R.id.phoneDeliveryIv);
        phoneRestaurantIv = (AppCompatImageView) findViewById(R.id.phoneRestaurantIv);
        arrowComfirmTv = (AppCompatImageView) findViewById(R.id.arrowComfirmTv);
        phoneDeliveryTv = (TextView) findViewById(R.id.phoneDeliveryTv);
        tvNoConnection = (TextView) findViewById(R.id.tvNoConnection);
        showItemsTv = (TextView) findViewById(R.id.showItemsTv);
        seekALl = findViewById(R.id.seekALl);
        timeTripTv  = (TextView) findViewById(R.id.timeTripTv);

        originLocation= new Location("");
        destinationLocation= new Location("");

        filterLocation= new IntentFilter(Constants.ACTION_SERVICE_LOCATION);
        receiveServiceLocation= new ReceiveServiceLocation();

        firebaseStorage= BkoFirebaseStorage.getStorage();

        placemarkList= new ArrayList<>();

        phoneRestaurantIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BkoDataMaganer.getRestaurantPhone(BkoTripDetailActivity.this).equals("")){
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + BkoDataMaganer.getRestaurantPhone(BkoTripDetailActivity.this)));
                    if (ActivityCompat.checkSelfPermission(BkoTripDetailActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(BkoTripDetailActivity.this,
                                new String[]{Manifest.permission.CALL_PHONE},
                                1000);
                        return;
                    }
                    startActivity(intent);
                }
            }
        });

        phoneDeliveryIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                String phone_no = phoneDeliveryTv.getText().toString().trim();
                intent.setData(Uri.parse("tel:" + phone_no));
                if (ActivityCompat.checkSelfPermission(BkoTripDetailActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(BkoTripDetailActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            1000);
                    return;
                }
                startActivity(intent);
            }
        });

        offerNavV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BkoDataMaganer.getCuurentTemporaryTrip() == null) {
                    return;
                }
                BkoVehicleVO vehicleVO= BkoDataMaganer.getCurrentVehicle(BkoTripDetailActivity.this);
                try {
                    if (Integer.parseInt(vehicleVO.getTypeid())==2){
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + trip.getBko_customeraddress_latitude() + "," + trip.getBko_customeraddress_longitude()+"&mode=b");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                        return;
                    }
                }catch (Exception e){
                    e.fillInStackTrace();
                }
                BkoUtilities.showLocation(BkoTripDetailActivity.this, trip.getBko_customeraddress_latitude(), trip.getBko_customeraddress_longitude());

            }
        });


        confirmTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (parentTrip == null) {
                    Toast.makeText(BkoTripDetailActivity.this, "4" + getString(R.string.blako_error), Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }


                if (parentTrip.getOrigen().get(0).getBko_orders_trips_item_confirm() == null || parentTrip.getOrigen().get(0).getBko_orders_trips_item_confirm().length() == 0) {
                    Intent intent = new Intent(BkoTripDetailActivity.this, BkoConfirmItemsActivity.class);
                    startActivityForResult(intent, 101);
                } else {
                    BkoChildTripVO currentTrip = trip;
                    Location location = BkoDataMaganer.getCurrentUserLocation(BkoTripDetailActivity.this);
                    currentTrip.setReceiveddatetime(BkoUtilities.nowCurrent());
                    currentTrip.setBko_orders_trips_completeddatetime(BkoUtilities.nowCurrent());
                    currentTrip.setEndLatitud(location.getLatitude());
                    currentTrip.setEndLongitude(location.getLongitude());
                    if (currentTrip.getBko_orders_trips_startdatetime() == null)
                        currentTrip.setBko_orders_trips_startdatetime(BkoUtilities.nowCurrent());
                    BkoDataMaganer.setCurrentTrip(currentTrip, BkoTripDetailActivity.this);

                    Intent intent = new Intent(BkoTripDetailActivity.this, BkoTicketActivity.class);
                    startActivity(intent);
                }


            }
        });


        showItemsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(BkoTripDetailActivity.this, BkoConfirmItemsActivity.class);
                intent.putExtra("onlyDetails", true);
                startActivityForResult(intent, 101);
            }
        });


        startSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                               @Override
                                               public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                                                   if (progress > 80) {
                                                       if (sending)
                                                           return;
                                                       startSB.setEnabled(false);
                                                       sending = true;
                                                       Log.i("progress", "*****" + progress);
                                                       showWaitDialogWhileExecuting("Cargando...", new Runnable() {
                                                                   public void run() {
                                                                       Log.i("Arriving", "Arriving");
                                                                       try {
                                                                           startingResponse = null;

                                                                           if (trip == null)
                                                                               return;
                                                                           trip = parentTrip.getDestino().get(0);
                                                                           final BkoPushRequest serviceClienteStatus = BkoDataMaganer.getCurrentStatusRequest(BkoTripDetailActivity.this);
                                                                           BkoUser user = BkoUserDao.Consultar(BkoTripDetailActivity.this);


                                                                           String workerLatitude,
                                                                                   workerLongitude,
                                                                                   customerLatitude = null,
                                                                                   customerLongitude = null;


                                                                           Location userLocation = BkoDataMaganer.getCurrentUserLocation(BkoTripDetailActivity.this);

                                                                           customerLatitude = "" + trip.getBko_customeraddress_latitude();
                                                                           customerLongitude = "" + trip.getBko_customeraddress_longitude();


                                                                           String request = Constants.GET_WORKER_STARTING_URL(BkoTripDetailActivity.this) +
                                                                                   "connectToken=" + user.getConectToken() +
                                                                                   "&oid=" + serviceClienteStatus.getOid() + "&tripId=" + trip.getBko_orders_trips_id()
                                                                                   + "&tripIdDestino=" + BkoDataMaganer.getCuurentTemporaryTrip().getDestino().get(0).getBko_orders_trips_id()
                                                                                   + "&customerLatitude=" + customerLatitude
                                                                                   + "&customerLongitude=" + customerLongitude + "&queuedtasksId=" + trip.getBko_queuedtasks_id()
                                                                                   + "&orderId=" + trip.getBko_orders_id();

                                                                           if (userLocation != null) {
                                                                               workerLatitude = "" + userLocation.getLatitude();

                                                                               workerLongitude = "" + userLocation.getLongitude();
                                                                               request += "&workerLatitude=" + workerLatitude
                                                                                       + "&workerLongitude=" + workerLongitude;
                                                                           }
                                                                           if (BkoSendLocationToServer.oneLocationDataToWebsite(BkoDataMaganer.getCurrentUserLocation(getApplicationContext()), getApplicationContext()))
                                                                               startingResponse = HttpRequest.get(request

                                                                               ).connectTimeout(6000).readTimeout(6000).body();

                                                                       } catch (Exception e) {
                                                                           e.printStackTrace();
                                                                       }

                                                                   }
                                                               }, new Runnable() {
                                                                   public void run() {

                                                                       startSB.setEnabled(true);
                                                                       if (startingResponse != null) {

                                                                           try {
                                                                               Gson gson = new Gson();
                                                                               BkoRequestResponse response = gson.fromJson(startingResponse, BkoRequestResponse.class);
                                                                               if (response != null) {
                                                                                   if (response.isResponse()) {
                                                                                       BkoDataMaganer.getCuurentTemporaryTrip().getDestino().get(0).setBko_orders_trips_startdatetime(BkoUtilities.nowCurrent());
                                                                                       BkoDataMaganer.getCuurentTemporaryTrip().getDestino().get(0).setBko_deliverystatus_id("8");
                                                                                       setData();
                                                                                       Location location = BkoDataMaganer.getCurrentUserLocation(BkoTripDetailActivity.this);
                                                                                       BkoTripDetailActivity.confirmed = true;

                                                                                   } else {
                                                                                       startSB.setProgress(10);
                                                                                       Toast.makeText(BkoTripDetailActivity.this, "1" + response.getMessage(), Toast.LENGTH_SHORT).show();

                                                                                   }
                                                                               } else {
                                                                                   Toast.makeText(BkoTripDetailActivity.this, "2" + getString(R.string.blako_error), Toast.LENGTH_SHORT).show();
                                                                                   startSB.setProgress(10);

                                                                               }
                                                                           } catch (Exception e) {
                                                                               Toast.makeText(BkoTripDetailActivity.this, "3" + getString(R.string.blako_error), Toast.LENGTH_SHORT).show();
                                                                               startSB.setProgress(10);

                                                                           }
                                                                       } else {
                                                                           Toast.makeText(BkoTripDetailActivity.this, "4" + getString(R.string.blako_error), Toast.LENGTH_SHORT).show();
                                                                           startSB.setProgress(10);

                                                                       }
                                                                       sending = false;
                                                                   }
                                                               }

                                                       );

                                                   } else if (progress < 10)
                                                       startSB.setProgress(10);
                                               }

                                               @Override
                                               public void onStartTrackingTouch(SeekBar seekBar) {

                                               }

                                               @Override
                                               public void onStopTrackingTouch(SeekBar seekBar) {

                                               }
                                           }

        );

        completeSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                                  @Override
                                                  public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                                                      if (progress > 80) {
                                                          if (SystemClock.elapsedRealtime() - mLastClickTime < 1500) {
                                                              mLastClickTime = SystemClock.elapsedRealtime();
                                                              completeSB.setProgress(9);
                                                              return;
                                                          }
                                                          mLastClickTime = SystemClock.elapsedRealtime();
                                                          if (sendingCompelte)
                                                              return;
                                                          sendingCompelte = true;


                                                          completeSB.setEnabled(false);
                                                          showWaitDialogWhileExecuting("Cargando...", new Runnable() {
                                                                      public void run() {
                                                                          Log.i("Arriving", "Arriving");
                                                                          try {
                                                                              BkoChildTripVO currentTrip = trip;
                                                                              Location location = BkoDataMaganer.getCurrentUserLocation(BkoTripDetailActivity.this);
                                                                              currentTrip.setReceiveddatetime(BkoUtilities.nowCurrent());
                                                                              currentTrip.setBko_orders_trips_completeddatetime(BkoUtilities.nowCurrent());
                                                                              currentTrip.setEndLatitud(location.getLatitude());
                                                                              currentTrip.setEndLongitude(location.getLongitude());
                                                                              if (currentTrip.getBko_orders_trips_startdatetime() == null)
                                                                                  currentTrip.setBko_orders_trips_startdatetime(BkoUtilities.nowCurrent());
                                                                              BkoDataMaganer.setCurrentTrip(currentTrip, BkoTripDetailActivity.this);

                                                                          } catch (Exception e) {
                                                                              e.printStackTrace();
                                                                          }

                                                                      }
                                                                  }, new Runnable() {
                                                                      public void run() {
                                                                          sendingCompelte = false;
                                                                          completeSB.setProgress(9);
                                                                          completeSB.setEnabled(true);
                                                                          Intent intent = new Intent(BkoTripDetailActivity.this, BkoTicketActivity.class);
                                                                          startActivity(intent);
                                                                          // Intent intent = new Intent(BkoTripDetailActivity.this, BkoStatusFinishedTripActivity.class);
                                                                          //startActivity(intent);

                                                                      }
                                                                  }

                                                          );

                                                      } else if (progress < 10)
                                                          completeSB.setProgress(10);
                                                  }

                                                  @Override
                                                  public void onStartTrackingTouch(SeekBar seekBar) {

                                                  }

                                                  @Override
                                                  public void onStopTrackingTouch(SeekBar seekBar) {

                                                  }
                                              }

        );

        arriveSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                                @Override
                                                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                                                    if (progress > 80) {
                                                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1500) {
                                                            mLastClickTime = SystemClock.elapsedRealtime();
                                                            arriveSB.setProgress(9);
                                                            return;
                                                        }
                                                        mLastClickTime = SystemClock.elapsedRealtime();
                                                        arriving(trip.getBko_orders_trips_id(), BkoDataMaganer.getCuurentTemporaryTrip().getDestino().get(0).getBko_orders_trips_id());


                                                    } else if (progress < 10)
                                                        arriveSB.setProgress(10);
                                                }

                                                @Override
                                                public void onStartTrackingTouch(SeekBar seekBar) {

                                                }

                                                @Override
                                                public void onStopTrackingTouch(SeekBar seekBar) {

                                                }
                                            }

        );


        try {
            registerReceiver(mPushReceiver, new IntentFilter("trips"));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        setData();

    }

    private class ReceiveServiceLocation extends BroadcastReceiver {

        public ReceiveServiceLocation() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent!=null){
                // TODO: 16/07/2018 location filter to enable/disable btn
                Log.d("Location_received","Ok");
                /*Location enableLocation= BkoDataMaganer.getCurrentUserLocation(BkoTripDetailActivity.this);
                if (enableLocation!=null){
                    if (originLocation!=null){
                        Log.d("DistanceToOrigin",String.valueOf(enableLocation.distanceTo(originLocation)));
                        if (enableLocation.distanceTo(originLocation)<1600){
                            arriveSB.setEnabled(true);
                            arriveSB.setBackgroundColor(ContextCompat.getColor(BkoTripDetailActivity.this, R.color.blako_background));
                        }else {
                            arriveSB.setEnabled(false);
                            arriveSB.setBackgroundColor(ContextCompat.getColor(BkoTripDetailActivity.this, R.color.blako_gray_low));
                        }
                    }
                    if (destinationLocation!=null){
                        Log.d("DistanceToDestination",String.valueOf(enableLocation.distanceTo(destinationLocation)));
                        if (enableLocation.distanceTo(destinationLocation)<1600){
                            startSB.setEnabled(true);
                            startSB.setBackgroundColor(ContextCompat.getColor(BkoTripDetailActivity.this, R.color.blako_green));
                        }else {
                            startSB.setEnabled(false);
                            startSB.setBackgroundColor(ContextCompat.getColor(BkoTripDetailActivity.this, R.color.blako_gray_low));
                        }
                    }
                }*/
            }
        }
    }

    private void setData() {
        parentTrip = BkoDataMaganer.getCuurentTemporaryTrip();

        if (parentTrip == null) {
            Intent intent = new Intent(BkoTripDetailActivity.this, BkoMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finishAffinity();
            return;
        }


        if (parentTrip.getOrigen().get(0).getBko_orders_trips_item_confirm() == null || parentTrip.getOrigen().get(0).getBko_orders_trips_item_confirm().length() == 0) {
            isDeliveryAddress = false;
            trip = parentTrip.getOrigen().get(0);
            if (trip!=null){
                originLocation.setLatitude(Double.parseDouble(trip.getBko_customeraddress_latitude()));
                originLocation.setLongitude(Double.parseDouble(trip.getBko_customeraddress_longitude()));
            }
            BkoDataMaganer.setRestaurantPhone(this,parentTrip.getOrigen().get(0).getBko_customeraddress_telephone());
        } else {
            trip = parentTrip.getDestino().get(0);
            if (trip!=null){
                destinationLocation.setLatitude(Double.parseDouble(trip.getBko_customeraddress_latitude()));
                destinationLocation.setLongitude(Double.parseDouble(trip.getBko_customeraddress_longitude()));
            }
            isDeliveryAddress = true;
        }

        if (trip.getBko_queuedtasks_shipping_schedule() != null && !trip.getBko_queuedtasks_shipping_schedule().equals("0") && trip.getBko_queuedtasks_shipping_schedule().length() != 0) {


            if(timer!=null)
            {
                timer.cancel();
            }

            long end;
            final Date now = new Date();
            final Date dateTrip = BkoUtilities.getDate(trip.getBko_queuedtasks_shipping_schedule(), "yyyy-MM-dd HH:mm:ss");
            end = now.getTime() - dateTrip.getTime();

           timer = new CountDownTimer(end, 1000) {

                public void onTick(long a) {
                    try {

                        Date now = new Date();
                        long end = now.getTime() - dateTrip.getTime();

                        long hoursL = ((end / (1000 * 60 * 60)) % 24);
                        long minutesL = ((end / (1000 * 60)) % 60);
                        long secondsL = ((end / 1000) % 60);
                        String seconds = secondsL < 10 ? "0" + secondsL : "" + secondsL;
                        String minutes = minutesL < 10 ? "0" + minutesL : "" + minutesL;
                        String hours = hoursL < 10 ? "0" + hoursL : "" + hoursL;
                        timeTripTv.setText(hours + ":" + minutes + ":" + seconds + " hrs.");

                        if (hoursL > 0) {
                            timeTripTv.setBackgroundColor(ContextCompat.getColor(BkoTripDetailActivity.this,R.color.blako_red));
                        }
                        else if (minutesL < 45 &&minutesL>=30 ) {
                            timeTripTv.setBackgroundColor(ContextCompat.getColor(BkoTripDetailActivity.this,R.color.blako_orange));
                        }
                        else if (minutesL >= 45) {
                            timeTripTv.setBackgroundColor(ContextCompat.getColor(BkoTripDetailActivity.this,R.color.blako_red));
                        }


                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

                public void onFinish() {

                }

            }.start();
        }


        try {
            phoneDeliveryIv.setVisibility(View.GONE);
            phoneDeliveryTv.setVisibility(View.GONE);
            phoneRestaurantIv.setVisibility(View.GONE);
            phoneDeliveryTv.setText("");
            ////////////////// TITLE TURN
            String rangeTime = "";
            BkoOffer.BkoAnnoucement annoucement = BkoDataMaganer.getCurrentOffer(this);

            if (annoucement!= null){
                if (annoucement.getBko_announcement_finishstatus() != null && annoucement.getBko_announcement_finishstatus().equals("1")) {
                    rangeTime =
                            BkoUtilities.formateDateFromstring(annoucement.getBko_announcement_datetimestart()).replace(":00 ", " ").toUpperCase().replace("PM", "").replace("P.M.", "").replace("AM", "").replace("A.M.", "") + " - " +
                                    BkoUtilities.formateDateFromstring(annoucement.getBko_announcement_datetimefinish()).replace(":00 ", " ");
                } else {
                    rangeTime =
                            BkoUtilities.formateDateFromstring(annoucement.getBko_announcement_datetimestart()).replace(":00 ", " ");
                }
            }




            if (trip.getBko_orders_trips_item_confirm() != null && trip.getBko_orders_trips_item_confirm().equals("1")) {
                onConfirmed();
                //4
                phoneDeliveryTv.setText(trip.getBko_customeraddress_telephone());
                phoneDeliveryIv.setVisibility(View.VISIBLE);
                phoneRestaurantIv.setVisibility(View.GONE);
                seekALl.setVisibility(View.GONE);
                seekFLl.setVisibility(View.VISIBLE);
                seekLl.setVisibility(View.INVISIBLE);
                confirmTv.setText(getString(R.string.blako_destino_entrega_confirmada));
                okComfirmTv.setImageResource(R.drawable.circle_checkin_ok);
                if (trip.getBko_queuedtasks_receiver_betweenstreets() != null && trip.getBko_queuedtasks_receiver_betweenstreets().length() != 0)
                    addressOfferTv.setText(addressOfferTv.getText().toString().trim() + " (" + trip.getBko_queuedtasks_receiver_betweenstreets() + ")");

                BkoChildTripVO currentTrip = trip;
                Location location = BkoDataMaganer.getCurrentUserLocation(BkoTripDetailActivity.this);
                currentTrip.setReceiveddatetime(BkoUtilities.nowCurrent());
                currentTrip.setBko_orders_trips_completeddatetime(BkoUtilities.nowCurrent());
                currentTrip.setEndLatitud(location.getLatitude());
                currentTrip.setEndLongitude(location.getLongitude());
                if (currentTrip.getBko_orders_trips_startdatetime() == null)
                    currentTrip.setBko_orders_trips_startdatetime(BkoUtilities.nowCurrent());
                BkoDataMaganer.setCurrentTrip(currentTrip, BkoTripDetailActivity.this);


                if (onConfirmedDelivery) {
                    finish();
                    Intent intent = new Intent(BkoTripDetailActivity.this, BkoTicketActivity.class);
                    startActivity(intent);
                }


            } else {

                if (isDeliveryAddress) {
                    /////2
                    if (trip.getBko_queuedtasks_receiver_betweenstreets() != null && trip.getBko_queuedtasks_receiver_betweenstreets().length() != 0)
                        addressOfferTv.setText(addressOfferTv.getText().toString().trim() + " (" + trip.getBko_queuedtasks_receiver_betweenstreets() + ")");
                    seekALl.setVisibility(View.GONE);
                    onConfirmed();
                    if (trip.getBko_orders_trips_startdatetime() == null || trip.getBko_orders_trips_startdatetime().length() == 0) {
                        seekFLl.setVisibility(View.GONE);
                        seekLl.setVisibility(View.VISIBLE);
                        if (annoucement!= null){
                            mTitle.setText(BkoUtilities.ensureNotNullString(("TURNO/" + rangeTime) + " \n" + annoucement.getBko_announcementaddress_alias()));
                        }

                        confirmTv.setText(getString(R.string.blako_destino_orden_confirmada));
                        okComfirmTv.setImageResource(R.drawable.circle_checkin_ok);
                        isDeliveryAddress = false;
                        trip = parentTrip.getOrigen().get(0);

                    } else {
                        seekALl.setVisibility(View.GONE);
                        //////3
                        onNoConfirmed();
                        if (trip.getBko_queuedtasks_receiver_betweenstreets() != null && trip.getBko_queuedtasks_receiver_betweenstreets().length() != 0)
                            addressOfferTv.setText(addressOfferTv.getText().toString().trim() + " (" + trip.getBko_queuedtasks_receiver_betweenstreets() + ")");
                        mTitle.setText(getString(R.string.blako_destino_orden_camino));
                        seekFLl.setVisibility(View.VISIBLE);
                        seekLl.setVisibility(View.INVISIBLE);
                        confirmTv.setText(getString(R.string.blako_confirmar_tarea));
                        phoneDeliveryIv.setVisibility(View.VISIBLE);
                        phoneRestaurantIv.setVisibility(View.GONE);
                        phoneDeliveryTv.setText(trip.getBko_customeraddress_telephone());
                        confirmTv.setTextColor(ContextCompat.getColor(this, R.color.blako_red_two));
                        okComfirmTv.setImageResource(R.drawable.pencilpurple);
                        arrowComfirmTv.setImageResource(R.drawable.arrored);
                    }


                } else {
                    phoneRestaurantIv.setVisibility(View.VISIBLE);
                    // 1

                    onNoConfirmed();
                    seekALl.setVisibility(View.VISIBLE);
                    seekLl.setVisibility(View.VISIBLE);
                    seekFLl.setVisibility(View.GONE);
                    confirmTv.setText(getString(R.string.blako_confirmar_orden));
                    confirmTv.setTextColor(ContextCompat.getColor(this, R.color.blako_green));
                    okComfirmTv.setImageResource(R.drawable.pencilgreen);
                    arrowComfirmTv.setImageResource(R.drawable.arrogreen);

                    if (trip.getBko_deliverystatus_id() != null && trip.getBko_deliverystatus_id().equals("4"))
                        seekALl.setVisibility(View.GONE);
                }

            }

            //ORIGEN
            if (!isDeliveryAddress) {
                pickDeliveryAddresTv.setText(BkoUtilities.ensureNotNullString(trip.getBko_customeraddress_alias()));

                if (annoucement!= null){
                    mTitle.setText(BkoUtilities.ensureNotNullString(("TURNO/" + rangeTime) + " \n" + annoucement.getBko_announcementaddress_alias()));
                }

                pickDeliveryTitleTv.setText(getString(R.string.blako_origen_recoger));
            }
            // ENTREGA
            else {
                pickDeliveryAddresTv.setText(BkoUtilities.ensureNotNullString(trip.getBko_customeraddress_contact()));
                pickDeliveryTitleTv.setText(getString(R.string.blako_destino_entregar));
            }


            ////////////////// ADDRESS AND CLIENT NAME TOP
            String numberIntExt = " Ext." + trip.getBko_customeraddress_numexterior();
            if (trip.getBko_customeraddress_numinterior() != null && trip.getBko_customeraddress_numinterior().length() > 0) {
                numberIntExt = numberIntExt + " Int." + trip.getBko_customeraddress_numinterior();
            }
            addressOfferTv.setText(BkoUtilities.ensureNotNullString(trip.getBko_customeraddress_street() + numberIntExt + ", " + trip.getBko_customeraddress_colony() + ", " + trip.getBko_customeraddress_delegation()));
            aliasOfferTv.setText(BkoUtilities.ensureNotNullString(trip.getBko_customeraddress_alias()));
            if (isDeliveryAddress && trip.getBko_queuedtasks_receiver_betweenstreets() != null && trip.getBko_queuedtasks_receiver_betweenstreets().length() != 0)
                addressOfferTv.setText(addressOfferTv.getText().toString().trim() + " --  Referencias : (" + trip.getBko_queuedtasks_receiver_betweenstreets() + ")");

            //////////////////
            ////////////////// LATITUDE LONGITUDE
            latitude = trip.getBko_customeraddress_latitude();
            longitude = trip.getBko_customeraddress_longitude();


            setMarker();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void onConfirmed() {
        showItemsTv.setVisibility(View.VISIBLE);
        confirmTv.setEnabled(false);
        confirmTv.setTextColor(ContextCompat.getColor(this, R.color.blako_gray_low));
        okComfirmTv.setVisibility(View.VISIBLE);
        arrowComfirmTv.setVisibility(View.INVISIBLE);
        // TODO: 16/07/2018 changes to enable button
        startSB.setBackgroundColor(ContextCompat.getColor(this, R.color.blako_green));
        startSB.setEnabled(true);
        completeSB.setBackgroundColor(ContextCompat.getColor(this, R.color.blako_red_two));
        completeSB.setEnabled(true);
    }

    private void onNoConfirmed() {
        showItemsTv.setVisibility(View.GONE);
        confirmTv.setEnabled(true);
        confirmTv.setTextColor(ContextCompat.getColor(this, R.color.blako_background));
        okComfirmTv.setVisibility(View.VISIBLE);
        arrowComfirmTv.setVisibility(View.VISIBLE);
        startSB.setBackgroundColor(ContextCompat.getColor(this, R.color.blako_gray_low));
        startSB.setEnabled(false);
        completeSB.setBackgroundColor(ContextCompat.getColor(this, R.color.blako_gray_low));
        completeSB.setEnabled(false);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            map.getUiSettings().setZoomControlsEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                setMarker();
            }
            map.setMyLocationEnabled(true);

            StorageReference kmlReference= firebaseStorage.getReference().child("kml-produccion/delivery-zones.kml");
            try {
                final File kmlFile= File.createTempFile("delivery-zones","kml");
                kmlReference.getFile(kmlFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.d("KmlDownload", "Success");
                        try {
                            final FileInputStream inputStream= new FileInputStream(kmlFile);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    kmlLayer= DeliveryZoneCheck.getLayerFromKml(BkoTripDetailActivity.this, map, inputStream);
                                    if (kmlLayer!=null){
                                        placemarkList= DeliveryZoneCheck.getPlacemarks(DeliveryZoneCheck.getMainContainer(kmlLayer));
                                    }
                                }
                            },2000);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("KmlDownload", e.getLocalizedMessage());
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setMarker() {

        if (latitude == null)
            return;

        double lat = Double.parseDouble(latitude);
        double lgt = Double.parseDouble(longitude);
        Location location = new Location("");
        location.setLatitude(lat);
        location.setLongitude(lgt);

        if (map != null) {
            map.clear();
            int drawable = R.drawable.markerorigin;


            if (isDeliveryAddress)
                drawable = R.drawable.markerdelivery;


            map.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lgt)).icon(BitmapDescriptorFactory.fromResource(drawable)));
            displayLocation(location, Constants.TRTP_DETAIL_MAP_ZOOM);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(mPushReceiver);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private BroadcastReceiver mPushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            try {
                String pushType = intent.getStringExtra("type");
                if (pushType.equals("finish")) {
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    private void displayLocation(Location location, float zoom) {
        if (location != null) {
            double currentLatitude = location.getLatitude();
            double currentLongitude = location.getLongitude();
            LatLng latLng = new LatLng(currentLatitude, currentLongitude);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(zoom).build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            map.animateCamera(cameraUpdate, 500, null);

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                BkoTripDetailActivity.confirmed = true;
                if (isDeliveryAddress) {
                    onConfirmedDelivery = true;
                    BkoDataMaganer.getCuurentTemporaryTrip().getDestino().get(0).setBko_orders_trips_item_confirm("1");
                } else {
                    BkoDataMaganer.getCuurentTemporaryTrip().getOrigen().get(0).setBko_orders_trips_item_confirm("1");
                }
                setData();

            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.deliverysBt:
                Intent intentMap = new Intent(this, BkoMainActivity.class);
                startActivity(intentMap);


                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pedidos, menu);
        return true;
    }


    private void arriving(final String tripIdOrigin, final String tripDeliveryId) {

        showWaitDialogWhileExecuting("Cargando...", new Runnable() {
            public void run() {

                try {

                    BkoUser user = BkoUserDao.Consultar(BkoTripDetailActivity.this);
                    arriveResponse = null;
                    String workerId;
                    workerId = user.getWorkerId();


                    Map<String, String> mapVisible = new HashMap<String, String>();
                    mapVisible.put("workerId", workerId);
                    mapVisible.put("tripIdO", tripIdOrigin);
                    mapVisible.put("tripIdD", tripDeliveryId);
                    mapVisible.put("tripType", "origen");
                    mapVisible.put("orderId", parentTrip.getBko_orders_id());
                    if (BkoSendLocationToServer.oneLocationDataToWebsite(BkoDataMaganer.getCurrentUserLocation(getApplicationContext()), getApplicationContext()))
                        arriveResponse = HttpRequest.get(Constants.GET_REPORT_CHECKIN(BkoTripDetailActivity.this), mapVisible, true).connectTimeout(5000).readTimeout(5000).body();


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Runnable() {
            public void run() {


                try {

                    if (arriveResponse != null) {

                        Gson gson = new Gson();

                        BkoRequestResponse response = gson.fromJson(arriveResponse, BkoRequestResponse.class);

                        if (response.isResponse()) {

                            try {

                                BkoTripDetailActivity.confirmed = true;
                                seekALl.setVisibility(View.GONE);


                            } catch (Exception e) {

                                arriveSB.setProgress(10);
                            }
                        } else {
                            Toast.makeText(BkoTripDetailActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                            arriveSB.setProgress(10);
                        }


                    } else {
                        arriveSB.setProgress(10);
                        Toast.makeText(BkoTripDetailActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    arriveSB.setProgress(10);
                    Toast.makeText(BkoTripDetailActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();
                }

            }


            //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.

        });

    }

    @Override
    public void onGpsStatusChanged(int event) {
        switch (event) {

            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                status++;
                if (BkoDataMaganer.getLastLocationTimeStamp(this)!=0 && checkEvery==status && (BkoDataMaganer.getOnDemand(this) || BkoDataMaganer.getCurrentOffer(this)!=null)){
                    status= 0;
                    if (Math.abs(System.currentTimeMillis()-BkoDataMaganer.getLastLocationTimeStamp(this))>NO_CONECTION_TIME){
                        Log.d("GPS_Connection_Status","Con Problemas");
                        tvNoConnection.setVisibility(View.VISIBLE);
                    }else {
                        Log.d("GPS_Connection_Status","Todo Bien");
                        tvNoConnection.setVisibility(View.GONE);
                        Location zoneLocation= BkoDataMaganer.getCurrentUserLocation(this);
                        if (zoneLocation!=null){
                            LatLng latLngLocation= new LatLng(zoneLocation.getLatitude(),zoneLocation.getLongitude());
                            new GetActualAreaTask().execute(latLngLocation);
                        }

                    }
                }
                break;
        }
    }

    private void registerGpsStatusListener() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.addGpsStatusListener(this);
        }catch (SecurityException e){
            e.fillInStackTrace();
        }
    }

    private class GetActualAreaTask extends AsyncTask<LatLng,Void,KmlPlacemark> {

        @Override
        protected KmlPlacemark doInBackground(LatLng... latLngs) {
            return DeliveryZoneCheck.getActualPlacemarkFromLocation(BkoTripDetailActivity.this,placemarkList,latLngs[0]);
        }

        @Override
        protected void onPostExecute(KmlPlacemark placemark) {
            if (placemark!=null){
                Log.d("Actual_Zone",placemark.getProperty("name"));
                BkoDataMaganer.setActualDeliveryZone(BkoTripDetailActivity.this,placemark.getProperty("name"));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("GeneralFlux","Activity: "+BkoTripDetailActivity.this.getLocalClassName());
        LocalBroadcastManager.getInstance(this).registerReceiver(receiveServiceLocation,filterLocation);
        registerGpsStatusListener();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiveServiceLocation);
        if (locationManager!=null){
            locationManager.removeGpsStatusListener(this);
        }
        super.onPause();
    }
}
package com.blako.mensajero.Views;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.blako.mensajero.Adapters.BkoOffersAdapter;
import com.blako.mensajero.Adapters.BkoOffersListAdapter;
import com.blako.mensajero.Adapters.BkoTripsAdapter;
import com.blako.mensajero.App;
import com.blako.mensajero.BkoCore;
import com.blako.mensajero.BkoDataMaganer;
import com.blako.mensajero.Constants;
import com.blako.mensajero.DB.AppDbHelper;
import com.blako.mensajero.DB.models.Hub;
import com.blako.mensajero.DB.models.HubDefaultValue;
import com.blako.mensajero.DB.models.HubPoints;
import com.blako.mensajero.Dao.BkoUserDao;
import com.blako.mensajero.R;
import com.blako.mensajero.Services.BkoGpsTrackerAlarmReceiver;
import com.blako.mensajero.Services.BkoSendLocationToServer;
import com.blako.mensajero.Services.LocationService;
import com.blako.mensajero.Utils.AppPreferences;
import com.blako.mensajero.Utils.BkoUtilities;
import com.blako.mensajero.Utils.DeliveryZoneCheck;
import com.blako.mensajero.Utils.FormatUtils;
import com.blako.mensajero.Utils.HttpRequest;
import com.blako.mensajero.Utils.HubUtils;
import com.blako.mensajero.Utils.KmlColorTempUtil;
import com.blako.mensajero.Utils.LocationUtils;
import com.blako.mensajero.Utils.ZoneUpdate;
import com.blako.mensajero.VO.BkoCheckInResponse;
import com.blako.mensajero.VO.BkoChildTripVO;
import com.blako.mensajero.VO.BkoOffer;
import com.blako.mensajero.VO.BkoPenaltyResponse;
import com.blako.mensajero.VO.BkoPushRequest;
import com.blako.mensajero.VO.BkoRecoverStatusVO;
import com.blako.mensajero.VO.BkoRequestResponse;
import com.blako.mensajero.VO.BkoTripVO;
import com.blako.mensajero.VO.BkoTrips;
import com.blako.mensajero.VO.BkoUser;
import com.blako.mensajero.VO.BkoUserStatusResponse;
import com.blako.mensajero.VO.BkoVehicleVO;
import com.blako.mensajero.firebase.BkoFirebaseDatabase;
import com.blako.mensajero.firebase.BkoFirebaseStorage;
import com.blako.mensajero.models.HubConfig;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class BkoMainActivity extends BkoMainBaseActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, BkoCore.OffersListener, BkoOffersListAdapter.OffersListListener, GpsStatus.Listener {

    protected BkoMainActivity.InternetBroadCast internetBroadCast = new BkoMainActivity.InternetBroadCast();
    private BkoTrips tripsRespone;
    private BkoPushRequest statusRequest;
    private String responseOrderStatus;
    private boolean disconecting = false;
    private FirebaseDatabase firebaseDatabase;
    //private FirebaseStorage firebaseStorage;

    private DatabaseReference emergencyDatabaseReference;
    private ValueEventListener emergencyListener;

    private static final long NO_CONECTION_TIME = 1000 * 60 * 4; //--> Minutes
    private int status=0;
    private int checkEvery= 15;
    private DecimalFormat decimalFormat;

    /*private KmlLayer kmlLayer;
    private List<KmlPlacemark> placemarkList;*/

    private AppDbHelper dbHelper;
    private AppPreferences preferences;

    private ArrayList<HubConfig> hubConfigs;

    private ReceiveZoneUpdate receiveZoneUpdate;
    private IntentFilter filterZones;

    private Handler zoneHandler;
    private Runnable zoneRunnable;

    private Handler syncHandler;
    private Runnable syncRunnable;

    private Long sync;

    private boolean doomsday= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentEnviromentUrl = BkoDataMaganer.getEnviromentUrl(this);
        mapFrag.getMapAsync(this);
        BkoCore.setoffersInterfaceListener(this);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        setupTabs();
        mLayout.setTouchEnabled(false);
        decimalFormat= new DecimalFormat("#.##");
        dbHelper= App.getInstance().getDbHelper();
        preferences= App.getInstance().getPreferences();
        //placemarkList= new ArrayList<>();

        hubConfigs= new ArrayList<>();

        new SendNewTokenTask().execute(preferences.getFirebaseToken());

        firebaseDatabase= BkoFirebaseDatabase.getDatabase();
        //firebaseStorage= BkoFirebaseStorage.getStorage();

        emergencyDatabaseReference= firebaseDatabase.getReference().child("doomsday").child("activate");
        emergencyDatabaseReference.keepSynced(true);

        emergencyDatabaseReference.setValue(false);

        registerGpsStatusListener();

        /*FirebaseMessaging.getInstance().subscribeToTopic("highScores");
        FirebaseMessaging.getInstance().subscribeToTopic("use_cases");
        FirebaseMessaging.getInstance().subscribeToTopic("config");
        FirebaseMessaging.getInstance().subscribeToTopic("remote");*/

        filterZones= new IntentFilter(Constants.ACTION_SERVICE_ZONES);
        receiveZoneUpdate= new ReceiveZoneUpdate();

        zoneHandler= new Handler();
        zoneRunnable= new Runnable() {
            @Override
            public void run() {
                if (map!=null){
                    new GetKmlFromDbTask().execute();
                    /*if (hubConfigs.size()==0){
                        new GetKmlFromDbTask().execute();
                    }else {
                        if (!doomsday){
                            new GetKmlValuesFromServerTask().execute();
                        }
                    }*/
                }
            }
        };

        syncHandler= new Handler();
        syncRunnable= new Runnable() {
            @Override
            public void run() {
                if (map!=null){
                    Log.d("Kml_Shown","OK");
                    map.clear();
                    new GetTextMarkerFromHubConfigTask().execute(hubConfigs);
                    for (HubConfig hubConfig:hubConfigs){
                        map.addPolygon(hubConfig.getPolygonOptions());
                    }
                }
            }
        };

        /*try {
            BkoUser user = BkoUserDao.Consultar(this);
            if (user!=null){
                Log.d("FB_DB_User",user.getWorkerId());
                locationDatabaseReference= firebaseDatabase.getReference().child("worker").child(user.getWorkerId());
                locationDatabaseReference.keepSynced(true);
            }

        } catch (Exception e) {
            e.fillInStackTrace();
        }*/

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (tabId.equals("MAPA") && mapReady != null && mapReady) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setData();

                        }
                    }, 500);
                }
                else {
                    BkoTripDetailActivity.confirmed = true;
                }
            }
        });

        offerNavV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (BkoDataMaganer.getStatusService(BkoMainActivity.this) == Constants.SERVICE_STATUS_WORKER_ARRIVED) {
                    if (BkoDataMaganer.getCurrentTrip(BkoMainActivity.this) == null) {
                        return;
                    }
                    BkoChildTripVO trip = BkoDataMaganer.getCurrentTrip(BkoMainActivity.this);
                    showLocation(trip.getBko_customeraddress_latitude(), trip.getBko_customeraddress_longitude());
                } else {

                    if (firstAnnounce == null)
                        firstAnnounce = BkoDataMaganer.getCurrentOffer(BkoMainActivity.this);


                    showLocation(firstAnnounce.getBko_announcementaddress_lat(), firstAnnounce.getBko_announcementaddress_lng());

                }

            }
        });
        searchOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        BkoMainActivity.this, BkoOffersByDateActivity.class);
                startActivity(intent);
            }
        });
        announcementV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        BkoMainActivity.this, BkoOfferDetailActivity.class);
                BkoOfferDetailActivity.selectedItem = firstAnnounce;
                startActivityForResult(intent, 12);
            }
        });

        checkInSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                                 @Override
                                                 public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                                     if (BkoUtilities.isMockLocationOn(BkoMainActivity.this)) {
                                                         Toast.makeText(BkoMainActivity.this, "Desactive el uso de ubicaciones simuladas!", Toast.LENGTH_SHORT).show();
                                                         return;
                                                     }
                                                     Location location = BkoDataMaganer.getCurrentUserLocation(BkoMainActivity.this);
                                                     if (location == null) {
                                                         Toast.makeText(BkoMainActivity.this, "No se ha podido obtener su ubicación", Toast.LENGTH_SHORT).show();
                                                         return;
                                                     }
                                                     if (progress > 80) {
                                                         if ((SystemClock.elapsedRealtime() - mLastClickTime) < 1500) {
                                                             checkInSB.setProgress(10);
                                                             mLastClickTime = SystemClock.elapsedRealtime();
                                                             return;
                                                         }
                                                         mLastClickTime = SystemClock.elapsedRealtime();
                                                         if (!BkoUtilities.checkCorrectDate(BkoMainActivity.this)) {
                                                             checkInSB.setProgress(10);
                                                             return;
                                                         }
                                                         reportOfferStatus(2);
                                                     }
                                                 }

                                                 @Override
                                                 public void onStartTrackingTouch(SeekBar seekBar) {

                                                 }
                                                 @Override
                                                 public void onStopTrackingTouch(SeekBar seekBar) {

                                                 }
                                             }

        );


        checktoutSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                                   @Override
                                                   public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                                       if (progress > 80) {
                                                           if ((SystemClock.elapsedRealtime() - mLastClickTime) < 1500) {
                                                               checktoutSB.setProgress(10);
                                                               mLastClickTime = SystemClock.elapsedRealtime();
                                                               return;
                                                           }
                                                           mLastClickTime = SystemClock.elapsedRealtime();
                                                           if (!BkoUtilities.checkCorrectDate(BkoMainActivity.this)) {
                                                               checktoutSB.setProgress(10);
                                                               return;
                                                           }
                                                           checktoutSB.setEnabled(false);
                                                           if (firstAnnounce != null) {
                                                               firstAnnounce.setBko_announcementworker_onway("1");
                                                               firstAnnounce.setBko_announcementworker_checkin("1");
                                                           }
                                                           reportOfferStatus(3);
                                                       }
                                                   }
                                                   @Override
                                                   public void onStartTrackingTouch(SeekBar seekBar) {
                                                   }

                                                   @Override
                                                   public void onStopTrackingTouch(SeekBar seekBar) {

                                                   }
                                               }

        );


        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        autoNavCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                 @Override
                                                 public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                     if (isChecked) {
                                                         BkoDataMaganer.setAutoNavigate(BkoMainActivity.this, true);
                                                     } else {
                                                         BkoDataMaganer.setAutoNavigate(BkoMainActivity.this, false);
                                                     }

                                                 }
                                             }
        );

        //////////////////////////// APPLICATION MENU  ////////////////////////////
        try {
            user = BkoUserDao.Consultar(this);
            if (user != null) {
                iProfile = new ProfileDrawerItem().withIdentifier(0).withName(user.getName() + " " + user.getLastname()).withEmail(user.getEmail()).withEnabled(false).withIcon(ContextCompat.getDrawable(this, R.drawable.avatar));
                headerResult.addProfile(iProfile
                        , 0);
                if (user.getPicurl() != null && user.getPicurl().length() > 0) {
                    loadBitmap(user.getPicurl());
                }
                if (user.isAvailable()) {
                } else {
                    mToolbar.setBackgroundColor(ContextCompat.getColor(BkoMainActivity.this, R.color.blako_background));
                }
                if (BkoDataMaganer.getAutoNavigate(this)) {
                    autoNavCb.setChecked(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        buildDrawer();
        /////////////////////////////////////////////////////////////////////////
        //////////////////////////// VIEWS INSTANCES ////////////////////////////


        statusSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {


                onSwitchChecked( checked);
            }
        });

        addressOfferLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BkoDataMaganer.getStatusService(BkoMainActivity.this) == Constants.SERVICE_STATUS_WORKER_ARRIVED) {
                    if (BkoDataMaganer.getCurrentTrip(BkoMainActivity.this) == null) {
                        return;
                    }
                } else {
                    Intent intent = new Intent(
                            BkoMainActivity.this, BkoOfferDetailActivity.class);
                    BkoOfferDetailActivity.selectedItem = firstAnnounce;
                    BkoOfferDetailActivity.selectedItem.setPenaltys(penaltys);
                    startActivityForResult(intent, 12);
                }

            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAnnoucements();

            }
        });
        refreshIv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                refreshIv.setVisibility(View.GONE);
                noResultsTv.setVisibility(View.GONE);
                getAnnoucements();
            }
        });
        buildGoogleApiClient();
        createLocationRequest();
        try {
            registerReceiver(mPushReceiver, new IntentFilter("unique_name"));
            registerReceiver(internetBroadCast, mIntentFilter);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        checkForUpdates();

        /*if(Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(BkoMainActivity.this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1234);
            }
        }
        else
        {
            Intent intentOverlayButton= new Intent(BkoMainActivity.this, OverlayButtonService.class);
            startService(intentOverlayButton);
        }*/
    }

    private class SendNewTokenTask extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... strings) {
            Map<String, String> mapVisible = new HashMap<String, String>();
            mapVisible.put("workerId", BkoDataMaganer.getWorkerId(BkoMainActivity.this));
            mapVisible.put("token", strings[0]);
            String tokenServiceResponse = HttpRequest.post(Constants.GET_UPDATE_TOKEN(BkoMainActivity.this), mapVisible, true).connectTimeout(5000).readTimeout(5000).body();
            Log.d("Token_Refreshed", tokenServiceResponse);
            Log.d("Token_String", strings[0]);
            return null;
        }
    }

    private class ReceiveZoneUpdate extends BroadcastReceiver {

        public ReceiveZoneUpdate() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent!=null){
                int jitter= 50;
                sync= (System.currentTimeMillis() + (1000*60*3))/1000;
                try{
                    jitter= intent.getExtras().getInt("jitter");
                    sync= intent.getExtras().getLong("sync");
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
                Log.d("Kml_Jitter",String.valueOf(jitter));
                Log.d("Kml_Sync",String.valueOf(sync));
                int delayValue= ThreadLocalRandom.current().nextInt(0, jitter + 1);
                zoneHandler.postDelayed(zoneRunnable,delayValue*1000);
            }
        }
    }

    private class GetKmlFromDbTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            hubConfigs.clear();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ArrayList<Hub> hubs= dbHelper.getAllHubs();
            ArrayList<HubPoints> points= new ArrayList<>();
            for (Hub hub:hubs){
                points.clear();
                HubDefaultValue defaultValue= dbHelper.getHubDefaultValueByHubId(hub.getHubId());
                PolygonOptions kmlHubDefault= HubUtils.getDefaultPolygonOptions(BkoMainActivity.this,defaultValue.getValue());
                points= dbHelper.getAllPointsByHubId(hub.getHubId());
                for (HubPoints point:points){
                    kmlHubDefault.add(new LatLng(point.getLat(),point.getLon()));
                }
                hubConfigs.add(new HubConfig(Integer.parseInt(hub.getHubId()),hub.getLabel(),defaultValue.getRate(),0,kmlHubDefault,hub.getRegionId()));
            }
            Location location= BkoDataMaganer.getCurrentUserLocation(BkoMainActivity.this);
            if (location!=null){
                LatLng latLngLocation= new LatLng(location.getLatitude(),location.getLongitude());
                int actualRegionId= DeliveryZoneCheck.getActualRegionFromLocation(hubConfigs,latLngLocation);
                Log.d("Kml_Region_Local", String.valueOf(actualRegionId));
                Log.d("Kml_Region_Push", String.valueOf(preferences.getHubsRegionId()));
                if (actualRegionId!=0){
                    hubConfigs= HubUtils.filterHubConfigsByRegion(hubConfigs,actualRegionId);
                }else if (preferences.getHubsRegionId()!=0){
                    hubConfigs= HubUtils.filterHubConfigsByRegion(hubConfigs, preferences.getHubsRegionId());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (doomsday){
                if (map!=null){
                    Log.d("Kml_Shown","OK");
                    map.clear();
                    new GetTextMarkerFromHubConfigTask().execute(hubConfigs);
                    for (HubConfig hubConfig:hubConfigs){
                        map.addPolygon(hubConfig.getPolygonOptions());
                    }
                }
            }else {
                new GetKmlValuesFromServerTask().execute();
            }
        }
    }

    private class GetKmlValuesFromServerTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                String geoHash= "9g3qw";
                Location actualLocation= BkoDataMaganer.getCurrentUserLocation(BkoMainActivity.this);
                if (actualLocation!=null){
                    geoHash= LocationUtils.getGeoHash(actualLocation, 9);
                }
                String endpointValues= "/"+BkoDataMaganer.getWorkerId(BkoMainActivity.this)+"/"+geoHash;
                Log.d("Kml_Url","https://zones-dot-blako-support.appspot.com/rates"+endpointValues);
                String kmlResponse = HttpRequest
                        .get("https://zones-dot-blako-support.appspot.com/rates"+endpointValues)
                        .connectTimeout(5000).readTimeout(5000).body();

                if (kmlResponse!=null){
                    Log.d("Kml_Response",kmlResponse);
                    JSONObject responseObject= new JSONObject(kmlResponse);
                    if (responseObject.getBoolean("success")){
                        JSONObject zonesObject= responseObject.getJSONObject("zones");
                        JSONArray ratesArray= zonesObject.getJSONArray("rates");
                        for (int i=0;i<ratesArray.length();i++){
                            JSONObject rateObject= ratesArray.getJSONObject(i);
                            for (HubConfig hub:hubConfigs){
                                if (hub.getHubId()==rateObject.getInt("id")){
                                    PolygonOptions options= hub.getPolygonOptions();
                                    HubUtils.changePolygonOptionsValues(BkoMainActivity.this,options,rateObject.getInt("value"));
                                    hub.setRate(rateObject.getDouble("base"));
                                    hub.setRateExtra(rateObject.getDouble("diff"));
                                    hub.setPolygonOptions(options);
                                }
                            }
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            syncHandler.removeCallbacks(syncRunnable);
            if (sync==null){
                if (map!=null){
                    Log.d("Kml_Shown","OK");
                    map.clear();
                    new GetTextMarkerFromHubConfigTask().execute(hubConfigs);
                    for (HubConfig hubConfig:hubConfigs){
                        map.addPolygon(hubConfig.getPolygonOptions());
                    }
                }
            }else {
                syncHandler.postDelayed(syncRunnable,ZoneUpdate.delay(sync));
            }
        }
    }

    private class GetTextMarkerFromHubConfigTask extends AsyncTask<ArrayList<HubConfig>,Void,ArrayList<MarkerOptions>>{

        @Override
        protected ArrayList<MarkerOptions> doInBackground(ArrayList<HubConfig>... hubs) {
            ArrayList<MarkerOptions> markerOptions= new ArrayList<>();
            for (HubConfig hub:hubs[0]){
                PolygonOptions options= hub.getPolygonOptions();
                LatLng markerLocation= KmlColorTempUtil.getCenterOfPolygon(options.getPoints());
                markerOptions.add(KmlColorTempUtil.createTextMarkerOptions(BkoMainActivity.this,map,markerLocation, FormatUtils.moneyFormat(hub.getRate()+hub.getRateExtra()),3,19));
            }
            return markerOptions;
        }

        @Override
        protected void onPostExecute(ArrayList<MarkerOptions> markerOptions) {
            for (MarkerOptions marker:markerOptions){
                map.addMarker(marker);
            }
        }
    }

    /*private class GetKmlJSONFromServiceTask extends AsyncTask<Void,Void,JSONObject>{

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try{
                String geoHash= "9g3qw";
                Location actualLocation= BkoDataMaganer.getCurrentUserLocation(BkoMainActivity.this);
                if (actualLocation!=null){
                    geoHash= LocationUtils.getGeoHash(actualLocation, 9);
                }
                String endpointValues= "/"+BkoDataMaganer.getWorkerId(BkoMainActivity.this)+"/"+geoHash;
                Log.d("Kml_Url","https://zones-dot-blako-support.appspot.com/zones"+endpointValues);
                String kmlResponse = HttpRequest
                        .get("https://zones-dot-blako-support.appspot.com/zones"+endpointValues)
                        .connectTimeout(5000).readTimeout(5000).body();

                if (kmlResponse!=null){
                    Log.d("Kml_Response",kmlResponse);
                    return new JSONObject(kmlResponse);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject!=null){
                new GetKmlFromJSONTask().execute(jsonObject);
            }
        }
    }*/

    /*private class GetKmlFromJSONTask extends AsyncTask<JSONObject,Void,Void>{

        @Override
        protected void onPreExecute() {
            kmlHubs.clear();
            kmlLabelsString.clear();

            kmlHubIds.clear();
            kmlHubPolygons.clear();
        }

        @Override
        protected Void doInBackground(JSONObject... jsonObjects) {
            try {
                if (jsonObjects[0].getBoolean("success")){
                    JSONArray fencesArray= jsonObjects[0].getJSONObject("zones").getJSONArray("fences");
                    for (int i=0;i<fencesArray.length();i++){
                        JSONObject kmlObject= fencesArray.getJSONObject(i);
                        JSONArray latArray= kmlObject.getJSONArray("lats");
                        JSONArray longArray= kmlObject.getJSONArray("lons");
                        PolygonOptions kmlHub= new PolygonOptions();
                        int colorAlpha= 70; //< 0 - 255 >
                        int temp= kmlObject.getInt("value");
                        int colorStroke= KmlColorTempUtil.getColorByTempValue(BkoMainActivity.this,temp);
                        int colorFill= Color.argb(colorAlpha,Color.red(colorStroke),Color.green(colorStroke),Color.blue(colorStroke));
                        kmlHub.strokeColor(colorStroke).fillColor(colorFill);
                        kmlHub.strokeWidth(5);
                        for (int j=0;j<latArray.length();j++){
                            kmlHub.add(new LatLng(latArray.getDouble(j),longArray.getDouble(j)));
                        }
                        kmlHubIds.add(kmlObject.getInt("id"));
                        kmlHubs.add(kmlHub);
                        String rate;
                        try {
                            rate= "$ "+decimalFormat.format(Double.parseDouble(kmlObject.getString("rate")));
                        }catch (NumberFormatException e){
                            e.printStackTrace();
                            rate= "$ "+kmlObject.getString("rate");
                        }
                        kmlLabelsString.add(rate);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            syncHandler.removeCallbacks(syncRunnable);
            if (sync==null){
                Log.d("Kml_Sync","500");
                syncHandler.postDelayed(syncRunnable,500);
            }else {
                syncHandler.postDelayed(syncRunnable,ZoneUpdate.delay(sync));
            }
        }
    }*/

    /*private class GetTextMarkerFromJSONTask extends AsyncTask<ArrayList<PolygonOptions>,Void,Void>{

        @Override
        protected void onPreExecute() {
            kmlLabels.clear();
        }

        @Override
        protected Void doInBackground(ArrayList<PolygonOptions>... arrayLists) {
            for (int i=0;i<arrayLists[0].size();i++){
                PolygonOptions hub= arrayLists[0].get(i);
                LatLng labelLocation= KmlColorTempUtil.getCenterOfPolygon(hub.getPoints());
                kmlLabels.add(KmlColorTempUtil.createTextMarkerOptions(BkoMainActivity.this,map,labelLocation,kmlLabelsString.get(i),3,19));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            for (MarkerOptions marker:kmlLabels){
                map.addMarker(marker);
            }
        }
    }*/

    private void onSwitchChecked(boolean checked) {
        if(disconecting)
            return;

        try {
            user = BkoUserDao.Consultar(BkoMainActivity.this);
            if (user.isAvailable()) {
                user.setIsAvailable(true);
                BkoUserDao.Actualizar(BkoMainActivity.this, user);
            } else {
                user.setIsAvailable(false);
                BkoUserDao.Actualizar(BkoMainActivity.this, user);
            }

            if(checked) {

                onDemandOn();

                //mTabHost.setCurrentTab(0);

                /*if(BkoDataMaganer.getOnDemand(BkoMainActivity.this)){
                    mTabHost.setCurrentTab(1);
                    mTabHost.setCurrentTab(0);

                } else {
                    mTabHost.setCurrentTab(1);

                    displayLocation(BkoDataMaganer.getCurrentUserLocation(BkoMainActivity.this), 17, null);
                }*/
                mTabHost.setCurrentTab(1);
                displayLocation(BkoDataMaganer.getCurrentUserLocation(BkoMainActivity.this), Constants.DEFAULT_MAP_ZOOM, null);

                BkoDataMaganer.setOnDemand( BkoMainActivity.this, true);
                sendOneLodation();
                //BkoSendLocationToServer.oneLocationDataToWebsite(BkoDataMaganer.getCurrentUserLocation(getApplicationContext()), getApplicationContext());
            }else  {

                onDemandOff();

            }
            setLocationsAlarms();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    @Override
    protected void onResume() {
        super.onResume();
        Log.d("GeneralFlux","Activity: "+BkoMainActivity.this.getLocalClassName());
        if (mapReady && preferences.getSycTime()!=0 && System.currentTimeMillis()>(preferences.getSycTime()*1000)){
            preferences.setSycTime(0);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new GetKmlFromDbTask().execute();
                }
            },800);
        }
        attachEmergencyListener();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiveZoneUpdate,filterZones);
        BkoCore.setoffersInterfaceListener(this);
        if (mGoogleApiClient == null)
            return;
        mGoogleApiClient.connect();
        drawerMenu.setSelection(1);
        int status = BkoDataMaganer.getStatusService(BkoMainActivity.this);
        if (status == Constants.SERVICE_STATUS_FREE ) {
            if (onOfferToChekin != null && !onOfferToChekin)
                getAnnoucements();
        }
        if(!BkoDataMaganer.getOnDemand(this) && BkoDataMaganer.getCurrentOffer(this)==null)
            dragView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 12) {
            if (resultCode == Activity.RESULT_OK) {
                statusSw.setVisibility(View.GONE);
                dragView.setVisibility(View.GONE);
                getAnnoucements();
            }

        } else if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                confirmTv.setEnabled(false);
                confirmTv.setTextColor(ContextCompat.getColor(this, R.color.blako_gray_low));
                okComfirmTv.setVisibility(View.VISIBLE);
                arrowComfirmTv.setVisibility(View.GONE);
                confirmTv.setText(getString(R.string.blako_destino_orden_confirmada));
            }
        }

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

    }
    ////////////////////////////////////////////////////////WORKER///////////////////////////////////////////////////////
    //////////////////////////////////////////////////// PUSH NOTIFICATIONS  ///////////////////////////////////////////////////////
    public static void updateMyActivity(Context context, String message, String type) {
        Intent intent = new Intent("unique_name");
        intent.putExtra("message", message);
        intent.putExtra("type", type);
        context.sendBroadcast(intent);
    }

    private BroadcastReceiver mPushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {

            try {
                String pushType = intent.getStringExtra("type");
                if (pushType.equals("updateApp")) {
                    checkForUpdates();
                } else if (pushType.equals("gpsSended")) {
                    if (internetProblem) {
                        statusTV.setText(selectedWareHouse());
                        mToolbar.setBackgroundColor(ContextCompat.getColor(BkoMainActivity.this, R.color.blako_background));
                        internetProblem = false;
                    }

                } else if (pushType.equals("withOutSended")) {
                    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(BkoMainActivity.this)
                            .setSmallIcon(R.drawable.motorcycle)
                            .setContentTitle("Blako")
                            .setContentText("SIN CONEXIÓN")
                            .setAutoCancel(true);
                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification notification = notificationBuilder.build();
                    if (!internetProblem) {
                        if (BkoDataMaganer.getCurrentOffer(BkoMainActivity.this) != null) {
                            notificationBuilder.setSound(defaultSoundUri);
                            Toast.makeText(BkoMainActivity.this, "SIN ENVIAR POSICIÓN", Toast.LENGTH_LONG).show();
                            statusTV.setText(getString(R.string.blako_sin_enviar));
                            mToolbar.setBackgroundColor(ContextCompat.getColor(BkoMainActivity.this, R.color.blako_red_two));
                            internetProblem = true;
                            Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            vib.vibrate(1500);
                        }
                    }
                    notificationManager.notify(0 /*     ID of notification */, notification);
                } else if (pushType.equals("finish")) {
                    finishAffinity();
                }  else if (pushType.equals("finisoffer")) {
                    finishAffinity();
                    Intent intentMain = new Intent(BkoMainActivity.this, BkoMainActivity.class);
                    intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intentMain);
                } else if (pushType.equals("updateAnnouncement")) {
                    finishAffinity();
                    Intent intentMain = new Intent(BkoMainActivity.this, BkoMainActivity.class);
                    intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intentMain);
                } else if (pushType.equals("logout")) {

                } else if (pushType.equals("consulttrips")) {
                    finishAffinity();
                    Intent intentMain = new Intent(BkoMainActivity.this, BkoMainActivity.class);
                    intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intentMain);

                } else if (pushType.equals("cancel")) {



                } else if (pushType.equals("zoomBearing")) {
                    try {
                        float Bearing = Float.parseFloat(intent.getStringExtra("message"));
                        if (Bearing != 0.0) {
                            Log.d("bearing", "" + Bearing);
                        }
                        if (BkoDataMaganer.getAutoNavigate(BkoMainActivity.this))
                            displayLocation(BkoDataMaganer.getCurrentUserLocation(BkoMainActivity.this), Constants.DEFAULT_MAP_ZOOM, Bearing);
                    } catch (Exception e) {
                    }
                } else if (pushType.equals("loadAnnouncement")) {
                    getAnnoucements();
                } else if (pushType.equals("location")) {
                    checkPositionOnRatio();
                } else if (pushType.equals("updateQueuedTasks")) {
                    updateQueuedTasks();
                }
            } catch (Exception e) {
            }
        }
    };

    private void onArriveBySeek() {
        BkoTripsInProgressFragment.onTripsList = true;
        mapHeaderView.setVisibility(View.VISIBLE);
        Integer dp = (int) BkoUtilities.convertDpToPixel(60, this);
        relativeMap.setPadding(0, dp, 0, 0);
        addressTv.setVisibility(View.GONE);
        googlemapsBt.setVisibility(View.GONE);
        wazeBt.setVisibility(View.GONE);
        refreshRouteIv.setVisibility(View.GONE);
        dragView.setVisibility(View.GONE);
        statusSw.setVisibility(View.GONE);
        statusTV.setText(selectedWareHouse());
        mToolbar.setBackgroundColor(ContextCompat.getColor(BkoMainActivity.this, R.color.blako_background));
        mTabHost.setVisibility(View.VISIBLE);
        BkoDataMaganer.setStatusService(Constants.SERVICE_STATUS_WORKER_ARRIVED, BkoMainActivity.this);
        BkoDataMaganer.setCurrentTrip(null, BkoMainActivity.this);
    }


    private void recoverAppStateifService() {
        int appState = BkoDataMaganer.getStatusService(this);

        switch (appState) {
            case Constants.SERVICE_STATUS_FREE:
                onFree();
                break;
            case Constants.SERVICE_STATUS_WORKER_ARRIVED:
                onWorkerArrived();
                break;
            case Constants.SERVICE_STATUS_TRIP_FINISHED:
                onTripFinished();
                break;
            case Constants.SERVICE_STATUS_WITH_QUEUEDTASKS:
                updateQueuedTasks();
                break;
            case Constants.SERVICE_STATUS_WITH_OFFER:
                getAnnoucements();
                break;
            case Constants.SERVICE_STATUS_WITH_OFFER_CHECKIN_FINISH:
                offerCheckinFinish();
                break;
            default:
                break;
        }
    }

    ////////////////////////////////////////////////////////////////// CLEAR DATA    //////////////////////////////////////////////////////////////////
    private void clearData(boolean cleanPrivate) {
        try {
            BkoDataMaganer.setCurrentOffer(null, this);
            BkoDataMaganer.setStatusRequest(null, this);
            BkoDataMaganer.setCurrentTrips(null, this);
            BkoDataMaganer.setCurrentTrip(null, this);
            BkoDataMaganer.setCuurentTemporaryTrip(null);

            if (cleanPrivate) {
                BkoDataMaganer.setWareHouseSelected(null, this);
                BkoUser user = BkoUserDao.Consultar(this);
                user.setWareHouseIdSelected(null);
                user.setWareHouseSelected(null);
                user.setPrivateCheckIn(null);
                BkoUserDao.Actualizar(this, user);
            }
        } catch (Exception e) {

        }
    }
    ////////////////////////////////////////////////////////////////// RECOVER STATE    //////////////////////////////////////////////////////////////////
    private void updateQueuedTasks() {
        showWaitDialogWhileExecuting("Cargando...", new Runnable() {
            public void run() {
                try {
                    statusResponse = null;
                    statusResponse = HttpRequest
                            .get(BkoDataMaganer.getEnviromentUrl(BkoMainActivity.this) + "/trips/getlastorderandtripsactive?workerId=" + user.getWorkerId())
                            .connectTimeout(8000).body();
                    Gson gson = new Gson();
                    if (statusResponse != null) {
                        recoverStatusVO = gson.fromJson(statusResponse, BkoRecoverStatusVO.class);
                    }
                } catch (Exception e) {
                }
            }
        }, new Runnable() {
            public void run() {
                if (recoverStatusVO != null) {
                    try {
                        if (recoverStatusVO.isResponse()) {
                            BkoLoginActivity.setData(null, recoverStatusVO, BkoMainActivity.this);
                            firstAnnounce = BkoDataMaganer.getCurrentOffer(BkoMainActivity.this);
                            if (firstAnnounce != null) {
                                BkoDataMaganer.setStatusService(Constants.SERVICE_STATUS_WORKER_ARRIVED, BkoMainActivity.this);
                            }
                            recoverAppStateifService();
                        } else {
                            if (!recoverStatusVO.getMessage().equals(getString(R.string.response_no_parameters))){
                                Toast.makeText(BkoMainActivity.this, recoverStatusVO.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            BkoDataMaganer.setStatusService(Constants.SERVICE_STATUS_FREE, BkoMainActivity.this);
                        }
                    } catch (Exception e) {
                        AlertaTextView(getString(R.string.blako_advertencia), getString(R.string.blako_error_tareas_encoladas), BkoMainActivity.this,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        updateQueuedTasks();
                                    }
                                }, getString(R.string.blako_aceptar), null);
                        Toast.makeText(BkoMainActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    AlertaTextView(getString(R.string.blako_advertencia), getString(R.string.blako_error_tareas_encoladas), BkoMainActivity.this,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    updateQueuedTasks();
                                }
                            }, getString(R.string.blako_aceptar), null);
                    Toast.makeText(BkoMainActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    private void onTripFinished() {
        mapHeaderView.setVisibility(View.VISIBLE);
        mTabHost.setVisibility(View.VISIBLE);;
        Integer dp = (int) BkoUtilities.convertDpToPixel(60, this);
        relativeMap.setPadding(0, dp, 0, 0);
        BkoDataMaganer.setCurrentTrip(null, this);
        statusSw.setVisibility(View.GONE);
        mToolbar.setBackgroundColor(ContextCompat.getColor(BkoMainActivity.this, R.color.blako_background));
        statusTV.setText(selectedWareHouse());
        dragView.setVisibility(View.GONE);
    }

    private void onFree() {
        statusTV.setText(getString(R.string.blako_offers_trabajos));
        clearData(true);
        BkoDataMaganer.setStatusService(Constants.SERVICE_STATUS_FREE, this);
        addressTv.setVisibility(View.GONE);
        addressSearchLl.setVisibility(View.INVISIBLE);
        addressDeliveryLl.setVisibility(View.GONE);
        refreshRouteIv.setVisibility(View.INVISIBLE);
        navigationLl.setVisibility(View.INVISIBLE);
        orderTv.setVisibility(View.INVISIBLE);
        orderTv.setText("");
        announcementV.setVisibility(View.GONE);
        mapHeaderView.setVisibility(View.GONE);
        //mTabHost.setVisibility(View.GONE);
        if (clientMarker != null) {
            clientMarker.remove();
        }
        tripsLl.setVisibility(View.GONE);
        addressSearchLl.setVisibility(View.VISIBLE);

        try {
            BkoUser user = BkoUserDao.Consultar(this);
            BkoVehicleVO vehicle = BkoDataMaganer.getCurrentVehicle(this);
            Log.d("SVL_Vehicle",vehicle.getTypeid());
            if (user != null && vehicle != null) {
                workerNameTv.setText(user.getName() + " " + user.getLastname());
                workerVehicleTv.setText(vehicle.getBrand() + " " + vehicle.getModel());
                visibilityTv.setText(user.getVisibilitytype());
                getAnnoucements();
                if (user.isAvailable()) {
                    user.setIsAvailable(true);
                    BkoUserDao.Actualizar(BkoMainActivity.this, user);
                } else {
                    user.setIsAvailable(false);
                    BkoUserDao.Actualizar(BkoMainActivity.this, user);
                }
            }
            map.clear();
            new GetTextMarkerFromHubConfigTask().execute(hubConfigs);
            for (HubConfig hubConfig:hubConfigs){
                map.addPolygon(hubConfig.getPolygonOptions());
            }
            /*if (kmlLayer!=null && map!=null){
                try {
                    kmlLayer.addLayerToMap();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }*/
        } catch (Exception e) {

        }
    }

    private void onDisconectedOnDemand() {
        disconecting = true;
        showWaitDialogWhileExecuting("Desconectando...", new Runnable() {
            public void run() {
                try {
                    tripsRespone = null;
                    Gson gson = new Gson();

                    statusRequest = BkoDataMaganer.getCurrentStatusRequest(BkoMainActivity.this);
                    BkoUser user = BkoUserDao.Consultar(BkoMainActivity.this);
                    statusResponse = HttpRequest
                            .get(Constants.GET_TRIPS_ACTIVE(BkoMainActivity.this) + "workerId=" + user.getWorkerId())
                            .connectTimeout(4000).readTimeout(4000).body();

                    if (statusResponse != null) {
                        recoverStatusVO = gson.fromJson(statusResponse, BkoRecoverStatusVO.class);
                        if (recoverStatusVO.isResponse()) {
                            BkoLoginActivity.setData(null, recoverStatusVO,BkoMainActivity.this);
                            statusRequest = BkoDataMaganer.getCurrentStatusRequest(BkoMainActivity.this);
                        }
                    }


                    if (statusRequest != null) {

                        String tripsReponse = HttpRequest
                                .get(Constants.GET_GO_TRIPS(BkoMainActivity.this) + "oId=" + statusRequest.getOid() + "&workerId=" + user.getWorkerId())
                                .connectTimeout(5000).readTimeout(5000).body();
                        if (tripsReponse != null) {
                            tripsRespone = gson.fromJson(tripsReponse, BkoTrips.class);

                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Runnable() {
            public void run() {
                if (tripsRespone != null) {
                    try {
                        if ((tripsRespone.getTrips() != null && tripsRespone.getTrips().size() != 0)) {
                            if (BkoTripsAdapter.mp != null && BkoTripsAdapter.mp.isPlaying())
                                BkoTripsAdapter.mp.stop();

                            boolean valid = true;

                            for(BkoTripVO tripPArent: tripsRespone.getTrips()){
                                BkoChildTripVO trip = tripPArent.getOrigen().get(0);
                                BkoChildTripVO tripDelivery = tripPArent.getDestino().get(0);
                                if (trip.getBko_orders_trips_completeddatetime() != null && trip.getBko_orders_trips_completeddatetime().length() != 0) {
                                    valid = false;
                                    break;
                                } else {
                                    if (tripDelivery.getBko_requeststatus_id() != null && tripDelivery.getBko_requeststatus_id().equals("2")) {
                                        valid = false;
                                        break;
                                    } else {

                                    }
                                }
                            }

                            if(!valid){
                                statusSw.setChecked(true);
                                AlertaTextView(getString(R.string.blako_advertencia), getString(R.string.blako_en_servicio_activos), BkoMainActivity.this,
                                        null, getString(R.string.blako_aceptar), null);
                            } else {
                                finishDisconectOnDemand();
                            }

                        } else {
                            if (BkoTripsAdapter.mp != null && BkoTripsAdapter.mp.isPlaying())
                                BkoTripsAdapter.mp.stop();
                            finishDisconectOnDemand();
                        }


                    } catch (Exception e) {
                        Toast.makeText(BkoMainActivity.this, R.string.blako_error, Toast.LENGTH_SHORT).show();
                        statusSw.setChecked(true);
                    }

                } else {
                    if(recoverStatusVO!=null && !recoverStatusVO.isResponse()  ){
                        finishDisconectOnDemand();
                    }else{
                        Toast.makeText(BkoMainActivity.this, R.string.blako_error, Toast.LENGTH_SHORT).show();
                        statusSw.setChecked(true);
                    }

                }

                disconecting = false;
            }
        });

    }


    private void onWorkerArrived() {
        onArriveBySeek();
        dragView.setVisibility(View.GONE);
        map.clear();
    }
    private String selectedWareHouse() {
        try {
            BkoUser user = BkoUserDao.Consultar(this);
            if (user.getVisibilitytype().equals("Publico")) {
                firstAnnounce = BkoDataMaganer.getCurrentOffer(this);
                if (firstAnnounce == null && BkoDataMaganer.getStatusService(this) == Constants.SERVICE_STATUS_WORKER_ARRIVED) {
                    getAnnoucements();
                }
                statusTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intentOwnOffers = new Intent(BkoMainActivity.this, BkoOwnOffersActivity.class);
                        startActivity(intentOwnOffers);
                    }
                });
            }
            if (BkoDataMaganer.getCurrentOffer(this) != null) {
                String typeTv;
                String rangeTime;
                if (BkoDataMaganer.getCurrentOffer(this).getBko_announcement_finishstatus().equals("1")) {
                    typeTv = "TURNO";
                    rangeTime =
                            BkoUtilities.formateDateFromstring(BkoDataMaganer.getCurrentOffer(this).getBko_announcement_datetimestart()).replace(":00 ", " ").toUpperCase().replace("PM", "").replace("P.M.", "").replace("AM", "").replace("A.M.", "") + " - " +
                                    BkoUtilities.formateDateFromstring(BkoDataMaganer.getCurrentOffer(this).getBko_announcement_datetimefinish()).replace(":00 ", " ");
                } else {
                    typeTv = "CARGA";
                    rangeTime =
                            BkoUtilities.formateDateFromstring(BkoDataMaganer.getCurrentOffer(this).getBko_announcement_datetimestart()).replace(":00 ", " ");
                }
                return "" + typeTv + " " + rangeTime + " \n" + BkoDataMaganer.getCurrentOffer(this).getBko_announcementaddress_alias();
            }
            if (user.getWareHouseSelected() != null)
                return user.getWareHouseSelected();
            else
                return "";
        } catch (Exception ex) {

        }
        return "";
    }


    //////////////////////////////////////////////////////// ROUTE ////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////// ACTIONS ////////////////////////////////////////////////////////////////////////////////////




    private void onLogout(final boolean finish) {
        try {
            BkoUser user = BkoUserDao.Consultar(BkoMainActivity.this);
            if (!user.isAvailable() && finish) {
                clearData(true);
                BkoDataMaganer.setStatusService(Constants.SERVICE_STATUS_FREE, this);
                Intent intent = new Intent(BkoMainActivity.this, BkoVehiclesActivity.class);
                BkoDataMaganer.setCurrentVehicle(null, BkoMainActivity.this);
                intent.putExtra("onRecover", true);
                startActivity(intent);
                finishAffinity();
                return;
            }

        } catch (Exception e) {

        }
        showWaitDialogWhileExecuting("Cargando...", new Runnable() {
            public void run() {

                try {
                    visibleReponse = null;
                    BkoUser user = BkoUserDao.Consultar(BkoMainActivity.this);
                    if (user != null) {
                        String timeregister = BkoUtilities.nowCurrent();
                        Map<String, String> mapVisible = new HashMap<String, String>();
                        mapVisible.put("workerEmail", user.getEmail());
                        if (user.isAvailable())
                            mapVisible.put("status", "0");
                        else
                            mapVisible.put("status", "1");
                        mapVisible.put("workerId", user.getWorkerId());
                        mapVisible.put("timeregister", timeregister);
                        visibleReponse = HttpRequest.get(Constants.GET_SET_VISIBLE_URL(BkoMainActivity.this), mapVisible, true).connectTimeout(5000).readTimeout(5000).body();
                        Location location = BkoDataMaganer.getCurrentUserLocation(BkoMainActivity.this);
                        String partnerId = user.getWorkerParentId();
                        if (partnerId == null || partnerId.equals("0"))
                            partnerId = user.getWorkerId();
                        else
                            partnerId = user.getWorkerParentId();
                        String responseUploadPostion = HttpRequest.get(Constants.GET_UPLOAD_GPS_POSITION(BkoMainActivity.this) + "workerEmail=" + user.getEmail() + "&name="
                                + user.getEmail() + "&lastname=" + user.getLastname() + "&status=" + "available" + "&file=" + "h" + "&Latitud=" + location.getLatitude()
                                + "&Longitud=" + location.getLongitude() + "&oid=" + "0" + "&banderaEta=" + "0" + "&workerId=" + user.getWorkerId() + "&hasStarted=" + "0"
                                + "&customerId=" + user.getWorkerId() + "&networkLocation=" + location.getProvider() + "&partnerId=" + partnerId + "&visibilityType=" + user.getVisibilitytype() + "").connectTimeout(2000).readTimeout(2000).body();

                        if (responseUploadPostion != null) {

                        }
                    }
                } catch (Exception e) {

                }
            }
        }, new Runnable() {
            public void run() {
                if (visibleReponse != null) {
                    try {
                        Gson gson = new Gson();
                        BkoUserStatusResponse response = gson.fromJson(visibleReponse, BkoUserStatusResponse.class);
                        if (response != null) {
                            if (response.isResponse()) {
                                BkoUser user = BkoUserDao.Consultar(BkoMainActivity.this);
                                user.setIsAvailable(!user.isAvailable());
                                if (!finish) {
                                    if (user.isAvailable()) {
                                        mToolbar.setBackgroundColor(ContextCompat.getColor(BkoMainActivity.this, R.color.blako_background));
                                        statusTV.setText(getString(R.string.blako_conectado));
                                    } else {
                                        mToolbar.setBackgroundColor(ContextCompat.getColor(BkoMainActivity.this, R.color.blako_background));
                                        statusTV.setText(getString(R.string.blako_desconectado));
                                    }
                                }
                                BkoUserDao.Actualizar(BkoMainActivity.this, user);
                                if (finish) {
                                    BkoDataMaganer.setCurrentVehicle(null, BkoMainActivity.this);
                                    Intent intent = new Intent(BkoMainActivity.this, BkoVehiclesActivity.class);
                                    intent.putExtra("onRecover", true);
                                    startActivity(intent);
                                    finishAffinity();
                                    clearData(true);
                                    BkoDataMaganer.setStatusService(Constants.SERVICE_STATUS_FREE, BkoMainActivity.this);
                                }
                            } else {
                                if (!response.getMessage().equals(getString(R.string.response_no_parameters))){
                                    Toast.makeText(BkoMainActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(BkoMainActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(BkoMainActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(BkoMainActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();

                }

            }
        });

    }


    private void onDemandOn() {
        googlemapsBt.setVisibility(View.GONE);
        wazeBt.setVisibility(View.GONE);
        dragView.setVisibility(View.GONE);
        offerNavIv.setColorFilter(ContextCompat.getColor(this, R.color.blako_green), PorterDuff.Mode.MULTIPLY);
        clientTv.setVisibility(View.GONE);
        onwaySB.setVisibility(View.GONE);
        checkInSB.setVisibility(View.GONE);
        addressOfferLl.setVisibility(View.GONE);
        mTabHost.setVisibility(View.VISIBLE);
        announcementV.setVisibility(View.GONE);
        refreshRouteIv.setVisibility(View.GONE);
        Integer dp = (int) BkoUtilities.convertDpToPixel(60, this);
        relativeMap.setPadding(0, dp, 0, 0);
        addressTv.setVisibility(View.GONE);
        BkoUser user = null;
        try {
            user = BkoUserDao.Consultar(this);
            user.setIsAvailable(true);
            mToolbar.setBackgroundColor(ContextCompat.getColor(BkoMainActivity.this, R.color.blako_background));
            statusTV.setText(getString(R.string.blako_conectado));
            BkoUserDao.Actualizar(this, user);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void onDemandOff() {
        onDisconectedOnDemand();
    }

    private void finishDisconectOnDemand() {
        BkoDataMaganer.setOnDemand( BkoMainActivity.this, false);
        dragView.setVisibility(View.VISIBLE);
        if (BkoTripsAdapter.mp != null && BkoTripsAdapter.mp.isPlaying())
            BkoTripsAdapter.mp.stop();


        onFree();

        try {
            user = BkoUserDao.Consultar(BkoMainActivity.this);
            user.setIsAvailable(false);
            BkoUserDao.Actualizar(this,user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////// OFFER //////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public void onLogoutListener(boolean status) {
        onLogout(status);
    }

    @Override
    public void onCheckInListener(BkoOffer.BkoAnnoucement selectedItem) {
        dragView.setVisibility(View.GONE);
        withOfferTaked(selectedItem);
    }

    @Override
    public void onOfferListener(BkoOffer.BkoAnnoucement selectedItem) {
        Intent intent = new Intent(
                this, BkoOfferDetailActivity.class);
        BkoOfferDetailActivity.selectedItem = selectedItem;
        BkoOfferDetailActivity.selectedItem.setPenaltys(penaltys);
        startActivityForResult(intent, 12);
    }

    @Override
    public void onCancelListener(final BkoOffer.BkoAnnoucement selectedItem) {
        AlertaTextViewListener(getString(R.string.blako_advertencia), getString(R.string.blako_cancelar_oferta), this,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onCancelOffer(selectedItem.getBko_announcement_id(), selectedItem.getBko_announcementworker_id());
                    }
                }, null, getString(R.string.blako_aceptar), getString(R.string.blako_rechazar), null);

    }




    private void onFailGetAnnouncements(){
        try {

            dialogNotAnnouncements = checkNotVitalService(this,  new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogNotAnnouncements.dismiss();
                    getAnnoucements();
                }
            });
            dialogNotAnnouncements.show();
        }catch (Exception e){
            e.printStackTrace();

        }




    }


    private void getAnnoucements() {
        mSwipeRefreshLayout.setRefreshing(false);
        checkInSB.setEnabled(false);
        offersRv.setAdapter(null);
        //backgroudView.setVisibility(View.VISIBLE);
        //dragView.setVisibility(View.GONE);
        statusSw.setVisibility(View.GONE);
        if(BkoDataMaganer.getOnDemand(this)){
            disconecting = true;
            statusSw.setChecked(true);
            disconecting = false;
        }

        showWaitDialogWhileExecuting("Cargando", new Runnable() {
            public void run() {

                try {
                    offersResponse = null;
                    offers = null;
                    offersResponse = HttpRequest
                            .get(Constants.GET_OFFERS(BkoMainActivity.this) + "workerId=" + user.getWorkerId())
                            .connectTimeout(8000).readTimeout(5000).body();
                    Gson gson = new Gson();
                    if (offersResponse != null) {
                        JSONObject object = new JSONObject(offersResponse);
                        offersResponse = object.getString("message");
                        offersResponse.replace("-", "");
                        offers = Arrays.asList(gson.fromJson(offersResponse, BkoOffer[].class));
                        boolean responseOk = object.getBoolean("response");
                        if (responseOk) {
                            distancecheckin = object.getInt("distancecheckin");
                            penaltys = Arrays.asList(gson.fromJson(object.getString("penalty"), BkoPenaltyResponse[].class));
                        }
                    }
                } catch (Exception e) {
                    e.fillInStackTrace();
                }

            }
        }, new Runnable() {
            public void run() {
                statusSw.setVisibility(View.VISIBLE);
                // dragView.setVisibility(View.VISIBLE);
                // backgroudView.setVisibility(View.GONE);
                if (offers != null) {
                    try {




                        firstAnnounce = null;
                        if (offers.size() != 0) {
                            refreshIv.setVisibility(View.GONE);
                            List<BkoOffer> _offers = new ArrayList<>();
                            for (int i = 0; i < offers.size(); i++) {
                                if (offers.get(i).getAnnouncement().size() != 0) {
                                    _offers.add(offers.get(i));
                                }
                            }

                            if (offers.size() != 0) {
                                if (offers.get(0).getAnnouncement().size() != 0) {
                                    if (offers.get(0).getAnnouncement().get(0).getBko_announcementworker_checkin() != null && offers.get(0).getAnnouncement().get(0).getBko_announcementworker_checkin().equals("1")) {
                                        firstAnnounce = offers.get(0).getAnnouncement().get(0);
                                    }
                                }
                            }
                            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            mLayoutManager.setAutoMeasureEnabled(true);
                            offersRv.setLayoutManager(mLayoutManager);
                            offersRv.setNestedScrollingEnabled(false);
                            BkoOffersAdapter adapter = new BkoOffersAdapter(BkoMainActivity.this, _offers, BkoMainActivity.this);
                            offersRv.setAdapter(adapter);
                            if (firstAnnounce != null && firstAnnounce.getBko_announcementworker_checkin().equals("1")) {
                                BkoDataMaganer.setOnDemand(BkoMainActivity.this,false);
                                statusSw.setVisibility(View.GONE);
                                BkoDataMaganer.setCurrentOffer(firstAnnounce, BkoMainActivity.this);
                                if (BkoDataMaganer.getStatusService(BkoMainActivity.this) != Constants.SERVICE_STATUS_WITH_OFFER_CHECKIN_FINISH) {
                                    BkoDataMaganer.setStatusService(Constants.SERVICE_STATUS_WITH_OFFER_CHECKIN_FINISH, BkoMainActivity.this);
                                    withOfferTaked(firstAnnounce);
                                }
                            } else {
                                int status = BkoDataMaganer.getStatusService(BkoMainActivity.this);
                                if (status != Constants.SERVICE_STATUS_FREE) {
                                    onFree();
                                } else {
                                    clearData(false);
                                    if(BkoDataMaganer.getOnDemand(BkoMainActivity.this)){
                                        user.setIsAvailable(true);
                                        BkoUserDao.Actualizar(BkoMainActivity.this,user);
                                        BkoTripDetailActivity.confirmed = true;
                                        if(!statusSw.isChecked())
                                        statusSw.setChecked(true);
                                        else {
                                            BkoTripDetailActivity.confirmed = true;
                                            onSwitchChecked( true);
                                        }

                                    }
                                }
                            }
                        } else {
                            Toast.makeText(BkoMainActivity.this, getString(R.string.blako_offers_no_ofertas), Toast.LENGTH_SHORT).show();
                        }


                    } catch (Exception e) {
                        refreshIv.setVisibility(View.VISIBLE);
                        e.fillInStackTrace();
                        Toast.makeText(BkoMainActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();
                        onFailGetAnnouncements();
                    }
                } else {
                    refreshIv.setVisibility(View.VISIBLE);
                    Toast.makeText(BkoMainActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();
                    onFailGetAnnouncements();
                }

            }
        });
    }





    private void withOfferTaked(BkoOffer.BkoAnnoucement annoucement) {
        try {
            BkoTripDetailActivity.confirmed = true;
            map.clear();
            onOfferToChekin = true;
            autoNavCb.setChecked(false);
            BkoDataMaganer.setAutoNavigate(BkoMainActivity.this, false);
            if (annoucement!= null && toolbarTitle != null && annoucement.getBko_announcementworker_checkin() != null && !annoucement.getBko_announcementworker_checkin().equals("1")) {
                mToolbar.setVisibility(View.INVISIBLE);
                toolbarTitle.setVisibility(View.VISIBLE);
                setSupportActionBar(toolbarTitle);
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                TextView mTitle = (TextView) toolbarTitle.findViewById(R.id.toolbar_title);
                mTitle.setTextColor(ContextCompat.getColor(this, R.color.blako_white));
                mTitle.setText(getString(R.string.blako_offers_detalle));
                String rangeTime = "";
                if (annoucement.getBko_announcement_finishstatus().equals("1")) {
                    rangeTime =
                            BkoUtilities.formateDateFromstring(annoucement.getBko_announcement_datetimestart()).replace(":00 ", " ").toUpperCase().replace("PM", "").replace("P.M.", "").replace("AM", "").replace("A.M.", "") + " - " +
                                    BkoUtilities.formateDateFromstring(annoucement.getBko_announcement_datetimefinish()).replace(":00 ", " ");

                } else {
                    rangeTime =
                            BkoUtilities.formateDateFromstring(annoucement.getBko_announcement_datetimestart()).replace(":00 ", " ");
                }
                mTitle.setText("TURNO/" + rangeTime);

            }
            googlemapsBt.setVisibility(View.GONE);
            wazeBt.setVisibility(View.GONE);
            mTabHost.setCurrentTab(1);
            announcementV.setVisibility(View.VISIBLE);
            offerNavIv.setColorFilter(ContextCompat.getColor(this, R.color.blako_green), PorterDuff.Mode.MULTIPLY);
            clientTv.setVisibility(View.GONE);

            if ((annoucement!=null &&annoucement.getBko_announcementworker_checkin() != null && annoucement.getBko_announcementworker_checkin().equals("1"))) {
                dragView.setVisibility(View.GONE);
                announcementV.setVisibility(View.GONE);
                onwaySB.setVisibility(View.GONE);
                checkInSB.setVisibility(View.GONE);
                addressOfferLl.setVisibility(View.GONE);
                if (annoucement!=null){
                    BkoDataMaganer.setStatusService(Constants.SERVICE_STATUS_WITH_OFFER_CHECKIN_FINISH, BkoMainActivity.this);
                }
                offerCheckinFinish();
                return;
            } else {
                addressOfferLl.setVisibility(View.VISIBLE);
                onwaySB.setVisibility(View.GONE);
                checkInSB.setVisibility(View.VISIBLE);
                seekOfferTv.setText(getString(R.string.blako_offers_registrarse_llegar));
                firstAnnounce = annoucement;
            }

            if (annoucement!=null){
                aliasTv.setText(annoucement.getBko_announcementaddress_alias());
                aliasOfferTv.setText(annoucement.getBko_announcementaddress_alias());
                String address = "";
                if (annoucement.getBko_announcementaddress_street() != null && annoucement.getBko_announcementaddress_street().length() != 0)
                    address += annoucement.getBko_announcementaddress_street() + " ";
                if (annoucement.getBko_announcementaddress_numext() != null && annoucement.getBko_announcementaddress_numext().length() != 0)
                    address += annoucement.getBko_announcementaddress_numext() + " ";
                if (annoucement.getBko_announcementaddress_numint() != null && annoucement.getBko_announcementaddress_numint().length() != 0)
                    address += annoucement.getBko_announcementaddress_numint() + " ";
                if (address.length() != 0)
                    address += ",";
                if (annoucement.getBko_announcementaddress_neighborhood() != null && annoucement.getBko_announcementaddress_neighborhood().length() != 0)
                    address += annoucement.getBko_announcementaddress_neighborhood() + ", ";
                if (annoucement.getBko_announcementaddress_province() != null && annoucement.getBko_announcementaddress_province().length() != 0)
                    address += annoucement.getBko_announcementaddress_province() + ", ";
                if (annoucement.getBko_announcementaddress_state() != null && annoucement.getBko_announcementaddress_state().length() != 0)
                    address += annoucement.getBko_announcementaddress_state() + ", ";

                addressOfferTv.setText(address);
                firstAnnounce = annoucement;
            }
            addOfferLocationAndRadio();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void onCancelOffer(final String idOffer, final String announcementWorkerId) {
        showWaitDialogWhileExecuting("Cargando...", new Runnable() {
            public void run() {
                Log.i("OnWay", "Arriving");
                try {

                    BkoUser user = BkoUserDao.Consultar(BkoMainActivity.this);
                    cancelServiceResponse = null;
                    cancelServiceResponse = HttpRequest.get(Constants.GET_OFFER_REPORT(BkoMainActivity.this) +
                            "workerId=" + user.getWorkerId() +
                            "&announcementId=" + idOffer + "&stage=4" + "&parentId=" + user.getWorkerParentId() + "&announcementWorkerId=" + announcementWorkerId).connectTimeout(3000).readTimeout(3000).body();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Runnable() {
            public void run() {
                if (cancelServiceResponse != null) {
                    try {
                        Gson gson = new Gson();
                        BkoRequestResponse response = gson.fromJson(cancelServiceResponse, BkoRequestResponse.class);
                        if (response != null) {
                            if (response.isResponse()) {
                                getAnnoucements();
                                if (!response.getMessage().equals(getString(R.string.response_no_parameters))){
                                    Toast.makeText(BkoMainActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                if (!response.getMessage().equals(getString(R.string.response_no_parameters))){
                                    Toast.makeText(BkoMainActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(BkoMainActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(BkoMainActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(BkoMainActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();

                }

            }
        });
    }


    private void reportOfferStatus(final int state) {
        showWaitDialogWhileExecuting("Cargando...", new Runnable() {
            public void run() {
                Log.i("OnWay", "Arriving");
                try {
                    user = BkoUserDao.Consultar(BkoMainActivity.this);
                    visibleReponse = null;
                    stateToReport = state;

                    if (stateToReport == 2 || stateToReport == 3) {
                        String timeregister = BkoUtilities.nowCurrent();
                        Map<String, String> mapVisibleV = new HashMap<String, String>();
                        mapVisibleV.put("workerEmail", user.getEmail());
                        if (stateToReport == 2)
                            mapVisibleV.put("status", "1");
                        else
                            mapVisibleV.put("status", "0");

                        mapVisibleV.put("workerId", user.getWorkerId());
                        mapVisibleV.put("timeregister", timeregister);


                        visibleReponse = HttpRequest.get(Constants.GET_SET_VISIBLE_URL(BkoMainActivity.this), mapVisibleV, true).connectTimeout(5000).readTimeout(5000).body();
                        if (visibleReponse != null) {

                            Gson gson = new Gson();
                            BkoUserStatusResponse response = gson.fromJson(visibleReponse, BkoUserStatusResponse.class);

                            if (response != null) {
                                if (response.isResponse()) {
                                    if (BkoDataMaganer.getStatusService(BkoMainActivity.this) == Constants.SERVICE_STATUS_WITH_OFFER_CHECKIN_FINISH)
                                        user.setIsAvailable(false);
                                    else
                                        user.setIsAvailable(true);
                                    BkoUserDao.Actualizar(BkoMainActivity.this, user);

                                    Location location = BkoDataMaganer.getCurrentUserLocation(BkoMainActivity.this);

                                    String latitude = "";
                                    String longitude = "";
                                    if (location != null) {
                                        latitude = "" + location.getLatitude();
                                        longitude = "" + location.getLongitude();

                                    }

                                    changeStatusOfferResponse = null;
                                    if (BkoSendLocationToServer.oneLocationDataToWebsite(BkoDataMaganer.getCurrentUserLocation(getApplicationContext()), getApplicationContext()))
                                        changeStatusOfferResponse = HttpRequest.get(Constants.GET_OFFER_REPORT(BkoMainActivity.this) +
                                                "workerId=" + user.getWorkerId() +
                                                "&announcementId=" + firstAnnounce.getBko_announcement_id() + "&stage=" + stateToReport + "&parentId=" + user.getWorkerParentId() + "&latitude=" + latitude + "&longitude=" + longitude + "&announcementWorkerId=" + firstAnnounce.getBko_announcementworker_id()).connectTimeout(3000).readTimeout(3000).body();


                                }

                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Runnable() {
            public void run() {

                checktoutSB.setEnabled(true);
                if (firstAnnounce == null) {
                    getAnnoucements();
                }
                if (changeStatusOfferResponse != null) {
                    try {

                        Gson gson = new Gson();
                        BkoCheckInResponse response = gson.fromJson(changeStatusOfferResponse, BkoCheckInResponse.class);
                        if (response != null) {
                            if (response.isResponse()) {
                                if (stateToReport == 2) {
                                    announcementV.setVisibility(View.GONE);
                                    checkInSB.setProgress(10);
                                    onwaySB.setProgress(10);
                                    onwaySB.setVisibility(View.GONE);
                                    checkInSB.setVisibility(View.GONE);
                                    seekOfferTv.setText("INICIAR");
                                    addressOfferLl.setVisibility(View.GONE);
                                    firstAnnounce.setTime(response.getTime());
                                    firstAnnounce.setBko_announcementworker_checkin("1");
                                    BkoDataMaganer.setCurrentOffer(firstAnnounce, BkoMainActivity.this);
                                    BkoDataMaganer.setStatusService(Constants.SERVICE_STATUS_WITH_OFFER_CHECKIN_FINISH, BkoMainActivity.this);
                                    finishAffinity();
                                    Intent intent = new Intent(BkoMainActivity.this, BkoMainActivity.class);
                                    startActivity(intent);
                                } else {
                                    BkoDataMaganer.setCurrentOffer(null, BkoMainActivity.this);
                                    BkoDataMaganer.setStatusService(Constants.SERVICE_STATUS_FREE, BkoMainActivity.this);
                                    finishAffinity();
                                    Intent intent = new Intent(BkoMainActivity.this, BkoMainActivity.class);
                                    startActivity(intent);
                                }
                                if (!response.getMessage().equals(getString(R.string.response_no_parameters))){
                                    Toast.makeText(BkoMainActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                if (!response.getMessage().equals(getString(R.string.response_no_parameters))){
                                    Toast.makeText(BkoMainActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                onwaySB.setProgress(10);
                                checkInSB.setProgress(10);
                                checktoutSB.setProgress(10);

                            }
                        } else {
                            Toast.makeText(BkoMainActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();
                            onwaySB.setProgress(10);
                            checkInSB.setProgress(10);
                            checktoutSB.setProgress(10);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(BkoMainActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();
                        onwaySB.setProgress(10);
                        checkInSB.setProgress(10);
                        checktoutSB.setProgress(10);
                    }
                } else {
                    Toast.makeText(BkoMainActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();
                    onwaySB.setProgress(10);
                    checkInSB.setProgress(10);
                    checktoutSB.setProgress(10);
                }

            }
        });
    }

    private void addOfferLocationAndRadio() {
        map.clear();
        if(firstAnnounce!=null){
            Double lat = Double.parseDouble(firstAnnounce.getBko_announcementaddress_lat());
            Double lng = Double.parseDouble(firstAnnounce.getBko_announcementaddress_lng());
            clientMarker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_white)));

            int distance = 500;
            if (distancecheckin != null) {
                distance = distancecheckin;
            }
            ratioOffer = map.addCircle(new CircleOptions().center(new LatLng(lat, lng)).radius(distance).fillColor(0x5050BC50).strokeColor(ContextCompat.getColor(BkoMainActivity.this, R.color.blako_green)).strokeWidth(3));
            checkPositionOnRatio();
            final Location location = new Location("");
            location.setLatitude(lat);
            location.setLongitude(lng);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    displayLocation(location, Constants.DEFAULT_MAP_ZOOM, null);

                }
            }, 1200);
        }

    }

    private void checkPositionOnRatio() {
        if (firstAnnounce == null || (onOfferToChekin == null || !onOfferToChekin))
            return;

        Double lat = Double.parseDouble(firstAnnounce.getBko_announcementaddress_lat());
        Double lng = Double.parseDouble(firstAnnounce.getBko_announcementaddress_lng());
        final Location location = new Location("");
        location.setLatitude(lat);
        location.setLongitude(lng);
        Location currentLocation = BkoDataMaganer.getCurrentUserLocation(this);
        if (currentLocation == null) {
            Toast.makeText(this, "Sin ubicación", Toast.LENGTH_SHORT).show();
            return;
        }
        float distance = currentLocation.distanceTo(location);
        int minDistanceCheckin = 50000;
        if (distancecheckin != null)
            minDistanceCheckin = distancecheckin;
        if (currentEnviromentUrl != null && currentEnviromentUrl.contains("sandbox"))
            distance = 120;

        if (distance < minDistanceCheckin) {
            if (onOfferToChekin) {
                checkInSB.setEnabled(true);
                checkInSB.setBackground(ContextCompat.getDrawable(this, R.drawable.green_button));
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                try {
                    Calendar calendar = Calendar.getInstance();
                    Date date1 = simpleDateFormat.parse(firstAnnounce.getBko_announcement_datetimestart());
                    Date date2 = calendar.getTime();
                    Date date3 = simpleDateFormat.parse(firstAnnounce.getMinutes_tolerance());
                    String now = simpleDateFormat.format(date2);
                    seekOfferTv.setText(getString(R.string.blako_offers_registrarse_llegar) + " " + BkoUtilities.formateDateFromstring(now).replace(":00 ", " "));
                    if (date2.getTime() <= date1.getTime()) {
                        aliasTv.setText(getString(R.string.blako_agregar_tiempo));
                    } else if (date2.getTime() < date3.getTime()) {
                        checkInSB.setBackground(ContextCompat.getDrawable(this, R.drawable.orange_button));
                        aliasTv.setText(getString(R.string.blako_agregar_tolerancia) + " " + BkoUtilities.formateDateFromstring(firstAnnounce.getMinutes_tolerance()).replace(":00 ", " "));
                    } else {
                        aliasTv.setText(getString(R.string.blako_agregar_tarde));
                        checkInSB.setBackground(ContextCompat.getDrawable(this, R.drawable.red_button));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (checkInSB.isEnabled()) {
                checkInSB.setEnabled(false);
                checkInSB.setProgress(10);
                checkInSB.setBackground(ContextCompat.getDrawable(this, R.drawable.grey_button));
            }
        }

    }


    private void offerCheckinFinish() {
        firstAnnounce = BkoDataMaganer.getCurrentOffer(this);
        mapHeaderView.setVisibility(View.VISIBLE);
        mTabHost.setVisibility(View.VISIBLE);
        dragView.setVisibility(View.GONE);
        announcementV.setVisibility(View.GONE);
        refreshRouteIv.setVisibility(View.GONE);
        googlemapsBt.setVisibility(View.GONE);
        wazeBt.setVisibility(View.GONE);
        statusSw.setVisibility(View.GONE);
        Integer dp = (int) BkoUtilities.convertDpToPixel(60, this);
        relativeMap.setPadding(0, dp, 0, 0);
        addressTv.setVisibility(View.GONE);
        BkoUser user = null;


        try {
            user = BkoUserDao.Consultar(this);
            user.setIsAvailable(true);
            mToolbar.setBackgroundColor(ContextCompat.getColor(BkoMainActivity.this, R.color.blako_background));
            statusTV.setText(getString(R.string.blako_conectado));
            BkoUserDao.Actualizar(this, user);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (firstAnnounce != null)
            statusTV.setText(selectedWareHouse());
        else {
            getAnnoucements();
            statusTV.setText(getString(R.string.blako_conectado));
        }
    }


    ////////////////////////////////////////////////////// NOT BUSSINESS AND UTILS //////////////////////////////////////////////////////////////////////////////////////////


    private void setLocationsAlarms(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent gpsTrackerIntent = new Intent(this, BkoGpsTrackerAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, gpsTrackerIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 12);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            alarmManager.set(AlarmManager.RTC_WAKEUP,
                    cal.getTimeInMillis(), pendingIntent);

        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                    cal.getTimeInMillis(), pendingIntent);
        }

        if (!isMyServiceRunning(LocationService.class, BkoMainActivity.this)) {
            Intent service = new Intent(BkoMainActivity.this, LocationService.class);
            startService(service);
            Log.d("*********START SERVICE ", "LOCATION***");
        } else {
            Log.d("*******RUNNING SERVICE ", "LOCATION***");
        }
        /*Intent service = new Intent(this, LocationService.class);
        startService(service);*/
        setAlarmPushHeartBeat();
    }

    @Override
    public void onMapReady(GoogleMap arg0) {
        Log.d("MapReady","Ok");
        map = arg0;
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
        if (map != null) {

            mapReady = true;
            setLocationsAlarms();
            //////////////////////////// INITIAL DATA AND CONFIG ////////////////////////////
            map.getUiSettings().setZoomControlsEnabled(true);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                map.setMyLocationEnabled(true);
            }
            map.getUiSettings().setCompassEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(final Marker myMarker) {
                    myMarker.showInfoWindow();
                    return true;
                }

            });
            map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {

                }
            });
            map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    if (marker == null || marker.getTitle() == null)
                        return null;
                    String[] titlePosition = marker.getTitle().split("-");
                    if (titlePosition.length == 0)
                        return null;
                    String order = titlePosition[0];
                    String address = "";

                    if (titlePosition.length == 2) {
                        address = titlePosition[1];
                    }

                    View v = getLayoutInflater().inflate(R.layout.bko_custom_info, null);
                    TextView title = (TextView) v.findViewById(R.id.orderTv);
                    title.setText(order);
                    TextView snip = (TextView) v.findViewById(R.id.dateTv);
                    snip.setText(marker.getSnippet());
                    TextView addresTv = (TextView) v.findViewById(R.id.addresTv);
                    addresTv.setText("" + address);
                    return v;
                }
            });

            try {
                mLastLocation = BkoDataMaganer.getCurrentUserLocation(this);

                if (mLastLocation != null) {
                    displayLocation(mLastLocation, Constants.DEFAULT_MAP_ZOOM, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            recoverAppStateifService();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new GetKmlFromDbTask().execute();
                }
            },4000);
        }
    }

    private Location getCurrentLocation() {
        Location location = null;
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location == null && map != null)
                location = map.getMyLocation();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    protected class InternetBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BkoUtilities.isNetWorkConnected(BkoMainActivity.this)) {
                if (internetDialog != null)
                    internetDialog.dismiss();
                if (map != null) {
                    Location location = new Location("");
                    location.setLatitude(map.getCameraPosition().target.latitude);
                    location.setLongitude(map.getCameraPosition().target.longitude);
                }
            } else {
                if (internetDialog == null || !internetDialog.isShowing()) {
                    internetDialog = checkInternetConection(BkoMainActivity.this);
                }
            }
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(mPushReceiver);
            unregisterReceiver(internetBroadCast);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        detachEmergencyListener();
        zoneHandler.removeCallbacks(zoneRunnable);
        syncHandler.removeCallbacks(syncRunnable);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiveZoneUpdate);
        if (mGoogleApiClient != null)
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                mGoogleApiClient.disconnect();
            }
        sendOneLodation();
        sync= null;
        super.onPause();
    }



    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null)
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
    }

    private synchronized void displayLocation(Location location, float zoom, Float bearing) {
        if (location == null)
            return;
        if (gpsDialog != null) {
            try {
                gpsDialog.dismiss();
            } catch (Exception e) {

            }
        }

        if (internetDialog != null) {
            try {
                internetDialog.dismiss();
            } catch (Exception e) {

            }
        }
        mLastLocation = location;
        if (bearing == null) {
            double currentLatitude = mLastLocation.getLatitude();
            double currentLongitude = mLastLocation.getLongitude();
            LatLng latLng = new LatLng(currentLatitude, currentLongitude);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(zoom).build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            map.animateCamera(cameraUpdate, 1000, null);
        } else {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())).
                    bearing(bearing).zoom(map.getCameraPosition().zoom).build();

            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
    }

    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(BkoMainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(BkoMainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(BkoMainActivity.this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
    }

    @Override
    public void onConnected(Bundle arg0) {
        Location location = BkoDataMaganer.getCurrentUserLocation(this);
        //Location location= mLastLocation;
        boolean isMockLocation = checkMockLocation(this, location);
        if (isMockLocation) {
            location.setProvider("mock");
            if (locationDialog != null) {
            } else {
                locationDialog = AlertaTextView(getString(R.string.blako_mock_location_titulo), getString(R.string.blako_mock_location), this,
                        null, getString(R.string.blako_reintentar), null);
                locationDialog.setCancelable(false);

                locationDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!checkMockLocation(BkoMainActivity.this, BkoDataMaganer.getCurrentUserLocation(BkoMainActivity.this))) {
                            locationDialog.dismiss();
                            locationDialog = null;
                        }
                    }
                });
            }
            return;
        } else {
            try {
                if (locationDialog != null) {
                    locationDialog.dismiss();
                    locationDialog = null;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        if (location == null || map == null) {
            if (gpsDialog != null) {
                try {
                    gpsDialog.dismiss();
                } catch (Exception e) {

                }
            }
            gpsDialog = checkGPSConection(this);
            startLocationUpdates();
        } else {
            if (onOfferToChekin != null && !onOfferToChekin || ( BkoDataMaganer.getOnDemand(this) ||  BkoDataMaganer.getStatusService(this) != Constants.SERVICE_STATUS_FREE))
                displayLocation(location, Constants.DEFAULT_MAP_ZOOM, null);
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null || map == null){
            return;
        }
        checkPositionOnRatio();

        if (mLastLocation == null) {
            mLastLocation = location;
            if (BkoDataMaganer.getAutoNavigate(this))
                displayLocation(mLastLocation, Constants.DEFAULT_MAP_ZOOM, null);
            sendOneLodation();
        } else{
            stopLocationUpdates();
        }
    }

    /*private class GetActualAreaTask extends AsyncTask<LatLng,Void,KmlPlacemark> {

        @Override
        protected KmlPlacemark doInBackground(LatLng... latLngs) {
            return DeliveryZoneCheck.getActualPlacemarkFromLocation(BkoMainActivity.this,placemarkList,latLngs[0]);
        }

        @Override
        protected void onPostExecute(KmlPlacemark placemark) {
            if (placemark!=null){
                Log.d("Actual_Zone",placemark.getProperty("name"));
                BkoDataMaganer.setActualDeliveryZone(BkoMainActivity.this,placemark.getProperty("name"));
                *//*if (locationDatabaseReference!=null){
                    locationDatabaseReference.child("currentLocation").child("zone").setValue(placemark.getProperty("name"));
                }*//*
            }
        }
    }*/

    @Override
    public void onBackPressed() {
        if (checkOutView.getVisibility() == View.VISIBLE) {
            checkOutView.setVisibility(View.GONE);
            return;
        }
        if (onOfferToChekin != null && !onOfferToChekin)
            super.onBackPressed();
        else {
            if (firstAnnounce != null && !firstAnnounce.getBko_announcementworker_checkin().equals("1"))
                onOfferToChekin();
            else
                super.onBackPressed();
        }
    }

    private void setData() {
        if (mTabHost.getVisibility() == View.VISIBLE)
            map.clear();

        if (map!=null){
            new GetTextMarkerFromHubConfigTask().execute(hubConfigs);
            for (HubConfig hubConfig:hubConfigs){
                map.addPolygon(hubConfig.getPolygonOptions());
            }
            /*try {
                kmlLayer.addLayerToMap();
                for (PolygonOptions polygon:kmlHubs){
                    map.addPolygon(polygon);
                }
                for (MarkerOptions marker:kmlLabels){
                    map.addMarker(marker);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }*/
        }

        BkoTrips trips = BkoDataMaganer.getCurrentTrips(this);
        if (trips != null && trips.getTrips() != null) {
            ArrayList<LatLng> locations = new ArrayList<>();
            for (BkoTripVO _trip : trips.getTrips()) {
                BkoChildTripVO trip = _trip.getOrigen().get(0);
                BkoChildTripVO tripDelivery = _trip.getDestino().get(0);
                try {
                    Double lat = Double.parseDouble(trip.getBko_customeraddress_latitude());
                    Double lng = Double.parseDouble(trip.getBko_customeraddress_longitude());
                    Double latD = Double.parseDouble(tripDelivery.getBko_customeraddress_latitude());
                    Double lngD = Double.parseDouble(tripDelivery.getBko_customeraddress_longitude());
                    String rangeTime = "";
                    // ORIGIN
                    String numberIntExt = " Ext." + trip.getBko_customeraddress_numexterior();
                    if (trip.getBko_customeraddress_numinterior() != null && trip.getBko_customeraddress_numinterior().length() > 0) {
                        numberIntExt = numberIntExt + " Int." + trip.getBko_customeraddress_numinterior();
                    }
                    String addressOrigin = BkoUtilities.ensureNotNullString(trip.getBko_customeraddress_street() + numberIntExt + ", " + trip.getBko_customeraddress_colony() + ", " + trip.getBko_customeraddress_delegation());
                    // DELIVERY
                    String numberIntExtD = " Ext." + tripDelivery.getBko_customeraddress_numexterior();
                    if (tripDelivery.getBko_customeraddress_numinterior() != null && tripDelivery.getBko_customeraddress_numinterior().length() > 0) {
                        numberIntExtD = numberIntExtD + " Int." + tripDelivery.getBko_customeraddress_numinterior();
                    }
                    String addressDelivery = BkoUtilities.ensureNotNullString(tripDelivery.getBko_customeraddress_street() + numberIntExtD + ", " + tripDelivery.getBko_customeraddress_colony() + ", " + tripDelivery.getBko_customeraddress_delegation());
                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lng)).snippet(rangeTime).icon(BitmapDescriptorFactory.fromResource(R.drawable.markerorigin)).title(trip.getBko_customeraddress_alias() + "-" + addressOrigin));

                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(latD, lngD)).snippet(rangeTime).icon(BitmapDescriptorFactory.fromResource(R.drawable.markerdelivery)).title(tripDelivery.getBko_customeraddress_contact() + "\n" + tripDelivery.getBko_customeraddress_alias() + "-" + addressDelivery));

                    locations.add(new LatLng(lat, lng));
                    locations.add(new LatLng(latD, lngD));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Location userLocation = BkoDataMaganer.getCurrentUserLocation(this);
            if (userLocation != null) {
                locations.add(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()));
            }
            if (locations.size() > 0) {
                setZoomForCurrentMarkers(locations);
            }
        }
    }
    public void setNumberOrder(int total ){


        if(mTabHost.getTabWidget().getChildCount()> 0){
            TextView tv = (TextView) mTabHost.getTabWidget().getChildAt(0).findViewById(R.id.tabsText);
            if (tv!=null){
                if(total==0)
                    tv.setText("ORDENES");
                else
                    tv.setText("ORDENES(" +total+")");

            }
     }

    }

    @Override
    public void onGpsStatusChanged(int event) {
        switch (event) {

            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                status++;
                if (BkoDataMaganer.getLastLocationTimeStamp(this)!=0 && checkEvery==status && (BkoDataMaganer.getOnDemand(this) || BkoDataMaganer.getCurrentOffer(this)!=null)){
                    status= 0;
                    if (Math.abs(System.currentTimeMillis()-BkoDataMaganer.getLastLocationTimeStamp(this))>NO_CONECTION_TIME){
                        tvNoConnection.setVisibility(View.VISIBLE);
                    }else {
                        tvNoConnection.setVisibility(View.GONE);
                        Location zoneLocation= BkoDataMaganer.getCurrentUserLocation(this);
                        if (zoneLocation!=null){
                            LatLng latLngLocation= new LatLng(zoneLocation.getLatitude(),zoneLocation.getLongitude());
                            // TODO: 27/07/2018 CAMBIAR!!!
                            int hubId= DeliveryZoneCheck.getActualHubFromLocation(hubConfigs,latLngLocation);
                            Log.d("Hub_Id",String.valueOf(hubId));
                            BkoDataMaganer.setActualDeliveryZoneId(BkoMainActivity.this,hubId);
                        }
                    }
                }
                break;
        }
    }

    private void registerGpsStatusListener() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.addGpsStatusListener(this);
        }catch (SecurityException e){
            e.fillInStackTrace();
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void attachEmergencyListener(){
        if (emergencyListener==null){
            emergencyListener= new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    doomsday= (boolean) dataSnapshot.getValue();
                    if (doomsday){
                        new GetKmlFromDbTask().execute();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            emergencyDatabaseReference.addValueEventListener(emergencyListener);
        }
    }

    private void detachEmergencyListener(){
        if (emergencyListener!=null){
            emergencyDatabaseReference.removeEventListener(emergencyListener);
            emergencyListener= null;
        }
    }
}
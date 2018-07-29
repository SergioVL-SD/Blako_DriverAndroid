package com.blako.mensajero.Views;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.blako.mensajero.App;
import com.blako.mensajero.BkoCore;
import com.blako.mensajero.BkoDataMaganer;
import com.blako.mensajero.Constants;
import com.blako.mensajero.Custom.BkoMapFragment;
import com.blako.mensajero.Custom.BkoSeekBar;
import com.blako.mensajero.Dao.BkoUserDao;
import com.blako.mensajero.R;
import com.blako.mensajero.Services.BkoHeartBeatPushAliveReceiver;
import com.blako.mensajero.Services.BkoSendLocationToServer;
import com.blako.mensajero.Services.RegistrationIntentService;
import com.blako.mensajero.Utils.AppPreferences;
import com.blako.mensajero.Utils.BkoUtilities;
import com.blako.mensajero.Utils.HttpRequest;
import com.blako.mensajero.VO.BkoOffer;
import com.blako.mensajero.VO.BkoPenaltyResponse;
import com.blako.mensajero.VO.BkoRecoverStatusVO;
import com.blako.mensajero.VO.BkoRequestResponse;
import com.blako.mensajero.VO.BkoUser;
import com.blako.mensajero.VO.BkoVersionVO;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by franciscotrinidad on 27/03/17.
 */

public class BkoMainBaseActivity extends BaseActivity {
    protected int UPDATE_INTERVAL = 5000;
    protected int FATEST_INTERVAL = 5000;
    protected int DISPLACEMENT = 2;
    protected GoogleApiClient mGoogleApiClient;
    public static final String ARG_SECTION = "complaint_page_number";
    private Target loadtarget;
    //VIEWS
    protected GoogleMap map = null;
    protected BkoMapFragment mapFrag;
    protected TextView addressTv, addressTripTv;
    protected View  addressSearchLl, addressDeliveryLl, tripsLl;
    protected ImageView refreshRouteIv;
    protected TextView tripsCountTv;
    protected TextView visibilityTv;
    protected TextView orderTv;
    protected CheckBox autoNavCb;
    protected View workerInfoLl, navigationLl;
    protected TextView workerNameTv, workerVehicleTv;
    protected ImageButton wazeBt, googlemapsBt;
    protected Toolbar mToolbar, toolbarTitle;
    protected Drawer drawerMenu;
    protected ImageView logoOfferIv;
    protected TextView aliasTv, clientTv, etaTimeTv, startEndTimeTv, seekOfferTv;
    protected View seekOfferLl;
    protected BkoSeekBar onwaySB, checkInSB, checktoutSB;
    protected View addressOfferLl;
    protected TextView aliasOfferTv, addressOfferTv;
    protected AppCompatImageView offerNavIv;
    protected Switch statusSw;
    protected TextView statusTV;
    protected View dragView;
    protected RelativeLayout relativeMap;
    protected Circle ratioOffer;
    protected View checkOutView;
    protected View announcementV;
    protected TextView timeCheckoutTv;
    protected FragmentTabHost mTabHost;
    protected RecyclerView offersRv;
    protected SlidingUpPanelLayout mLayout;
    protected IProfile iProfile;
    protected AccountHeader headerResult;
    protected View mapHeaderView, offersViews;
    protected TextView  checkoutTv;
    protected View offerNavV;
    protected TextView timeRemainginTv;
    protected View searchOffer;
    protected Boolean mapReady= false;
    protected Boolean onOfferToChekin = null;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected ImageView refreshIv;
    protected TextView noResultsTv, searchOfferTv;
    protected String versionResponse;
    protected BkoVersionVO versionResponseVO;
    protected View confirmLl;
    protected TextView pickDeliveryAddresTv, confirmTv, tvNoConnection;
    protected AppCompatImageView okComfirmTv;
    protected AppCompatImageView arrowComfirmTv;
    protected android.app.AlertDialog gpsDialog;
    protected android.app.AlertDialog internetDialog;
    protected android.app.AlertDialog locationDialog;
    protected TextView phoneDeliveryTv;
    protected Location mLastLocation;
    protected String tokenServiceResponse;
    protected LocationRequest mLocationRequest;
    protected IntentFilter mIntentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    protected BkoUser user;
    protected String visibleReponse, cancelServiceResponse;
    protected Marker clientMarker;
    protected String statusResponse;
    protected View backgroudView;
    protected BkoRecoverStatusVO recoverStatusVO;
    protected List<BkoOffer> offers;
    protected String offersResponse;
    protected BkoOffer.BkoAnnoucement firstAnnounce;
    protected String changeStatusOfferResponse;
    protected int stateToReport = 1;
    protected long mLastClickTime = 0;
    protected Integer distancecheckin;
    protected String currentEnviromentUrl = "";
    protected List<BkoPenaltyResponse> penaltys;
    protected boolean internetProblem;
    protected android.app.AlertDialog dialogNotAnnouncements;

    private AppPreferences preferences;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (onOfferToChekin != null && !onOfferToChekin()) {
                    drawerMenu.getDrawerLayout().openDrawer(GravityCompat.START, true);
                    mLayout.setEnabled(true);
                    mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    mLayout.setTouchEnabled(false);
                }
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }/**/
    }


    protected boolean onOfferToChekin() {
        if (onOfferToChekin != null && onOfferToChekin) {
            drawerMenu.setToolbar(this, mToolbar);
            onOfferToChekin = false;
            mLayout.setEnabled(true);
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            mLayout.setTouchEnabled(false);
            mToolbar.setVisibility(View.VISIBLE);
            toolbarTitle.setVisibility(View.INVISIBLE);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setHomeButtonEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            return true;

        }
        buildDrawer();
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bko_main_activity);
        toolbarTitle = (Toolbar) findViewById(R.id.toolbartitle);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mapFrag = (BkoMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapaFt);
        addressTv = (TextView) findViewById(R.id.addressTv);
        backgroudView =  findViewById(R.id.backgroudView);
        announcementV = (View) findViewById(R.id.announcementV);
        addressSearchLl = (View) findViewById(R.id.addressSearchLl);
        tripsLl = (View) findViewById(R.id.tripsLl);
        addressDeliveryLl = (View) findViewById(R.id.addressDeliveryLl);
        addressTripTv = (TextView) findViewById(R.id.addressTripTv);
        refreshRouteIv = (ImageView) findViewById(R.id.refreshRouteIv);
        tripsCountTv = (TextView) findViewById(R.id.tripsCountTv);
        workerInfoLl = (View) findViewById(R.id.workerInfoLl);
        workerNameTv = (TextView) findViewById(R.id.workerNameTv);
        workerVehicleTv = (TextView) findViewById(R.id.workerVehicleTv);
        navigationLl = (View) findViewById(R.id.navigationLl);
        wazeBt = (ImageButton) findViewById(R.id.wazeBt);
        googlemapsBt = (ImageButton) findViewById(R.id.googlemapsBt);
        orderTv = (TextView) findViewById(R.id.orderTv);
        visibilityTv = (TextView) findViewById(R.id.visibilityTv);
        autoNavCb = (CheckBox) findViewById(R.id.autoNavCb);
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        aliasTv = (TextView) findViewById(R.id.aliasTv);
        clientTv = (TextView) findViewById(R.id.clientTv);
        etaTimeTv = (TextView) findViewById(R.id.etaTimeTv);
        startEndTimeTv = (TextView) findViewById(R.id.startEndTimeTv);
        logoOfferIv = (ImageView) findViewById(R.id.logoOfferIv);
        seekOfferTv = (TextView) findViewById(R.id.seekOfferTv);
        seekOfferLl = (View) findViewById(R.id.seekOfferLl);
        onwaySB = (BkoSeekBar) findViewById(R.id.onwaySB);
        checkInSB = (BkoSeekBar) findViewById(R.id.checkInSB);
        addressOfferLl = (View) findViewById(R.id.addressOfferLl);
        aliasOfferTv = (TextView) findViewById(R.id.aliasOfferTv);
        addressOfferTv = (TextView) findViewById(R.id.addressOfferTv);
        offerNavIv = (AppCompatImageView) findViewById(R.id.offerNavIv);
        statusSw = (Switch) findViewById(R.id.statusSw);
        statusTV = (TextView) findViewById(R.id.statusTV);
        dragView = (View) findViewById(R.id.dragView);
        relativeMap = (RelativeLayout) findViewById(R.id.relativeMap);
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        checkOutView = (View) findViewById(R.id.checkOutView);
        checktoutSB = (BkoSeekBar) checkOutView.findViewById(R.id.checktoutSB);
        timeCheckoutTv = (TextView) checkOutView.findViewById(R.id.timeCheckoutTv);
        mapHeaderView = (View) findViewById(R.id.mapHeaderView);
        checkoutTv = (TextView) findViewById(R.id.checkoutTv);
        offerNavV = (View) findViewById(R.id.offerNavV);
        timeRemainginTv = (TextView) findViewById(R.id.timeRemainginTv);
        offersViews = (View) findViewById(R.id.offersViews);
        searchOffer = (View) findViewById(R.id.searchOffer);
        searchOfferTv = (TextView) findViewById(R.id.searchOfferTv);
        refreshIv = (ImageView) findViewById(R.id.refreshIv);
        noResultsTv = (TextView) findViewById(R.id.noResultsTv);
        confirmLl = (View) findViewById(R.id.confirmLl);
        pickDeliveryAddresTv = (TextView) findViewById(R.id.pickDeliveryAddresTv);
        confirmTv = (TextView) findViewById(R.id.confirmTv);
        tvNoConnection = (TextView) findViewById(R.id.tvNoConnection);
        okComfirmTv = (AppCompatImageView) findViewById(R.id.okComfirmTv);
        arrowComfirmTv = (AppCompatImageView) findViewById(R.id.arrowComfirmTv);
        phoneDeliveryTv = (TextView) findViewById(R.id.phoneDeliveryTv);

        preferences= App.getInstance().getPreferences();

        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(new ColorDrawable(0xFF17202d))
                .withSelectionListEnabledForSingleProfile(false)
                .withProfileImagesClickable(false)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();
        offersRv = (RecyclerView) findViewById(R.id.offersRv);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        offersRv.setLayoutManager(layoutManager);
    }

    protected void buildDrawer() {
        String version = "V. " + BkoUtilities.getVersionName(this);

        View view;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.slide_footer, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView versionTv = (TextView) view.findViewById(R.id.versionTv);
        versionTv.setText(version);

        drawerMenu = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult, false)
                .withToolbar(mToolbar)
                .withStickyFooterDivider(true)
                .withStickyFooterShadow(true)
                .withFooter((ViewGroup) view)
                .addDrawerItems(
                        new SecondaryDrawerItem().withName(R.string.blako_menu_ofertas_agregar).withIdentifier(6).withSelectedTextColor(ContextCompat.getColor(this, R.color.blako_background))
                        , new SecondaryDrawerItem().withName(R.string.blako_menu_ofertas).withIdentifier(7).withSelectedTextColor(ContextCompat.getColor(this, R.color.blako_background)),
                        new SecondaryDrawerItem().withName(R.string.blako_menu_reglamento).withIdentifier(8).withSelectedTextColor(ContextCompat.getColor(this, R.color.blako_background)),
                        new SecondaryDrawerItem().withName(R.string.blako_servicios_sin_oferta).withIdentifier(9).withSelectedTextColor(ContextCompat.getColor(this, R.color.blako_background)),
                        new SecondaryDrawerItem().withName(R.string.blako_menu_notificaciones).withIdentifier(10).withSelectedTextColor(ContextCompat.getColor(this, R.color.blako_background)),
                        new SecondaryDrawerItem().withName(R.string.blako_menu_salir).withIdentifier(5).withSelectedTextColor(ContextCompat.getColor(this, R.color.blako_background))

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {


                        if (drawerItem != null && drawerItem.getIdentifier() == 5) {

                            if (BkoDataMaganer.getStatusService(BkoMainBaseActivity.this) > Constants.SERVICE_STATUS_FREE || BkoDataMaganer.getOnDemand(BkoMainBaseActivity.this)) {

                                AlertaTextView(getString(R.string.blako_advertencia), getString(R.string.blako_en_servicio_no_logout), BkoMainBaseActivity.this,
                                        null, getString(R.string.blako_aceptar), null);

                                return  true;

                            }

                            AlertaTextViewListener(getString(R.string.blako_advertencia), getString(R.string.blako_desconectar), BkoMainBaseActivity.this,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            BkoCore.offersInterfaceListener.onLogoutListener(true);
                                        }
                                    }, null, getString(R.string.blako_aceptar), getString(R.string.blako_rechazar), null);
                        } else if (drawerItem != null && drawerItem.getIdentifier() == 9) {

                            Intent intent = new Intent(BkoMainBaseActivity.this, BkoServicesNoOfferActivity.class);
                            startActivity(intent);

                        } else if (drawerItem != null && drawerItem.getIdentifier() == 6) {


                            Intent intent = new Intent(BkoMainBaseActivity.this, BkoOffersByDateActivity.class);
                            startActivity(intent);
                        } else if (drawerItem != null && drawerItem.getIdentifier() == 7) {


                            Intent intent = new Intent(BkoMainBaseActivity.this, BkoOffersActivity.class);
                            startActivity(intent);
                        } else if (drawerItem != null && drawerItem.getIdentifier() == 8) {


                            Intent intent = new Intent(BkoMainBaseActivity.this, BkoPolicyActivity.class);
                            intent.putExtra("fromMain", true);
                            startActivity(intent);
                        } else if (drawerItem != null && drawerItem.getIdentifier() == 10) {
                            updateToken();
                        }
                        drawerMenu.closeDrawer();
                        return true;
                    }
                })
                .build();

    }

    protected void loadBitmap(String url) {

        if (loadtarget == null) loadtarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                try {
                    BkoUser user = BkoUserDao.Consultar(BkoMainBaseActivity.this);
                    iProfile.withIcon(bitmap);
                    iProfile.withEmail((user.getEmail()));
                    iProfile.withName((user.getName()) + " " + user.getLastname());
                    headerResult.updateProfile(iProfile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {


            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }


        };

        Picasso.with(this).load(url).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(loadtarget);
    }

    protected void setupTabs() {
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        mTabHost.getTabWidget().setDividerDrawable(null);
        setupTab(new TextView(this), "ORDENES", 1);
        setupTab(new TextView(this), "MAPA", 2);
    }

    private void setupTab(final View view, final String tag, int sectionNumber) {

        Bundle args = new Bundle();
        args.putInt(ARG_SECTION, sectionNumber);
        View tabvView = createTabView(mTabHost.getContext(), tag);
        TabHost.TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabvView).setContent(new TabHost.TabContentFactory() {

            public View createTabContent(String tag) {
                return view;
            }
        });

        try {
            if (sectionNumber == 1) {
                mTabHost.addTab(setContent, BkoTripsInProgressFragment.class, args);
            } else {
                mTabHost.addTab(setContent, TransparentFragment.class, args);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static View createTabView(final Context context, final String text) {
        View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
        TextView tv = (TextView) view.findViewById(R.id.tabsText);
        tv.setText(text);
        return view;
    }

    protected void setAlarmPushHeartBeat() {

        String TAG = "SenPendingTripsReceiver";

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent pushHeartBeatintent = new Intent(this, BkoHeartBeatPushAliveReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 9, pushHeartBeatintent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE,
                10);
        Log.d("Calendar seconds", " " + cal.getTimeInMillis());


        Log.d(TAG, "CREATING A" +
                "" +
                "" +
                "" +
                "LARM PUSH");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            alarmManager.set(AlarmManager.RTC_WAKEUP,
                    cal.getTimeInMillis(), pendingIntent);
            Log.d(TAG, "call alarmManager.set() PUSH");


        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                    cal.getTimeInMillis(), pendingIntent);
            Log.d(TAG, "call alarmManager.setExact() PUSH");

        }
    }


    protected void showLocation(final String lat, final String lng) {

        BkoUtilities.showLocation(this, lat, lng);


    }


    protected void setZoomForCurrentMarkers(ArrayList<LatLng> locations) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng location : locations) {
            builder.include(location);
        }
        LatLngBounds bounds = builder.build();
        int padding = 100;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        map.animateCamera(cu);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (BkoDataMaganer.getSendedFirebaseToken(this)) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }


    }


    private void updateToken() {

        showWaitDialogWhileExecuting("Cargando...", new Runnable() {
            public void run() {
                Log.i("OnWay", "Arriving");
                try {

                    BkoUser user = BkoUserDao.Consultar(BkoMainBaseActivity.this);
                    tokenServiceResponse = null;
                    String refreshedToken = preferences.getFirebaseToken();


                    if (user != null) {
                        Map<String, String> mapVisible = new HashMap<String, String>();
                        mapVisible.put("workerId", user.getWorkerId());
                        mapVisible.put("token", refreshedToken);
                        tokenServiceResponse = HttpRequest.get(Constants.GET_UPDATE_TOKEN(BkoMainBaseActivity.this), mapVisible, true).connectTimeout(5000).readTimeout(5000).body();


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Runnable() {
            public void run() {
                if (tokenServiceResponse != null) {

                    try {

                        Gson gson = new Gson();
                        BkoRequestResponse response = gson.fromJson(tokenServiceResponse, BkoRequestResponse.class);
                        if (response != null) {
                            if (response.isResponse()) {
                                if (!response.getMessage().equals(getString(R.string.response_no_parameters))){
                                    Toast.makeText(BkoMainBaseActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                if (!response.getMessage().equals(getString(R.string.response_no_parameters))){
                                    Toast.makeText(BkoMainBaseActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        } else {
                            Toast.makeText(BkoMainBaseActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(BkoMainBaseActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(BkoMainBaseActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();

                }

            }
        });
    }
    protected AlertDialog checkNotVitalService(final Context context,  View.OnClickListener listener) {

        final AlertDialog dialog = AlertaTextView(context.getString(R.string.blako_advertencia), context.getString(R.string.blako_error), context,
                null, context.getString(R.string.blako_reintentar), null);

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(listener);

        return dialog;
    }


    protected void sendOneLodation() {
        executeInBackground(new Runnable() {
            public void run() {
                try {
                    BkoSendLocationToServer.oneLocationDataToWebsite(BkoDataMaganer.getCurrentUserLocation(getApplicationContext()), getApplicationContext());
                } catch (Exception e) {

                    e.fillInStackTrace();
                }
            }
        }, new Runnable() {
            public void run() {

            }
        });
    }

    protected void checkForUpdates() {
        sendOneLodation();
        int status = BkoDataMaganer.getStatusService(BkoMainBaseActivity.this);
        if (status == Constants.SERVICE_STATUS_FREE) {
            showWaitDialogWhileExecuting("Cargando", new Runnable() {
                public void run() {

                    try {
                        versionResponse = null;
                        Gson gson = new Gson();
                        versionResponse = HttpRequest
                                .get(Constants.URL_VERSION + "versionCode=" + BkoUtilities.getVersionCode(BkoMainBaseActivity.this) + "&versionName=" + BkoUtilities.getVersionName(BkoMainBaseActivity.this))
                                .connectTimeout(3000).readTimeout(3000).body();

                        BkoDataMaganer.setStatusService(Constants.SERVICE_STATUS_FREE, BkoMainBaseActivity.this);
                        if (versionResponse == null)
                            return;
                        versionResponseVO = gson.fromJson(versionResponse, BkoVersionVO.class);
                        if (versionResponseVO == null || !versionResponseVO.isResponse())
                            return;
                    } catch (Exception e) {

                        e.fillInStackTrace();
                    }

                }
            }, new Runnable() {
                public void run() {

                    if (versionResponseVO != null) {
                        if (!versionResponseVO.isResponse())
                        {
                            final AlertDialog dialog = AlertaTextView(getString(R.string.blako_advertencia), versionResponseVO.getMessage(), BkoMainBaseActivity.this,
                                    null, getString(R.string.blako_aceptar), null);

                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                    try {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                    } catch (android.content.ActivityNotFoundException anfe) {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                    }

                                }
                            });
                        }
                    }
                }
            });

        }
    }
}

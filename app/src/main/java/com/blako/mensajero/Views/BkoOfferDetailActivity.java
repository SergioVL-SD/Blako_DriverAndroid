package com.blako.mensajero.Views;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blako.mensajero.Adapters.BkoServicesAdapter;
import com.blako.mensajero.App;
import com.blako.mensajero.BkoDataMaganer;
import com.blako.mensajero.Constants;
import com.blako.mensajero.Custom.BkoMapFragment;
import com.blako.mensajero.Dao.BkoUserDao;
import com.blako.mensajero.R;
import com.blako.mensajero.Utils.BkoUtilities;
import com.blako.mensajero.Utils.HttpRequest;
import com.blako.mensajero.Utils.LogUtils;
import com.blako.mensajero.VO.BkoOffer;
import com.blako.mensajero.VO.BkoPushRequest;
import com.blako.mensajero.VO.BkoRequestResponse;
import com.blako.mensajero.VO.BkoUser;
import com.blako.mensajero.VO.BkoVehicleVO;
import com.blako.mensajero.VO.BkoVehiclesResponse;
import com.blako.mensajero.models.Fence;
import com.blako.mensajero.repositories.Repository;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.nineoldandroids.view.ViewHelper;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by franciscotrinidad on 1/11/16.
 */
public class BkoOfferDetailActivity extends BaseActivity implements OnMapReadyCallback, ObservableScrollViewCallbacks {

    Repository repository = App.getInstance().getRepository();

    private AlertDialog dialogPending;
    private BkoPushRequest statusRequest;
    private ImageButton wazeBt, googlemapsBt;
    private TextView announcementActionTv, dateTv, dateRangeTv, turnsTv, earnTv, warrantyTv, addressTv;
    private boolean isDeliveryAddress;
    private String latitud, longitud;
    private View priceLl;
    public static BkoOffer.BkoAnnoucement selectedItem;
    private String offersResponse;
    private Button takeTurnBtn;
    private Button cancelBtn;
    private TextView mTitle, minPayTv, earnHourTv, typeOTv;
    private GoogleMap map = null;
    private BkoMapFragment mapFrag;
    private ImageView typeVechicleIv;
    private TextView kmTv;
    private View mapView;
    private ObservableScrollView mScrollView;
    private String responseWorkerVehicles, setVehicleResponse;
    private ImageView refreshIv;
    private TextView noResultsTv, cancelTermsTv, showMapTv;
    private BkoVehiclesResponse vehicles;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView vehiclesLv;
    private BkoServicesAdapter mAdapter;
    private ImageView logoTakenIv;
    private View mapaLl;
    public static String lastIdOfferTaked;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bko_offer_detail_activity);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            mTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
            mTitle.setTextColor(ContextCompat.getColor(this, R.color.blako_white));
            mTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        }

        mapaLl = (View) findViewById(R.id.mapaLl);
        announcementActionTv = (TextView) findViewById(R.id.announcementActionTv);
        dateTv = (TextView) findViewById(R.id.dateTv);
        dateRangeTv = (TextView) findViewById(R.id.dateRangeTv);
        turnsTv = (TextView) findViewById(R.id.turnsTv);
        earnTv = (TextView) findViewById(R.id.earnTv);
        typeOTv = (TextView) findViewById(R.id.typeOTv);
        addressTv = (TextView) findViewById(R.id.addressTv);
        mapFrag = (BkoMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapaFt);
        mapFrag.getMapAsync(this);
        takeTurnBtn = (Button) findViewById(R.id.takeTurnBtn);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        earnHourTv = (TextView) findViewById(R.id.earnHourTv);
        kmTv = (TextView) findViewById(R.id.kmTv);
        typeVechicleIv = (ImageView) findViewById(R.id.typeVechicleIv);
        logoTakenIv = (ImageView) findViewById(R.id.logoTakenIv);
        takeTurnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeTurn();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        try {
            announcementActionTv.setText(selectedItem.getBko_campaign_title());
            dateTv.setText(BkoUtilities.formatToYesterdayOrToday(selectedItem.getBko_announcement_datetimestart()));

            String typeTv = "";
            String rangeTime = "";

            if (selectedItem.getBko_announcement_finishstatus().equals("1")) {
                typeTv = "TURNO";
                rangeTime =
                        BkoUtilities.formateDateFromstring(selectedItem.getBko_announcement_datetimestart()).replace(":00 ", " ").toUpperCase().replace("PM", "").replace("P.M.", "").replace("AM", "").replace("A.M.", "") + " - " +
                                BkoUtilities.formateDateFromstring(selectedItem.getBko_announcement_datetimefinish()).replace(":00 ", " ");

            } else {
                typeTv = "CARGA";
                rangeTime =
                        BkoUtilities.formateDateFromstring(selectedItem.getBko_announcement_datetimestart()).replace(":00 ", " ");
            }

            mTitle.setText(selectedItem.getBko_announcementaddress_alias());
            typeOTv.setText(typeTv);
            dateRangeTv.setText(" " + rangeTime);
            String address = "";

            if (selectedItem.getBko_announcementaddress_street() != null && selectedItem.getBko_announcementaddress_street().length() != 0)
                address += selectedItem.getBko_announcementaddress_street() + " ";

            if (selectedItem.getBko_announcementaddress_numext() != null && selectedItem.getBko_announcementaddress_numext().length() != 0)
                address += selectedItem.getBko_announcementaddress_numext() + " ";

            if (selectedItem.getBko_announcementaddress_numint() != null && selectedItem.getBko_announcementaddress_numint().length() != 0)
                address += selectedItem.getBko_announcementaddress_numint() + " ";

            if (address.length() != 0)
                address += ",";


            if (selectedItem.getBko_announcementaddress_neighborhood() != null && selectedItem.getBko_announcementaddress_neighborhood().length() != 0)
                address += selectedItem.getBko_announcementaddress_neighborhood() + ", ";

            if (selectedItem.getBko_announcementaddress_province() != null && selectedItem.getBko_announcementaddress_province().length() != 0)
                address += selectedItem.getBko_announcementaddress_province() + ", ";
            if (selectedItem.getBko_announcementaddress_state() != null && selectedItem.getBko_announcementaddress_state().length() != 0)
                address += selectedItem.getBko_announcementaddress_state() + ", ";

            addressTv.setText(address);

            turnsTv.setText(selectedItem.getBko_announcement_numberworkers());
            //warrantyTv.setText(selectedItem.getBko_announcement_numberservices() + " servicios");
            earnTv.setText("$" + selectedItem.getBko_announcement_bid());
            earnHourTv.setText("$" + selectedItem.getBko_announcement_costhourguaranteed());
            if (selectedItem.getServicetaken().equals("1")) {
                takeTurnBtn.setVisibility(View.INVISIBLE);

            }

            try {

                Integer services = Integer.parseInt(selectedItem.getBko_announcement_numberservices());
                Float payService = Float.parseFloat(selectedItem.getBko_announcement_bid());
                Float total = services * payService;
                Location lastLocation = BkoDataMaganer.getCurrentUserLocation(this);
                if (lastLocation != null) {
                    BkoOffer.BkoAnnoucement offer = BkoOfferDetailActivity.selectedItem;


                    Double lat = Double.parseDouble(offer.getBko_announcementaddress_lat());
                    Double lng = Double.parseDouble(offer.getBko_announcementaddress_lng());
                    Location location = new Location("");
                    location.setLatitude(lat);
                    location.setLongitude(lng);

                    float distanceKm = location.distanceTo(lastLocation) / 1000;

                    kmTv.setText("" + Math.round(distanceKm));
                    Picasso.with(this).load(selectedItem.getBko_customer_logo()).resize(150, 150).noFade().placeholder(ContextCompat.getDrawable(this, R.drawable.avatar)).networkPolicy(NetworkPolicy.NO_CACHE).into(logoTakenIv);

                }

            } catch (Exception e) {

            }

            typeVechicleIv.setColorFilter(ContextCompat.getColor(this, R.color.blako_white));

            if (selectedItem.getBko_vehiclestype_id() != null) {
                if (selectedItem.getBko_vehiclestype_id().equals("1")) {
                    typeVechicleIv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.motoc));
                } else if (selectedItem.getBko_vehiclestype_id().equals("2")) {
                    typeVechicleIv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bike));
                } else if (selectedItem.getBko_vehiclestype_id().equals("3")) {
                    typeVechicleIv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.car));
                } else if (selectedItem.getBko_vehiclestype_id().equals("4")) {
                    typeVechicleIv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.van));
                }
            }

            String cancelTerms = getString(R.string.blako_detalle_oferta_cargos);


            cancelTermsTv.setText(cancelTerms);
        } catch (Exception e) {

        }

//  -------------------------------------
        repository.getFences().observe(this, new Observer<List<Fence>>() {
            @Override
            public void onChanged(@Nullable List<Fence> fences) {
                if(fences != null)
                {
                    LogUtils.debug(this.getClass().getSimpleName(), String.valueOf(fences.size()) + " were loaded");
                }
            }
        });
        //  -------------------------------------
    }


    private void takeTurn() {


        try {
            BkoVehicleVO vehicleVO = BkoDataMaganer.getCurrentVehicle(this);

            if (vehicleVO.getTypeid() == null) {
                showVechiclesView();
                Toast.makeText(this, "Actualiza tu vehículo para tomar esta oferta", Toast.LENGTH_LONG).show();
                return;
            }
        } catch (Exception e) {

        }


        AlertaTextViewListener("Tomar Turno", "¿Realmente desea tomar esta oferta?", this,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showWaitDialogWhileExecuting("Cargando...", new Runnable() {
                            public void run() {
                                // TODO: 29/06/2018 Aquí se aceptan las ordenes

                                try {

                                    offersResponse = null;


                                    BkoUser user = BkoUserDao.Consultar(BkoOfferDetailActivity.this);
                                    offersResponse = HttpRequest
                                            .get(Constants.GET_OFFER(BkoOfferDetailActivity.this) + "workerId=" + user.getWorkerId() + "&announcementId=" + selectedItem.getBko_announcement_id())
                                            .connectTimeout(8000).body();


                                } catch (Exception e) {

                                    e.fillInStackTrace();
                                }

                            }
                        }, new Runnable() {
                            public void run() {


                                if (offersResponse != null) {
                                    try {


                                        Gson gson = new Gson();
                                        BkoRequestResponse user = gson.fromJson(offersResponse, BkoRequestResponse.class);

                                        if (user.isResponse()) {
                                            BkoOfferDetailActivity.lastIdOfferTaked = selectedItem.getBko_announcement_id();
                                            Toast.makeText(BkoOfferDetailActivity.this, "" + user.getMessage(), Toast.LENGTH_SHORT).show();
                                            finishAffinity();
                                            Intent intent = new Intent(BkoOfferDetailActivity.this, BkoMainActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(BkoOfferDetailActivity.this, "" + user.getMessage(), Toast.LENGTH_SHORT).show();

                                        }


                                    } catch (Exception e) {
                                        Toast.makeText(BkoOfferDetailActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();
                                        e.fillInStackTrace();
                                    }

                                } else {


                                    Toast.makeText(BkoOfferDetailActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();

                                }

                            }
                        });


                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                }, getString(R.string.blako_aceptar), getString(R.string.blako_cancelar), null);


    }


    @Override
    public void onMapReady(GoogleMap arg0) {
        map = arg0;

        if (map != null) {
            map.getUiSettings().setZoomControlsEnabled(true);
            BkoOffer.BkoAnnoucement offer = BkoOfferDetailActivity.selectedItem;


            if (offer == null) {
                Intent intent = new Intent(BkoOfferDetailActivity.this, BkoMainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finishAffinity();
                return;
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            Double lat = Double.parseDouble(offer.getBko_announcementaddress_lat());
            Double lng = Double.parseDouble(offer.getBko_announcementaddress_lng());
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_white)).title(offer.getBko_announcementaddress_alias()));

            map.setMyLocationEnabled(true);
            LatLng latLng = new LatLng(lat, lng);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(Constants.DEFAULT_MAP_ZOOM).build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            map.animateCamera(cameraUpdate, 100, null);
            map.getUiSettings().setScrollGesturesEnabled(true);
            map.getUiSettings().setAllGesturesEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);

        }


    }

    private void showVechiclesView() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = ((Activity) this).getLayoutInflater();
        View view = inflater.inflate(R.layout.bko_vehicles_alert, null);
        builder.setView(view);
        builder.setCancelable(false);


        final Button aceptBtn = (Button) view.findViewById(R.id.aceptBtn);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.activity_main_swipe_refresh_layout);
        refreshIv = (ImageView) view.findViewById(R.id.refreshIv);
        noResultsTv = (TextView) view.findViewById(R.id.noResultsTv);

        vehiclesLv = (ListView) view.findViewById(R.id.servicesLv);

        vehiclesLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (vehicles == null)
                    return;

                setVehicle(position);


            }

        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshIv.setVisibility(View.GONE);
                noResultsTv.setVisibility(View.GONE);
                setData();

                //cancelBt.setVisible(true);
            }
        });

        refreshIv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                refreshIv.setVisibility(View.GONE);
                noResultsTv.setVisibility(View.GONE);
                setData();
            }

        });

        dialogPending = builder.create();
        dialogPending.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogPending.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialogPending.getWindow().setAttributes(lp);


        aceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dialogPending.dismiss();


            }
        });
        setData();
    }

    private void setData() {


        showWaitDialogWhileExecuting("Cargando...", new Runnable() {
            public void run() {

                try {
                    vehicles = null;
                    responseWorkerVehicles = null;
                    BkoUser user = BkoUserDao.Consultar(BkoOfferDetailActivity.this);

                    String workerId;
                    if (user != null) {
                        if (user.getWorkerParentId().equals("0"))
                            workerId = user.getWorkerId();
                        else
                            workerId = user.getWorkerParentId();

                        responseWorkerVehicles = HttpRequest.get(Constants.GET_VEHICLES_UR(BkoOfferDetailActivity.this) + "workerId=" + workerId).connectTimeout(5000).readTimeout(5000).body();
                    }
                } catch (Exception e) {

                }

            }
        }, new Runnable() {
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                if (responseWorkerVehicles != null) {

                    try {
                        Gson gson = new Gson();

                        vehicles = gson.fromJson(responseWorkerVehicles, BkoVehiclesResponse.class);
                        if (vehicles != null) {

                            if (vehicles.isResponse() && vehicles.getVehicles().size() > 0) {
                                mAdapter = new BkoServicesAdapter(BkoOfferDetailActivity.this, vehicles.getVehicles());
                                vehiclesLv.setAdapter(mAdapter);
                            } else {
                                noResultsTv.setVisibility(View.VISIBLE);
                            }
                        } else {
                            refreshIv.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        refreshIv.setVisibility(View.VISIBLE);
                        //noResultsTv.setVisibility(View.VISIBLE);
                    }

                } else {
                    refreshIv.setVisibility(View.VISIBLE);
                    //noResultsTv.setVisibility(View.VISIBLE);
                }

            }
        });

    }


    private void setVehicle(final int position) {

        showWaitDialogWhileExecuting("Cargando...", new Runnable() {
            public void run() {

                try {

                    BkoUser user = BkoUserDao.Consultar(BkoOfferDetailActivity.this);
                    setVehicleResponse = null;
                    String workerId;
                    workerId = user.getWorkerId();

                    BkoVehicleVO currentVehicle = vehicles.getVehicles().get(position);


                    Map<String, String> mapVisible = new HashMap<String, String>();
                    mapVisible.put("workerId", workerId);
                    mapVisible.put("vehiclesId", currentVehicle.getId());
                    mapVisible.put("carbrand", currentVehicle.getBrand());
                    mapVisible.put("carmodel", currentVehicle.getModel());


                    setVehicleResponse = HttpRequest.get(Constants.GET_SET_VEHICLE(BkoOfferDetailActivity.this), mapVisible, true).connectTimeout(5000).readTimeout(5000).body();


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Runnable() {
            public void run() {


                try {

                    if (setVehicleResponse != null) {

                        Gson gson = new Gson();

                        BkoRequestResponse response = gson.fromJson(setVehicleResponse, BkoRequestResponse.class);

                        if (response.isResponse()) {

                            try {

                                dialogPending.dismiss();

                                BkoVehicleVO currentVehicle = vehicles.getVehicles().get(position);
                                BkoDataMaganer.setCurrentVehicle(currentVehicle, BkoOfferDetailActivity.this);


                            } catch (Exception e) {


                            }
                        } else {
                            Toast.makeText(BkoOfferDetailActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        Toast.makeText(BkoOfferDetailActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(BkoOfferDetailActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();
                }

            }


            //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.

        });

    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        ViewHelper.setTranslationY(mapaLl, scrollY / 2);
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("GeneralFlux","Activity: "+BkoOfferDetailActivity.this.getLocalClassName());
    }
}


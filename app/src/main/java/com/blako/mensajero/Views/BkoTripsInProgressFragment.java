package com.blako.mensajero.Views;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blako.mensajero.Adapters.BkoTripsAdapter;
import com.blako.mensajero.BkoCore;
import com.blako.mensajero.BkoDataMaganer;
import com.blako.mensajero.Constants;
import com.blako.mensajero.Custom.BkoSeekBar;
import com.blako.mensajero.Custom.VerticallyScrollRecyclerView;
import com.blako.mensajero.Dao.BkoUserDao;
import com.blako.mensajero.R;
import com.blako.mensajero.Services.BkoSendLocationToServer;
import com.blako.mensajero.Utils.BkoUtilities;
import com.blako.mensajero.Utils.HttpRequest;
import com.blako.mensajero.VO.BkoCheckoutResponse;
import com.blako.mensajero.VO.BkoChildTripVO;
import com.blako.mensajero.VO.BkoOffer;
import com.blako.mensajero.VO.BkoPushRequest;
import com.blako.mensajero.VO.BkoRecoverOrderStatusVO;
import com.blako.mensajero.VO.BkoRecoverStatusVO;
import com.blako.mensajero.VO.BkoRequestResponse;
import com.blako.mensajero.VO.BkoTripVO;
import com.blako.mensajero.VO.BkoTrips;
import com.blako.mensajero.VO.BkoUser;
import com.blako.mensajero.VO.BkoUserStatusResponse;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * Created by franciscotrinidad on 1/12/16.
 */
public class BkoTripsInProgressFragment extends BaseFragment implements BkoCore.OffersTripsListener {
    private ListView servicesLv;
    private ImageView refreshIv;
    private TextView noResultsTv;
    private BkoTrips tripsRespone;
    private BkoTripsAdapter mAdapter;
    private String pushType;
    private String responseOrderStatus;
    private TextView manualTripBt, qrBt, tvNoOffers;
    private String manualTripResponse;
    private MenuItem cancelBt;
    private String cancelServiceResponse;
    private String awaiteServiceResponse;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View rootView;
    private BkoPushRequest statusRequest;
    private String statusResponse;
    private BkoRecoverStatusVO recoverStatusVO;
    private boolean loading = false;
    private View seekOfferLl;
    private long end;
    private long mLastClickTime = 0;
    private BkoSeekBar checktoutSB;
    private View timeLl;
    private String visibleReponse;
    private TextView timeEndTv, timeBeginTv, timeBeginStatusTv;
    private CountDownTimer timer;
    public static boolean onTripsList = false;
    private View checkoutLl;
    private boolean offerHasFinished;
    private RecyclerView tripsRv;
    private boolean InternetProblem = false;
    public static boolean acepted  = false;
    private int lasPosition;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        BkoCore.setoffersTripsListener(this);
        BkoTripsInProgressFragment.acepted = false;
        BkoTripDetailActivity.confirmed = true;
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.bko_trips_progress_activity, null);
            setHasOptionsMenu(true);
            seekOfferLl = (View) rootView.findViewById(R.id.seekOfferLl);
            refreshIv = (ImageView) rootView.findViewById(R.id.refreshIv);
            noResultsTv = (TextView) rootView.findViewById(R.id.noResultsTv);
            servicesLv = (ListView) rootView.findViewById(R.id.servicesLv);
            manualTripBt = (TextView) rootView.findViewById(R.id.manualTripBt);
            qrBt = (TextView) rootView.findViewById(R.id.qrBt);
            timeLl = (View) rootView.findViewById(R.id.timeLl);
            timeEndTv = (TextView) rootView.findViewById(R.id.timeEndTv);
            checktoutSB = (BkoSeekBar) rootView.findViewById(R.id.checktoutSB);
            timeBeginStatusTv = (TextView) rootView.findViewById(R.id.timeBeginStatusTv);
            timeBeginTv = (TextView) rootView.findViewById(R.id.timeBeginTv);
            tvNoOffers= (TextView) rootView.findViewById(R.id.tvNoOffers);
            checkoutLl = (View) rootView.findViewById(R.id.checkoutLl);
            tripsRv = (VerticallyScrollRecyclerView) rootView.findViewById(R.id.tripsRv);
            tripsRv.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutOffersManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            tripsRv.setLayoutManager(layoutOffersManager);


            mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.activity_main_swipe_refresh_layout);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    servicesLv.setAdapter(null);
                    setListViewHeightBasedOnChildren(servicesLv);
                    refreshIv.setVisibility(View.INVISIBLE);
                    noResultsTv.setVisibility(View.INVISIBLE);
                    setData(false);
                }
            });

            checkoutLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (seekOfferLl.getVisibility() == View.VISIBLE)
                        seekOfferLl.setVisibility(View.GONE);
                    else
                        seekOfferLl.setVisibility(View.VISIBLE);
                }
            });


            refreshIv.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    refreshIv.setVisibility(View.GONE);
                    noResultsTv.setVisibility(View.GONE);
                    setData(true);
                }

            });

            servicesLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    onSelected(position);

                }

            });



            try {
                getActivity().registerReceiver(mPushReceiver, new IntentFilter("trips"));


            } catch (Exception exc) {
                exc.printStackTrace();
            }


        }

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
                                                           if (!BkoUtilities.checkCorrectDate(getActivity())) {
                                                               checktoutSB.setProgress(10);
                                                               return;
                                                           }
                                                           checktoutSB.setProgress(10);
                                                           checktoutSB.setEnabled(false);
                                                           showCheckoutAlertView();

                                                       }
                                                   }

                                                   @Override
                                                   public void onStartTrackingTouch(SeekBar seekBar) {

                                                   }

                                                   @Override
                                                   public void onStopTrackingTouch(SeekBar seekBar) {

                                                       if (seekBar.getProgress() <= 80)
                                                           seekBar.setProgress(10);

                                                   }
                                               }

        );




        return rootView;
    }

    private void onSelected(int position){
        if (tripsRespone == null || tripsRespone.getTrips() == null)
            return;

        try {

            if (tripsRespone.getTrips().get(position).getOrigen().get(0).getBko_requeststatus_id() != null && tripsRespone.getTrips().get(position).getOrigen().get(0).getBko_requeststatus_id().equals("1")) {
                onReportAwaite(tripsRespone.getTrips().get(position).getOrigen().get(0), tripsRespone.getTrips().get(position).getDestino().get(0));
                return;
            }

            BkoTripVO trip = tripsRespone.getTrips().get(position);
            BkoDataMaganer.setCuurentTemporaryTrip(trip);
            Intent intent = new Intent(getActivity(), BkoTripDetailActivity.class);
            startActivityForResult(intent, 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                servicesLv.setAdapter(null);
                setListViewHeightBasedOnChildren(servicesLv);
                refreshIv.setVisibility(View.INVISIBLE);
                noResultsTv.setVisibility(View.INVISIBLE);
                setData(true);
            }
        }

    }



    private BroadcastReceiver mPushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {


            try {
                pushType = intent.getStringExtra("type");
                if (pushType.equals("consulttrips")) {
                    servicesLv.setAdapter(null);
                    setListViewHeightBasedOnChildren(servicesLv);
                    refreshIv.setVisibility(View.INVISIBLE);
                    noResultsTv.setVisibility(View.INVISIBLE);
                    setData(true);
                }
            } catch (Exception e) {

            }


        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(mPushReceiver);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setData(boolean withMsg) {
        if (loading)
            return;
        BkoTripsInProgressFragment.acepted = false;

        String msg = "Cargando...";

        if (!withMsg)
            msg = null;
        loading = true;
        servicesLv.setAdapter(null);
        setListViewHeightBasedOnChildren(servicesLv);
        showWaitDialogWhileExecuting(msg, new Runnable() {
            public void run() {

                try {
                    InternetProblem = false;
                    tripsRespone = null;
                    Gson gson = new Gson();
                    statusRequest = BkoDataMaganer.getCurrentStatusRequest(getActivity());
                    BkoUser user = BkoUserDao.Consultar(getActivity());
                    statusResponse = HttpRequest
                            .get(Constants.GET_TRIPS_ACTIVE(getActivity()) + "workerId=" + user.getWorkerId())
                            .connectTimeout(4000).readTimeout(4000).body();

                    if (statusResponse != null) {
                        recoverStatusVO = gson.fromJson(statusResponse, BkoRecoverStatusVO.class);
                        if (recoverStatusVO.isResponse()) {
                            BkoLoginActivity.setData(null, recoverStatusVO, getActivity());
                            statusRequest = BkoDataMaganer.getCurrentStatusRequest(getActivity());
                        }
                    }

                    if (statusRequest != null) {
                        String tripsReponse = HttpRequest
                                .get(Constants.GET_GO_TRIPS(getActivity()) + "oId=" + statusRequest.getOid() + "&workerId=" + user.getWorkerId())
                                .connectTimeout(5000).readTimeout(5000).body();
                        if (tripsReponse != null) {
                            tripsRespone = gson.fromJson(tripsReponse, BkoTrips.class);
                        }

                        if (BkoDataMaganer.getStatusService(getActivity()) == Constants.SERVICE_STATUS_TRIP_FINISHED || BkoDataMaganer.getStatusService(getActivity()) == Constants.SERVICE_STATUS_WORKER_ARRIVED) {
                            user = BkoUserDao.Consultar(getActivity());
                            responseOrderStatus = null;
                            responseOrderStatus = HttpRequest.get(Constants.GET_STATUS_REQUEST_URL(getActivity()) + "connectToken=" + user.getConectToken() + "&oid=" + statusRequest.getOid() + "&workerId=" + user.getWorkerId()).connectTimeout(2500).body();

                        }


                        else if (BkoDataMaganer.getOnDemand(getActivity()) ){
                            responseOrderStatus = null;
                            responseOrderStatus = HttpRequest.get(Constants.GET_STATUS_REQUEST_URL(getActivity()) + "connectToken=" + user.getConectToken() + "&oid=" + statusRequest.getOid() + "&workerId=" + user.getWorkerId()).connectTimeout(2500).body();

                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    InternetProblem = true;

                }


            }
        }, new Runnable() {
            public void run() {
                servicesLv.setAdapter(null);

                boolean checkStatusOK = true;


                if(getActivity()!=null && getActivity() instanceof  BkoMainActivity){
                    ((BkoMainActivity)getActivity()).setNumberOrder(0);
                }

                if (BkoDataMaganer.getStatusService(getActivity()) == Constants.SERVICE_STATUS_TRIP_FINISHED) {
                    if (responseOrderStatus == null)
                        checkStatusOK = false;
                }


                if (tripsRespone != null && checkStatusOK) {

                    try {


                        if (   (tripsRespone.getTrips() != null && tripsRespone.getTrips().size() != 0)) {

                            if(getActivity()!=null && getActivity() instanceof  BkoMainActivity){
                                ( (BkoMainActivity)getActivity()).setNumberOrder(tripsRespone.getTrips().size() );
                            }



                            if (BkoTripsAdapter.mp != null && BkoTripsAdapter.mp.isPlaying())
                                BkoTripsAdapter.mp.stop();

                            if (tripsRespone.getTrips().get(0).getOrigen() == null)
                                tripsRespone.getTrips().remove(0);

                            if ( BkoDataMaganer.getOnDemand(getActivity()) && BkoDataMaganer.getCurrentOffer(getActivity()) == null ) {

                                int i =0;
                                for(BkoTripVO tripPArent: tripsRespone.getTrips()){
                                    BkoChildTripVO trip = tripPArent.getOrigen().get(0);
                                    BkoChildTripVO tripDelivery = tripPArent.getDestino().get(0);
                                    if (trip.getBko_orders_trips_completeddatetime() != null && trip.getBko_orders_trips_completeddatetime().length() != 0) {

                                    } else {
                                        if (tripDelivery.getBko_requeststatus_id() != null && tripDelivery.getBko_requeststatus_id().equals("2")) {

                                        } else {
                                            if(!BkoRequestActivity.onRequest ){
                                                BkoRequestActivity.onRequest = true;
                                                getActivity().finishAffinity();
                                                BkoRequestActivity.tripP =  tripPArent;
                                                BkoRequestActivity.trip =  trip;
                                                BkoRequestActivity.tripDelivery =  tripDelivery;
                                                Intent intentMap = new Intent(getActivity(), BkoRequestActivity.class);
                                                getActivity().startActivity(intentMap);
                                                break;
                                            }

                                        }
                                    }
                                    lasPosition = i;
                                    i++;
                                }

                            }

                            mAdapter = new BkoTripsAdapter(getActivity(), tripsRespone.getTrips());


                            servicesLv.setAdapter(mAdapter);
                            tvNoOffers.setVisibility(View.GONE);
                            setListViewHeightBasedOnChildren(servicesLv);
                            BkoDataMaganer.setCurrentTrips(tripsRespone, getActivity());
                            tripsRespone.setTrips(tripsRespone.getTrips());


                            seekOfferLl.setVisibility(View.GONE);
                            if (offerHasFinished)
                                seekOfferLl.setVisibility(View.VISIBLE);

                            if ((BkoDataMaganer.getStatusService(getActivity()) == Constants.SERVICE_STATUS_TRIP_FINISHED || BkoDataMaganer.getStatusService(getActivity()) == Constants.SERVICE_STATUS_WORKER_ARRIVED || BkoDataMaganer.getOnDemand( getActivity())) && responseOrderStatus != null) {
                                Gson gson = new Gson();
                                BkoRecoverOrderStatusVO orderStatusVO = gson.fromJson(responseOrderStatus, BkoRecoverOrderStatusVO.class);

                                if (orderStatusVO != null && orderStatusVO.isResponse()) {

                                    Integer deliveryStatus = Integer.parseInt(orderStatusVO.getDeliverystatus());

                                    if ((deliveryStatus == 5 || deliveryStatus == 7 )&& ( recoverStatusVO.getTrips()==null )){//||recoverStatusVO.getTrips().size()==0) ){
                                        BkoDataMaganer.setCuurentTemporaryTrip(null);
                                        BkoDataMaganer.setCurrentTrip(null, getActivity());
                                        if(BkoDataMaganer.getCurrentOffer( getActivity())!=null)
                                            BkoDataMaganer.setStatusService(Constants.SERVICE_STATUS_WITH_OFFER_CHECKIN_FINISH, getActivity());
                                        BkoDataMaganer.setCurrentTrips(null, getActivity());
                                        AlertaTextView(null, getString(R.string.blako_destinos_terminados), getActivity(),
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        getActivity().finishAffinity();
                                                        Intent intent = new Intent(getActivity(), BkoMainActivity.class);
                                                        startActivity(intent);

                                                    }
                                                }, getString(R.string.blako_aceptar), null);
                                    }


                                }
                            }

                        } else {
                            if (BkoTripsAdapter.mp != null && BkoTripsAdapter.mp.isPlaying())
                                BkoTripsAdapter.mp.stop();
                            if (responseOrderStatus != null) {
                                Gson gson = new Gson();
                                BkoRecoverOrderStatusVO orderStatusVO = gson.fromJson(responseOrderStatus, BkoRecoverOrderStatusVO.class);
                                if (orderStatusVO != null && orderStatusVO.isResponse()) {
                                    Integer deliveryStatus = Integer.parseInt(orderStatusVO.getDeliverystatus());
                                    if ((deliveryStatus == 5 || deliveryStatus == 7)&& ( recoverStatusVO.getTrips()==null )){//||recoverStatusVO.getTrips().size()==0)) {
                                        BkoDataMaganer.setCurrentTrip(null, getActivity());
                                        if(BkoDataMaganer.getCurrentOffer( getActivity())!=null)
                                            BkoDataMaganer.setStatusService(Constants.SERVICE_STATUS_WITH_OFFER_CHECKIN_FINISH, getActivity());
                                        AlertaTextView(null, getString(R.string.blako_destinos_terminados), getActivity(),
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        getActivity().finishAffinity();
                                                        Intent intent = new Intent(getActivity(), BkoMainActivity.class);
                                                        startActivity(intent);
                                                    }
                                                }, getString(R.string.blako_aceptar), null);

                                        return;
                                    }
                                }
                            }
                            BkoDataMaganer.setCurrentTrip(null, getActivity());
                            BkoDataMaganer.setCurrentTrips(null, getActivity());

                            Toast.makeText(getActivity(), R.string.blako_sin_tareas, Toast.LENGTH_SHORT).show();


                        }


                    } catch (Exception e) {
                        setcancelOption();
                    }

                } else {
                    if(InternetProblem ){
                        Toast.makeText(getActivity(), getString(R.string.blako_error), Toast.LENGTH_SHORT).show();
                    }

                    if (BkoTripsAdapter.mp != null && BkoTripsAdapter.mp.isPlaying())
                        BkoTripsAdapter.mp.stop();
                    if (recoverStatusVO != null && !recoverStatusVO.isResponse()) {
                        setcancelOption();
                    }
                    tvNoOffers.setVisibility(View.VISIBLE);
                }
                mSwipeRefreshLayout.setRefreshing(false);
                loading = false;
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("GeneralFlux","Fragment: BkoTripsInProgressFragment");
        setCheckin();
        int appState = BkoDataMaganer.getStatusService(getActivity());
        if (appState == Constants.SERVICE_STATUS_WORKER_ARRIVED || appState == Constants.SERVICE_STATUS_TRIP_FINISHED || appState == Constants.SERVICE_STATUS_WITH_OFFER_CHECKIN_FINISH) {
            CountDownOffer();
        }


        if (BkoTripDetailActivity.confirmed) {

            setData(true);
            BkoTripDetailActivity.confirmed = false;
        }


        try {
            BkoUser user = BkoUserDao.Consultar(getActivity());
            if (appState == Constants.SERVICE_STATUS_FREE && user.isAvailable() && !BkoDataMaganer.getOnDemand(getActivity()))
                onLogout();

        } catch (Exception e) {

        }

        if(BkoTripsInProgressFragment.acepted){

            BkoDataMaganer.setCuurentTemporaryTrip(BkoRequestActivity.tripP);
            Intent intent = new Intent(getActivity(), BkoTripDetailActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(intent, 1);
            BkoTripsInProgressFragment.acepted = false;
            //onSelected(lasPosition);
            //BkoTripsInProgressFragment.acepted = false;
        }

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        int appState = BkoDataMaganer.getStatusService(getContext());
        if (appState >= Constants.SERVICE_STATUS_WORKER_ARRIVED && appState < Constants.SERVICE_STATUS_WITH_OFFER) {
            inflater.inflate(R.menu.menu_pedidos, menu);
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    public void setcancelOption() {

        if (cancelBt != null)
            cancelBt.setVisible(false);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.deliverysBt:
                if (BkoTripsInProgressFragment.onTripsList) {
                    setData(true);
                    return true;
                }

                getActivity().finishAffinity();
                Intent intentMap = new Intent(getActivity(), BkoMainActivity.class);
                startActivity(intentMap);
                break;

            case R.id.cancelOfferBt:
                AlertaTextViewListener(getString(R.string.blako_advertencia), getString(R.string.blako_cancelar_oferta), getActivity(),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onCancelOffer(4);
                            }
                        }, null, getString(R.string.blako_aceptar), getString(R.string.blako_rechazar), null);

                break;





            default:
                return super.onOptionsItemSelected(item);
        }/**/

        return super.onOptionsItemSelected(item);
    }


    private void onReportAwaite(final BkoChildTripVO childTripVO, final BkoChildTripVO childDeliveryTripVO) {
        showWaitDialogWhileExecuting("Cargando...", new Runnable() {
            public void run() {
                try {


                    BkoUser user = BkoUserDao.Consultar(getActivity());
                    awaiteServiceResponse = null;

                    String queuedtasksrequestId, tripIdOrigen, warehouseLatitude, warehouseLongitude, workerLatitude = null, workerLongitude = null;


                    queuedtasksrequestId = childTripVO.getBko_queuedtasksrequest_id();
                    tripIdOrigen = childTripVO.getBko_orders_trips_id();
                    warehouseLatitude = childTripVO.getBko_customeraddress_latitude();
                    warehouseLongitude = childTripVO.getBko_customeraddress_longitude();


                    Location userLocation = BkoDataMaganer.getCurrentUserLocation(getActivity());


                    String request = Constants.GET_CONFIRM_AWAIT(getActivity()) +
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


                    if (BkoSendLocationToServer.oneLocationDataToWebsite(BkoDataMaganer.getCurrentUserLocation(getActivity().getApplicationContext()), getActivity().getApplicationContext()))
                        awaiteServiceResponse = HttpRequest.get(request).connectTimeout(3000).readTimeout(3000).body();


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Runnable() {
            public void run() {
                if (awaiteServiceResponse != null) {

                    try {

                        Gson gson = new Gson();
                        BkoRequestResponse response = gson.fromJson(awaiteServiceResponse, BkoCheckoutResponse.class);

                        if (response != null) {
                            if (response.isResponse()) {
                                setData(true);
                            } else {
                                Toast.makeText(getActivity(), response.getMessage(), Toast.LENGTH_SHORT).show();


                            }
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.blako_error), Toast.LENGTH_SHORT).show();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), getString(R.string.blako_error), Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(getActivity(), getString(R.string.blako_error), Toast.LENGTH_SHORT).show();

                }

            }
        });
    }


    private void onCancelOffer(final int stage) {
        showWaitDialogWhileExecuting("Cargando...", new Runnable() {
            public void run() {
                Log.i("OnWay", "Arriving");
                try {


                    BkoOffer.BkoAnnoucement firstAnnounce = BkoDataMaganer.getCurrentOffer(getActivity());

                    BkoUser user = BkoUserDao.Consultar(getActivity());
                    cancelServiceResponse = null;
                    Location location = BkoDataMaganer.getCurrentUserLocation(getActivity());
                    String latitude = "";
                    String longitude = "";
                    if (location != null) {
                        latitude = "" + location.getLatitude();
                        longitude = "" + location.getLongitude();

                    }


                    cancelServiceResponse = HttpRequest.get(Constants.GET_OFFER_REPORT(getActivity()) +
                            "workerId=" + user.getWorkerId() +
                            "&announcementId=" + firstAnnounce.getBko_announcement_id() + "&stage=" + stage + "&parentId=" + user.getWorkerParentId() + "&latitude=" + latitude + "&longitude=" + longitude + "&announcementWorkerId=" + firstAnnounce.getBko_announcementworker_id()).connectTimeout(3000).readTimeout(3000).body();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Runnable() {
            public void run() {
                if (cancelServiceResponse != null) {

                    try {

                        Gson gson = new Gson();
                        BkoCheckoutResponse response = gson.fromJson(cancelServiceResponse, BkoCheckoutResponse.class);


                        if (response != null) {


                            if (response.isResponse() && response.getPayment() != null) {
                                BkoDataMaganer.setStatusService(Constants.SERVICE_STATUS_FREE, getActivity());
                                BkoDataMaganer.setCurrentOffer(null,getActivity());
                                showCheckoutPaymentAlertView(response.getPayment());
                                onLogout();
                            } else {
                                Toast.makeText(getActivity(), response.getMessage(), Toast.LENGTH_SHORT).show();


                            }
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.blako_error), Toast.LENGTH_SHORT).show();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), getString(R.string.blako_error), Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(getActivity(), getString(R.string.blako_error), Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    private void onLogout() {

        executeInBackground(new Runnable() {
            public void run() {

                try {
                    visibleReponse = null;

                    BkoUser user = BkoUserDao.Consultar(getActivity());

                    if (user != null) {
                        String timeregister = BkoUtilities.nowCurrent();
                        Map<String, String> mapVisible = new HashMap<String, String>();
                        mapVisible.put("workerEmail", user.getEmail());
                        mapVisible.put("status", "0");
                        mapVisible.put("workerId", user.getWorkerId());
                        mapVisible.put("timeregister", timeregister);

                        visibleReponse = HttpRequest.get(Constants.GET_SET_VISIBLE_URL(getActivity()), mapVisible, true).connectTimeout(5000).readTimeout(5000).body();

                        Location location = BkoDataMaganer.getCurrentUserLocation(getActivity());
                        String partnerId = user.getWorkerParentId();

                        if (partnerId == null || partnerId.equals("0"))
                            partnerId = user.getWorkerId();
                        else
                            partnerId = user.getWorkerParentId();
                        String responseUploadPostion = HttpRequest.get(Constants.GET_UPLOAD_GPS_POSITION(getActivity()) + "workerEmail=" + user.getEmail() + "&name="
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
                                BkoUser user = BkoUserDao.Consultar(getActivity());
                                user.setIsAvailable(!user.isAvailable());
                                BkoUserDao.Actualizar(getActivity(), user);
                            }
                        }
                    } catch (Exception e) {

                    }
                }

            }
        });

    }


    public void setListViewHeightBasedOnChildren(ListView listView) {

        try {

            BaseAdapter listAdapter = (BaseAdapter) listView.getAdapter();
            if (listAdapter == null) {
                ViewGroup.LayoutParams params = listView.getLayoutParams();
                params.height = 0;
                listView.setLayoutParams(params);
                listView.requestLayout();
                return;
            }

            int totalHeight = 0;
            for (int i = 0; i < listAdapter.getCount(); i++) {
                View listItem = listAdapter.getView(i, null, listView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
            listView.setLayoutParams(params);
            listView.requestLayout();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private void showCheckoutAlertView() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final FrameLayout frameView = new FrameLayout(getActivity());
        builder.setView(frameView);

        final AlertDialog alertDialog = builder.create();
        LayoutInflater inflater = alertDialog.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.bko_checkout_alert, frameView);
        alertDialog.setCancelable(false);
        Button cancel = (Button) dialoglayout.findViewById(R.id.cancelBtn);
        TextView checkoutClientTv = (TextView) dialoglayout.findViewById(R.id.checkoutClientTv);
        TextView checkoutTimeTv = (TextView) dialoglayout.findViewById(R.id.checkoutTimeTv);
        Button checkoutBtn = (Button) dialoglayout.findViewById(R.id.checkoutBtn);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                checktoutSB.setEnabled(true);
            }
        });


        BkoOffer.BkoAnnoucement firstAnnounce = BkoDataMaganer.getCurrentOffer(getActivity());
        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checktoutSB.setEnabled(true);
                alertDialog.dismiss();

                onCancelOffer(3);
            }
        });
        if (firstAnnounce != null && firstAnnounce.getBko_announcement_datetimefinish() != null) {
            Date now = new Date();
            float sp = 20;
            float px = sp * getResources().getDisplayMetrics().scaledDensity;
            Date dateAnnounce = BkoUtilities.getDate(firstAnnounce.getBko_announcement_datetimefinish(), "yyyy-MM-dd HH:mm:ss");
            end = dateAnnounce.getTime() - now.getTime();


            if (end > 0) {
                String seconds = ((end / 1000) % 60) < 10 ? "0" + ((end / 1000) % 60) : "" + ((end / 1000) % 60);
                String minutes = ((end / (1000 * 60)) % 60) < 10 ? "0" + ((end / (1000 * 60)) % 60) : "" + ((end / (1000 * 60)) % 60);
                String hours = ((end / (1000 * 60 * 60)) % 24) < 10 ? "0" + ((end / (1000 * 60 * 60)) % 24) : "" + ((end / (1000 * 60 * 60)) % 24);
                checkoutTimeTv.setText(getActivity().getString(R.string.blako_checkout_salir_checkout_error, "" + hours + ":" + minutes + ":" + seconds + " hrs."));


            } else {
                checkoutTimeTv.setText("");
            }

            checkoutClientTv.setText(getActivity().getString(R.string.blako_checkout_salir_checkout_error_client, "" + firstAnnounce.getBko_announcementaddress_alias()));

        }

        alertDialog.show();
    }


    private void showCheckoutPaymentAlertView(final String payment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final FrameLayout frameView = new FrameLayout(getActivity());
        builder.setView(frameView);

        final AlertDialog alertDialog = builder.create();
        LayoutInflater inflater = alertDialog.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.bko_checkout_payment_alert, frameView);
        alertDialog.setCancelable(false);
        Button aceptBtn = (Button) dialoglayout.findViewById(R.id.aceptBtn);
        TextView paymentTv = (TextView) dialoglayout.findViewById(R.id.paymentTv);


        aceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                getActivity().finishAffinity();
                Intent intent = new Intent(getActivity(), BkoMainActivity.class);
                startActivity(intent);
            }
        });

        paymentTv.setText(payment);

        alertDialog.show();
    }

    private void setCheckin() {


        BkoOffer.BkoAnnoucement selectedItem = BkoDataMaganer.getCurrentOffer(getActivity());

        if (selectedItem != null) {

            if (selectedItem.getTime() == null)
                selectedItem.setTime(selectedItem.getDate());

            String dateStart = BkoUtilities.JsonDate(selectedItem.getTime(), "yyyy-MM-dd HH:mm:ss", "h:mm:ss a");
            timeBeginTv.setText(dateStart);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date dateCheckin = simpleDateFormat.parse(selectedItem.getTime());
                Date dateOfferStart = simpleDateFormat.parse(selectedItem.getBko_announcement_datetimestart());
                Date dateTolerance = simpleDateFormat.parse(selectedItem.getMinutes_tolerance());

                if (dateCheckin.getTime() >= dateTolerance.getTime()) {
                    timeBeginTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.blako_red_two));
                    timeBeginStatusTv.setText(getActivity().getString(R.string.blako_mis_ofertas_llegada_tarde));
                } else if (dateCheckin.getTime() > dateOfferStart.getTime()) {
                    timeBeginTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.blako_orange));
                    timeBeginStatusTv.setText(getActivity().getString(R.string.blako_mis_ofertas_llegada_tolerancia));
                } else {
                    timeBeginTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.blako_green));
                    timeBeginStatusTv.setText(getActivity().getString(R.string.blako_mis_ofertas_llegada_tiempo));
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }


        } else {
            timeLl.setVisibility(View.GONE);
        }

    }


    private void CountDownOffer() {


        try {


            BkoOffer.BkoAnnoucement firstAnnounce = BkoDataMaganer.getCurrentOffer(getActivity());

            if (firstAnnounce == null)
                return;

            if (firstAnnounce.getBko_announcement_finishstatus() != null && firstAnnounce.getBko_announcement_finishstatus().equals("0")) {
                timeEndTv.setText("-");
                return;
            }


            Date now = new Date();
            float sp = 20;
            float px = sp * getResources().getDisplayMetrics().scaledDensity;
            Date dateAnnounce = BkoUtilities.getDate(firstAnnounce.getBko_announcement_datetimefinish(), "yyyy-MM-dd HH:mm:ss");
            end = dateAnnounce.getTime() - now.getTime();
            timeEndTv.setText("");
            offerHasFinished = false;
            if (timer != null)
                timer.cancel();

            if (end < 0) {

                offerHasFinished = true;
                timeLl.setVisibility(View.GONE);
                seekOfferLl.setVisibility(View.VISIBLE);

            } else {
                timer = new CountDownTimer(end, 1000) {

                    public void onTick(long milliseconds) {
                        String seconds = ((milliseconds / 1000) % 60) < 10 ? "0" + ((milliseconds / 1000) % 60) : "" + ((milliseconds / 1000) % 60);
                        String minutes = ((milliseconds / (1000 * 60)) % 60) < 10 ? "0" + ((milliseconds / (1000 * 60)) % 60) : "" + ((milliseconds / (1000 * 60)) % 60);
                        String hours = ((milliseconds / (1000 * 60 * 60)) % 24) < 10 ? "0" + ((milliseconds / (1000 * 60 * 60)) % 24) : "" + ((milliseconds / (1000 * 60 * 60)) % 24);
                        timeEndTv.setText(hours + ":" + minutes + ":" + seconds + " hrs.");
                    }

                    public void onFinish() {
                        timeLl.setVisibility(View.GONE);
                        seekOfferLl.setVisibility(View.VISIBLE);
                    }

                }.start();
            }

        } catch (Exception e) {

        }


    }

    @Override
    public void onCheckOut() {
        if (BkoUtilities.isMockLocationOn(getActivity())) {
            Toast.makeText(getActivity(), "Desactive el uso de ubicaciones simuladas!", Toast.LENGTH_SHORT).show();
            return;
        }

        Location location = BkoDataMaganer.getCurrentUserLocation(getActivity());
        if (location == null) {
            Toast.makeText(getActivity(), "No se ha podido obtener su ubicación", Toast.LENGTH_SHORT).show();
            return;
        }


        showCheckoutAlertView();
    }

}


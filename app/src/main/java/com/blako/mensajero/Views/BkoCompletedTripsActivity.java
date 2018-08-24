package com.blako.mensajero.Views;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.blako.mensajero.Adapters.BkoTripsAdapter;
import com.blako.mensajero.BkoDataMaganer;
import com.blako.mensajero.Constants;
import com.blako.mensajero.Dao.BkoUserDao;
import com.blako.mensajero.R;
import com.blako.mensajero.Utils.HttpRequest;
import com.blako.mensajero.VO.BkoPushRequest;
import com.blako.mensajero.VO.BkoTripVO;
import com.blako.mensajero.VO.BkoTrips;
import com.blako.mensajero.VO.BkoUser;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by franciscotrinidad on 20/04/17.
 */

public class BkoCompletedTripsActivity extends BaseActivity {

    private ListView servicesLv;
    private ImageView refreshIv;
    private TextView noResultsTv;
    private BkoTrips tripsRespone;
    private BkoTripsAdapter mAdapter;
    private String responseOrderStatus;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bko_completed_trips_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        if (toolbar != null) {
            setSupportActionBar(toolbar);
            boolean onRecover = getIntent().getBooleanExtra("onRecover", false);

            if (!onRecover) {
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        }


        refreshIv = (ImageView) findViewById(R.id.refreshIv);
        noResultsTv = (TextView) findViewById(R.id.noResultsTv);
        servicesLv = (ListView) findViewById(R.id.servicesLv);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                servicesLv.setAdapter(null);
                refreshIv.setVisibility(View.INVISIBLE);
                noResultsTv.setVisibility(View.INVISIBLE);
                setData(false);
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

        setData( true);

    }






    private void setData(boolean withMsg) {


        String msg="Cargando...";

        if(!withMsg)
            msg=null;


        showWaitDialogWhileExecuting(msg, new Runnable() {
            public void run() {

                try {

                    tripsRespone = null;
                    Gson gson = new Gson();
                    BkoPushRequest statusRequest = BkoDataMaganer.getCurrentStatusRequest(BkoCompletedTripsActivity.this);
                    String tripsReponse = HttpRequest
                            .get(Constants.GET_GO_TRIPS(BkoCompletedTripsActivity.this) + "oId=" + statusRequest.getOid())
                            .connectTimeout(6000).readTimeout(6000).body();
                    if (tripsReponse != null) {
                        tripsRespone = gson.fromJson(tripsReponse, BkoTrips.class);

                    }
                    if (BkoDataMaganer.getStatusService(BkoCompletedTripsActivity.this) == Constants.SERVICE_STATUS_TRIP_FINISHED) {

                        BkoUser user = BkoUserDao.Consultar(BkoCompletedTripsActivity.this);
                        responseOrderStatus = null;
                        responseOrderStatus = HttpRequest.get(Constants.GET_STATUS_REQUEST_URL(BkoCompletedTripsActivity.this) + "connectToken=" + user.getConectToken() + "&oid=" + statusRequest.getOid()).connectTimeout(5000).body();

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    return;

                }


            }
        }, new Runnable() {
            public void run() {


                boolean checkStatusOK = true;

                if (BkoDataMaganer.getStatusService(BkoCompletedTripsActivity.this) == Constants.SERVICE_STATUS_TRIP_FINISHED) {
                    if (responseOrderStatus == null)
                        checkStatusOK = false;
                }


                if (tripsRespone != null && checkStatusOK) {

                    try {


                        if (tripsRespone.isResponse() && tripsRespone.getTrips() != null && tripsRespone.getTrips().size() > 0) {
                            ArrayList<BkoTripVO> _trips = new ArrayList<BkoTripVO>();


                            for (BkoTripVO trip: tripsRespone.getTrips()){
                                //int status = Integer.parseInt(trip.getBko_deliverystatus_id());

                                //if(status==5)
                                //    _trips.add(trip);
                            }

                            mAdapter = new BkoTripsAdapter(BkoCompletedTripsActivity.this, _trips);


                            servicesLv.setAdapter(mAdapter);


                        } else {


                        }


                    } catch (Exception e) {
                      //  setcancelOption();
                        refreshIv.setVisibility(View.VISIBLE);
                        //manualTripBt.setVisibility(View.INVISIBLE);

                    }

                } else {
                   // setcancelOption();
                    refreshIv.setVisibility(View.VISIBLE);
                    //manualTripBt.setVisibility(View.INVISIBLE);
                    //noResultsTv.setVisibility(View.VISIBLE);
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("GeneralFlux","Activity: "+BkoCompletedTripsActivity.this.getLocalClassName());
    }
}

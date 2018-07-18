package com.blako.mensajero.Views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blako.mensajero.Adapters.BkoOffersDateAdapter;
import com.blako.mensajero.Constants;
import com.blako.mensajero.Custom.VerticallyScrollRecyclerView;
import com.blako.mensajero.Dao.BkoUserDao;
import com.blako.mensajero.R;
import com.blako.mensajero.Utils.HttpRequest;
import com.blako.mensajero.VO.BkoOfferDate;
import com.blako.mensajero.VO.BkoUser;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by franciscotrinidad on 11/09/17.
 */

public class BkoOffersByDateActivity extends BaseActivity implements BkoOffersDateAdapter.OffersDateListener {
    private List<BkoOfferDate> offers;
    private String offersResponse;
    protected VerticallyScrollRecyclerView OffersDateRv;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected ImageView refreshIv;
    protected TextView noResultsTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bko_offer_by_date);

        Toolbar   mToolbarView = (Toolbar) findViewById(R.id.toolbar);

        if (mToolbarView != null) {
            setSupportActionBar(mToolbarView);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            TextView mTitle = (TextView) mToolbarView.findViewById(R.id.toolbar_title);
            mTitle.setTextColor(ContextCompat.getColor(this, R.color.blako_white));
            mTitle.setText(getString(R.string.blako_oferta));

        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAnnoucements();

            }
        });
        refreshIv = (ImageView) findViewById(R.id.refreshIv);
        noResultsTv = (TextView) findViewById(R.id.noResultsTv);
        refreshIv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                refreshIv.setVisibility(View.GONE);
                noResultsTv.setVisibility(View.GONE);
                getAnnoucements();
            }

        });


        OffersDateRv = (VerticallyScrollRecyclerView) findViewById(R.id.OffersDateRv);
        refreshIv = (ImageView) findViewById(R.id.refreshIv);
        OffersDateRv.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutOffersManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        OffersDateRv.setLayoutManager(layoutOffersManager);
        OffersDateRv.setNestedScrollingEnabled(false);
        getAnnoucements();


        // ALARM FOR SOCKET AND REGISTER RECEIVERS
        try {
            registerReceiver(mPushReceiver, new IntentFilter("unique_name"));

        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("GeneralFlux","Activity: "+BkoOffersByDateActivity.this.getLocalClassName());
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

                if (pushType.equals("loadAnnouncement")) {

                    getAnnoucements();
                }
            } catch (Exception e) {

            }
        }
    };
    public static void updateMyActivity(Context context, String message, String type) {
        Intent intent = new Intent("unique_name");
        intent.putExtra("message", message);
        intent.putExtra("type", type);
        context.sendBroadcast(intent);
    }


    @Override
    public void onOfferListener(BkoOfferDate selectedItem, String title) {
        Intent intent = new Intent(
                this, BkoOffersByDayActivity.class);
        BkoOffersByDayActivity.title = title;

        BkoOffersByDayActivity.date = selectedItem.getDate();
        startActivityForResult(intent, 12);
    }


    private void getAnnoucements() {
        OffersDateRv.setAdapter(null);
        mSwipeRefreshLayout.setRefreshing(false);


        showWaitDialogWhileExecuting("Cargando",new Runnable() {
            public void run() {

                try {

                    offersResponse = null;

                    BkoUser user = BkoUserDao.Consultar(BkoOffersByDateActivity.this);
                    offersResponse = HttpRequest
                            .get(Constants.GET_OFFERS_DATE(BkoOffersByDateActivity.this) + "workerId=" + user.getWorkerId())
                            .connectTimeout(8000).body();


                    Gson gson = new Gson();
                    if (offersResponse != null) {

                        JSONObject object = new JSONObject(offersResponse);
                        offersResponse = object.getString("message");

                        offersResponse.replace("-", "");


                        boolean responseOk = object.getBoolean("response");

                        if (responseOk) {

                            offers = Arrays.asList(gson.fromJson(offersResponse, BkoOfferDate[].class));
                        }else{
                            offers = new ArrayList<>();
                        }



                    }


                } catch (Exception e) {

                    e.fillInStackTrace();
                }

            }
        }, new Runnable() {
            public void run() {

                if (offers != null) {

                    try {


                        OffersDateRv.setAdapter(null);
                        if (offers.size() != 0) {
                            List<BkoOfferDate>   _offers = new ArrayList<>();

                            for(BkoOfferDate offerDate : offers){


                                    _offers.add(offerDate);
                            }


                            BkoOffersDateAdapter adapter = new BkoOffersDateAdapter(_offers,BkoOffersByDateActivity.this);
                            OffersDateRv.setAdapter(adapter);
                        }
                        else{
                            noResultsTv.setVisibility(View.VISIBLE);
                            Toast.makeText(BkoOffersByDateActivity.this, getString(R.string.blako_offers_no_ofertas), Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.fillInStackTrace();
                        refreshIv.setVisibility(View.VISIBLE);
                        Toast.makeText(BkoOffersByDateActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    refreshIv.setVisibility(View.VISIBLE);
                    Toast.makeText(BkoOffersByDateActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();

                }
            }
        });


    }

}



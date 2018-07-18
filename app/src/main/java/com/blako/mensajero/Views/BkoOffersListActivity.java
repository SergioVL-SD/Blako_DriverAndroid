package com.blako.mensajero.Views;

import android.content.Intent;
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

import com.blako.mensajero.Adapters.BkoOffersGroupListAdapter;
import com.blako.mensajero.Adapters.BkoOffersListAdapter;
import com.blako.mensajero.Constants;
import com.blako.mensajero.Custom.VerticallyScrollRecyclerView;
import com.blako.mensajero.Dao.BkoUserDao;
import com.blako.mensajero.R;
import com.blako.mensajero.Utils.HttpRequest;
import com.blako.mensajero.VO.BkoOffer;
import com.blako.mensajero.VO.BkoUser;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by franciscotrinidad on 11/09/17.
 */

public class BkoOffersListActivity extends BaseActivity implements BkoOffersListAdapter.OffersListListener {
    public static List<BkoOffer> offers;
    private String offersResponse;
    public static String date;
    public static String customerId;
    public static String title;
    protected VerticallyScrollRecyclerView OffersDateRv;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected ImageView refreshIv;
    protected TextView noResultsTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bko_offer_list);

        Toolbar mToolbarView = (Toolbar) findViewById(R.id.toolbar);

        if (mToolbarView != null) {
            setSupportActionBar(mToolbarView);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            TextView mTitle = (TextView) mToolbarView.findViewById(R.id.toolbar_title);
            mTitle.setTextColor(ContextCompat.getColor(this, R.color.blako_white));
            mTitle.setText(title);

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("GeneralFlux","Activity: "+BkoOffersListActivity.this.getLocalClassName());
    }


    @Override
    public void onOfferListener(BkoOffer.BkoAnnoucement item) {
        Intent intent = new Intent(
                this, BkoOfferDetailActivity.class);
        BkoOfferDetailActivity.selectedItem = item;
        BkoOfferDetailActivity.selectedItem.setPenaltys(null);
        startActivityForResult(intent, 12);
    }


    private void getAnnoucements() {
        OffersDateRv.setAdapter(null);
        mSwipeRefreshLayout.setRefreshing(false);
        showWaitDialogWhileExecuting("Cargando", new Runnable() {
            public void run() {

                try {

                    offersResponse = null;

                    BkoUser user = BkoUserDao.Consultar(BkoOffersListActivity.this);
                    offersResponse = HttpRequest
                            .get(Constants.GET_OFFERS_BY_GROUP(BkoOffersListActivity.this) + "workerId=" + user.getWorkerId() + "&dateStart=" + date + "&customerId=" + customerId).connectTimeout(8000).body();


                    Gson gson = new Gson();
                    if (offersResponse != null) {

                        JSONObject object = new JSONObject(offersResponse);
                        offersResponse = object.getString("message");


                        boolean responseOk = object.getBoolean("response");

                        if (responseOk) {
                            offers = Arrays.asList(gson.fromJson(offersResponse, BkoOffer[].class));


                        } else {
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
                            BkoOffersGroupListAdapter adapter = new BkoOffersGroupListAdapter(BkoOffersListActivity.this, offers, BkoOffersListActivity.this);
                            OffersDateRv.setAdapter(adapter);
                        } else {
                            noResultsTv.setVisibility(View.VISIBLE);
                            Toast.makeText(BkoOffersListActivity.this, getString(R.string.blako_offers_no_ofertas), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.fillInStackTrace();
                        refreshIv.setVisibility(View.VISIBLE);
                        Toast.makeText(BkoOffersListActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    refreshIv.setVisibility(View.VISIBLE);
                    Toast.makeText(BkoOffersListActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();


                }
            }
        });


    }

}



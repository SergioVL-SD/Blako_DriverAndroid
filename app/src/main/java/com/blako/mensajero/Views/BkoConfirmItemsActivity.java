package com.blako.mensajero.Views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.blako.mensajero.Adapters.BkoSimpleRecyclerViewAdapter;
import com.blako.mensajero.BkoDataMaganer;
import com.blako.mensajero.Constants;
import com.blako.mensajero.Dao.BkoUserDao;
import com.blako.mensajero.R;
import com.blako.mensajero.Services.BkoSendLocationToServer;
import com.blako.mensajero.Utils.HttpRequest;
import com.blako.mensajero.VO.BkoChildTripVO;
import com.blako.mensajero.VO.BkoItem;
import com.blako.mensajero.VO.BkoRequestResponse;
import com.blako.mensajero.VO.BkoTripVO;
import com.blako.mensajero.VO.BkoUser;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by franciscotrinidad on 23/03/17.
 */

public class BkoConfirmItemsActivity extends BaseActivity {
    private String responseOrderStatus;
    private Toolbar mToolbarView;
    private TextView mTitle, clientTv, orderTv, stepsTv,pickDeliveryTitleTv;
    private ImageButton cancelBt, okBt;
    private RecyclerView itemsRv;
    private String confirmItemsResponse;
    private BkoChildTripVO trip;
    private boolean isDeliveryAddress;
    private boolean onlyDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bko_confirm_items_activity);
        mToolbarView = (Toolbar) findViewById(R.id.toolbar);

        if (mToolbarView != null) {
            setSupportActionBar(mToolbarView);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            mTitle = (TextView) mToolbarView.findViewById(R.id.toolbar_title);
            mTitle.setTextColor(ContextCompat.getColor(this, R.color.blako_white));
            mToolbarView.setBackgroundColor(ContextCompat.getColor(BkoConfirmItemsActivity.this, R.color.blako_background));
        }
        onlyDetails = getIntent().getBooleanExtra("onlyDetails",false);
        clientTv = (TextView) findViewById(R.id.clientTv);
        orderTv = (TextView) findViewById(R.id.orderTv);
        stepsTv = (TextView) findViewById(R.id.stepsTv);
        cancelBt = (ImageButton) findViewById(R.id.cancelBt);
        okBt = (ImageButton) findViewById(R.id.okBt);
        itemsRv = (RecyclerView) findViewById(R.id.itemsRv);
        pickDeliveryTitleTv= (TextView) findViewById(R.id.pickDeliveryTitleTv);


        if(onlyDetails){
            okBt.setVisibility(View.INVISIBLE);
            cancelBt.setVisibility(View.INVISIBLE);
        }

        cancelBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmItems("0");
            }
        });
        okBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmItems("1");


            }
        });

        itemsRv.setLayoutManager(new LinearLayoutManager(this));
        itemsRv.setNestedScrollingEnabled(false);
        setData();
    }

    private void setData() {
        try {
            BkoTripVO  parentTrip = BkoDataMaganer.getCuurentTemporaryTrip();
            BkoChildTripVO   delivery =   BkoDataMaganer.getCuurentTemporaryTrip().getDestino().get(0);

            if (parentTrip.getOrigen().get(0).getBko_orders_trips_item_confirm() == null || parentTrip.getOrigen().get(0).getBko_orders_trips_item_confirm().length() == 0) {
                isDeliveryAddress = false;
                trip = parentTrip.getOrigen().get(0);
            } else {
                trip = parentTrip.getDestino().get(0);
                isDeliveryAddress = true;
            }

            orderTv.setText(delivery.getBko_customeraddress_alias());
            if(!isDeliveryAddress){
                mTitle.setText(R.string.blako_confirmar_orden);
                clientTv.setText(delivery.getBko_customeraddress_contact());
                stepsTv.setText(delivery.getBko_customeraddress_note());
                pickDeliveryTitleTv.setText(R.string.blako_confirmar_ir_orden);
            }
            else {
                mTitle.setText(R.string.blako_confirmar_tarea);
                stepsTv.setText(delivery.getBko_customeraddress_note());
                clientTv.setText(delivery.getBko_customeraddress_contact());
                pickDeliveryTitleTv.setText(R.string.blako_destino_entregar);
            }

            // PRODUCTS

            ArrayList<String> items = new ArrayList<String>();
            Gson gson = new Gson();
            List<BkoItem> products = Arrays.asList(gson.fromJson(delivery.getBko_queuedtasks_products(), BkoItem[].class));
            for (BkoItem item : products) {
                items.add(item.getItem());
            }
            BkoSimpleRecyclerViewAdapter adapter = new BkoSimpleRecyclerViewAdapter(items);
            itemsRv.setAdapter(adapter);
        }catch (Exception e){
            e.fillInStackTrace();
        }

    }


    private void confirmItems(final String confirmed) {
        showWaitDialogWhileExecuting("Cargando...", new Runnable() {
            public void run() {

                try {

                    BkoUser user = BkoUserDao.Consultar(BkoConfirmItemsActivity.this);
                    confirmItemsResponse = null;
                    String workerId;
                    workerId = user.getWorkerId();
                    Map<String, String> mapVisible = new HashMap<String, String>();
                    mapVisible.put("workerId", workerId);
                    mapVisible.put("tripId", trip.getBko_orders_trips_id());
                    mapVisible.put("confirmed", confirmed);
                    mapVisible.put("tripsType", trip.getBko_orders_trips_type());
                    mapVisible.put("tripIdDestino",  BkoDataMaganer.getCuurentTemporaryTrip().getDestino().get(0).getBko_orders_trips_id());
                    mapVisible.put("orderId", trip.getBko_orders_id());

                    if(BkoSendLocationToServer.oneLocationDataToWebsite(BkoDataMaganer.getCurrentUserLocation(getApplicationContext()),getApplicationContext()))
                    confirmItemsResponse = HttpRequest.get(Constants.GET_CONFIRM_ITEMS(BkoConfirmItemsActivity.this), mapVisible, true).connectTimeout(6000).readTimeout(6000).body();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Runnable() {
            public void run() {


                try {

                    if (confirmItemsResponse != null) {
                        Gson gson = new Gson();
                        BkoRequestResponse response = gson.fromJson(confirmItemsResponse, BkoRequestResponse.class);
                        if (response.isResponse()) {
                            if (confirmed.equals("1"))
                                BkoDataMaganer.getCuurentTemporaryTrip().getDestino().get(0).setSuccess(true);
                            else
                                BkoDataMaganer.getCuurentTemporaryTrip().getDestino().get(0).setSuccess(false);

                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        } else {
                            if (!response.getMessage().equals(getString(R.string.response_no_parameters))){
                                Toast.makeText(BkoConfirmItemsActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }


                    } else {
                        Toast.makeText(BkoConfirmItemsActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(BkoConfirmItemsActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();
                }

            }


            //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("GeneralFlux","Activity: "+BkoConfirmItemsActivity.this.getLocalClassName());
    }

}

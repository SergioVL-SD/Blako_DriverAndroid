package com.blako.mensajero.Views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.blako.mensajero.Adapters.BkoOffersAdapter;
import com.blako.mensajero.BkoCore;
import com.blako.mensajero.BkoCore.OffersListener;
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
 * Created by franciscotrinidad on 14/08/17.
 */

public class BkoOwnOffersActivity extends BaseActivity implements OffersListener {
    private String responseOrderStatus;
    private List<BkoOffer> offers;
    private String offersResponse;
    protected VerticallyScrollRecyclerView offersRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bko_ownoffer_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setTextColor(ContextCompat.getColor(this, R.color.blako_white));
        mTitle.setText(getString(R.string.blako_mis_ofertas_titulo));


        offersRv = (VerticallyScrollRecyclerView) findViewById(R.id.offersRv);
        offersRv.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        offersRv.setLayoutManager(layoutManager);
        offersRv.setNestedScrollingEnabled(false);

        getAnnoucements();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("GeneralFlux","Activity: "+BkoOwnOffersActivity.this.getLocalClassName());
        BkoCore.setoffersInterfaceListener(this);
    }

    private void getAnnoucements() {
        offersRv.setAdapter(null);

        showWaitDialogWhileExecuting(new Runnable() {
            public void run() {

                try {

                    offersResponse = null;

                    BkoUser user = BkoUserDao.Consultar(BkoOwnOffersActivity.this);
                    offersResponse = HttpRequest
                            .get(Constants.GET_OFFERS(BkoOwnOffersActivity.this) + "workerId=" + user.getWorkerId())
                            .connectTimeout(8000).body();


                    Gson gson = new Gson();
                    if (offersResponse != null) {

                        JSONObject object = new JSONObject(offersResponse);
                        offersResponse = object.getString("message");

                        offersResponse.replace("-", "");
                        offers = Arrays.asList(gson.fromJson(offersResponse, BkoOffer[].class));

                        boolean responseOk = object.getBoolean("response");

                        if (responseOk) {


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

                        offersRv.setAdapter(null);

                        if (offers.size() != 0) {



                            List<BkoOffer> _offers = new ArrayList<BkoOffer>();
                            for (int i = 0; i < offers.size(); i++) {

                                if (offers.get(i).getAnnouncement().size() != 0) {
                                    _offers.add(offers.get(i));

                                    for (int j = 0; j < offers.get(i).getAnnouncement().size(); j++) {

                                        //if (offers.get(i).getAnnouncement().get(j).getBko_announcementworker_checkin() != null && offers.get(i).getAnnouncement().get(j).getBko_announcementworker_checkin().equals("1"))
                                          //  firstAnnounce = offers.get(i).getAnnouncement().get(j);

                                    }


                                }


                            }

                            BkoOffersAdapter adapter = new BkoOffersAdapter(BkoOwnOffersActivity.this, _offers, BkoOwnOffersActivity.this);
                            offersRv.setAdapter(adapter);

                        }


                    } catch (Exception e) {
                        e.fillInStackTrace();

                    }

                } else {


                }
            }
        });


    }


    @Override
    public void onLogoutListener(boolean status) {

    }

    @Override
    public void onCheckInListener(BkoOffer.BkoAnnoucement selectedItem) {
        Intent intent = new Intent(
                this, BkoOfferDetailActivity.class);
        BkoOfferDetailActivity.selectedItem = selectedItem;
        //BkoOfferDetailActivity.selectedItem.setPenaltys(penaltys);
        startActivityForResult(intent, 12);
    }

    @Override
    public void onOfferListener(BkoOffer.BkoAnnoucement selectedItem) {

    }

    @Override
    public void onCancelListener(BkoOffer.BkoAnnoucement selectedItem) {

    }

}

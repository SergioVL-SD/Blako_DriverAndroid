package com.blako.mensajero.Views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.blako.mensajero.BkoDataMaganer;
import com.blako.mensajero.Constants;
import com.blako.mensajero.Dao.BkoUserDao;
import com.blako.mensajero.R;
import com.blako.mensajero.Utils.HttpRequest;
import com.blako.mensajero.VO.BkoPushRequest;
import com.blako.mensajero.VO.BkoRecoverOrderStatusVO;
import com.blako.mensajero.VO.BkoUser;
import com.google.gson.Gson;


public class BkoConfigActivity extends BaseActivity {
    private String responseOrderStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_activity);

        try {
            BkoUser user = BkoUserDao.Consultar(BkoConfigActivity.this);
            if (user != null){
                BkoDataMaganer.setWorkerId(this,user.getWorkerId());
                startAppActivity();
            } else{
                startLoginActivity();
            }

        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    private void startAppActivity() {

        if (BkoDataMaganer.getCurrentVehicle(this) != null) {
            try {

                final BkoUser user = BkoUserDao.Consultar(this);
                if (user != null && user.isAvailable()) {
                    final BkoPushRequest serviceClienteStatus = BkoDataMaganer.getCurrentStatusRequest(this);
                    if (serviceClienteStatus != null) {
                        executeInBackgroundd(new Runnable() {
                            public void run() {

                                try {
                                    responseOrderStatus = null;
                                    String requestId = BkoDataMaganer.getRequestId(BkoConfigActivity.this);
                                    responseOrderStatus = HttpRequest.get(Constants.GET_STATUS_REQUEST_URL(BkoConfigActivity.this) + "connectToken=" + user.getConectToken() + "&oid=" + serviceClienteStatus.getOid()).connectTimeout(2500).body();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Runnable() {
                            public void run() {

                                if (responseOrderStatus != null) {

                                    try {
                                        Gson gson = new Gson();

                                        BkoRecoverOrderStatusVO orderStatusVO = gson.fromJson(responseOrderStatus, BkoRecoverOrderStatusVO.class);

                                        if (orderStatusVO != null && orderStatusVO.isResponse()) {


                                            if (orderStatusVO.isResponse()) {


                                                Integer deliveryStatus = Integer.parseInt(orderStatusVO.getDeliverystatus());

                                                if ((deliveryStatus == 5 || deliveryStatus == 7) && BkoDataMaganer.getStatusService(BkoConfigActivity.this) != Constants.SERVICE_STATUS_PAYMENT) {
                                                    BkoDataMaganer.setStatusService(Constants.SERVICE_STATUS_FREE, BkoConfigActivity.this);
                                                }

                                                if (deliveryStatus == 7 && BkoDataMaganer.getCurrentOffer(BkoConfigActivity.this)!=null)
                                                    BkoDataMaganer.setStatusService(Constants.SERVICE_STATUS_WITH_OFFER_CHECKIN_FINISH, BkoConfigActivity.this);


                                                startApplication();
                                            } else {
                                                startApplication();
                                            }

                                        } else {
                                            startApplication();
                                        }

                                    } catch (Exception e) {
                                        startApplication();
                                    }

                                } else {

                                    Toast.makeText(BkoConfigActivity.this,getString(R.string.blako_error), Toast.LENGTH_LONG).show();
                                    startApplication();
                                }

                            }
                        });
                    } else {
                        startApplication();
                    }
                } else {
                    finishAffinity();
                    Intent intent = new Intent(BkoConfigActivity.this, BkoMainActivity.class);
                    startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        } else {

            if(BkoDataMaganer.getPolicyReaded(this))
            {
                Intent intent = new Intent(this, BkoVehiclesActivity.class);
                intent.putExtra("onRecover", true);
                startActivity(intent);
            }
            else
            {
                finish();
                Intent intent = new Intent(this, BkoPolicyActivity.class);
                intent.putExtra("onRecover", true);
                startActivity(intent);
            }


        }
    }

    private void startLoginActivity() {
        startActivity(new Intent(this, BkoLoginActivity.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        finishAffinity();
    }

    private void startApplication() {
        Intent intent = new Intent(BkoConfigActivity.this, BkoMainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("GeneralFlux","Activity: "+BkoConfigActivity.this.getLocalClassName());
    }
}

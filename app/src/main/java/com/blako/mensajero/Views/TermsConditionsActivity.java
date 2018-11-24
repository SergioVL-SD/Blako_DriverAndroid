package com.blako.mensajero.Views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blako.mensajero.App;
import com.blako.mensajero.BkoDataMaganer;
import com.blako.mensajero.Constants;
import com.blako.mensajero.Dao.BkoUserDao;
import com.blako.mensajero.R;
import com.blako.mensajero.Utils.AppPreferences;
import com.blako.mensajero.Utils.HttpRequest;
import com.blako.mensajero.VO.BkoPushRequest;
import com.blako.mensajero.VO.BkoRecoverOrderStatusVO;
import com.blako.mensajero.VO.BkoUser;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class TermsConditionsActivity extends BaseActivity {

    TextView tvTitle;
    TextView tvMessage;
    TextView tvLink;
    CheckBox cbAccept;
    ProgressBar pbWait;
    Button btnContinue;
    FloatingActionButton fabRetry;
    FloatingActionButton fabExit;

    AppPreferences preferences;

    private String responseOrderStatus;

    private String termsUrl= "http://www.sindelantal.mx";

    private final String TERMS_AND_CONDITIONS= "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_conditions);

        tvTitle= findViewById(R.id.tvTitle);
        tvMessage= findViewById(R.id.tvMessage);
        tvLink= findViewById(R.id.tvLink);
        cbAccept= findViewById(R.id.cbAccept);
        pbWait= findViewById(R.id.pbWait);
        btnContinue= findViewById(R.id.btnContinue);
        fabRetry= findViewById(R.id.fabRetry);
        fabExit= findViewById(R.id.fabExit);

        preferences= App.getInstance().getPreferences();

        new GetTermsAndConditionsTask().execute();
        pbWait.setVisibility(View.VISIBLE);

        cbAccept.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                btnContinue.setEnabled(isChecked);
            }
        });

        tvLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(termsUrl));
                    startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AcceptTermsAndConditionsTask().execute();
                btnContinue.setEnabled(false);
                pbWait.setVisibility(View.VISIBLE);
            }
        });

        fabRetry.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                fabRetry.setVisibility(View.GONE);
                new GetTermsAndConditionsTask().execute();
                pbWait.setVisibility(View.VISIBLE);
            }
        });

        fabExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BkoUserDao.Eliminar(getApplicationContext());
                BkoDataMaganer.clearData(getApplicationContext());
                Intent intent = new Intent(TermsConditionsActivity.this, BkoLoginActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        });
    }

    private class GetTermsAndConditionsTask extends AsyncTask<Void,Void,JSONObject>{

        @Override
        protected JSONObject doInBackground(Void... voids) {
            Log.d("Term_Cond_Url",Constants.GET_BLAKO_2_ENVIRONMENT(getApplicationContext())+"v1/app/legals/legal/"+TERMS_AND_CONDITIONS+"/worker/"+BkoDataMaganer.getWorkerId(getApplicationContext()));
            try {
                String termsResponse = HttpRequest
                        .get(Constants.GET_BLAKO_2_ENVIRONMENT(getApplicationContext())+"v1/app/legals/legal/"+TERMS_AND_CONDITIONS+"/worker/"+BkoDataMaganer.getWorkerId(getApplicationContext()))
                        .connectTimeout(6000).readTimeout(6000).body();
                if (termsResponse!=null){
                    Log.d("Term_Cond_Response",termsResponse);
                    return new JSONObject(termsResponse);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @SuppressLint("RestrictedApi")
        @Override
        protected void onPostExecute(JSONObject response) {
            try {
                if (response!=null && response.getBoolean("success")){
                    if (response.getJSONObject("data").getBoolean("check")){
                        preferences.setTermsAndConditions(true);
                        startAppActivity();
                        return;
                    }
                    JSONObject termsAndConditions= response.getJSONObject("data").getJSONObject("document");
                    if (termsAndConditions.getInt("visible")!=1){
                        startAppActivity();
                        return;
                    }
                    pbWait.setVisibility(View.GONE);
                    tvMessage.setVisibility(View.VISIBLE);
                    cbAccept.setVisibility(View.VISIBLE);
                    tvLink.setVisibility(View.VISIBLE);
                    btnContinue.setVisibility(View.VISIBLE);
                    Log.d("Term_Cond_Message",termsAndConditions.getString("message"));
                    Log.d("Term_Cond_Url",termsAndConditions.getString("url_file"));
                    tvTitle.setText(termsAndConditions.getString("name"));
                    tvMessage.setText(termsAndConditions.getString("message"));
                    String accept= getString(R.string.cb_terms_accept)+" "+termsAndConditions.getString("name");
                    String read= getString(R.string.tv_terms_link)+" "+termsAndConditions.getString("name");
                    cbAccept.setText(accept);
                    tvLink.setText(read);
                    termsUrl= termsAndConditions.getString("url_file");
                }else {
                    pbWait.setVisibility(View.GONE);
                    fabRetry.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(),getString(R.string.error_terms_charge),Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                pbWait.setVisibility(View.GONE);
                fabRetry.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(),getString(R.string.error_terms_charge),Toast.LENGTH_LONG).show();
            }
        }
    }

    private class AcceptTermsAndConditionsTask extends AsyncTask<Void,Void,JSONObject>{

        @Override
        protected JSONObject doInBackground(Void... voids) {
            Log.d("Term_Cond_Accept_Url",Constants.GET_BLAKO_2_ENVIRONMENT(getApplicationContext())+"v1/app/legals/check/legal/"+TERMS_AND_CONDITIONS+"/worker/"+BkoDataMaganer.getWorkerId(getApplicationContext()));
            try {
                String termsResponse = HttpRequest
                        .get(Constants.GET_BLAKO_2_ENVIRONMENT(getApplicationContext())+"v1/app/legals/check/legal/"+TERMS_AND_CONDITIONS+"/worker/"+BkoDataMaganer.getWorkerId(getApplicationContext()))
                        .connectTimeout(6000).readTimeout(6000).body();
                if (termsResponse!=null){
                    Log.d("Term_Cond_Accept_Resp",termsResponse);
                    return new JSONObject(termsResponse);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            btnContinue.setEnabled(true);
            pbWait.setVisibility(View.GONE);
            try {
                if (response!=null && response.getBoolean("success")){
                    preferences.setTermsAndConditions(true);
                    startAppActivity();
                }else {
                    Toast.makeText(getApplicationContext(),getString(R.string.error_terms),Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),getString(R.string.error_terms),Toast.LENGTH_LONG).show();
            }
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
                                    String requestId = BkoDataMaganer.getRequestId(TermsConditionsActivity.this);
                                    responseOrderStatus = HttpRequest.get(Constants.GET_STATUS_REQUEST_URL(TermsConditionsActivity.this) + "connectToken=" + user.getConectToken() + "&oid=" + serviceClienteStatus.getOid()).connectTimeout(5000).body();

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

                                                if ((deliveryStatus == 5 || deliveryStatus == 7) && BkoDataMaganer.getStatusService(TermsConditionsActivity.this) != Constants.SERVICE_STATUS_PAYMENT) {
                                                    BkoDataMaganer.setStatusService(Constants.SERVICE_STATUS_FREE, TermsConditionsActivity.this);
                                                }

                                                if (deliveryStatus == 7 && BkoDataMaganer.getCurrentOffer(TermsConditionsActivity.this)!=null)
                                                    BkoDataMaganer.setStatusService(Constants.SERVICE_STATUS_WITH_OFFER_CHECKIN_FINISH, TermsConditionsActivity.this);


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

                                    Toast.makeText(TermsConditionsActivity.this,getString(R.string.blako_error), Toast.LENGTH_LONG).show();
                                    startApplication();
                                }

                            }
                        });
                    } else {
                        startApplication();
                    }
                } else {
                    finishAffinity();
                    Intent intent = new Intent(TermsConditionsActivity.this, BkoMainActivity.class);
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

    private void startApplication() {
        Intent intent = new Intent(TermsConditionsActivity.this, BkoMainActivity.class);
        startActivity(intent);
    }
}

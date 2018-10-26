package com.blako.mensajero.Views;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blako.mensajero.App;
import com.blako.mensajero.BkoDataMaganer;
import com.blako.mensajero.Constants;
import com.blako.mensajero.Dao.BkoUserDao;
import com.blako.mensajero.R;
import com.blako.mensajero.Utils.AppPreferences;
import com.blako.mensajero.Utils.BkoUtilities;
import com.blako.mensajero.Utils.HttpRequest;
import com.blako.mensajero.VO.BkoChildTripVO;
import com.blako.mensajero.VO.BkoOrderVO;
import com.blako.mensajero.VO.BkoPushRequest;
import com.blako.mensajero.VO.BkoRecoverStatusVO;
import com.blako.mensajero.VO.BkoRequestResponse;
import com.blako.mensajero.VO.BkoTripVO;
import com.blako.mensajero.VO.BkoUser;
import com.blako.mensajero.VO.BkoUserLoginResponse;
import com.blako.mensajero.VO.BkoVehicleVO;
import com.blako.mensajero.VO.BkoVersionVO;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.util.regex.Pattern;

/**
 * Created by franciscotrinidad on 12/17/15.
 */
public class BkoLoginActivity extends BaseActivity {

    private EditText emailEt, passwordEt;
    private Button loginBt;
    private String loginResponse;
    private BkoUserLoginResponse userLoginResponse;
    private String versionResponse, statusResponse;
    private BkoVersionVO versionResponseVO;
    private BkoRecoverStatusVO recoverStatusVO;
    private String visibilityResponse;

    private AppPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bko_login_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        preferences= App.getInstance().getPreferences();

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        emailEt = (EditText) findViewById(R.id.emailEt);
        passwordEt = (EditText) findViewById(R.id.passwordEt);
        loginBt = (Button) findViewById(R.id.loginBt);
        TextView versionTv = (TextView) findViewById(R.id.versionTv);
        TextView registerTv = (TextView) findViewById(R.id.registerTv);
        emailEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                emailEt.setTextColor(Color.BLACK);
                validData();
                String result = s.toString().replaceAll(" ", "");
                if (!s.toString().equals(result)) {
                    emailEt.setText(result);
                    emailEt.setSelection(result.length());

                }


            }
        });

        passwordEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validData();
                String result = s.toString().replaceAll(" ", "");
                if (!s.toString().equals(result)) {
                    passwordEt.setText(result);
                    passwordEt.setSelection(result.length());
                }
            }
        });


        loginBt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                try {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                } catch (Exception e) {
                    Log.e("KeyBoardUtil", e.toString(), e);
                }

                if (!checkPermissions())
                    return;

                if (!validEmail()) {
                    Toast.makeText(BkoLoginActivity.this, getString(R.string.blako_correo_invalido), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!validPassword()) {
                    Toast.makeText(BkoLoginActivity.this, getString(R.string.blako_contrasenia_invalido), Toast.LENGTH_SHORT).show();
                    return;
                }


                showWaitDialogWhileExecuting("Cargando...", new Runnable() {
                            public void run() {


                                try {
                                    versionResponse = null;
                                    userLoginResponse = null;
                                    loginResponse = null;
                                    versionResponseVO = null;
                                    recoverStatusVO = null;
                                    statusResponse = null;
                                    visibilityResponse = null;
                                    Gson gson = new Gson();
                                    versionResponse = HttpRequest
                                            .get(Constants.URL_VERSION + "versionCode=" + BkoUtilities.getVersionCode(BkoLoginActivity.this) + "&versionName=" + BkoUtilities.getVersionName(BkoLoginActivity.this))
                                            .connectTimeout(5000).readTimeout(5000).body();

                                    BkoDataMaganer.setStatusService(Constants.SERVICE_STATUS_FREE, BkoLoginActivity.this);

                                    if (versionResponse == null)
                                        return;

                                    versionResponseVO = gson.fromJson(versionResponse, BkoVersionVO.class);
                                    if (versionResponseVO == null || !versionResponseVO.isResponse())
                                        return;


                                    String token = preferences.getFirebaseToken();
                                    Log.d("SVL_firebase",token);

                                    if (token == null || token.length() == 0)
                                        return;

                                    loginResponse = HttpRequest
                                            .get(Constants.URL_LOGIN + "email=" + emailEt.getText().toString() + "&pass=" + passwordEt.getText().toString() + "&token=" + token)
                                            .connectTimeout(5000).readTimeout(5000).body();

                                    Log.d("SVL_loginRes",loginResponse);

                                    if (loginResponse != null) {

                                        userLoginResponse = gson.fromJson(loginResponse, BkoUserLoginResponse.class);
                                        if (userLoginResponse != null) {
                                            if (userLoginResponse.isResponse()) {

                                                String secondLogin;
                                                // ambiente distinto de produccion
                                                if (!userLoginResponse.getEnviromentId().equals("1")) {

                                                    try {

                                                        secondLogin = HttpRequest
                                                                .get(userLoginResponse.getEnviromentUrl() + "/public/workers/login?" + "email=" + emailEt.getText().toString() + "&pass=" + passwordEt.getText().toString() + "&token=" + token)
                                                                .connectTimeout(5000).body();

                                                        Log.d("SVL_secondLoginRes",secondLogin);

                                                        if (secondLogin != null) {
                                                            BkoUserLoginResponse responseSecondLogin = gson.fromJson(secondLogin, BkoUserLoginResponse.class);
                                                            if (responseSecondLogin != null && responseSecondLogin.isResponse()) {
                                                                userLoginResponse = responseSecondLogin;


                                                                statusResponse = HttpRequest
                                                                        .get(userLoginResponse.getEnviromentUrl() + "/trips/getlastorderandtripsactive?workerId=" + userLoginResponse.getWorkerId())
                                                                        .connectTimeout(5000).body();


                                                                if (statusResponse != null) {
                                                                    recoverStatusVO = gson.fromJson(statusResponse, BkoRecoverStatusVO.class);

                                                                    if (recoverStatusVO != null) {


                                                                        String visibility = userLoginResponse.getVisibilitytype();


                                                                        if (!visibility.equals(userLoginResponse.getVisibilitytype())) {
                                                                            visibilityResponse = HttpRequest.get(Constants.GET_SWITCH_VISIBILITY(BkoLoginActivity.this) + "workerId=" + userLoginResponse.getWorkerId() + "&visibilityType=" + visibility).connectTimeout(5000).readTimeout(5000).body();


                                                                            BkoRequestResponse response = gson.fromJson(visibilityResponse, BkoRequestResponse.class);

                                                                            if (response != null && response.isResponse()) {
                                                                                userLoginResponse.setVisibilitytype(visibility);
                                                                            } else {
                                                                                userLoginResponse = null;
                                                                            }


                                                                        } else {
                                                                            userLoginResponse.setVisibilitytype(visibility);
                                                                        }


                                                                    } else {
                                                                        userLoginResponse = null;
                                                                    }
                                                                } else {
                                                                    userLoginResponse = null;

                                                                }

                                                            } else {
                                                                userLoginResponse = null;
                                                            }

                                                        } else {
                                                            userLoginResponse = null;
                                                        }
                                                    } catch (Exception e)

                                                    {
                                                        userLoginResponse = null;
                                                    }


                                                } else {

                                                    statusResponse = HttpRequest
                                                            .get(userLoginResponse.getEnviromentUrl() + "/trips/getlastorderandtripsactive?workerId=" + userLoginResponse.getWorkerId())
                                                            .connectTimeout(5000).body();

                                                    Log.d("SVL_statusRes",statusResponse);

                                                    if (statusResponse != null) {
                                                        recoverStatusVO = gson.fromJson(statusResponse, BkoRecoverStatusVO.class);

                                                        if (recoverStatusVO != null) {
                                                            String visibility = "";

                                                            visibility = userLoginResponse.getVisibilitytype();

                                                            if (!visibility.equals(userLoginResponse.getVisibilitytype())) {
                                                                visibilityResponse = HttpRequest.get(userLoginResponse.getEnviromentUrl() + "/workers/visibilitytype?" + "workerId=" + userLoginResponse.getWorkerId() + "&visibilityType=" + visibility).connectTimeout(5000).readTimeout(5000).body();


                                                                BkoRequestResponse response = gson.fromJson(visibilityResponse, BkoRequestResponse.class);

                                                                if (response != null && response.isResponse()) {
                                                                    userLoginResponse.setVisibilitytype(visibility);
                                                                } else {
                                                                    userLoginResponse = null;
                                                                }


                                                            } else {
                                                                userLoginResponse.setVisibilitytype(visibility);
                                                            }


                                                        } else {
                                                            userLoginResponse = null;
                                                        }
                                                    } else {
                                                        userLoginResponse = null;

                                                    }


                                                }
                                            } else {

                                            }
                                        }

                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();

                                    userLoginResponse = null;

                                }
                            }
                        }

                        , new

                                Runnable() {
                                    public void run() {

                                        if (versionResponseVO != null) {


                                            if (!versionResponseVO.isResponse())

                                            {
                                                AlertaTextView(getString(R.string.blako_advertencia), versionResponseVO.getMessage(), BkoLoginActivity.this,
                                                        new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                                                try {
                                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                                                } catch (android.content.ActivityNotFoundException anfe) {
                                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                                                }
                                                            }
                                                        }, getString(R.string.blako_aceptar), null);

                                                return;
                                            }

                                            try {

                                                if (userLoginResponse != null) {
                                                    if (userLoginResponse.isResponse()) {

                                                        userLoginResponse.setEmail(emailEt.getText().toString().trim());
                                                        setData(userLoginResponse, recoverStatusVO, BkoLoginActivity.this);

                                                        BkoDataMaganer.setWorkerId(BkoLoginActivity.this,userLoginResponse.getWorkerId());

                                                        BkoUser user = BkoUserDao.Consultar(BkoLoginActivity.this);

                                                        if (recoverStatusVO != null && recoverStatusVO.isResponse() || userLoginResponse.getAnnouncementData() != null && userLoginResponse.getAnnouncementData().size()!=0) {
                                                            user.setIsAvailable(true);

                                                            BkoUserDao.Actualizar(BkoLoginActivity.this,user);

                                                            BkoVehicleVO vehicleVO = new BkoVehicleVO();
                                                            vehicleVO.setId(userLoginResponse.getVehicles_id());
                                                            vehicleVO.setBrand(userLoginResponse.getVehicles_brand());
                                                            vehicleVO.setModel(userLoginResponse.getVehicles_carmodel());
                                                            BkoDataMaganer.setCurrentVehicle(vehicleVO, BkoLoginActivity.this);
                                                            if(userLoginResponse.getAnnouncementData() != null && userLoginResponse.getAnnouncementData().size()!=0){
                                                                BkoDataMaganer.setStatusService(Constants.SERVICE_STATUS_WORKER_ARRIVED,BkoLoginActivity.this);
                                                                userLoginResponse.getAnnouncementData().get(0).setTime(userLoginResponse.getAnnouncementData().get(0).getDate());
                                                                BkoDataMaganer.setCurrentOffer(userLoginResponse.getAnnouncementData().get(0), BkoLoginActivity.this);
                                                            } else {

                                                                if(   recoverStatusVO.getTrips()!=null ){


                                                                    for(BkoTripVO tripPArent: recoverStatusVO.getTrips()){

                                                                        BkoChildTripVO trip = tripPArent.getOrigen().get(0);
                                                                        BkoChildTripVO tripDelivery = tripPArent.getDestino().get(0);

                                                                        if (trip.getBko_orders_trips_completeddatetime() != null && trip.getBko_orders_trips_completeddatetime().length() != 0) {
                                                                            BkoDataMaganer.setOnDemand(BkoLoginActivity.this,true);
                                                                            break;
                                                                        } else {
                                                                            if (tripDelivery.getBko_requeststatus_id() != null && tripDelivery.getBko_requeststatus_id().equals("2")) {
                                                                                BkoDataMaganer.setOnDemand(BkoLoginActivity.this,true);
                                                                                break;
                                                                            } else {

                                                                            }
                                                                        }
                                                                    }

                                                                }
                                                            }

                                                            Intent intent = new Intent(BkoLoginActivity.this, BkoMainActivity.class);
                                                            finishAffinity();
                                                            startActivity(intent);
                                                        } else {
                                                            finish();
                                                            BkoDataMaganer.setPolicyReaded(false,BkoLoginActivity.this);
                                                            Intent intent = new Intent(BkoLoginActivity.this, BkoPolicyActivity.class);
                                                            intent.putExtra("onRecover", true);
                                                            startActivity(intent);

                                                        }
                                                    } else {
                                                        Toast.makeText(BkoLoginActivity.this, userLoginResponse.getMessage(), Toast.LENGTH_SHORT).show();

                                                    }
                                                } else {
                                                    Toast.makeText(BkoLoginActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();
                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                Toast.makeText(BkoLoginActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();
                                            }


                                        } else

                                        {
                                            Toast.makeText(BkoLoginActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }

                );

            }
        });
        registerTv.setMovementMethod(LinkMovementMethod.getInstance());


        if (BkoDataMaganer.isHasOtherSessionActive(this)) {
            onOtherSessionActive();

        }

        try {
            versionTv.setText("V. " + BkoUtilities.getVersionName(this));
        } catch (Exception e) {

        }

        BkoDataMaganer.clearData(this);
        checkPermissions();

    }

    private void validData()
    {
        if(emailEt.getText().toString().length()!=0 && passwordEt.getText().toString().length()!=0){
            loginBt.setBackgroundColor(ContextCompat.getColor(this,R.color.blako_green));
            loginBt.setEnabled(true);
        }
        else {
            loginBt.setEnabled(false);
            loginBt.setBackgroundColor(ContextCompat.getColor(this,R.color.blako_gray_low));
        }
    }


    public boolean validEmail() {
        boolean isValido = true;
        Pattern valCorreo = Pattern.compile(Constants.CORREO_EXPRESION);
        String correo = this.emailEt.getText().toString().trim();
        if (correo.length() == 0 || !valCorreo.matcher(correo).matches()) {
            emailEt.setTextColor(Color.RED);
            isValido = false;
        } else {
            emailEt.setTextColor(Color.BLACK);

        }
        return isValido;
    }

    public boolean validPassword() {
        if (passwordEt.getText().toString().length() == 0)
            return false;
        return true;
    }

    public static void setData(BkoUserLoginResponse userLoginResponse, BkoRecoverStatusVO recoverStatusVO, Context context) {

        if (userLoginResponse != null) {
            BkoDataMaganer.setEnviromentId(userLoginResponse.getEnviromentId(), context);
            BkoDataMaganer.setEnviromentUrl(userLoginResponse.getEnviromentUrl(), context);
            BkoUser user = new BkoUser();
            user.setName(userLoginResponse.getName());
            user.setLastname(userLoginResponse.getLastname());
            user.setEnvironment(userLoginResponse.getEnviromentUrl());
            user.setOtherdevice(userLoginResponse.isOtherdevice());
            user.setWorkerId(userLoginResponse.getWorkerId());
            user.setWorkerParentId(userLoginResponse.getWorkerParentId());
            user.setEmail(userLoginResponse.getEmail().trim());
            user.setConectToken(BkoUtilities.md5((userLoginResponse.getEmail().trim().toLowerCase() + "48cy6t5m")));
            user.setCanbeprivate(userLoginResponse.getCanbeprivate());
            user.setCanbepublic(userLoginResponse.getCanbepublic());
            user.setVisibilitytype(userLoginResponse.getVisibilitytype());
            user.setPicurl(userLoginResponse.getPicurl());
            user.setTakeWareHouse(userLoginResponse.getCheckinexact());
            user.setWareHouseSelected(userLoginResponse.getWarehouse_name());
            user.setWareHouseIdSelected(userLoginResponse.getWarehouse_id());
            if (userLoginResponse.getAnnouncementData() != null && userLoginResponse.getAnnouncementData().size() != 0)
                user.setVisibilitytype("Publico");

            BkoUserDao.Eliminar(context);
            BkoUserDao.Insertar(context, user);

        }


        if (recoverStatusVO != null) {
            BkoPushRequest request = new BkoPushRequest();
            if (recoverStatusVO.getOrder() != null && recoverStatusVO.getOrder().size() > 0) {
                BkoDataMaganer.setAllRecoverStatus(recoverStatusVO, context);
                BkoOrderVO order = recoverStatusVO.getOrder().get(0);
                request.setWorkerrequestid(order.getBko_workerrequest_id());
                request.setLat(order.getBko_customeraddress_latitude());
                request.setLng(order.getBko_customeraddress_longitude());
                request.setAlias(order.getBko_customeraddress_alias());
                request.setStreet(order.getBko_customeraddress_street());
                request.setColony(order.getBko_customeraddress_colony());
                request.setDelegation(order.getBko_customeraddress_delegation());
                request.setZip(order.getBko_customeraddress_zip());
                request.setTelephone(order.getBko_customeraddress_telephone());
                request.setNote(order.getBko_customeraddress_note());
                request.setCustomerId(order.getBko_customer_id());
                request.setTypeaddress(order.getBko_customeraddress_typeaddress());
                request.setTypeservice(order.getBko_customeraddress_typeservice());
                request.setContact(order.getBko_customeraddress_contact());
                request.setEta(order.getBko_orders_eta());
                request.setOid(order.getBko_orders_id());
                BkoDataMaganer.setStatusRequest(request, context);
            }
        }
    }

    private void onOtherSessionActive() {
        AlertaTextView(null, getString(R.string.blako_otra_sesion_activa), this,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BkoDataMaganer.setHasOtherSessionActive(false, BkoLoginActivity.this);
                    }
                }, getString(R.string.blako_aceptar), null);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("GeneralFlux","Activity: "+BkoLoginActivity.this.getLocalClassName());
    }

}


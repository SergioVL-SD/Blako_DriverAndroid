package com.blako.mensajero.Views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.blako.mensajero.App;
import com.blako.mensajero.BkoDataMaganer;
import com.blako.mensajero.Constants;
import com.blako.mensajero.DB.AppDbHelper;
import com.blako.mensajero.DB.models.Hub;
import com.blako.mensajero.DB.models.HubDefaultValue;
import com.blako.mensajero.DB.models.HubPoints;
import com.blako.mensajero.Dao.BkoUserDao;
import com.blako.mensajero.R;
import com.blako.mensajero.Utils.AppPreferences;
import com.blako.mensajero.Utils.DateUtils;
import com.blako.mensajero.Utils.HttpRequest;
import com.blako.mensajero.VO.BkoPushRequest;
import com.blako.mensajero.VO.BkoRecoverOrderStatusVO;
import com.blako.mensajero.VO.BkoUser;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class BkoConfigActivity extends BaseActivity {

    private AppPreferences preferences;
    private AppDbHelper dbHelper;

    private String responseOrderStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_activity);

        preferences= App.getInstance().getPreferences();
        dbHelper= App.getInstance().getDbHelper();

        if (dbHelper.getAllHubs().size()==0){
            preferences.setHubsLastCheck("");
            preferences.setHubsUpdate(true);
        }

        if (!preferences.getHubsLastCheck().equals(DateUtils.getActualDate())){
            new GetDefaultValuesJSONFromServiceTask().execute();
        }else {
            initApp();
        }

    }

    private void initApp(){
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

    private void chargeFailed(){
        AlertDialog.Builder builder= new AlertDialog.Builder(BkoConfigActivity.this);
        builder.setMessage(getString(R.string.dialog_charge_message));
        builder.setPositiveButton(getString(R.string.dialog_charge_accept), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new GetDefaultValuesJSONFromServiceTask().execute();
            }
        });
        AlertDialog alertDialog= builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private class GetDefaultValuesJSONFromServiceTask extends AsyncTask<Void,Void,JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try{
                Log.d("Hubs_Values_Url","http://manager.blako.com/api/v1/hubs");
                String defaultValuesResponse = HttpRequest
                        .post("http://manager.blako.com/api/v1/hubs")
                        .connectTimeout(5000).readTimeout(5000).body();

                if (defaultValuesResponse!=null){
                    Log.d("Hubs_Values_Response",defaultValuesResponse);
                    return new JSONObject(defaultValuesResponse);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject!=null){
                new GetDefaultValuesFromJSONTask().execute(jsonObject);
            }else {
                chargeFailed();
            }
        }
    }

    private class GetDefaultValuesFromJSONTask extends AsyncTask<JSONObject,Void,Void>{

        @Override
        protected Void doInBackground(JSONObject... jsonObjects) {
            try {
                if (jsonObjects[0].getBoolean("success")){
                    JSONObject dataObject= jsonObjects[0].getJSONObject("data");
                    dbHelper.deleteAllHubDefaultValues();
                    JSONArray hubsArray = dataObject.getJSONArray("hubs");
                    for (int i=0;i<hubsArray.length();i++){
                        JSONObject hubObject= hubsArray.getJSONObject(i);
                        HubDefaultValue defaultValue= new HubDefaultValue(String.valueOf(hubObject.getInt("city_id")),hubObject.getInt("value"),hubObject.getDouble("rate"),hubObject.getInt("region_id"));
                        dbHelper.saveHubDefaultValue(defaultValue);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new GetHubsJSONFromServiceTask().execute();
        }
    }

    private class GetHubsJSONFromServiceTask extends AsyncTask<Void,Void,JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try{
                String geoHash= "9g3qw";
                String endpointValues= "/"+BkoDataMaganer.getWorkerId(BkoConfigActivity.this)+"/"+geoHash;
                Log.d("Hubs_Url","https://zones-dot-blako-support.appspot.com/hubs"+endpointValues);
                String kmlResponse = HttpRequest
                        .get("https://zones-dot-blako-support.appspot.com/hubs"+endpointValues)
                        .connectTimeout(5000).readTimeout(5000).body();

                if (kmlResponse!=null){
                    Log.d("Hubs_Response",kmlResponse);
                    return new JSONObject(kmlResponse);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject!=null){
                new GetHubsFromJSONTask().execute(jsonObject);
            }else {
                chargeFailed();
            }
        }
    }

    private class GetHubsFromJSONTask extends AsyncTask<JSONObject,Void,Void>{

        @Override
        protected Void doInBackground(JSONObject... jsonObjects) {
            try {
                if (jsonObjects[0].getBoolean("success")){
                    JSONObject zonesObject= jsonObjects[0].getJSONObject("zones");
                    if (preferences.getHubsUpdate()){
                        preferences.setHubsUpdate(false);
                        dbHelper.deleteAllHubs();
                        dbHelper.deleteAllPoints();
                        JSONArray hubsArray = zonesObject.getJSONArray("hubs");
                        for (int i=0;i<hubsArray.length();i++){
                            JSONObject hubObject= hubsArray.getJSONObject(i);
                            String hubId= String.valueOf(hubObject.getInt("id"));
                            HubDefaultValue defaultValue= dbHelper.getHubDefaultValueByHubId(hubId);
                            Hub hub= new Hub(hubId,hubObject.getString("label"),zonesObject.getInt("revision"),defaultValue.getRegionId());
                            dbHelper.saveHub(hub);
                            JSONArray latArray= hubObject.getJSONArray("lats");
                            JSONArray longArray= hubObject.getJSONArray("lons");
                            for (int j=0;j<latArray.length();j++){
                                dbHelper.savePoint(new HubPoints(hubId,latArray.getDouble(j),longArray.getDouble(j)));
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            preferences.setHubsLastCheck(DateUtils.getActualDate());
            initApp();
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

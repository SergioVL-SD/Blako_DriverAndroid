package com.blako.mensajero.Views;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blako.mensajero.Adapters.BkoServicesAdapter;
import com.blako.mensajero.App;
import com.blako.mensajero.BkoDataMaganer;
import com.blako.mensajero.Constants;
import com.blako.mensajero.Dao.BkoUserDao;
import com.blako.mensajero.R;
import com.blako.mensajero.Utils.AppPreferences;
import com.blako.mensajero.Utils.BkoUtilities;
import com.blako.mensajero.Utils.HttpRequest;
import com.blako.mensajero.VO.BkoRecoverStatusVO;
import com.blako.mensajero.VO.BkoRequestResponse;
import com.blako.mensajero.VO.BkoUser;
import com.blako.mensajero.VO.BkoUserStatusResponse;
import com.blako.mensajero.VO.BkoVehicleVO;
import com.blako.mensajero.VO.BkoVehiclesResponse;
import com.blako.mensajero.VO.BkoVersionVO;
import com.google.gson.Gson;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by franciscotrinidad on 12/17/15.
 */
public class BkoVehiclesActivity extends BaseActivity {

    private String visibleReponse;
    private String versionResponse, statusResponse, setVehicleResponse;
    private BkoVersionVO versionResponseVO;
    BkoRecoverStatusVO recoverStatusVO;
    private ListView vehiclesLv;
    private ImageView refreshIv;
    private TextView noResultsTv;
    private String responseWorkerVehicles;
    private BkoVehiclesResponse vehicles;
    private BkoServicesAdapter mAdapter;
    private ImageButton deleteBt, rotateBt, saveBt;
    private View profileOptiosLl;
    private TextView versionTv, nameWorkerTv, workerVehicleTv;
    private CircleImageView messengerProfileIv;
    private String photoFileName = "FOTO";
    private String uploadPhotoResponse;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private   Uri takenPhotoUri;

    private AppPreferences preferences= App.getInstance().getPreferences();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bko_vehicles_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        new SendNewTokenTask().execute(preferences.getFirebaseToken());

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
        vehiclesLv = (ListView) findViewById(R.id.servicesLv);
        nameWorkerTv = (TextView) findViewById(R.id.nameWorkerTv);
        workerVehicleTv = (TextView) findViewById(R.id.workerVehicleTv);
        messengerProfileIv = (CircleImageView) findViewById(R.id.messengerProfileIv);
        profileOptiosLl = (View) findViewById(R.id.profileOptiosLl);
        deleteBt = (ImageButton) findViewById(R.id.deleteBt);
        rotateBt = (ImageButton) findViewById(R.id.rotateBt);
        saveBt = (ImageButton) findViewById(R.id.saveBt);


        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshIv.setVisibility(View.GONE);
                noResultsTv.setVisibility(View.GONE);
                setData();

                //cancelBt.setVisible(true);
            }
        });

        refreshIv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                refreshIv.setVisibility(View.GONE);
                noResultsTv.setVisibility(View.GONE);
                setData();
            }

        });

        messengerProfileIv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                try {

                    if (!checkPermissions()){
                        return;
                    }

                    BkoUser user = BkoUserDao.Consultar(BkoVehiclesActivity.this);
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, BkoUtilities.getPhotoFileUri("" + user.getWorkerId() + "_" + photoFileName + ".jpg"));
                    startActivityForResult(takePictureIntent, 2);
                } catch (Exception e) {

                }

            }

        });
        deleteBt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                messengerProfileIv.setImageDrawable(ContextCompat.getDrawable(BkoVehiclesActivity.this, R.drawable.avatar));
                profileOptiosLl.setVisibility(View.GONE);
                setInfo();

            }

        });

        rotateBt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                rotateImageFile(true);
            }

        });
        saveBt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                AlertaTextViewListener(getString(R.string.blako_estatus_foto_perfil), getString(R.string.blako_estatus_confirma_foto), BkoVehiclesActivity.this,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                try {


                                    BkoUser user = BkoUserDao.Consultar(BkoVehiclesActivity.this);
                                    Uri takenPhotoUri = BkoUtilities.getPhotoFileUri("" + user.getWorkerId() + "_" + photoFileName + ".jpg");
                                    UploadPhoto(takenPhotoUri.getPath());
                                } catch (Exception e) {

                                }
                            }

                        }, null, getString(R.string.blako_aceptar), getString(R.string.blako_rechazar), null);

            }

        });


        vehiclesLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (vehicles == null)
                    return;

                setVehicle(position);


            }

        });


        noResultsTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                try {
                    BkoUserDao.Eliminar(BkoVehiclesActivity.this);
                    Intent intent = new Intent(BkoVehiclesActivity.this, BkoConfigActivity.class);
                    startActivity(intent);
                    finishAffinity();

                } catch (Exception e) {

                }
            }

        });
        setInfo();
        setData();
    }

    private class SendNewTokenTask extends AsyncTask<String,Void,Void> {

        @Override
        protected Void doInBackground(String... strings) {
            Map<String, String> mapVisible = new HashMap<String, String>();
            mapVisible.put("workerId", BkoDataMaganer.getWorkerId(BkoVehiclesActivity.this));
            mapVisible.put("token", strings[0]);
            String tokenServiceResponse = HttpRequest.post(Constants.GET_UPDATE_TOKEN(BkoVehiclesActivity.this), mapVisible, true).connectTimeout(5000).readTimeout(5000).body();
            Log.d("Token_Refreshed", tokenServiceResponse);
            return null;
        }
    }


    private void rotateImageFile(boolean rotate) {

        try {
            BkoUser user = BkoUserDao.Consultar(BkoVehiclesActivity.this);
            Uri takenPhotoUri = BkoUtilities.getPhotoFileUri("" + user.getWorkerId() + "_" + photoFileName + ".jpg");


            Bitmap bitmap = BkoUtilities.decodeSampledBitmapFromResource(takenPhotoUri.getPath(), rotate);
            Drawable drawableImage = new BitmapDrawable(getResources(), bitmap);
            messengerProfileIv.setImageDrawable(drawableImage);


        } catch (Exception ex) {

        }

    }

    private void setData() {


        showWaitDialogWhileExecuting("Cargando...", new Runnable() {
            public void run() {

                try {
                    vehicles = null;
                    responseWorkerVehicles = null;
                    BkoUser user = BkoUserDao.Consultar(BkoVehiclesActivity.this);

                    String workerId;
                    if (user != null) {
                        if (user.getWorkerParentId().equals("0"))
                            workerId = user.getWorkerId();
                        else
                            workerId = user.getWorkerParentId();

                        responseWorkerVehicles = HttpRequest.get(Constants.GET_VEHICLES_UR(BkoVehiclesActivity.this) + "workerId=" + workerId).connectTimeout(5000).readTimeout(5000).body();
                    }
                } catch (Exception e) {

                }

            }
        }, new Runnable() {
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                if (responseWorkerVehicles != null) {

                    try {
                        Gson gson = new Gson();

                        vehicles = gson.fromJson(responseWorkerVehicles, BkoVehiclesResponse.class);
                        if (vehicles != null) {

                            if (vehicles.isResponse() && vehicles.getVehicles().size() > 0) {
                                mAdapter = new BkoServicesAdapter(BkoVehiclesActivity.this, vehicles.getVehicles());
                                vehiclesLv.setAdapter(mAdapter);
                            } else {
                                noResultsTv.setVisibility(View.VISIBLE);
                            }
                        } else {
                            refreshIv.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        refreshIv.setVisibility(View.VISIBLE);
                        //noResultsTv.setVisibility(View.VISIBLE);
                    }

                } else {
                    refreshIv.setVisibility(View.VISIBLE);
                    //noResultsTv.setVisibility(View.VISIBLE);
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {
            profileOptiosLl.setVisibility(View.VISIBLE);
            rotateImageFile(false);
            //doSomethingWithCroppedImage(outputUri);
        }

        if (requestCode == 2) {
            if (

                    resultCode == RESULT_OK) {
                try {


                    try {
                        BkoUser user = BkoUserDao.Consultar(BkoVehiclesActivity.this);
                        takenPhotoUri = BkoUtilities.getPhotoFileUri("" + user.getWorkerId() + "_" + photoFileName + ".jpg");
                        Crop.of(takenPhotoUri, takenPhotoUri).asSquare().start(this);


                    } catch (Exception ex) {

                    }


                } catch (Exception ex) {

                }


            } else if (resultCode == RESULT_CANCELED) {


            }

        }
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                vehiclesLv.setAdapter(null);
                setData();
            } else if (resultCode == RESULT_CANCELED) {

            }

        }
    }


    private void checkStatus() {


        if (!checkPermissions())
            return;

        showWaitDialogWhileExecuting("Cargando...", new Runnable() {
            public void run() {

                try {
                    visibleReponse = null;
                    versionResponse = null;
                    versionResponseVO = null;
                    statusResponse = null;
                    Gson gson = new Gson();
                    versionResponse = HttpRequest
                            .get(Constants.URL_VERSION + "versionCode=" + BkoUtilities.getVersionCode(BkoVehiclesActivity.this) + "&versionName=" + BkoUtilities.getVersionName(BkoVehiclesActivity.this))
                            .connectTimeout(5000).readTimeout(5000).body();


                    if (versionResponse == null)
                        return;


                    versionResponseVO = gson.fromJson(versionResponse, BkoVersionVO.class);


                    if (versionResponseVO == null || !versionResponseVO.isResponse())
                        return;


                    BkoUser user = BkoUserDao.Consultar(BkoVehiclesActivity.this);
                    statusResponse = HttpRequest
                            .get(BkoDataMaganer.getEnviromentUrl(BkoVehiclesActivity.this) + "/trips/getlastorderandtripsactive?workerId=" + user.getWorkerId())
                            .connectTimeout(5000).body();


                    if (statusResponse != null) {
                        recoverStatusVO = gson.fromJson(statusResponse, BkoRecoverStatusVO.class);

                        if (recoverStatusVO != null) {


                            String timeregister = BkoUtilities.nowCurrent();
                            Map<String, String> mapVisible = new HashMap<String, String>();
                            mapVisible.put("workerEmail", user.getEmail());
                            if (recoverStatusVO.isResponse())
                                mapVisible.put("status", "1");
                            else
                                mapVisible.put("status", "0");
                            mapVisible.put("workerId", user.getWorkerId());
                            mapVisible.put("timeregister", timeregister);

                            visibleReponse = HttpRequest.get(Constants.GET_SET_VISIBLE_URL(BkoVehiclesActivity.this), mapVisible, true).connectTimeout(5000).readTimeout(5000).body();


                        } else {
                            visibleReponse = null;
                        }
                    } else {
                        visibleReponse = null;

                    }


                } catch (Exception e) {

                }

            }
        }, new Runnable() {
            public void run() {
                if (versionResponseVO != null) {
                    if (!versionResponseVO.isResponse())

                    {
                        AlertaTextView(getString(R.string.blako_advertencia), versionResponseVO.getMessage(), BkoVehiclesActivity.this,
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
                        if (visibleReponse != null) {


                            try {
                                Gson gson = new Gson();
                                BkoUserStatusResponse response = gson.fromJson(visibleReponse, BkoUserStatusResponse.class);

                                if (response != null) {
                                    if (response.isResponse()) {
                                        BkoUser user = BkoUserDao.Consultar(BkoVehiclesActivity.this);

                                        if (recoverStatusVO != null && recoverStatusVO.isResponse())
                                            user.setIsAvailable(true);
                                        else
                                            user.setIsAvailable(false);
                                        BkoUserDao.Actualizar(BkoVehiclesActivity.this, user);
                                        BkoLoginActivity.setData(null, recoverStatusVO, BkoVehiclesActivity.this);
                                        finishAffinity();
                                        Intent intent = new Intent(BkoVehiclesActivity.this, BkoMainActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(BkoVehiclesActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                } else {
                                    Toast.makeText(BkoVehiclesActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();


                                }


                            } catch (Exception e) {
                                Toast.makeText(BkoVehiclesActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();

                            }


                        }


                    } catch (Exception e) {
                        Toast.makeText(BkoVehiclesActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();

                    }


                } else

                {
                    Toast.makeText(BkoVehiclesActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vechicles, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case R.id.logoutBt:

                BkoUserDao.Eliminar(this);
                BkoDataMaganer.clearData(this);
                Intent intent = new Intent(this, BkoLoginActivity.class);
                startActivity(intent);
                finishAffinity();

                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }


    private void setInfo() {
        try {
            BkoUser user = BkoUserDao.Consultar(this);
            BkoVehicleVO vehicle = BkoDataMaganer.getCurrentVehicle(this);

            if (user != null) {
                nameWorkerTv.setText(user.getName() + " " + user.getLastname());
            }
            if (vehicle != null) {
                workerVehicleTv.setText(vehicle.getBrand() + " " + vehicle.getModel());
            }


            if (user.getPicurl() != null && user.getPicurl().length() > 0)
                Picasso.with(BkoVehiclesActivity.this).load(user.getPicurl()).resize(150, 150).noFade().centerCrop().placeholder(ContextCompat.getDrawable(this, R.drawable.avatar)).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(messengerProfileIv);

        } catch (Exception e) {

        }
    }


    private void UploadPhoto(final String path) {

        showWaitDialogWhileExecuting("Cargando...", new Runnable() {
            public void run() {

                try {

                    uploadPhotoResponse = BkoUtilities.uploadProfilePicture(path, BkoVehiclesActivity.this);


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Runnable() {
            public void run() {


                try {

                    if (uploadPhotoResponse != null) {

                        Gson gson = new Gson();

                        BkoRequestResponse response = gson.fromJson(uploadPhotoResponse, BkoRequestResponse.class);

                        if (response.isResponse()) {
                            profileOptiosLl.setVisibility(View.GONE);

                            BkoUser user = BkoUserDao.Consultar(BkoVehiclesActivity.this);

                            user.setPicurl(Constants.GET_PROFILE_URL(BkoVehiclesActivity.this) + "" + user.getWorkerId() + "/" + user.getWorkerId() + "_FOTO.jpg");
                            BkoUserDao.Actualizar(BkoVehiclesActivity.this, user);

                            AlertaTextView(null, getString(R.string.blako_estatus_foto_cargada), BkoVehiclesActivity.this,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }, getString(R.string.blako_aceptar), null);

                        } else {
                            Toast.makeText(BkoVehiclesActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        Toast.makeText(BkoVehiclesActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(BkoVehiclesActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();
                }

            }


            //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.

        });

    }




    private void setVehicle(final int position) {


        try {



            BkoUser user = BkoUserDao.Consultar(BkoVehiclesActivity.this);

            if (user.getPicurl() == null || user.getPicurl().length() == 0||user.getPicurl().equals("false"))
            {


                AlertaTextView(getString(R.string.blako_advertencia),getString(R.string.blako_estatus_foto_perfil_cambiar), BkoVehiclesActivity.this,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        }, getString(R.string.blako_aceptar), null);



                return;
            }


            if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                int automaticTime= Settings.Global.getInt(getContentResolver(), Settings.Global.AUTO_TIME);


                int automaticTimeZone =Settings.Global.getInt(getContentResolver(), Settings.Global.AUTO_TIME_ZONE);


                if(automaticTime==0||automaticTimeZone==0)
                {

                    AlertaTextView(getString(R.string.blako_advertencia),getString(R.string.blako_estatus_cambiar_fecha_mensaje), BkoVehiclesActivity.this,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {


                                    startActivityForResult(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS), 0);
                                }
                            }, getString(R.string.blako_estatus_cambiar_fecha), null);

                    return;
                }


            }
        }catch ( Exception e)
        {

        }





        showWaitDialogWhileExecuting("Cargando...", new Runnable() {
            public void run() {

                try {

                    BkoUser user = BkoUserDao.Consultar(BkoVehiclesActivity.this);
                    setVehicleResponse = null;
                    String workerId;
                    workerId = user.getWorkerId();

                    BkoVehicleVO currentVehicle = vehicles.getVehicles().get(position);


                    Map<String, String> mapVisible = new HashMap<String, String>();
                    mapVisible.put("workerId", workerId);
                    mapVisible.put("vehiclesId", currentVehicle.getId());
                    mapVisible.put("carbrand", currentVehicle.getBrand());
                    mapVisible.put("carmodel", currentVehicle.getModel());


                    setVehicleResponse = HttpRequest.get(Constants.GET_SET_VEHICLE(BkoVehiclesActivity.this), mapVisible, true).connectTimeout(5000).readTimeout(5000).body();


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Runnable() {
            public void run() {


                try {

                    if (setVehicleResponse != null) {

                        Gson gson = new Gson();

                        BkoRequestResponse response = gson.fromJson(setVehicleResponse, BkoRequestResponse.class);

                        if (response.isResponse()) {

                            try {
                                BkoUser user = BkoUserDao.Consultar(BkoVehiclesActivity.this);


                                BkoVehicleVO currentVehicle = vehicles.getVehicles().get(position);
                                BkoDataMaganer.setCurrentVehicle(currentVehicle, BkoVehiclesActivity.this);

                                checkStatus();


                            } catch (Exception e) {


                            }
                        } else {
                            Toast.makeText(BkoVehiclesActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        Toast.makeText(BkoVehiclesActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(BkoVehiclesActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();
                }

            }


            //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.

        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("GeneralFlux","Activity: "+BkoVehiclesActivity.this.getLocalClassName());
    }

}

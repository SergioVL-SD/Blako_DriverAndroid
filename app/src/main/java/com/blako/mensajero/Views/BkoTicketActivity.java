package com.blako.mensajero.Views;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.blako.mensajero.Adapters.BkoSimpleRecyclerViewAdapter;
import com.blako.mensajero.BkoDataMaganer;
import com.blako.mensajero.Constants;
import com.blako.mensajero.Custom.DrawingView;
import com.blako.mensajero.Dao.BkoUserDao;
import com.blako.mensajero.R;
import com.blako.mensajero.Services.BkoSendLocationToServer;
import com.blako.mensajero.Utils.BkoUtilities;
import com.blako.mensajero.Utils.HttpRequest;
import com.blako.mensajero.VO.BkoChildTripVO;
import com.blako.mensajero.VO.BkoCompleteResponse;
import com.blako.mensajero.VO.BkoItem;
import com.blako.mensajero.VO.BkoRequestResponse;
import com.blako.mensajero.VO.BkoUser;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by franciscotrinidad on 1/21/16.
 */
public class BkoTicketActivity extends BaseActivity {


    private DrawingView drawView;
    private EditText whoReceiptEt;
    private Button aceptBt;
    private String photoFileName = "photo";
    private BkoCompleteResponse completeResponseVO;
    private int countArriveIntent = 0;
    private BkoChildTripVO currentTrip;
    private String confirmItemsResponse;

    private String fileName;
    private TextView mTitle, clientTv, orderTv, stepsTv,pickDeliveryTitleTv;
    private ImageButton cancelBt, okBt;
    private RecyclerView itemsRv;
    private void deleteFile() {


        try {
            Uri takenPhotoUri = BkoUtilities.getPhotoFileUri(photoFileName);
            File f = new File(takenPhotoUri.getPath());

            if (f.exists())
                f.delete();

        } catch (Exception e) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bko_ticket_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
            mTitle.setTextColor(ContextCompat.getColor(this, R.color.blako_red_two));
            mTitle.setText(getString(R.string.blako_ticket));
        }

        whoReceiptEt = (EditText) findViewById(R.id.whoReceiptEt);
        aceptBt = (Button) findViewById(R.id.aceptBt);
        orderTv = (TextView) findViewById(R.id.orderTv);
        itemsRv = (RecyclerView) findViewById(R.id.itemsRv);
        drawView = (DrawingView) findViewById(R.id.drawing);
        aceptBt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                onComplete();
            }

        });


        if (BkoDataMaganer.getCurrentTrip(BkoTicketActivity.this) == null) {
            return;
        }


        BkoChildTripVO currentTrip = BkoDataMaganer.getCurrentTrip(BkoTicketActivity.this);
        itemsRv.setLayoutManager(new LinearLayoutManager(this));
        itemsRv.setNestedScrollingEnabled(false);
        setData();
    }

    private void setData() {

        try {
            BkoChildTripVO current = BkoDataMaganer.getCurrentTrip(this);
            BkoChildTripVO   delivery =   BkoDataMaganer.getCuurentTemporaryTrip().getDestino().get(0);
            // PRODUCTS

            ArrayList<String> items = new ArrayList<String>();
            Gson gson = new Gson();
            List<BkoItem> products = Arrays.asList(gson.fromJson(delivery.getBko_queuedtasks_products(), BkoItem[].class));
            for (BkoItem item : products) {
                items.add(item.getItem());
            }
            BkoSimpleRecyclerViewAdapter adapter = new BkoSimpleRecyclerViewAdapter(items);
            itemsRv.setAdapter(adapter);
            orderTv.setText(delivery.getBko_customeraddress_alias());
            stepsTv.setText(delivery.getBko_customeraddress_note());
            clientTv.setText(delivery.getBko_customeraddress_contact());
            pickDeliveryTitleTv.setText(R.string.blako_destino_entregar);
        }catch (Exception e) {
            e.fillInStackTrace();
        }
    }


    private void onComplete() {

        if (!drawView.isDrawed) {
            Toast.makeText(BkoTicketActivity.this, getString(R.string.blako_ticket_falta_firma), Toast.LENGTH_SHORT).show();
            return;
        }

        if (whoReceiptEt.getText().toString().length() == 0) {
            Toast.makeText(BkoTicketActivity.this, getString(R.string.blako_ticket_falta_quien_recibe), Toast.LENGTH_SHORT).show();

            return;
        }


        try {
            View rootView = findViewById(R.id.drawing);
            File newBackgroundBitmap = saveFileToStorage(rootView);


            if (newBackgroundBitmap != null) {
                AlertaTextViewListener(getString(R.string.blako_advertencia), getString(R.string.blako_ticket_enviar), BkoTicketActivity.this,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                BkoChildTripVO currentTrip = BkoDataMaganer.getCurrentTrip(BkoTicketActivity.this);

                                if (currentTrip == null)
                                    return;


                                saveTicket();
                            }
                        }, null, getString(R.string.blako_aceptar), getString(R.string.blako_cancelar), null);
            }
        } catch (Exception e) {
            Toast.makeText(BkoTicketActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();
        }


    }

    private void saveTicket() {
        showWaitDialogWhileExecuting("Cargando...", new Runnable() {
            public void run() {

                try {
                    completeResponseVO = null;
                    BkoUser user = BkoUserDao.Consultar(BkoTicketActivity.this);
                    currentTrip = BkoDataMaganer.getCurrentTrip(BkoTicketActivity.this);
                    currentTrip.setReceivedcomment("");
                    currentTrip.setReceivedpackage(whoReceiptEt.getText().toString().trim());
                    currentTrip.setSuccess(true);

                    if (BkoSendLocationToServer.oneLocationDataToWebsite(BkoDataMaganer.getCurrentUserLocation(getApplicationContext()), getApplicationContext())) {
                        confirmItemsResponse = null;
                        String workerId;
                        workerId = user.getWorkerId();
                        Map<String, String> mapVisible = new HashMap<String, String>();
                        mapVisible.put("workerId", workerId);
                        mapVisible.put("tripId", currentTrip.getBko_orders_trips_id());
                        mapVisible.put("confirmed", "1");
                        mapVisible.put("orderId", currentTrip.getBko_orders_id());

                        mapVisible.put("tripsType", currentTrip.getBko_orders_trips_type());
                        mapVisible.put("tripIdDestino", BkoDataMaganer.getCuurentTemporaryTrip().getDestino().get(0).getBko_orders_trips_id());
                        confirmItemsResponse = HttpRequest.get(Constants.GET_CONFIRM_ITEMS(BkoTicketActivity.this), mapVisible, true).connectTimeout(5000).readTimeout(5000).body();

                        if (confirmItemsResponse != null && confirmItemsResponse.length() != 0) {
                            Gson gson = new Gson();
                            BkoRequestResponse response = gson.fromJson(confirmItemsResponse, BkoRequestResponse.class);
                            if (response.isResponse()) {
                                BkoDataMaganer.getCuurentTemporaryTrip().getDestino().get(0).setSuccess(true);
                                completeResponseVO = BkoUtilities.completeTrip(currentTrip, BkoTicketActivity.this);
                            }

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Runnable() {
            public void run() {
                if (completeResponseVO != null && completeResponseVO.isResponse()) {
                    onCompleteByIntents();
                } else {
                    Toast.makeText(BkoTicketActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    private void onCompleteByIntents() {
        BkoDataMaganer.setCurrentTrip(null, BkoTicketActivity.this);
        if(BkoDataMaganer.getCurrentOffer(this)!=null) {
            BkoDataMaganer.setStatusService(Constants.SERVICE_STATUS_TRIP_FINISHED, BkoTicketActivity.this);
        }

        finishAffinity();
        Intent intent = new Intent(BkoTicketActivity.this, BkoMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {



        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                try {
                    Bitmap bitmapImage = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().toString() + "/blako_data/" + BkoDataMaganer.getCurrentTrip(this).getBko_orders_trips_id() + ".png");
                    Drawable drawableImage = new BitmapDrawable(getResources(), bitmapImage);
                    //signatureIv.setImageBitmap(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().toString() + "/blako_data/" + BkoDataMaganer.getCurrentTrip(this).getBko_orders_trips_id() + ".png"));
                } catch (Exception e) {

                }

            } else if (resultCode == RESULT_CANCELED) {


            }

        } else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {


                try {


                    BkoChildTripVO currentTrip = BkoDataMaganer.getCurrentTrip(BkoTicketActivity.this);

                    Uri takenPhotoUri = BkoUtilities.getPhotoFileUri(photoFileName + "_" + currentTrip.getBko_orders_trips_id() + ".jpg");
                    Bitmap bitmap = BkoUtilities.decodeSampledBitmapFromResource(takenPhotoUri.getPath(), false);
                    bitmap = BkoUtilities.modifyOrientation(bitmap, takenPhotoUri.getPath(), 0);
                    Drawable drawableImage = new BitmapDrawable(getResources(), bitmap);

                } catch (Exception ex) {

                }


            } else if (resultCode == RESULT_CANCELED) {


            }

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case R.id.completeBt:
                onComplete();

                break;
            default:
                return super.onOptionsItemSelected(item);
        }/**/

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_complete, menu);


        return true;
    }


    private File saveFileToStorage(View rootView) {


        rootView.setDrawingCacheEnabled(true);
        Bitmap mBitmap = rootView.getDrawingCache();


        String mFileName = BkoDataMaganer.getCurrentStatusRequest(this).getWorkerrequestid() + ".png";


        File f = null;


        try {
            File folder = new File(Environment.getExternalStorageDirectory() +
                    File.separator + "blako_data");
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdir();
            }

            String path = Environment.getExternalStorageDirectory().toString() + "/blako_data/" + BkoDataMaganer.getCurrentTrip(this).getBko_orders_trips_id() + ".png";

            f = new File(path);

            if (f.exists())
                f.delete();
            f.createNewFile();  // your mistake was at here
            FileOutputStream fos = new FileOutputStream(f);

            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

            fos.flush();

            fos.close();

        } catch (IOException e) {

            e.printStackTrace();
        }

        return f;

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("GeneralFlux","Activity: "+BkoTicketActivity.this.getLocalClassName());
    }

}

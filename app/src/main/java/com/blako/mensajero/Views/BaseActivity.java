package com.blako.mensajero.Views;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.blako.mensajero.R;
import com.blako.mensajero.Utils.BkoUtilities;
import com.blako.mensajero.Utils.NotificationDialog;


public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void showWaitDialogWhileExecuting(String msg, final Runnable task, final Runnable completion) {

        if (msg != null)
            showWaitDialogWhileExecutingg(msg, task, completion);
        else
            executeInBackground(task, completion);

    }

    public void showWaitDialogWhileExecuting(final Runnable task, final Runnable completion) {
        showWaitDialogWhileExecutingg("", task, completion);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void showWaitDialogWhileExecutingg(String msg, final Runnable task, final Runnable completion) {

        final ProgressDialog dialogo = new ProgressDialog(this);
        dialogo.setMessage(msg);
        dialogo.setCanceledOnTouchOutside(false);
        dialogo.setCancelable(false);

        AsyncTask<Void, Void, Throwable> asyncTask = new AsyncTask<Void, Void, Throwable>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                /*if (!dialogo.isShowing())
                    dialogo.show();*/
            }

            @Override
            protected Throwable doInBackground(Void... params) {
                try {
                    if (task != null)
                        task.run();
                } catch (Exception e) {
                    /*if ((dialogo != null) && dialogo.isShowing()) {

                        dialogo.dismiss();
                    }*/
                    Log.e("doInBackground", "Excpetion" + e);
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Throwable result) {
                if (result != null) {

                    if (BaseActivity.this.isDestroyed()) { // or call isFinishing() if min sdk version < 17
                        return;
                    }
                    /*dialogo.dismiss();*/
                    handleException(getApplicationContext(), result);
                } else if (completion != null) {
                    /*if ((dialogo != null) && dialogo.isShowing()) {
                        if (BaseActivity.this.isDestroyed()) { // or call isFinishing() if min sdk version < 17
                            return;
                        }
                        dialogo.dismiss();
                    }*/
                    completion.run();

                } /*else if ((dialogo != null) && dialogo.isShowing()) {
                    if (BaseActivity.this.isDestroyed()) { // or call isFinishing() if min sdk version < 17
                        return;
                    }

                    dialogo.dismiss();
                }*/
            }
        };
        asyncTask.execute();
    }

    public void executeInBackground(Runnable task, Runnable completion) {
        executeInBackgroundd(task, completion);
    }

    /**
     * Ejecuta una tarea en segundo plano y al terminar ejecuta la segunda tarea
     ***/
    public void executeInBackgroundd(final Runnable task, final Runnable completion) {
        AsyncTask<Void, Void, Throwable> asyncTask = new AsyncTask<Void, Void, Throwable>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Throwable doInBackground(Void... params) {
                try {
                    if (task != null)
                        task.run();
                } catch (Exception e) {
                    Log.e("executeInBackground", "Excpetion" + e);
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Throwable result) {
                if (result != null) {
                    handleExceptionInBackground(result);
                } else if (completion != null) {
                    completion.run();
                }
            }
        };
        asyncTask.execute();
    }

    /**
     * Cuando se genera una excepcion se llama este metodo para manejarla
     */
    public void handleExceptionInBackground(Throwable caught) {
        Log.e("handleExcepInBackground", "causa: " + caught.getMessage().toString());
    }

    /**
     * Cuando se genera una excepcion se llama este metodo para manejarla
     */
    public void handleException(Context ctx, Throwable caught) {
        Log.e("handleException()", "causa: " + caught.getMessage().toString());
    }


    protected AlertDialog AlertaTextView(String TituloAlerta, String MensajeAlerta, Context context,
                                         OnClickListener listenerPositive, String positiveButtonText, View CustomView) {

        return NotificationDialog.showDialog(TituloAlerta, MensajeAlerta, context, listenerPositive, positiveButtonText, CustomView);

    }

    protected AlertDialog AlertaTextViewListener(String TituloAlerta, String MensajeAlerta, Context context,
                                                 OnClickListener listenerPositive, OnClickListener listenerNegative, String positiveButtonText,
                                                 String negativeButtonText, View CustomView) {

        return NotificationDialog.showDialogListener(TituloAlerta, MensajeAlerta, context, listenerPositive, listenerNegative,
                positiveButtonText, negativeButtonText, CustomView);

    }


    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    protected boolean checkMockLocation(final Context context, final Location location) {
        boolean isMock = false;


        if (location == null)
            return false;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && location != null) {
            isMock = location.isFromMockProvider();


            if (!isMock) {


                if (location.getProvider().equals("mock"))
                    isMock = true;
                else if (isAllowMockLocationsOn(context))
                    isMock = true;
            }

        } else {
            isMock = !Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
            // Toast.makeText(context, "Desactive el uso de ubicaciones simuladas!", Toast.LENGTH_SHORT).show();

        }


        return isMock;

    }

    public static boolean isAllowMockLocationsOn(Context context) {
        // returns true if mock location enabled, false if not enabled.

        int sdk = android.os.Build.VERSION.SDK_INT;

        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
                return false;
            else {
                return true;
            }
        } else {
            return false;
        }
    }




    protected AlertDialog checkGPSConection(final Context context) {
        if (BkoUtilities.isGPSEnabled(context))
            return null;

        final AlertDialog dialog = AlertaTextView(context.getString(R.string.blako_sin_gps_titulo), context.getString(R.string.blako_sin_gps_mensaje), context,
                null, context.getString(R.string.blako_reintentar), null);

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (BkoUtilities.isGPSEnabled(context))
                    dialog.dismiss();

            }
        });

        return dialog;
    }


    protected AlertDialog checkInternetConection(final Context context) {
        if (BkoUtilities.isNetWorkConnected(context))
            return null;

        final AlertDialog dialog = AlertaTextView(context.getString(R.string.blako_sin_internet_titulo), context.getString(R.string.blako_sin_internet_mensaje), context,
                null, context.getString(R.string.blako_reintentar), null);

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (BkoUtilities.isNetWorkConnected(context))
                    dialog.dismiss();

            }
        });
        return dialog;
    }

    protected boolean checkPermissions() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED  || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA},
                        1000);


                return false;


            } else {

                return true;
            }
        } catch (Exception e) {

        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case android.R.id.home:

                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }/**/

        return super.onOptionsItemSelected(item);
    }
}

package com.blako.mensajero.Views;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.blako.mensajero.R;
import com.blako.mensajero.Utils.BkoUtilities;
import com.blako.mensajero.Utils.NotificationDialog;


public class BaseFragment extends Fragment {


    public void showWaitDialogWhileExecuting(String msg, final Runnable task, final Runnable completion) {

        if (msg != null)
            showWaitDialogWhileExecutingg(msg, task, completion);
        else
            executeInBackground(task, completion);

    }

    public void showWaitDialogWhileExecuting(final Runnable task, final Runnable completion) {
        showWaitDialogWhileExecutingg("", task, completion);
    }

    protected void showWaitDialogWhileExecutingg(String msg, final Runnable task, final Runnable completion) {

        final ProgressDialog dialogo = new ProgressDialog(getActivity());
        dialogo.setMessage(msg);
        dialogo.setCanceledOnTouchOutside(false);
        dialogo.setCancelable(false);

        AsyncTask<Void, Void, Throwable> asyncTask = new AsyncTask<Void, Void, Throwable>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                /*dialogo.show();*/
            }

            @Override
            protected Throwable doInBackground(Void... params) {
                try {

                    if (task != null)
                        task.run();
                } catch (Exception e) {
                    /*dialogo.dismiss();*/
                    Log.e("doInBackground", "Excpetion" + e);
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Throwable result) {
                if (result != null) {
                    /*dialogo.dismiss();*/
                    handleException(getActivity(), result);
                } else if (completion != null) {
                    /*dialogo.dismiss();*/
                    completion.run();

                } /*else
                    dialogo.dismiss();*/
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

    protected String ensureNotNullString(String value) {

        return BkoUtilities.ensureNotNullString(value);

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

    protected void onFinish() {
        AlertaTextViewListener(getString(R.string.blako_advertencia), getString(R.string.blako_salir_aplicacion), getActivity(),
                new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                }, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                }, getString(R.string.blako_aceptar), getString(R.string.blako_cancelar), null);
    }

}

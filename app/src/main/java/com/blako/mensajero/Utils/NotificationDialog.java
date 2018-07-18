package com.blako.mensajero.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.blako.mensajero.R;


public class NotificationDialog {

    public static AlertDialog showDialog(String TituloAlerta, String MensajeAlerta, Context context,
                                         OnClickListener listenerPositive, String positiveButtonText,View CustomView) {
        AlertDialog.Builder b = new AlertDialog.Builder(context);
        if (TituloAlerta != null && TituloAlerta.length() > 0) {
            TextView title = new TextView(context);
            title.setText(Html.fromHtml("<b>" + TituloAlerta + "</b>"));
            title.setTextSize(18);
            title.setGravity(Gravity.CENTER);
            title.setPadding(10, 5, 10, 5);
            title.setTextColor(ContextCompat.getColor(context, R.color.blako_background));
            b.setCustomTitle(title);
        }

        if(CustomView!=null)
        {
            b.setView(CustomView);
        }
        else {
            TextView message = new TextView(context);
            message.setText(Html.fromHtml("<small>" + MensajeAlerta + "</small>"));


            message.setPadding(10, 20, 10, 20);
            message.setGravity(Gravity.CENTER);
            message.setTextSize(18);
            message.setTextColor(ContextCompat.getColor(context, R.color.blako_black));
            b.setView(message);
        }


        b.setCancelable(false);
        if (listenerPositive != null)
            b.setPositiveButton(positiveButtonText, listenerPositive);
        else {
            b.setPositiveButton(positiveButtonText, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }
        AlertDialog a = b.create();

        a.setInverseBackgroundForced(true);
        a.show();
        a.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.blako_background));

        return a;
    }

    public static AlertDialog showDialogListener(String TituloAlerta, String MensajeAlerta, Context context,
                                          OnClickListener listenerPositive, OnClickListener listenerNegative, String positiveButtonText,
                                          String negativeButtonText,View CustomView) {
        AlertDialog.Builder b = new AlertDialog.Builder(context);


        if (TituloAlerta != null && TituloAlerta.length() > 0) {
            TextView title = new TextView(context);
            title.setText(Html.fromHtml("<b>" + TituloAlerta + "</b>"));
            title.setTextSize(18);
            title.setPadding(10, 20, 10, 20);
            title.setGravity(Gravity.CENTER);
            title.setTextColor(ContextCompat.getColor(context, R.color.blako_background));
            b.setCustomTitle(title);
        }

        if(CustomView!=null)
        {
            b.setView(CustomView);
        }
        else {
            TextView message = new TextView(context);
            message.setText(Html.fromHtml("<small>" + MensajeAlerta + "</small>"));


            message.setPadding(10, 20, 10, 20);
            message.setGravity(Gravity.CENTER);
            message.setTextSize(18);
            message.setTextColor(ContextCompat.getColor(context, R.color.blako_black));
            b.setView(message);
        }
        b.setCancelable(false);
        if (listenerPositive != null)
            b.setPositiveButton(positiveButtonText, listenerPositive);
        else {
            b.setPositiveButton(positiveButtonText, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }


        if (listenerNegative != null)
            b.setNegativeButton(negativeButtonText, listenerNegative);
        else {
            b.setNegativeButton(negativeButtonText, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }

        AlertDialog a = b.create();

        a.setInverseBackgroundForced(true);
        a.show();
        a.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.blako_gray));
        a.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.blako_background));
        return a;
    }


}

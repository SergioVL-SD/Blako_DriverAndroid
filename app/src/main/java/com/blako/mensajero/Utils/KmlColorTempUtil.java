package com.blako.mensajero.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.TextView;

import com.blako.mensajero.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class KmlColorTempUtil {

    public static int getColorByTempValue(Context context, int value){
        int color;
        if (value>80){
            color= context.getResources().getColor(R.color.c_kml_temp_5);
        }else if (value>60){
            color= context.getResources().getColor(R.color.c_kml_temp_4);
        }else if (value>40){
            color= context.getResources().getColor(R.color.c_kml_temp_3);
        }else if (value>20){
            color= context.getResources().getColor(R.color.c_kml_temp_2);
        }else {
            color= context.getResources().getColor(R.color.c_kml_temp_1);
        }
        return color;
    }

    public static MarkerOptions createTextMarkerOptions(final Context context, final GoogleMap map,
                                final LatLng location, final String text, final int padding,
                                final int fontSize) {
        MarkerOptions marker = null;

        if (context == null || map == null || location == null || text == null
                || fontSize <= 0) {
            return marker;
        }

        final TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(fontSize);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setShadowLayer(6,2,2,context.getResources().getColor(R.color.c_rate_shadow));

        final Paint paintText = textView.getPaint();

        final Rect boundsText = new Rect();
        paintText.getTextBounds(text, 0, textView.length(), boundsText);
        paintText.setTextAlign(Paint.Align.CENTER);

        final Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        final Bitmap bmpText = Bitmap.createBitmap(boundsText.width() + 2
                * padding, boundsText.height() + 2 * padding, conf);

        final Canvas canvasText = new Canvas(bmpText);
        paintText.setColor(context.getResources().getColor(R.color.c_rate));

        canvasText.drawText(text, canvasText.getWidth() / 2,
                canvasText.getHeight() - padding - boundsText.bottom, paintText);

        final MarkerOptions markerOptions = new MarkerOptions()
                .position(location)
                .icon(BitmapDescriptorFactory.fromBitmap(bmpText))
                .anchor(0.5f, 1);

        return markerOptions;
    }

    public static LatLng getCenterOfPolygon(List<LatLng> latLngList) {
        double[] centroid = {0.0, 0.0};
        for (int i = 0; i < latLngList.size(); i++) {
            centroid[0] += latLngList.get(i).latitude;
            centroid[1] += latLngList.get(i).longitude;
        }
        int totalPoints = latLngList.size();
        return new LatLng(centroid[0] / totalPoints, centroid[1] / totalPoints);
    }
}

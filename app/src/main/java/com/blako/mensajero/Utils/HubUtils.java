package com.blako.mensajero.Utils;

import android.content.Context;
import android.graphics.Color;

import com.google.android.gms.maps.model.PolygonOptions;

public class HubUtils {

    public static PolygonOptions getDefaultPolygonOptions(Context context, int temp){
        PolygonOptions kmlHub= new PolygonOptions();
        int colorAlpha= 70; //< 0 - 255 >
        int colorStroke= KmlColorTempUtil.getColorByTempValue(context,temp);
        int colorFill= Color.argb(colorAlpha,Color.red(colorStroke),Color.green(colorStroke),Color.blue(colorStroke));
        kmlHub.strokeColor(colorStroke).fillColor(colorFill);
        kmlHub.strokeWidth(5);
        return kmlHub;
    }

    public static void changePolygonOptionsValues(Context context, PolygonOptions options, int temp){
        int colorAlpha= 70; //< 0 - 255 >
        int colorStroke= KmlColorTempUtil.getColorByTempValue(context,temp);
        int colorFill= Color.argb(colorAlpha,Color.red(colorStroke),Color.green(colorStroke),Color.blue(colorStroke));
        options.strokeColor(colorStroke).fillColor(colorFill);
    }
}

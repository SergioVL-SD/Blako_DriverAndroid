package com.blako.mensajero.Utils;

import android.content.Context;
import android.graphics.Color;

import com.blako.mensajero.models.HubConfig;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;

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

    public static ArrayList<HubConfig> filterHubConfigsByRegion(ArrayList<HubConfig> hubConfigs, int regionId){
        ArrayList<HubConfig> hubConfigsNew= new ArrayList<>();
        for (HubConfig hubConfig:hubConfigs){
            if (hubConfig.getRegionId()==regionId){
                hubConfigsNew.add(hubConfig);
            }
        }
        return hubConfigsNew;
    }
}

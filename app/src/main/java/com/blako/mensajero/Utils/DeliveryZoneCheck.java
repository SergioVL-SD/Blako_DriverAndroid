package com.blako.mensajero.Utils;

import android.content.Context;
import android.util.Log;

import com.blako.mensajero.R;
import com.blako.mensajero.models.HubConfig;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.data.kml.KmlContainer;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.android.data.kml.KmlPolygon;


import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DeliveryZoneCheck {

    public static KmlLayer getLayerFromKml(Context context, GoogleMap map, FileInputStream inputStream){
        try {
            //KmlLayer kmlLayer= new KmlLayer(map, R.raw.delivery_zones, context);
            KmlLayer kmlLayer= new KmlLayer(map, inputStream, context);
            kmlLayer.addLayerToMap();
            return kmlLayer;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static KmlContainer getMainContainer(KmlLayer kmlLayer){
        if (kmlLayer!=null){
            List<KmlContainer> containers= new ArrayList<>();
            for (KmlContainer container:kmlLayer.getContainers()){
                containers.add(container);
                /*for (KmlContainer subContainer:container.getContainers()){
                    containers.add(subContainer);
                }*/
            }
            return containers.get(0); //-> Regresa capa "Áreas de Adquisición"
        }
        return null;
    }

    public static List<KmlPlacemark> getPlacemarks(KmlContainer mainContainer){
        List<KmlPlacemark> placemarks= new ArrayList<>();
        if (mainContainer!=null){
            for (KmlPlacemark placemark:mainContainer.getPlacemarks()){
                placemarks.add(placemark);
            }
        }
        return placemarks;
    }

    public static String getActualAreaFromLocation(Context context, List<KmlPlacemark> placemarks, LatLng location){
        for (KmlPlacemark placemark:placemarks){
            Log.d("Kml_placemark",placemark.getProperty("name"));
            KmlPolygon polygon= (KmlPolygon) placemark.getGeometry();
            if (polygon!=null){
                List<LatLng> boundaryCoordinates = polygon.getOuterBoundaryCoordinates();
                if (PolyUtil.containsLocation(location,boundaryCoordinates,true)){
                    return placemark.getProperty("name");
                }
            }
        }
        return context.getString(R.string.unknown_area);
    }

    public static Integer getActualHubFromLocation(ArrayList<HubConfig> hubConfigs, LatLng location){
        for (HubConfig hubConfig:hubConfigs){
            List<LatLng> boundaryCoordinates = hubConfig.getPolygonOptions().getPoints();
            if (PolyUtil.containsLocation(location,boundaryCoordinates,true)){
                return hubConfig.getHubId();
            }
        }
        return 1;
    }

    public static KmlPlacemark getActualPlacemarkFromLocation(Context context, List<KmlPlacemark> placemarks, LatLng location){
        for (KmlPlacemark placemark:placemarks){
            KmlPolygon polygon= (KmlPolygon) placemark.getGeometry();
            if (polygon!=null){
                List<LatLng> boundaryCoordinates = polygon.getOuterBoundaryCoordinates();
                if (PolyUtil.containsLocation(location,boundaryCoordinates,true)){
                    return placemark;
                }
            }
        }
        return null;
    }
}

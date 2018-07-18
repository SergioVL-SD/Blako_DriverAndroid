package com.blako.mensajero;

import android.content.Context;

public class Constants {

    public static final String ACTION_SERVICE_LOCATION = "com.blako.mensajero.action.LOCATION";
    public static final String EXTRA_SERVICE_LOCATION = "com.blako.mensajero.extra.LOCATION";

    public static final float DEFAULT_MAP_TRIPS = 15f;
    public static final float TRTP_DETAIL_MAP_ZOOM = 17f;
    public static final float DEFAULT_MAP_ZOOM = 16.5f;
    public static final float DEFAULT_OFFER_LOCATION_ZOOM = 15f;
    public static final String CORREO_EXPRESION = "^[_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{2,4})$";
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    // STATUS SERVICE
    public static final int SERVICE_STATUS_FREE = 0;
    public static final int SERVICE_STATUS_INCOMING_REQUEST = 1;
    public static final int SERVICE_STATUS_WORKER_ARRIVED = 3;
    public static final int SERVICE_STATUS_WITH_SERVICE_AND_CANCELED = 7;
    public static final int SERVICE_STATUS_TRIP_FINISHED = 8;
    public static final int SERVICE_STATUS_PAYMENT = 9;
    public static final int SERVICE_STATUS_WITH_QUEUEDTASKS = 10;
    public static final int SERVICE_STATUS_REQUEST_ACEPTED = 11;
    //OFFER
    public static final int SERVICE_STATUS_WITH_OFFER = 12;
    public static final int SERVICE_STATUS_WITH_OFFER_CHECKIN_FINISH = 14;
    public static final String URL_VERSION = "https://food.blako.com/app?";
    public static final String URL_LOGIN = "https://food.blako.com/public/workers/login?";
    public static String GET_CALCULATE_ETA_URL(Context context) {
        String url = BkoDataMaganer.getEnviromentUrl(context) + "/gps/calculationeta?";
        return url;
    }


    public static String GET_TRIPS_ACTIVE(Context context) {
        String url = BkoDataMaganer.getEnviromentUrl(context) + "/trips/getlastorderandtripsactive?";
        return url;
    }

    public static String GET_GO_TRIPS(Context context) {
        String url = BkoDataMaganer.getEnviromentUrl(context) + "/tripssd/gettrips?";
        return url;
    }


    public static String GET_VEHICLES_UR(Context context) {
        String url = BkoDataMaganer.getEnviromentUrl(context) + "/vehicles/getblakovehicles?";
        return url;
    }

    public static String GET_SET_VISIBLE_URL(Context context) {
        String url = BkoDataMaganer.getEnviromentUrl(context) + "/gps/visibilityworkers?";
        return url;
    }


    public static String GET_UPLOAD_GPS_POSITION(Context context) {
        String url = BkoDataMaganer.getEnviromentUrl(context) + "/gps/index?";
        return url;
    }



    public static String GET_WORKER_STARTING_URL(Context context) {
        String url = BkoDataMaganer.getEnviromentUrl(context) + "/trips/workerstart?";
        return url;
    }


    public static String GET_STATUS_REQUEST_URL(Context context) {
        String url = BkoDataMaganer.getEnviromentUrl(context) + "/orders/orderstatus?";
        return url;
    }

    public static String GET_WORKER_COMPLETED_URL(Context context) {
        String url = BkoDataMaganer.getEnviromentUrl(context) + "/trips/workercompleted?";
        return url;
    }




    public static String GET_SEND_TICKET(Context context) {
        String url = BkoDataMaganer.getEnviromentUrl(context) + "/app/uploadreceipt";
        return url;
    }


    public static String GET_UPLOAD_PHOTO_PROFILE(Context context) {
        String url = BkoDataMaganer.getEnviromentUrl(context) + "/app/uploadphotoworker";
        return url;
    }


    public static String GET_SWITCH_VISIBILITY(Context context) {
        String url = BkoDataMaganer.getEnviromentUrl(context) + "/workers/visibilitytype?";
        return url;
    }





    public static String GET_SEND_TXT(Context context) {
        String url = BkoDataMaganer.getEnviromentUrl(context) + "/gps/uploadtxtandroid";
        return url;
    }


    public static String GET_PROFILE_URL(Context context) {
        String url = "http://www.socios.blako.com/public/upload/worker/";
        return url;
    }



    public static String GET_OFFERS(Context context) {
        String url = BkoDataMaganer.getEnviromentUrl(context) + "/announcement/getofferstaken?";
        return url;
    }

    public static String GET_OFFER(Context context) {
        String url = BkoDataMaganer.getEnviromentUrl(context) + "/announcement/taketurn?";
        return url;
    }

    public static String GET_OFFER_REPORT(Context context) {
        String url = BkoDataMaganer.getEnviromentUrl(context) + "/announcement/stagereport?";
        return url;
    }



    public static String GET_SET_VEHICLE(Context context) {
        String url = BkoDataMaganer.getEnviromentUrl(context) + "/vehicles/setvehicleselectbyworker?";
        return url;
    }




    //??????????


    public static String GET_OFFERS_HISTORY(Context context) {
        String url = BkoDataMaganer.getEnviromentUrl(context) + "/reports/getservicesoffertworker?";
        return url;
    }

    public static String GET_SERVICES_NOT_OFFER(Context context) {
        String url = BkoDataMaganer.getEnviromentUrl(context) + "/reports/getserviceswithoutoffer?";
        return url;
    }

    public static String GET_OFFERS_GROUPS(Context context) {
        String url = BkoDataMaganer.getEnviromentUrl(context) + "/announcement/getgroups?";
        return url;
    }

    public static String GET_OFFERS_BY_GROUP(Context context) {
        String url = BkoDataMaganer.getEnviromentUrl(context) + "/announcement/getoffers?";
        return url;
    }


    public static String GET_OFFERS_DATE(Context context) {
        String url = BkoDataMaganer.getEnviromentUrl(context) + "/announcement/getdays?";
        return url;
    }

    public static String GET_CONFIRM_ITEMS(Context context) {
        String url = BkoDataMaganer.getEnviromentUrl(context) + "/tripssd/itemconfirm?";
        return url;
    }

    public static String GET_CONFIRM_AWAIT(Context context) {
        String url = BkoDataMaganer.getEnviromentUrl(context) + "/queuedtasks/responserequest?";
        return url;
    }


    public static String GET_REPORT_CHECKIN(Context context) {
        String url = BkoDataMaganer.getEnviromentUrl(context) + "/trips/reportcheckin?";
        return url;
    }


    public static String GET_UPDATE_TOKEN(Context context) {
        String url = BkoDataMaganer.getEnviromentUrl(context) + "/workers/updatetoken?";
        return url;
    }
}



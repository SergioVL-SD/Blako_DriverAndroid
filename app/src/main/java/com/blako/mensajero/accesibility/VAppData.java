package com.blako.mensajero.accesibility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by franciscotrinidad on 17/01/18.
 */

public class VAppData {

    /////////////////////////////////// PACKAGES ///////////////////////////////////
    public static ArrayList<String> appPackages = new ArrayList<String>(
            Arrays.asList("com.postmates.android.courier", "com.ubercab.driver")); //  ""

    /////////////// FIREBASE TABLES
    public static String ROOM_VEHICLES = "vehicles";
    public static String ROOM_SERVICE = "services";
    public static String ROOM_NOTIFICATIONS = "notifications";
    public static String ROOM_PAYMENTS = "payments";
    public static String ROOM_CONNECTIONS = "connections";
    public static String ROOM_VICTOR_CONNECTION = "vconnections";
    /////////////// FLAGS SCREEN APPS
    public static int FLAG_LOGIN = 1;
    public static int FLAG_MAIN = 2;
    public static int FLAG_REQUEST = 3;
    public static int FLAG_SERVICE = 4;
    public static int FLAG_VEHICLES = 5;
    public static int FLAG_PAYMENTS_TRIPS = 6;
    public static int FLAG_ACEPTED_REQUEST = 7;
    public static int FLAG_ONWAY = 8;
    public static int FLAG_ARRIVED = 9;
    public static int FLAG_STARTED = 10;
    public static int FLAG_FINISHED = 11;
    public static int FLAG_EMPTY = 100;
    /////// APPS CONECTED AND LOGGED
    public static ArrayList<String> appsConnected = new ArrayList<String>();
    public static ArrayList<String> appsDisconected = new ArrayList<String>();

    /////// KEY FIELDS FIREBASE
    public static String REQUEST_KEY_ADDRESS = "address";
    public static String REQUEST_KEY_CLIENT = "client";
    public static String REQUEST_KEY_TIME = "time";
    public static String REQUEST_KEY_VEHICLES_TYPE = "type";
    public static String PAYMENT_TRIPS_DATE = "date";
    public static String PAYMENT_TRIPS_COST = "cost";
    public static String REQUEST_KEY_FILTER = "filter";
    public static String REQUEST_KEY_RATING = "rating";
    public static String REQUEST_KEY_DISTANCE = "distance";
    public static String REQUEST_KEY_COST = "cost";
    public static Map<String, String> lastStateConnection = new HashMap<String, String>();

    /////////////////////////////////// TIME TO CONNECT ///////////////////////////////////

    public static Map<String, Integer> timeConnectionData = new HashMap<String, Integer>() {{
        put(VAppData.appPackages.get(0), 600);
        put(VAppData.appPackages.get(1), 1800);
        put(VAppData.appPackages.get(2), 3400);
    }};

    public static Map<String, Integer> timeDisConnectionData = new HashMap<String, Integer>() {{
        put(VAppData.appPackages.get(0), 600);
        put(VAppData.appPackages.get(1), 1500);
        put(VAppData.appPackages.get(2), 1200);
    }};


    /////////////////////////////////// DATA AND CONNECTION ACTION BUTTON ///////////////////////////////////

    public static Map<String, String> connectButtonData = new HashMap<String, String>() {{
        put(VAppData.appPackages.get(2), "br.com.easytaxista:id/busy_button");
        put(VAppData.appPackages.get(1), "com.ubercab.driver:id/ub__app_state_switch");
        put(VAppData.appPackages.get(0), "com.postmates.android.courier:id/stop_counter_container");
    }};


    public static Map<String, String> connectButtonTextData = new HashMap<String, String>() {{
        put(VAppData.appPackages.get(2), "br.com.easytaxista:id/busy_button_switch");
        put(VAppData.appPackages.get(1), "com.ubercab.driver:id/ub__app_state_switch");
        put(VAppData.appPackages.get(0), "com.postmates.android.courier:id/stop_counter");
    }};

    public static Map<String, List<String>> connectedData = new HashMap<String, List<String>>() {{
        put(VAppData.appPackages.get(2), Arrays.asList("SÍ", "ON"));
        put(VAppData.appPackages.get(1), Arrays.asList("ACTIVADO", "ON"));
        put(VAppData.appPackages.get(0), Arrays.asList("DESCONECTARSE"));
    }};

    public static Map<String, List<String>> disconectedData = new HashMap<String, List<String>>() {{
        put(VAppData.appPackages.get(2), Arrays.asList("NO", "OFF"));
        put(VAppData.appPackages.get(1), Arrays.asList("DESACTIVADO", "OFF"));
        put(VAppData.appPackages.get(0), Arrays.asList("CONECTARSE"));
    }};

    //////////////////////////////////// ACEPT REQUEST AND DATA REQUEST ///////////////////////////////////

    public static Map<String, String> aceptRequestData = new HashMap<String, String>() {{
        put(VAppData.appPackages.get(2), "br.com.easytaxista:id/accept_button");
        put(VAppData.appPackages.get(1), "com.ubercab.driver:id/ub__dispatch_primary_touch_area");
        put(VAppData.appPackages.get(0), "com.postmates.android.courier:id/bottom_button1");
    }};


    public static Map<String, Map<String, List<String>>> onRequestData = new HashMap<String, Map<String, List<String>>>() {{
        put(VAppData.appPackages.get(2), new HashMap<String, List<String>>() {{
            put("address", Arrays.asList("br.com.easytaxista:id/pickup_address"));
            put("time", Arrays.asList("vrex","(\\d?)(\\d)(\\s+)(m)(i)(n)"));
            put("distance", Arrays.asList("vrex", "(\\d)(.?)(\\d?)(\\s+)(k?)(m?)(\\s+)"));
        }});
        put(VAppData.appPackages.get(1), new HashMap<String, List<String>>() {{
            put("time", Arrays.asList(" min"));
            put("distance", Arrays.asList(" km"));
            put("rating", Arrays.asList("5.", "4.", "3.", "2.", "1."));
            put("filter", Arrays.asList("uberx", "pool", "black", "suv", "xl", "eat"));
        }});
        put(VAppData.appPackages.get(0), new HashMap<String, List<String>>() {{
            put("address", Arrays.asList("com.postmates.android.courier:id/address"));
            put("client", Arrays.asList("com.postmates.android.courier:id/title"));
        }});
    }};


    public static Map<String, Map<String, List<String>>> onWayData = new HashMap<String, Map<String, List<String>>>() {{
        put(VAppData.appPackages.get(2), new HashMap<String, List<String>>() {{
            put("client", Arrays.asList("br.com.easytaxista:id/tv_passenger_name"));
            put("address", Arrays.asList("br.com.easytaxista:id/tv_complete_address"));
        }});
        put(VAppData.appPackages.get(1), new HashMap<String, List<String>>() {{
            put("client", Arrays.asList("com.ubercab.driver:id/ub__header_textview_name"));
            put("address", Arrays.asList("com.ubercab.driver:id/ub__nav_address_view_poi_text", "com.ubercab.driver:id/ub__nav_address_view_address_text"));
        }});
        put(VAppData.appPackages.get(0), new HashMap<String, List<String>>() {{
            put("address", Arrays.asList("com.postmates.android.courier:id/address"));
            put("client", Arrays.asList("com.postmates.android.courier:id/title"));
        }});
    }};


    public static Map<String, Map<String, List<String>>> onArrivedData = new HashMap<String, Map<String, List<String>>>() {{
        put(VAppData.appPackages.get(2), new HashMap<String, List<String>>() {{
            put("address", Arrays.asList("br.com.easytaxista:id/pickup_address"));
            put("time", Arrays.asList("br.com.easytaxista:id/eta"));
        }});
        put(VAppData.appPackages.get(1), new HashMap<String, List<String>>() {{
            put("client", Arrays.asList("com.ubercab.driver:id/ub__tripsmanager_textview_client"));
        }});
        put(VAppData.appPackages.get(0), new HashMap<String, List<String>>() {{
            put("address", Arrays.asList("com.postmates.android.courier:id/address"));
            put("client", Arrays.asList("com.postmates.android.courier:id/title"));
        }});
    }};


    public static Map<String, Map<String, List<String>>> onStartedData = new HashMap<String, Map<String, List<String>>>() {{
        put(VAppData.appPackages.get(2), new HashMap<String, List<String>>() {{
            put("client", Arrays.asList("br.com.easytaxista:id/tv_passenger_name"));
            put("address", Arrays.asList("br.com.easytaxista:id/tv_address_destination"));
        }});
        put(VAppData.appPackages.get(1), new HashMap<String, List<String>>() {{
            put("client", Arrays.asList("com.ubercab.driver:id/ub__header_textview_name"));
            put("address", Arrays.asList("com.ubercab.driver:id/ub__nav_address_view_poi_text", "com.ubercab.driver:id/ub__nav_address_view_address_text"));

        }});
        put(VAppData.appPackages.get(0), new HashMap<String, List<String>>() {{
            put("address", Arrays.asList("com.postmates.android.courier:id/address"));
            put("client", Arrays.asList("com.postmates.android.courier:id/title"));
        }});
    }};


    public static Map<String, Map<String, List<String>>> onFinishedData = new HashMap<String, Map<String, List<String>>>() {{
        put(VAppData.appPackages.get(2), new HashMap<String, List<String>>() {{
            put("cost", Arrays.asList("br.com.easytaxista:id/txtFinalValue"));
        }});
        put(VAppData.appPackages.get(1), new HashMap<String, List<String>>() {{
            put("client", Arrays.asList("com.ubercab.driver:id/ub__header_textview_name"));
        }});
        put(VAppData.appPackages.get(0), new HashMap<String, List<String>>() {{
            put("address", Arrays.asList("com.postmates.android.courier:id/address"));
            put("client", Arrays.asList("com.postmates.android.courier:id/title"));
        }});
    }};

    /////////////////////////////////// DATA SCREENS TO SEND ///////////////////////////////////
    public static Object[][] loginRegisterData = {
            {VAppData.appPackages.get(2), new ArrayList<String>(
                    Arrays.asList("br.com.easytaxista:id/btLogin", "Ingresa un correo para enviarte una nueva contraseña", "CREAR CUENTA", "CREATE ACCOUNT", "Documents"))},

            {VAppData.appPackages.get(1), new ArrayList<String>(
                    Arrays.asList("com.ubercab.driver:id/ub__signedout_button_signin", "com.ubercab.driver:id/ub__signedout_button_register",
                            "com.ubercab.driver:id/ub__rider_app_redirect", "com.ubercab.driver:id/ub__partner_signup_webview_webview", "com.ubercab.driver:id/ub__signin2_button_signin",
                            "com.ubercab.driver:id/ub__signin2_textview_forgot_password", "Iniciar sesión", "¿Buscas la app de usuario?", "Correo electrónico", "Contraseña", "¿Olvidaste tu contraseña?"))},

            {VAppData.appPackages.get(0), new ArrayList<String>(
                    Arrays.asList("com.postmates.android.courier:id/forgot_password", "com.postmates.android.courier:id/version_code_and_name", "com.postmates.android.courier:id/signin_goto_signup"
                            , "com.postmates.android.courier:id/forgot_password_email", "com.postmates.android.courier:id/signup_title", "com.postmates.android.courier:id/signup_button",
                            "com.postmates.android.courier:id/signup_agreement_and_policy", "com.postmates.android.courier:id/signup_goto_login"
                    ))}
    };


    public static Object[][] mainData = {
            {VAppData.appPackages.get(2), new ArrayList<String>(
                    Arrays.asList("br.com.easytaxista:id/busy_button_switch"))},
            {VAppData.appPackages.get(1), new ArrayList<String>(
                    Arrays.asList("com.ubercab.driver:id/ub__app_state_switch"))},
            {VAppData.appPackages.get(0), new ArrayList<String>(
                    Arrays.asList("com.postmates.android.courier:id/stop_counter"
                    ))}
    };


    public static Object[][] onRequest = {
            {VAppData.appPackages.get(2), new ArrayList<String>(
                    Arrays.asList("br.com.easytaxista:id/accept_button", "br.com.easytaxista:id/refuse_button", "br.com.easytaxista:id/eta"))},

            {VAppData.appPackages.get(1), new ArrayList<String>(
                    Arrays.asList("com.ubercab.driver:id/ub__dispatch_framework_pulse_view"))},

            {VAppData.appPackages.get(0), new ArrayList<String>(
                    Arrays.asList("com.postmates.android.courier:id/bottom_button", "com.postmates.android.courier:id/skip_button"
                    ))}
    };


    public static Object[][] onService = {
            {VAppData.appPackages.get(2), new ArrayList<String>(
                    //Driver 6.3.5 , Olvidaste tu contraseña?, Inicia sesión
                    Arrays.asList("br.com.easytaxista:id/btn_start_trip", "LLEGUÉ", "br.com.easytaxista:id/boarded", "br.com.easytaxista:id/label_waiting_time", "br.com.easytaxista:id/bt_charge_ride", "br.com.easytaxista:id/tv_address_destination", "br.com.easytaxista:id/txtFinalValue"))},
            {VAppData.appPackages.get(1), new ArrayList<String>(
                        Arrays.asList("CONFIRMAR LLEGADA", "ub__tripsmanager_textview_task", "INICIAR VIAJE", "DESTINO", "COMPLETAR CALIFICACIÓN"))},
            {VAppData.appPackages.get(0), new ArrayList<String>(
                    Arrays.asList(""
                    ))}
    };


    public static Object[][] onWay = {
            {VAppData.appPackages.get(2), new ArrayList<String>(
                    Arrays.asList("br.com.easytaxista:id/btn_start_trip", "LLEGUÉ"))},
            {VAppData.appPackages.get(1), new ArrayList<String>(
                    Arrays.asList("INCIO DE VIAJE","CONFIRMAR LLEGADA","RECOGER"))},
            {VAppData.appPackages.get(0), new ArrayList<String>(
                    Arrays.asList("com.postmates.android.courier:id/bottomsheet_overlay", "com.postmates.android.courier:id/alert_body_text"
                    ))}
    };


    public static Object[][] onArrived = {
            {VAppData.appPackages.get(2), new ArrayList<String>(
                    Arrays.asList("br.com.easytaxista:id/boarded", "br.com.easytaxista:id/label_waiting_time"))},
            {VAppData.appPackages.get(1), new ArrayList<String>(
                    Arrays.asList("INICIAR VIAJE"))},
            {VAppData.appPackages.get(0), new ArrayList<String>(
                    Arrays.asList("com.postmates.android.courier:id/bottomsheet_overlay", "com.postmates.android.courier:id/alert_body_text"
                    ))}
    };


    public static Object[][] onStarted = {
            {VAppData.appPackages.get(2), new ArrayList<String>(
                    Arrays.asList("br.com.easytaxista:id/bt_charge_ride", "br.com.easytaxista:id/tv_address_destination"))},
            {VAppData.appPackages.get(1), new ArrayList<String>(
                    Arrays.asList("DESTINO"))},
            {VAppData.appPackages.get(0), new ArrayList<String>(
                    Arrays.asList("com.postmates.android.courier:id/bottomsheet_overlay", "com.postmates.android.courier:id/alert_body_text"
                    ))}
    };


    public static Object[][] onFinished = {
            {VAppData.appPackages.get(2), new ArrayList<String>(
                    Arrays.asList("br.com.easytaxista:id/txtFinalValue"))},
            {VAppData.appPackages.get(1), new ArrayList<String>(
                    Arrays.asList("COMPLETAR CALIFICACIÓN","FINALIZAR VALORACIÓN"))},
            {VAppData.appPackages.get(0), new ArrayList<String>(
                    Arrays.asList("com.postmates.android.courier:id/bottomsheet_overlay", "com.postmates.android.courier:id/alert_body_text"
                    ))}
    };

    /////////////////////////////////// OTHER DATA SCREENS TO SEND ///////////////////////////////////


    public static Object[][] vehiclesData = {
            {VAppData.appPackages.get(2), new ArrayList<String>(
                    Arrays.asList(""))},

            {VAppData.appPackages.get(1), new ArrayList<String>(
                    Arrays.asList("Información del vehículo"))},

            {VAppData.appPackages.get(0), new ArrayList<String>(
                    Arrays.asList("com.postmates.android.courier:id/vehicle_selection_title"
                    ))}
    };


    public static Object[][] paymentTripsData = {
            {VAppData.appPackages.get(2), new ArrayList<String>(

                    Arrays.asList("."))},

            {VAppData.appPackages.get(1), new ArrayList<String>(
                    Arrays.asList("Historial de viajes"))},

            {VAppData.appPackages.get(0), new ArrayList<String>(
                    Arrays.asList("Ganancias de la entrega"
                    ))}
    };



}

package com.blako.mensajero.accesibility;

import android.content.Context;
import android.location.Location;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by franciscotrinidad on 08/12/17.
 */

public class VAccesibilityActions {


    public static synchronized boolean getIsOnScreen(String packageName, ArrayList<AccessibilityNodeInfo> elementsScreen, int type) {
        if (elementsScreen == null || elementsScreen.size() == 0)
            return false;
        int totalElementsMS = 0;
        int count = 0;
        Object[][] caseData = null;
        if (type == VAppData.FLAG_LOGIN) {
            caseData = VAppData.loginRegisterData;
        } else if (type == VAppData.FLAG_MAIN) {
            caseData = VAppData.mainData;
        } else if (type == VAppData.FLAG_REQUEST) {
            caseData = VAppData.onRequest;
        } else if (type == VAppData.FLAG_SERVICE) {
            caseData = VAppData.onService;
        } else if (type == VAppData.FLAG_VEHICLES) {
            caseData = VAppData.vehiclesData;
        } else if (type == VAppData.FLAG_PAYMENTS_TRIPS) {
            caseData = VAppData.paymentTripsData;
        } else if (type == VAppData.FLAG_ONWAY) {
            caseData = VAppData.onWay;
        } else if (type == VAppData.FLAG_ARRIVED) {
            caseData = VAppData.onArrived;
        } else if (type == VAppData.FLAG_STARTED) {
            caseData = VAppData.onStarted;
        } else if (type == VAppData.FLAG_FINISHED) {
            caseData = VAppData.onFinished;
        }

        if (caseData == null)
            return false;
        for (Object[] data : caseData) {
            String packageNameInstalled = (String) data[0];
            if (packageName.equals(packageNameInstalled)) {
                ArrayList<String> elementsMainScreen = (ArrayList<String>) data[1];
                totalElementsMS = elementsMainScreen.size();
                for (AccessibilityNodeInfo element : elementsScreen) {
                    if (element != null) {
                        element.refresh();
                        if ((element.getViewIdResourceName() != null && elementsMainScreen.contains(element.getViewIdResourceName())) || (element.getText() != null && (elementsMainScreen.contains(element.getText().toString().toUpperCase()) || elementsMainScreen.contains(element.getText().toString().toLowerCase())))) {
                            count++;
                        } else {
                            for (String elementMainScreen : elementsMainScreen) {
                                if (element.getText() != null && element.getText().toString().toUpperCase().contains(elementMainScreen.toUpperCase())) {
                                    count++;
                                }
                            }
                        }
                    }
                }
            }
        }

        double percent = 0;
        if (count != 0)
            percent = 100.0 * count / totalElementsMS;

        if (type == VAppData.FLAG_REQUEST) {
            return percent >= 70;
        } else return percent >= 20;
    }


    /////////////////////////// REQUEST
    static Map<String, Object> getRequest(final Context context, ArrayList<AccessibilityNodeInfo> textViewNodes, String packageName, Location location, boolean acepted) {
        String client = null, time = null, address = null, rating = null, filter = null, distance = null;
        Map<String, List<String>> data = VAppData.onRequestData.get(packageName);
        List<String> addressKeys = data.get(VAppData.REQUEST_KEY_ADDRESS);
        List<String> clientKeys = data.get(VAppData.REQUEST_KEY_CLIENT);
        List<String> timeKeys = data.get(VAppData.REQUEST_KEY_TIME);
        List<String> ratingKeys = data.get(VAppData.REQUEST_KEY_RATING);
        List<String> filterKeys = data.get(VAppData.REQUEST_KEY_FILTER);
        List<String> distanceKeys = data.get(VAppData.REQUEST_KEY_DISTANCE);
        boolean validRequest = false;
        if (textViewNodes == null)
            return null;

        try {
            for (AccessibilityNodeInfo info : textViewNodes) {
                if (info.getText() != null) {
                    if (addressKeys != null)
                        for (String addressKey : addressKeys) {
                            if (info.getText().toString().toLowerCase().contains(addressKey.toLowerCase()) || (info.getViewIdResourceName() != null && info.getViewIdResourceName().equals(addressKey))) {
                                address = info.getText().toString();
                                validRequest = true;
                            }
                        }
                    if (clientKeys != null)
                        for (String clientKey : clientKeys) {
                            if (info.getText().toString().toLowerCase().contains(clientKey.toLowerCase()) || (info.getViewIdResourceName() != null && info.getViewIdResourceName().equals(clientKey))) {
                                client = info.getText().toString();
                                validRequest = true;
                            }
                        }


                    if (ratingKeys != null)
                        for (String ratingKey : ratingKeys) {
                            if (info.getText().toString().toLowerCase().contains(ratingKey.toLowerCase()) || (info.getViewIdResourceName() != null && info.getViewIdResourceName().equals(ratingKey))) {
                                rating = info.getText().toString();
                                validRequest = true;
                            }
                        }

                    if (filterKeys != null)
                        for (String filterKey : filterKeys) {
                            if (info.getText().toString().toLowerCase().contains(filterKey.toLowerCase()) || (info.getViewIdResourceName() != null && info.getViewIdResourceName().equals(filterKey))) {
                                filter = info.getText().toString();
                                validRequest = true;
                            }
                        }


                    if (timeKeys != null) {
                        if (timeKeys.size() == 2 && timeKeys.get(0).contains("vrex")) {
                            String timeValue = VAccesibilityActions.getValue(info.getText().toString(), timeKeys.get(1));
                            if (timeValue != null && timeValue.length() != 0) {
                                time = timeValue.replace(" ", "");
                                validRequest = true;
                            }
                        } else {
                            for (String timeKey : timeKeys) {
                                if (info.getText().toString().toLowerCase().contains(timeKey.toLowerCase()) || (info.getViewIdResourceName() != null && info.getViewIdResourceName().equals(timeKey))) {
                                    time = info.getText().toString();
                                    validRequest = true;
                                }
                            }
                        }
                    }

                    if (distanceKeys != null) {
                        if (distanceKeys.size() == 2 && distanceKeys.get(0).contains("vrex")) {
                            String distanceValue = VAccesibilityActions.getValue(info.getText().toString(), distanceKeys.get(1));
                            if (distanceValue != null && distanceValue.length() != 0) {
                                distance = distanceValue.replace(" ", "");
                                validRequest = true;
                            }
                        } else {
                            for (String distanceKey : distanceKeys) {
                                if (info.getText().toString().toLowerCase().contains(distanceKey.toLowerCase()) || (info.getViewIdResourceName() != null && info.getViewIdResourceName().equals(distanceKey))) {
                                    distance = info.getText().toString();
                                    validRequest = true;
                                }
                            }
                        }
                    }
                }

            }
        } catch (Exception e) {

        }


        if (validRequest) {
            Map<String, Object> map = new HashMap<>();
            try {


               // map.put("ts", ServerValue.TIMESTAMP);
                map.put("acepted", acepted);
                map.put("app", packageName);
                //map.put("idUser", VDataManager.getIdUser(context));
                //map.put("userName", VDataManager.getNameUser(context));

                if (client != null)
                    map.put("client", client);
                if (time != null)
                    map.put("time", time.toLowerCase());
                if (address != null)
                    map.put("address", address);
                if (rating != null)
                    map.put("rating", rating);
                if (filter != null)
                    map.put("filter", filter.toLowerCase());
                if (distance != null)
                    map.put("distance", distance.toLowerCase());

                if (location != null) {
                    map.put("latitude", location.getLatitude());
                    map.put("longitude", location.getLongitude());
                }


            } catch (Exception e) {

            }
            return map;
        } else {
            return null;
        }


    }


    private static String getValue(String text, String patther) {
        Pattern p = Pattern.compile(patther, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(text);
        boolean find = false;
        String match = "";
        while (m.find()) {
            match += m.group();
            find = true;
        }
        if (!find)
            return null;

        return match;
    }


    static Map<String, Object> getRequestStep(ArrayList<AccessibilityNodeInfo> textViewNodes, String packageName, Location location, Map<String, List<String>> _data) {
        String client = null, time = null, address = null, cost = null;
        Map<String, List<String>> data = _data;
        List<String> addressKeys = data.get(VAppData.REQUEST_KEY_ADDRESS);
        List<String> clientKeys = data.get(VAppData.REQUEST_KEY_CLIENT);
        List<String> timeKeys = data.get(VAppData.REQUEST_KEY_TIME);
        List<String> costKeys = data.get(VAppData.REQUEST_KEY_COST);
        if (textViewNodes == null)
            return null;

        for (AccessibilityNodeInfo info : textViewNodes) {
            if (info.getText() != null) {
                if (addressKeys != null)
                    for (String addressKey : addressKeys) {
                        if (info.getText().toString().contains(addressKey) || (info.getViewIdResourceName() != null && info.getViewIdResourceName().equals(addressKey))) {

                            if (address == null)
                                address = info.getText().toString();
                            else {
                                if (!info.getText().toString().equals(address)) {
                                    address += " " + info.getText().toString();
                                }
                            }
                        }
                    }
                if (clientKeys != null)
                    for (String clientKey : clientKeys) {
                        if (info.getText().toString().contains(clientKey) || (info.getViewIdResourceName() != null && info.getViewIdResourceName().equals(clientKey))) {
                            client = info.getText().toString();
                        }
                    }
                if (timeKeys != null)
                    for (String timeKey : timeKeys) {
                        if (info.getText().toString().contains(timeKey) || (info.getViewIdResourceName() != null && info.getViewIdResourceName().equals(timeKey))) {
                            time = info.getText().toString();
                        }
                    }
                if (costKeys != null)
                    for (String costKey : costKeys) {
                        if (info.getText().toString().contains(costKey) || (info.getViewIdResourceName() != null && info.getViewIdResourceName().equals(costKey))) {
                            cost = info.getText().toString();
                        }
                    }
            }

        }

        Map<String, Object> map = new HashMap<>();
        if (client != null)
            map.put("client", client);
        if (time != null)
            map.put("time", time);
        if (address != null)
            map.put("address", address);
        if (cost != null)
            map.put("cost", cost);

        if (location != null) {
            map.put("latitude", location.getLatitude());
            map.put("longitude", location.getLongitude());
        }

       // map.put("ts", ServerValue.TIMESTAMP);
        map.put("app", packageName);
       // map.put("idUser", VDataManager.getIdUser(context));
        //map.put("userName", VDataManager.getNameUser(context));
        return map;
    }


    /////////////////////////// VEHICLES






}
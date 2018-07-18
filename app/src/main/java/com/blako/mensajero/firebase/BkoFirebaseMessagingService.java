package com.blako.mensajero.firebase;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.blako.mensajero.Adapters.BkoTripsAdapter;
import com.blako.mensajero.BkoDataMaganer;
import com.blako.mensajero.Constants;
import com.blako.mensajero.Dao.BkoUserDao;
import com.blako.mensajero.R;
import com.blako.mensajero.Utils.BkoUtilities;
import com.blako.mensajero.Utils.HttpRequest;
import com.blako.mensajero.VO.BkoChildTripVO;
import com.blako.mensajero.VO.BkoOffer;
import com.blako.mensajero.VO.BkoPushRequest;
import com.blako.mensajero.VO.BkoRecoverStatusVO;
import com.blako.mensajero.VO.BkoTripVO;
import com.blako.mensajero.VO.BkoTrips;
import com.blako.mensajero.VO.BkoUser;
import com.blako.mensajero.Views.BkoLoginActivity;
import com.blako.mensajero.Views.BkoMainActivity;
import com.blako.mensajero.Views.BkoOffersByDateActivity;
import com.blako.mensajero.Views.BkoRequestActivity;
import com.blako.mensajero.Views.BkoTripDetailActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Map;

public class BkoFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";


    private boolean sound;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sound = false;
        synchronized (this) {
            String data = "";//data.getString("Notice");
            String body = "";
            String title="";

            for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key.equals("notification"))
                    data = value;

            }

            if (data == null || data.length() == 0)
                return;

            boolean display = false;

            try {
                BkoUser worker = BkoUserDao.Consultar(this);

                if (worker == null) {
                    return;
                }



                JSONObject contentData = new JSONObject(data);

                if(contentData.has("display"))
                    display = contentData.getBoolean("display");

                if(contentData.has("title"))
                    title = contentData.getString("title");

                if(contentData.has("body"))
                    body = contentData.getString("body");

                JSONObject jsonPushNotification = null;

                if(contentData.has("message"))
                    jsonPushNotification = contentData.getJSONObject("message");


                if(jsonPushNotification==null)
                    return;


                String pushNotificationType = "";
                if (jsonPushNotification.has("typepush")) {
                    Gson gson = new Gson();
                    pushNotificationType = jsonPushNotification.getString("typepush");
                    if (pushNotificationType != null) {

                        if (pushNotificationType.equals("requestworker")) {
                            if (!worker.isAvailable())
                                return;


                        } else if (pushNotificationType.equals("cancelorder")) {
                            if (!worker.isAvailable())
                                return;

                        } else if (pushNotificationType.equals("logout")) {



                                Intent intent = new Intent(this, BkoLoginActivity.class);
                                BkoUserDao.Eliminar(this);
                                BkoDataMaganer.setCurrentVehicle(null, this);
                                BkoDataMaganer.setStatusService(Constants.SERVICE_STATUS_FREE, this);
                                BkoDataMaganer.setStatusRequest(null, this);
                                BkoDataMaganer.setHasOtherSessionActive(true, this);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);





                        } else if (pushNotificationType.equals("desahogo")) {

                            return;

                        } else if (pushNotificationType.equals("variablesdb")) {

                        } else if (pushNotificationType.equals("consulttrips")) {
                            if (!worker.isAvailable())
                                return;

                            if (BkoDataMaganer.getStatusService(this) == Constants.SERVICE_STATUS_FREE && !BkoDataMaganer.getOnDemand(this)) {
                                return;
                            }

                            try {

                                sound = true;
                                Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                vib.vibrate(8000);

                                if(BkoDataMaganer.getOnDemand(this)){
                                    onDisconectedOnDemand();
                                }

                                else
                                {
                                //notificationMessage = getString(R.string.blako_push_nuevo_destino);
                                    BkoTripDetailActivity.confirmed = true;
                                Intent intent = new Intent(BkoFirebaseMessagingService.this, BkoMainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                }
                                PowerManager.WakeLock screenOn = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "example");
                                screenOn.acquire();


                            } catch (Exception e) {
                                e.printStackTrace();


                            }

                        } else if (pushNotificationType.equals("updateQueuedTasks")) {
                            if (!worker.isAvailable())
                                return;


                            if (BkoDataMaganer.getStatusService(this) != Constants.SERVICE_STATUS_FREE && BkoDataMaganer.getStatusService(this) != Constants.SERVICE_STATUS_WITH_OFFER_CHECKIN_FINISH)
                                return;

                            BkoTripDetailActivity.confirmed = true;
                            sound = true;
                            Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            vib.vibrate(6000);
                            BkoDataMaganer.setStatusService(Constants.SERVICE_STATUS_WITH_QUEUEDTASKS, BkoFirebaseMessagingService.this);
                            BkoMainActivity.updateMyActivity(BkoFirebaseMessagingService.this, jsonPushNotification.toString(), "updateQueuedTasks");
                            Intent intent = new Intent(BkoFirebaseMessagingService.this, BkoMainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                            //notificationMessage = getString(R.string.blako_push_nuevo_destino);

                            PowerManager.WakeLock screenOn = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "example");
                            screenOn.acquire();

                        } else if (pushNotificationType.equals("loadAnnouncement")) {

                            if (BkoDataMaganer.getCurrentVehicle(this) == null )
                                return;

                            if (!BkoDataMaganer.getOnDemand(this)){
                                BkoMainActivity.updateMyActivity(this, jsonPushNotification.toString(), "loadAnnouncement");
                                BkoOffersByDateActivity.updateMyActivity(this, jsonPushNotification.toString(), "loadAnnouncement");
                            }

                        } else if (pushNotificationType.equals("announcementimposed")) {

                            if (BkoDataMaganer.getCurrentVehicle(this) == null )
                                return;
                            BkoMainActivity.updateMyActivity(this, jsonPushNotification.toString(), "loadAnnouncement");
                            BkoTripDetailActivity.confirmed = true;
                            Intent intent = new Intent(this, BkoMainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            //notificationMessage = getString(R.string.blako_push_nuevo_oferta_impuesta);
                        } else if (pushNotificationType.equals("OnWay")) {

                            if (!worker.isAvailable())
                                return;
                            //notificationMessage = getString(R.string.blako_offers_en_camino);

                        } else if (pushNotificationType.equals("finishOffer")) {

                            if (!worker.isAvailable())
                                return;

                            BkoOffer.BkoAnnoucement currentOffer = BkoDataMaganer.getCurrentOffer(this);

                            if (currentOffer == null)
                                return;

                            String currentAnnouncementId = currentOffer.getBko_announcement_id();

                            String announcementId = jsonPushNotification.getString("announcementId");

                            if (!currentAnnouncementId.equals(announcementId))
                                return;
                            BkoDataMaganer.setStatusService(Constants.SERVICE_STATUS_FREE, this);
                            Intent intent = new Intent(this, BkoMainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            //notificationMessage = getString(R.string.blako_push_checkout);

                        } else if (pushNotificationType.equals("finishOfferCheckin")) {

                            if (!worker.isAvailable())
                                return;


                            String checkin = jsonPushNotification.getString("timeStarCheckin");
                            String announcementId = jsonPushNotification.getString("announcementId");
                            if (checkin != null) {
                                BkoOffer.BkoAnnoucement offer = BkoDataMaganer.getCurrentOffer(this);
                                if (offer != null && offer.getBko_announcement_id().equals(announcementId)) {
                                    offer.setBko_announcementworker_checkin(checkin);
                                    BkoDataMaganer.setStatusService(Constants.SERVICE_STATUS_FREE, this);
                                    BkoDataMaganer.setCurrentOffer(null, this);
                                }
                            }

                            Intent intent = new Intent(this, BkoMainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            // notificationMessage = getString(R.string.blako_push_checkout);

                        } else if (pushNotificationType.equals("updateAnnouncement")) {

                            if (!worker.isAvailable())
                                return;

                            BkoOffer.BkoAnnoucement currentOffer = BkoDataMaganer.getCurrentOffer(this);

                            if (currentOffer == null)
                                return;

                            String currentAnnouncementId = currentOffer.getBko_announcement_id();

                            String timeStart = jsonPushNotification.getString("timestartmod");
                            String timeFinish = jsonPushNotification.getString("timefinishmod");

                            currentOffer.setBko_announcement_datetimestart(BkoUtilities.JsonDate(timeStart, "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm:ss"));
                            currentOffer.setBko_announcement_datetimefinish(BkoUtilities.JsonDate(timeFinish, "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm:ss"));


                            BkoDataMaganer.setCurrentOffer(currentOffer, this);
                            Intent intent = new Intent(this, BkoMainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            //notificationMessage = getString(R.string.blako_push_ofer_actualizada);

                        } else if (pushNotificationType.equals("newsWorkersByCity")) {
                            //notificationMessage = jsonPushNotification.getString("messageWorkersByCity");
                        } else if (pushNotificationType.equals("OnWay")) {

                            if (!worker.isAvailable())
                                return;
                            //notificationMessage = getString(R.string.blako_offers_en_camino);

                        } else if (pushNotificationType.equals("offerCancelMoney")) {
                            //notificationMessage = jsonPushNotification.getString("notificationMessage");

                        }

                    }


                } else if (jsonPushNotification.has("cancel")) {
                }

                if(display)
                    sendNotification(body, sound, title);

            } catch (Exception e) {
                return;

            }


        }
    }
    // [END receive_message]


    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {

        /*
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code ,intent,
                PendingIntent.FLAG_ONE_SHOT);
 /*
        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.)
                        .setContentTitle("FCM Message")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
*/
 /*
        notificationManager.notify(0 /* ID of notification ,// notificationBuilder.build());
*/
    }


    public boolean isValidJSON(String toTestStr) {
        try {
            new JSONObject(toTestStr);
        } catch (JSONException jsExcp) {
            try {
                new JSONArray(toTestStr);
            } catch (JSONException jsExcp1) {
                return false;
            }
        }
        return true;
    }

    private void sendNotification(String message, boolean sound, String title) {

        Intent notificationIntent = new Intent(getApplicationContext(), BkoMainActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        String titleNotification = "Blako";

        if (title != null)
            titleNotification = title;

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        notificationIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.motorcycle)
                .setContentTitle(titleNotification)
                .setContentText(message)
                .setContentIntent(resultPendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true);
        if (sound) {
            defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sindelantal);

        }

        notificationBuilder.setSound(defaultSoundUri);
        notificationBuilder.setLights(0xff152949, 1000, 2000);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = notificationBuilder.build();
        notification.flags |= Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_AUTO_CANCEL;
        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        notificationManager.notify(m  /*     ID of notification */, notification);


    }


    private void onDisconectedOnDemand() {

                try {
                     String statusResponse;
                    Gson gson = new Gson();
                     BkoTrips tripsRespone = null;
                     BkoPushRequest statusRequest;
                    BkoRecoverStatusVO recoverStatusVO;
                    statusRequest = BkoDataMaganer.getCurrentStatusRequest(this);
                    BkoUser user = BkoUserDao.Consultar(this);
                    if (user!=null){
                        BkoDataMaganer.setWorkerId(this,user.getWorkerId());
                    }
                    statusResponse = HttpRequest
                            .get(Constants.GET_TRIPS_ACTIVE(this) + "workerId=" + user.getWorkerId())
                            .connectTimeout(4000).readTimeout(4000).body();

                    if (statusResponse != null) {
                        recoverStatusVO = gson.fromJson(statusResponse, BkoRecoverStatusVO.class);
                        if (recoverStatusVO.isResponse()) {
                            BkoLoginActivity.setData(null, recoverStatusVO,this);
                            statusRequest = BkoDataMaganer.getCurrentStatusRequest(this);
                        }
                    }


                    if (statusRequest != null) {
                        String tripsReponse = HttpRequest
                                .get(Constants.GET_GO_TRIPS(this) + "oId=" + statusRequest.getOid() + "&workerId=" + user.getWorkerId())
                                .connectTimeout(5000).readTimeout(5000).body();
                        if (tripsReponse != null) {
                            tripsRespone = gson.fromJson(tripsReponse, BkoTrips.class);
                        }
                    }

                    if (tripsRespone != null) {
                        try {
                            if ((tripsRespone.getTrips() != null && tripsRespone.getTrips().size() != 0)) {
                                if (BkoTripsAdapter.mp != null && BkoTripsAdapter.mp.isPlaying())
                                    BkoTripsAdapter.mp.stop();
                                boolean valid = false;
                                for(BkoTripVO tripPArent: tripsRespone.getTrips()){
                                    BkoChildTripVO trip = tripPArent.getOrigen().get(0);
                                    BkoChildTripVO tripDelivery = tripPArent.getDestino().get(0);




                                    if (trip.getBko_orders_trips_completeddatetime() != null && trip.getBko_orders_trips_completeddatetime().length() != 0) {

                                    } else {
                                        if (tripDelivery.getBko_requeststatus_id() != null && tripDelivery.getBko_requeststatus_id().equals("2")) {
                                        } else {
                                            valid = true;
                                            if(!BkoRequestActivity.onRequest ){
                                                BkoRequestActivity.tripP =  tripPArent;
                                                BkoRequestActivity.trip =  trip;
                                                BkoRequestActivity.tripDelivery =  tripDelivery;


                                                break;
                                            }

                                        }
                                    }
                                }

                                if(valid){
                                    if (BkoTripsAdapter.mp != null && BkoTripsAdapter.mp.isPlaying())
                                        BkoTripsAdapter.mp.stop();

                                    if(!BkoRequestActivity.onRequest){
                                        BkoRequestActivity.onRequest = true;
                                        Intent intent = new Intent(this, BkoRequestActivity.class);
                                        startActivity(intent);
                                    }

                                } else {
                                    Intent intent = new Intent(BkoFirebaseMessagingService.this, BkoMainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }

                            } else {
                                if (BkoTripsAdapter.mp != null && BkoTripsAdapter.mp.isPlaying())
                                    BkoTripsAdapter.mp.stop();
                                BkoRequestActivity.onRequest = false;
                                Intent intent = new Intent(BkoFirebaseMessagingService.this, BkoMainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                            }


                        } catch (Exception e) {
                            BkoRequestActivity.onRequest = false;
                            Intent intent = new Intent(BkoFirebaseMessagingService.this, BkoMainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        }

                    } else {
                        BkoRequestActivity.onRequest = false;
                        Intent intent = new Intent(BkoFirebaseMessagingService.this, BkoMainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

    }
}

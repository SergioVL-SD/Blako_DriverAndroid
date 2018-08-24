package com.blako.mensajero.Services.mqtt;

import android.content.Context;

import com.blako.mensajero.Constants;
import com.blako.mensajero.Utils.LogUtils;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Random;

public class MqttService {

    final String LOG_SOURCE = MqttService.class.getSimpleName();

    MqttAndroidClient mqttAndroidClient;

    private final String username = "mqtt-test";
    private final String password = "mqtt-test";

    private OnMqttConnectionListener connectionListener;

    public void setOnMqttConnectionListener(OnMqttConnectionListener listener) {
        this.connectionListener = listener;
    }

    private OnMqttMessageListener messageListener;

    public void setOnMqttMessageListener(OnMqttMessageListener listener) {
        this.messageListener = listener;
    }

    private OnMqttSubscriptionsListener subscriptionsListener;

    public void setOnMqttSubscriptionsListener(OnMqttSubscriptionsListener listener) {
        this.subscriptionsListener = listener;
    }

    public MqttService(Context context) {
        //  TODO: run when user has an uid
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());

        String clientId = "Victor";
        clientId += String.valueOf(1000000L * random.nextDouble());

        mqttAndroidClient = new MqttAndroidClient(context, Constants.MQTT_SERVER_URI, clientId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                LogUtils.debug(LOG_SOURCE, "(mqtt) connectComplete(): " + String.valueOf(s));

                if(connectionListener != null){
                    connectionListener.onConnectComplete(b);
                }
            }

            @Override
            public void connectionLost(Throwable throwable) {
                LogUtils.error(LOG_SOURCE, "(mqtt) connectionLost(): " + throwable.getMessage());

                if(connectionListener != null){
                    connectionListener.onConnectionLost();
                }
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                LogUtils.debug(LOG_SOURCE, "(mqtt) messageArrived(): " + mqttMessage.toString());

                if(messageListener != null){
                    messageListener.onMessageArrived(topic, mqttMessage.toString());
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                LogUtils.debug(LOG_SOURCE, "(mqtt) deliveryComplete(): " + iMqttDeliveryToken.toString());

                if(messageListener != null){
                    messageListener.onDeliveryComplete(iMqttDeliveryToken.toString());
                }
            }
        });
//        connect();
    }

    public void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }

    //  due to subscriptions being done from within the service
    public void connect(){
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(true);
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());

        try {

            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    LogUtils.debug(LOG_SOURCE, "(mqtt) connect onSuccess(): " + asyncActionToken.getClient().getClientId());

                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);

                    if(connectionListener != null){
                        connectionListener.postConnectionSuccess();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    LogUtils.error(LOG_SOURCE, "(mqtt) Failed to connect to: " + Constants.MQTT_SERVER_URI + exception.toString());

                    if(connectionListener != null){
                        connectionListener.postConnectionFailure();
                    }
                }
            });


        } catch (MqttException ex){
            ex.printStackTrace();
        }
    }


    public void subscribeToTopic(final String topic) {
        try {
            mqttAndroidClient.subscribe(topic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    LogUtils.debug(LOG_SOURCE,"(mqtt) subscribed to " + topic);

                    if(subscriptionsListener != null){
                        subscriptionsListener.onSubscribeSuccess(topic);
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    LogUtils.debug(LOG_SOURCE, "(mqtt) subscribed to " + topic + " failed");

                    if(subscriptionsListener != null){
                        subscriptionsListener.onSubscribeFailure(topic);
                    }
                }
            });

        } catch (MqttException ex) {
            System.err.println("Exceptionst subscribing");
            ex.printStackTrace();
        }
    }

    public void unsubscribeFromTopic(final String topic)
    {
        try
        {
            mqttAndroidClient.unsubscribe(topic, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    LogUtils.debug(LOG_SOURCE, "(mqtt) unsubscribed from " + topic);

                    if(subscriptionsListener != null){
                        subscriptionsListener.onUnsubscribeSuccess(topic);
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    LogUtils.debug(LOG_SOURCE, "(mqtt) unsubscribed from " + topic + " failed");

                    if(subscriptionsListener != null){
                        subscriptionsListener.onUnsubscribeFailure(topic);
                    }
                }
            });
        } catch (MqttException ex) {
            System.err.println("Exceptionst subscribing");
            ex.printStackTrace();
        }
    }

    public boolean publishToTopic(String topic, String payload, int qos)
    {
        if(mqttAndroidClient != null && mqttAndroidClient.isConnected())
        {
            MqttMessage message = new MqttMessage();
            message.setPayload(payload.getBytes());
            message.setQos(qos);

            try {
                mqttAndroidClient.publish(topic, message);
                LogUtils.debug(LOG_SOURCE, "(mqtt) published " + topic + ":" + message);
            } catch (MqttException e) {
                e.printStackTrace();

                LogUtils.error(LOG_SOURCE, "(mqtt) error while publishing: " + e.getLocalizedMessage());
            }
            return true;
        }
        return false;
    }

    public interface OnMqttConnectionListener {
        void onConnectComplete(boolean complete);
        void onConnectionLost();
        void postConnectionSuccess();
        void postConnectionFailure();
    }

    public interface OnMqttMessageListener {
        void onMessageArrived(String topic, String payload);
        void onDeliveryComplete(String mid);
    }

    public interface OnMqttSubscriptionsListener {
        void onSubscribeSuccess(String topic);
        void onSubscribeFailure(String topic);
        void onUnsubscribeSuccess(String topic);
        void onUnsubscribeFailure(String topic);
    }
}

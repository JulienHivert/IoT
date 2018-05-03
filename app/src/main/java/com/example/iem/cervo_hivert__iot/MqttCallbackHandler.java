package com.example.iem.cervo_hivert__iot;

import android.content.Context;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttCallbackHandler implements MqttCallback {

    private Context context;
    private String clientHandle;

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String message_str = new String(message.getPayload(), "UTF-8");
        System.out.println("message arrivé str " + topic + " " + message_str);


    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}

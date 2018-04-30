package com.example.iem.cervo_hivert__iot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {

    private Button connexionButton;
    private Button sendHello;
    private MqttAndroidClient client = null;
    private final String topic = "LEDArduino";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();
        initListeners();
    }

    @Override
    public void onPause() {
        super.onPause();
        disconnect();
    }

    public void initComponents(){
        this.connexionButton = findViewById(R.id.connexionButton);
        this.sendHello = findViewById(R.id.sendHello);
    }

    public void initListeners(){
        connexionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                connect("10.0.2.15", "1883");
            }
        });

        sendHello.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendMsg("Hello world");
            }
        });
    }

    public void connect(String address, String port) {
        String clientId = MqttClient.generateClientId(); // génère un ID
        client = new MqttAndroidClient(getApplicationContext(), "tcp://" + address + ":" + port, clientId);

        try {
            IMqttToken token = client.connect(); // on tente de se connecter
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // Nous sommes connecté
                    System.out.println("On est connecté !");
                    //subscribe(topic); // ligne à commenter pour le moment
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Erreur de connexion : temps de connexion trop long ou problème de pare-feu
                    System.err.println("Echec de connection !");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        //client.setCallback(new MqttCallbackHandler()); // ligne à commenter pour le moment
    }

    public void disconnect() {
        if (client == null) {
            return;
        }
        try {
            IMqttToken disconToken = client.disconnect();
            disconToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // Nous nous sommes correctement déconnecté
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // Quelque chose c'est mal passé, mais on est probablement déconnecté malgré tout
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String msg) {
        MqttMessage message = new MqttMessage();
        message.setPayload(msg.getBytes());
        try {
            client.publish(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


}

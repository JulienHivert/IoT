package com.example.iem.cervo_hivert__iot;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {

    private Button connexionButton;
    private Button send1;
    private Button send2;
    private Button sendRGB;
    private final String topic = "LEDArduino";
    private static int QOS = 0;
    private final static  String MQTT_IP = "172.20.10.14";
    private final static  String MQTT_PORT = "1896";
    private String clientId;
    private MqttAndroidClient client;
    //Seekbars
    private SeekBar seekBarR;
    private SeekBar seekBarG;
    private SeekBar seekBarB;
    //Textview linked to seekBars
    private TextView tvR;
    private TextView tvG;
    private TextView tvB;

    private ImageView imgPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();
        initClickListeners();
        initSeekbars();
        iniConnection();
    }

    @Override
    public void onPause() {
        super.onPause();
        disconnect();
    }

    public void initComponents(){
        this.connexionButton = findViewById(R.id.connexionButton);
        this.send1 = findViewById(R.id.send1);
        this.send2 = findViewById(R.id.send2);
        this.sendRGB = findViewById(R.id.sendRGB);
        this.seekBarR = findViewById(R.id.seekBarR);
        this.seekBarG = findViewById(R.id.seekBarG);
        this.seekBarB = findViewById(R.id.seekBarB);

        this.tvR = findViewById(R.id.dataR);
        this.tvR.setText("0");

        this.tvG = findViewById(R.id.dataG);
        this.tvG.setText("0");

        this.tvB = findViewById(R.id.dataB);
        this.tvB.setText("0");

        this.imgPreview = findViewById(R.id.imageView2);
    }

    public void initClickListeners(){
        connexionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                connect(MQTT_IP, MQTT_PORT);
            }
        });

        send1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendMsg("1");
            }
        });
        send2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendMsg("2");
            }
        });
        sendRGB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MQTT", tvR.getText() + "," + tvG.getText() + "," + tvB.getText());
                sendMsg(tvR.getText() + "," + tvG.getText() + "," + tvB.getText()+ "%");

            }
        });

    }

    public void initSeekbars(){
        seekBarR.setProgress(0);
        seekBarR.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvR.setText(String.valueOf(progress));
                reloadBackground();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //nothing
            }
        });
        seekBarG.setProgress(0);
        seekBarG.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvG.setText(String.valueOf(progress));
                reloadBackground();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //nothing
            }
        });
        seekBarB.setProgress(0);
        seekBarB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvB.setText(String.valueOf(progress));
                reloadBackground();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //nothing
            }
        });
    }

    public void reloadBackground(){
        int intTVR = Integer.parseInt(tvR.getText().toString());
        int intTVG = Integer.parseInt(tvG.getText().toString());
        int intTVB = Integer.parseInt(tvB.getText().toString());
        String hex = String.format("#%02x%02x%02x", intTVR, intTVG, intTVB);
        Color color = new Color();
        this.imgPreview.setBackgroundColor(color.parseColor(hex));
    }

    public void iniConnection(){
        this.clientId = MqttClient.generateClientId(); // génère un ID
        this.client = new MqttAndroidClient(getApplicationContext(), "tcp://" + MQTT_IP + ":" + MQTT_PORT, clientId);
    }

    public void connect(String address, String port) {
        try {
            IMqttToken token = client.connect(); // on tente de se connecter
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // Nous sommes connecté
                    Log.i("MQTT","On est connecté !");
                    subscribe(topic); // ligne à commenter pour le moment
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Erreur de connexion : temps de connexion trop long ou problème de pare-feu
                    Log.e("MQTT ","Echec de connection !");
                }
            });
        } catch (MqttException e) {
//            e.printStackTrace();
        }
        client.setCallback(new MqttCallbackHandler());
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
                    Log.e("MQTT","exception");
                }
            });
        } catch (MqttException e) {
//            e.printStackTrace();
        }
    }

    public void sendMsg(String msg) {
        MqttMessage message = new MqttMessage();
        message.setPayload(msg.getBytes());
        try {
            client.publish(topic, message);
            Log.i("MQTT", msg + " envoyé");
        } catch (MqttException e) {
            Log.e("MQTT","exception");
        }
    }
    public void subscribe(final String topic) {
        try {
            IMqttToken subToken = client.subscribe(topic, QOS);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // On a bien souscrit au topic
                    System.out.println("onSuccess subscribe topic " + topic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Log.d("INFO", "Erreur de souscription au topic ..");
                    // La souscription n'a pas pu se faire, peut être que l'utilisateur n'a pas
                    // l'autorisation de souscrire à ce topic
                }
            });
        } catch (MqttException e) {
            Log.d("MQTT", "Erreur de connexion ..");
//            e.printStackTrace();
        } catch (Exception e) {
            Log.e("MQTT","exception");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        connect(MQTT_IP, MQTT_PORT);
    }
}

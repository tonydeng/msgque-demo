package com.github.chord1645.msgque.demo.client;

import com.github.chord1645.msgque.demo.PaintData;
import com.github.chord1645.msgque.demo.ui.Apoint;
import com.github.chord1645.msgque.demo.ui.PaintFrame;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.msgpack.MessagePack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class PaintMqttClient implements IPaintClient {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    IMqttClient iclient;
    MqttClientPersistence dataStore;
    String topic = "/painter";

    public PaintMqttClient() {
        paintFrame = new PaintFrame("画图程序", this);
    }

    public static void main(String[] args) throws Exception {
        new PaintMqttClient().connect("tcp://localhost:9999");
    }

    private void connect(String url) throws MqttException {
        String tmpDir = System.getProperty("java.io.tmpdir");
        dataStore = new MqttDefaultFilePersistence(tmpDir + File.separator + "publisher");
        iclient = new MqttClient(url, System.currentTimeMillis() + "", dataStore);
        iclient.setCallback(new TestCallback());
        iclient.connect();
        iclient.subscribe("/painter", qos);

    }

    PaintFrame paintFrame;


    List<Apoint> list = new ArrayList<>();
    MessagePack messagePack = new MessagePack();
    int qos = 2;

    @Override
    public void clearCache() {
        list.clear();
    }


    @Override
    public void flushCache() {
        try {
            list.add(new Apoint(-1, -1, 6));
            PaintData paintData = new PaintData();
            paintData.setData(list);
            MqttMessage mqttMessage = new MqttMessage(messagePack.write(paintData));
            mqttMessage.setQos(qos);
            iclient.publish(topic, mqttMessage);
        } catch (Exception e) {
            logger.error("flushCache ex", e);
        }

    }

    @Override
    public void append(Apoint apoint) {
        list.add(apoint);
    }

    class TestCallback implements MqttCallback {

        private boolean m_connectionLost = false;

        @Override
        public void connectionLost(Throwable cause) {
            m_connectionLost = true;
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            logger.info("messageArrived:{}", topic);
            PaintData paintData = messagePack.read(message.getPayload(), PaintData.class);
            paintFrame.addData(paintData.getData());
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
        }
    }

}
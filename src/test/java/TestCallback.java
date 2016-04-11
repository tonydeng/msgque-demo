/*
 * Copyright (c) 2012-2015 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.SimpleDateFormat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author andrea
 */
class TestCallback implements MqttCallback {

    private MqttMessage m_message;
    private String m_topic;
    private CountDownLatch m_latch = new CountDownLatch(1);
    private boolean m_connectionLost = false;

    public MqttMessage getMessage(boolean checkElapsed) {
        try {
            boolean elapsed = !m_latch.await(2, TimeUnit.SECONDS);//TODO bug
            if (elapsed && checkElapsed) {
                throw new IllegalStateException("Elapsed the timeout to get the result");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return m_message;
    }

    public String getTopic() {
        try {
            m_latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return m_topic;
    }

    void reinit() {
        m_latch = new CountDownLatch(1);
        m_message = null;
        m_topic = null;
        m_connectionLost = false;
    }

    public boolean connectionLost() {
        return m_connectionLost;
    }

//    @Override
    public void connectionLost(Throwable cause) {
        m_connectionLost = true;
    }

    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss SSS");

//    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

        System.out.println("publish to suscribe cost("+PublishTest.Qos+"):"+(System.currentTimeMillis() - Long.valueOf(new String(message.getPayload())))+"ms");
        m_message = message;
        m_topic = topic;
        m_latch.countDown();
    }

//    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }
}

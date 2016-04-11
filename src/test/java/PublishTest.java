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


import io.moquette.server.Server;
import io.moquette.server.config.IConfig;
import io.moquette.server.config.MemoryConfig;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author andrea
 */
public class PublishTest {

//    private static final Logger LOG = LoggerFactory.getLogger(PublishTest.class);

    Server m_server;
    static MqttClientPersistence s_subDataStore;
    static MqttClientPersistence s_pubDataStore;

    IMqttClient m_subscriber;
    IMqttClient m_publisher;
    TestCallback m_callback;
    IConfig m_config;

    @BeforeClass
    public static void beforeTests() {
        String tmpDir = System.getProperty("java.io.tmpdir");
        s_subDataStore = new MqttDefaultFilePersistence(tmpDir + File.separator + "subscriber");
        s_pubDataStore = new MqttDefaultFilePersistence(tmpDir + File.separator + "publisher");
    }

    protected void startServer() throws IOException {
        m_server = new Server();
        final Properties configProps = IntegrationUtils.prepareTestPropeties();
        m_config = new MemoryConfig(configProps);
        m_server.startServer(m_config);
    }

    @Before
    public void setUp() throws Exception {
        startServer();
        m_subscriber = new MqttClient("tcp://localhost:1883", "Subscriber", s_subDataStore);
        m_callback = new TestCallback();
        m_subscriber.setCallback(m_callback);
        m_subscriber.connect();

        m_publisher = new MqttClient("tcp://localhost:1883", "Publisher", s_pubDataStore);
        m_publisher.connect();
    }

    @After
    public void tearDown() throws Exception {
        if (m_publisher.isConnected()) {
            m_publisher.disconnect();
        }

        if (m_subscriber.isConnected()) {
            m_subscriber.disconnect();
        }

        m_server.stopServer();
        IntegrationUtils.cleanPersistenceFile(m_config);
    }

    @Test
    public void test1() throws Exception {
        m_subscriber.subscribe("/topic", 2);
        MqttMessage mqttMessage = new MqttMessage("Hello world MQTT QoS0".getBytes());
        mqttMessage.setQos(2);
        m_publisher.publish("/topic", mqttMessage);
        System.out.println("start()");
    }

    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss SSS");
    static  public  int Qos =2;
    @Test
    public void test() throws Exception {
        CountDownLatch countDown = new CountDownLatch(3);
        m_subscriber.subscribe("/topic", Qos);
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    System.out.println("m_publisher.publish");
                    for (int i = 0; i < 10; i++) {
                        MqttMessage mqttMessage = buildMsg();
                        m_publisher.publish("/topic", mqttMessage);
                    }
                } catch (MqttException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        }.start();
        System.out.println("start()");
        countDown.await(3, TimeUnit.SECONDS);
    }

    private MqttMessage buildMsg() {
        MqttMessage mqttMessage = new MqttMessage(String.valueOf(new Date().getTime()).getBytes());
        mqttMessage.setQos(Qos);
        return mqttMessage;
    }
}

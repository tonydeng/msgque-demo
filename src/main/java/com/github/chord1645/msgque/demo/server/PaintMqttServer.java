package com.github.chord1645.msgque.demo.server;

import io.moquette.BrokerConstants;
import io.moquette.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class PaintMqttServer extends Server {
    private static final Logger logger = LoggerFactory.getLogger(PaintMqttServer.class);

    public static void main(String[] args) throws IOException {
        Properties cfg = new Properties();
        cfg.put(BrokerConstants.HOST_PROPERTY_NAME, "localhost");
        cfg.put(BrokerConstants.PORT_PROPERTY_NAME, "9999");
        final PaintMqttServer server = new PaintMqttServer();
        server.startServer(cfg);
        logger.info("Server started, version 0.8");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                server.stopServer();
            }
        });
    }


}
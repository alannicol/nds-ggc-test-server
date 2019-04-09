package org.nds.ggc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.util.Properties;

public class ServerProperty {

    private static Logger logger = LoggerFactory.getLogger(ServerProperty.class);

    private static final String SERVER_PROPERTIES="server.properties";
    private static final String CLIENT_URL="client.url";
    private static final String SERVER_DELAY="server.delay";

    private static Properties properties = obtainProperties();

    public static String getClientUrl() {
        return properties.get(CLIENT_URL).toString();
    }

    public static long getServerDelay() {
        return Long.parseLong(properties.get(SERVER_DELAY).toString());
    }

    private static Properties obtainProperties() {
        Properties properties=null;

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(ServerProperty.class.getClassLoader().getResourceAsStream(SERVER_PROPERTIES))) {
            properties = new Properties();
            properties.load(bufferedInputStream);
        } catch(Exception exception) {
            logger.error("Cannot obtain properties file", exception);
        }

        return properties;
    }

}

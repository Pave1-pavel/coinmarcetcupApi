package org.example;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Configuration {

    private static final Properties properties;
    public static final List<String> currencies;

    static {
        String path = System.getProperty("app_settings");
        if (path == null) {
            path = "app.properties";
        }
        properties = new Properties();
        try {
            properties.load(new FileInputStream(path));
        } catch (IOException e) {
            throw new RuntimeException("Cannot read application properties");
        }
        currencies = Arrays.stream(getProperty("currencies").split(",")).toList();
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}

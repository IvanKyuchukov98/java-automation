package com.qa.automation.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Reads settings with this priority: -D system property, then environment variable,
 * then config.properties.
 */
public final class Config {

    private static final Properties PROPERTIES = load();

    private Config() {
    }

    public static String get(String key) {
        String value = System.getProperty(key);
        if (value == null) {
            value = System.getenv(key.replace('.', '_').toUpperCase());
        }
        if (value == null) {
            value = PROPERTIES.getProperty(key);
        }
        if (value == null) {
            throw new IllegalStateException("Missing config value: " + key);
        }
        return value.trim();
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    public static long getLong(String key) {
        return Long.parseLong(get(key));
    }

    public static String uiBaseUrl() {
        return get("ui.base.url");
    }

    public static String apiBaseUrl() {
        return get("api.base.url");
    }

    private static Properties load() {
        Properties properties = new Properties();
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new IllegalStateException("config.properties not found on classpath");
            }
            properties.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read config.properties", e);
        }
        return properties;
    }
}

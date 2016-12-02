package com.epam.cm.core.properties;

import com.epam.cm.core.logger.LoggerFactory;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesController.class);

    private static PropertiesController instance = new PropertiesController();
    private final Properties properties = new Properties();

    private PropertiesController() {
    }

    public static String getProperty(final String propertyName) {
        return System.getProperty(propertyName);
    }



}
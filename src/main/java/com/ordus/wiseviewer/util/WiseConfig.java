/*
 * Copyright (c) 2017 Hugo Viejo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ordus.wiseviewer.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WiseConfig {
    private static Logger LOG = LoggerFactory.getLogger(WiseConfig.class);

    private static final String CONFIG_FILE = "WiseViewer.properties";

    private static Properties p = null;

    private WiseConfig() {

    }

    public static String get(String key, String defaultValue) {
        if (p == null) {
            loadConfig();
        }
        String ret = p.getProperty(key);
        if (ret == null) {
            ret = defaultValue;
        }
        return ret;
    }

    public static void loadConfig() {
        Path configDirectory = Paths.get(WiseConstants.CONFIG_PATH);
        if (!Files.exists(configDirectory)) {
            try {
                Files.createDirectories(configDirectory);
            } catch (IOException e) {
                LOG.error("Error creating config dir: " + e.getMessage(), e);
            }
        }

        p = new Properties();
        Path configFile = Paths.get(WiseConstants.CONFIG_PATH, CONFIG_FILE);
        if (Files.exists(configFile)) {
            try {
                p.load(Files.newInputStream(configFile));
            } catch (IOException e) {
                LOG.error("Error reading config file");
                loadDefault();
            }
        } else {
            loadDefault();
        }
    }

    private static void loadDefault() {
        try {
            p.load(WiseConfig.class.getResourceAsStream("/" + CONFIG_FILE));
            store();
        } catch (IOException e) {
            LOG.error("Error reading default config file: " + e.getMessage(), e);
        }
    }

    private static void store() {
        Path configFile = Paths.get(WiseConstants.CONFIG_PATH, CONFIG_FILE);
        if (!Files.exists(configFile)) {
            try {
                OutputStream outputStream = Files.newOutputStream(configFile);
                p.store(outputStream, "WiseViewer config file");
                outputStream.close();
            } catch (IOException e) {
                LOG.error("Error writing config file");
            }
        }

    }

}

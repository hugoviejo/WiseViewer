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

package com.ordus.wiseviewer.service;

import java.io.IOException;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadImageThread implements Runnable {
    private static Logger LOG = LoggerFactory.getLogger(LoadImageThread.class);

    private WiseViewerService wiseService;
    private JLabel label;
    private String url;
    private String defaultImage;

    public LoadImageThread(WiseViewerService wiseService, JLabel label, String url, String defaultImage) {
        this.wiseService = wiseService;
        this.label = label;
        this.url = url;
        this.defaultImage = defaultImage;
        new Thread(this).start();
    }

    @Override public void run() {
        try {
            Icon image = wiseService.loadImage(url, false);
            if (image == null) {
                image = wiseService.loadImage(defaultImage, true);
            }
            label.setIcon(image);

        } catch (IOException | NullPointerException e) {
            LOG.warn("Error loading image: " + e.getMessage() + " url: " + url, e);
        }
    }
}

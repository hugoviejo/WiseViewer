package com.ordus.wiseviewer.service;

import java.io.IOException;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by hugo on 24/04/17.
 */
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

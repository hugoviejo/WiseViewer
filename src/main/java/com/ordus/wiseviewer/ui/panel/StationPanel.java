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

package com.ordus.wiseviewer.ui.panel;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ordus.wiseviewer.domain.Station;
import com.ordus.wiseviewer.service.LoadImageThread;
import com.ordus.wiseviewer.service.WiseViewerService;
import com.ordus.wiseviewer.util.WiseConstants;

public class StationPanel extends JPanel {
    private static Logger LOG = LoggerFactory.getLogger(StationPanel.class);

    private Station station;
    private WiseViewerService wiseService;

    public StationPanel(WiseViewerService wiseService, Station station) {
        this.station = station;
        this.wiseService = wiseService;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setPreferredSize(
                new Dimension(WiseConstants.CELL_WIDTH, WiseConstants.CELL_HEIGHT + WiseConstants.CELL_HEIGHT_MARGIN));
        JLabel wIcon;
        try {
        //    wIcon = new JLabel(wiseService.loadImage(station.getImage()));
            wIcon = new JLabel(wiseService.loadImage(WiseConstants.DOWNLOADING_IMAGE, true));
        } catch (IOException | NullPointerException e) {
            LOG.warn("Error: " + e.getMessage() + " station: " + station, e);
            wIcon = new JLabel(station.getName());

        }
        if(station.getInfo() != null) {
            wIcon.setToolTipText(station.getInfo());
        } else {
            wIcon.setToolTipText(station.getName());
        }
        wIcon.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                switch (e.getButton()) {
                case MouseEvent.BUTTON1:
                    wiseService.watch(station);
                    break;
                case MouseEvent.BUTTON2:
                case MouseEvent.BUTTON3:
                    Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                    StringSelection stringSelection = new StringSelection(station.getUrl());
                    clpbrd.setContents(stringSelection, null);
                    break;
                }
            }
        });
        add(wIcon);

        JPanel namePanel = new JPanel(new GridLayout(2, 1));
        JLabel nameLabel = new JLabel(station.getName());
        namePanel.add(nameLabel);

        add(wIcon, BorderLayout.CENTER);
        add(namePanel, BorderLayout.SOUTH);

        new LoadImageThread(wiseService, wIcon, station.getImage(), WiseConstants.DEFAULT_STATION_IMAGE);
    }

}

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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ordus.wiseviewer.domain.Group;
import com.ordus.wiseviewer.service.WiseViewerService;
import com.ordus.wiseviewer.util.WiseConstants;

public class GroupPanel extends JPanel {
    private static Logger LOG = LoggerFactory.getLogger(GroupPanel.class);

    private Group group;
    private WiseViewerService wiseService;

    public GroupPanel(WiseViewerService wiseService, Group group) {
        this.group = group;
        this.wiseService = wiseService;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setPreferredSize(
                new Dimension(WiseConstants.CELL_WIDTH, WiseConstants.CELL_HEIGHT + WiseConstants.CELL_HEIGHT_MARGIN));
        JLabel wIcon;
        try {
            wIcon = new JLabel(wiseService.loadImage(group.getImage()));
        } catch (IOException | NullPointerException e) {
            LOG.warn("Error: " + e.getMessage() + " group: " + group, e);
            wIcon = new JLabel(group.getName());
        }
        wIcon.setToolTipText(group.getName());
        wIcon.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 1) {
                    wiseService.loadWiseVisualizacionPanel(group);
                }
            }
        });

        int items = (group.getGroups() != null ? group.getGroups().size() : 0) + (group.getStations() != null ? group
                .getStations().size() : 0);
        JPanel namePanel = new JPanel(new GridLayout(2, 1));
        JLabel nameLabel = new JLabel(group.getName());
        JLabel itemsLabel = new JLabel("Items: " + items);
        namePanel.add(nameLabel);
        namePanel.add(itemsLabel);

        add(wIcon, BorderLayout.CENTER);
        add(namePanel, BorderLayout.SOUTH);
    }
}

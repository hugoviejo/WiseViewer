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
import java.awt.event.ActionEvent;

import javax.swing.*;

import com.ordus.wiseviewer.domain.ListElement;
import com.ordus.wiseviewer.service.ListsManagementService;
import com.ordus.wiseviewer.service.WiseViewerService;
import com.ordus.wiseviewer.util.WiseConstants;

public class ListsPanel extends JPanel {
    private WiseViewerService wiseService;
    private ListsManagementService listsManagementService;
    private JPanel centerPanel;

    public ListsPanel(WiseViewerService wiseService, ListsManagementService listsManagementService) {
        this.wiseService = wiseService;
        this.listsManagementService = listsManagementService;
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        centerPanel = new JPanel();
        JScrollPane scrollPane = new JScrollPane(centerPanel);
        add(scrollPane, BorderLayout.CENTER);

        JButton addButton = new JButton("Add");
        addButton.addActionListener(new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                wiseService.addListDialog();
            }
        });
        add(addButton, BorderLayout.SOUTH);
    }

    private void loadData() {
        centerPanel.removeAll();
        java.util.List<ListElement> fullList = listsManagementService.load();
        int cols = WiseConstants.NUM_COLS_MAIN;
        int rows = (int) Math.ceil(fullList.size() / (float) cols);
        centerPanel.setLayout(new GridLayout(rows, cols));
        for (ListElement currentList : fullList) {
            centerPanel.add(new ListElementPanel(wiseService, listsManagementService, currentList));
        }
    }

}

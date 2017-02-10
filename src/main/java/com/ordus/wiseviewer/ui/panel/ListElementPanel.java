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
import com.ordus.wiseviewer.domain.WiseList;
import com.ordus.wiseviewer.service.ListsManagementService;
import com.ordus.wiseviewer.service.WiseViewerService;

public class ListElementPanel extends JPanel {
    private ListElement listElement;
    private ListsManagementService listsManagementService;
    private WiseViewerService wiseService;

    public ListElementPanel(WiseViewerService wiseService, ListsManagementService listsManagementService, ListElement
            listElement) {
        this.wiseService = wiseService;
        this.listsManagementService = listsManagementService;
        this.listElement = listElement;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        final JLabel nameLabel = new JLabel(listElement.getName());

        JButton showButton = new JButton("Show");
        showButton.addActionListener(new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                wiseService.loadList(listElement);
                listsManagementService.store();
            }
        });
        JButton editButton = new JButton("View URL");
        editButton.addActionListener(new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                wiseService.editListDialog(listElement);
            }
        });
        JButton downloadButton = new JButton("Download");
        downloadButton.addActionListener(new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                WiseList wiseList = wiseService.downloadList(listElement);
                if (wiseList != null) {
                    nameLabel.setText(wiseList.getName());
                    listsManagementService.store();
                }
            }
        });
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                if (JOptionPane
                        .showConfirmDialog(null, "Are you sure?", "Delete list confirmation", JOptionPane.YES_NO_OPTION)
                        ==
                        JOptionPane.YES_OPTION) {
                    wiseService.deleteList(listElement);
                    wiseService.loadListsPanel();
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(showButton);
        buttonPanel.add(downloadButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(new JLabel("  "), BorderLayout.WEST);
        add(nameLabel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}

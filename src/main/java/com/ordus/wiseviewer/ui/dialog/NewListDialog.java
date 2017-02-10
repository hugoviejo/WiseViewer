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

package com.ordus.wiseviewer.ui.dialog;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.UUID;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;

import com.ordus.wiseviewer.domain.ListElement;
import com.ordus.wiseviewer.domain.WiseList;
import com.ordus.wiseviewer.service.ListsManagementService;
import com.ordus.wiseviewer.service.WiseViewerService;

public class NewListDialog extends JDialog {
    private WiseViewerService wiseService;
    private ListsManagementService listsManagementService;
    private ListElement listElement;

    public NewListDialog(Frame owner, WiseViewerService wiseService, ListsManagementService listsManagementService,
            ListElement listElement) {
        super(owner, true);
        this.wiseService = wiseService;
        this.listsManagementService = listsManagementService;
        this.listElement = listElement;

        initUI();
        setLocationRelativeTo(owner);
        setVisible(true);
    }

    private void initUI() {
        JLabel urlLabel = new JLabel("URL:");
        final JTextField urlTextfield = new JTextField(50);

        JPopupMenu popup = new JPopupMenu();
        JMenuItem item;
        if (listElement == null) {
            item = new JMenuItem(new DefaultEditorKit.CutAction());
            item.setText("Cut");
            popup.add(item);
        }
        item = new JMenuItem(new DefaultEditorKit.CopyAction());
        item.setText("Copy");
        popup.add(item);
        if (listElement == null) {
            item = new JMenuItem(new DefaultEditorKit.PasteAction());
            item.setText("Paste");
            popup.add(item);
        }
        urlTextfield.setComponentPopupMenu(popup);

        JPanel urlPanel = new JPanel();
        urlPanel.add(urlLabel);
        urlPanel.add(urlTextfield);

        JButton okButton = new JButton("Ok");
        if (listElement != null) {
            urlTextfield.setText(listElement.getUrl());
            urlTextfield.setEditable(false);
            okButton.addActionListener(new AbstractAction() {
                @Override public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                }
            });
        } else {
            okButton.addActionListener(new AbstractAction() {
                @Override public void actionPerformed(ActionEvent e) {
                    //TODO check if URL alredy exists
                    ListElement listElement = new ListElement(UUID.randomUUID().toString(), null,
                            urlTextfield.getText(),
                            null);
                    WiseList wiseList = wiseService.downloadList(listElement);
                    if (wiseList != null) {
                        listsManagementService.add(listElement);
                        wiseService.loadListsPanel();
                        setVisible(false);
                    }
                }
            });
        }
        setLayout(new BorderLayout());
        add(urlPanel, BorderLayout.CENTER);
        add(okButton, BorderLayout.SOUTH);

        setResizable(false);
        pack();
    }
}

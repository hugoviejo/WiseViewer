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

import javax.swing.*;

public class AboutDialog extends JDialog {

    public AboutDialog(Frame owner) {
        super(owner, true);
        initUI();
        setLocationRelativeTo(owner);
        setVisible(true);
    }

    private void initUI() {
        setLayout(new BorderLayout());

        String appVersion = AboutDialog.class.getPackage().getImplementationVersion();
        appVersion = appVersion != null ? appVersion : "DEVELOPMENT";

        JLabel versionLabel = new JLabel("WiseViewer V." + appVersion);
        JLabel authorLabel = new JLabel("Developped by Ordus 2017");
        JLabel licenseLabel = new JLabel("Apache 2 license");

        JPanel aboutPanel = new JPanel();
        aboutPanel.setLayout(new GridLayout(3, 1, 10, 5));
        aboutPanel.add(versionLabel);
        aboutPanel.add(authorLabel);
        aboutPanel.add(licenseLabel);

        JButton okButton = new JButton("Ok");
        okButton.addActionListener(new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        add(new JLabel("  "), BorderLayout.EAST);
        add(new JLabel("  "), BorderLayout.WEST);
        add(aboutPanel, BorderLayout.CENTER);
        add(okButton, BorderLayout.SOUTH);

        setResizable(false);
        pack();

    }
}

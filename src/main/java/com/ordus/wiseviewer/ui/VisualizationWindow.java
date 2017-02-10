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

package com.ordus.wiseviewer.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import com.ordus.wiseviewer.service.WiseViewerService;

public class VisualizationWindow extends JFrame implements MainFrame {
    private static final String APP_ICON = "/img/icon.png";

    private WiseViewerService wiseService;
    private Component currentContent;
    private JScrollPane scrollPane;

    public VisualizationWindow(WiseViewerService wiseService) {
        this.wiseService = wiseService;
        this.wiseService.setMainFrame(this);
        initUI();
        wiseService.loadListsPanel();
    }

    private void initUI() {
        setTitle("WiseList Viewer");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        try {
            setIconImage(ImageIO.read(VisualizationWindow.class.getResourceAsStream(APP_ICON)));
        } catch (IOException e) {
            //do nothing
        }

        JMenuBar menubar = new JMenuBar();
        JMenuItem eMenuHome = new JMenuItem("Home");
        eMenuHome.setMnemonic(KeyEvent.VK_H);
        eMenuHome.setToolTipText("Go Home");
        eMenuHome.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                wiseService.loadListsPanel();
            }
        });
        menubar.add(eMenuHome);
        JMenuItem eMenuAbout = new JMenuItem("About");
        eMenuAbout.setMnemonic(KeyEvent.VK_A);
        eMenuAbout.setToolTipText("About");
        eMenuAbout.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                wiseService.aboutDialog();
            }
        });
        eMenuAbout.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        eMenuAbout.setHorizontalAlignment(SwingConstants.RIGHT);
        menubar.add(Box.createHorizontalGlue());
        menubar.add(eMenuAbout);
        setJMenuBar(menubar);

        setLayout(new BorderLayout());
        scrollPane = new JScrollPane();
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        wiseService.setProgressBar(progressBar);

        add(scrollPane, BorderLayout.CENTER);
        add(progressBar, BorderLayout.SOUTH);

        setSize(1024, 768);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void changeContent(Component component) {
        if (currentContent != null) {
            scrollPane.remove(currentContent);
        }
        scrollPane.setViewportView(component);
        currentContent = component;
        scrollPane.doLayout();
        repaint();
    }

    @Override public Frame getFrame() {
        return this;
    }
}

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

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ordus.wiseviewer.domain.Group;
import com.ordus.wiseviewer.domain.ListElement;
import com.ordus.wiseviewer.domain.Station;
import com.ordus.wiseviewer.domain.WiseList;
import com.ordus.wiseviewer.parser.WiseListParser;
import com.ordus.wiseviewer.ui.MainFrame;
import com.ordus.wiseviewer.ui.dialog.AboutDialog;
import com.ordus.wiseviewer.ui.dialog.NewListDialog;
import com.ordus.wiseviewer.ui.panel.ListsPanel;
import com.ordus.wiseviewer.ui.panel.WiseVisualizationPanel;
import com.ordus.wiseviewer.util.WiseConfig;
import com.ordus.wiseviewer.util.WiseConstants;

public class WiseViewerServiceImpl implements WiseViewerService {
    private static Logger LOG = LoggerFactory.getLogger(WiseViewerServiceImpl.class);

    private MainFrame mainFrame;
    private JProgressBar progressBar;
    private WiseListParser wiseListParser;
    private ImageCacheService imageCacheService;
    private ListsManagementService listsManagementService;
    private DownloadService downloadService;

    public WiseViewerServiceImpl() {
        WiseConfig.loadConfig();
        this.wiseListParser = new WiseListParser();
        this.imageCacheService = new ImageCacheServiceImpl();
        this.listsManagementService = new ListsManagementServiceImpl();
        this.downloadService = new DownloadServiceImpl(this.wiseListParser);
    }

    @Override public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    @Override public void changePanel(JPanel panel) {
        mainFrame.changeContent(panel);
    }

    @Override public void launchProcess() {
        progressBar.setIndeterminate(true);
    }

    @Override public void updateProgress(int current, int total) {
        progressBar.setIndeterminate(false);
        progressBar.setMaximum(total);
        progressBar.setValue(current);
    }

    @Override public void finishProcess() {
        progressBar.setMaximum(100);
        progressBar.setValue(100);
        imageCacheService.store();
    }

    @Override public void setProgressBar(JProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    @Override public void loadListsPanel() {
        final WiseViewerService wiseViewerService = this;
        Thread t = new Thread(new Runnable() {
            @Override public void run() {
                ListsPanel listsPanel = new ListsPanel(wiseViewerService, listsManagementService);
                changePanel(listsPanel);
            }
        });
        t.start();
    }

    @Override public void loadWiseVisualizacionPanel(final Group group) {
        final WiseViewerService wiseViewerService = this;
        Thread t = new Thread(new Runnable() {
            @Override public void run() {
                WiseVisualizationPanel wiseVisualizationPanel = new WiseVisualizationPanel(wiseViewerService, group);
                changePanel(wiseVisualizationPanel);
            }
        });
        t.start();
    }

    @Override public void loadList(ListElement listElement) {
        try {
            //Check need download (or not exist)
            boolean needsDownload = false;
            Path listFile = Paths.get(WiseConstants.LISTS_PATH, listElement.getId() + LIST_EXTENSION);
            if (!Files.exists(listFile)) {
                needsDownload = true;
            } else {
                if ((System.currentTimeMillis() - listElement.getLastDownload().getTime())
                        > WiseConstants.LIST_CACHED_MILLISECONDS) {
                    needsDownload = true;
                }
            }
            // if yes download
            WiseList wiseList = null;
            if (needsDownload) {
                //Download
                wiseList = downloadList(listElement);
            }
            if (wiseList == null) {
                //parse
                InputStream inputStream = Files.newInputStream(listFile);
                wiseList = wiseListParser.parse(inputStream);
                inputStream.close();
            }
            //load panel
            loadWiseVisualizacionPanel(wiseList);
        } catch (IOException e) {
            LOG.error("Error downloading list: " + e.getMessage(), e);
        }

    }

    @Override public WiseList downloadList(ListElement listElement) {
        return downloadService.downloadList(listElement);
    }

    @Override public void deleteList(ListElement listElement) {
        listsManagementService.delete(listElement);
        Path destPath = Paths.get(WiseConstants.LISTS_PATH, listElement.getId() + LIST_EXTENSION);
        try {
            if (Files.exists(destPath)) {
                Files.delete(destPath);
            }
        } catch (IOException e) {
            LOG.warn("Error deleting list file: " + listElement + " - " + e.getMessage(), e);
        }
    }

    @Override public void addListDialog() {
        new NewListDialog(mainFrame.getFrame(), this, listsManagementService, null);
    }

    @Override public void editListDialog(ListElement listElement) {
        new NewListDialog(mainFrame.getFrame(), this, listsManagementService, listElement);
    }

    @Override public void watch(Station station) {
        try {
            if (station.getIsHost() != null && station.getIsHost()) {
                Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
                if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(new URL(station.getUrl()).toURI());
                }
            } else {
                String[] cmd = new String[] { WiseConfig.get(WiseConstants.PROPERTIE_VIDEOPLAYER, "vlc"),
                        station.getUrl() };
                Runtime.getRuntime().exec(cmd);
            }
        } catch (URISyntaxException | IOException e) {
            LOG.error("Error watching station: " + station + " - " + e.getMessage(), e);
        }
    }

    @Override public ImageIcon loadImage(String imageUrl) throws IOException {
        ImageIcon imageIcon = imageCacheService.getImage(imageUrl);
        if (imageIcon == null) {
            return null;
        }

        int iconWidth = imageIcon.getIconWidth();
        int iconHeight = imageIcon.getIconHeight();
        int newWidth;
        int newHeight;
        if (iconHeight > iconWidth) {
            newHeight = WiseConstants.CELL_HEIGHT;
            newWidth = (int) (iconWidth * ((float) newHeight / (float) iconHeight));
        } else {
            newWidth = WiseConstants.CELL_WIDTH;
            newHeight = (int) (iconHeight * ((float) newWidth / (float) iconWidth));
        }
        return new ImageIcon(imageIcon.getImage().getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH));
    }

    @Override public void aboutDialog() {
        new AboutDialog(mainFrame.getFrame());
    }
}

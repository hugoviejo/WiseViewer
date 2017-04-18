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

import javax.swing.*;

import com.ordus.wiseviewer.domain.Group;
import com.ordus.wiseviewer.domain.Station;
import com.ordus.wiseviewer.service.WiseViewerService;
import com.ordus.wiseviewer.util.WiseConstants;

public class WiseVisualizationPanel extends JPanel {
    private Group group;
    private WiseViewerService wiseService;

    public WiseVisualizationPanel(WiseViewerService wiseService, Group group) {
        this.wiseService = wiseService;
        this.group = group;
        initUI();
        loadData();
    }

    private void initUI() {
        int itemSize = (group.getGroups() != null ? group.getGroups().size() : 0) + (group.getStations() != null ? group
                .getStations().size() : 0);
        int cols = WiseConstants.NUM_COLS;
        int rows = (int) Math.ceil(itemSize / (float) cols);
        setLayout(new GridLayout(rows, cols));
        setSize(cols * WiseConstants.CELL_WIDTH, rows * WiseConstants.CELL_HEIGHT);
    }

    private void loadData() {
        wiseService.launchProcess();
        int itemSize = (group.getGroups() != null ? group.getGroups().size() : 0) + (group.getStations() != null ? group
                .getStations().size() : 0);
        int i = 0;
        wiseService.updateProgress(i, itemSize);
        for (final Group currentGroup : group.getGroups()) {
            i++;
            if(currentGroup != null) {
                add(new GroupPanel(wiseService, currentGroup));
            }
            wiseService.updateProgress(i, itemSize);
        }
        for (final Station currentStation : group.getStations()) {
            i++;
            if (currentStation != null && currentStation.getUrl() != null
                    && currentStation.getUrl().length() > 0) {
                add(new StationPanel(wiseService, currentStation));
            }
            wiseService.updateProgress(i, itemSize);
        }
        wiseService.finishProcess();
    }
}

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

import java.io.IOException;

import javax.swing.*;

import com.ordus.wiseviewer.domain.Group;
import com.ordus.wiseviewer.domain.ListElement;
import com.ordus.wiseviewer.domain.Station;
import com.ordus.wiseviewer.domain.WiseList;
import com.ordus.wiseviewer.ui.MainFrame;

public interface WiseViewerService {
    String LIST_EXTENSION = ".wise";

    void setMainFrame(MainFrame mainFrame);

    void changePanel(JPanel panel);

    void launchProcess();

    void updateProgress(int current, int total);

    void finishProcess();

    void setProgressBar(JProgressBar progressBar);

    void loadListsPanel();

    void loadWiseVisualizacionPanel(Group group);

    void loadList(ListElement listElement);

    WiseList downloadList(ListElement listElement);

    void deleteList(ListElement listElement);

    void addListDialog();

    void editListDialog(ListElement listElement);

    void watch(Station station);

    Icon loadImage(String imageUrl) throws IOException;

    void aboutDialog();
}

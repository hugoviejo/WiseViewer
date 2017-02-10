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

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ordus.wiseviewer.domain.ListElement;
import com.ordus.wiseviewer.parser.ListsParser;
import com.ordus.wiseviewer.util.WiseConstants;

public class ListsManagementServiceImpl implements ListsManagementService {
    private static Logger LOG = LoggerFactory.getLogger(ListsManagementServiceImpl.class);
    protected static final String LISTS_FILE = "lists.json";

    private ListsParser listsParser;

    public ListsManagementServiceImpl() {
        listsParser = new ListsParser();
    }

    private List<ListElement> fullList;

    @Override public boolean add(ListElement listElement) {
        fullList.add(listElement);
        return store();
    }

    @Override public void delete(ListElement listElement) {
        fullList.remove(listElement);
        store();
    }

    @Override public List<ListElement> load() {
        if (fullList == null) {
            List<ListElement> listsRet = new ArrayList<>();
            Path listsFile = Paths.get(WiseConstants.CONFIG_PATH, LISTS_FILE);
            if (Files.exists(listsFile)) {
                try {
                    listsRet = listsParser.parse(Files.newInputStream(listsFile));
                } catch (IOException e) {
                    LOG.error("Error reading lists file: " + e.getMessage(), e);
                }
            }
            fullList = listsRet;
        }
        return fullList;
    }

    @Override public boolean store(List<ListElement> lists) {
        String listsJson = listsParser.serialize(lists);

        Path listsFile = Paths.get(WiseConstants.CONFIG_PATH, LISTS_FILE);
        boolean isOk = false;
        try {
            BufferedWriter bufferedWriter = Files.newBufferedWriter(listsFile, WiseConstants.DEFAULT_CHARSET);
            bufferedWriter.write(listsJson);
            bufferedWriter.close();
            isOk = true;
        } catch (IOException e) {
            LOG.error("Error storing lists file: " + e.getMessage(), e);
        }
        return isOk;
    }

    @Override public boolean store() {
        return store(fullList);
    }
}

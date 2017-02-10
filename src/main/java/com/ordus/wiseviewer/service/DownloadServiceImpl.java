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
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import javax.swing.*;

import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ordus.wiseviewer.domain.ListElement;
import com.ordus.wiseviewer.domain.WiseList;
import com.ordus.wiseviewer.parser.WiseListParser;
import com.ordus.wiseviewer.util.WiseConstants;

public class DownloadServiceImpl implements DownloadService {
    private static Logger LOG = LoggerFactory.getLogger(DownloadServiceImpl.class);

    private static final String TEMPFILE_PREFIX = "WiseViewer_list_";
    private static final String TEMPFILE_SUFFIX = "";
    private static final String PASTEBIN_URL = "http://pastebin.com/";
    private static final String PASTEBIN_RAW_PATH = "raw/";

    private CloseableHttpClient httpClient;
    private RequestConfig requestConfig;
    private WiseListParser wiseListParser;

    public DownloadServiceImpl(WiseListParser wiseListParser) {
        this.wiseListParser = wiseListParser;
        this.httpClient = HttpClients.createDefault();
        this.requestConfig = RequestConfig.custom()
                .setSocketTimeout(WiseConstants.HTTP_TIMEOUT)
                .setConnectTimeout(WiseConstants.HTTP_TIMEOUT)
                .build();
    }

    @Override public WiseList downloadList(ListElement listElement) {
        WiseList listRet = null;
        try {
            Path listsDirectory = Paths.get(WiseConstants.LISTS_PATH);
            if (!Files.exists(listsDirectory)) {
                Files.createDirectories(listsDirectory);
            }

            CloseableHttpResponse closeableHttpResponse = null;
            OutputStream outputStream = null;
            try {
                String url = listElement.getUrl();
                if (url.startsWith(PASTEBIN_URL)) {
                    if (!url.substring(PASTEBIN_URL.length()).startsWith(PASTEBIN_RAW_PATH)) {
                        url = PASTEBIN_URL + PASTEBIN_RAW_PATH + url.substring(PASTEBIN_URL.length());
                    }
                }
                HttpGet request = new HttpGet(url);
                request.setConfig(requestConfig);
                closeableHttpResponse = httpClient.execute(request);

                if (closeableHttpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    LOG.error("ERROR: couldn't open URL: " + listElement.getUrl() + " - SatusCode: "
                            + closeableHttpResponse
                            .getStatusLine());
                    return listRet;
                }
                //Check if file is already downloaded
                //File destFile = new File(downloadPath + File.separator + relativePath);

                Path tempPath = Files
                        .createTempFile(Paths.get(System.getProperty(WiseConstants.JAVA_IO_TMPDIR)), TEMPFILE_PREFIX,
                                TEMPFILE_SUFFIX);
                LOG.info("Downloading " + listElement.getUrl());

                outputStream = Files.newOutputStream(tempPath);
                //TimeoutChecker timeoutChecker = new TimeoutChecker(closeableHttpResponse);
                closeableHttpResponse.getEntity().writeTo(outputStream);
                outputStream.flush();
                //timeoutChecker.finish();
                InputStream inputStream = Files.newInputStream(tempPath);
                WiseList wiseList = wiseListParser.parse(inputStream);
                inputStream.close();
                if (wiseList.getName() != null && wiseList.getName().length() > 0) {
                    listRet = wiseList;
                    Path destPath = Paths
                            .get(WiseConstants.LISTS_PATH, listElement.getId() + WiseViewerService.LIST_EXTENSION);
                    if (Files.exists(destPath)) {
                        Files.delete(destPath);
                    }
                    Files.move(tempPath, destPath);
                    listElement.setName(wiseList.getName());
                    listElement.setLastDownload(new Date());
                }
            } catch (Throwable e) {
                //Catching Throwable to try to not to lose control of the process on an unexpected situation
                LOG.error("ERROR: couldn't open URL: " + e.getMessage(), e);
                JOptionPane.showMessageDialog(null, "Error downloading list from: " + listElement
                        .getUrl());
            } finally {
                if (closeableHttpResponse != null) {
                    try {
                        closeableHttpResponse.close();
                    } catch (IOException e) {
                        //Do nothing
                    }
                }
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        //Do nothing
                    }
                }
            }

        } catch (IOException e) {
            LOG.error("Error downloading list: " + e.getMessage(), e);
        }
        return listRet;
    }

}

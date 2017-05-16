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
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonSyntaxException;
import com.ordus.wiseviewer.domain.CacheElement;
import com.ordus.wiseviewer.parser.CacheParser;
import com.ordus.wiseviewer.util.WiseConstants;

public class ImageCacheServiceImpl implements ImageCacheService {
    private static Logger LOG = LoggerFactory.getLogger(ImageCacheServiceImpl.class);

    private static final String CACHE_FILE = "./conf/cache.json";
    private static final String CACHE_DIR = "./cache";

    private static final String TEMPFILE_PREFIX = "WiseViewer_file_";

    private static Map<String, ImageIcon> resourceCache = new HashMap<>();

    private CacheParser cacheParser;
    private Map<String, CacheElement> cachedMap;
    private CloseableHttpClient httpClient;
    private RequestConfig requestConfig;

    public ImageCacheServiceImpl() {
        cacheParser = new CacheParser();
        this.httpClient = HttpClients.createDefault();
        this.requestConfig = RequestConfig.custom()
                .setSocketTimeout(WiseConstants.HTTP_TIMEOUT)
                .setConnectTimeout(WiseConstants.HTTP_TIMEOUT)
                .build();
        cachedMap = loadCache();
    }

    @Override public ImageIcon getImage(String url, boolean isResource) {
        ImageIcon imageIconRet = null;
        if(isResource) {
            imageIconRet = resourceCache.get(url);
            if(imageIconRet == null) {
                InputStream inputStream = null;
                try {
                    inputStream = this.getClass().getResourceAsStream(url);
                    imageIconRet = new ImageIcon(ImageIO.read(inputStream));
                    resourceCache.put(url, imageIconRet);
                } catch (IOException e) {
                    LOG.error("Error loading resource image: " + e.getMessage(), e);
                }
            }
        } else {
            CacheElement cacheElement = cachedMap.get(url);
            boolean needsDownload = false;
            if (cacheElement != null) {
                if (System.currentTimeMillis() - cacheElement.getDownloadedDate().getTime()
                        < WiseConstants.FILE_CACHED_MILLISECONDS) {
                    InputStream inputStream = null;
                    try {
                        inputStream = Files.newInputStream(Paths.get(CACHE_DIR, cacheElement.getFile()));
                        imageIconRet = new ImageIcon(ImageIO.read(inputStream));
                        cacheElement.setLastAccessDate(new Date());
                    } catch (IOException e) {
                        LOG.error("Error loading image: " + e.getMessage(), e);
                        needsDownload = true;
                    }
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            //do nothing
                        }
                    }
                } else {
                    needsDownload = true;
                }

            } else {
                needsDownload = true;
            }

            if (needsDownload) {
                imageIconRet = download(url);
                //if (imageIconRet == null) {
                //TODO if where cached and fails download return cached one
                //}
            }
        }
        return imageIconRet;
    }

    @Override
    public boolean store() {
        String cacheJson = cacheParser.serialize(cachedMap);

        Path cacheFile = Paths.get(CACHE_FILE);
        boolean isOk = false;
        try {
            BufferedWriter bufferedWriter = Files.newBufferedWriter(cacheFile, WiseConstants.DEFAULT_CHARSET);
            bufferedWriter.write(cacheJson);
            bufferedWriter.close();
            isOk = true;
        } catch (IOException e) {
            LOG.error("Error storing lists file: " + e.getMessage(), e);
        }
        return isOk;
    }

    private Map<String, CacheElement> loadCache() {
        Map<String, CacheElement> mapRet = new HashMap<>();
        Path listsFile = Paths.get(CACHE_FILE);
        if (Files.exists(listsFile)) {
            try {
                mapRet = cacheParser.parse(Files.newInputStream(listsFile));
            } catch (IOException | JsonSyntaxException e) {
                LOG.error("Error reading cache file: " + e.getMessage(), e);
            }
        }
        cleanCache(mapRet);
        return mapRet;
    }

    private ImageIcon download(String url) {
        ImageIcon imageIconRet = null;
        try {
            Path cacheDirectory = Paths.get(CACHE_DIR);
            if (!Files.exists(cacheDirectory)) {
                Files.createDirectories(cacheDirectory);
            }

            CloseableHttpResponse closeableHttpResponse = null;
            OutputStream outputStream = null;
            try {
                HttpGet request = new HttpGet(url);
                request.setConfig(requestConfig);
                closeableHttpResponse = httpClient.execute(request);

                if (closeableHttpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    LOG.error("ERROR: couldn't open URL: " + url + " - SatusCode: "
                            + closeableHttpResponse
                            .getStatusLine());
                    return imageIconRet;
                }
                //Check if file is already downloaded
                //File destFile = new File(downloadPath + File.separator + relativePath);
                String extension = null;
                try {
                    extension = url.substring(url.lastIndexOf("."));
                } catch (IndexOutOfBoundsException io) {
                    //Do nothing
                }

                if (extension == null || extension.length() <= 0 || extension.length() > 4) {
                    extension = ".png";
                }
                Path tempPath = Files.createTempFile(Paths.get(System.getProperty(WiseConstants.JAVA_IO_TMPDIR)),
                        TEMPFILE_PREFIX,
                        extension);
                LOG.info("Downloading " + url);

                outputStream = Files.newOutputStream(tempPath);
                //TimeoutChecker timeoutChecker = new TimeoutChecker(closeableHttpResponse);
                closeableHttpResponse.getEntity().writeTo(outputStream);
                //timeoutChecker.finish();
                InputStream inputStream = Files.newInputStream(tempPath);

                ImageIcon imageIconTemp = null;
                try {
                    imageIconTemp = new ImageIcon(ImageIO.read(inputStream));
                } catch (IOException e) {
                    LOG.error("Error loading image: " + e.getMessage(), e);
                }
                inputStream.close();
                if (imageIconTemp != null) {
                    imageIconRet = imageIconTemp;
                    CacheElement cacheElement = new CacheElement();
                    cacheElement.setUrl(url);
                    cacheElement.setFile(UUID.randomUUID().toString() + extension);
                    cacheElement.setDownloadedDate(new Date());
                    cacheElement.setLastAccessDate(new Date());
                    Path destPath = Paths.get(CACHE_DIR, cacheElement.getFile());
                    if (Files.exists(destPath)) {
                        Files.delete(destPath);
                    }
                    Files.move(tempPath, destPath);
                    cachedMap.put(url, cacheElement);
                }
            } catch (Throwable e) {
                //Catching Throwable to try to not to lose control of the process on an unexpected situation
                LOG.error("ERROR: couldn't open URL: " + url + " - " + e.getMessage(), e);
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
        return imageIconRet;
    }

    private void cleanCache(Map<String, CacheElement> cachedMap) {
        for (Iterator<Map.Entry<String, CacheElement>> it = cachedMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, CacheElement> currentEntry = it.next();
            CacheElement currentElement = currentEntry.getValue();
            if (System.currentTimeMillis() - currentElement.getLastAccessDate().getTime() > WiseConstants
                    .FILE_CACHED_MILLISECONDS) {
                try {
                    Path destPath = Paths.get(CACHE_DIR, currentElement.getFile());
                    if (Files.exists(destPath)) {
                        Files.delete(destPath);
                    }
                    it.remove();
                } catch (IOException e) {
                    LOG.error("Error deleting cached element: " + currentElement + " - " + e.getMessage(), e);
                }
            }
        }
    }
}

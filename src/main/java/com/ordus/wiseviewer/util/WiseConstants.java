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

package com.ordus.wiseviewer.util;

import java.nio.charset.Charset;

public final class WiseConstants {

    private WiseConstants() {
    }

    public static final int CELL_WIDTH = 300;

    public static final int CELL_HEIGHT = 250;
    public static final int CELL_HEIGHT_MARGIN = 50;
    public static final int NUM_COLS = 4;
    public static final int NUM_COLS_MAIN = 3;
    public static final int HTTP_TIMEOUT = 2 * 60 * 1000;

    public static final long LIST_CACHED_MILLISECONDS = 1000L * 60L * 60L * 24L;
    public static final long FILE_CACHED_MILLISECONDS = 1000L * 60L * 60L * 24L * 30L;

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF8");

    public static final String JAVA_IO_TMPDIR = "java.io.tmpdir";

    public static final String CONFIG_PATH = "./conf";
    public static final String LISTS_PATH = "./lists";

    public static final String PROPERTIE_VIDEOPLAYER = "watch.videoplayer";

    public static final String DOWNLOADING_IMAGE = "/img/downloading.png";
    public static final String DEFAULT_STATION_IMAGE = "/img/play.png";
    public static final String DEFAULT_GROUP_IMAGE = "/img/folder.png";

}

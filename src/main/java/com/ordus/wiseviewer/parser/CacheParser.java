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

package com.ordus.wiseviewer.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.ordus.wiseviewer.domain.CacheElement;
import com.ordus.wiseviewer.util.WiseConstants;

public class CacheParser {
    private Gson gson = new GsonBuilder().create();

    public Map<String, CacheElement> parse(InputStream inputStream) throws IOException, JsonSyntaxException {
        Type mapType = new TypeToken<Map<String, CacheElement>>() {
        }.getType();
        Reader reader = new InputStreamReader(inputStream, WiseConstants.DEFAULT_CHARSET);
        return gson.fromJson(reader, mapType);
    }

    public String serialize(Map<String, CacheElement> cachedElements) {
        return gson.toJson(cachedElements);
    }
}

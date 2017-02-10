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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.ordus.wiseviewer.domain.CacheElement;

public class CacheParserTest {

    @Test
    public void testParse() throws Exception {
        CacheParser cacheParser = new CacheParser();
        Map<String, CacheElement> cacheMap = cacheParser.parse(WiseListParserTest.class.getResourceAsStream
                ("/test_cache.json"));
        Assert.assertNotNull("JSON not parsed", cacheMap);

    }

    @Test
    public void testSerialize() throws Exception {
        Map<String, CacheElement> cacheElementMap = new HashMap<>();
        CacheElement cacheElement = new CacheElement();
        cacheElement.setUrl("url");
        cacheElement.setFile("file");
        cacheElement.setDownloadedDate(new Date());
        cacheElementMap.put(cacheElement.getUrl(), cacheElement);
        CacheParser cacheParser = new CacheParser();
        String serialize = cacheParser.serialize(cacheElementMap);
        Assert.assertNotNull("Cannot serialize cache", serialize);
    }
}
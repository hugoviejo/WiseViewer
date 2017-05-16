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

package com.ordus.wiseviewer.domain;

import java.util.HashMap;
import java.util.Map;

public class Station {

    private String name;
    private String image;
    private String url;
    private String referer;
    private String subtitle;
    private String info;
    private Boolean isHost;
    private Boolean parental;
    private Map<String, Object> additionalProperties = new HashMap<>();

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The image
     */
    public String getImage() {
        return image;
    }

    /**
     * @param image The image
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * @return The url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    /**
     * @return The isHost
     */
    public Boolean getIsHost() {
        return isHost;
    }

    /**
     * @param isHost The isHost
     */
    public void setIsHost(Boolean isHost) {
        this.isHost = isHost;
    }

    public Boolean getParental() {
        return parental;
    }

    public void setParental(Boolean parental) {
        this.parental = parental;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override public String toString() {
        return "Station{" +
                "name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", url='" + url + '\'' +
                ", referer='" + referer + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", info='" + info +'\'' +
                ", isHost=" + isHost +
                ", parental=" + parental +
                ", additionalProperties=" + additionalProperties +
                '}';
    }
}

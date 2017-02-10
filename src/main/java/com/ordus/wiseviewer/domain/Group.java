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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Group {

    private String name;
    private String image;
    private Boolean parental;
    private List<Group> groups = new ArrayList<>();
    private List<Station> stations = new ArrayList<>();
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

    public Boolean getParental() {
        return parental;
    }

    public void setParental(Boolean parental) {
        this.parental = parental;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    /**
     * @return The stations
     */
    public List<Station> getStations() {
        return stations;
    }

    /**
     * @param stations The stations
     */
    public void setStations(List<Station> stations) {
        this.stations = stations;
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
        return "Group{" +
                "name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", parental=" + parental +
                ", groups=" + groups +
                ", stations=" + stations +
                ", additionalProperties=" + additionalProperties +
                '}';
    }
}

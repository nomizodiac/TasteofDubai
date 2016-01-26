package org.progos.tasteofdubaicms.model;

import java.io.Serializable;

/**
 * Created by NomBhatti on 1/18/2016.
 */
public class VenueMap implements Serializable {

    String id;
    String mapTitle;

    public VenueMap(String id, String mapTitle) {
        this.id = id;
        this.mapTitle = mapTitle;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMapTitle() {
        return mapTitle;
    }

    public void setMapTitle(String mapTitle) {
        this.mapTitle = mapTitle;
    }
}

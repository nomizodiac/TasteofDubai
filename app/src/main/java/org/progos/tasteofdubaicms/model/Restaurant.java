package org.progos.tasteofdubaicms.model;

import java.io.Serializable;

/**
 * Created by NomBhatti on 11/30/2015.
 */
public class Restaurant implements Serializable {

    String id;
    String imgUrl;
    String hasCat;

    public Restaurant(String id, String imgUrl, String hasCat) {
        this.id = id;
        this.imgUrl = imgUrl;
        this.hasCat = hasCat;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getHasCat() {
        return hasCat;
    }

    public void setHasCat(String hasCat) {
        this.hasCat = hasCat;
    }
}

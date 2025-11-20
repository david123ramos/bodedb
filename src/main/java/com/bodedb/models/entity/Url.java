package com.bodedb.models.entity;

import com.bodedb.infra.persistance.entity.Entry;

public class Url implements Entry<String, UrlInfo> {

    private String key;
    private UrlInfo info;

    public Url(String key, UrlInfo info) {
        this.key = key;
        this.info = info;
    }


    @Override
    public String getkey() {
        return this.key;
    }

    @Override
    public UrlInfo getValue() {
        return this.info;
    }
}

package com.davdb.services;

import com.davdb.infra.db.impl.DavDBImpl;
import com.davdb.infra.persistance.serialization.Serializer;
import com.davdb.models.dto.UrlEntryDTO;
import com.davdb.models.entity.UrlInfo;
import org.springframework.stereotype.Service;

@Service
public class UrlService {

    private final DavDBImpl<String, UrlInfo> davDB;

    UrlService(Serializer<String> keySerializer, Serializer<UrlInfo> urlInfoSerializer) {
       this.davDB = new DavDBImpl<>(keySerializer, urlInfoSerializer);
    }

    public void saveUrlClick(UrlEntryDTO entry) {
        System.out.println("[INFO] Received: "+entry);
        this.davDB.put(entry.getUrl(), UrlInfo.from(entry));
    }

}

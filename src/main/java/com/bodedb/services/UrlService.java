package com.bodedb.services;

import com.bodedb.infra.db.impl.BodeDBImpl;
import com.bodedb.infra.persistance.serialization.Serializer;
import com.bodedb.models.dto.UrlEntryDTO;
import com.bodedb.models.entity.UrlInfo;
import org.springframework.stereotype.Service;

@Service
public class UrlService {

    private final BodeDBImpl<String, UrlInfo> bodeDB;

    UrlService(Serializer<String> keySerializer, Serializer<UrlInfo> urlInfoSerializer) {
       this.bodeDB = new BodeDBImpl<>(keySerializer, urlInfoSerializer);
    }

    public void saveUrlClick(UrlEntryDTO entry) {
        System.out.println("[INFO] Received: "+entry);
        this.bodeDB.put(entry.getUrl(), UrlInfo.from(entry));
    }

}

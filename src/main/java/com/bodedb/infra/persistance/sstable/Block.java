package com.bodedb.infra.persistance.sstable;

import java.util.List;
import java.util.zip.CRC32;

public class Block<K, V> {
    List<Record<K, V>> records;
    CRC32 checksum;

    public List<Record<K, V>> getRecords() {
        return records;
    }

    public void setRecords(List<Record<K, V>> records) {
        this.records = records;
    }

    public CRC32 getChecksum() {
        return checksum;
    }

    public void setChecksum(CRC32 checksum) {
        this.checksum = checksum;
    }
}

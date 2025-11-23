package com.bodedb.infra.persistance.sstable;

import com.bodedb.infra.persistance.serialization.Serializer;

import java.util.Map;
import java.util.SortedMap;

public class StringSortedTable<K, V> {

    private Header header;
    private Data<K,V> data;
    private Footer footer;

    private final SortedMap<K,V> map;
    private final Serializer<K> keySerializer;
    private final Serializer<V> valueSerializer;

    public StringSortedTable(SortedMap<K, V> map, Serializer<K> keySerializer, Serializer<V> valueSerializer) {
        this.map = map;
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
    }


    void write() {
        for (Map.Entry<K,V> record : this.map.entrySet()) {

        }
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Data<K, V> getData() {
        return data;
    }

    public void setData(Data<K, V> data) {
        this.data = data;
    }

    public Footer getFooter() {
        return footer;
    }

    public void setFooter(Footer footer) {
        this.footer = footer;
    }
}

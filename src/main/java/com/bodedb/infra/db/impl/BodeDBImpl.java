package com.bodedb.infra.db.impl;

import com.bodedb.infra.db.BodeDB;
import com.bodedb.infra.persistance.Memtable;
import com.bodedb.infra.persistance.SSTableReader;
import com.bodedb.infra.persistance.serialization.Serializer;

import java.util.concurrent.ExecutionException;


public class BodeDBImpl<K extends Comparable<K>, V> implements BodeDB<K, V> {

    private Memtable<K, V> memtable;
    private SSTableReader<K, V> chunkReader;


    public BodeDBImpl(Serializer<K> keySerializer, Serializer<V> valueSerializer) {
        this.memtable = new Memtable<>(keySerializer, valueSerializer);
        this.chunkReader = new SSTableReader<>(keySerializer, valueSerializer);
    }

    @Override
    public V put(K key, V value) {

        try {
            return memtable.insert(key, value);
        }catch (InterruptedException | ExecutionException e) {
            System.out.println("[BodeDB] An error was happen when trying to wait record being saving on WAL ");
        }

        return null;
    }

    @Override
    public V delete(K key) {
        return null;
    }

    @Override
    public V search(K key) {
        return null;
    }
}

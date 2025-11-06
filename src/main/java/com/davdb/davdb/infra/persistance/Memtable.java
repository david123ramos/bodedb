package com.davdb.davdb.infra.persistance;

import com.davdb.davdb.infra.persistance.entity.Entry;
import com.davdb.davdb.infra.persistance.serialization.Serializer;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class Memtable<K, V> {

    private TreeMap<K, V> table = new TreeMap<>();
    private final Integer MEMTABLE_SIZE_LIMIT = 1000;

    Serializer<K> keySerializer;
    Serializer<V> valueSerializer;
    SSTableReader<K,V> tableReader;
    AtomicBoolean frozen = new AtomicBoolean(false);


    public Memtable(Serializer<K> keySerializer, Serializer<V> valueSerializer) {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        this.tableReader = new SSTableReader<>(keySerializer, valueSerializer);
    }

    public SortedMap<K,V> readLast() {
        return Collections.unmodifiableSortedMap(this.tableReader.readMostRecent().getMap());
    }

    public V insert(Entry<K, V> entry) throws Exception {

        if(this.frozen.get()) throw new Exception("[SStable] Attempting to write to memtable while it`s frozen");

        V result = table.put(entry.getkey(), entry.getValue());

        if(table.size() >= MEMTABLE_SIZE_LIMIT) {
            System.out.println("[MEMTABLE] Limit reached! Flushing data");
            rotate();
        }

        return result;
    }

    public void printdata() {
        System.out.println("[MEMTABLE] start printing data...");

        int line = 1;
        for(K key : this.table.keySet()) {
            System.out.println("[MEMTABLE] {"+line+"} "+key+": "+ this.table.get(key));
            line++;
        }

        System.out.println("[MEMTABLE] end printing data...");
    }

    public void rotate() {
        freezeToggle();
        SortedMap<K,V> memtableToFlush = Collections.unmodifiableSortedMap(table);
        flush(memtableToFlush);
        table = new TreeMap<>();
        freezeToggle();
    }

    private void freezeToggle() {
        boolean prev;
        do {
            prev = frozen.get();
        } while (!frozen.compareAndSet(prev, !prev));
    }

    private void flush(SortedMap<K,V> table) {

        Runnable writeToDisk = new Runnable() {
            @Override
            public void run() {
                new SStable<>(table, keySerializer, valueSerializer).writeToFile();
            }
        };

       Thread.ofVirtual().start(writeToDisk);
    }
}
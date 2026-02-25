package com.bodedb.infra.persistance;

import com.bodedb.infra.persistance.serialization.Serializer;

import java.io.IOException;
import java.util.Collections;
import java.util.SortedMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

//Epoch-based reclamation
class ThreadedTable<K, V> {
    public final ConcurrentSkipListMap<K, V> map = new ConcurrentSkipListMap<>();
    public AtomicInteger size = new AtomicInteger(0);
    public AtomicInteger holders = new AtomicInteger(0);
}

public class Memtable<K extends Comparable<K>, V> {

    private AtomicReference<ThreadedTable<K, V>> table = new AtomicReference<>(new ThreadedTable<K, V>());
    private final Integer MEMTABLE_SIZE_LIMIT = 4;
    private final WAL<K, V> WALService;

    Serializer<K> keySerializer;
    Serializer<V> valueSerializer;
    AtomicBoolean rotating = new AtomicBoolean(false);

    boolean isSynchronousCommitActive = Boolean.parseBoolean(System.getenv("BODEDB_SYNCHRONOUS_WAL_COMMIT_ACTIVE"));

    public Memtable(Serializer<K> keySerializer, Serializer<V> valueSerializer) {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;

        System.out.println("[MEMTABLE - WAL persistance type] synchronous commit is "
                + (this.isSynchronousCommitActive ? "active" : "deactivated"));

        try {
            this.WALService = new WAL<>();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("[MEMTABLE] Was not possible to create log file");
        }

    }

    public V insert(K key, V value) throws ExecutionException, InterruptedException {

        CompletableFuture<Boolean> resultWalLine = WALService.write(key, value);

        if (isSynchronousCommitActive)
            resultWalLine.get();

        ThreadedTable<K, V> state;

        while (true) {
            state = this.table.get();

            state.holders.incrementAndGet();

            if (state == this.table.get())
                break;

            state.holders.decrementAndGet();
        }

        try {
            V result = state.map.put(key, value);

            // it's not completely guaranteed that flushed sstable will contain exactly
            // MEMTABLE_SIZE_LIMIT entries.
            // Because incrementAndGet and check of rotating flag can happen concurrently.
            // There`s a race condition.
            // It means that flushed file can contain more entries than the limit defined
            // even if the thread that has inserted
            // into memtable wasn`t the one responsible for calling the rotating function.
            if (result == null && state.size.incrementAndGet() >= MEMTABLE_SIZE_LIMIT) {
                if (rotating.compareAndSet(false, true)) {
                    System.out.println("[MEMTABLE] Limit reached! Flushing data");
                    rotate();
                }
            }

            return result;

        } finally {
            state.holders.decrementAndGet();
        }

    }

    public void rotate() {
        ThreadedTable<K, V> memtableToFlush = table.getAndSet(new ThreadedTable<K, V>());

        flush(memtableToFlush);
    }

    private void flush(ThreadedTable<K, V> table) {

        Runnable writeToDisk = () -> {
            try {

                while (table.holders.get() > 0) {
                    Thread.onSpinWait();
                }

                new SStable<>(Collections.unmodifiableSortedMap(table.map), keySerializer, valueSerializer)
                        .writeToFile();
            } finally {
                rotating.set(false);
            }
        };

        Thread.ofVirtual().start(writeToDisk);
    }
}
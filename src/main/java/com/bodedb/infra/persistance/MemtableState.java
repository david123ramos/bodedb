package com.bodedb.infra.persistance;

import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MemtableState<K, V> {
    public final ConcurrentSkipListMap<K, V> map = new ConcurrentSkipListMap<>();
    public AtomicInteger size = new AtomicInteger(0);
    public AtomicInteger holders = new AtomicInteger(0);
}
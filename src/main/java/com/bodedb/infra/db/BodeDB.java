package com.bodedb.infra.db;

public interface BodeDB<K,V> {

    V put(K key, V value);
    V delete(K key);
    V search(K key);

}

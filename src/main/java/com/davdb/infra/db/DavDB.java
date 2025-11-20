package com.davdb.infra.db;

public interface DavDB<K,V> {

    V put(K key, V value);
    V delete(K key);
    V search(K key);

}

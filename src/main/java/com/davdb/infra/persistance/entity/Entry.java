package com.davdb.infra.persistance.entity;

public interface Entry<K,V> {
   K getkey();
   V getValue();
}

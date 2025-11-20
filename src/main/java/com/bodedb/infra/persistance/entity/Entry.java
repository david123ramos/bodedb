package com.bodedb.infra.persistance.entity;

public interface Entry<K,V> {
   K getkey();
   V getValue();
}

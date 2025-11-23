package com.bodedb.infra.persistance.sstable;

import java.util.ArrayList;
import java.util.List;

public class Data<K,V> {
    List<Block<K, V>> blocks = new ArrayList<>();

    public List<Block<K, V>> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Block<K, V>> blocks) {
        this.blocks = blocks;
    }
}

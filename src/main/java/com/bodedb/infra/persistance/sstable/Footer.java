package com.bodedb.infra.persistance.sstable;

public class Footer {

    Integer indexOffset  = null;
    Integer indexSize = null;
    Integer BloomOffset = null;
    Integer BloomSize = null;
    public static final Integer MAGIC_NUMBER = 0xB0DEBDED;

    public Integer getIndexOffset() {
        return indexOffset;
    }

    public void setIndexOffset(Integer indexOffset) {
        this.indexOffset = indexOffset;
    }

    public Integer getIndexSize() {
        return indexSize;
    }

    public void setIndexSize(Integer indexSize) {
        this.indexSize = indexSize;
    }

    public Integer getBloomOffset() {
        return BloomOffset;
    }

    public void setBloomOffset(Integer bloomOffset) {
        BloomOffset = bloomOffset;
    }

    public Integer getBloomSize() {
        return BloomSize;
    }

    public void setBloomSize(Integer bloomSize) {
        BloomSize = bloomSize;
    }
}

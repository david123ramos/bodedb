package com.davdb.infra.persistance.entity;

import java.util.concurrent.CompletableFuture;

public class WALLine {
    private String line;
    private CompletableFuture<Boolean> ack;

    public WALLine(String line, CompletableFuture<Boolean> ack) {
        this.line = line;
        this.ack = ack;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public CompletableFuture<Boolean> getAck() {
        return ack;
    }

    public void setAck(CompletableFuture<Boolean> ack) {
        this.ack = ack;
    }
}

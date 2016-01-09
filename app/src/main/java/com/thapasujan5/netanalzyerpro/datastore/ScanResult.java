package com.thapasujan5.netanalzyerpro.DataStore;

/**
 * Created by Sujan Thapa on 9/01/2016.
 */
public final class ScanResult {
    private final int port;
    private final boolean isOpen;

    public ScanResult(int port, boolean isOpen) {
        this.port = port;
        this.isOpen = isOpen;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public int getPort() {
        return port;
    }
}
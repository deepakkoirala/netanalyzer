package com.thapasujan5.netanalzyerpro.PortScanner;

import android.util.Log;

import com.thapasujan5.netanalzyerpro.DataStore.ScanResult;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Sujan Thapa on 9/01/2016.
 */
public class PortScanner {

    public static ArrayList<ScanResult> scanHost(final String ip) {
        Log.d("Scanning", ip);
        final ExecutorService es = Executors.newFixedThreadPool(20);
        final int timeout = 200;
        final List<Future<ScanResult>> futures = new ArrayList<>();
        for (int port = 1; port <= 65535; port++) {
            futures.add(portIsOpen(es, ip, port, timeout));
        }
        es.shutdown();
        int openPorts = 0;
        ArrayList<ScanResult> scanResults = new ArrayList<ScanResult>();
        for (final Future<ScanResult> f : futures) {
            ScanResult result = null;
            try {
                result = f.get();
            } catch (InterruptedException e) {
            } catch (ExecutionException e) {
            }
            if (result != null) {
                if (result.isOpen())
                    scanResults.add(result);
            }
        }
        return scanResults;
    }

    protected static Future<ScanResult> portIsOpen(final ExecutorService es, final String ip, final int port, final int timeout) {
        return es.submit(new Callable<ScanResult>() {
            @Override
            public ScanResult call() {
                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(ip, port), timeout);
                    socket.close();
                    return new ScanResult(port, true);
                } catch (Exception ex) {
                    return new ScanResult(port, false);
                }
            }
        });
    }
}

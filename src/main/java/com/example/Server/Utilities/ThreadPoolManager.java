package com.example.Server.Utilities;

import java.util.concurrent.*;

public class ThreadPoolManager {
    private final ExecutorService readPool;
    private final ForkJoinPool processPool;
    private final ExecutorService responsePool;
    public ThreadPoolManager(){
        this.readPool = Executors.newFixedThreadPool(10);
        this.processPool = ForkJoinPool.commonPool();
        this.responsePool = Executors.newCachedThreadPool();
    }

    public ExecutorService getReadPool() { return readPool; }
    public ForkJoinPool getProcessPool() { return processPool; }
    public ExecutorService getResponsePool() { return responsePool; }


    public void shutdown(){
        this.readPool.shutdown();
        this.processPool.shutdown();
        this.responsePool.shutdown();
    }

}

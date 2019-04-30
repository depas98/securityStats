package com.depas98.security;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 *  This class will add security data and store security stats information about that data.
 *
 *  Also it provides a method to get the latest stats, the output will be a string with
 *  a format similar to the following:
 *
 *   	    Event Count: 8, Door Count: 2, Image Count: 1, Alarm Count: 5, avgProcessingTime: 10ms
 */
public class SecurityMonitorServiceImpl implements SecurityMonitorService {

    private static volatile SecurityMonitorServiceImpl securityMonitorServiceImpl;

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();

    private final Map<SecurityType, Integer> securityStats = new HashMap<>();

    // The average time it took to process data for each iteration
    // an iteration can process multiple files (so multiple events per iteration)
    private long avgProcessingTimeInMillis;

    // The number of batches processed, batch reads in x number of files and processes them
    private long processCount;

    // Make this class a singleton
    private SecurityMonitorServiceImpl(){}

    public static SecurityMonitorServiceImpl getInstance(){
        if (securityMonitorServiceImpl == null){
            synchronized (SecurityMonitorServiceImpl.class){
                if (securityMonitorServiceImpl == null){
                    securityMonitorServiceImpl = new SecurityMonitorServiceImpl();
                }
            }
        }
        return securityMonitorServiceImpl;
    }

    /**
     * Takes in a list of SecurityTypes and updates the stats
     * with he information
     *
     * @param securityEvents List of Security Types that represent a security event
     * @param newProcessingTime Time it too to collect the security data
     */
    @Override
    public void addSecurityData(final List<SecurityType> securityEvents, final long newProcessingTime){
        if (securityEvents == null){
            throw new IllegalArgumentException("securityData can't be null");
        }

        if (newProcessingTime < 0){
            throw new IllegalArgumentException("newprocessingTime can't be negative");
        }

        writeLock.lock();
        try{
            if (securityEvents.size() > 0){
                avgProcessingTimeInMillis = calculateAvgProcessingTime(avgProcessingTimeInMillis,
                        processCount, newProcessingTime);
                processCount++;

                securityEvents.forEach(s -> {
                    int count = securityStats.getOrDefault(s, 0);
                    securityStats.put(s, ++count);
                });
           }
        }
        finally{
            writeLock.unlock();
        }
    }

    /**
     * Return stats information with the following format:
     * 	    Event Count: 8, Door Count: 2, Image Count: 1, Alarm Count: 5, avgProcessingTime: 10ms
     *
     * @return stats information
     */
    @Override
    public String getStats(){
        readLock.lock();
        try{
            String stats = securityStats.entrySet()
                    .stream()
                    .map(e -> e.getKey().getName() + ": " + e.getValue())
                    .collect(Collectors.joining(", " ));

            stats = stats.isBlank() ? stats :  ", " + stats;
            return "Event Count: " + getEventCount() + stats + ", avgProcessingTime: " + avgProcessingTimeInMillis;
        }
        finally{
            readLock.unlock();
        }
    }

    @Override
    public void clearStats() {
        writeLock.lock();
        try{
            securityStats.clear();
            avgProcessingTimeInMillis=0;
            processCount=0;
        }
        finally {
            writeLock.unlock();
        }
    }

    private int getEventCount(){
        return securityStats.values().stream()
                .mapToInt(count -> count)
                .sum();
    }

    private long calculateAvgProcessingTime(final long avgProcessingTime, final long count, final long newProcessingTime){
        long totalTime = count * avgProcessingTime;
        totalTime += newProcessingTime;
        return Math.round((double)totalTime / (count + 1));
    }

}

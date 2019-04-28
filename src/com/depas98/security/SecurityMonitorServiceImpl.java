package com.depas98.security;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class SecurityMonitorServiceImpl implements SecurityMonitorService {

    private static volatile SecurityMonitorServiceImpl securityMonitorServiceImpl;

    private final ConcurrentHashMap<SecurityType, Integer> securityStats = new ConcurrentHashMap<>();
    private final AtomicLong avgProcessingTimeInMillis = new AtomicLong(0);

    // The number of batches processed, batch reads in x number of files and processes them
    private final AtomicLong processCount = new AtomicLong(0);

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

    @Override
    public void addSecurityData(final List<SecurityType> securityData, final long processingTime){
        if (securityData == null){
            throw new IllegalArgumentException("securityData can't be null");
        }

        if (processingTime < 0){
            throw new IllegalArgumentException("processingTime can't be negative");
        }

//        writeLock.lock();
//        try{
            if (securityData.size() > 0){
//                long currentTime = System.currentTimeMillis();
                updateAvgProcessingTime(processingTime);

                securityData.forEach(s -> {
                    int count = securityStats.getOrDefault(s, 0);
                    securityStats.put(s, ++count);
                });

//                int newProcessingTime = processingTime + (int) (System.currentTimeMillis() - currentTime);
//                System.out.println("processingTime: " + processingTime + " newProcessingTime: " + newProcessingTime);
            }
//        }
//        finally{
//            writeLock.unlock();
//        }

    }

    @Override
    public String getStats(){
//        readLock.lock();
//        try{
            String stats = securityStats.entrySet()
                    .stream()
                    .map(e -> e.getKey().getName() + ": " + e.getValue())
                    .collect(Collectors.joining(", " ));

            stats = stats.isBlank() ? stats :  ", " + stats;
            return "Event Count: " + getEventCount() + stats + ", avgProcessingTime: " + avgProcessingTimeInMillis;
//        }
//        finally{
//            readLock.unlock();
//        }
    }

    @Override
    public void clearStats() {
        securityStats.clear();
        avgProcessingTimeInMillis.set(0);
        processCount.set(0);
    }

    private int getEventCount(){
        return securityStats.values().stream()
                .mapToInt(count -> count)
                .sum();
    }

    private void updateAvgProcessingTime(final long newProcessingTime){
        long totalTime = processCount.get() * avgProcessingTimeInMillis.get();
        totalTime += newProcessingTime;
        processCount.getAndIncrement();
        long count =  processCount.get();
//        System.out.println("totalTime: " + totalTime + " processCount: " + count);
        long avgProcTime = Math.round((double)totalTime / count);
        avgProcessingTimeInMillis.set(avgProcTime);
    }

//    private void updateAvgProcessingTime(final long newProcessingTime, long newEventCount){
//        long eventCount = getEventCount();
//        long totalTime = eventCount * avgProcessingTimeInMillis;
//        totalTime += newProcessingTime;
//        eventCount += newEventCount;
//        System.out.println("totalTime: " + totalTime + " eventCount: " + eventCount);
//        avgProcessingTimeInMillis = totalTime / eventCount;
//    }
}

package com.depas98.security;

import java.util.Scanner;
import java.util.Timer;

/**
 * This class can start and stop two tasks that will be scheduled to run every second.
 * One task is {@link SecurityFileReaderTask}, it will read and process recent files
 * that have the following format:
 *
 *      {"Type":"Door", "Date":"2017-02-01 10:01:02", "open": true}

 * The other task is {@link SecurityStatsWriterTask}, this will output the status information
 * from the data read from the files processed in the other {@link SecurityFileReaderTask}.
 * The output will have the following format:
 *
 * 	    Event Count: 8, Door Count: 2, Image Count: 1, Alarm Count: 5, avgProcessingTime: 10ms
 *
 */
public class SecurityMonitor {

    private final SecurityFileReaderTask fileReaderTask;
    private final SecurityStatsWriterTask statsWriterTask;
    private final Timer fileReaderTimer;
    private final Timer statsWriterTimer;


    public SecurityMonitor(){
        this.fileReaderTask = new SecurityFileReaderTask(SecurityMonitorServiceImpl.getInstance());
        this.statsWriterTask = new SecurityStatsWriterTask(SecurityMonitorServiceImpl.getInstance());
        this.fileReaderTimer = new Timer();
        this.statsWriterTimer = new Timer();
    }

    private void startSecurityFileReader(){
        fileReaderTimer.scheduleAtFixedRate(fileReaderTask, 0, 1000); // run every one second
    }

    private void stopSecurityReader(){
        fileReaderTimer.cancel();
    }

    private void startSecurityStatsWriter(){
        statsWriterTimer.scheduleAtFixedRate(statsWriterTask, 0, 1000); // run every one second
    }

    private void stopSecurityStatsWriter(){
        statsWriterTimer.cancel();
    }

    /**
     * This will start the two task File Reader and Stats Writer
     */
    public void startUp(){
        startSecurityFileReader();
        startSecurityStatsWriter();
    }

    /**
     * This will stop the two task File Reader and Stats Writer
     */
    public void shutDown(){
        stopSecurityReader();
        stopSecurityStatsWriter();
    }

    public static void main(String[] args) {

        String inputVal = "";
        Scanner scan = new Scanner(System.in);

        // Create and startup the monitor
        SecurityMonitor securityMonitor = new SecurityMonitor();
        securityMonitor.startUp();

        while (!inputVal.equalsIgnoreCase("q")
                && !inputVal.equalsIgnoreCase("quit")){
            // get user input and exit if "q" or "quit"
            inputVal = scan.next();
        }

        // shutdown the monitor
        securityMonitor.shutDown();

    }
}

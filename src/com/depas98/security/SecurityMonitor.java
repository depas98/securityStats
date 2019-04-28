package com.depas98.security;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.Timer;

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

    public void startUp(){
        startSecurityFileReader();
        startSecurityStatsWriter();
    }

    public void shutDown(){
        stopSecurityReader();
        stopSecurityStatsWriter();
    }

    public static void main(String[] args) {
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        System.out.println("Current relative path is: " + s);

        String inputVal = "";
        Scanner scan = new Scanner(System.in);

        SecurityMonitor securityMonitor = new SecurityMonitor();
        securityMonitor.startUp();

        while (!inputVal.equalsIgnoreCase("q")
                && !inputVal.equalsIgnoreCase("quit")){
            System.out.println("working...");
            inputVal = scan.next();
        }

        securityMonitor.shutDown();

    }
}

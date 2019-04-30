package com.depas98.security;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This task class will read and process recent files that have the following format:
 *
 *      {"Type":"Door", "Date":"2017-02-01 10:01:02", "open": true}
 */
public class SecurityFileReaderTask extends TimerTask {

    private final SecurityMonitorService securityMonitorService;

    // only process files that have a time later than this
    private long lastFileTime;

    public SecurityFileReaderTask(SecurityMonitorService securityMonitorService){
        this.securityMonitorService = securityMonitorService;
    }

    /**
     * This task will get the latest files based on the lastFileTime
     * and parse the file to get the security event types, and save them to a list.
     * It also keeps how long this process took.
     *
     * It will then add the Security Type events to the {@link SecurityMonitorService}
     */
    @Override
    public void run() {

        long startTime = System.currentTimeMillis();
        // get the recent files
        List<Path> paths = getRecentFilePaths(lastFileTime);

        // update the lastFileTIme with the max time of the recent Files
        paths.stream()
                .mapToLong(getLastModifiedTime())
                .max()
                .ifPresent(t -> lastFileTime = t);

        if (paths.size() == 0){
            return;
        }

        // parse the recent files and create a List of Security Types for them and store them in the list below
        List<SecurityType> securityEvents = new ArrayList<>();
        for (Path path:paths) {
            try(Stream<String> linesStream = Files.lines(path)) {   // this will close the file after reading is done
                linesStream
                    .map(SecurityFileReaderTask::getSecurityTypeStrFromLine)
                    .filter(s -> !s.isBlank())
                    .map(SecurityType::getSecurityTypeByID)
                    .filter(st -> st != SecurityType.UNKNOWN)
                    .findAny()
                    .ifPresent(securityEvents::add);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // TODO Would log this as trace
//            System.out.println("Security Events " + securityEvents);

        // calc the processing time
        long processingTime = System.currentTimeMillis() - startTime;

        // add the security events to the security service
        securityMonitorService.addSecurityData(securityEvents, processingTime);
    }

    /**
     * This will parse a string representing a line in a file.  If the line is not the correct format,
     * it will return a empty string otherwise it will return a string representing the security type
     * Format will be similar to the following:
     *
     *      {"Type":"Door", "Date":"2017-02-01 10:01:02", "open": true}
     *      {"Type":"Alarm", "Date":"2017-02-01 10:01:01", "name":"fire", "floor":"1", "Room": "101"}
     *      {"Type":"img", "Date":"2017-02-01 10:01:02", "bytes": "ab39szh6", "size": 8}
     *
     * @param line string representing a line in the file
     * @return Security Type string, or empty string if line was not correct format
     */
    static String getSecurityTypeStrFromLine(String line){
        if (line == null || line.isBlank()){
            // Ignore null and blank lines
            return "";
        }

        try{
            final String[] lineArr = line.split(",");
            if (lineArr.length > 1){
                return lineArr[0].split(":")[1].replace("\"", "").trim();
            }
        }
        catch (Exception e){
            // TODO Log this to a file
            System.out.println("The line value [" + line + "] is not the correct format.");
        }
        return "";
    }

    /**
     * This will return a list of Path (file path) objects, using the current relative path
     * and will filter out file paths that are not more recent than the latestFileTime
     * @param latestFileTime - time that is used to compare against the file path times
     * @return List of Path objects that are more recent than the latestFileTime
     */
    private List<Path> getRecentFilePaths(final long latestFileTime){
        final Path currentRelativePath = Paths.get("");
        final String pathString = currentRelativePath.toAbsolutePath().toString() + File.separator + "files";

        // this will close the resource when done walking the paths
        try(Stream<Path> pathStream = Files.walk(Paths.get(pathString))){
            // get all the recent files
            return pathStream
                    .filter(Files::isRegularFile)
                    .filter(isRecentFile(latestFileTime))
                    .map(Path::toAbsolutePath)
                    .collect(Collectors.toList());
        }
        catch (IOException e) {
            // TODO Log this to a file
            System.out.println("Unable to get the latest files in the directory " + pathString + ": " + e);
        }

        return new ArrayList<>();
    }

    /**
     * Returns a function that will get a files last modified time
     * @return Function for getting files last modified time
     */
    private static ToLongFunction<Path> getLastModifiedTime(){
        return p -> {
            try {
                return Files.getLastModifiedTime(p).toMillis();
            } catch (IOException e) {
                // TODO Log this to a file
                System.out.println("Couldn't get the last modified time for file [" + p.getFileName() + "]: " + e);
            }
            return 0L;
        };
    }

    /**
     * Predicate that test if a file is later than the time given (latestFileTime)
     * @param latestFileTime the time to test if the file was created/modified after that time
     * @return predicate that test if a file is recent
     */
    private static Predicate<Path> isRecentFile(final long latestFileTime){
        return p -> {
            try {
                if (Files.getLastModifiedTime(p).toMillis() > latestFileTime){
                    return true;
                }
            } catch (IOException e) {
                // TODO Log this to a file
                System.out.println("The file [" + p.getFileName() + "] couldn't be read: " + e);
            }
            return false;
        };
    }

}

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

public class SecurityFileReaderTask extends TimerTask {

    private final SecurityMonitorService securityMonitorService;
    private long lastFileTime;

    public SecurityFileReaderTask(SecurityMonitorService securityMonitorService){
        this.securityMonitorService = securityMonitorService;
    }

    @Override
    public void run() {

        try {

            long startTime = System.currentTimeMillis();

            final Path currentRelativePath = Paths.get("");
            final String pathString = currentRelativePath.toAbsolutePath().toString() + File.separator + "files";

            // get the recent files
            List<Path> paths = Files.walk(Paths.get(pathString))
                    .filter(Files::isRegularFile)
                    .filter(isRecentFile(lastFileTime))
                    .map(Path::toAbsolutePath)
                    .collect(Collectors.toList());

            // TODO Would log this as trace
//            System.out.println("SecurityFileReaderTask is running... " + paths);

            // update the lastFileTIme with the max time of the recent Files
            paths.stream()
                    .mapToLong(getLastModifiedTime())
                    .max()
                    .ifPresent(t -> lastFileTime = t);

            if (paths.size() == 0){
                return;
            }

            // parse the recent files and create a List of Security Types for them
            List<SecurityType> securityEvents = new ArrayList<>();
            for (Path path:paths) {
                Files.lines(path)
                    .map(SecurityFileReaderTask::getSecurityTypeStrFromLine)
                    .filter(s -> !s.isBlank())
                    .map(SecurityType::getSecurityTypeByID)
                    .filter(st -> st != SecurityType.UNKNOWN)
                    .findAny()
                    .ifPresent(securityEvents::add);
            }

            // TODO Would log this as trace
//            System.out.println("Security Events " + securityEvents);

            // calc the processing time
            long processingTime = System.currentTimeMillis() - startTime;
            System.out.println("processingTime: " + processingTime);

            // add the security events to the security service
            securityMonitorService.addSecurityData(securityEvents, processingTime);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    private static Predicate<Path> isRecentFile(final long lastFileTime){
        return p -> {
            try {
                if (Files.getLastModifiedTime(p).toMillis() > lastFileTime){
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

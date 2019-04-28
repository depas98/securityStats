package com.depas98.security;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

public class FileGenerator {

    private static final String[] fileText = {"{\"Type\":\"door\", \"Date\":\"2017-02-01 10:01:02\", \"open\": true}",
            "{\"Type\":\"img\", \"Date\":\"2014-02-01 10:01:02\", \"bytes\": \"ab39szh6\", \"size\": 8}",
            "{\"Type\":\"alarm\", \"Date\":\"2014-02-01 10:01:05\", \"name\":\"fire\", \"floor\":\"1\", \"room\":\"101\"}\n"};

    private static final String[] fileNames = {"door_gen", "img_gen", "alarm_gen"};
    private static final String fileExt = ".json";

    // {alarmCount, doorCount, imgCount)
    private static final int[] fileCounts = {0, 0, 0};

    static void generateFile(){
        Path currentRelativePath = Paths.get("");
        String pathString = currentRelativePath.toAbsolutePath().toString() + File.separator + "files";

        Random random = new Random();
        final int num = random.nextInt(3);

        String text = fileText[num];
        String filePath = pathString + File.separator + fileNames[num] + fileCounts[num] + fileExt;

        System.out.println("Generating file: " + filePath);
        System.out.println("with text: " + text);
        System.out.println();
        System.out.println("---------------------");
        System.out.println();

        try {
            Files.write(Paths.get(filePath), text.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileCounts[num] = fileCounts[num] + 1;
    }

    public static void main(String[] args) {

        int fileToCreate = 100;
        for (int i = 0; i <  fileToCreate; i++) {
            generateFile();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}

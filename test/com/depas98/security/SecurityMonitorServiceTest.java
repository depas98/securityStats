package com.depas98.security;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SecurityMonitorServiceTest {

    @Test (expected = IllegalArgumentException.class)
    public void addSecurityDataNullTest(){
        SecurityMonitorService service = SecurityMonitorServiceImpl.getInstance();

        // Test the null argument case
        try {
            service.addSecurityData(null, 0);
        } catch (IllegalArgumentException e) {
            throw e;

        }
    }

    @Test (expected = IllegalArgumentException.class)
    public void addSecurityDataNegProcessTimeTest(){
        SecurityMonitorService service = SecurityMonitorServiceImpl.getInstance();
        final List<SecurityType> securityData = new ArrayList<>();
        // Test the null argument case
        try {
            service.addSecurityData(securityData, -1);
        } catch (IllegalArgumentException e) {
            throw e;

        }
    }

    @Test
    public void addSecurityDataTest(){
        SecurityMonitorService service = SecurityMonitorServiceImpl.getInstance();

        final List<SecurityType> securityData = new ArrayList<>();

        // Test no data case
        service.addSecurityData(securityData, 0);
        String stats = service.getStats();
        assertEquals("Event Count: 0, avgProcessingTime: 0", stats);

        // Test one record with 0 processing time
        securityData.add(SecurityType.DOOR);
        service.addSecurityData(securityData, 0);

        stats = service.getStats();
        assertEquals("Event Count: 1, Door: 1, avgProcessingTime: 0", stats);

        // Test one record
        securityData.clear();
        service.clearStats();
        securityData.add(SecurityType.DOOR);
        service.addSecurityData(securityData, 10);

        stats = service.getStats();
        assertEquals("Event Count: 1, Door: 1, avgProcessingTime: 10", stats);

        // Test multiple  records of same type
        securityData.clear();
        securityData.add(SecurityType.DOOR);
        securityData.add(SecurityType.DOOR);
        service.addSecurityData(securityData, 10);

        stats = service.getStats();
        assertEquals("Event Count: 3, Door: 3, avgProcessingTime: 10", stats);

        // Test multiple records of different types
        securityData.clear();
        securityData.add(SecurityType.ALARM);
        securityData.add(SecurityType.ALARM);
        securityData.add(SecurityType.ALARM);
        securityData.add(SecurityType.ALARM);
        securityData.add(SecurityType.IMG);
        securityData.add(SecurityType.IMG);
        service.addSecurityData(securityData, 30);

        stats = service.getStats();
        String[] events = stats.split(",");
        assertEquals(5, events.length);
        assertEquals("Event Count: 9", events[0].trim());
        assertEquals("avgProcessingTime: 17", events[4].trim());
    }

    @Test
    public void getSecurityTypeStrFromLineTest(){
        // Test null
        String typeStr = SecurityFileReaderTask.getSecurityTypeStrFromLine(null);
        assertEquals("", typeStr);

        // Test empty string
        typeStr = SecurityFileReaderTask.getSecurityTypeStrFromLine("");
        assertEquals("", typeStr);

        // Test bad json string no commas
        typeStr = SecurityFileReaderTask.
                getSecurityTypeStrFromLine("{\"Type\":\"door\" \"Date\":\"2017-02-01 10:01:02\" \"open\": true}");
        assertEquals("", typeStr);

        // Test bad json string no colons
        typeStr = SecurityFileReaderTask.
                getSecurityTypeStrFromLine("{\"Type\"\"door\", \"Date\"\"2017-02-01 10:01:02\", \"open\" true}");
        assertEquals("", typeStr);

        typeStr = SecurityFileReaderTask.
                getSecurityTypeStrFromLine("{\"Type\":\"bogus\", \"Date\":\"2017-02-01 10:01:02\", \"open\": true}");
        assertEquals("bogus", typeStr);

        typeStr = SecurityFileReaderTask.
                getSecurityTypeStrFromLine("{\"Type\":\"door\", \"Date\":\"2017-02-01 10:01:02\", \"open\": true}");
        assertEquals("door", typeStr);

        typeStr = SecurityFileReaderTask.
                getSecurityTypeStrFromLine("{\"Type\":\"img\", \"Date\":\"2014-02-01 10:01:02\", " +
                        "\"bytes\": \"ab39szh6\", \"size\": 8}");
        assertEquals("img", typeStr);

        typeStr = SecurityFileReaderTask.
                getSecurityTypeStrFromLine("{\"Type\":\"alarm\", \"Date\":\"2014-02-01 10:01:05\", \"name\":\"fire\", " +
                        "\"floor\":\"1\", \"room\":\"101\"}");
        assertEquals("alarm", typeStr);
    }

}

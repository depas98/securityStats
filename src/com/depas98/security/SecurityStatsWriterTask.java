package com.depas98.security;

import java.util.TimerTask;

/**
 * This task will print out stats from the {@Link SecurityMonitorService}
 */
public class SecurityStatsWriterTask extends TimerTask {

    private final SecurityMonitorService securityMonitorService;

    public SecurityStatsWriterTask(SecurityMonitorService securityMonitorService){
        this.securityMonitorService = securityMonitorService;
    }

    @Override
    public void run() {
        System.out.println(securityMonitorService.getStats());
    }
}

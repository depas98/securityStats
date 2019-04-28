package com.depas98.security;

import java.util.TimerTask;

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

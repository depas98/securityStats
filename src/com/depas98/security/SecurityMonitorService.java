package com.depas98.security;

import java.util.List;

public interface SecurityMonitorService {

    void addSecurityData(List<SecurityType> securityData, long processingTime);
    String getStats();
    void clearStats();
}

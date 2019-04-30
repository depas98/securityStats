package com.depas98.security;

import java.util.List;

public interface SecurityMonitorService {

    /**
     * Takes in a list of SecurityTypes and updates the stats
     * with he information
     *
     * @param securityEvents List of Security Types that represent a security event
     * @param processingTime Time it too to collect the security data
     */
    void addSecurityData(List<SecurityType> securityEvents, long processingTime);

    /**
     * Return stats (status) information
     * @return stats
     */
    String getStats();

    /**
     * Clears out stats to the initial state
     */
    void clearStats();
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.searches.respin.model.memory;

import java.lang.management.ManagementFactory;
import org.apache.log4j.Logger;

/**
 *
 * @author Kenneth
 */
public class MemoryManager {

    private static final Logger LOGGER = Logger.getLogger(MemoryManager.class);
    private static final int allowedMemory = (int) (0.9 * (((com.sun.management.OperatingSystemMXBean) ManagementFactory
            .getOperatingSystemMXBean()).getTotalPhysicalMemorySize()) / 1024 / 1024);

    public static int getAllowedRam() {
        return allowedMemory;
    }
}

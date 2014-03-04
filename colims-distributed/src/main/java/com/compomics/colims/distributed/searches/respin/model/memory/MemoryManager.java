/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.distributed.searches.respin.model.memory;

import java.lang.management.ManagementFactory;

/**
 *
 * @author Kenneth
 */
public class MemoryManager {

    static {
        allowedMemory = (int) (0.9 * (((com.sun.management.OperatingSystemMXBean) ManagementFactory
                .getOperatingSystemMXBean()).getTotalPhysicalMemorySize()) / 1024 / 1024);
    }

    private static final int allowedMemory;

    public static int getAllowedRam() {
        return allowedMemory;
    }
}

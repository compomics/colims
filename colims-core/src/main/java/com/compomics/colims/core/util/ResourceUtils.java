/*
 *

 */
package com.compomics.colims.core.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * This utility class provides methods for accessing resources.
 *
 * @author Niels Hulstaert
 */
public final class ResourceUtils {

    /**
     * Private constructor to prevent instantiation.
     */
    private ResourceUtils() {
    }

    /**
     * Get a resource by its relative path. If the resource is not found on the file system, the classpath is searched.
     * If nothing is found, null is returned.
     *
     * @param relativePath the relative path of the resource
     * @return the found resource
     */
    public static Resource getResourceByRelativePath(final String relativePath) {
        Resource resource = new FileSystemResource(relativePath);

        if (!resource.exists()) {
            //try to find it on the classpath
            resource = new ClassPathResource(relativePath);

            if (!resource.exists()) {
                resource = null;
            }
        }

        return resource;
    }

    /**
     * Check if a resource with the given relative path exists on the file system.
     *
     * @param relativePath the relative path of the resource
     * @return the is existing boolean
     */
    public static boolean isExistingFile(final String relativePath) {
        boolean isExistingResource = false;

        Resource resource = new FileSystemResource(relativePath);
        if (resource.exists()) {
            isExistingResource = true;
        }

        return isExistingResource;
    }
}

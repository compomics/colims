package com.compomics.colims.core.util;

import java.nio.file.Path;
import java.util.Iterator;

/**
 * This utility class provides methods for file paths.
 *
 * @author Niels Hulstaert
 */
public final class PathUtils {

    /**
     * Private constructor to prevent instantiation.
     */
    private PathUtils() {
    }

    /**
     * Get the relative child path for the path component in the given path. (For example: if the path is
     * "/home/foo/bar/test.txt" and the path component is "foo", the method will return "bar/test.txt")
     *
     * @param path          the given path
     * @param pathComponent the path component
     * @return the relative child path
     * @throws IllegalArgumentException if the path doesn't contain the path component or if the given path doesn't have
     *                                  a root
     */
    public static Path getRelativeChildPath(final Path path, final String pathComponent) {
        Path relativePath = null;

        Path parentPath = path.getRoot();
        if (parentPath == null) {
            throw new IllegalArgumentException("The given path " + path.toString() + " doesn't have a root.");
        }

        Iterator<Path> pathIterator = path.iterator();
        while (pathIterator.hasNext()) {
            Path nextElement = pathIterator.next();
            parentPath = parentPath.resolve(nextElement);
            if (nextElement.toString().equals(pathComponent)) {
                //relativize the 2 paths
                relativePath = parentPath.relativize(path);
                break;
            }
        }

        if (relativePath == null) {
            throw new IllegalArgumentException("The given path " + path.toString() + " doesn't have " + pathComponent + " as a path component.");
        }

        return relativePath;
    }

}

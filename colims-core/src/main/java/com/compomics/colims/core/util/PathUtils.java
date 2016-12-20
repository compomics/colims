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

    /**
     * Get the relative child path for the child path compared to the parent path. (For example: if the path is
     * "/home/foo/bar/test.txt" and the path component is "/home/foo", the method will return "bar/test.txt")
     *
     * @param parentPath the parent path
     * @param childPath  the child path
     * @return the relative child path
     * @throws IllegalArgumentException if the child path doesn't contain the parent path or if the given paths don't
     *                                  have the same root
     */
    public static Path getRelativeChildPath(final Path parentPath, final Path childPath) {
        Path parentRootPath = parentPath.getRoot();
        if (parentPath == null) {
            throw new IllegalArgumentException("The given parent path " + parentPath.toString() + " doesn't have a root.");
        }
        Path childRootPath = childPath.getRoot();
        if (childRootPath == null) {
            throw new IllegalArgumentException("The given child path " + childPath.toString() + " doesn't have a root.");
        }
        if (!parentRootPath.equals(childRootPath)) {
            throw new IllegalArgumentException("The given paths don't have the same root.");
        }

        Path relativePath = parentPath.relativize(childPath);
        if (!childPath.toString().contains(relativePath.toString())) {
            throw new IllegalArgumentException("The given child path " + childPath.toString() + " doesn't contain the root path " + parentPath.toString());
        }

        return relativePath;
    }

}

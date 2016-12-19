
package com.compomics.colims.core.util;

import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Niels Hulstaert
 */
public class PathUtilsTest {

    private final Path rootPath = Paths.get("/home/foo/bar/test.txt");
    private final Path parentPath = Paths.get("/home/foo");
    private final Path childPath = Paths.get("/home/foo/bar/test.txt");
    private final Path relativePath = Paths.get("home/foo/bar/test.txt");

    /**
     * Test the throwing of an {@link IllegalArgumentException} in case the path component doesn't occur in the path.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetRelativeChildPathNotFound1() {
        PathUtils.getRelativeChildPath(rootPath, "foobar");
    }

    /**
     * Test the throwing of an {@link IllegalArgumentException} in case the child path doesn't contain the child path.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetRelativeChildPathNotFound2() {
        PathUtils.getRelativeChildPath(rootPath, Paths.get("/home/foobar/test.txt"));
    }

    /**
     * Test with a relative path, should throw an {@link IllegalArgumentException}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetRelativeChildPathRelative1() {
        PathUtils.getRelativeChildPath(relativePath, "foo");
    }

    /**
     * Test with a relative path, should throw an {@link IllegalArgumentException}
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetRelativeChildPathRelative2() {
        PathUtils.getRelativeChildPath(parentPath, relativePath);
    }

    /**
     * Test with a root path.
     */
    @Test
    public void testGetRelativeChildPathRoot1() {
        Path relativeChildPath = PathUtils.getRelativeChildPath(rootPath, "foo");
        Assert.assertEquals(Paths.get("bar/test.txt"), relativeChildPath);

        relativeChildPath = PathUtils.getRelativeChildPath(rootPath, "bar");
        Assert.assertEquals(Paths.get("test.txt"), relativeChildPath);
    }

    /**
     * Test with a root path.
     */
    @Test
    public void testGetRelativeChildPathRoot2() {
        Path relativeChildPath = PathUtils.getRelativeChildPath(parentPath, childPath);
        Assert.assertEquals(Paths.get("bar/test.txt"), relativeChildPath);

        relativeChildPath = PathUtils.getRelativeChildPath(Paths.get("/home/foo/bar"), childPath);
        Assert.assertEquals(Paths.get("test.txt"), relativeChildPath);
    }
}

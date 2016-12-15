
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
    private final Path relativePath = Paths.get("home/foo/bar/test.txt");

    /**
     * The the throwing of an IllegalArgumentException in case the path component doesn't occur in the path.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetRelativeChildPathNotFound() {
        PathUtils.getRelativeChildPath(rootPath, "foobar");
    }

    /**
     * Test with a relative path.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetRelativeChildPathRelative() {
        PathUtils.getRelativeChildPath(relativePath, "foo");
    }

    /**
     * Test with a root path.
     */
    @Test
    public void testGetRelativeChildPathRoot() {
        Path relativeChildPath = PathUtils.getRelativeChildPath(rootPath, "foo");
        Assert.assertEquals(Paths.get("bar/test.txt"), relativeChildPath);

        relativeChildPath = PathUtils.getRelativeChildPath(rootPath, "bar");
        Assert.assertEquals(Paths.get("test.txt"), relativeChildPath);
    }

}

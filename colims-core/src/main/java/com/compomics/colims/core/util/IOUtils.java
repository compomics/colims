/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.util;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * This utility class provides input and output methods.
 *
 * @author Niels Hulstaert
 */
public final class IOUtils {

    /**
     * private constructor to prevent initialization.
     */
    private IOUtils() {
    }

    /**
     * Reads the byte array from a given file.
     *
     * @param file the file
     * @return the byte array
     * @throws java.io.IOException exception thrown in case of an IO problem
     */
    public static byte[] read(final File file) throws IOException {
        return FileUtils.readFileToByteArray(file);
    }

    /**
     * Write the byte array to file.
     *
     * @param bytes the byte array
     * @param file  the file to be written to
     * @throws java.io.IOException exception thrown in case of an IO problem
     */
    public static void write(final byte[] bytes, final File file) throws IOException {
        FileUtils.writeByteArrayToFile(file, bytes);
    }

    /**
     * (G)zip the byte array.
     *
     * @param bytes the byte array
     * @return the zipped byte array
     * @throws java.io.IOException exception thrown in case of an IO problem
     */
    public static byte[] zip(final byte[] bytes) throws IOException {
        byte[] zippedBytes;

        //gzip the byte array
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             GZIPOutputStream gzipos = new GZIPOutputStream(baos)) {
            gzipos.write(bytes);

            gzipos.flush();
            gzipos.finish();

            zippedBytes = baos.toByteArray();
        }

        return zippedBytes;
    }

    /**
     * Unzip and write to a byte array.
     *
     * @param bytes the byte array
     * @return the unzipped byte array
     * @throws java.io.IOException exception thrown in case of an IO problem
     */
    public static byte[] unzip(final byte[] bytes) throws IOException {
        byte[] unzippedBytes;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             GZIPInputStream gzipis = new GZIPInputStream(bais)) {
            //unzip
            //this method uses a buffer internally
            org.apache.commons.io.IOUtils.copy(gzipis, baos);

            unzippedBytes = baos.toByteArray();
        }

        return unzippedBytes;
    }

    /**
     * Reads the byte array from a given file and (G)zip.
     *
     * @param file the file
     * @return the byte array
     * @throws java.io.IOException exception thrown in case of an IO problem
     */
    public static byte[] readAndZip(final File file) throws IOException {
        byte[] zippedBytes;

        //get file as byte array
        byte[] bytes = read(file);

        zippedBytes = zip(bytes);

        return zippedBytes;
    }

    /**
     * Unzip and write the byte array to file.
     *
     * @param bytes the byte array
     * @param file  the file to be written to
     * @throws java.io.IOException exception thrown in case of an IO problem
     */
    public static void unzipAndWrite(final byte[] bytes, final File file) throws IOException {
        //first unzip byte array
        byte[] unzippedBytes = unzip(bytes);

        //then write to file
        write(unzippedBytes, file);
    }
}

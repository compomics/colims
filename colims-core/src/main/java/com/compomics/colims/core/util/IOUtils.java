/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FileUtils;

import java.util.zip.GZIPOutputStream;

/**
 *
 * @author Niels Hulstaert
 */
public class IOUtils {

    /**
     * private constructor to prevent initialization
     */
    private IOUtils() {
    }

    /**
     * Reads the byte array from a given file.
     *
     * @param file the file
     * @return the byte array
     * @throws java.io.IOException
     */
    public static byte[] read(File file) throws IOException {
        return FileUtils.readFileToByteArray(file);
    }
    
    /**
     * Write the byte array to file.
     *
     * @param bytes the byte array
     * @param file the file to be written to
     * @throws java.io.IOException
     */
    public static void write(byte[] bytes, File file) throws IOException {
        FileUtils.writeByteArrayToFile(file, bytes);
    }
    
    /**
     * (G)zip the byte array.
     *
     * @param bytes the byte array
     * @return 
     * @throws java.io.IOException
     */
    public static byte[] zip(byte[] bytes) throws IOException {        
        byte[] zippedBytes;

        //gzip the byte array
        try (ByteArrayOutputStream zippedByteArrayOutputStream = new ByteArrayOutputStream();
                GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(zippedByteArrayOutputStream);) {
            gZIPOutputStream.write(bytes);

            gZIPOutputStream.flush();
            gZIPOutputStream.finish();

            zippedBytes = zippedByteArrayOutputStream.toByteArray();
        }
        
        return zippedBytes;
    }
    
    /**
     * Unzip and write to a byte array.
     *
     * @param bytes the byte array
     * @return the unzipped byte array
     * @throws java.io.IOException
     */
    public static byte[] unzip(byte[] bytes) throws IOException {
        byte[] unzippedBytes;

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                GZIPInputStream gZIPInputStream = new GZIPInputStream(new ByteArrayInputStream(bytes))) {
            //unzip
            //this method uses a buffer internally
            org.apache.commons.io.IOUtils.copy(gZIPInputStream, byteArrayOutputStream);

            unzippedBytes = byteArrayOutputStream.toByteArray();
        }

        return unzippedBytes;
    }

    /**
     * Reads the byte array from a given file and (G)zip.
     *
     * @param file the file
     * @return the byte array
     * @throws java.io.IOException
     */
    public static byte[] readAndUnzip(File file) throws IOException {
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
     * @param file the file to be written to
     * @throws java.io.IOException
     */
    public static void unzipAndWrite(byte[] bytes, File file) throws IOException {
        //first unzip byte array
        byte[] unzippedBytes = unzip(bytes);

        //then write to file
        write(unzippedBytes, file);
    }        
}

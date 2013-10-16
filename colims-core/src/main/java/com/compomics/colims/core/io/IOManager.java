/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.io;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Niels Hulstaert
 */
public interface IOManager {

    /**
     * Reads the byte array from a given file.
     *
     * @param file the file
     * @return the byte array
     */
    byte[] readBytesFromFile(File file) throws IOException;

    /**
     * Write the byte array to file.
     * 
     * @param bytes the byte array
     * @param file the file to be written to
     */
    void writeBytesToFile(byte[] bytes, File file) throws IOException;

    /**
     * Unzip and write to a byte array.
     *
     * @param bytes the byte array
     * @return the unzipped byte array
     */
    byte[] unzip(byte[] bytes) throws IOException;
    
    /**
     * Unzip and write the byte array to file.
     * 
     * @param bytes the byte array
     * @param file the file to be written to
     */
    void unzipAndWriteBytesToFile(byte[] bytes, File file) throws IOException;
}

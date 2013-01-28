/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.io.impl;

import com.compomics.colims.core.io.IOManager;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

/**
 *
 * @author Niels Hulstaert
 */
@Service("ioManager")
public class IOManagerImpl implements IOManager {

    @Override
    public byte[] readBytesFromFile(File file) throws IOException {
        return FileUtils.readFileToByteArray(file);
    }

    @Override
    public void writeBytesToFile(File file, byte[] bytes) throws IOException {
        FileUtils.writeByteArrayToFile(file, bytes);
    }

    @Override
    public byte[] unzip(byte[] bytes) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        
        //this method uses a buffer internally
        IOUtils.copy(new GZIPInputStream(new ByteArrayInputStream(bytes)), byteArrayOutputStream);
        
        return byteArrayOutputStream.toByteArray(); 
    }

    @Override
    public void unzipAndWriteBytesToFile(File file, byte[] bytes) throws IOException {
        //first unzip byte array
        byte[] unzippedBytes = unzip(bytes);
        
        //then write to file
        writeBytesToFile(file, unzippedBytes);       
    }
}

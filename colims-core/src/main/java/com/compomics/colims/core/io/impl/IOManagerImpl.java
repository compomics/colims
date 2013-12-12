/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.io.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import com.compomics.colims.core.io.IOManager;
import java.util.zip.GZIPOutputStream;

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
    public byte[] readZippedBytesFromFile(File file) throws IOException {
        byte[] zippedBytes = null;

        //get file as byte array
        byte[] bytes = readBytesFromFile(file);

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

    @Override
    public void writeBytesToFile(byte[] bytes, File file) throws IOException {
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
    public void unzipAndWriteBytesToFile(byte[] bytes, File file) throws IOException {
        //first unzip byte array
        byte[] unzippedBytes = unzip(bytes);

        //then write to file
        writeBytesToFile(unzippedBytes, file);
    }
}

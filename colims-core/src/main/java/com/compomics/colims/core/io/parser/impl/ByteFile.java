package com.compomics.colims.core.io.parser.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import org.apache.commons.io.IOUtils;

/**
 * a wrapper for the File class that handles in memory files
 *
 * @author Davy
 */
public class ByteFile extends File {

    private byte[] slurpedFile;

    public ByteFile(final String pathname) {
        super(pathname);
    }

    public ByteFile(final String parent, final String child) {
        super(parent, child);
    }

    public ByteFile(final File parent, final String child) {
        super(parent, child);
    }

    public ByteFile(final URI uri) {
        super(uri);
    }

    public ByteFile(final File readInFile) throws IOException, FileNotFoundException {
        super(readInFile.getAbsolutePath());
        slurpFile(readInFile);
    }

    public ByteFile(final byte[] fileByteArray) {
        super("not on disk");
        slurpedFile = fileByteArray.clone();
    }

    private void slurpFile(final File readInFile) throws IOException, FileNotFoundException {
        if (readInFile.isFile()) {
            ByteArrayOutputStream bais = new ByteArrayOutputStream(1024);
            IOUtils.copy(new FileInputStream(readInFile), bais);
            slurpedFile = bais.toByteArray();
        }
    }

    public InputStream getOutputStream() throws FileNotFoundException {
        if (slurpedFile != null) {
            return new ByteArrayInputStream(slurpedFile);
        } else {
            return new FileInputStream(this);
        }
    }
       
}

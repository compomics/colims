package com.compomics.colims.core.io.parser.impl;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Davy
 */
public class MaxQuantParameterFileAggregator {
// the sexiest of titles   

    private ByteFile summaryFile;
    private ByteFile parametersFile;
    private TarArchiveOutputStream tOut;

    /**
     * this class tries to emulate a tarball in memory, also returns tarballs if
     * needed
     *
     * @param aSummaryFile
     * @param aParametersFile
     */
    public MaxQuantParameterFileAggregator(File aSummaryFile, File aParametersFile) throws IOException, FileNotFoundException {
        this.summaryFile = new ByteFile(aSummaryFile);
        this.parametersFile = new ByteFile(aParametersFile);
    }

    public void setParametersFile(File aParametersFile) throws FileNotFoundException, IOException {
        this.parametersFile = new ByteFile(aParametersFile);
    } 

    public void setSummaryFile(File aSummaryFile) throws FileNotFoundException, IOException {
        this.summaryFile = new ByteFile(aSummaryFile);
    }

    public File getTarredParaMeterFiles() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        BufferedOutputStream bOut = new BufferedOutputStream(baos);
        tOut = new TarArchiveOutputStream(bOut);
        addFileToTar(tOut, summaryFile, false);
        addFileToTar(tOut, parametersFile, false);
        tOut.finish();
        return new ByteFile(baos.toByteArray());
    }

    /**
     * Creates a tar entry for the path specified with a name built from the
     * base passed in and the file/directory name. If the path is a directory, a
     * recursive call is made such that the full directory is added to the tar.
     *
     * @param tOut The tar file's output stream
     * @param path The filesystem path of the file/directory being added
     * @param base The base prefix to for the name of the tar file entry
     *
     * @throws IOException If anything goes wrong
     */
    private static void addFileToTar(TarArchiveOutputStream tOut, File fileToTar, boolean tarIfFolder) throws IOException {
        String entryName = fileToTar.getParentFile().getAbsolutePath() + fileToTar.getName();
        TarArchiveEntry tarEntry = new TarArchiveEntry(fileToTar, entryName);
        tOut.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
        //STAR algorithm
        tOut.setBigNumberMode(2);
        tOut.putArchiveEntry(tarEntry);

        if (fileToTar.isFile()) {
            IOUtils.copy(new FileInputStream(fileToTar), tOut);

            tOut.closeArchiveEntry();
        } else if (fileToTar instanceof ByteFile) {
        } else if (tarIfFolder) {
            tOut.closeArchiveEntry();

            File[] children = fileToTar.listFiles();

            if (children != null) {
                for (File child : children) {
                    addFileToTar(tOut, new File(child.getAbsolutePath()), tarIfFolder);
                }
            }
        }
    }
}

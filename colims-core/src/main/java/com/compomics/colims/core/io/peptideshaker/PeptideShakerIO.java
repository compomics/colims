package com.compomics.colims.core.io.peptideshaker;

import java.io.File;
import java.io.IOException;
import org.apache.commons.compress.archivers.ArchiveException;

/**
 *
 * @author Niels Hulstaert
 */
public interface PeptideShakerIO {

    /**
     * Unpack a PeptideShaker .cps file and return a PeptideShakerImport
     * instance. The .cps archive is unzipped in a temp folder.
     *
     * @param peptideShakerCpsArchive the PepitideShaker .cps file
     * @return the PeptideShaker import object
     * @throws java.io.IOException
     * @throws org.apache.commons.compress.archivers.ArchiveException
     * @throws java.lang.ClassNotFoundException
     */
    UnpackedPeptideShakerDataImport unpackPeptideShakerCpsArchive(File peptideShakerCpsArchive) throws IOException, ArchiveException, ClassNotFoundException;

    /**
     * Unpack a PeptideShaker .cps file and return PeptideShakerImport instance.
     * The .cps archive is unzippped in the given destination directory.
     *
     * @param peptideShakerCpsArchive the PepitideShaker .cps file
     * @param destinationDirectory the destination directory
     * @return the PeptideShaker import object
     * @throws java.io.IOException
     * @throws org.apache.commons.compress.archivers.ArchiveException
     * @throws java.lang.ClassNotFoundException
     */
    UnpackedPeptideShakerDataImport unpackPeptideShakerCpsArchive(File peptideShakerCpsArchive, File destinationDirectory) throws IOException, ArchiveException, ClassNotFoundException;
}

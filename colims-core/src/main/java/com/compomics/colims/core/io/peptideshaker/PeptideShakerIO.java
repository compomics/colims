package com.compomics.colims.core.io.peptideshaker;

import java.io.File;
import java.io.IOException;
import org.apache.commons.compress.archivers.ArchiveException;

/**
 * This interface defines PeptideShaker import methods.
 *
 * @author Niels Hulstaert
 */
public interface PeptideShakerIO {

    /**
     * Unpack a PeptideShaker .cps file and return an
     * UnpackedPeptideShakerImport instance. The .cps archive is unzipped in a
     * temp folder.
     *
     * @param peptideShakerCpsArchive the PepitideShaker .cps file
     * @return the PeptideShaker import object
     * @throws java.io.IOException the IOException
     * @throws org.apache.commons.compress.archivers.ArchiveException the ArchiveException
     * @throws java.lang.ClassNotFoundException the ClassNotFoundException
     */
    UnpackedPeptideShakerImport unpackPeptideShakerCpsArchive(File peptideShakerCpsArchive) throws IOException, ArchiveException, ClassNotFoundException;

    /**
     * Unpack a PeptideShaker .cps file and return an
     * UnpackedPeptideShakerImport instance. The .cps archive is unzippped in
     * the given destination directory.
     *
     * @param peptideShakerCpsArchive the PepitideShaker .cps file
     * @param destinationDirectory the destination directory
     * @return the PeptideShaker import object
     * @throws java.io.IOException the IOException
     * @throws org.apache.commons.compress.archivers.ArchiveException the ArchiveException
     * @throws java.lang.ClassNotFoundException the ClassNotFoundException
     */
    UnpackedPeptideShakerImport unpackPeptideShakerCpsArchive(File peptideShakerCpsArchive, File destinationDirectory) throws IOException, ArchiveException, ClassNotFoundException;

    /**
     * Unpack the given PeptideShakerImport instance: unpack the PeptideShaker
     * .cps fileand return an UnpackedPeptideShakerImport instance. The .cps
     * archive is unzipped in a temp folder.
     *
     * @param peptideShakerDataImport the PepitideShakerDataImport
     * @return the PeptideShaker import object
     * @throws java.io.IOException the IOException
     * @throws org.apache.commons.compress.archivers.ArchiveException the ArchiveException
     * @throws java.lang.ClassNotFoundException the ClassNotFoundException
     */
    UnpackedPeptideShakerImport unpackPeptideShakerImport(PeptideShakerImport peptideShakerDataImport) throws IOException, ArchiveException, ClassNotFoundException;
}

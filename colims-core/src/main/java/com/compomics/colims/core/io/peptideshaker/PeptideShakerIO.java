package com.compomics.colims.core.io.peptideshaker;

import java.io.File;

import com.compomics.colims.core.exception.PeptideShakerIOException;
import com.compomics.colims.core.io.peptideshaker.model.PeptideShakerImport;

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
     * @throws PeptideShakerIOException
     */
    PeptideShakerImport unpackPeptideShakerCpsArchive(File peptideShakerCpsArchive) throws PeptideShakerIOException;

    /**
     * Unpack a PeptideShaker .cps file and return PeptideShakerImport instance.
     * The .cps archive is unzippped in the given destination directory.
     *
     * @param peptideShakerCpsArchive the PepitideShaker .cps file
     * @param destinationDirectory the destination directory
     * @return the PeptideShaker import object
     * @throws PeptideShakerIOException
     */
    PeptideShakerImport unpackPeptideShakerCpsArchive(File peptideShakerCpsArchive, File destinationDirectory) throws PeptideShakerIOException;
}

package com.compomics.colims.core.io.peptideshaker;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.compress.archivers.ArchiveException;

/**
 * This interface defines PeptideShaker import methods.
 *
 * @author Niels Hulstaert
 */
public interface PeptideShakerIO {

    /**
     * Unpack a PeptideShaker .cps file and return an UnpackedPeptideShakerImport instance. The .cps archive is unzipped
     * in a temp folder.
     *
     * @param peptideShakerCpsArchive the PeptideShaker .cps file
     * @return the PeptideShaker import object
     * @throws IOException            thrown in case of an IO related problem
     * @throws ArchiveException       thrown in case of an archive related problem
     * @throws ClassNotFoundException thrown in case of a failure to load a class by it's string name.
     * @throws java.sql.SQLException  thrown in case of a database access error
     * @throws InterruptedException   thrown in case a thread is interrupted
     */
    UnpackedPeptideShakerImport unpackPeptideShakerCpsArchive(File peptideShakerCpsArchive) throws IOException, ArchiveException, ClassNotFoundException, SQLException, InterruptedException;

    /**
     * Unpack a PeptideShaker .cps file and return an UnpackedPeptideShakerImport instance. The .cps archive is unzipped
     * in the given destination directory.
     *
     * @param peptideShakerCpsArchive the PeptideShaker .cps file
     * @param destinationDirectory    the destination directory
     * @return the PeptideShaker import object
     * @throws IOException            thrown in case of an IO related problem
     * @throws ClassNotFoundException thrown in case of a failure to load a class by it's string name.
     * @throws java.sql.SQLException  thrown in case of a database access error
     * @throws InterruptedException   thrown in case a thread is interrupted
     */
    UnpackedPeptideShakerImport unpackPeptideShakerCpsArchive(File peptideShakerCpsArchive, File destinationDirectory) throws IOException, ClassNotFoundException, SQLException, InterruptedException;

    /**
     * Unpack the given PeptideShakerImport instance: unpack the PeptideShaker .cps file and return an
     * UnpackedPeptideShakerImport instance. The .cps archive is unzipped in a temp folder.
     *
     * @param peptideShakerDataImport the PeptideShakerDataImport instance
     * @return the PeptideShaker import object
     * @throws IOException            thrown in case of an IO related problem
     * @throws ArchiveException       thrown in case of an archive related problem
     * @throws ClassNotFoundException thrown in case of a failure to load a class by it's string name.
     * @throws java.sql.SQLException  thrown in case of a database access error
     * @throws InterruptedException   thrown in case a thread is interrupted
     */
    UnpackedPeptideShakerImport unpackPeptideShakerImport(PeptideShakerImport peptideShakerDataImport) throws IOException, ArchiveException, ClassNotFoundException, SQLException, InterruptedException;
}

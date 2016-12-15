package com.compomics.colims.distributed.io.peptideshaker;

import com.compomics.colims.core.io.PeptideShakerImport;
import org.apache.commons.compress.archivers.ArchiveException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;

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
     * @param peptideShakerCpsArchive the PeptideShaker .cps file
     * @return the PeptideShaker import object
     * @throws IOException                                            thrown in case of an IO related problem
     * @throws org.apache.commons.compress.archivers.ArchiveException in case of an Archiver related problem
     * @throws ClassNotFoundException                                 thrown in case of a failure to load a class by
     *                                                                it's string name
     * @throws java.sql.SQLException                                  thrown in case of a database access error
     * @throws InterruptedException                                   thrown in case a thread is interrupted
     */
    UnpackedPeptideShakerImport unpackPeptideShakerCpsArchive(File peptideShakerCpsArchive) throws IOException, ArchiveException, ClassNotFoundException, SQLException, InterruptedException;

    /**
     * Unpack a PeptideShaker .cps file and return an
     * UnpackedPeptideShakerImport instance. The .cps archive is unzipped in the
     * given destination directory.
     *
     * @param peptideShakerCpsArchive the PeptideShaker .cps file
     * @param destinationDirectory    the destination directory
     * @return the PeptideShaker import object
     * @throws IOException                                            thrown in case of an IO related problem
     * @throws ClassNotFoundException                                 thrown in case of a failure to load a class by
     *                                                                it's string name
     * @throws java.sql.SQLException                                  thrown in case of a database access error
     * @throws InterruptedException                                   thrown in case a thread is interrupted
     * @throws org.apache.commons.compress.archivers.ArchiveException thrown in case of an archive related problem
     */
    UnpackedPeptideShakerImport unpackPeptideShakerCpsArchive(File peptideShakerCpsArchive, File destinationDirectory) throws IOException, ClassNotFoundException, SQLException, InterruptedException, ArchiveException;

    /**
     * Unpack the given PeptideShakerImport instance: unpack the PeptideShaker .cps file and return an
     * UnpackedPeptideShakerImport instance. The .cps archive is unzipped in a temp folder. The relative paths are
     * resolved against the given experiments directory path.
     *
     * @param peptideShakerDataImport the PeptideShakerDataImport instance
     * @param experimentsDirectory    the experiments parent directory
     * @return the PeptideShaker import object
     * @throws IOException            thrown in case of an IO related problem
     * @throws ArchiveException       thrown in case of an archive related problem
     * @throws ClassNotFoundException thrown in case of a failure to load a class by it's string name
     * @throws java.sql.SQLException  thrown in case of a database access error
     * @throws InterruptedException   thrown in case a thread is interrupted
     */
    UnpackedPeptideShakerImport unpackPeptideShakerImport(PeptideShakerImport peptideShakerDataImport, Path experimentsDirectory) throws IOException, ArchiveException, ClassNotFoundException, SQLException, InterruptedException;
}

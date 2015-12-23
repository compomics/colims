package com.compomics.colims.distributed.io.peptideshaker.impl;

import com.compomics.colims.core.io.PeptideShakerImport;
import com.compomics.colims.distributed.io.peptideshaker.PeptideShakerIO;
import com.compomics.colims.distributed.io.peptideshaker.UnpackedPeptideShakerImport;
import com.google.common.io.Files;
import eu.isas.peptideshaker.utils.CpsParent;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @author Niels Hulstaert
 */
@SuppressWarnings("ConstantConditions")
@Component("peptideShakerIO")
public class PeptideShakerIOImpl implements PeptideShakerIO {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(PeptideShakerIOImpl.class);
//    private static final String PEPTIDESHAKER_SERIALIZATION_DIR = "matches";

    @Override
    public UnpackedPeptideShakerImport unpackPeptideShakerCpsArchive(File peptideShakerCpsArchive) throws IOException, ClassNotFoundException, SQLException, InterruptedException, ArchiveException {
        File tempDirectory = Files.createTempDir();
        if (tempDirectory.exists()) {
            return this.unpackPeptideShakerCpsArchive(peptideShakerCpsArchive, tempDirectory);
        } else {
            throw new IOException("Unable to create a temporary directory in " + tempDirectory.getParent());
        }
    }

    @Override
    public UnpackedPeptideShakerImport unpackPeptideShakerCpsArchive(File peptideShakerCpsArchive, File destinationDirectory) throws IOException, ClassNotFoundException, SQLException, InterruptedException, ArchiveException {
        LOGGER.info("Start importing PeptideShaker .cps file " + peptideShakerCpsArchive.getName());

        CpsParent cpsParent = new CpsParent(destinationDirectory);
        cpsParent.setCpsFile(peptideShakerCpsArchive);

        //load and unpack the .cps file
        cpsParent.loadCpsFile(destinationDirectory, null);

        UnpackedPeptideShakerImport unpackedPeptideShakerImport = new UnpackedPeptideShakerImport(peptideShakerCpsArchive, destinationDirectory, cpsParent);

        LOGGER.info("Finished importing PeptideShaker file " + peptideShakerCpsArchive.getName());

        return unpackedPeptideShakerImport;
    }

    @Override
    public UnpackedPeptideShakerImport unpackPeptideShakerImport(PeptideShakerImport peptideShakerDataImport) throws IOException, ArchiveException, ClassNotFoundException, SQLException, InterruptedException {
        //unpacked PeptideShakerImport .cps archive
        UnpackedPeptideShakerImport unpackedPeptideShakerImport = unpackPeptideShakerCpsArchive(peptideShakerDataImport.getPeptideShakerCpsArchive());

        //set fast file and MGF files
        unpackedPeptideShakerImport.setFastaDbIds(peptideShakerDataImport.getFastaDbIds());
        unpackedPeptideShakerImport.setMgfFiles(peptideShakerDataImport.getMgfFiles());

        return unpackedPeptideShakerImport;
    }
}

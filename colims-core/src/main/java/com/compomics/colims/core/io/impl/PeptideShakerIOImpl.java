package com.compomics.colims.core.io.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.compomics.colims.core.config.PropertiesConfigurationHolder;
import com.compomics.colims.core.exception.PeptideShakerIOException;
import com.compomics.colims.core.io.PeptideShakerIO;
import com.compomics.colims.core.io.model.PeptideShakerImport;
import com.compomics.util.experiment.MsExperiment;
import com.compomics.util.experiment.io.ExperimentIO;
import com.google.common.io.Files;

/**
 *
 * @author Niels Hulstaert
 */
@Component("peptideShakerIO")
public class PeptideShakerIOImpl implements PeptideShakerIO {

    private static final Logger LOGGER = Logger.getLogger(PeptideShakerIOImpl.class);
    private static final String PEPTIDESHAKER_SERIALIZATION_DIR = "resources/matches";
    private static final String PEPTIDESHAKER_SERIALIZIZED_EXP_NAME = "experiment";

    @Override
    public PeptideShakerImport importPeptideShakerCpsArchive(File peptideShakerCpsArchive) throws PeptideShakerIOException {
        LOGGER.info("starting import peptideshaker file " + peptideShakerCpsArchive.getName());

        MsExperiment msExperiment;
        File tempFolder;

        if (!peptideShakerCpsArchive.exists()) {
            throw new IllegalArgumentException("The PeptideShaker .cps file with name: " + peptideShakerCpsArchive.getName() + " doesn't exist.");
        }

        try (FileInputStream fileInputStream = new FileInputStream(peptideShakerCpsArchive);) {
            //define buffer for BufferedInputStream
            final int buffer = PropertiesConfigurationHolder.getInstance().getInt("peptideshakerio.buffer_size");
            byte data[] = new byte[buffer];

            try (ArchiveInputStream tarInput = new ArchiveStreamFactory().createArchiveInputStream(new BufferedInputStream(fileInputStream, buffer));) {
                long fileLength = peptideShakerCpsArchive.length();

                //create temp folder to unzip to
                tempFolder = Files.createTempDir();

                ArchiveEntry archiveEntry;
                int progress;
                while ((archiveEntry = tarInput.getNextEntry()) != null) {
                    //for each entry in the archive, make a new file
                    LOGGER.debug("making file for archive entry " + archiveEntry.getName());
                    File destinationFile = new File(tempFolder, archiveEntry.getName());

                    //make the necessary directories in the temp folder
                    boolean madeDirs = destinationFile.getParentFile().mkdirs();
                    //check if directories have been made it they didn't exist yet
                    if (!madeDirs && !destinationFile.getParentFile().exists()) {
                        throw new IOException("Unable to create the necessary directories in " + destinationFile.getAbsolutePath());
                    }

                    try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(destinationFile))) {
                        int count;
                        while ((count = tarInput.read(data, 0, buffer)) != -1) {
                            bufferedOutputStream.write(data, 0, count);
                        }
                    }
                    //@todo do something with progress
                    progress = (int) (100 * tarInput.getBytesRead() / fileLength);
                }
            }
        } catch (ArchiveException | IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PeptideShakerIOException(ex);
        }

        //get serialized experiment object file
        File serializedExperimentFile = new File(tempFolder, PEPTIDESHAKER_SERIALIZATION_DIR + File.separator + PEPTIDESHAKER_SERIALIZIZED_EXP_NAME);
        try {
            //deserialize the experiment
            LOGGER.info("deserializing experiment from file " + serializedExperimentFile.getAbsolutePath());
            msExperiment = ExperimentIO.loadExperiment(serializedExperimentFile);
        } catch (IOException | ClassNotFoundException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PeptideShakerIOException(ex);
        }

        PeptideShakerImport peptideShakerImport = new PeptideShakerImport(msExperiment, new File(tempFolder, PEPTIDESHAKER_SERIALIZATION_DIR));
        LOGGER.info("Finishing import peptideshaker file " + peptideShakerCpsArchive.getName());

        return peptideShakerImport;
    }
}

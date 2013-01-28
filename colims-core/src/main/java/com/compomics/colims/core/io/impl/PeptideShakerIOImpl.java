package com.compomics.colims.core.io.impl;

import com.compomics.colims.core.config.PropertiesConfigurationHolder;
import com.compomics.colims.core.exception.PeptideShakerIOException;
import com.compomics.colims.core.io.PeptideShakerIO;
import com.compomics.colims.core.io.model.PeptideShakerImport;
import com.compomics.util.experiment.MsExperiment;
import com.compomics.util.experiment.io.ExperimentIO;
import com.google.common.io.Files;
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
        PeptideShakerImport peptideShakerImport;
        MsExperiment msExperiment;
        File tempFolder;

        LOGGER.info("starting import peptideshaker file " + peptideShakerCpsArchive.getName());
        if (!peptideShakerCpsArchive.exists()) {
            throw new IllegalArgumentException("The PeptideShaker .cps file with name: " + peptideShakerCpsArchive.getName() + " doesn't exist.");
        }
        try {
            final int buffer = PropertiesConfigurationHolder.getInstance().getInt("peptideshakerio.buffer_size");
            byte data[] = new byte[buffer];
            FileInputStream fileInputStream = new FileInputStream(peptideShakerCpsArchive);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream, buffer);
            ArchiveInputStream tarInput = new ArchiveStreamFactory().createArchiveInputStream(bufferedInputStream);

            long fileLength = peptideShakerCpsArchive.length();

            //create temp folder to unzip to
            tempFolder = Files.createTempDir();

            ArchiveEntry archiveEntry = null;
            while ((archiveEntry = tarInput.getNextEntry()) != null) {
                //for each entry in the archive, make a new file
                LOGGER.debug("making file for archive entry " + archiveEntry.getName());
                File destinationFile = new File(tempFolder, archiveEntry.getName());
                //make the necessary directories in the temp folder
                destinationFile.getParentFile().mkdirs();

                FileOutputStream fileOutputStream = new FileOutputStream(destinationFile);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

                int count = 0;
                while ((count = tarInput.read(data, 0, buffer)) != -1) {
                    bufferedOutputStream.write(data, 0, count);
                }
                //close output streams
                bufferedOutputStream.close();
                fileOutputStream.close();

                //@todo do something with progress
                int progress = (int) (100 * tarInput.getBytesRead() / fileLength);
            }
            //close input streams
            tarInput.close();
            bufferedInputStream.close();
            fileInputStream.close();

            //get serialized experiment object file
            File serializedExperimentFile = new File(tempFolder, PEPTIDESHAKER_SERIALIZATION_DIR + File.separator + PEPTIDESHAKER_SERIALIZIZED_EXP_NAME);
            //deserialize the experiment
            LOGGER.info("deserializing experiment from file " + serializedExperimentFile.getAbsolutePath());
            msExperiment = ExperimentIO.loadExperiment(serializedExperimentFile);

            LOGGER.info("Finishing import peptideshaker file " + peptideShakerCpsArchive.getName());
        } catch (ArchiveException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PeptideShakerIOException(ex);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PeptideShakerIOException(ex);
        } catch (ClassNotFoundException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PeptideShakerIOException(ex);
        }

        peptideShakerImport = new PeptideShakerImport(msExperiment, new File(tempFolder, PEPTIDESHAKER_SERIALIZATION_DIR));

        return peptideShakerImport;
    }
}

package com.compomics.colims.core.io.peptideshaker.impl;

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

import com.compomics.colims.core.io.peptideshaker.PeptideShakerImport;
import com.compomics.colims.core.io.peptideshaker.PeptideShakerIO;
import com.compomics.colims.core.io.peptideshaker.UnpackedPeptideShakerImport;
import com.compomics.util.experiment.MsExperiment;
import com.compomics.util.experiment.io.ExperimentIO;
import com.google.common.io.Files;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("peptideShakerIO")
public class PeptideShakerIOImpl implements PeptideShakerIO {

    private static final Logger LOGGER = Logger.getLogger(PeptideShakerIOImpl.class);
    private static final String PEPTIDESHAKER_SERIALIZATION_DIR = "matches";
    private static final String PEPTIDESHAKER_SERIALIZIZED_EXP_NAME = "experiment";
    @Value("${peptideshakerio.buffer_size}")
    private int buffer;
//    @Autowired
//    private EventBus eventBus;

//    @PostConstruct
//    private void init() {
//        eventBus.register(this);
//    }
    @Override
    public UnpackedPeptideShakerImport unpackPeptideShakerCpsArchive(File peptideShakerCpsArchive) throws IOException, ArchiveException, ClassNotFoundException {
        File tempDirectory = Files.createTempDir();
        if (tempDirectory.exists()) {
            return this.unpackPeptideShakerCpsArchive(peptideShakerCpsArchive, tempDirectory);
        } else {
            throw new IOException("Unable to create a temporary directory in " + tempDirectory.getParent());
        }
    }

    @Override
    public UnpackedPeptideShakerImport unpackPeptideShakerCpsArchive(File peptideShakerCpsArchive, File destinationDirectory) throws IOException, ArchiveException, ClassNotFoundException {
        LOGGER.info("Start importing PeptideShaker .cps file " + peptideShakerCpsArchive.getName());

        MsExperiment msExperiment;

        if (!peptideShakerCpsArchive.exists()) {
            throw new IllegalArgumentException("The PeptideShaker .cps file with name: " + peptideShakerCpsArchive.getName() + " could not be found.");
        }

        try (FileInputStream fileInputStream = new FileInputStream(peptideShakerCpsArchive);) {
            byte data[] = new byte[buffer];

            try (ArchiveInputStream tarInput = new ArchiveStreamFactory().createArchiveInputStream(new BufferedInputStream(fileInputStream, buffer));) {
//                long fileLength = peptideShakerCpsArchive.length();

                ArchiveEntry archiveEntry;
//                int progress;
                while ((archiveEntry = tarInput.getNextEntry()) != null) {
                    //for each entry in the archive, make a new file
                    LOGGER.debug("Creating file for archive entry " + archiveEntry.getName());                    
                    
                    File destinationFile = new File(destinationDirectory, FilenameUtils.separatorsToSystem(archiveEntry.getName()));

                    //create the necessary directories in the destination directory
                    boolean madeDirs = destinationFile.getParentFile().mkdirs();
                    //check if the directories have been made if they didn't exist yet
                    if (!madeDirs && !destinationFile.getParentFile().exists()) {
                        throw new IOException("Unable to create the necessary directories in directory " + destinationFile.getAbsolutePath()
                                + ". Check if you have to right to write in this directory.");
                    }

                    try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(destinationFile))) {
                        int count;
                        while ((count = tarInput.read(data, 0, buffer)) != -1) {
                            bufferedOutputStream.write(data, 0, count);
                        }
                    }
//                    //@todo do something with progress
//                    progress = (int) (100 * tarInput.getBytesRead() / fileLength);
//                    eventBus.post(new ProgressEvent(progress, "unzipping archive"));
                }
            }
        }

        //get serialized experiment object file
        File serializedExperimentFile = new File(destinationDirectory, PEPTIDESHAKER_SERIALIZATION_DIR + File.separator + PEPTIDESHAKER_SERIALIZIZED_EXP_NAME);

        //deserialize the experiment
        LOGGER.info("Deserializing experiment from file " + serializedExperimentFile.getAbsolutePath());

        msExperiment = ExperimentIO.loadExperiment(serializedExperimentFile);
        UnpackedPeptideShakerImport unpackedPeptideShakerImport = new UnpackedPeptideShakerImport(peptideShakerCpsArchive, destinationDirectory, new File(destinationDirectory, PEPTIDESHAKER_SERIALIZATION_DIR), msExperiment);

        LOGGER.info("Finished importing PeptideShaker file " + peptideShakerCpsArchive.getName());

        return unpackedPeptideShakerImport;
    }

    @Override
    public UnpackedPeptideShakerImport unpackPeptideShakerImport(PeptideShakerImport peptideShakerDataImport) throws IOException, ArchiveException, ClassNotFoundException {
        //unpacked PeptideShakerImport .cps archive
        UnpackedPeptideShakerImport unpackedPeptideShakerImport = unpackPeptideShakerCpsArchive(peptideShakerDataImport.getPeptideShakerCpsArchive());

        //set fast file and MGF files
        unpackedPeptideShakerImport.setFastaDb(peptideShakerDataImport.getFastaDb());
        unpackedPeptideShakerImport.setMgfFiles(peptideShakerDataImport.getMgfFiles());

        return unpackedPeptideShakerImport;
    }
}

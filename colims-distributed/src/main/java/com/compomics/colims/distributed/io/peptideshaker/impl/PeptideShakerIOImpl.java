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
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public UnpackedPeptideShakerImport unpackPeptideShakerCpsxArchive(File peptideShakerCpsArchive) throws IOException, ClassNotFoundException, SQLException, InterruptedException, ArchiveException {
        File tempDirectory = Files.createTempDir();
        if (tempDirectory.exists()) {
            return this.unpackPeptideShakerCpsxArchive(peptideShakerCpsArchive, tempDirectory);
        } else {
            throw new IOException("Unable to create a temporary directory in " + tempDirectory.getParent());
        }
    }

    @Override
    public UnpackedPeptideShakerImport unpackPeptideShakerCpsxArchive(File peptideShakerCpsArchive, File destinationDirectory) throws IOException, ClassNotFoundException, SQLException, InterruptedException, ArchiveException {
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
    public UnpackedPeptideShakerImport unpackPeptideShakerImport(PeptideShakerImport peptideShakerDataImport, Path experimentsDirectory) throws IOException, ArchiveException, ClassNotFoundException, SQLException, InterruptedException {
        Path relativePeptideShakerCpsxArchivePath = peptideShakerDataImport.getPeptideShakerCpsxArchive();
        //make the relative path absolute
        Path absolutePeptideShakerCpsxArchivePath = experimentsDirectory.resolve(relativePeptideShakerCpsxArchivePath);
        //check if the path exists
        if (!java.nio.file.Files.exists(absolutePeptideShakerCpsxArchivePath)) {
            throw new IllegalArgumentException("The PeptideShaker file " + absolutePeptideShakerCpsxArchivePath.toString() + " doesn't exist.");
        }
        //unpacked PeptideShakerImport .cps archive
        UnpackedPeptideShakerImport unpackedPeptideShakerImport = unpackPeptideShakerCpsxArchive(absolutePeptideShakerCpsxArchivePath.toFile());

        //set FASTA DB ids and MGF files
        unpackedPeptideShakerImport.setFastaDbIds(peptideShakerDataImport.getFastaDbIds());
        //make the relative MGF file paths absolute
        List<Path> absoluteMgfFiles = peptideShakerDataImport.getMgfFiles().stream().map(experimentsDirectory::resolve).collect(Collectors.toList());
        //check if all the MFG files exist
        absoluteMgfFiles.forEach(mgfPath -> {
            if (!java.nio.file.Files.exists(mgfPath)) {
                throw new IllegalArgumentException("The MGF file " + mgfPath.toString() + " doesn't exist.");
            }
        });

        unpackedPeptideShakerImport.setMgfFiles(absoluteMgfFiles);

        return unpackedPeptideShakerImport;
    }
}

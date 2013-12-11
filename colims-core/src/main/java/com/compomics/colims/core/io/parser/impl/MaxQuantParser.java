package com.compomics.colims.core.io.parser.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.compomics.colims.model.QuantificationFile;
import com.compomics.colims.model.QuantificationGroup;
import com.compomics.colims.model.QuantificationMethod;
import com.compomics.util.experiment.identification.PeptideAssumption;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Parser for the text files generated by a <a
 * href="http://maxquant.org/">MaxQuant</a>run. Invokes sub-parsers such as
 * {@link MaxQuantEvidenceParser} and {@link MaxQuantMsmsParser} to handle
 * specific files contained in the text folder.
 */
@Service("maxQuantParser")
public class MaxQuantParser {

    private static final Logger LOGGER = Logger.getLogger(MaxQuantParser.class);
    private static final String MSMSTXT = "msms.txt";
    private static final String EVIDENCETXT = "evidence.txt";
    private static final String PROTEINGROUPS = "proteingroups.txt";
    private static final String PARAMETERS = "parameters.txt";
    private static final String SUMMARY = "summary.txt";
    @Autowired
    MaxQuantMsmsParser maxQuantMsmsParser;
    @Autowired
    MaxQuantProteinGroupParser maxQuantProteinGroupParser;
    @Autowired
    MaxQuantEvidenceParser maxQuantEvidenceParser;
    private static Map<Integer, PeptideAssumption> peptideAssumptions = new HashMap<>();
    private static Map<Integer, MSnSpectrum> msms = new HashMap<>();
    private static Map<Integer, ProteinMatch> proteinMap = new HashMap<>();
    private static boolean initialized = false;

    /**
     * Parse a folder containing the MaxQuant txt output files, using the
     * correct sub-parser for each known file type.
     *
     * @param maxQuantTextFolder
     * @throws IOException
     */
    public void parseMaxQuantTextFolder(final File maxQuantTextFolder) throws IOException, HeaderEnumNotInitialisedException, UnparseableException {

        //TODO parameters

        // Create a single QuantificationFile file to the argument folder, and store it in the database
        QuantificationFile quantificationFile = new QuantificationFile();
        // TODO Probably store the folder name in quantificationFile, but there are no such fields available
        // Associate the correct QuantificationMethod with this QuantificationFile
        // TODO Actually retrieve or create and store quantificationMethod, but it currently lacks a field for names
        QuantificationMethod quantificationMethod = null;
        quantificationFile.setQuantificationMethod(quantificationMethod);
        // TODO Store quantificationFile; we are currently missing a Hibernate Repository to do so, so skip for now

        // Create a new QuantificationGroup to group all the Quantifications found and link it to the QuantificationFile
        QuantificationGroup quantificationGroup = new QuantificationGroup();
        quantificationGroup.setQuantificationFile(quantificationFile);
        // TODO Store quantificationGroup; we are currently missing a Hibernate Repository to do so, so skip for now

        // Parse msms.txt and create and persist the objects found within
        LOGGER.debug("starting msms parsing");
        File msmsFile = new File(maxQuantTextFolder, MSMSTXT);
        msms = maxQuantMsmsParser.parse(msmsFile, true);

        // Parse evidence.txt and create and persist the objects found within
        LOGGER.debug("starting evidence parsing");
        File evidenceFile = new File(maxQuantTextFolder, EVIDENCETXT);
        peptideAssumptions = maxQuantEvidenceParser.parse(evidenceFile);

        //update peptide msms to best scoring msms entry

        LOGGER.debug("starting protein group parsing");
        File proteinGroupsFile = new File(maxQuantTextFolder, PROTEINGROUPS);
        proteinMap = maxQuantProteinGroupParser.parse(proteinGroupsFile);
        initialized = true;
    }

    public Iterator<PeptideAssumption> getIdentificationsFromParsedFile() {
        return peptideAssumptions.values().iterator();
    }

    public boolean hasParsedAFile() {
        return initialized;
    }

    public PeptideAssumption getIdentificationForSpectrum(MSnSpectrum aSpectrum) throws NumberFormatException {
        return peptideAssumptions.get(Integer.parseInt(aSpectrum.getSpectrumKey()));
    }

    public Iterator<MSnSpectrum> getSpectra() {
        return msms.values().iterator();
    }

    public ProteinMatch getBestProteinHitForIdentification(PeptideAssumption aPeptideAssumption) throws NumberFormatException {
        return proteinMap.get(Integer.parseInt(aPeptideAssumption.getPeptide().getKey()));
    }

    public void clearParsedProject() {
        msms.clear();
        peptideAssumptions.clear();
        proteinMap.clear();
    }
}

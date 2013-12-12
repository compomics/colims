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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
    private static final String PROTEINGROUPS = "proteinGroups.txt";
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
    private Map<String, List<MSnSpectrum>> spectraPerRunMap = new HashMap<>();

    /**
     * Parse a folder containing the MaxQuant txt output files, using the
     * correct sub-parser for each known file type.
     *
     * @param maxQuantTextFolder
     * @throws IOException
     */
    public void parseMaxQuantTextFolder(final File maxQuantTextFolder) throws IOException, HeaderEnumNotInitialisedException, UnparseableException {
        parseMaxQuantTextFolder(maxQuantTextFolder, false);
    }

    public void parseMaxQuantTextFolder(final File maxQuantTextFolder, boolean indexPerRun) throws IOException, HeaderEnumNotInitialisedException, UnparseableException {

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

        if (indexPerRun) {
            if (getSpectraPerRun().isEmpty()) {
                throw new UnparseableException("could not connect spectra to any run");
            }
        }

        // Parse evidence.txt and create and persist the objects found within
        LOGGER.debug("starting evidence parsing");
        File evidenceFile = new File(maxQuantTextFolder, EVIDENCETXT);
        peptideAssumptions = maxQuantEvidenceParser.parse(evidenceFile);

        //update peptide msms to best scoring msms entry

        LOGGER.debug("starting protein group parsing");
        File proteinGroupsFile = new File(maxQuantTextFolder, PROTEINGROUPS);
        proteinMap = maxQuantProteinGroupParser.parse(proteinGroupsFile);
        if (msms.keySet().isEmpty() || peptideAssumptions.keySet().isEmpty() || proteinMap.keySet().isEmpty()) {
            throw new UnparseableException("one of the parsed files could not be read properly");
        } else {
            initialized = true;
        }
    }

    /**
     *
     * @return
     */
    public Collection<PeptideAssumption> getIdentificationsFromParsedFile() {
        return Collections.unmodifiableCollection(peptideAssumptions.values());
    }

    public boolean hasParsedAFile() {
        return initialized;
    }

    public PeptideAssumption getIdentificationForSpectrum(MSnSpectrum aSpectrum) throws NumberFormatException {
        return peptideAssumptions.get(Integer.parseInt(aSpectrum.getSpectrumKey()));
    }

    public Collection<MSnSpectrum> getSpectra() {
        return Collections.unmodifiableCollection(msms.values());
    }

    public Collection<ProteinMatch> getProteinsFromParsedFile() {
        return Collections.unmodifiableCollection(proteinMap.values());
    }

    public ProteinMatch getBestProteinHitForIdentification(PeptideAssumption aPeptideAssumption) throws NumberFormatException {
        return proteinMap.get(Integer.parseInt(aPeptideAssumption.getPeptide().getKey()));
    }

    public Map<String, List<MSnSpectrum>> getSpectraPerRun() {
        if (spectraPerRunMap.isEmpty()) {
            for (MSnSpectrum spectrum : getSpectra()) {
                if (spectraPerRunMap.containsKey(spectrum.getFileName())) {
                    spectraPerRunMap.get(spectrum.getFileName()).add(spectrum);
                } else {
                    List<MSnSpectrum> spectraList = new ArrayList<>();
                    spectraList.add(spectrum);
                    spectraPerRunMap.put(spectrum.getFileName(), spectraList);
                }
            }
        }
        return spectraPerRunMap;
    }

    public void clearParsedProject() {
        msms.clear();
        peptideAssumptions.clear();
        proteinMap.clear();
        spectraPerRunMap.clear();
        initialized = false;
    }
}

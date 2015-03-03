package com.compomics.colims.core.io.maxquant;

import java.io.IOException;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.maxquant.headers.HeaderEnumNotInitialisedException;
import com.compomics.colims.core.io.maxquant.headers.MaxQuantSummaryHeaders;
import com.compomics.colims.model.QuantificationFile;
import com.compomics.colims.model.enums.FragmentationType;
import com.compomics.util.experiment.identification.SearchParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.compomics.util.experiment.identification.PeptideAssumption;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import java.io.File;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * Parser for the text files generated by a <a href="http://maxquant.org/">MaxQuant</a>run. Invokes sub-parsers such as
 * {@link MaxQuantEvidenceParser} and {@link MaxQuantSpectrumParser} to handle specific files contained in the text folder.
 */
@Service("maxQuantParser")
public class MaxQuantParser {

    private static final Logger LOGGER = Logger.getLogger(MaxQuantParser.class);
    private static final String MSMSTXT = "msms.txt";
    private static final String PROTEINGROUPS = "proteinGroups.txt";

    @Autowired
    private MaxQuantSpectrumParser maxQuantSpectrumParser;
    @Autowired
    private MaxQuantProteinGroupParser maxQuantProteinGroupParser;
    @Autowired
    private MaxQuantEvidenceParser maxQuantEvidenceParser;

    private Map<Integer, MSnSpectrum> msms = new HashMap<>();
    private Map<Integer, ProteinMatch> proteinMap = new HashMap<>();
    private Map<String, MaxQuantAnalyticalRun> spectraPerRunMap = new HashMap<>();
    private Map<Integer, FragmentationType> fragmentations = new HashMap<>();

    private boolean initialised = false;

    /**
     * An extra constructor for fun testing times
     * @param quantFolder
     * @throws IOException
     * @throws HeaderEnumNotInitialisedException
     * @throws MappingException
     * @throws UnparseableException
     */
    public void parseFolder(final File quantFolder) throws IOException, HeaderEnumNotInitialisedException, MappingException, UnparseableException {
        TabularFileLineValuesIterator summaryIter = new TabularFileLineValuesIterator(new File(quantFolder, "summary.txt"));
        Map<String, String> row;
        String multiplicity = null;

        while (summaryIter.hasNext()) {
            row = summaryIter.next();

            if (row.containsKey(MaxQuantSummaryHeaders.MULTIPLICITY.getColumnName())) {
                multiplicity = row.get(MaxQuantSummaryHeaders.MULTIPLICITY.getColumnName());
                break;
            }
        }

        parseFolder(quantFolder, multiplicity);
    }

    /**
     * Parse the output folder and populate the parser with various datasets
     * @param quantFolder File pointer to MaxQuant txt folder
     * @throws IOException
     * @throws com.compomics.colims.core.io.maxquant.headers.HeaderEnumNotInitialisedException
     * @throws UnparseableException
     * @throws MappingException
     */
    public void parseFolder(final File quantFolder, String multiplicity) throws IOException, HeaderEnumNotInitialisedException, UnparseableException, MappingException {
        LOGGER.debug("parsing MSMS");
        msms = maxQuantSpectrumParser.parse(new File(quantFolder, MSMSTXT), true);

        LOGGER.debug("parsing fragmentation types");
        fragmentations = maxQuantSpectrumParser.parseFragmenations((new File(quantFolder, MSMSTXT)));

        Iterator<Map.Entry<Integer, MSnSpectrum>> spectra = getSpectraFromParsedFile().entrySet().iterator();

        while (spectra.hasNext()) {
            Map.Entry<Integer, MSnSpectrum> spectrum = spectra.next();

            if (spectraPerRunMap.containsKey(spectrum.getValue().getFileName())) {
                spectraPerRunMap.get(spectrum.getValue().getFileName()).addASpectrum(spectrum.getKey(), spectrum.getValue());
            } else {
                MaxQuantAnalyticalRun maxQuantRun = new MaxQuantAnalyticalRun();
                maxQuantRun.setAnalyticalRunName(spectrum.getValue().getFileName());
                maxQuantRun.addASpectrum(spectrum.getKey(), spectrum.getValue());
                spectraPerRunMap.put((spectrum.getValue().getFileName()), maxQuantRun);
            }
        }

        if (spectraPerRunMap.isEmpty()) {
            throw new UnparseableException("could not connect spectra to any run");
        }

        LOGGER.debug("parsing evidence");
        maxQuantEvidenceParser.parse(quantFolder, multiplicity);

        LOGGER.debug("parsing protein groups");
        proteinMap = maxQuantProteinGroupParser.parse(new File(quantFolder, PROTEINGROUPS));

        if (msms.keySet().isEmpty() || maxQuantEvidenceParser.peptideAssumptions.keySet().isEmpty() || proteinMap.keySet().isEmpty()) {
            throw new UnparseableException("one of the parsed files could not be read properly");
        } else {
            initialised = true;
        }
    }

    /**
     * get all the {@code PeptideAssumption}s that were parsed from the max
     * quant folder
     *
     * @return a {@code Collection} of all the {@code PeptideAssumption}s
     */
    public Collection<PeptideAssumption> getIdentificationsFromParsedFile() {
        return Collections.unmodifiableCollection(maxQuantEvidenceParser.peptideAssumptions.values());
    }

    public boolean hasParsedAFile() {
        return initialised;
    }

    /**
     * fetch the associated identification with a spectrum, null if not present
     *
     * @param aSpectrum the spectrum to fetch the identification for
     * @return the {@code PeptideAssumption} connected to the spectrum
     * @throws NumberFormatException if the spectrum is not present in the
     * parsed file
     */
    public PeptideAssumption getIdentificationForSpectrum(MSnSpectrum aSpectrum) throws NumberFormatException {
        return maxQuantEvidenceParser.peptideAssumptions.get(((SpectrumIntUrParameterShizzleStuff) aSpectrum.getUrParam(new SpectrumIntUrParameterShizzleStuff())).getSpectrumid());
    }

    public Map<Integer, MSnSpectrum> getSpectraFromParsedFile() {
        return Collections.unmodifiableMap(msms);
    }

    public Collection<ProteinMatch> getProteinsFromParsedFile() {
        return Collections.unmodifiableCollection(proteinMap.values());
    }

    /**
     * gets the parsed protein hit that is the most likely according to max
     * quant for the parsed identification
     *
     * @param aPeptideAssumption the identification to get the protein for
     * @return the protein with the best association for the given parsed
     * identification
     * @throws NumberFormatException if the identification is not present in the
     * parsed files
     */
    public ProteinMatch getBestProteinHitForIdentification(PeptideAssumption aPeptideAssumption) throws NumberFormatException {
        return proteinMap.get(Integer.parseInt(aPeptideAssumption.getPeptide().getParentProteinsNoRemapping().get(0)));
    }

    public Collection<ProteinMatch> getProteinHitsForIdentification(PeptideAssumption aPeptideAssumption) throws NumberFormatException {
        Collection<ProteinMatch> proteins = new ArrayList<>();

        for (String proteinKey : aPeptideAssumption.getPeptide().getParentProteinsNoRemapping()) {
            proteins.add(proteinMap.get(Integer.parseInt(proteinKey)));
        }

        return Collections.unmodifiableCollection(proteins);
    }

    public Collection<MaxQuantAnalyticalRun> getRuns() {
        return Collections.unmodifiableCollection(spectraPerRunMap.values());
    }

    public FragmentationType getFragmentationType(Integer id) {
        return fragmentations.get(id);
    }

    public void clearParsedProject() {
        msms.clear();
        maxQuantEvidenceParser.clear();
        proteinMap.clear();
        spectraPerRunMap.clear();
        initialised = false;
    }
}

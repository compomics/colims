package com.compomics.colims.core.io.maxquant.parsers;

import java.io.IOException;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.maxquant.MaxQuantAnalyticalRun;
import com.compomics.colims.core.io.maxquant.urparams.SpectrumIntUrParameterShizzleStuff;
import com.compomics.colims.core.io.maxquant.TabularFileLineValuesIterator;
import com.compomics.colims.core.io.maxquant.UnparseableException;
import com.compomics.colims.core.io.maxquant.headers.HeaderEnumNotInitialisedException;
import com.compomics.colims.core.io.maxquant.headers.MaxQuantSummaryHeaders;
import com.compomics.colims.model.enums.FragmentationType;
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
    private static final String PROTEINGROUPSTXT = "proteinGroups.txt";

    @Autowired
    private MaxQuantSpectrumParser maxQuantSpectrumParser;
    @Autowired
    private MaxQuantProteinGroupParser maxQuantProteinGroupParser;
    @Autowired
    private MaxQuantEvidenceParser maxQuantEvidenceParser;

    private Map<Integer, MSnSpectrum> spectra = new HashMap<>();
    private Map<Integer, ProteinMatch> proteinMatches = new HashMap<>();
    private Map<String, MaxQuantAnalyticalRun> analyticalRuns = new HashMap<>();
    private Map<Integer, FragmentationType> fragmentations = new HashMap<>();

    private boolean parsed = false;

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
        spectra = maxQuantSpectrumParser.parse(new File(quantFolder, MSMSTXT), true);

        LOGGER.debug("parsing fragmentation types");
        fragmentations = maxQuantSpectrumParser.parseFragmentations((new File(quantFolder, MSMSTXT)));

        Iterator<Map.Entry<Integer, MSnSpectrum>> spectra = getSpectra().entrySet().iterator();

        while (spectra.hasNext()) {
            Map.Entry<Integer, MSnSpectrum> spectrum = spectra.next();

            if (analyticalRuns.containsKey(spectrum.getValue().getFileName())) {
                analyticalRuns.get(spectrum.getValue().getFileName()).addASpectrum(spectrum.getKey(), spectrum.getValue());
            } else {
                MaxQuantAnalyticalRun maxQuantRun = new MaxQuantAnalyticalRun();
                maxQuantRun.setAnalyticalRunName(spectrum.getValue().getFileName());
                maxQuantRun.addASpectrum(spectrum.getKey(), spectrum.getValue());
                analyticalRuns.put((spectrum.getValue().getFileName()), maxQuantRun);
            }
        }

        if (analyticalRuns.isEmpty()) {
            throw new UnparseableException("could not connect spectra to any run");
        }

        LOGGER.debug("parsing evidence");
        maxQuantEvidenceParser.parse(quantFolder, multiplicity);

        LOGGER.debug("parsing protein groups");
        proteinMatches = maxQuantProteinGroupParser.parse(new File(quantFolder, PROTEINGROUPSTXT));

        if (this.spectra.keySet().isEmpty() || maxQuantEvidenceParser.peptideAssumptions.keySet().isEmpty() || proteinMatches.keySet().isEmpty()) {
            throw new UnparseableException("one of the parsed files could not be read properly");
        } else {
            parsed = true;
        }
    }

    /**
     * If parser has parsed
     * @return Parsed
     */
    public boolean hasParsed() {
        return parsed;
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

    /**
     * Return a copy of the spectra map
     * @return Map of ids and spectra
     */
    public Map<Integer, MSnSpectrum> getSpectra() {
        return Collections.unmodifiableMap(spectra);
    }

    /**
     * Return a list of protein matches for a peptide assumption
     * @param peptideAssumption A peptide assumption
     * @return Collection of protein matches
     * @throws NumberFormatException
     */
    public Collection<ProteinMatch> getProteinHitsForIdentification(PeptideAssumption peptideAssumption) throws NumberFormatException {
        Collection<ProteinMatch> proteins = new ArrayList<>();

        for (String proteinKey : peptideAssumption.getPeptide().getParentProteinsNoRemapping()) {
            if (proteinMatches.containsKey(Integer.parseInt(proteinKey))) {
                proteins.add(proteinMatches.get(Integer.parseInt(proteinKey)));
            }
        }

        return Collections.unmodifiableCollection(proteins);
    }

    /**
     * Return a list copy of the spectra per run map values
     * @return Collection of runs
     */
    public Collection<MaxQuantAnalyticalRun> getRuns() {
        return Collections.unmodifiableCollection(analyticalRuns.values());
    }

    /**
     * Get fragmentation type for the given ID
     * @param id The given ID
     * @return A FragmentationType
     */
    public FragmentationType getFragmentationType(Integer id) {
        return fragmentations.get(id);
    }

    /**
     * Clear the parser
     */
    public void clear() {
        spectra.clear();
        maxQuantEvidenceParser.clear();
        proteinMatches.clear();
        analyticalRuns.clear();
        parsed = false;
    }
}

package com.compomics.colims.core.io.maxquant.parsers;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.maxquant.TabularFileLineValuesIterator;
import com.compomics.colims.core.io.maxquant.UnparseableException;
import com.compomics.colims.core.io.maxquant.headers.MaxQuantSummaryHeaders;
import com.compomics.colims.model.*;
import com.compomics.colims.model.enums.FragmentationType;
import com.google.common.io.LineReader;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Parser for the text files generated by a <a href="http://maxquant.org/">MaxQuant</a>run. Invokes sub-parsers such as
 * {@link MaxQuantEvidenceParser} and {@link MaxQuantSpectrumParser} to handle specific files contained in the text
 * folder.
 */
@Service("maxQuantParser")
public class MaxQuantParser {

    private static final Logger LOGGER = Logger.getLogger(MaxQuantParser.class);
    private static final String MSMSTXT = "msms.txt";
    private static final String PROTEINGROUPSTXT = "proteinGroups.txt";
    private static final String BLOCK_SEPARATOR = ">";   // TODO: is it always this?

    @Autowired
    private MaxQuantSpectrumParser maxQuantSpectrumParser;
    @Autowired
    private MaxQuantProteinGroupParser maxQuantProteinGroupParser;
    @Autowired
    private MaxQuantEvidenceParser maxQuantEvidenceParser;

    private Map<String, String> parsedFasta = new HashMap<>();
    private Map<Spectrum, Integer> spectrumIds = new HashMap<>();
    private Map<Integer, ProteinGroup> proteinGroups = new HashMap<>();
    private Map<String, AnalyticalRun> analyticalRuns = new HashMap<>();
    private Map<Integer, FragmentationType> fragmentations = new HashMap<>();

    private boolean parsed = false;

    /**
     * An extra constructor for fun testing times.
     *
     * @param quantFolder File pointer to MaxQuant txt folder
     * @throws IOException thrown in case of a I/O related problem
     * @throws MappingException
     * @throws UnparseableException
     */
    public void parseFolder(final File quantFolder, final FastaDb fastaDb) throws IOException, MappingException, UnparseableException {
        TabularFileLineValuesIterator summaryIter = new TabularFileLineValuesIterator(new File(quantFolder, "summary.txt"));
        Map<String, String> row;
        String multiplicity = null;

        while (summaryIter.hasNext()) {
            row = summaryIter.next();

            if (row.containsKey(MaxQuantSummaryHeaders.MULTIPLICITY.getDefaultColumnName())) {
                multiplicity = row.get(MaxQuantSummaryHeaders.MULTIPLICITY.getDefaultColumnName());
                break;
            }
        }

        parseFolder(quantFolder, fastaDb, multiplicity);
    }

    /**
     * Parse the output folder and populate the parser with various datasets.
     *
     * @param quantFolder File pointer to MaxQuant txt folder
     * @throws IOException
     * @throws UnparseableException
     * @throws MappingException
     */
    public void parseFolder(final File quantFolder, final FastaDb fastaDb, String multiplicity) throws IOException, UnparseableException, MappingException {
        LOGGER.debug("parsing MSMS");
        spectrumIds = maxQuantSpectrumParser.parse(new File(quantFolder, MSMSTXT));

        for (Map.Entry<Spectrum, Integer> entry : getSpectra().entrySet()) {
            if (analyticalRuns.containsKey(entry.getKey().getTitle())) {
                analyticalRuns.get(entry.getKey().getTitle()).getSpectrums().add(entry.getKey());
            } else {
                AnalyticalRun analyticalRun = new AnalyticalRun();
                analyticalRun.setName(entry.getKey().getTitle());
                analyticalRun.getSpectrums().add(entry.getKey());

                analyticalRuns.put(entry.getKey().getTitle(), analyticalRun);
            }
        }

        if (analyticalRuns.isEmpty()) {
            throw new UnparseableException("could not connect spectra to any run");
        }

        LOGGER.debug("parsing evidence");
        maxQuantEvidenceParser.parse(quantFolder, multiplicity);

        LOGGER.debug("parsing protein groups");
        proteinGroups = maxQuantProteinGroupParser.parse(new File(quantFolder, PROTEINGROUPSTXT), parseFasta(fastaDb));

        if (this.spectrumIds.size() == 0 || maxQuantEvidenceParser.peptides.size() == 0 || proteinGroups.size() == 0) {
            throw new UnparseableException("one of the parsed files could not be read properly");
        } else {
            parsed = true;
        }
    }

    /**
     * Parse a FASTA file into a map of protein -> sequence pairs
     *
     * @param fastaDb FASTA file to parse
     * @return String/String map of protein/sequence
     * @throws IOException If the file is invalid
     */
    public Map<String, String> parseFasta(FastaDb fastaDb) throws IOException {
        Map<String, String> parsedFasta = new HashMap<>();
        StringBuilder sequence = new StringBuilder();
        String header = "";

        try {
            LineReader reader = new LineReader(new FileReader(fastaDb.getFilePath()));
            String line = reader.readLine();

            while (line != null) {
                if (line.startsWith(BLOCK_SEPARATOR)) {
                    if (sequence.length() > 0) {
                        // extract only accession
                        parsedFasta.put(header.substring(1).split(" ")[0], sequence.toString());
                        sequence.setLength(0);
                    }

                    header = line;
                } else {
                    sequence.append(line);
                }

                line = reader.readLine();
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new IOException("Error parsing FASTA file, please check that it contains valid data");
        }

        return parsedFasta;
    }

    /**
     * If parser has parsed.
     *
     * @return Parsed
     */
    public boolean hasParsed() {
        return parsed;
    }

    /**
     * Fetch the identification associated with a spectrum
     *
     * @param spectrum the spectrum
     * @return the {@code PeptideAssumption} connected to the spectrum
     * @throws NumberFormatException if the spectrum is not present in the parsed file
     */
    public Peptide getIdentificationForSpectrum(Spectrum spectrum) throws NumberFormatException {
        return maxQuantEvidenceParser.peptides.get(spectrumIds.get(spectrum));
    }

    /**
     * Return a copy of the spectra map.
     *
     * @return Map of ids and spectra
     */
    public Map<Spectrum, Integer> getSpectra() {
        return Collections.unmodifiableMap(spectrumIds);
    }

    /**
     * Return a list of protein group matches for a peptide
     *
     * @param peptide A peptide
     * @return Collection of protein groups
     * @throws NumberFormatException
     */
    public List<ProteinGroup> getProteinHitsForIdentification(Peptide peptide) throws NumberFormatException {
        return maxQuantEvidenceParser.peptideProteins.get(peptide)
            .stream()
            .map(proteinGroups::get)
            .collect(Collectors.toList());
    }

    /**
     * Return a list copy of the spectra per run map values.
     *
     * @return Collection of runs
     */
    public Collection<AnalyticalRun> getRuns() {
        return Collections.unmodifiableCollection(analyticalRuns.values());
    }

    /**
     * Get fragmentation type for the given ID.
     *
     * @param id The given ID
     * @return A FragmentationType
     */
    public FragmentationType getFragmentationType(Integer id) {
        return fragmentations.get(id);
    }

    /**
     * Clear the parser.
     */
    public void clear() {
        spectrumIds.clear();
        maxQuantEvidenceParser.clear();
        proteinGroups.clear();
        analyticalRuns.clear();
        parsed = false;
    }
}

package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.distributed.io.maxquant.MaxQuantConstants;
import com.compomics.colims.distributed.io.maxquant.TabularFileLineValuesIterator;
import com.compomics.colims.distributed.io.maxquant.UnparseableException;
import com.compomics.colims.distributed.io.maxquant.headers.MaxQuantSummaryHeaders;
import com.compomics.colims.model.*;
import com.compomics.colims.model.enums.FastaDbType;
import com.compomics.colims.model.enums.FragmentationType;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Parser for the text files generated by a <a href="http://maxquant.org/">MaxQuant</a>run. Invokes sub-parsers such as
 * {@link MaxQuantEvidenceParser} and {@link MaxQuantSpectrumParser} to handle specific files contained in the text
 * folder.
 */
@Component("maxQuantParser")
public class MaxQuantParser {

    private static final Logger LOGGER = Logger.getLogger(MaxQuantParser.class);

    private static final String BLOCK_SEPARATOR = ">";
    private static final String SPLITTER = " ";

    private Map<Spectrum, Integer> spectrumIds = new HashMap<>();
    private Map<Integer, ProteinGroup> proteinGroups = new HashMap<>();
    private final Map<String, AnalyticalRun> analyticalRuns = new HashMap<>();
    private final Map<Integer, FragmentationType> fragmentations = new HashMap<>();
    private boolean parsed = false;

    @Autowired
    private MaxQuantSpectrumParser maxQuantSpectrumParser;
    @Autowired
    private MaxQuantProteinGroupParser maxQuantProteinGroupParser;
    @Autowired
    private MaxQuantEvidenceParser maxQuantEvidenceParser;

    /**
     * An extra constructor for fun testing times.
     *
     * @param maxQuantDirectory File pointer to MaxQuant directory
     * @param fastaDbs          the FASTA databases
     * @throws IOException          thrown in case of a I/O related problem
     * @throws MappingException     thrown in case of a mapping related problem
     * @throws UnparseableException
     */
    public void parse(File maxQuantDirectory, EnumMap<FastaDbType, FastaDb> fastaDbs) throws IOException, MappingException, UnparseableException {
        TabularFileLineValuesIterator summaryIterator = new TabularFileLineValuesIterator(new File(maxQuantDirectory, "summary.txt"));
        Map<String, String> row;
        String multiplicity = null;

        while (summaryIterator.hasNext()) {
            row = summaryIterator.next();

            if (row.containsKey(MaxQuantSummaryHeaders.MULTIPLICITY.getDefaultColumnName())) {
                multiplicity = row.get(MaxQuantSummaryHeaders.MULTIPLICITY.getDefaultColumnName());
                break;
            }
        }

        parse(maxQuantDirectory, fastaDbs, multiplicity);
    }

    /**
     * Parse the MaxQuant output folder and map the content of the different files to Colims entities.
     *
     * @param maxQuantDirectory File pointer to MaxQuant directory
     * @param fastaDbs          the FASTA database map (key: FastaDb type; value: FastaDb instance)
     * @param multiplicity      the multiplicity String
     * @throws IOException          thrown in case of an input/output related problem
     * @throws UnparseableException
     * @throws MappingException     thrown in case of a mapping related problem
     */
    public void parse(File maxQuantDirectory, EnumMap<FastaDbType, FastaDb> fastaDbs, String multiplicity) throws IOException, UnparseableException, MappingException {
        LOGGER.debug("parsing MSMS");
//        spectrumIds = maxQuantSpectrumParser.parse(new File(txtFolder, MSMS_FILE));

        //look for the MaxQuant txt directory
        File txtDirectory = new File(maxQuantDirectory, MaxQuantConstants.TXT_DIRECTORY.value());
        if (!txtDirectory.exists()) {
            throw new FileNotFoundException("The MaxQuant txt directory was not found.");
        }

        getSpectra().entrySet().stream().forEach((entry) -> {
            String rawFile = entry.getKey().getTitle().split("-")[0];   // this rather sucks

            if (analyticalRuns.containsKey(rawFile)) {
                analyticalRuns.get(rawFile).getSpectrums().add(entry.getKey());
            } else {
                AnalyticalRun analyticalRun = new AnalyticalRun();
                analyticalRun.setName(rawFile);
                analyticalRun.getSpectrums().add(entry.getKey());

                analyticalRuns.put(rawFile, analyticalRun);
            }
        });

        if (analyticalRuns.isEmpty()) {
            throw new UnparseableException("could not connect spectra to any run");
        }

        LOGGER.debug("parsing evidence");
        maxQuantEvidenceParser.parse(txtDirectory, multiplicity);

        LOGGER.debug("parsing protein groups");
        proteinGroups = maxQuantProteinGroupParser.parse(new File(maxQuantDirectory + File.separator + MaxQuantConstants.TXT_DIRECTORY.value(), MaxQuantConstants.PROTEIN_GROUPS_FILE.value()), parseFastas(fastaDbs.values()));

        if (this.spectrumIds.isEmpty() || maxQuantEvidenceParser.peptides.isEmpty() || proteinGroups.isEmpty()) {
            throw new UnparseableException("one of the parsed files could not be read properly");
        } else {
            parsed = true;
        }
    }

    /**
     * Parse the FASTA files into a map of protein -> sequence pairs.
     *
     * @param fastaDbs the FASTA files to parse
     * @return String/String map of protein/sequence
     * @throws IOException thrown in case of an input/output related problem
     */
    public Map<String, String> parseFastas(Collection<FastaDb> fastaDbs) throws IOException {
        Map<String, String> parsedFasta = new HashMap<>();

        try {
            final StringBuilder sequenceBuilder = new StringBuilder();
            String header = "";
            String line;
            for (FastaDb fastaDb : fastaDbs) {
                try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(FilenameUtils.separatorsToSystem(fastaDb.getFilePath())))) {
                    line = bufferedReader.readLine();
                    while (line != null) {
                        if (line.startsWith(BLOCK_SEPARATOR)) {
                            //add limiting check for protein store to avoid growing
                            if (sequenceBuilder.length() > 0) {
                                parsedFasta.put(header.substring(1).split(SPLITTER)[0], sequenceBuilder.toString().trim());
                                sequenceBuilder.setLength(0);
                            }
                            header = line;
                        } else {
                            sequenceBuilder.append(line);
                        }
                        line = bufferedReader.readLine();
                    }
                }
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
     * @throws NumberFormatException thrown in case of a String to numeric format conversion error.
     */
    public List<ProteinGroup> getProteinHitsForIdentification(Peptide peptide) throws NumberFormatException {
        List<ProteinGroup> peptideProteinGroups = maxQuantEvidenceParser.peptideProteins.get(peptide)
                .stream()
                .map(proteinGroups::get)
                .collect(Collectors.toList());

        peptideProteinGroups.removeIf(p -> p == null);

        return peptideProteinGroups;
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
     * Get the protein groups as a set.
     *
     * @return the protein group set
     */
    public Set<ProteinGroup> getProteinGroupSet() {
        return proteinGroups.values().stream().collect(Collectors.toSet());
    }

    /**
     * Clear the parser.
     */
    public void clear() {
        fragmentations.clear();
        spectrumIds.clear();
        maxQuantEvidenceParser.clear();
        proteinGroups.clear();
        analyticalRuns.clear();
        parsed = false;
    }
}

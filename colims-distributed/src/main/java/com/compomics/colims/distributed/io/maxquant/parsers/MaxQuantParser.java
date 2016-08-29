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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

//// TODO: 6/1/2016 make youtrack entry to separate concerns between mapper and parser class, i.e. remove all setting to parser class from mapper class
/**
 * Parser for the text files generated by a
 * <a href="http://maxquant.org/">MaxQuant</a>run. Invokes sub-parsers such as
 * {@link MaxQuantEvidenceParser} and {@link MaxQuantSpectraParser} to handle
 * specific files contained in the text folder.
 */
@Component("maxQuantParser")
public class MaxQuantParser {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(MaxQuantParser.class);

    private static final String BLOCK_SEPARATOR = ">";
    private static final String SPLITTER = " ";
    private static final String PARSE_RULE_SPLITTER = ";";
    private static final String EMPTY_HEADER_PARSE_RULE = "&gt;([^ ]*)";

    private Map<Integer, ProteinGroup> proteinGroups = new HashMap<>();
    private final Map<String, AnalyticalRun> analyticalRuns = new HashMap<>();
    private final Map<Integer, FragmentationType> fragmentations = new HashMap<>();

    private boolean parsed = false;

    @Autowired
    private MaxQuantSpectraParser maxQuantSpectraParser;
    @Autowired
    private MaxQuantProteinGroupParser maxQuantProteinGroupParser;
    @Autowired
    private MaxQuantEvidenceParser maxQuantEvidenceParser;
    @Autowired
    private MaxQuantSearchSettingsParser maxQuantSearchSettingsParser;

    /**
     * An extra method for fun testing times. @TODO do we still need this
     * method?
     *
     * @param maxQuantDirectory File pointer to MaxQuant directory
     * @param fastaDbs the FASTA databases
     * @throws IOException thrown in case of a I/O related problem
     * @throws MappingException thrown in case of a mapping related problem
     * @throws UnparseableException
     */
    public void parse(Path maxQuantDirectory, EnumMap<FastaDbType, FastaDb> fastaDbs, boolean includeContaminants, List<String> optionalHeaders) throws IOException, MappingException, UnparseableException {
        Path txtDirectory = Paths.get(maxQuantDirectory.toString() + File.separator + MaxQuantConstants.TXT_DIRECTORY.value());
        Path summaryDirectory = Paths.get(txtDirectory.toString() + File.separator + MaxQuantConstants.SUMMARY_FILE.value());
        TabularFileLineValuesIterator summaryIterator = new TabularFileLineValuesIterator(summaryDirectory.toFile());
        Map<String, String> row;
        String multiplicity = null;

        while (summaryIterator.hasNext()) {
            row = summaryIterator.next();

            if (row.containsKey(MaxQuantSummaryHeaders.MULTIPLICITY.getValue())) {
                multiplicity = row.get(MaxQuantSummaryHeaders.MULTIPLICITY.getValue());
                break;
            }
        }

        parse(maxQuantDirectory, fastaDbs, multiplicity,includeContaminants, optionalHeaders);
    }

    /**
     * Parse the MaxQuant output folder and map the content of the different
     * files to Colims entities.
     *
     * @param maxQuantDirectory File pointer to MaxQuant directory
     * @param fastaDbs the FASTA database map (key: FastaDb type; value: FastaDb
     * instance)
     * @param multiplicity the multiplicity String
     * @throws IOException thrown in case of an input/output related problem
     * @throws UnparseableException
     * @throws MappingException thrown in case of a mapping related problem
     */
    public void parse(Path maxQuantDirectory, EnumMap<FastaDbType, FastaDb> fastaDbs, String multiplicity, boolean includeContaminants, List<String> optionalHeaders) throws IOException, UnparseableException, MappingException {
        //look for the MaxQuant txt directory
        Path txtDirectory = Paths.get(maxQuantDirectory.toString() + File.separator + MaxQuantConstants.TXT_DIRECTORY.value());
        if (!txtDirectory.toFile().exists()) {
            throw new FileNotFoundException("The MaxQuant txt directory was not found.");
        }
    //    analyticalRuns.clear(); check if analytical run map is empty?
        maxQuantSearchSettingsParser.getAnalyticalRuns().forEach((k, v) -> {
            analyticalRuns.put(k.getName(), k);
        });

        //first, parse the protein groups file
        LOGGER.debug("parsing protein groups");
        proteinGroups = maxQuantProteinGroupParser.parse(new File(maxQuantDirectory + File.separator + MaxQuantConstants.TXT_DIRECTORY.value(), 
                MaxQuantConstants.PROTEIN_GROUPS_FILE.value()), parseFastas(fastaDbs.values()), includeContaminants, optionalHeaders);

        LOGGER.debug("parsing MSMS");

        // TODO: 6/8/2016 write a method for unidentified spectra
        maxQuantSpectraParser.parse(maxQuantDirectory, false, maxQuantProteinGroupParser.getOmittedProteinGroupIds());

        getSpectra().forEach((k, v) -> {
            String rawFile = k.getTitle().split("--")[0];
            if (analyticalRuns.containsKey(rawFile)) {
                analyticalRuns.get(rawFile).getSpectrums().add(k);
            } else {
                AnalyticalRun analyticalRun = new AnalyticalRun();
                analyticalRun.setName(rawFile);
                analyticalRun.getSpectrums().add(k);

                analyticalRuns.put(rawFile, analyticalRun);
            }
        });

        if (analyticalRuns.isEmpty()) {
            throw new UnparseableException("could not connect spectra to any run");
        }

        LOGGER.debug("parsing evidence");
        maxQuantEvidenceParser.parse(txtDirectory.toFile(), maxQuantProteinGroupParser.getOmittedProteinGroupIds());

        if (getSpectra().isEmpty() || maxQuantEvidenceParser.getPeptides().isEmpty() || proteinGroups.isEmpty()) {
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
                                //get parse rule from fastaDb and parse the key
                                if (fastaDb.getHeaderParseRule() == null || fastaDb.getHeaderParseRule().equals("")) {
                                    fastaDb.setHeaderParseRule(EMPTY_HEADER_PARSE_RULE);
                                }
                                Pattern pattern;
                                if (fastaDb.getHeaderParseRule().contains(PARSE_RULE_SPLITTER)) {
                                    pattern = Pattern.compile(fastaDb.getHeaderParseRule().split(PARSE_RULE_SPLITTER)[1]);
                                } else {
                                    pattern = Pattern.compile(fastaDb.getHeaderParseRule());
                                }
                                Matcher matcher = pattern.matcher(header.substring(1).split(SPLITTER)[0]);
                                if (matcher.find()) {
                                    parsedFasta.put(matcher.group(1), sequenceBuilder.toString().trim());
                                } else {
                                    parsedFasta.put(header.substring(1).split(SPLITTER)[0], sequenceBuilder.toString().trim());
                                }
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
     * Fetch the identification(s) associated with a spectrum.
     *
     * @param spectrum the spectrum
     * @return the peptide(s) connected to the spectrum
     * @throws NumberFormatException if the spectrum is not present in the
     * parsed file
     */
    public List<Peptide> getIdentificationForSpectrum(Spectrum spectrum) throws NumberFormatException {
        // TODO: 6/1/2016 move peptide list to this class.
        List<Integer> spectrumKeys = getSpectra().get(spectrum);
        List<Peptide> peptideList = new ArrayList<>();
        if (spectrumKeys != null) {
            for (int spectrumKey : spectrumKeys) {
                if (!maxQuantEvidenceParser.getPeptides().isEmpty()) {
                    peptideList.addAll(maxQuantEvidenceParser.getPeptides().get(spectrumKey));
                } else {
                    throw new java.lang.IllegalStateException("At this stage peptites map is empty");
                }
            }
        }
        return peptideList;
    }

    /**
     * Return a copy of the spectra map.
     *
     * @return Map of ids and spectra
     */
    public Map<Spectrum, List<Integer>> getSpectra() {
        return Collections.unmodifiableMap(maxQuantSpectraParser.getMaxQuantSpectra().getSpectrumIDs());
    }

    /**
     * Return a list of protein group matches for a peptide
     *
     * @param peptide the given {@link Peptide} instance
     * @return Collection of protein groups
     * @throws NumberFormatException thrown in case of a String to numeric
     * format conversion error.
     */
    public List<ProteinGroup> getProteinHitsForIdentification(Peptide peptide) throws NumberFormatException {
        List<ProteinGroup> peptideProteinGroups = maxQuantEvidenceParser.getPeptideProteins().get(peptide)
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
        maxQuantEvidenceParser.clear();
        proteinGroups.clear();
        analyticalRuns.clear();
        maxQuantSpectraParser.clear();
        maxQuantProteinGroupParser.clear();
        parsed = false;
    }
}

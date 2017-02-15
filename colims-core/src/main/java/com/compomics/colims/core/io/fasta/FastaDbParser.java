package com.compomics.colims.core.io.fasta;

import com.compomics.colims.model.FastaDb;
import com.compomics.util.protein.Header;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class parses FASTA files (protein accession and sequence).
 * <p>
 * Created by Niels Hulstaert on 7/10/16.
 */
@Component("fastaDbParser")
public class FastaDbParser {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(FastaDbParser.class);

    private static final String BLOCK_SEPARATOR = ">";
    private static final String SPLITTER = " ";
    private static final String PARSE_RULE_SPLITTER = ";";

    /**
     * Parse the given FASTA files into a map of protein accession -> sequence pairs. This method takes a {@link
     * LinkedHashMap} of {@link FastaDb} instances as keys as an argument to consistently handle possible duplicate
     * accessions between different FASTA DB files. In case of a duplicate accession, the associated protein sequence of
     * the first entry is put in the map.
     *
     * @param fastaDbs the FASTA files to parse and their associated (absolute) path
     * @return the protein sequences map (key: protein accession; value: protein sequence)
     * @throws IOException thrown in case of an input/output related problem
     */
    public Map<String, String> parse(LinkedHashMap<FastaDb, Path> fastaDbs) throws IOException {
        Map<String, String> proteinSequences = new HashMap<>();
        try {
            for (Map.Entry<FastaDb, Path> entry : fastaDbs.entrySet()) {
                FastaDb fastaDb = entry.getKey();
                Path fastaPath = entry.getValue();
                //check if the FASTA has an associated header parse rule and parse accordingly
                //otherwise, use the Compomics Utilities library
                if (fastaDb.getHeaderParseRule() == null || fastaDb.getHeaderParseRule().equals("")) {
                    parseWithoutRule(proteinSequences, fastaDb, fastaPath);
                } else {
                    parseWithRule(proteinSequences, fastaDb, fastaPath);
                }
                if (proteinSequences.isEmpty()) {
                    throw new IllegalStateException("No accessions could be parsed from the FASTA DB file(s). Are you using the correct parse rule?");
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new IOException("Error parsing FASTA file, please check that it contains valid data");
        }

        return proteinSequences;
    }

    /**
     * Parse the protein accessions from the given FASTA files into a map (key: the {@link FastaDb} instance; value: the
     * set of protein accessions). The argument is a {@link LinkedHashMap} to be able to return the the parsed
     * accessions in the same order as they were passed.
     *
     * @param fastaDbs the FASTA files to parse and their associated (absolute) path
     * @return the map of parsed accessions (key: the {@link FastaDb} instance; value: the set of protein accessions).
     * @throws IOException           thrown in case of an input/output related problem
     * @throws IllegalStateException if the set of parsed accessions of the one of the FASTA DB files is empty
     */
    public LinkedHashMap<FastaDb, Set<String>> parseAccessions(LinkedHashMap<FastaDb, Path> fastaDbs) throws IOException {
        LinkedHashMap<FastaDb, Set<String>> parsedFastas = new LinkedHashMap<>();
        try {
            for (Map.Entry<FastaDb, Path> entry : fastaDbs.entrySet()) {
                FastaDb fastaDb = entry.getKey();
                Path fastaPath = entry.getValue();
                //check if the FASTA has an associated header parse rule and parse accordingly
                //otherwise, use the Compomics Utilities library
                Set<String> accessions;
                if (fastaDb.getHeaderParseRule() == null || fastaDb.getHeaderParseRule().equals("")) {
                    accessions = parseAccessionsWithoutRule(fastaDb, fastaPath);
                } else {
                    accessions = parseAccessionsWithRule(fastaDb, fastaPath);
                }
                if (accessions.isEmpty()) {
                    throw new IllegalStateException("No accessions could be parsed from FASTA DB file " + entry.getValue().toString() + ". Are you using the correct parse rule?");
                }
                parsedFastas.put(fastaDb, accessions);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new IOException("Error parsing FASTA file, please check that it contains valid data");
        }

        return parsedFastas;
    }

    /**
     * Test the header parse rule for the given FASTA DB file.
     *
     * @param fastaPath       the FASTA DB file path
     * @param parseRule       the header parse rule
     * @param numberOfHeaders the number of headers that will be returned
     * @return the map of parsed headers (key: the original header; value: the parsed accession)
     * @throws IOException           thrown in case of an input/output related problem
     * @throws IllegalStateException if the set of parsed accessions of the one of the FASTA DB files is empty
     */
    public Map<String, String> testParseRule(Path fastaPath, String parseRule, int numberOfHeaders) throws IOException {
        Map<String, String> headers;
        try {
            //check if the FASTA has an associated header parse rule and parse accordingly
            //otherwise, use the Compomics Utilities library
            if (parseRule == null || parseRule.equals("")) {
                headers = testParseWithoutRule(fastaPath, parseRule, numberOfHeaders);
            } else {
                headers = testParseWithRule(fastaPath, parseRule, numberOfHeaders);
            }
            if (headers.isEmpty()) {
                throw new IllegalStateException("No accessions could be parsed from FASTA DB file " + fastaPath + ".");
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new IOException("Error parsing FASTA file, please check that it contains valid data");
        }

        return headers;
    }

    /**
     * Parse the given FASTA file in case a header parse rule is present.
     *
     * @param proteinSequences the protein sequences map
     * @param fastaDb          the {@link FastaDb} instance
     * @param fastaPath        the FASTA path
     * @throws IOException in case of file reading related problem
     */
    private void parseWithRule(Map<String, String> proteinSequences, FastaDb fastaDb, Path fastaPath) throws IOException {
        try (BufferedReader bufferedReader = Files.newBufferedReader(fastaPath)) {
            //compile the pattern
            Pattern pattern;
            if (fastaDb.getHeaderParseRule().contains(PARSE_RULE_SPLITTER)) {
                pattern = Pattern.compile(fastaDb.getHeaderParseRule().split(PARSE_RULE_SPLITTER)[1]);
            } else {
                pattern = Pattern.compile(fastaDb.getHeaderParseRule());
            }
            //start reading the file
            final StringBuilder sequenceBuilder = new StringBuilder();
            String fastaHeader = "";
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith(BLOCK_SEPARATOR)) {
                    //add limiting check for protein store to avoid growing
                    if (sequenceBuilder.length() > 0) {
                        Matcher matcher = pattern.matcher(fastaHeader.substring(1).split(SPLITTER)[0]);
                        if (matcher.find()) {
                            proteinSequences.putIfAbsent(matcher.group(1), sequenceBuilder.toString().trim());
                        } else {
                            proteinSequences.putIfAbsent(fastaHeader.substring(1).split(SPLITTER)[0], sequenceBuilder.toString().trim());
                        }
                        sequenceBuilder.setLength(0);
                    }
                    fastaHeader = line;
                } else {
                    sequenceBuilder.append(line);
                }
            }
        }
    }

    /**
     * Parse the given FASTA file in case no header parse rule is present.
     *
     * @param proteinSequences the protein sequences map
     * @param fastaDb          the {@link FastaDb} instance
     * @param fastaPath        the FASTA path
     * @throws IOException in case of file reading related problem
     */
    private void parseWithoutRule(Map<String, String> proteinSequences, FastaDb fastaDb, Path fastaPath) throws IOException {
        try (BufferedReader bufferedReader = Files.newBufferedReader(fastaPath)) {
            //start reading the file
            final StringBuilder sequenceBuilder = new StringBuilder();
            String fastaHeader = "";
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith(BLOCK_SEPARATOR)) {
                    //add limiting check for protein store to avoid growing
                    if (sequenceBuilder.length() > 0) {
                        Header header = Header.parseFromFASTA(fastaHeader.substring(1).split(SPLITTER)[0]);
                        proteinSequences.putIfAbsent(header.getAccessionOrRest(), sequenceBuilder.toString().trim());
                        sequenceBuilder.setLength(0);
                    }
                    fastaHeader = line;
                } else {
                    sequenceBuilder.append(line);
                }
            }
        }
    }

    /**
     * Parse the given FASTA file in case a header parse rule is present.
     *
     * @param fastaDb   the {@link FastaDb} instance
     * @param fastaPath the FASTA path
     * @return the set of parsed protein accessions
     * @throws IOException in case of file reading related problem
     */
    private Set<String> parseAccessionsWithRule(FastaDb fastaDb, Path fastaPath) throws IOException {
        Set<String> accessions = new HashSet<>();
        try (BufferedReader bufferedReader = Files.newBufferedReader(fastaPath)) {
            //compile the pattern
            Pattern pattern;
            if (fastaDb.getHeaderParseRule().contains(PARSE_RULE_SPLITTER)) {
                pattern = Pattern.compile(fastaDb.getHeaderParseRule().split(PARSE_RULE_SPLITTER)[1]);
            } else {
                pattern = Pattern.compile(fastaDb.getHeaderParseRule());
            }
            //start reading the file
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith(BLOCK_SEPARATOR)) {
                    Matcher matcher = pattern.matcher(line.substring(1).split(SPLITTER)[0]);
                    if (matcher.find()) {
                        accessions.add(matcher.group(1));
                    } else {
                        accessions.add(line.substring(1).split(SPLITTER)[0]);
                    }
                }
            }
        }

        return accessions;
    }

    /**
     * Parse the given FASTA file in case no header parse rule is present.
     *
     * @param fastaDb   the {@link FastaDb} instance
     * @param fastaPath the FASTA path
     * @return the set of parsed protein accessions
     * @throws IOException in case of file reading related problem
     */
    private Set<String> parseAccessionsWithoutRule(FastaDb fastaDb, Path fastaPath) throws IOException {
        Set<String> accessions = new HashSet<>();
        try (BufferedReader bufferedReader = Files.newBufferedReader(fastaPath)) {
            //start reading the file
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith(BLOCK_SEPARATOR)) {
                    Header header = Header.parseFromFASTA(line);
                    accessions.add(header.getAccessionOrRest());
                }
            }
        }

        return accessions;
    }

    /**
     * Parse the given FASTA file in case a header parse rule is present.
     *
     * @param fastaPath       the FASTA DB path
     * @param parseRule       the header parse rule
     * @param numberOfHeaders the number of headers that will be parsed
     * @return the set of parsed protein accessions
     * @throws IOException in case of file reading related problem
     */
    private Map<String, String> testParseWithRule(Path fastaPath, String parseRule, int numberOfHeaders) throws IOException {
        Map<String, String> headers = new HashMap<>();
        try (BufferedReader bufferedReader = Files.newBufferedReader(fastaPath)) {
            //compile the pattern
            Pattern pattern;
            if (parseRule.contains(PARSE_RULE_SPLITTER)) {
                pattern = Pattern.compile(parseRule.split(PARSE_RULE_SPLITTER)[1]);
            } else {
                pattern = Pattern.compile(parseRule);
            }
            //start reading the file
            String line;
            while ((line = bufferedReader.readLine()) != null && headers.size() < numberOfHeaders) {
                if (line.startsWith(BLOCK_SEPARATOR)) {
                    Matcher matcher = pattern.matcher(line.substring(1).split(SPLITTER)[0]);
                    if (matcher.find()) {
                        headers.put(line, matcher.group(1));
                    } else {
                        headers.put(line, line.substring(1).split(SPLITTER)[0]);
                    }
                }
            }
        }

        return headers;
    }

    /**
     * Parse the given FASTA file in case no header parse rule is present.
     *
     * @param fastaPath       the FASTA DB path
     * @param parseRule       the header parse rule
     * @param numberOfHeaders the number of headers that will be parsed
     * @return the set of parsed protein accessions
     * @throws IOException in case of file reading related problem
     */
    private Map<String, String> testParseWithoutRule(Path fastaPath, String parseRule, int numberOfHeaders) throws IOException {
        Map<String, String> headers = new HashMap<>();
        try (BufferedReader bufferedReader = Files.newBufferedReader(fastaPath)) {
            //start reading the file
            String line;
            while ((line = bufferedReader.readLine()) != null && headers.size() < numberOfHeaders) {
                if (line.startsWith(BLOCK_SEPARATOR)) {
                    Header header = Header.parseFromFASTA(line);
                    headers.put(line, header.getAccessionOrRest());
                }
            }
        }

        return headers;
    }

}

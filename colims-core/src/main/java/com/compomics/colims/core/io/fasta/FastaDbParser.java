package com.compomics.colims.core.io.fasta;

import com.compomics.colims.model.FastaDb;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
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
    private static final String EMPTY_HEADER_PARSE_RULE = "&gt;([^ ]*)";

    /**
     * Parse the given FASTA files into a map of protein accession -> sequence pairs. This method takes a {@link
     * LinkedHashMap} of {@link FastaDb} instances as keys as an argument to consistently handle possible duplicate
     * accessions between different FASTA DB files. In case of a duplicate accession, the associated protein sequence of
     * the first entry is put in the map.
     *
     * @param fastaDbs the FASTA files to parse and their associated (absolute) path
     * @return String/String map of protein/sequence
     * @throws IOException thrown in case of an input/output related problem
     */
    public Map<String, String> parseFastas(LinkedHashMap<FastaDb, Path> fastaDbs) throws IOException {
        Map<String, String> parsedFastas = new HashMap<>();
        try {
            for (Map.Entry<FastaDb, Path> entry : fastaDbs.entrySet()) {
                try (BufferedReader bufferedReader = Files.newBufferedReader(entry.getValue())) {
                    FastaDb fastaDb = entry.getKey();
                    //get parse rule from the FastaDb instance and parse the key
                    if (fastaDb.getHeaderParseRule() == null || fastaDb.getHeaderParseRule().equals("")) {
                        fastaDb.setHeaderParseRule(EMPTY_HEADER_PARSE_RULE);
                    }
                    //compile the pattern
                    Pattern pattern;
                    if (fastaDb.getHeaderParseRule().contains(PARSE_RULE_SPLITTER)) {
                        pattern = Pattern.compile(fastaDb.getHeaderParseRule().split(PARSE_RULE_SPLITTER)[1]);
                    } else {
                        pattern = Pattern.compile(fastaDb.getHeaderParseRule());
                    }
                    final StringBuilder sequenceBuilder = new StringBuilder();
                    String header = "";
                    String line;
                    //start reading the file
                    line = bufferedReader.readLine();
                    while (line != null) {
                        if (line.startsWith(BLOCK_SEPARATOR)) {
                            //add limiting check for protein store to avoid growing
                            if (sequenceBuilder.length() > 0) {
                                Matcher matcher = pattern.matcher(header.substring(1).split(SPLITTER)[0]);
                                if (matcher.find()) {
                                    parsedFastas.putIfAbsent(matcher.group(1), sequenceBuilder.toString().trim());
                                } else {
                                    parsedFastas.putIfAbsent(header.substring(1).split(SPLITTER)[0], sequenceBuilder.toString().trim());
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

        return parsedFastas;
    }

}

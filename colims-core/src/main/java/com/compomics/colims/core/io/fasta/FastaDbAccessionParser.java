package com.compomics.colims.core.io.fasta;

import com.compomics.colims.model.FastaDb;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class parses FASTA files (accession only).
 * <p>
 * Created by Niels Hulstaert on 7/10/16.
 */
@Component("fastaDbAccessionParser")
public class FastaDbAccessionParser {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(FastaDbAccessionParser.class);

    private static final String BLOCK_SEPARATOR = ">";
    private static final String SPLITTER = " ";
    private static final String PARSE_RULE_SPLITTER = ";";
    private static final String EMPTY_HEADER_PARSE_RULE = "&gt;([^ ]*)";

    /**
     * Parse the protein accessions from the given FASTA files into a map (key: the {@link FastaDb} instance; value: the
     * set of protein accessions). The argument is a {@link LinkedHashMap} to be able to return the the parsed
     * accessions in the same order as they were passed.
     *
     * @param fastaDbs the FASTA files to parse and their associated (absolute) path
     * @return the map of parsed accessions (key: the {@link FastaDb} instance; value: the set of protein accessions).
     * @throws IOException thrown in case of an input/output related problem
     */
    public LinkedHashMap<FastaDb, Set<String>> parseFastas(LinkedHashMap<FastaDb, Path> fastaDbs) throws IOException {
        LinkedHashMap<FastaDb, Set<String>> parsedFastas = new LinkedHashMap<>();
        try {
            for (Map.Entry<FastaDb, Path> entry : fastaDbs.entrySet()) {
                try (BufferedReader bufferedReader = Files.newBufferedReader(entry.getValue())) {
                    Set<String> accessions = new HashSet<>();
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
                    String line;
                    //start reading the file
                    line = bufferedReader.readLine();
                    while (line != null) {
                        if (line.startsWith(BLOCK_SEPARATOR)) {
                            try {
                                Matcher matcher = pattern.matcher(line.substring(1).split(SPLITTER)[0]);
                                if (matcher.find()) {
                                    accessions.add(matcher.group(1));
                                } else {
                                    accessions.add(line.substring(1).split(SPLITTER)[0]);
                                }
                            } catch (StringIndexOutOfBoundsException ex) {
                                System.out.println("=====");
                            }
                        }
                        line = bufferedReader.readLine();
                    }
                    parsedFastas.put(fastaDb, accessions);
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new IOException("Error parsing FASTA file, please check that it contains valid data");
        }

        return parsedFastas;
    }

}

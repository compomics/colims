package com.compomics.colims.core.io.fasta;

import com.compomics.colims.model.FastaDb;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
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
     * Parse the given FASTA files into a map of protein accession -> sequence pairs.
     *
     * @param fastaDbs the FASTA files to parse
     * @return the map of parsed accessions (key: the {@link FastaDb} instance; value: the set of protein accessions)
     * @throws IOException thrown in case of an input/output related problem
     */
    public Map<FastaDb, Set<String>> parseFastas(Collection<FastaDb> fastaDbs) throws IOException {
        Map<FastaDb, Set<String>> parsedFastas = new HashMap<>();
        try {
            String header = "";
            String line;
            for (FastaDb fastaDb : fastaDbs) {
                Set<String> accessions = new HashSet<>();
                try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(FilenameUtils.separatorsToSystem(fastaDb.getFilePath())))) {
                    //get parse rule from the FastaDb instance and parse the key
                    if (fastaDb.getHeaderParseRule() == null || fastaDb.getHeaderParseRule().equals("")) {
                        fastaDb.setHeaderParseRule(EMPTY_HEADER_PARSE_RULE);
                    }
                    line = bufferedReader.readLine();
                    while (line != null) {
                        if (line.startsWith(BLOCK_SEPARATOR)) {
                            Pattern pattern;
                            if (fastaDb.getHeaderParseRule().contains(PARSE_RULE_SPLITTER)) {
                                pattern = Pattern.compile(fastaDb.getHeaderParseRule().split(PARSE_RULE_SPLITTER)[1]);
                            } else {
                                pattern = Pattern.compile(fastaDb.getHeaderParseRule());
                            }
                            Matcher matcher = pattern.matcher(header.substring(1).split(SPLITTER)[0]);
                            if (matcher.find()) {
                                accessions.add(matcher.group(1));
                            } else {
                                accessions.add(header.substring(1).split(SPLITTER)[0]);
                            }
                            header = line;
                        }
                        line = bufferedReader.readLine();
                    }
                }
                parsedFastas.put(fastaDb, accessions);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new IOException("Error parsing FASTA file, please check that it contains valid data");
        }

        return parsedFastas;
    }

}

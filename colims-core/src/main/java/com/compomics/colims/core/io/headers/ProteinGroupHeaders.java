/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.io.headers;

import com.google.common.io.LineReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

/**
 * Parse protein group headers for user to choose which SILAC label to add to the database.
 * @author demet
 */
@Component("proteinGroupHeaders")
public class ProteinGroupHeaders {
    
    private static final char DELIMITER = '\t';
    private FileReader fileReader = null;
    private LineReader lineReader = null;

    /**
     * All headers in the protein group file.
     */
    private List<String> headersInProteinGroupFile;
    /**
     * SILAC label headers in the protein file.
     */
    private List<String> proteinGroupHeaders;

    /**
     * Get protein group headers.
     * @return proteinGroupHeaders.
     */
    public List<String> getProteinGroupHeaders() {
        return proteinGroupHeaders;
    }
    
    /**
     * Parse protein group file headers. Find the SILAC label headers and add to the proteinGroupHeaders list.
     * @param proteinGroupsFile
     * @throws IOException 
     */
    public void parseProteinGroupHeaders(Path proteinGroupsFile) throws IOException{
        proteinGroupHeaders = new ArrayList<>();
        readFileHeaders(proteinGroupsFile.toFile());
        ProteinGroupIntensityHeadersEnum.getHeaderValues().entrySet().stream().
                filter((header) -> (headersInProteinGroupFile.stream().
                        anyMatch(s -> s.startsWith(header.getKey())))).forEach((header) -> {
            proteinGroupHeaders.add(header.getValue());
        });
    }
    
    /**
     * Parse a TSV file and create a map for each line that maps the keys found on the first line to the values found to
     * the values found on lines two and further until the end of the file.
     *
     * @param tsvFile tab separated values file
     * @throws IOException
     */
    private void readFileHeaders(final File tsvFile) throws IOException {
        headersInProteinGroupFile = new ArrayList<>();
        fileReader = new FileReader(tsvFile);
        lineReader = new LineReader(fileReader);

        String firstLine = lineReader.readLine();

        if (firstLine == null || firstLine.isEmpty()) {
            throw new IOException("Input file " + tsvFile.getPath() + " is empty.");
        } else {
            String[] headers = firstLine.toLowerCase(Locale.US).split("" + DELIMITER);
            Arrays.stream(headers).forEach(e -> headersInProteinGroupFile.add(e));
        }
    }

}

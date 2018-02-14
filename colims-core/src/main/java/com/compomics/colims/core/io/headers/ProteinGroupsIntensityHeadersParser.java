/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.io.headers;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

/**
 * Parse protein group headers for user to choose which SILAC or ICAT label to
 * add to the database.
 *
 * @author demet
 */
@Component("proteinGroupsIntensityHeadersParser")
public class ProteinGroupsIntensityHeadersParser {

    private static final String DELIMITER = "\t";

    /**
     * All headers in the protein group file.
     */
    private final List<String> headersInProteinGroupFile = new ArrayList<>();
    /**
     * SILAC/ICAT label intensity headers in the protein file.
     */
    private final List<String> proteinGroupsIntensityHeaders = new ArrayList<>();

    /**
     * Get the protein groups intensity headers.
     *
     * @return the list of protein groups headers
     */
    public List<String> getProteinGroupsIntensityHeaders() {
        return proteinGroupsIntensityHeaders;
    }

    /**
     * Parse the protein groups file headers. Find the SILAC/ICAT label headers
     * and add to the proteinGroupHeaders list.
     *
     * @param proteinGroupsFile the protein groups file path
     * @throws IOException in case of a file read related problem
     */
    public void parseProteinGroupHeaders(Path proteinGroupsFile) throws IOException {
        //clear the lists before parsing
        headersInProteinGroupFile.clear();
        proteinGroupsIntensityHeaders.clear();

        //read the headers
        readProteinGroupsHeaders(proteinGroupsFile);

        ProteinGroupsIntensityHeadersEnum.getHeaderValues().stream().
                filter(header
                        -> (headersInProteinGroupFile.stream())
                        .anyMatch(proteinGroupsHeader -> isHeaderExist(header,proteinGroupsHeader)))
                .forEach((header) -> proteinGroupsIntensityHeaders.add(header));
    }

    /**
     * Check if ProteinGroupsIntensityHeaders exist in the current protein group file
     * Splitting header and checking is important because "startsWith" can also bring 
     * headers like Intensity LSD1_1 as an Intensity L.
     * @param header ProteinGroupsIntensityHeaders
     * @param proteinGroupsHeader headers from protein groups file
     * @return boolean value
     */
    private boolean isHeaderExist(String header, String proteinGroupsHeader){
        boolean exist = false;
        String subHeader = proteinGroupsHeader.split(" ")[0];
        if(subHeader.equals(header)){
            exist = true;
        }else{
            for(int i=1; i<proteinGroupsHeader.split(" ").length; i++){
                subHeader = subHeader + " " + proteinGroupsHeader.split(" ")[i];
                if(subHeader.equals(header)){
                    exist = true;
                    break;
                }
            }
        }
        return exist;
    }
    
    /**
     * Parse the header line of proteinGroups.txt file.
     *
     * @param proteinGroupsFile the proteinGroups.txt file
     * @throws IOException in case of a file read related problem
     */
    private void readProteinGroupsHeaders(final Path proteinGroupsFile) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(proteinGroupsFile)) {
            String firstLine = reader.readLine();

            if (firstLine == null || firstLine.isEmpty()) {
                throw new IOException("The proteinGroups.txt file " + proteinGroupsFile + " is empty.");
            } else {
                String[] headers = firstLine.toLowerCase(Locale.US).split(DELIMITER);
                headersInProteinGroupFile.addAll(Arrays.asList(headers));
            }
        }
    }

}

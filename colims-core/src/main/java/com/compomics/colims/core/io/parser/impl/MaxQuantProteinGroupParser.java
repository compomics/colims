package com.compomics.colims.core.io.parser.impl;

import com.compomics.util.experiment.identification.matches.ProteinMatch;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Davy
 */
public class MaxQuantProteinGroupParser {

    /**
     * parses a max quant protein groups file into memory
     *
     * @param aProteinGroupsFile the file to parse
     * @return a Map key: the protein groupid, value: the ProteinMatch
     */
    public static Map<Integer, ProteinMatch> parseMaxQuantProteinGroups(File aProteinGroupsFile) throws IOException, FileNotFoundException {
        Map<Integer, ProteinMatch> proteinGroupMap = new HashMap<>(1000);
        TabularFileLineValuesIterator iter = new TabularFileLineValuesIterator(aProteinGroupsFile);
        Map<String, String> proteinGroupLine;
        while (iter.hasNext()) {
            proteinGroupLine = iter.next();
            ProteinMatch proteinMatch = new ProteinMatch();
            String decoyString = proteinGroupLine.get(ProteinGroupHeaders.DECOY.headerName);
            boolean isDecoy = decoyString.equals("+");
//Header header = Header.parseFromFASTA(proteinGroupLine.get(ProteinGroupHeaders.FASTAHEADER.headerName));
            String parsedAccession = proteinGroupLine.get(ProteinGroupHeaders.ACCESSION.headerName);
            if (parsedAccession.contains(";")) {
                String[] accessions = parsedAccession.split(";");
                proteinMatch.setMainMatch(accessions[0]);
                proteinGroupMap.put(Integer.parseInt(proteinGroupLine.get(ProteinGroupHeaders.ID.headerName)), proteinMatch);
                for (String anAccession : accessions) {
                    proteinMatch.addTheoreticProtein(anAccession);
                }
            } else {
                proteinMatch.setMainMatch(parsedAccession);
                proteinMatch.addTheoreticProtein(parsedAccession);
                proteinGroupMap.put(Integer.parseInt(proteinGroupLine.get(ProteinGroupHeaders.ID.headerName)), proteinMatch);
            }
        }
        return proteinGroupMap;
    }

    /**
     * parses a max quant protein group file into a random access file
     *
     * @param aProteinGroupsFile the protein groups file to turn into a raf file
     * @return a Map key: the protein group id, value the line number of the
     * protein group
     */
    public static Map<Integer, Integer> createProteinGroupsRAFMAP(File aProteinGroupsFile) throws IOException, FileNotFoundException {
        Map<Integer, Integer> proteinGroupMap = new HashMap<>(1000);
        TabularFileLineValuesIterator iter = new TabularFileLineValuesIterator(aProteinGroupsFile);
        Map<String, String> proteinGroupLine;
        while (iter.hasNext()) {
            proteinGroupLine = iter.next();

        }

        return proteinGroupMap;
    }

    private enum ProteinGroupHeaders {

        ACCESSION("Protein IDs"),
        FASTAHEADER("Fasta headers"),
        PEPTIDEIDS("Peptide IDs"),
        EVIDENCEIDS("Evidence IDs"),
        MSMSIDS("MS/MS IDs"),
        BESTMSMS("Best MS/MS"),
        DECOY("Reverse"),
        CONTAMINANT("Contaminant"),
        ID("id");
        public String headerName;

        private ProteinGroupHeaders(String aHeaderName) {
            headerName = aHeaderName;
        }
    }
}

package com.compomics.colims.core.io.maxquant;

import com.compomics.util.experiment.identification.matches.ProteinMatch;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 *
 * @author Davy
 */
@Component("maxQuantProteinGroupParser")
public class MaxQuantProteinGroupParser {

    private static final Logger LOGGER = Logger.getLogger(MaxQuantProteinGroupParser.class);

    /**
     * parses a max quant protein groups file into memory
     *
     * @param aProteinGroupsFile the file to parse
     * @return a Map key: the protein groupid, value: the ProteinMatch
     */
    public Map<Integer, ProteinMatch> parse(File aProteinGroupsFile) throws IOException, FileNotFoundException, HeaderEnumNotInitialisedException, UnparseableException {
        Map<Integer, ProteinMatch> proteinGroupMap = new HashMap<>(1000);
        TabularFileLineValuesIterator iter = new TabularFileLineValuesIterator(aProteinGroupsFile, ProteinGroupHeaders.values());
        Map<String, String> values;
        while (iter.hasNext()) {
            values = iter.next();
            if (values.containsKey(ProteinGroupHeaders.ID.getColumnName())) {
                ProteinMatch proteinMatch = parseProteinMatch(values);
                proteinGroupMap.put(Integer.parseInt(values.get(ProteinGroupHeaders.ID.getColumnName())), proteinMatch);
            } else {
                throw new UnparseableException("could not find id");
            }
        }
        return proteinGroupMap;

    }

    private ProteinMatch parseProteinMatch(Map<String, String> values) throws UnparseableException, HeaderEnumNotInitialisedException {
        ProteinMatch proteinMatch = new ProteinMatch();
        if (values.containsKey(ProteinGroupHeaders.DECOY.getColumnName())) {
            String decoyString = values.get(ProteinGroupHeaders.DECOY.getColumnName());
            boolean isDecoy = decoyString.equals("+");
        }
//Header header = Header.parseFromFASTA(values.get(ProteinGroupHeaders.FASTAHEADER.headerName));
        if (values.containsKey(ProteinGroupHeaders.ACCESSION.getColumnName())) {
            String parsedAccession = values.get(ProteinGroupHeaders.ACCESSION.getColumnName());
            if (parsedAccession.contains(";")) {
                String[] accessions = parsedAccession.split(";");
                proteinMatch.setMainMatch(accessions[0]);
                for (String anAccession : accessions) {
                    proteinMatch.addTheoreticProtein(anAccession);
                }
            } else {
                proteinMatch.setMainMatch(parsedAccession);
                proteinMatch.addTheoreticProtein(parsedAccession);
            }
        } else {
            throw new UnparseableException("no accessions");
        }
        if (values.containsKey(ProteinGroupHeaders.EVIDENCEIDS.getColumnName())) {
            String evidenceids = values.get(ProteinGroupHeaders.EVIDENCEIDS.getColumnName());
            if (evidenceids.contains(";")) {
                String[] splitEvidenceIds = evidenceids.split(";");
                for (String anEvidenceId : splitEvidenceIds) {
                    proteinMatch.addPeptideMatch(anEvidenceId);
                }
            } else {
                proteinMatch.addPeptideMatch(evidenceids);
            }
        }
        return proteinMatch;
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

    private enum ProteinGroupHeaders implements HeaderEnum {

        ACCESSION(new String[]{"Protein IDs"}),
        FASTAHEADER(new String[]{"Fasta headers"}),
        PEPTIDEIDS(new String[]{"Peptide IDs"}),
        EVIDENCEIDS(new String[]{"Evidence IDs"}),
        MSMSIDS(new String[]{"MS/MS IDs"}),
        BESTMSMS(new String[]{"Best MS/MS"}),
        DECOY(new String[]{"Reverse"}),
        CONTAMINANT(new String[]{"Contaminant"}),
        ID(new String[]{"id"});
        protected String[] columnNames;
        protected int columnReference = -1;

        private ProteinGroupHeaders(final String[] fieldnames) {
            columnNames = fieldnames;
        }

        @Override
        public final String[] returnPossibleColumnNames() {
            return columnNames;
        }

        @Override
        public final void setColumnReference(int columnReference) {
            this.columnReference = columnReference;
        }

        @Override
        public final String getColumnName() throws HeaderEnumNotInitialisedException {
            if (columnNames != null) {
                if (columnReference < 0 || columnReference > (columnNames.length - 1) && columnNames.length > 0) {
                    return columnNames[0].toLowerCase(Locale.US);
                } else if (columnNames.length < 0) {
                    throw new HeaderEnumNotInitialisedException("header enum not initialised");
                } else {
                    return columnNames[columnReference].toLowerCase(Locale.US);
                }
            } else {
                throw new HeaderEnumNotInitialisedException("array was null");
            }
        }
    }
}

package com.compomics.colims.core.io.maxquant;

import com.compomics.colims.core.io.maxquant.headers.HeaderEnumNotInitialisedException;
import com.compomics.colims.core.io.maxquant.headers.MaxQuantProteinGroupHeaders;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
import java.io.File;
import java.io.IOException;
import java.util.*;

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
    public Map<Integer, ProteinMatch> parse(File aProteinGroupsFile) throws IOException, HeaderEnumNotInitialisedException, UnparseableException {
        Map<Integer, ProteinMatch> proteinGroupMap = new HashMap<>(1000);
        TabularFileLineValuesIterator iter = new TabularFileLineValuesIterator(aProteinGroupsFile, MaxQuantProteinGroupHeaders.values());
        Map<String, String> values;

        while (iter.hasNext()) {
            values = iter.next();

            if (values.containsKey(MaxQuantProteinGroupHeaders.ID.getColumnName())) {
                if ((!values.containsKey(MaxQuantProteinGroupHeaders.REVERSE.getColumnName()) || values.get(MaxQuantProteinGroupHeaders.REVERSE.getColumnName()).trim().length() == 0)
                    && (!values.containsKey(MaxQuantProteinGroupHeaders.CONTAMINANT.getColumnName()) || values.get(MaxQuantProteinGroupHeaders.CONTAMINANT.getColumnName()).trim().length() == 0)) {
                    ProteinMatch proteinMatch = parseProteinMatch(values);
                    proteinGroupMap.put(Integer.parseInt(values.get(MaxQuantProteinGroupHeaders.ID.getColumnName())), proteinMatch);
                }
            } else {
                throw new UnparseableException("could not find id");
            }
        }

        return proteinGroupMap;
    }

    private ProteinMatch parseProteinMatch(Map<String, String> values) throws UnparseableException, HeaderEnumNotInitialisedException {
        ProteinMatch proteinMatch = new ProteinMatch();

        if (values.containsKey(MaxQuantProteinGroupHeaders.ACCESSION.getColumnName())) {
            String parsedAccession = values.get(MaxQuantProteinGroupHeaders.ACCESSION.getColumnName());

            if (parsedAccession.contains(";")) {
                String[] accessions = parsedAccession.split(";");
                List<String> filteredAccessions = new ArrayList<>();

                for (String accession : accessions) {
                    if (!accession.contains("REV") && !accession.contains("CON")) {
                        filteredAccessions.add(accession);
                    }
                }

                proteinMatch.setMainMatch(filteredAccessions.get(0));

                for (String anAccession : filteredAccessions) {
                    proteinMatch.addTheoreticProtein(anAccession);
                }
            } else {
                proteinMatch.setMainMatch(parsedAccession);
                proteinMatch.addTheoreticProtein(parsedAccession);
            }
        } else {
            throw new UnparseableException("no accessions");
        }

        if (values.containsKey(MaxQuantProteinGroupHeaders.EVIDENCEIDS.getColumnName())) {
            String evidenceids = values.get(MaxQuantProteinGroupHeaders.EVIDENCEIDS.getColumnName());

            if (evidenceids.contains(";")) {
                String[] splitEvidenceIds = evidenceids.split(";");

                for (String anEvidenceId : splitEvidenceIds) {
                    proteinMatch.addPeptideMatchKey(anEvidenceId);
                }
            } else {
                proteinMatch.addPeptideMatchKey(evidenceids);
            }
        }
        return proteinMatch;
    }
}

package com.compomics.colims.core.io.maxquant.parsers;

import com.compomics.colims.core.io.maxquant.TabularFileLineValuesIterator;
import com.compomics.colims.core.io.maxquant.UnparseableException;
import com.compomics.colims.core.io.maxquant.headers.HeaderEnum;
import com.compomics.colims.core.io.maxquant.headers.HeaderEnumNotInitialisedException;
import com.compomics.colims.core.io.maxquant.headers.MaxQuantProteinGroupHeaders;
import com.compomics.util.experiment.identification.matches.ProteinMatch;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * @author Davy
 */
@Component("maxQuantProteinGroupParser")
public class MaxQuantProteinGroupParser {

    private static final HeaderEnum[] mandatoryHeaders = new HeaderEnum[]{
            MaxQuantProteinGroupHeaders.REVERSE,
            MaxQuantProteinGroupHeaders.CONTAMINANT,
            MaxQuantProteinGroupHeaders.ID,
            MaxQuantProteinGroupHeaders.ACCESSION,
            MaxQuantProteinGroupHeaders.EVIDENCEIDS
    };

    private static final Logger LOGGER = Logger.getLogger(MaxQuantProteinGroupParser.class);

    /**
     * parses a max quant protein groups file into memory
     *
     * @param aProteinGroupsFile the file to parse
     * @return a Map key: the protein groupid, value: the ProteinMatch
     */
    public Map<Integer, ProteinMatch> parse(File aProteinGroupsFile) throws IOException, UnparseableException {
        Map<Integer, ProteinMatch> proteinGroupMap = new HashMap<>(1000);
        TabularFileLineValuesIterator iter = new TabularFileLineValuesIterator(aProteinGroupsFile, mandatoryHeaders);
        Map<String, String> values;

        while (iter.hasNext()) {
            values = iter.next();
            ProteinMatch proteinMatch = parseProteinMatch(values);
            if (proteinMatch.getMainMatch() != null && !proteinMatch.getMainMatch().isEmpty()) {
                proteinGroupMap.put(Integer.parseInt(values.get(MaxQuantProteinGroupHeaders.ID.getDefaultColumnName())), proteinMatch);
            }
        }

        return proteinGroupMap;
    }

    private ProteinMatch parseProteinMatch(Map<String, String> values) throws UnparseableException {
        ProteinMatch proteinMatch = new ProteinMatch();

        String parsedAccession = values.get(MaxQuantProteinGroupHeaders.ACCESSION.getDefaultColumnName());

        if (parsedAccession.contains(";")) {
            String[] accessions = parsedAccession.split(";");
            List<String> filteredAccessions = new ArrayList<>();

            for (String accession : accessions) {
                if (!accession.contains("REV") && !accession.contains("CON")) {
                    filteredAccessions.add(accession);
                }
            }

            if (!filteredAccessions.isEmpty()) {
                proteinMatch.setMainMatch(filteredAccessions.get(0));
                filteredAccessions.forEach(proteinMatch::addTheoreticProtein);
            }

        } else {
            proteinMatch.setMainMatch(parsedAccession);
            proteinMatch.addTheoreticProtein(parsedAccession);
        }

        if (proteinMatch.getMainMatch() != null && !proteinMatch.getMainMatch().isEmpty()) {

            String evidenceids = values.get(MaxQuantProteinGroupHeaders.EVIDENCEIDS.getDefaultColumnName());

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

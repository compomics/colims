package com.compomics.colims.core.io.maxquant.parsers;

import com.compomics.colims.core.io.maxquant.TabularFileLineValuesIterator;
import com.compomics.colims.core.io.maxquant.headers.HeaderEnum;
import com.compomics.colims.core.io.maxquant.headers.MaxQuantProteinGroupHeaders;
import com.compomics.colims.core.service.ProteinService;
import com.compomics.colims.model.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Iain
 */
@Component("maxQuantProteinGroupParser")
public class MaxQuantProteinGroupParser {

    private static final HeaderEnum[] mandatoryHeaders = new HeaderEnum[]{
            MaxQuantProteinGroupHeaders.REVERSE,
            MaxQuantProteinGroupHeaders.CONTAMINANT,
            MaxQuantProteinGroupHeaders.ID,
            MaxQuantProteinGroupHeaders.ACCESSION,
            MaxQuantProteinGroupHeaders.EVIDENCEIDS,
            MaxQuantProteinGroupHeaders.PEP
    };

    private static final Logger LOGGER = Logger.getLogger(MaxQuantProteinGroupParser.class);

    @Autowired
    private ProteinService proteinService;

    public Map<Integer, ProteinGroup> parse(File proteinGroupsFile, Map<String, String> parsedFasta) throws IOException {
        Map<Integer, ProteinGroup> proteinGroups = new HashMap<>();
        TabularFileLineValuesIterator iterator = new TabularFileLineValuesIterator(proteinGroupsFile, mandatoryHeaders);

        while (iterator.hasNext()) {
            Map<String, String> values = iterator.next();

            ProteinGroup proteinGroup = parseProteinGroup(values, parsedFasta);

            if (proteinGroup.getMainProtein() != null) {
                proteinGroups.put(Integer.parseInt(values.get(MaxQuantProteinGroupHeaders.ID.getDefaultColumnName())), parseProteinGroup(values, parsedFasta));
            }
        }

        return proteinGroups;
    }

    private ProteinGroup parseProteinGroup(final Map<String, String> values, Map<String, String> parsedFasta) {
        ProteinGroup proteinGroup = new ProteinGroup();
        //proteinGroup.setProteinProbability(1.0);   // TODO: not in file

        if (values.get(MaxQuantProteinGroupHeaders.PEP.getDefaultColumnName()) != null) {
            proteinGroup.setProteinPostErrorProbability(Double.parseDouble(values.get(MaxQuantProteinGroupHeaders.PEP.getDefaultColumnName())));
        }

        String parsedAccession = values.get(MaxQuantProteinGroupHeaders.ACCESSION.getDefaultColumnName());
        List<String> filteredAccessions = new ArrayList<>();

        //  NEW PLAN!! chop off full header in fasta parse then just match on accession

        if (parsedAccession.contains(";")) {
            String[] accessions = parsedAccession.split(";");

            for (String accession : accessions) {
                if (!accession.contains("REV") && !accession.contains("CON")) {
                    filteredAccessions.add(accession);
                }
            }

            boolean isMainGroup = true;

            for (String accession : filteredAccessions) {
                proteinGroup.getProteinGroupHasProteins().add(createProteinGroupHasProtein(parsedFasta.get(accession), accession, isMainGroup));

                if (isMainGroup) {
                    isMainGroup = false;
                }
            }
        } else if (!parsedAccession.contains("REV") && !parsedAccession.contains("CON")) {
            proteinGroup.getProteinGroupHasProteins().add(createProteinGroupHasProtein(parsedFasta.get(parsedAccession), parsedAccession, true));
        }

        return proteinGroup;
    }

    private ProteinGroupHasProtein createProteinGroupHasProtein(String sequence, String accession, boolean mainGroup) {
        Protein protein = proteinService.findBySequence(sequence);

        if (protein == null) {
            protein = new Protein(sequence);
        }

        ProteinAccession proteinAccession = new ProteinAccession(accession);

        ProteinGroupHasProtein pghProtein = new ProteinGroupHasProtein();
        pghProtein.setIsMainGroupProtein(mainGroup);

        // relationships
        proteinAccession.setProtein(protein);
        protein.getProteinAccessions().add(proteinAccession);
        protein.getProteinGroupHasProteins().add(pghProtein);
        pghProtein.setProtein(protein);
        pghProtein.setProteinAccession(accession);

        return pghProtein;
    }
}

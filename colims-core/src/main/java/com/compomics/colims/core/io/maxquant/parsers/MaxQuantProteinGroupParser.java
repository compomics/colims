package com.compomics.colims.core.io.maxquant.parsers;

import com.compomics.colims.core.io.maxquant.TabularFileLineValuesIterator;
import com.compomics.colims.core.io.maxquant.headers.HeaderEnum;
import com.compomics.colims.core.io.maxquant.headers.MaxQuantProteinGroupHeaders;
import com.compomics.colims.core.service.ProteinService;
import com.compomics.colims.model.Protein;
import com.compomics.colims.model.ProteinAccession;

import java.io.File;
import java.io.IOException;
import java.util.*;

import com.compomics.colims.model.ProteinGroup;
import com.compomics.colims.model.ProteinGroupHasProtein;
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
            MaxQuantProteinGroupHeaders.EVIDENCEIDS
    };

    private static final Logger LOGGER = Logger.getLogger(MaxQuantProteinGroupParser.class);

    @Autowired
    private ProteinService proteinService;

    public Map<Integer, ProteinGroup> parse(File proteinGroupsFile) throws IOException {
        Map<Integer, ProteinGroup> proteinGroups = new HashMap<>();
        TabularFileLineValuesIterator iterator = new TabularFileLineValuesIterator(proteinGroupsFile, mandatoryHeaders);

        while (iterator.hasNext()) {
            Map<String, String> values = iterator.next();

            proteinGroups.put(Integer.parseInt(values.get(MaxQuantProteinGroupHeaders.ID.getDefaultColumnName())), parseProteinGroup(values));
        }

        return proteinGroups;
    }

    private ProteinGroup parseProteinGroup(final Map<String, String> values) {
        ProteinGroup proteinGroup = new ProteinGroup();
        proteinGroup.setProteinProbability(1.0);   // TODO: not in file

        if (values.get(MaxQuantProteinGroupHeaders.PEP.getDefaultColumnName()) != null) {
            proteinGroup.setProteinPostErrorProbability(Double.parseDouble(values.get(MaxQuantProteinGroupHeaders.PEP.getDefaultColumnName())));
        }

        // TODO!! we need the sequence from the fasta
        String temporarySequence ="BREADBREADBREADBREADBREADBREADBREADBREADBREADBREADBREADBREADBREADBREADBREADBREADBREAD";

        String parsedAccession = values.get(MaxQuantProteinGroupHeaders.ACCESSION.getDefaultColumnName());

        if (parsedAccession.contains(";")) {
            String[] accessions = parsedAccession.split(";");
            List<String> filteredAccessions = new ArrayList<>();

            for (String accession : accessions) {
                if (!accession.contains("REV") && !accession.contains("CON")) {
                    filteredAccessions.add(accession);
                }
            }

            boolean main = true;

            for (String accession : filteredAccessions) {
                if (main) {
                    main = false;
                }

                proteinGroup.getProteinGroupHasProteins().add(createProteinGroupHasProtein(temporarySequence, accession, main));
            }
        } else {
            proteinGroup.getProteinGroupHasProteins().add(createProteinGroupHasProtein(temporarySequence, parsedAccession, true));
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

package com.compomics.colims.core.io.maxquant.parsers;

import com.compomics.colims.core.io.maxquant.TabularFileLineValuesIterator;
import com.compomics.colims.core.io.maxquant.headers.HeaderEnum;
import com.compomics.colims.core.io.maxquant.headers.MaxQuantProteinGroupHeaders;
import com.compomics.colims.core.service.ProteinService;
import com.compomics.colims.model.PeptideHasProtein;
import com.compomics.colims.model.Protein;
import com.compomics.colims.model.ProteinAccession;

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
            MaxQuantProteinGroupHeaders.EVIDENCEIDS
    };

    private static final Logger LOGGER = Logger.getLogger(MaxQuantProteinGroupParser.class);

    @Autowired
    private ProteinService proteinService;
    @Autowired
    private MaxQuantEvidenceParser maxQuantEvidenceParser;

    public Map<Integer, List<PeptideHasProtein>> parse(File proteinGroupsFile) throws IOException {
        Map<Integer, List<PeptideHasProtein>> peptideHasProteins = new HashMap<>(1000);
        TabularFileLineValuesIterator iterator = new TabularFileLineValuesIterator(proteinGroupsFile, mandatoryHeaders);

        while (iterator.hasNext()) {
            Map<Integer, List<PeptideHasProtein>> proteinMap = parseProteins(iterator.next());

            if (proteinMap.size() > 0) {
                peptideHasProteins.putAll(proteinMap);
            }
        }

        return peptideHasProteins;
    }

    private Map<Integer, List<PeptideHasProtein>> parseProteins(final Map<String, String> values) {
        Map<Integer, List<PeptideHasProtein>> proteinMap = new HashMap<>();
        List<PeptideHasProtein> peptideHasProteins = new ArrayList<>();
        Map<String, Protein> accessionsProteins = new HashMap<>();
        List<String> mainGroupProteins = new ArrayList<>();

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
                    mainGroupProteins.add(accession);
                    main = false;
                }

                Protein protein = proteinService.findBySequence(temporarySequence);

                if (protein == null) {
                    protein = new Protein(temporarySequence);
                }

                ProteinAccession proteinAccession = new ProteinAccession(accession);
                proteinAccession.setProtein(protein);
                protein.getProteinAccessions().add(proteinAccession);

                accessionsProteins.put(parsedAccession, protein);
            }
        } else {
            mainGroupProteins.add(parsedAccession);

            Protein protein = proteinService.findBySequence(temporarySequence);

            if (protein == null) {
                protein = new Protein(temporarySequence);
            }

            ProteinAccession proteinAccession = new ProteinAccession(parsedAccession);
            proteinAccession.setProtein(protein);
            protein.getProteinAccessions().add(proteinAccession);

            accessionsProteins.put(parsedAccession, protein);
        }

        if (accessionsProteins.size() > 0) {
            for (Map.Entry<String, Protein> entry : accessionsProteins.entrySet()) {
                String evidenceIds = values.get(MaxQuantProteinGroupHeaders.EVIDENCEIDS.getDefaultColumnName());

                if (evidenceIds.contains(";")) {
                    for (String evidenceId : evidenceIds.split(";")) {
                        PeptideHasProtein phProtein = new PeptideHasProtein();
                        phProtein.setMainGroupProtein(mainGroupProteins.contains(entry.getKey()));
                        phProtein.setProteinAccession(entry.getKey());
                        phProtein.setProtein(entry.getValue());
                        phProtein.setPeptide(maxQuantEvidenceParser.peptides.get(Integer.parseInt(evidenceId)));

                        peptideHasProteins.add(phProtein);
                    }
                } else {
                    peptideHasProteins.add(createPeptideHasProtein(entry.getKey(), true, entry.getValue(), evidenceIds));
                }
            }
        }

        proteinMap.put(Integer.parseInt(values.get(MaxQuantProteinGroupHeaders.ID.getDefaultColumnName())), peptideHasProteins);

        return proteinMap;
    }

    private PeptideHasProtein createPeptideHasProtein(String accession, boolean mainGroup, Protein protein, String evidenceId) {
        PeptideHasProtein phProtein = new PeptideHasProtein();
        phProtein.setMainGroupProtein(mainGroup);
        phProtein.setProteinAccession(accession);
        phProtein.setProtein(protein);
        phProtein.setPeptide(maxQuantEvidenceParser.peptides.get(Integer.parseInt(evidenceId)));

        return phProtein;
    }
}

package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.core.service.ProteinService;
import com.compomics.colims.distributed.io.maxquant.TabularFileLineValuesIterator;
import com.compomics.colims.distributed.io.maxquant.headers.HeaderEnum;
import com.compomics.colims.distributed.io.maxquant.headers.MaxQuantProteinGroupHeaders;
import com.compomics.colims.model.Protein;
import com.compomics.colims.model.ProteinAccession;
import com.compomics.colims.model.ProteinGroup;
import com.compomics.colims.model.ProteinGroupHasProtein;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create grouped proteins from the protein groups file output by MaxQuant.
 *
 * @author Iain
 */
@Component("maxQuantProteinGroupParser")
public class MaxQuantProteinGroupParser {

    @Autowired
    private ProteinService proteinService;

    private static final HeaderEnum[] mandatoryHeaders = new HeaderEnum[]{
        MaxQuantProteinGroupHeaders.ACCESSION,
        MaxQuantProteinGroupHeaders.EVIDENCEIDS,
        MaxQuantProteinGroupHeaders.ID,
        MaxQuantProteinGroupHeaders.PEP
    };

    /**
     * Parse a data file and return grouped proteins.
     *
     * @param proteinGroupsFile MaxQuant protein groups file
     * @param parsedFasta FASTA parsed into header/sequence pairs
     * @return Protein groups indexed by id
     * @throws IOException
     */
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

    /**
     * Construct a group of proteins.
     *
     * @param values A row of values
     * @param parsedFasta A parsed fasta
     * @return A protein group
     */
    private ProteinGroup parseProteinGroup(Map<String, String> values, Map<String, String> parsedFasta) {
        ProteinGroup proteinGroup = new ProteinGroup();
        proteinGroup.setPeptideHasProteinGroups(new ArrayList<>());

        if (values.get(MaxQuantProteinGroupHeaders.PEP.getDefaultColumnName()) != null) {
            proteinGroup.setProteinPostErrorProbability(Double.parseDouble(values.get(MaxQuantProteinGroupHeaders.PEP.getDefaultColumnName())));
        }

        String parsedAccession = values.get(MaxQuantProteinGroupHeaders.ACCESSION.getDefaultColumnName());
        List<String> filteredAccessions = new ArrayList<>();

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

    /**
     * Create a protein and it's relation to a protein group
     * @param sequence The sequence of the protein
     * @param accession The accession of the protein
     * @param mainGroup Whether this is the main protein of the group
     * @return A ProteinGroupHasProtein object
     */
    private ProteinGroupHasProtein createProteinGroupHasProtein(String sequence, String accession, boolean mainGroup) {
        Protein protein = proteinService.findBySequence(sequence);

        if (protein == null) {
            protein = new Protein(sequence);
        }

        ProteinAccession proteinAccession = new ProteinAccession(accession);

        ProteinGroupHasProtein pghProtein = new ProteinGroupHasProtein();
        pghProtein.setIsMainGroupProtein(mainGroup);

        proteinAccession.setProtein(protein);
        protein.getProteinAccessions().add(proteinAccession);
        //protein.getProteinGroupHasProteins().add(pghProtein);
        pghProtein.setProtein(protein);
        pghProtein.setProteinAccession(accession);

        return pghProtein;
    }
}

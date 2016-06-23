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
     * @param parsedFastas      FASTA files parsed into header/sequence pairs
     * @return Protein groups indexed by id
     * @throws IOException
     */
    public Map<Integer, ProteinGroup> parse(File proteinGroupsFile, Map<String, String> parsedFastas) throws IOException {
        Map<Integer, ProteinGroup> proteinGroups = new HashMap<>();
        TabularFileLineValuesIterator iterator = new TabularFileLineValuesIterator(proteinGroupsFile, mandatoryHeaders);

        while (iterator.hasNext()) {
            Map<String, String> values = iterator.next();

            ProteinGroup proteinGroup = parseProteinGroup(values, parsedFastas);

            if (proteinGroup.getMainProtein() != null) {
                proteinGroups.put(Integer.parseInt(values.get(MaxQuantProteinGroupHeaders.ID.getValue())), parseProteinGroup(values, parsedFastas));
            }
        }

        return proteinGroups;
    }

    /**
     * Construct a group of proteins.
     *
     * @param values       A row of values
     * @param parsedFastas the parsed FASTA files
     * @return A protein group
     */
    private ProteinGroup parseProteinGroup(Map<String, String> values, Map<String, String> parsedFastas) {
        ProteinGroup proteinGroup = new ProteinGroup();

        if (values.get(MaxQuantProteinGroupHeaders.PEP.getValue()) != null) {
            proteinGroup.setProteinPostErrorProbability(Double.parseDouble(values.get(MaxQuantProteinGroupHeaders.PEP.getValue())));
        }

        String parsedAccession = values.get(MaxQuantProteinGroupHeaders.ACCESSION.getValue());
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
                String sequence = "";
                for(String key : parsedFastas.keySet()) {
                    if(key.contains(accession)){
                        sequence = parsedFastas.get(key);
                        break;
                    }
                }
                if(sequence.equals("")){
                    throw new IllegalArgumentException("Protein has no sequence in Fasta File!");
                }
                proteinGroup.getProteinGroupHasProteins().add(createProteinGroupHasProtein(sequence, accession, isMainGroup, proteinGroup));

                if (isMainGroup) {
                    isMainGroup = false;
                }
            }
        } else if (!parsedAccession.contains("REV") && !parsedAccession.contains("CON")) {
            proteinGroup.getProteinGroupHasProteins().add(createProteinGroupHasProtein(parsedFastas.get(parsedAccession), parsedAccession, true, proteinGroup));
        }

        return proteinGroup;
    }

    /**
     * Create a protein and it's relation to a protein group
     *
     * @param sequence  The sequence of the protein
     * @param accession The accession of the protein
     * @param mainGroup Whether this is the main protein of the group
     * @return A ProteinGroupHasProtein object
     */
    private ProteinGroupHasProtein createProteinGroupHasProtein(String sequence, String accession, boolean mainGroup, ProteinGroup proteinGroup) {
        Protein protein = proteinService.findBySequence(sequence);

        if (protein == null) {
            protein = new Protein(sequence);
        }

        ProteinAccession proteinAccession = new ProteinAccession(accession);

        ProteinGroupHasProtein proteinGroupHasProtein = new ProteinGroupHasProtein();
        proteinGroupHasProtein.setIsMainGroupProtein(mainGroup);

        proteinAccession.setProtein(protein);
        protein.getProteinAccessions().add(proteinAccession);
        //protein.getProteinGroupHasProteins().add(proteinGroupHasProtein);
        proteinGroupHasProtein.setProtein(protein);
        proteinGroupHasProtein.setProteinAccession(accession);
        proteinGroupHasProtein.setProteinGroup(proteinGroup);

        return proteinGroupHasProtein;
    }
}

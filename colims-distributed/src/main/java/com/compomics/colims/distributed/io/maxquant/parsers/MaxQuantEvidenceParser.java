package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.ModificationMappingException;
import com.compomics.colims.distributed.io.maxquant.MaxQuantConstants;
import com.compomics.colims.distributed.io.maxquant.TabularFileLineValuesIterator;
import com.compomics.colims.distributed.io.maxquant.UnparseableException;
import com.compomics.colims.distributed.io.maxquant.headers.HeaderEnum;
import com.compomics.colims.distributed.io.maxquant.headers.MaxQuantEvidenceHeaders;
import com.compomics.colims.distributed.io.utilities_to_colims.UtilitiesModificationMapper;
import com.compomics.colims.model.*;
import com.compomics.colims.model.enums.ModificationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Parser class for the MaxQuant evidence file.
 * <p/>
 * Created by Iain on 01/12/2014.
 */
@Component("maxQuantEvidenceParser")
public class MaxQuantEvidenceParser {

    @Autowired
    private UtilitiesModificationMapper utilitiesModificationMapper;

    private static final HeaderEnum[] MANDATORY_HEADERS = new HeaderEnum[]{
        MaxQuantEvidenceHeaders.ACETYL_PROTEIN_N_TERM,
        MaxQuantEvidenceHeaders.CHARGE,
        MaxQuantEvidenceHeaders.DELTA_SCORE,
        MaxQuantEvidenceHeaders.MASS,
        MaxQuantEvidenceHeaders.MODIFICATIONS,
        MaxQuantEvidenceHeaders.MS_MS_IDS,
        MaxQuantEvidenceHeaders.OXIDATION_M,
        MaxQuantEvidenceHeaders.PEP,
        MaxQuantEvidenceHeaders.PROTEIN_GROUP_IDS,
        MaxQuantEvidenceHeaders.SCORE,
        MaxQuantEvidenceHeaders.SEQUENCE};

    private static final String PROTEIN_GROUP_ID_DELIMITER = ";";
    /**
     * Spectrum IDs and associated quantifications.
     */
    private final Map<Integer, List<Quantification>> quantifications = new HashMap<>();
    /**
     * Spectrum IDs and peptides.
     */
    private final Map<Integer, List<Peptide>> peptides = new HashMap<>();
    /**
     * Peptides and associated protein group IDs.
     */
    private final Map<Peptide, List<Integer>> peptideProteins = new HashMap<>();

    public Map<Integer, List<Quantification>> getQuantifications() {
        return quantifications;
    }

    public Map<Integer, List<Peptide>> getPeptides() {
        return peptides;
    }

    public Map<Peptide, List<Integer>> getPeptideProteins() {
        return peptideProteins;
    }

    /**
     * This method parses an evidence file.
     *
     * @param txtDirectory the MaxQuant txt directory
     * @param omittedProteinGroupIds removed protein group IDs.
     * @throws IOException in case of an I/O related problem
     * @throws com.compomics.colims.distributed.io.maxquant.UnparseableException
     * @throws com.compomics.colims.core.io.MappingException in case of a
     * mapping problem
     */
    public void parse(File txtDirectory, List<String> omittedProteinGroupIds) throws IOException, UnparseableException, MappingException {
        TabularFileLineValuesIterator evidenceIterator = new TabularFileLineValuesIterator(new File(txtDirectory, MaxQuantConstants.EVIDENCE_FILE.value()), MANDATORY_HEADERS);

        Map<String, String> values;
        while (evidenceIterator.hasNext()) {
            values = evidenceIterator.next();

            String evidenceId = values.get(MaxQuantEvidenceHeaders.ID.getValue());
            if (evidenceId.equals("26876")) {
                System.out.println("dkfdkfkfkf");
            }

            String[] proteinGroupIds = values.get(MaxQuantEvidenceHeaders.PROTEIN_GROUP_IDS.getValue()).split(PROTEIN_GROUP_ID_DELIMITER);

            //check for peptides coming from omitted protein groups
            boolean omitPeptide = true;

            for (String proteinGroupId : proteinGroupIds) {
                if (!omittedProteinGroupIds.contains(proteinGroupId)) {
                    omitPeptide = false;
                }
            }
            if (!omitPeptide) {
                if (values.get(MaxQuantEvidenceHeaders.MS_MS_IDS.getValue()) != null) {
                    String[] msmsIds = values.get(MaxQuantEvidenceHeaders.MS_MS_IDS.getValue()).split(";");
                    for (String msmsId : msmsIds) {
                        if (!msmsId.isEmpty()) {
                            Integer spectrumID = Integer.parseInt(msmsId);
                            Peptide peptide = createPeptide(values);

                            if (!peptides.containsKey(spectrumID)) {
                                peptides.put(spectrumID, Arrays.asList(new Peptide[]{peptide}));
                            } else {
                                peptides.get(spectrumID).add(peptide);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Create a Peptide from a row in the evidence file
     *
     * @param values Set of values from a line in the file
     * @return Peptide object
     * @throws com.compomics.colims.core.io.ModificationMappingException in case
     * of a modification mapping problem
     */
    public Peptide createPeptide(Map<String, String> values) throws ModificationMappingException {
        Peptide peptide = new Peptide();

        double probability = -1;
        double pep = -1;

        if (!values.get(MaxQuantEvidenceHeaders.SCORE.getValue()).equalsIgnoreCase("nan")) {
            probability = Double.parseDouble(values.get(MaxQuantEvidenceHeaders.SCORE.getValue()));
        } else if (!values.get(MaxQuantEvidenceHeaders.DELTA_SCORE.getValue()).equalsIgnoreCase("nan")) {
            probability = Double.parseDouble(values.get(MaxQuantEvidenceHeaders.DELTA_SCORE.getValue()));
        }

        if (values.containsKey(MaxQuantEvidenceHeaders.PEP.getValue())) {
            pep = Double.parseDouble(values.get(MaxQuantEvidenceHeaders.PEP.getValue()));
        }

        peptide.setCharge(Integer.parseInt(values.get(MaxQuantEvidenceHeaders.CHARGE.getValue())));
        peptide.setPsmPostErrorProbability(pep);
        peptide.setPsmProbability(probability);
        peptide.setSequence(values.get(MaxQuantEvidenceHeaders.SEQUENCE.getValue()));
        peptide.setTheoreticalMass(Double.valueOf(values.get(MaxQuantEvidenceHeaders.MASS.getValue())));
        peptide.getPeptideHasModifications().addAll(createModifications(peptide, values));

        List<Integer> proteinGroups = new ArrayList<>();

        if (values.get(MaxQuantEvidenceHeaders.PROTEIN_GROUP_IDS.getValue()) != null) {
            for (String id : values.get(MaxQuantEvidenceHeaders.PROTEIN_GROUP_IDS.getValue()).split(";")) {
                proteinGroups.add(Integer.parseInt(id));
            }
        }

        peptideProteins.put(peptide, proteinGroups);

        return peptide;
    }

    /**
     * Clear run data from parser.
     */
    public void clear() {
        peptideProteins.clear();
        peptides.clear();
        quantifications.clear();
    }

    /**
     * Create modifications for the given peptide.
     *
     * @param peptide Peptide to associate with modifications
     * @param values Row of data from evidence file
     * @return List of PeptideHasModification objects
     */
    private List<PeptideHasModification> createModifications(Peptide peptide, Map<String, String> values) throws ModificationMappingException {
        List<PeptideHasModification> peptideHasModifications = new ArrayList<>();

        String modifications = values.get(MaxQuantEvidenceHeaders.MODIFICATIONS.getValue());

        if (modifications == null || "Unmodified".equalsIgnoreCase(modifications)) {
            return peptideHasModifications;
        } else {
            ArrayList<String[]> modificationList = new ArrayList<>();

            for (String mod : modifications.split(";")) {
                String modWithLowerCase[] = {mod, mod.toLowerCase()};
                modificationList.add(modWithLowerCase);
            }

            for (String[] modificationHeader : modificationList) {
                if (Character.isDigit(modificationHeader[1].charAt(0))) {
                    modificationHeader[1] = modificationHeader[1].split(" ", 2)[1];
                    modificationHeader[0] = modificationHeader[0].split(" ", 2)[1];
                }

                String modificationString = values.get(modificationHeader[1]);

                if (modificationString != null) {
                    if (modificationHeader[1].contains("n-term") && "1".equals(modificationString)) {
                        PeptideHasModification phModification = createPeptideHasModification(100.0, 0, 100.0, peptide);

                        Modification modification = utilitiesModificationMapper.mapByName(modificationHeader[0].split(" ")[0]);

                        //modification.getPeptideHasModifications().add(phModification);
                        phModification.setModification(modification);
                        peptideHasModifications.add(phModification);
                    }

                    if (modificationHeader[1].contains("c-term") && "1".equals(modificationString)) {
                        PeptideHasModification phModification = createPeptideHasModification(100.0, values.get(MaxQuantEvidenceHeaders.SEQUENCE.getValue()).length() - 1, 100.0, peptide);

                        Modification modification = utilitiesModificationMapper.mapByName(modificationHeader[0].split(" ")[0]);
                        //modification.getPeptideHasModifications().add(phModification);

                        phModification.setModification(modification);

                        peptideHasModifications.add(phModification);
                    }

                    // TODO: find some test data for this
                    if ((modificationString = values.get(modificationHeader[1] + " probabilities")) != null && modificationString.contains("(")) {
                        modificationString = modificationString.replaceAll("_", "");
                        int location = -1;
                        int previousLocation = 0;
                        String[] modificationLocations = modificationString.split("\\(");
                        String[] modificationDeltaScores = null;

                        if (values.containsKey(modificationHeader[1] + " score diffs")) {
                            modificationDeltaScores = values.get(modificationHeader[1] + " score diffs").split("\\(");
                        }

                        for (int i = 0; i < modificationLocations.length; i++) {
                            if (modificationLocations[i].contains(")")) {
                                PeptideHasModification phModification = new PeptideHasModification();

                                phModification.setModificationType(ModificationType.VARIABLE);
                                phModification.setPeptide(peptide);

                                Modification modification = utilitiesModificationMapper.mapByName(modificationHeader[0].split(" ")[0]);
                                phModification.setModification(modification);
                                if (modificationDeltaScores != null) {
                                    phModification.setDeltaScore(Double.parseDouble(modificationDeltaScores[i].substring(0, modificationDeltaScores[i].indexOf(")"))));
                                } else {
                                    phModification.setDeltaScore(-1.0);
                                }

                                phModification.setProbabilityScore(Double.parseDouble(modificationLocations[i].substring(0, modificationLocations[i].indexOf(")"))));

                                modificationLocations[i] = modificationLocations[i].replaceFirst(".*\\)", "");
                                phModification.setLocation(modificationLocations[i - 1].length());

                                peptideHasModifications.add(phModification);
                            }

                            previousLocation = location + previousLocation;
                        }
                    }
                }
            }
        }

        return peptideHasModifications;
    }

    /**
     * Create a PeptideHasModification instance for the given peptide.
     *
     * @param deltaScore the delta score value
     * @param location the modification location
     * @param probability the probability score
     * @param peptide the Peptide instance
     * @return the PeptideHasModification instance
     */
    private PeptideHasModification createPeptideHasModification(double deltaScore, int location, double probability, Peptide peptide) {
        PeptideHasModification phModification = new PeptideHasModification();

        phModification.setModificationType(ModificationType.VARIABLE);
        phModification.setDeltaScore(deltaScore);
        phModification.setLocation(location);
        phModification.setProbabilityScore(probability);
        phModification.setPeptide(peptide);

        return phModification;
    }

}

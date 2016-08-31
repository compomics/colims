package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.ModificationMappingException;
import com.compomics.colims.distributed.io.maxquant.TabularFileLineValuesIterator;
import com.compomics.colims.distributed.io.maxquant.UnparseableException;
import com.compomics.colims.distributed.io.maxquant.headers.HeaderEnum;
import com.compomics.colims.distributed.io.maxquant.headers.MaxQuantEvidenceHeaders;
import com.compomics.colims.distributed.io.utilities_to_colims.UtilitiesModificationMapper;
import com.compomics.colims.model.*;
import com.compomics.colims.model.enums.ModificationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
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
    private static final String MODIFICATION_DELIMITER = ";";
    private static final String UNMODIFIED = "Unmodified";
    protected static final String N_TERMINAL_MODIFICATION = "Protein N-term";
    protected static final String C_TERMINAL_MODIFICATION = "Protein C-term";
    protected static final String MODIFICATION_PROBABILITIES = " probabilities";
    protected static final String MODIFICATION_SCORE_DIFFS = " score diffs";
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
     * @param evidenceFilePath the MaxQuant evidence file path
     * @param omittedProteinGroupIds removed protein group IDs.
     * @throws IOException in case of an I/O related problem
     * @throws com.compomics.colims.distributed.io.maxquant.UnparseableException
     * @throws com.compomics.colims.core.io.MappingException in case of a
     * mapping problem
     */
    public void parse(Path evidenceFilePath, List<String> omittedProteinGroupIds) throws IOException, UnparseableException, MappingException {
        TabularFileLineValuesIterator evidenceIterator = new TabularFileLineValuesIterator(evidenceFilePath.toFile(), MANDATORY_HEADERS);

        Map<String, String> values;
        while (evidenceIterator.hasNext()) {
            values = evidenceIterator.next();

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

        //get the modifications
        String modifications = values.get(MaxQuantEvidenceHeaders.MODIFICATIONS.getValue());
        if (modifications == null || modifications.equalsIgnoreCase(UNMODIFIED)) {
            return peptideHasModifications;
        } else {
            for (String modificationString : modifications.split(MODIFICATION_DELIMITER)) {
                EvidenceModification evidenceModification = new EvidenceModification(modificationString);

                /**
                 * Check the modification type (N-terminal, C-terminal or
                 * non-terminal). Only non-terminal modifications have an
                 * additional probabilities entry.
                 */
                if (evidenceModification.isNTerminal()) {
                    PeptideHasModification phModification = createPeptideHasModification(100.0, 0, 100.0, peptide);

                    Modification modification = utilitiesModificationMapper.mapByName(evidenceModification.getModificationName());

                    //modification.getPeptideHasModifications().add(phModification);
                    phModification.setModification(modification);
                    peptideHasModifications.add(phModification);
                } else if (evidenceModification.isCTerminal()) {
                    PeptideHasModification phModification = createPeptideHasModification(100.0, values.get(MaxQuantEvidenceHeaders.SEQUENCE.getValue()).length() - 1, 100.0, peptide);

                    Modification modification = utilitiesModificationMapper.mapByName(evidenceModification.getModificationName());

                    //modification.getPeptideHasModifications().add(phModification);
                    phModification.setModification(modification);
                    peptideHasModifications.add(phModification);
                }

                //get the modifications probabilities
                String probabilitiesString = values.get(evidenceModification.getLowerCaseFullModificationName() + MODIFICATION_PROBABILITIES);
                if (probabilitiesString != null) {
//                    modificationString = modificationString.replaceAll("_", "");
                    int location = 0;
                    int previousLocation = 0;
                    String[] modificationLocations = modificationString.split("\\(");
                    String[] modificationDeltaScores = null;

                    String scoreDiffsString = values.get(evidenceModification.getLowerCaseFullModificationName() + MODIFICATION_SCORE_DIFFS);
                    if (values.containsKey(modificationHeader[1] + MODIFICATION_SCORE_DIFFS)) {
                        modificationDeltaScores = values.get(modificationHeader[1] + MODIFICATION_SCORE_DIFFS).split("\\(");
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
                            if (i != 0) {
                                location = modificationLocations[i - 1].length() + previousLocation;

                                phModification.setLocation(location);

                                peptideHasModifications.add(phModification);
                            }

                            previousLocation = location;
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

/**
 * Convenience class for parsing modification entries from the evidence file.
 *
 * @author Niels Hulstaert
 */
class EvidenceModification {

    /**
     * The full modification modificicationName (with brackets).
     */
    private String fullModificationName;
    /**
     * The modification modificicationName.
     */
    private String modificicationName;
    /**
     * The content between brackets.
     */
    private String betweenBrackets;
    /**
     * The occurrence of the modification in the peptide. This value is null if
     * the modification occurs only once.
     */
    private Integer occurrence;

    public EvidenceModification(String modificationString) {
        //check if the modification string contains an occurance count
        if (Character.isDigit(modificationString.charAt(0))) {
            String[] split = modificationString.split(" ", 2);
            occurrence = Integer.valueOf(split[0]);
            this.fullModificationName = split[1];
            parseModificationName(split[1]);
        } else {
            this.fullModificationName = modificationString;
            parseModificationName(modificationString);
        }
    }

    public String getFullModificationName() {
        return fullModificationName;
    }

    public String getBetweenBrackets() {
        return betweenBrackets;
    }

    public String getModificationName() {
        return modificicationName;
    }

    public int getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(int occurance) {
        this.occurrence = occurance;
    }

    public String getLowerCaseModificationName() {
        return modificicationName.toLowerCase();
    }

    public String getLowerCaseFullModificationName() {
        return fullModificationName.toLowerCase();
    }

    public boolean isNTerminal() {
        return betweenBrackets.equalsIgnoreCase(MaxQuantEvidenceParser.N_TERMINAL_MODIFICATION);
    }

    public boolean isCTerminal() {
        return betweenBrackets.equalsIgnoreCase(MaxQuantEvidenceParser.C_TERMINAL_MODIFICATION);
    }

    private void parseModificationName(String modificationName) {
        String[] split = modificationName.split(" ", 2);
        modificicationName = split[0];
        if (split.length == 2) {
            //remove brackets
            betweenBrackets = split[1].substring(1, split[1].length() - 1);
        }
    }

}

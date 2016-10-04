package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.ontology.OntologyMapper;
import com.compomics.colims.core.ontology.OntologyTerm;
import com.compomics.colims.distributed.io.ModificationMapper;
import com.compomics.colims.distributed.io.maxquant.TabularFileIterator;
import com.compomics.colims.distributed.io.maxquant.headers.EvidenceHeader;
import com.compomics.colims.distributed.io.maxquant.headers.EvidenceHeaders;
import com.compomics.colims.model.Modification;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.colims.model.enums.ModificationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser class for the MaxQuant evidence file.
 * <p/>
 * Created by Iain on 01/12/2014.
 */
@Component("maxQuantEvidenceParser")
public class MaxQuantEvidenceParser {

    private static final String PROTEIN_GROUP_ID_DELIMITER = ";";
    private static final String MODIFICATION_DELIMITER = ";";
    private static final String MS_MS_IDS_DELIMITER = ";";
    private static final String NAN = "nan";
    private static final String UNMODIFIED = "Unmodified";
    private static final Pattern MODIFICATION_PATTERN = Pattern.compile("\\(([^)]+)\\)");
    private static final String MODIFICATION_PROBABILITIES = " probabilities";
    private static final String MODIFICATION_SCORE_DIFFS = " score diffs";
    static final String N_TERMINAL_MODIFICATION = "Protein N-term";
    static final String C_TERMINAL_MODIFICATION = "Protein C-term";

    /**
     * Spectrum IDs and peptides.
     */
    private final Map<Integer, List<Peptide>> peptides = new HashMap<>();
    /**
     * Peptides and associated protein group IDs.
     */
    private final Map<Peptide, List<Integer>> peptideProteins = new HashMap<>();
    /**
     * The MaxQuant to UNIMOD modification mappings.
     */
    private final Map<String, OntologyTerm> modificationMappings;
    /**
     * The evidence evidenceHeaders.
     */
    private final EvidenceHeaders evidenceHeaders;
    /**
     * Beans.
     */
    private final ModificationMapper modificationMapper;

    /**
     * No-arg constructor.
     *
     * @param modificationMapper the modification mapper
     * @param ontologyMapper     the ontology mapper
     * @throws IOException in case of an Input/Output related problem while parsing the headers.
     */
    @Autowired
    public MaxQuantEvidenceParser(ModificationMapper modificationMapper, OntologyMapper ontologyMapper) throws IOException {
        this.modificationMapper = modificationMapper;
        //get the modification mappings from the OntologyMapper
        modificationMappings = ontologyMapper.getMaxQuantMapping().getModifications();
        evidenceHeaders = new EvidenceHeaders();
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
     * @param evidenceFilePath       the MaxQuant evidence file path
     * @param omittedProteinGroupIds removed protein group IDs.
     * @throws IOException      in case of an I/O related problem
     * @throws MappingException in case of a mapping problem
     */
    public void parse(Path evidenceFilePath, List<String> omittedProteinGroupIds) throws IOException, MappingException {
        TabularFileIterator evidenceIterator = new TabularFileIterator(evidenceFilePath, evidenceHeaders.getMandatoryHeaders());

        Map<String, String> evidenceEntry;
        while (evidenceIterator.hasNext()) {
            evidenceEntry = evidenceIterator.next();

            String[] proteinGroupIds = evidenceEntry.get(evidenceHeaders.get(EvidenceHeader.PROTEIN_GROUP_IDS)).split(PROTEIN_GROUP_ID_DELIMITER);

            //check for peptides coming from omitted protein groups
            //if one of the protein groups is not in the omitted list, include the peptide
            boolean omitPeptide = true;
            for (String proteinGroupId : proteinGroupIds) {
                if (!omittedProteinGroupIds.contains(proteinGroupId)) {
                    omitPeptide = false;
                    break;
                }
            }
            if (!omitPeptide) {
                String[] msmsIds = evidenceEntry.get(evidenceHeaders.get(EvidenceHeader.MS_MS_IDS)).split(MS_MS_IDS_DELIMITER);
                for (String msmsIdString : msmsIds) {
                    if (!msmsIdString.isEmpty()) {
                        Integer msmsId = Integer.parseInt(msmsIdString);
                        Peptide peptide = createPeptide(evidenceEntry);

                        if (!peptides.containsKey(msmsId)) {
                            peptides.put(msmsId, Arrays.asList(new Peptide[]{peptide}));
                        } else {
                            peptides.get(msmsId).add(peptide);
                        }
                    }
                }
            }
        }
    }

    /**
     * Create a Peptide from a row entry in the evidence file.
     *
     * @param evidenceEntry key-value pairs from an evidence entry
     * @return the mapped Peptide object
     */
    public Peptide createPeptide(Map<String, String> evidenceEntry) {
        Peptide peptide = new Peptide();

        if (!evidenceEntry.get(evidenceHeaders.get(EvidenceHeader.SCORE)).equalsIgnoreCase(NAN)) {
            Double probability = Double.parseDouble(evidenceEntry.get(evidenceHeaders.get(EvidenceHeader.SCORE)));
            peptide.setPsmProbability(probability);
        }

        if (!evidenceEntry.get(evidenceHeaders.get(EvidenceHeader.PEP)).equalsIgnoreCase(NAN)) {
            Double pep = Double.parseDouble(evidenceEntry.get(evidenceHeaders.get(EvidenceHeader.PEP)));
            peptide.setPsmPostErrorProbability(pep);
        }

        peptide.setCharge(Integer.parseInt(evidenceEntry.get(evidenceHeaders.get(EvidenceHeader.CHARGE))));
        peptide.setSequence(evidenceEntry.get(evidenceHeaders.get(EvidenceHeader.SEQUENCE)));
        peptide.setTheoreticalMass(Double.valueOf(evidenceEntry.get(evidenceHeaders.get(EvidenceHeader.MASS))));
        peptide.getPeptideHasModifications().addAll(createModifications(peptide, evidenceEntry));

        List<Integer> proteinGroups = new ArrayList<>();
        if (evidenceEntry.get(evidenceHeaders.get(EvidenceHeader.PROTEIN_GROUP_IDS)) != null) {
            for (String id : evidenceEntry.get(evidenceHeaders.get(EvidenceHeader.PROTEIN_GROUP_IDS)).split(PROTEIN_GROUP_ID_DELIMITER)) {
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
    }

    /**
     * Create modifications for the given peptide.
     *
     * @param peptide Peptide to associate with modifications
     * @param values  Row of data from evidence file
     * @return List of PeptideHasModification objects
     */
    private List<PeptideHasModification> createModifications(Peptide peptide, Map<String, String> values) {
        List<PeptideHasModification> peptideHasModifications = new ArrayList<>();

        //get the modifications
        String modificationsEntry = values.get(evidenceHeaders.get(EvidenceHeader.MODIFICATIONS));
        if (modificationsEntry.equalsIgnoreCase(UNMODIFIED)) {
            return peptideHasModifications;
        } else {
            for (String modificationString : modificationsEntry.split(MODIFICATION_DELIMITER)) {
                EvidenceModification evidenceModification = new EvidenceModification(modificationString);
                Modification modification;
                //try to the modification name to an ontology term
                if (modificationMappings.containsKey(evidenceModification.getFullModificationName())) {
                    OntologyTerm modificationTerm = modificationMappings.get(evidenceModification.getFullModificationName());
                    modification = modificationMapper.mapByOntologyTerm(modificationTerm.getOntologyPrefix(),
                            modificationTerm.getOboId(),
                            modificationTerm.getLabel());
                } else {
                    modification = modificationMapper.mapByName(evidenceModification.getFullModificationName());
                }

                //Check the modification type (N-terminal, C-terminal or
                //non-terminal). Only non-terminal modifications have an
                //additional probabilities entry.
                //@// TODO: 29/09/16 ask for a good value for the terminal modification score
                if (evidenceModification.isNTerminal()) {
                    PeptideHasModification peptideHasModification = createPeptideHasModification(0, 100.0, null, peptide);

                    peptideHasModification.setModification(modification);
                    peptideHasModifications.add(peptideHasModification);
                } else if (evidenceModification.isCTerminal()) {
                    PeptideHasModification peptideHasModification = createPeptideHasModification(values.get(evidenceHeaders.get(EvidenceHeader.SEQUENCE)).length() - 1, 100.0, null, peptide);

                    peptideHasModification.setModification(modification);
                    peptideHasModifications.add(peptideHasModification);
                } else {
                    //Get the modifications probabilities for non-terminal modifications.
                    //This field can be empty in the evidence file, even though the peptide has modifications.
                    String probabilitiesString = values.get(evidenceModification.getLowerCaseFullModificationName() + MODIFICATION_PROBABILITIES);
                    String deltasString = values.get(evidenceModification.getLowerCaseFullModificationName() + MODIFICATION_SCORE_DIFFS);
                    //Array to store the parsed scores and the affected amino acid location.
                    //(Element 0: the probability score; element 1: the delta score; element 2: the affected amino acid location starting from 1)
                    List<Object[]> scoresAndLocations = new ArrayList<>();
                    if (probabilitiesString != null && deltasString != null) {
                        //first, parse the modification probability scores and delta scores between brackets
                        Matcher probabilities = MODIFICATION_PATTERN.matcher(probabilitiesString);
                        Matcher deltas = MODIFICATION_PATTERN.matcher(deltasString);
                        //keep track of the overhead from the scores in the sequence to get the right affected amino acid location
                        int probabilityOverhead = 1;
                        while (probabilities.find() && deltas.find()) {
                            Object[] scoresAndLocation = new Object[3];
                            scoresAndLocation[0] = Double.parseDouble(probabilities.group(1));
                            scoresAndLocation[1] = Double.parseDouble(deltas.group(1));
                            scoresAndLocation[2] = probabilities.start(1) - probabilityOverhead;

                            scoresAndLocations.add(scoresAndLocation);

                            probabilityOverhead += probabilities.end() - probabilities.start();
                        }

                        /**
                         * Check the modification occurrences;
                         * if the number of scores is higher than the occurrences rate, take the one with the highest score.
                         */
                        if (evidenceModification.getOccurrences() != scoresAndLocations.size()) {
                            //sort the scores in descending order
                            scoresAndLocations.sort(((o1, o2) -> ((Double) o2[0]).compareTo((Double) o1[0])));
                        }
                    }

                    for (int i = 0; i < evidenceModification.getOccurrences(); i++) {
                        if (!scoresAndLocations.isEmpty()) {
                            PeptideHasModification peptideHasModification = createPeptideHasModification((Integer) scoresAndLocations.get(i)[2], (Double) scoresAndLocations.get(i)[0], (Double) scoresAndLocations.get(i)[1], peptide);

                            peptideHasModification.setModification(modification);
                            peptideHasModifications.add(peptideHasModification);
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
     * @param location    the modification location
     * @param probability the probability score
     * @param deltaScore  the delta score value
     * @param peptide     the Peptide instance
     * @return the PeptideHasModification instance
     */
    private PeptideHasModification createPeptideHasModification(Integer location, Double probability, Double deltaScore, Peptide peptide) {
        PeptideHasModification peptideHasModification = new PeptideHasModification();

        peptideHasModification.setLocation(location);
        peptideHasModification.setProbabilityScore(probability);
        peptideHasModification.setDeltaScore(deltaScore);
        peptideHasModification.setPeptide(peptide);

        return peptideHasModification;
    }

}

/**
 * Convenience class for parsing modification entries from the evidence file.
 *
 * @author Niels Hulstaert
 */
class EvidenceModification {

    /**
     * The full modification modificationName (with brackets).
     */
    private final String fullModificationName;
    /**
     * The modification name.
     */
    private String modificationName;
    /**
     * The affected amino acid (between brackets in the evidence file).
     */
    private String affectedAminoAcid;
    /**
     * The number of occurrences of the modification in the peptide. This value is null if
     * the modification occurs only once.
     */
    private Integer occurrences = 1;

    EvidenceModification(String modificationString) {
        //check if the modification string contains an occurrence count
        if (Character.isDigit(modificationString.charAt(0))) {
            String[] split = modificationString.split(" ", 2);
            occurrences = Integer.valueOf(split[0]);
            this.fullModificationName = split[1];
            parseModificationName(split[1]);
        } else {
            this.fullModificationName = modificationString;
            parseModificationName(modificationString);
        }
    }

    String getFullModificationName() {
        return fullModificationName;
    }

    String getAffectedAminoAcid() {
        return affectedAminoAcid;
    }

    String getModificationName() {
        return modificationName;
    }

    int getOccurrences() {
        return occurrences;
    }

    String getLowerCaseModificationName() {
        return modificationName.toLowerCase();
    }

    String getLowerCaseFullModificationName() {
        return fullModificationName.toLowerCase();
    }

    boolean isNTerminal() {
        return affectedAminoAcid.equalsIgnoreCase(MaxQuantEvidenceParser.N_TERMINAL_MODIFICATION);
    }

    boolean isCTerminal() {
        return affectedAminoAcid.equalsIgnoreCase(MaxQuantEvidenceParser.C_TERMINAL_MODIFICATION);
    }

    /**
     * Parse the modification name from evidence file entry.
     *
     * @param modificationEntry the evidence modification entry
     */
    private void parseModificationName(String modificationEntry) {
        String[] split = modificationEntry.split(" ", 2);
        this.modificationName = split[0];
        if (split.length == 2) {
            //remove brackets
            affectedAminoAcid = split[1].substring(1, split[1].length() - 1);
        }
    }

}

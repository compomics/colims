package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.core.ontology.ModificationOntologyTerm;
import com.compomics.colims.core.ontology.OntologyMapper;
import com.compomics.colims.core.ontology.OntologyTerm;
import com.compomics.colims.distributed.io.ModificationMapper;
import com.compomics.colims.distributed.io.maxquant.TabularFileIterator;
import com.compomics.colims.distributed.io.maxquant.headers.EvidenceHeader;
import com.compomics.colims.distributed.io.maxquant.headers.EvidenceHeaders;
import com.compomics.colims.model.Modification;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Parser class for the MaxQuant evidence file.
 * <p/>
 * Created by Iain on 01/12/2014.
 */
@Component("maxQuantEvidenceParser")
public class MaxQuantEvidenceParser {

    private static final String PROTEIN_GROUP_ID_DELIMITER = ";";
    private static final String MODIFICATION_DELIMITER = ",";
    private static final String MS_MS_IDS_DELIMITER = ";";
    private static final String NAN = "nan";
    private static final String UNMODIFIED = "Unmodified";
    private static final Pattern MODIFICATION_PATTERN = Pattern.compile("\\(([^)]+)\\)");
    private static final String MODIFICATION_PROBABILITIES = " probabilities";
    private static final String MODIFICATION_SCORE_DIFFS = " score diffs";
    private static final String MODIFIED_SEQUENCE_FIX = "_";
    static final String N_TERMINAL_MODIFICATION = "Protein N-term";
    static final String C_TERMINAL_MODIFICATION = "Protein C-term";

    /**
     * The parsed peptides map (key: evidence ID; value: the {@link Peptide}
     * object).
     */
    private final Map<Integer, Peptide> peptides = new HashMap<>();
    /**
     * This map holds the links between spectrum and associated peptides (key:
     * msms ID; value: set of evidence IDs).
     */
    private final Map<Integer, Set<Integer>> spectrumToPeptides = new HashMap<>();
    /**
     * This map holds the links between a peptide and associated proteins (key:
     * evidence ID; value: set of protein groups IDs).
     */
    private final Map<Integer, Set<Integer>> peptideToProteinGroups = new HashMap<>();
    /**
     * The map of matching between runs and (MBR) peptides (key: RAW file name;
     * value: the set of evidence IDs).
     */
    private final Map<String, Set<Integer>> runToMbrPeptides = new HashMap<>();
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

    public Map<Integer, Peptide> getPeptides() {
        return peptides;
    }

    public Map<Integer, Set<Integer>> getSpectrumToPeptides() {
        return spectrumToPeptides;
    }

    public Map<Integer, Set<Integer>> getPeptideToProteinGroups() {
        return peptideToProteinGroups;
    }

    public Map<String, Set<Integer>> getRunToMbrPeptides() {
        return runToMbrPeptides;
    }

    /**
     * This method parses an evidence file.
     *
     * @param evidenceFilePath       the MaxQuant evidence file path
     * @param omittedProteinGroupIds removed protein group IDs
     * @throws IOException in case of an I/O related problem
     */
    public void parse(Path evidenceFilePath, Set<Integer> omittedProteinGroupIds) throws IOException {
        TabularFileIterator evidenceIterator = new TabularFileIterator(evidenceFilePath, evidenceHeaders.getMandatoryHeaders());

        Map<String, String> evidenceEntry;
        while (evidenceIterator.hasNext()) {
            evidenceEntry = evidenceIterator.next();

            String[] proteinGroupIds = evidenceEntry.get(evidenceHeaders.get(EvidenceHeader.PROTEIN_GROUP_IDS)).split(PROTEIN_GROUP_ID_DELIMITER);

            //check for spectrumToPeptides coming from omitted protein groups
            //if one of the protein groups is not in the omitted list, include the peptide
            Set<Integer> nonOmittedProteinIds = Arrays.stream(proteinGroupIds)
                    .map(Integer::valueOf)
                    .filter(proteinGroupsId -> !omittedProteinGroupIds.contains(proteinGroupsId))
                    .collect(Collectors.toSet());

            if (!nonOmittedProteinIds.isEmpty()) {
                String[] msmsIds = evidenceEntry.get(evidenceHeaders.get(EvidenceHeader.MS_MS_IDS)).split(MS_MS_IDS_DELIMITER);
                for (String msmsIdString : msmsIds) {
                    //get the evidence ID
                    Integer evidenceId = Integer.valueOf(evidenceEntry.get(evidenceHeaders.get(EvidenceHeader.ID)));
                    //create the peptide
                    createPeptide(evidenceId, evidenceEntry, nonOmittedProteinIds);
                    //check if the peptide has a spectrum (PSM)
                    if (!msmsIdString.isEmpty()) {
                        Integer msmsId = Integer.parseInt(msmsIdString);
                        if (!spectrumToPeptides.containsKey(msmsId)) {
                            Set<Integer> evidenceIds = new HashSet<>();
                            evidenceIds.add(evidenceId);
                            spectrumToPeptides.put(msmsId, evidenceIds);
                        } else {
                            spectrumToPeptides.get(msmsId).add(evidenceId);
                        }

                    } //otherwise it's a matching between runs (MBR) peptide
                    else {
                        String rawFile = evidenceEntry.get(evidenceHeaders.get(EvidenceHeader.RAW_FILE));
                        if (!runToMbrPeptides.containsKey(rawFile)) {
                            Set<Integer> evidenceIds = new HashSet<>();
                            evidenceIds.add(evidenceId);
                            runToMbrPeptides.put(rawFile, evidenceIds);
                        } else {
                            runToMbrPeptides.get(rawFile).add(evidenceId);
                        }
                    }
                }
            }
        }
    }

    /**
     * Create a Peptide from a row entry in the evidence file.
     *
     * @param evidenceId                 the evidence ID
     * @param evidenceEntry              key-value pairs from an evidence entry
     * @param nonOmittedProteinGroupsIds the set of non omitted protein group IDs for the given evidence entry
     */
    private void createPeptide(Integer evidenceId, Map<String, String> evidenceEntry, Set<Integer> nonOmittedProteinGroupsIds) {
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

        //add to the peptideToProteinGroups map
        peptideToProteinGroups.put(evidenceId, nonOmittedProteinGroupsIds);
        peptides.put(evidenceId, peptide);
    }

    /**
     * Clear run data from parser.
     */
    public void clear() {
        peptideToProteinGroups.clear();
        spectrumToPeptides.clear();
        runToMbrPeptides.clear();
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
                //look for the modification by it's name in the modification ontology terms
                if (modificationMappings.containsKey(evidenceModification.getFullModificationName())) {
                    ModificationOntologyTerm modificationTerm = (ModificationOntologyTerm) modificationMappings.get(evidenceModification.getFullModificationName());
                    modification = modificationMapper.mapByOntologyTerm(modificationTerm.getOntologyPrefix(),
                            modificationTerm.getOboId(),
                            modificationTerm.getLabel(),
                            modificationTerm.getUtilitiesName());
                } else {
                    modification = modificationMapper.mapByName(evidenceModification.getFullModificationName());
                }

                //Check the modification type (N-terminal, C-terminal or
                //non-terminal). Only non-terminal modifications have an
                //additional probabilities entry.
                //@// TODO: 29/09/16 ask for a good value for the terminal modification score
                if (evidenceModification.isNTerminal()) {
                    PeptideHasModification peptideHasModification = createPeptideHasModification(0, null, null, peptide);

                    peptideHasModification.setModification(modification);
                    peptideHasModifications.add(peptideHasModification);
                } else if (evidenceModification.isCTerminal()) {
                    PeptideHasModification peptideHasModification = createPeptideHasModification(values.get(evidenceHeaders.get(EvidenceHeader.SEQUENCE)).length() - 1, null, null, peptide);

                    peptideHasModification.setModification(modification);
                    peptideHasModifications.add(peptideHasModification);
                } else {
                    //Get the modifications probabilities and score diffs for non-terminal modifications.
                    //These fields can be empty in the evidence file, even though the peptide has modifications.
                    String probabilitiesString = values.get(evidenceModification.getLowerCaseFullModificationName() + MODIFICATION_PROBABILITIES);
                    String deltasString = values.get(evidenceModification.getLowerCaseFullModificationName() + MODIFICATION_SCORE_DIFFS);
                    //Array to store the parsed scores and the affected amino acid location.
                    //(Element 0: the probability score; element 1: the delta score; element 2: the affected amino acid location starting from 1)
                    List<Object[]> scoresAndLocations = new ArrayList<>();
                    if (!probabilitiesString.isEmpty() && !deltasString.isEmpty()) {
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

                        //Check the modification occurrences;
                        //if the number of scores is higher than the occurrences rate, take the one with the highest score.
                        if (evidenceModification.getOccurrences() != scoresAndLocations.size()) {
                            //sort the scores in descending order
                            scoresAndLocations.sort(((o1, o2) -> ((Double) o2[0]).compareTo((Double) o1[0])));
                        }
                    } else {
                        //parse the modified sequence
                        String modifiedSequenceString = values.get(evidenceHeaders.get(EvidenceHeader.MODIFIED_SEQUENCE));
                        //trim underscores
                        modifiedSequenceString = StringUtils.strip(modifiedSequenceString, MODIFIED_SEQUENCE_FIX);
                        Matcher locations = MODIFICATION_PATTERN.matcher(modifiedSequenceString);
                        int locationOverhead = 1;
                        while (locations.find()) {
                            Object[] location = new Object[1];
                            //get the abbreviated modification name
                            String mod = locations.group(1);
                            if (evidenceModification.getLowerCaseModificationName().startsWith(mod)) {
                                location[0] = locations.start(1) - locationOverhead;
                                scoresAndLocations.add(location);
                            }

                            locationOverhead += locations.end() - locations.start();
                        }
                    }

                    for (int i = 0; i < evidenceModification.getOccurrences(); i++) {
                        PeptideHasModification peptideHasModification;
                        if (!scoresAndLocations.isEmpty()) {
                            if (scoresAndLocations.get(0).length == 3) {
                                peptideHasModification = createPeptideHasModification((Integer) scoresAndLocations.get(i)[2], (Double) scoresAndLocations.get(i)[0], (Double) scoresAndLocations.get(i)[1], peptide);
                            } else {
                                peptideHasModification = createPeptideHasModification((Integer) scoresAndLocations.get(i)[0], null, null, peptide);
                            }
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
     * The number of occurrences of the modification in the peptide. This value
     * is null if the modification occurs only once.
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

package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.core.io.MaxQuantImport;
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
import com.compomics.colims.model.enums.QuantificationMethod;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.compomics.colims.model.enums.QuantificationMethod.LABEL_FREE;

/**
 * Parser class for the MaxQuant evidence file.
 * <p/>
 * Created by Iain on 01/12/2014.
 */
@Component("maxQuantEvidenceParser")
public class MaxQuantEvidenceParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(MaxQuantEvidenceParser.class);

    private static final String PROTEIN_GROUP_ID_DELIMITER = ";";
    private static final String MODIFICATION_DELIMITER = ",";
    private static final String MS_MS_IDS_DELIMITER = ";";
    private static final String NAN = "nan";
    private static final String UNMODIFIED = "Unmodified";
    private static final Pattern MODIFICATION_PATTERN = Pattern.compile("\\(([^)]+)\\)");
    private static final String MODIFICATION_PROBABILITIES = " probabilities";
    private static final String MODIFICATION_SCORE_DIFFS = " score diffs";
    private static final String MODIFIED_SEQUENCE_FIX = "_";
    private static final String REPORTER_INTENSITY_CORRECTED = "%1$s %2$d";
    static final String N_TERMINAL_MODIFICATION = "Protein N-term";
    static final String C_TERMINAL_MODIFICATION = "Protein C-term";

    /**
     * The parsed peptides map (key: evidence ID; value: the associated
     * {@link Peptide} objects).
     */
    private final Map<Integer, List<Peptide>> peptides = new HashMap<>();
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
     * The quantification method.
     */
    private QuantificationMethod quantificationMethod;
    /**
     * The JSON mapper.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();
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
    private final MaxQuantSearchSettingsParser maxQuantSearchSettingsParser;

    /**
     * No-arg constructor.
     *
     * @param modificationMapper           the modification mapper
     * @param ontologyMapper               the ontology mapper
     * @param maxQuantSearchSettingsParser the MaxQuant search setting parser
     * @throws IOException in case of an Input/Output related problem while
     *                     parsing the headers.
     */
    @Autowired
    public MaxQuantEvidenceParser(ModificationMapper modificationMapper, OntologyMapper ontologyMapper, MaxQuantSearchSettingsParser maxQuantSearchSettingsParser) throws IOException {
        this.modificationMapper = modificationMapper;
        this.maxQuantSearchSettingsParser = maxQuantSearchSettingsParser;
        //get the modification mappings from the OntologyMapper
        modificationMappings = ontologyMapper.getMaxQuantMapping().getModifications();
        evidenceHeaders = new EvidenceHeaders();
    }

    public Map<Integer, List<Peptide>> getPeptides() {
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
     * This method parses an evidence file. If the given raw file name is not null, it only parses the entries
     * linked to the given raw file.
     *
     * @param evidenceFilePath       the MaxQuant evidence file path
     * @param rawFileName            the raw file name of the run
     * @param omittedProteinGroupIds removed protein group IDs
     * @param quantificationMethod   the quantification method
     * @param optionalHeaders        the list of optional headers
     * @throws IOException in case of an I/O related problem
     */
    public void parse(Path evidenceFilePath, String rawFileName, Set<Integer> omittedProteinGroupIds, QuantificationMethod quantificationMethod, List<String> optionalHeaders) throws IOException {
        TabularFileIterator evidenceIterator = new TabularFileIterator(evidenceFilePath, evidenceHeaders.getMandatoryHeaders());
        this.quantificationMethod = quantificationMethod;

        Map<String, String> evidenceEntry;
        while (evidenceIterator.hasNext()) {
            evidenceEntry = evidenceIterator.next();

            if (rawFileName == null) {
                parseEvidenceEntry(evidenceEntry, evidenceEntry.get(evidenceHeaders.get(EvidenceHeader.RAW_FILE)), omittedProteinGroupIds, quantificationMethod, optionalHeaders);
            } else if (rawFileName.equals(evidenceEntry.get(evidenceHeaders.get(EvidenceHeader.RAW_FILE)))) {
                parseEvidenceEntry(evidenceEntry, rawFileName, omittedProteinGroupIds, quantificationMethod, optionalHeaders);
            } else {
                //do nothing
            }
        }
    }

    /**
     * Parse the given evidence entry.
     *
     * @param evidenceEntry          the map of entries (key: column header; value: column value)
     * @param rawFileName            the raw file name of the run
     * @param omittedProteinGroupIds removed protein group IDs
     * @param quantificationMethod   the quantification method
     * @param optionalHeaders        the list of optional headers
     */
    private void parseEvidenceEntry(Map<String, String> evidenceEntry, String rawFileName, Set<Integer> omittedProteinGroupIds, QuantificationMethod quantificationMethod, List<String> optionalHeaders) {
        String[] proteinGroupIds = evidenceEntry.get(evidenceHeaders.get(EvidenceHeader.PROTEIN_GROUP_IDS)).split(PROTEIN_GROUP_ID_DELIMITER);

        //check for spectrumToPeptides coming from omitted protein groups
        //if one of the protein groups is not in the omitted list, include the peptide
        Set<Integer> includedProteinIds = Arrays.stream(proteinGroupIds)
                .map(Integer::valueOf)
                .filter(proteinGroupsId -> !omittedProteinGroupIds.contains(proteinGroupsId))
                .collect(Collectors.toSet());

        if (!includedProteinIds.isEmpty()) {
            String[] msmsIds = evidenceEntry.get(evidenceHeaders.get(EvidenceHeader.MS_MS_IDS)).split(MS_MS_IDS_DELIMITER);
            for (String msmsIdString : msmsIds) {
                //get the evidence ID
                Integer evidenceId = Integer.valueOf(evidenceEntry.get(evidenceHeaders.get(EvidenceHeader.ID)));
                //create the peptide
                createPeptide(evidenceId, evidenceEntry, includedProteinIds, optionalHeaders);
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
                    if (!runToMbrPeptides.containsKey(rawFileName)) {
                        Set<Integer> evidenceIds = new HashSet<>();
                        evidenceIds.add(evidenceId);
                        runToMbrPeptides.put(rawFileName, evidenceIds);
                    } else {
                        runToMbrPeptides.get(rawFileName).add(evidenceId);
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
     * @param nonOmittedProteinGroupsIds the set of non omitted protein group
     *                                   IDs for the given evidence entry
     * @param optionalHeaders            the optional proteinGroups.txt entries
     */
    private void createPeptide(Integer evidenceId, Map<String, String> evidenceEntry, Set<Integer> nonOmittedProteinGroupsIds, List<String> optionalHeaders) {
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
        if (!evidenceEntry.get(evidenceHeaders.get(EvidenceHeader.MASS_ERROR)).equalsIgnoreCase(NAN)) {
            peptide.setMassError(Double.valueOf(evidenceEntry.get(evidenceHeaders.get(EvidenceHeader.MASS_ERROR))));
        }
        peptide.getPeptideHasModifications().addAll(createModifications(peptide, evidenceEntry));

        //add to the peptideToProteinGroups map
        peptideToProteinGroups.put(evidenceId, nonOmittedProteinGroupsIds);
        //add to the peptides map
        if (peptides.containsKey(evidenceId)) {
            peptides.get(evidenceId).add(peptide);
        } else {
            List<Peptide> associatedPeptides = new ArrayList<>();
            associatedPeptides.add(peptide);
            peptides.put(evidenceId, associatedPeptides);
        }

        //handle quantification related entries
        String intensity = evidenceEntry.get(evidenceHeaders.get(EvidenceHeader.INTENSITY));
        String lfqIntensity = evidenceEntry.get(evidenceHeaders.get(EvidenceHeader.LFQ_INTENSITY));
        String ibaq = evidenceEntry.get(evidenceHeaders.get(EvidenceHeader.IBAQ));
        String msmsCount = evidenceEntry.get(evidenceHeaders.get(EvidenceHeader.MSMS_COUNT));

        Map<String, Double> labeledIntensities = null;
        if (!quantificationMethod.equals(LABEL_FREE)) {
            labeledIntensities = parseLabeledQuantification(evidenceEntry, optionalHeaders);
        }
        //set the intensity
        if (intensity != null && !intensity.isEmpty()) {
            peptide.setIntensity(Double.parseDouble(intensity));
        }
        //set the LFQ intensity
        if (lfqIntensity != null && !lfqIntensity.isEmpty()) {
            peptide.setLfqIntensity(Double.parseDouble(lfqIntensity));
        }
        //set the iBAQ intensity
        if (ibaq != null && !ibaq.isEmpty()) {
            peptide.setIbaq(Double.parseDouble(ibaq));
        }
        //set the MSMS count
        if (msmsCount != null && !msmsCount.isEmpty()) {
            peptide.setMsmsCount(Integer.parseInt(msmsCount));
        }
        //set the labeled intensities as a JSON string
        if (labeledIntensities != null && !labeledIntensities.isEmpty()) {
            try {
                peptide.setLabels(objectMapper.writeValueAsString(labeledIntensities));
            } catch (JsonProcessingException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

    }

    /**
     * Clear run data from parser.
     */
    public void clear() {
        peptides.clear();
        spectrumToPeptides.clear();
        peptideToProteinGroups.clear();
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
                EvidenceModification evidenceModification = new EvidenceModification(modificationString.replaceAll("\"", ""));
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
                    if (probabilitiesString != null && deltasString != null && !probabilitiesString.isEmpty() && !deltasString.isEmpty()) {
                        //first, parseSpectraAndPSMs the modification probability scores and delta scores between brackets
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
                        //parseSpectraAndPSMs the modified sequence
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

    /**
     * Parse labeled quantifications for the given run and protein group where
     * the quantification names come from mqpar file. If the value is null or
     * not numeric, it is not stored.
     *
     * @param evidenceEntry key-value pairs from an evidence entry
     * @return the map of labeled intensities
     */
    private Map<String, Double> parseLabeledQuantification(Map<String, String> evidenceEntry, List<String> optionalHeaders) {
        Map<String, Double> intensities = new LinkedHashMap<>();
        switch (quantificationMethod) {
            case SILAC:
            case ITRAQ:
            case ICAT:
                String intensityL = evidenceEntry.get(evidenceHeaders.get(EvidenceHeader.INTENSITY_L));
                String intensityH = evidenceEntry.get(evidenceHeaders.get(EvidenceHeader.INTENSITY_H));
                //in case of 2 label mods, we have a light and a heavy label (L, H).
                //if there are 3 label mods, we have light, medium and heavy labels (L, M, H).
                if (intensityL != null && NumberUtils.isNumber(intensityL)) {
                    if (maxQuantSearchSettingsParser.getLabelMods().get(0) != null) {
                        intensities.put(maxQuantSearchSettingsParser.getLabelMods().get(0), Double.valueOf(intensityL));
                    } else {
                        intensities.put(MaxQuantImport.NO_LABEL, Double.valueOf(intensityL));
                    }
                }
                if (intensityH != null && NumberUtils.isNumber(intensityH)) {
                    if (maxQuantSearchSettingsParser.getLabelMods().get(maxQuantSearchSettingsParser.getLabelMods().size() - 1) != null) {
                        intensities.put(maxQuantSearchSettingsParser.getLabelMods().get(maxQuantSearchSettingsParser.getLabelMods().size() - 1), Double.valueOf(intensityH));
                    } else {
                        intensities.put(MaxQuantImport.NO_LABEL, Double.valueOf(intensityH));
                    }
                }
                if (maxQuantSearchSettingsParser.getLabelMods().size() == 3) { //parseSpectraAndPSMs the medium label as well
                    String intensityM = evidenceEntry.get(evidenceHeaders.get(EvidenceHeader.INTENSITY_M));
                    if (intensityM != null && NumberUtils.isNumber(intensityM)) {
                        if (maxQuantSearchSettingsParser.getLabelMods().get(1) != null) {
                            intensities.put(maxQuantSearchSettingsParser.getLabelMods().get(1), Double.valueOf(intensityM));
                        } else {
                            intensities.put(MaxQuantImport.NO_LABEL, Double.valueOf(intensityM));
                        }
                    }
                }
                break;
            case TMT:
                int reportersSize = calculateTmtReportersSize(maxQuantSearchSettingsParser.getIsobaricLabels().size());
                for (int i = 0; i < reportersSize; i++) {
                    String reporterIntensityCorrected = evidenceEntry.get(String.format(REPORTER_INTENSITY_CORRECTED, evidenceHeaders.get(EvidenceHeader.REPORTER_INTENSITY_CORRECTED), i));
                    if (reporterIntensityCorrected != null && NumberUtils.isNumber(reporterIntensityCorrected) && maxQuantSearchSettingsParser.getIsobaricLabels().size() >= i + 1) {
                        if (maxQuantSearchSettingsParser.getIsobaricLabels().get(i) != null) {
                            intensities.put(maxQuantSearchSettingsParser.getIsobaricLabels().get(i), Double.valueOf(reporterIntensityCorrected));
                        } else {
                            intensities.put(EvidenceHeader.REPORTER_INTENSITY_CORRECTED + " " + i, Double.valueOf(reporterIntensityCorrected));
                        }
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("Unexpected quantification label: " + quantificationMethod.toString());
        }

        //store the given optional header if it has a numeric value per run and protein group
        optionalHeaders.forEach((optionalHeader) -> {
//            String experimentOptionalHeader = optionalHeader.toLowerCase() + " " + experimentName;
            String experimentOptionalHeader = optionalHeader.toLowerCase();
            if (evidenceEntry.containsKey(experimentOptionalHeader) && NumberUtils.isNumber(evidenceEntry.get(experimentOptionalHeader))) {
                intensities.put(optionalHeader, Double.valueOf(evidenceEntry.get(experimentOptionalHeader)));
            }
        });

        return intensities;
    }

    /**
     * Calculates the label size for the TMT reporters because it's possible
     * that for example for TMT10plex MaxQuant list 20 labels instead of 10.
     *
     * @param isobaricLabelsSize the number of isobaric labels
     * @return the number of reporters
     */
    private int calculateTmtReportersSize(int isobaricLabelsSize) {
        int labelSize;
        if (isobaricLabelsSize % 6 == 0) {
            labelSize = 6;
        } else if (isobaricLabelsSize % 10 == 0) {
            labelSize = 10;
        } else if (isobaricLabelsSize % 11 == 0) {
            labelSize = 11;
        } else if (isobaricLabelsSize % 2 == 0) {
            labelSize = 2;
        } else {
            labelSize = isobaricLabelsSize;
        }

        return labelSize;
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

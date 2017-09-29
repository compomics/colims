package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.core.io.MaxQuantImport;
import com.compomics.colims.core.io.fasta.FastaDbParser;
import com.compomics.colims.core.service.ProteinService;
import com.compomics.colims.distributed.io.maxquant.TabularFileIterator;
import com.compomics.colims.distributed.io.maxquant.headers.ProteinGroupsHeader;
import com.compomics.colims.distributed.io.maxquant.headers.ProteinGroupsHeaders;
import com.compomics.colims.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Create grouped proteins from the protein groups file output by MaxQuant.
 *
 * @author Iain
 */
@Component("maxQuantProteinGroupParser")
public class MaxQuantProteinGroupsParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(MaxQuantProteinGroupsParser.class);

    private static final String CONTAMINANT_SHORT_PREFIX = "CON";
    private static final String REVERSE_PREFIX = "REV";
    private static final String ACCESSION_DELIMITER = ";";
    private static final String REPORTER_INTENSITY_CORRECTED = "%1$s %2$d %3$s";
    private static final String INTENSITY_HEADER = "%1$s %2$s";

    /**
     * The map of parsed protein groups (key: proteinGroups.txt entry ID; value:
     * the {@link ProteinGroup} instance).
     */
    private final Map<Integer, ProteinGroup> proteinGroups = new HashMap<>();
    /**
     * The list of omitted protein group IDs. The peptides, PSMs, spectra for
     * these protein groups are not stored in the database.
     */
    private final Set<Integer> omittedProteinGroupIds = new HashSet<>();
    /**
     * The map of parsed protein sequences (key: protein accession; value:
     * protein sequence).
     */
    private Map<String, String> proteinSequences = new HashMap<>();
    /**
     * The quantification label.
     */
    private String quantificationLabel;
    /**
     * The JSON mapper.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ProteinGroupsHeaders proteinGroupsHeaders;
    /**
     * Child beans.
     */
    private final ProteinService proteinService;
    private final MaxQuantSearchSettingsParser maxQuantSearchSettingsParser;
    private final FastaDbParser fastaDbParser;

    @Autowired
    public MaxQuantProteinGroupsParser(ProteinService proteinService, MaxQuantSearchSettingsParser maxQuantSearchSettingsParser, FastaDbParser fastaDbParser) throws IOException {
        proteinGroupsHeaders = new ProteinGroupsHeaders();
        this.proteinService = proteinService;
        this.maxQuantSearchSettingsParser = maxQuantSearchSettingsParser;
        this.fastaDbParser = fastaDbParser;
    }

    public Map<Integer, ProteinGroup> getProteinGroups() {
        return proteinGroups;
    }

    /**
     * Getter for the list of omitted protein group IDs.
     *
     * @return omittedProteinGroupIds
     */
    public Set<Integer> getOmittedProteinGroupIds() {
        return omittedProteinGroupIds;
    }

    /**
     * Clear resources.
     */
    public void clear() {
        proteinGroups.clear();
        omittedProteinGroupIds.clear();
        proteinSequences.clear();
        proteinService.clear();
    }

    /**
     * Parse the proteinGroups.txt file.
     *
     * @param proteinGroupsFile   MaxQuant protein groups file
     * @param fastaDbMap          the map of {@link FastaDb} instances
     * @param quantificationLabel the quantification label
     * @param includeContaminants whether or not to include contaminants
     * @param optionalHeaders     the list of optional headers
     * @throws IOException in case of an Input/Output related problem
     */
    public void parse(Path proteinGroupsFile, LinkedHashMap<FastaDb, Path> fastaDbMap, String quantificationLabel, boolean includeContaminants, List<String> optionalHeaders) throws IOException {
        TabularFileIterator iterator = new TabularFileIterator(proteinGroupsFile, proteinGroupsHeaders.getMandatoryHeaders());
        this.quantificationLabel = quantificationLabel;
        while (iterator.hasNext()) {
            Map<String, String> values = iterator.next();

            //parse the FASTA files
            proteinSequences = fastaDbParser.parse(fastaDbMap);
            ProteinGroup proteinGroup = parseProteinGroup(values, includeContaminants, optionalHeaders);
            if (proteinGroup.getMainProtein() != null) {
                proteinGroups.put(Integer.parseInt(values.get(proteinGroupsHeaders.get(ProteinGroupsHeader.ID))), proteinGroup);
            }
        }
    }

    /**
     * Construct a {@link ProteinGroup} instance from a proteinGroups file
     * entry.
     *
     * @param proteinGroupsEntry key-value pairs from an proteinGroups entry
     * @return the {@link ProteinGroup} object
     */
    private ProteinGroup parseProteinGroup(Map<String, String> proteinGroupsEntry, boolean includeContaminants, List<String> optionalHeaders) {
        ProteinGroup proteinGroup = new ProteinGroup();

        //set the protein group posterior error probability, which is derived from
        //peptide posterior error probabilities
        proteinGroup.setProteinPostErrorProbability(Double.parseDouble(proteinGroupsEntry.get(proteinGroupsHeaders.get(ProteinGroupsHeader.SCORE))));

        String accessionHeader = proteinGroupsEntry.get(proteinGroupsHeaders.get(ProteinGroupsHeader.ACCESSION));
        //keep track of all protein accessions that need to be stored
        List<String> accessionsToInclude = new ArrayList<>();
        String[] accessions = accessionHeader.split(ACCESSION_DELIMITER);
        if (!accessions[0].startsWith(CONTAMINANT_SHORT_PREFIX) || includeContaminants) { //check for contaminants and whether they need to be included
            for (String accession : accessions) {
                if (!accession.startsWith(REVERSE_PREFIX)) { //exclude the protein groups that contain at least one reverse protein
                    accessionsToInclude.add(accession);
                } else {
                    accessionsToInclude.clear();
                    break;
                }
            }
        }

        if (!accessionsToInclude.isEmpty()) { //add the proteins to the protein group
            boolean isMainGroup = true;
            for (String accession : accessionsToInclude) {
                String strippedAccession = accession;
                if (strippedAccession.startsWith(CONTAMINANT_SHORT_PREFIX)) { //strip contaminant accessions from the prefix
                    strippedAccession = org.apache.commons.lang3.StringUtils.substringAfter(strippedAccession, ProteinGroupHasProtein.CONTAMINANT_PREFIX);
                }
                //get the protein sequence by it's accession
                String sequence = getProteinSequence(strippedAccession);
                proteinGroup.getProteinGroupHasProteins().add(createProteinGroupHasProtein(sequence, accession, isMainGroup, proteinGroup));

                if (isMainGroup) {
                    isMainGroup = false;
                }
            }

            //handle quantification related entries
            maxQuantSearchSettingsParser.getAnalyticalRuns().forEach((run, name) -> {
                String intensity = proteinGroupsEntry.get(String.format(INTENSITY_HEADER, proteinGroupsHeaders.get(ProteinGroupsHeader.INTENSITY), name.toLowerCase()));
                String lfqIntensity = proteinGroupsEntry.get(String.format(INTENSITY_HEADER, proteinGroupsHeaders.get(ProteinGroupsHeader.LFQ_INTENSITY), name.toLowerCase()));
                String ibaq = proteinGroupsEntry.get(String.format(INTENSITY_HEADER, proteinGroupsHeaders.get(ProteinGroupsHeader.IBAQ), name.toLowerCase()));
                String msmsCount = proteinGroupsEntry.get(String.format(INTENSITY_HEADER, proteinGroupsHeaders.get(ProteinGroupsHeader.MSMS_COUNT), name.toLowerCase()));

                Map<String, Double> labeledIntensities = null;
                if (!quantificationLabel.equals(MaxQuantImport.LABEL_FREE)) {
                    labeledIntensities = parseLabeledQuantification(proteinGroupsEntry, name.toLowerCase(), optionalHeaders);
                }

                if (intensity != null || lfqIntensity != null || ibaq != null || msmsCount != null || labeledIntensities != null) {
                    try {
                        createProteinGroupQuant(proteinGroup, run, intensity, lfqIntensity, ibaq, msmsCount, labeledIntensities);
                    } catch (JsonProcessingException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }

            });
        } else {
            omittedProteinGroupIds.add(Integer.valueOf(proteinGroupsEntry.get(proteinGroupsHeaders.get(ProteinGroupsHeader.ID))));
        }

        return proteinGroup;
    }

    /**
     * Search for a protein sequence in the FASTA entries map by accession.
     *
     * @param accession the protein accession
     * @return sequence the found sequence
     * @throws IllegalArgumentException if the accession key is not found
     */
    private String getProteinSequence(String accession) {
        if (proteinSequences.containsKey(accession)) {
            return proteinSequences.get(accession);
        } else {
            throw new IllegalArgumentException("The protein with accession " + accession + " has no sequence in the parsed FASTA files.");
        }
    }

    /**
     * Create a protein and it's relation to a protein group.
     *
     * @param sequence  the sequence of the protein
     * @param accession the accession of the protein
     * @param mainGroup whether this is the main protein of the group
     * @return a ProteinGroupHasProtein object
     */
    private ProteinGroupHasProtein createProteinGroupHasProtein(String sequence, String accession, boolean mainGroup, ProteinGroup proteinGroup) {
        ProteinGroupHasProtein proteinGroupHasProtein = new ProteinGroupHasProtein();
        proteinGroupHasProtein.setIsMainGroupProtein(mainGroup);

        //get protein
        Protein protein = proteinService.getProtein(sequence);

        //set protein accession
        proteinGroupHasProtein.setProteinAccession(accession);

        //set entity associations
        proteinGroupHasProtein.setProtein(protein);
        proteinGroupHasProtein.setProteinGroup(proteinGroup);

        return proteinGroupHasProtein;
    }

    /**
     * Create protein group quantification and it's relation to a protein group
     * and analytical run.
     *
     * @param proteinGroup       the protein group
     * @param analyticalRun      the analytical run related to quantification
     * @param intensity          the intensity
     * @param lfqIntensity       the LFQ intensity
     * @param ibaq               the iBAQ
     * @param msmsCount          the MSMS Count
     * @param labeledIntensities the labeled intensities map (key: label; value:
     *                           intensity)
     * @throws JsonProcessingException in case of a json serializing problem
     */
    private void createProteinGroupQuant(ProteinGroup proteinGroup, AnalyticalRun analyticalRun, String intensity, String lfqIntensity, String ibaq, String msmsCount, Map<String, Double> labeledIntensities) throws JsonProcessingException {
        ProteinGroupQuant proteinGroupQuant = new ProteinGroupQuant();
        //set the protein group
        proteinGroupQuant.setProteinGroup(proteinGroup);
        //set the analytical run
        proteinGroupQuant.setAnalyticalRun(analyticalRun);
        //set the intensity
        if (intensity != null) {
            proteinGroupQuant.setIntensity(Double.parseDouble(intensity));
        }
        //set the LFQ intensity
        if (lfqIntensity != null) {
            proteinGroupQuant.setLfqIntensity(Double.parseDouble(lfqIntensity));
        }
        //set the iBAQ
        if (ibaq != null) {
            proteinGroupQuant.setIbaq(Double.parseDouble(ibaq));
        }
        //set the MSMS Count
        if (msmsCount != null) {
            proteinGroupQuant.setMsmsCount(Integer.parseInt(msmsCount));
        }
        //set the labeled intensities as a JSON string
        if (labeledIntensities != null && !labeledIntensities.isEmpty()) {
            proteinGroupQuant.setLabels(objectMapper.writeValueAsString(labeledIntensities));
        }

        //add this protein quantification to protein group.
        proteinGroup.getProteinGroupQuants().add(proteinGroupQuant);
        //add this protein quantification to the related analytical run.
        analyticalRun.getProteinGroupQuants().add(proteinGroupQuant);
    }

    /**
     * Parse labeled quantifications for the given run and protein group where
     * the quantification names come from mqpar file. If the value is null or
     * not numeric, it is not stored.
     *
     * @param proteinGroupsEntry key-value pairs from an evidence entry
     * @param experimentName     the experiment name
     * @return the map of labeled intensities
     */
    private Map<String, Double> parseLabeledQuantification(Map<String, String> proteinGroupsEntry, String experimentName, List<String> optionalHeaders) {
        Map<String, Double> intensities = new LinkedHashMap<>();
        switch (quantificationLabel) {
            case MaxQuantImport.SILAC:
            case MaxQuantImport.ITRAQ:
            case MaxQuantImport.ICAT:
                String intensityL = proteinGroupsEntry.get(String.format(INTENSITY_HEADER, proteinGroupsHeaders.get(ProteinGroupsHeader.INTENSITY_L), experimentName));
                String intensityH = proteinGroupsEntry.get(String.format(INTENSITY_HEADER, proteinGroupsHeaders.get(ProteinGroupsHeader.INTENSITY_H), experimentName));
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
                if (maxQuantSearchSettingsParser.getLabelMods().size() == 3) { //parse the medium label as well
                    String intensityM = proteinGroupsEntry.get(String.format(INTENSITY_HEADER, proteinGroupsHeaders.get(ProteinGroupsHeader.INTENSITY_M), experimentName));
                    if (intensityM != null && NumberUtils.isNumber(intensityM)) {
                        if (maxQuantSearchSettingsParser.getLabelMods().get(1) != null) {
                            intensities.put(maxQuantSearchSettingsParser.getLabelMods().get(1), Double.valueOf(intensityM));
                        } else {
                            intensities.put(MaxQuantImport.NO_LABEL, Double.valueOf(intensityM));
                        }
                    }
                }

                break;
            case MaxQuantImport.TMT:
                int reportersSize = calculateTmtReportersSize(maxQuantSearchSettingsParser.getIsobaricLabels().size());
                for (int i = 0; i < reportersSize; i++) {
                    String reporterIntensityCorrected = proteinGroupsEntry.get(String.format(REPORTER_INTENSITY_CORRECTED, proteinGroupsHeaders.get(ProteinGroupsHeader.REPORTER_INTENSITY_CORRECTED), i, experimentName));
                    if (reporterIntensityCorrected != null && NumberUtils.isNumber(reporterIntensityCorrected) && maxQuantSearchSettingsParser.getIsobaricLabels().size() >= i + 1) {
                        if (maxQuantSearchSettingsParser.getIsobaricLabels().get(i) != null) {
                            intensities.put(maxQuantSearchSettingsParser.getIsobaricLabels().get(i), Double.valueOf(reporterIntensityCorrected));
                        } else {
                            intensities.put(ProteinGroupsHeader.REPORTER_INTENSITY_CORRECTED + " " + i, Double.valueOf(reporterIntensityCorrected));
                        }
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("Unexpected quantification label: " + quantificationLabel);
        }

        //store the given optional header if it has a numeric value per run and protein group
        optionalHeaders.forEach((optionalHeader) -> {
            String experimentOptionalHeader = optionalHeader.toLowerCase() + " " + experimentName;
            if (proteinGroupsEntry.containsKey(experimentOptionalHeader) && NumberUtils.isNumber(proteinGroupsEntry.get(experimentOptionalHeader))) {
                intensities.put(optionalHeader, Double.valueOf(proteinGroupsEntry.get(experimentOptionalHeader)));
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

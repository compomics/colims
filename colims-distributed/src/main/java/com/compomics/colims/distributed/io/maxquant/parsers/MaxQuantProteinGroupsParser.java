package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.core.io.MaxQuantImport;
import com.compomics.colims.core.io.fasta.FastaDbParser;
import com.compomics.colims.core.service.ProteinService;
import com.compomics.colims.distributed.io.maxquant.TabularFileIterator;
import com.compomics.colims.distributed.io.maxquant.headers.ProteinGroupsHeader;
import com.compomics.colims.distributed.io.maxquant.headers.ProteinGroupsHeaders;
import com.compomics.colims.model.*;
import org.apache.commons.lang.math.NumberUtils;
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

    private static final String CONTAMINANT_SHORT_PREFIX = "CON";
    private static final String REVERSE_PREFIX = "REV";
    private static final String ACCESSION_DELIMITER = ";";
    private static final String REPORTER_INTENSITY_CORRECTED = "%1$s %2$d %3$s";
    private static final String INTENSITY_HEADER = "%1$s %2$s";

    /**
     * The map of parsed protein groups (key: proteinGroups.txt entry ID; value: the {@link ProteinGroup} instance).
     */
    private final Map<Integer, ProteinGroup> proteinGroups = new HashMap<>();
    /**
     * The list of omitted protein group IDs. The peptides, PSMs, spectra for
     * these protein groups are not stored in the database.
     */
    private final Set<Integer> omittedProteinGroupIds = new HashSet<>();
    /**
     * The map of parsed protein sequences (key: protein accession; value: protein sequence).
     */
    private Map<String, String> proteinSequences = new HashMap<>();
    /**
     * The quantification label.
     */
    private String quantificationLabel;
    private final ProteinGroupsHeaders proteinGroupsHeaders;
    /**
     * Child beans.
     */
    private final ProteinService proteinService;
    private final MaxQuantSearchSettingsParser maxQuantSearchSettingsParser;
    private final MaxQuantQuantificationSettingsParser maxQuantQuantificationSettingsParser;
    private final FastaDbParser fastaDbParser;

    @Autowired
    public MaxQuantProteinGroupsParser(ProteinService proteinService, MaxQuantSearchSettingsParser maxQuantSearchSettingsParser, MaxQuantQuantificationSettingsParser maxQuantQuantificationSettingsParser, FastaDbParser fastaDbParser) throws IOException {
        proteinGroupsHeaders = new ProteinGroupsHeaders();
        this.proteinService = proteinService;
        this.maxQuantSearchSettingsParser = maxQuantSearchSettingsParser;
        this.maxQuantQuantificationSettingsParser = maxQuantQuantificationSettingsParser;
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
     * Construct a {@link ProteinGroup} instance from a proteinGroups file entry.
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

                if (intensity != null || lfqIntensity != null || ibaq != null || msmsCount != null) {
                    createProteinGroupQuant(proteinGroup, run, intensity, lfqIntensity, ibaq, msmsCount);
                }

                if (!quantificationLabel.equals(MaxQuantImport.LABEL_FREE)) {
                    parseLabeledQuantification(proteinGroupsEntry, proteinGroup, run, name.toLowerCase(), optionalHeaders);
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
     * Create protein group quantification and it's relation to a protein group and analytical run.
     *
     * @param proteinGroup  the protein group
     * @param analyticalRun the analytical run related to quantification
     * @param intensity     the intensity
     * @param lfqIntensity  the LFQ intensity
     * @param ibaq          the iBAQ
     * @param msmsCount     the MSMS Count
     */
    private void createProteinGroupQuant(ProteinGroup proteinGroup, AnalyticalRun analyticalRun, String intensity, String lfqIntensity, String ibaq, String msmsCount) {
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
        //add this protein quantification to protein group.
        proteinGroup.getProteinGroupQuants().add(proteinGroupQuant);
        //add this protein quantification to the related analytical run.
        analyticalRun.getProteinGroupQuants().add(proteinGroupQuant);
    }

    /**
     * Create protein group quantification for labeled experiment and it's relation to a protein group and analytical
     * run.
     *
     * @param proteinGroup  the protein group
     * @param analyticalRun the analytical run related to quantification
     * @param label         the label
     * @param labelValue    the label value
     */
    private void createProteinGroupQuantLabeled(ProteinGroup proteinGroup, AnalyticalRun analyticalRun, String label, String labelValue) {
        ProteinGroupQuantLabeled proteinGroupQuantLabeled = new ProteinGroupQuantLabeled();
        // set the protein group
        proteinGroupQuantLabeled.setProteinGroup(proteinGroup);
        // set analytical run
        proteinGroupQuantLabeled.setAnalyticalRun(analyticalRun);
        // set label
        proteinGroupQuantLabeled.setLabel(label);
        // set label value
        if (labelValue != null) {
            proteinGroupQuantLabeled.setLabelValue(Double.parseDouble(labelValue));
        }

        // add this protein quantification to protein group.
        proteinGroup.getProteinGroupQuantsLabeled().add(proteinGroupQuantLabeled);
        // add this protein quantification to the related analytical run.
        analyticalRun.getProteinGroupQuantsLabeled().add(proteinGroupQuantLabeled);
    }

    /**
     * Parse labeled quantifications for the given run and protein group where the quantification names come from MQPAR file.
     * If the value is null or not numeric, it is not stored.
     *
     * @param proteinGroupsEntry key-value pairs from an evidence entry
     * @param proteinGroup       the {@link ProteinGroup} instance
     * @param analyticalRun      the analytical run
     * @param experimentName     the experiment name
     */
    private void parseLabeledQuantification(Map<String, String> proteinGroupsEntry, ProteinGroup proteinGroup, AnalyticalRun analyticalRun, String experimentName,
                                            List<String> optionalHeaders) {
        switch (quantificationLabel) {
            case MaxQuantImport.SILAC:
            case MaxQuantImport.iTRAQ:
            case MaxQuantImport.ICAT:
                String intensityL = proteinGroupsEntry.get(String.format(INTENSITY_HEADER, proteinGroupsHeaders.get(ProteinGroupsHeader.INTENSITY_L), experimentName));
                String intensityH = proteinGroupsEntry.get(String.format(INTENSITY_HEADER, proteinGroupsHeaders.get(ProteinGroupsHeader.INTENSITY_H), experimentName));
                //in case of 2 label mods, we have a light and a heavy label (L, H).
                //if there are 3 label mods, we have light, medium and heavy labels (L, M, H).
                if (intensityL != null && NumberUtils.isNumber(intensityL)) {
                    if (maxQuantSearchSettingsParser.getLabelMods().get(0) != null) {
                        createProteinGroupQuantLabeled(proteinGroup, analyticalRun, maxQuantSearchSettingsParser.getLabelMods().get(0), intensityL);
                    } else {
                        createProteinGroupQuantLabeled(proteinGroup, analyticalRun, proteinGroupsHeaders.get(ProteinGroupsHeader.INTENSITY_L), intensityL);
                    }
                }
                if (intensityH != null && NumberUtils.isNumber(intensityH)) {
                    if (maxQuantSearchSettingsParser.getLabelMods().get(maxQuantSearchSettingsParser.getLabelMods().size() - 1) != null) {
                        createProteinGroupQuantLabeled(proteinGroup, analyticalRun, maxQuantSearchSettingsParser.getLabelMods().get(maxQuantSearchSettingsParser.getLabelMods().size() - 1), intensityH);
                    } else {
                        createProteinGroupQuantLabeled(proteinGroup, analyticalRun, proteinGroupsHeaders.get(ProteinGroupsHeader.INTENSITY_H), intensityH);
                    }
                }
                if (maxQuantSearchSettingsParser.getLabelMods().size() == 3) { //parse the medium label as well
                    String intensityM = proteinGroupsEntry.get(String.format(INTENSITY_HEADER, proteinGroupsHeaders.get(ProteinGroupsHeader.INTENSITY_M), experimentName));
                    if (intensityM != null && NumberUtils.isNumber(intensityM)) {
                        if (maxQuantSearchSettingsParser.getLabelMods().get(1) != null) {
                            createProteinGroupQuantLabeled(proteinGroup, analyticalRun, maxQuantSearchSettingsParser.getLabelMods().get(1), intensityM);
                        } else {
                            createProteinGroupQuantLabeled(proteinGroup, analyticalRun, proteinGroupsHeaders.get(ProteinGroupsHeader.INTENSITY_M), intensityM);
                        }

                    }
                }
                break;
            case MaxQuantImport.TMT:
                for (int i = 0; i < maxQuantSearchSettingsParser.getIsobaricLabels().size(); i++) {
                    String reporterIntensityCorrected = proteinGroupsEntry.get(String.format(REPORTER_INTENSITY_CORRECTED, proteinGroupsHeaders.get(ProteinGroupsHeader.REPORTER_INTENSITY_CORRECTED), i, experimentName));
                    if (reporterIntensityCorrected != null && NumberUtils.isNumber(reporterIntensityCorrected) && maxQuantSearchSettingsParser.getIsobaricLabels().size() >= i + 1) {
                        if (maxQuantSearchSettingsParser.getIsobaricLabels().get(i) != null) {
                            createProteinGroupQuantLabeled(proteinGroup, analyticalRun, maxQuantSearchSettingsParser.getIsobaricLabels().get(i), reporterIntensityCorrected);
                        } else {
                            createProteinGroupQuantLabeled(proteinGroup, analyticalRun, ProteinGroupsHeader.REPORTER_INTENSITY_CORRECTED + " " + i, reporterIntensityCorrected);
                        }
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("Unexpected quantification label: " + quantificationLabel);
        }

        // if given header has numeric value per run and protein group, store.
        optionalHeaders.stream().map(String::toLowerCase).filter(header -> proteinGroupsEntry.get(header + " " + experimentName) != null && NumberUtils.isNumber(proteinGroupsEntry.get(header + " " + experimentName)))
                .forEach(optionalHeader -> createProteinGroupQuantLabeled(proteinGroup, analyticalRun, optionalHeader, proteinGroupsEntry.get(optionalHeader + " " + experimentName)));
    }
}

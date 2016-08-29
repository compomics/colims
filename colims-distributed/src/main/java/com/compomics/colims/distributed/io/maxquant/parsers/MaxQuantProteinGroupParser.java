package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.core.service.ProteinService;
import com.compomics.colims.distributed.io.maxquant.TabularFileLineValuesIterator;
import com.compomics.colims.distributed.io.maxquant.headers.HeaderEnum;
import com.compomics.colims.distributed.io.maxquant.headers.MaxQuantProteinGroupHeaders;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Protein;
import com.compomics.colims.model.ProteinGroup;
import com.compomics.colims.model.ProteinGroupHasProtein;
import com.compomics.colims.model.ProteinGroupQuant;
import com.compomics.colims.model.ProteinGroupQuantLabeled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.math.NumberUtils;

/**
 * Create grouped proteins from the protein groups file output by MaxQuant.
 *
 * @author Iain
 */
@Component("maxQuantProteinGroupParser")
public class MaxQuantProteinGroupParser {

    @Autowired
    private ProteinService proteinService;
    
    @Autowired
    private MaxQuantSearchSettingsParser maxQuantSearchSettingsParser;

    private static final HeaderEnum[] MANDATORY_HEADERS = new HeaderEnum[]{
        MaxQuantProteinGroupHeaders.ACCESSION,
        MaxQuantProteinGroupHeaders.EVIDENCEIDS,
        MaxQuantProteinGroupHeaders.ID,
        MaxQuantProteinGroupHeaders.PEP
    };

    /**
     * The list of omitted protein group IDs. The peptides, PSMs, spectra for
     * these protein groups are not stored in the database.
     */
    private final List<String> omittedProteinGroupIds = new ArrayList<>();

    /**
     * Getter for the list of omitted protein group IDs.
     *
     * @return omittedProteinGroupIds
     */
    public List<String> getOmittedProteinGroupIds() {
        return omittedProteinGroupIds;
    }

    /**
     * Parse a data file and return grouped proteins.
     *
     * @param proteinGroupsFile MaxQuant protein groups file
     * @param parsedFastas FASTA files parsed into header/sequence pairs
     * @return Protein groups indexed by id
     * @throws IOException
     */
    public Map<Integer, ProteinGroup> parse(File proteinGroupsFile, Map<String, String> parsedFastas, boolean includeContaminants, List<String> optionalHeaders) throws IOException {
        Map<Integer, ProteinGroup> proteinGroups = new HashMap<>();

        TabularFileLineValuesIterator iterator = new TabularFileLineValuesIterator(proteinGroupsFile, MANDATORY_HEADERS);
        while (iterator.hasNext()) {
            Map<String, String> values = iterator.next();

            ProteinGroup proteinGroup = parseProteinGroup(values, parsedFastas, includeContaminants, optionalHeaders);

            if (proteinGroup.getMainProtein() != null) {
                proteinGroups.put(Integer.parseInt(values.get(MaxQuantProteinGroupHeaders.ID.getValue())), proteinGroup);
            }
        }

        return proteinGroups;
    }

    /**
     * Clear resources.
     */
    public void clear() {
        proteinService.clear();
        omittedProteinGroupIds.clear();
    }

    /**
     * Construct a group of proteins.
     *
     * @param values A row of values
     * @param parsedFastas the parsed FASTA files
     * @return A protein group
     */
    private ProteinGroup parseProteinGroup(Map<String, String> values, Map<String, String> parsedFastas, boolean includeContaminants, List<String> optionalHeaders) {

        ProteinGroup proteinGroup = new ProteinGroup();

        if (values.get(MaxQuantProteinGroupHeaders.PEP.getValue()) != null) {
            proteinGroup.setProteinPostErrorProbability(Double.parseDouble(values.get(MaxQuantProteinGroupHeaders.PEP.getValue())));
        }

        String parsedAccession = values.get(MaxQuantProteinGroupHeaders.ACCESSION.getValue());
        List<String> filteredAccessions = new ArrayList<>();

        boolean omittedProteinGroup = false;
        
        if (parsedAccession.contains(";")) {
            String[] accessions = parsedAccession.split(";");
            // if select to omit contaminants and main protein is contaminant exclude that protein group.
            if(!includeContaminants){
                if(accessions[0].contains("CON")){
                    omittedProteinGroup = true;
                // if main protein is not contaminant, add all accessions except reverse. If reverse, exclude
                }else{
                    for (String accession : accessions) {
                        if (!accession.contains("REV")) {
                            filteredAccessions.add(accession);
                        }else{
                            omittedProteinGroup = true;
                        } 
                    }
                }
            // if select not to omit contaminants, add all accessions except reverse. If reverse, exclude
            }else{
                for (String accession : accessions) {
                    if (!accession.contains("REV")) {
                        filteredAccessions.add(accession);
                    }else{
                        omittedProteinGroup = true;
                    } 
                }
            }

            if(!omittedProteinGroup){
                boolean isMainGroup = true;

                for (String accession : filteredAccessions) {
                    String accToSearchSeq = accession;
                    if(accToSearchSeq.contains("CON")){
                        accToSearchSeq = org.apache.commons.lang3.StringUtils.substringAfter(accToSearchSeq, "CON__"); 
                    }
                    String sequence = sequenceInFasta(accToSearchSeq, parsedFastas);
                    proteinGroup.getProteinGroupHasProteins().add(createProteinGroupHasProtein(sequence, accession, isMainGroup, proteinGroup));

                    if (isMainGroup) {
                        isMainGroup = false;
                    }
                }
            }else{
                omittedProteinGroupIds.add(values.get(MaxQuantProteinGroupHeaders.ID.getValue()));
            }
            
        } else {
            if(!includeContaminants){
                if (!parsedAccession.contains("REV") && !parsedAccession.contains("CON")) {
                    proteinGroup.getProteinGroupHasProteins().add(createProteinGroupHasProtein(sequenceInFasta(parsedAccession, parsedFastas), parsedAccession, true, proteinGroup));
                }else {
                    omittedProteinGroupIds.add(values.get(MaxQuantProteinGroupHeaders.ID.getValue()));
                    omittedProteinGroup = true;
                }
            }else{
                if (!parsedAccession.contains("REV")){
                    String accToSearchSeq = parsedAccession;
                    if(accToSearchSeq.contains("CON")){
                        accToSearchSeq = org.apache.commons.lang3.StringUtils.substringAfter(accToSearchSeq, "CON__"); 
                    }
                    proteinGroup.getProteinGroupHasProteins().add(createProteinGroupHasProtein(sequenceInFasta(accToSearchSeq, parsedFastas), parsedAccession, true, proteinGroup));
                }else {
                    omittedProteinGroupIds.add(values.get(MaxQuantProteinGroupHeaders.ID.getValue()));
                    omittedProteinGroup = true;
                }
            }
            
        } 

        if(!omittedProteinGroup){
            maxQuantSearchSettingsParser.getAnalyticalRuns().forEach((k, v) -> {

                String intensity = values.get(MaxQuantProteinGroupHeaders.INTENSITY.getValue() + " " + v.toLowerCase());

                String lfqIntensity = values.get(MaxQuantProteinGroupHeaders.LFQ_INTENSITY.getValue() + " " + v.toLowerCase());

                String ibaq = values.get(MaxQuantProteinGroupHeaders.IBAQ.getValue() + " " + v.toLowerCase());

                String msmsCount = values.get(MaxQuantProteinGroupHeaders.MSMS_COUNT.getValue() + " " + v.toLowerCase());

                if(intensity != null || lfqIntensity != null || ibaq != null || msmsCount != null){
                    createProteinGroupQuant(proteinGroup, k, intensity, lfqIntensity, ibaq, msmsCount);
                }
                // check for all labeled quantification. If exists for the run, parse.
                parseLabeledQuantification(values, proteinGroup, k, v.toLowerCase(), optionalHeaders);
                
            });
        }
        return proteinGroup;
    }

    /**
     * Search sequence in FASTA map by using accession
     * @param accession
     * @param parsedFastas
     * @return sequence
     */
    private String sequenceInFasta(String accession, Map<String, String> parsedFastas) {
        String sequence = "";
        /*for (String key : parsedFastas.keySet()) {
        if (key.contains(accession)) {
        sequence = parsedFastas.get(key);
        }
        }*/
        if(parsedFastas.get(accession) != null){
            sequence = parsedFastas.get(accession);
        }else{
            throw new IllegalArgumentException("Protein has no sequence in Fasta file!");
        }
        /*if (sequence == null || sequence.equals("")) {
        throw new IllegalArgumentException("Protein has no sequence in Fasta file!");
        }*/
        return sequence;
    }
    
    /**
     * Create a protein and it's relation to a protein group.
     *
     * @param sequence The sequence of the protein
     * @param accession The accession of the protein
     * @param mainGroup Whether this is the main protein of the group
     * @return A ProteinGroupHasProtein object
     */
    private ProteinGroupHasProtein createProteinGroupHasProtein(String sequence, String accession, boolean mainGroup, ProteinGroup proteinGroup) {
        ProteinGroupHasProtein proteinGroupHasProtein = new ProteinGroupHasProtein();
        proteinGroupHasProtein.setIsMainGroupProtein(mainGroup);

        //get protein
        Protein protein = proteinService.getProtein(sequence, accession);

        //set protein accession
        proteinGroupHasProtein.setProteinAccession(accession);

        //set entity associations
        proteinGroupHasProtein.setProtein(protein);
        proteinGroupHasProtein.setProteinGroup(proteinGroup);

    //    proteinGroup.getProteinGroupHasProteins().add(proteinGroupHasProtein);

        return proteinGroupHasProtein;
    }

    /**
     * Create protein group quantification and it's relation to a protein group and analytical run.
     * 
     * @param proteinGroup the protein group.
     * @param analyticalRun the analytical run related to quantification.
     * @param intensity the intensity.
     * @param lfqIntensity the LFQ intensity.
     * @param ibaq the iBAQ.
     * @param msmsCount the MSMS Count.
     */
    private void createProteinGroupQuant(ProteinGroup proteinGroup, AnalyticalRun analyticalRun, String intensity, String lfqIntensity, String ibaq, String msmsCount){
        ProteinGroupQuant proteinGroupQuant = new ProteinGroupQuant();
        // set protein group.
        proteinGroupQuant.setProteinGroup(proteinGroup);
        // set analytical run.
        proteinGroupQuant.setAnalyticalRun(analyticalRun);
        // set intensity
        if(intensity != null){
            proteinGroupQuant.setIntensity(Double.parseDouble(intensity));
        }
        // set LFQ intensity
        if(lfqIntensity != null){
            proteinGroupQuant.setLfqIntensity(Double.parseDouble(lfqIntensity));
        }
        // set iBAQ
        if(ibaq != null){
            proteinGroupQuant.setIbaq(Double.parseDouble(ibaq));
        }
        // set MSMS Count
        if(msmsCount != null){
            proteinGroupQuant.setMsmsCount(Integer.parseInt(msmsCount));
        }
        // add this protein quantification to protein group.
        proteinGroup.getProteinGroupQuants().add(proteinGroupQuant);
        // add this protein quantification to the related analytical run.
        analyticalRun.getProteinGroupQuants().add(proteinGroupQuant);

    }
    
    /**
     * Create protein group quantification for labeled experiment and it's relation to a protein group and analytical run.
     * 
     * @param proteinGroup the protein group.
     * @param analyticalRun the analytical run related to quantification.
     * @param label the label.
     * @param labelValue the label value.
     */
    private void createProteinGroupQuantLabeled(ProteinGroup proteinGroup, AnalyticalRun analyticalRun, String label, String labelValue){
        ProteinGroupQuantLabeled proteinGroupQuantLabeled = new ProteinGroupQuantLabeled();
        // set the protein group
        proteinGroupQuantLabeled.setProteinGroup(proteinGroup);
        // set analytical run
        proteinGroupQuantLabeled.setAnalyticalRun(analyticalRun);
        // set label
        proteinGroupQuantLabeled.setLabel(label);
        // set label value
        if(labelValue != null){
            proteinGroupQuantLabeled.setLabelValue(Double.parseDouble(labelValue));
        }
        
        // add this protein quantification to protein group.
        proteinGroup.getProteinGroupQuantsLabeled().add(proteinGroupQuantLabeled);
        // add this protein quantification to the related analytical run.
        analyticalRun.getProteinGroupQuantsLabeled().add(proteinGroupQuantLabeled);
    }
    
    /**
     * Parse labeled quantifications for given run and protein group where quantification names come from MQPAR file.
     * If value is null or not numeric , it is not stored
     * @param values
     * @param proteinGroup
     * @param analyticalRun
     * @param experimentName 
     */
    private void parseLabeledQuantification(Map<String, String> values, ProteinGroup proteinGroup, AnalyticalRun analyticalRun, String experimentName, List<String> optionalHeaders) {
        for (int i = 0; i < 10; i++) {
            String reporterIntensityCorrected = values.get(MaxQuantProteinGroupHeaders.REPORTER_INTENSITY_CORRECTED.getValue() + " " + i + " " + experimentName);

            if (reporterIntensityCorrected != null && NumberUtils.isNumber(reporterIntensityCorrected)  && maxQuantSearchSettingsParser.getIsobaricLabels().size() >= i + 1) {
                if (maxQuantSearchSettingsParser.getIsobaricLabels().get(i) != null) {
                    createProteinGroupQuantLabeled(proteinGroup, analyticalRun, maxQuantSearchSettingsParser.getIsobaricLabels().get(i), reporterIntensityCorrected);
                } else {
                    createProteinGroupQuantLabeled(proteinGroup, analyticalRun, MaxQuantProteinGroupHeaders.REPORTER_INTENSITY_CORRECTED.getValue() + " " + i, reporterIntensityCorrected);
                }
            }
        }

        String intensityL = values.get(MaxQuantProteinGroupHeaders.INTENSITY_L.getValue() + " " + experimentName);
        String intensityM = values.get(MaxQuantProteinGroupHeaders.INTENSITY_M.getValue() + " " + experimentName);
        String intensityH = values.get(MaxQuantProteinGroupHeaders.INTENSITY_H.getValue() + " " + experimentName);
        // if there are 3 label mods, we have 3 sample experiment (L, M, H).
        // if 2, we have 2 sample experiment (L, H). There is no 1 sample option for SILAC.
        if (maxQuantSearchSettingsParser.getLabelMods().size() == 3) {
            if (intensityL != null && NumberUtils.isNumber(intensityL)  && maxQuantSearchSettingsParser.getLabelMods().size() >= 1) {
                if (maxQuantSearchSettingsParser.getLabelMods().get(0) != null) {
                    createProteinGroupQuantLabeled(proteinGroup, analyticalRun, maxQuantSearchSettingsParser.getLabelMods().get(0), intensityL);
                } else {
                    createProteinGroupQuantLabeled(proteinGroup, analyticalRun, MaxQuantProteinGroupHeaders.INTENSITY_L.getValue(), intensityL);
                }

            }
            if (intensityM != null && NumberUtils.isNumber(intensityM)  && maxQuantSearchSettingsParser.getLabelMods().size() >= 2) {
                if (maxQuantSearchSettingsParser.getLabelMods().get(1) != null) {
                    createProteinGroupQuantLabeled(proteinGroup, analyticalRun, maxQuantSearchSettingsParser.getLabelMods().get(1), intensityM);
                } else {
                    createProteinGroupQuantLabeled(proteinGroup, analyticalRun, MaxQuantProteinGroupHeaders.INTENSITY_M.getValue(), intensityM);
                }

            }
            if (intensityH != null && NumberUtils.isNumber(intensityH)  && maxQuantSearchSettingsParser.getLabelMods().size() >= 3) {
                if (maxQuantSearchSettingsParser.getLabelMods().get(2) != null) {
                    createProteinGroupQuantLabeled(proteinGroup, analyticalRun, maxQuantSearchSettingsParser.getLabelMods().get(2), intensityH);
                } else {
                    createProteinGroupQuantLabeled(proteinGroup, analyticalRun, MaxQuantProteinGroupHeaders.INTENSITY_H.getValue(), intensityH);
                }
            }
        } else if (maxQuantSearchSettingsParser.getLabelMods().size() == 2) {
            if (intensityL != null && NumberUtils.isNumber(intensityL)  && maxQuantSearchSettingsParser.getLabelMods().size() >= 1) {
                if (maxQuantSearchSettingsParser.getLabelMods().get(0) != null) {
                    createProteinGroupQuantLabeled(proteinGroup, analyticalRun, maxQuantSearchSettingsParser.getLabelMods().get(0), intensityL);
                } else {
                    createProteinGroupQuantLabeled(proteinGroup, analyticalRun, MaxQuantProteinGroupHeaders.INTENSITY_L.getValue(), intensityL);
                }

            }
            if (intensityH != null && NumberUtils.isNumber(intensityH) && maxQuantSearchSettingsParser.getLabelMods().size() >= 2) {
                if (maxQuantSearchSettingsParser.getLabelMods().get(1) != null) {
                    createProteinGroupQuantLabeled(proteinGroup, analyticalRun, maxQuantSearchSettingsParser.getLabelMods().get(1), intensityH);
                } else {
                    createProteinGroupQuantLabeled(proteinGroup, analyticalRun, MaxQuantProteinGroupHeaders.INTENSITY_H.getValue(), intensityH);
                }
            }
        }
        // if given header has numeric value per run and protein group, store.
        optionalHeaders.stream().map(String::toLowerCase).filter(header -> {
            return values.get(header + " " + experimentName) != null && NumberUtils.isNumber(values.get(header + " " + experimentName));
        })
            .forEach(optionalHeader ->{
            createProteinGroupQuantLabeled(proteinGroup, analyticalRun, optionalHeader, values.get(optionalHeader + " " + experimentName));
        });

    }
}

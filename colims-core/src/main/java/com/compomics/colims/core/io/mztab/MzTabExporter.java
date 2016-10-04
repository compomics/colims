/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.io.mztab;

import com.compomics.colims.core.service.QuantificationSettingsService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.QuantificationMethodHasReagent;
import com.compomics.colims.model.QuantificationSettings;
import com.compomics.colims.model.SearchAndValidationSettings;
import com.compomics.colims.model.SearchParametersHasModification;
import com.compomics.colims.model.enums.ModificationType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Niels Hulstaert
 */
@Component("mzTabExporter")
public class MzTabExporter {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(MzTabExporter.class);
    private static final String JSON_VALUES = "values";
    private static final String JSON_NAME = "user_friendly_name";
    private static final String MZTAB_EXTENSION = ".mzTab";
    private static final String COLUMN_DELIMITER = "/t";
    private static final String COMMENT_PREFIX = "COM";
    private static final String METADATA_PREFIX = "MTD";
    private static final String PROTEINS_HEADER_PREFIX = "PRH";
    private static final String PROTEINS_PREFIX = "PRT";
    private static final String OPEN_BRACKET = "[";
    private static final String CLOSE_BRACKET = "]";
    private static final String COMMA_SEPARATOR = ", ";
    /**
     * Metadata section.
     */
    private static final String MZTAB_VERSION = "mzTab_version";
    private static final String VERSION = "1.0.0";
    private static final String MZTAB_MODE = "mzTab_mode";
    private static final String MODE_SUMMARY = "Summary";
    private static final String MZTAB_COMPLETE = "Complete";
    private static final String MZTAB_TYPE = "mzTab_type";
    private static final String MZTAB_ID = "mzTab_ID";
    private static final String DESCRIPTION = "description";
    private static final String RUN_LOCATION = "ms_run[%d]-location";
    private static final String FIXED_MOD = "fixed_mod[%d]";
    private static final String VARIABLE_MOD = "variable_mod[%d]";
    private static final String STUDY_VARIABLE_ASSAY_REFS = "study_variable[%d]-assay_refs";
    private static final String STUDY_VARIABLE_DESCRIPTION = "study_variable[%d]-description";
    private static final String SOFTWARE = "software[%d]";
    private static final String ASSAY_RUN_REF = "assay[%d]-ms_run_ref";
    private static final String MS_RUN_REF = "ms_run[%d]";
    private static final String ASSAY = "assay[%d]";
    private static final String ASSAY_QUANTIFICATION_REAGENT = "assay[%d]-quantification_reagent";
    /**
     * Search engine metadata.
     */
    private static final String PROTEIN_SEARCH_ENGINE_SCORE = "protein_search_engine_score[%d]";
    private static final String PEPTIDE_SEARCH_ENGINE_SCORE = "peptide_search_engine_score[%d]";
    private static final String PSM_SEARCH_ENGINE_SCORE = "psm_search_engine_score[%d]";
    private static final String SMALL_MOLECULE_SEARCH_ENGINE_SCORE = "smallmolecule_search_engine_score[%d]";
    /**
     * Quantification metadata.
     */
    private static final String QUANTIFICATION_METHOD = "quantification_method";
    private static final String PROTEIN_QUANTIFICATION_UNIT = "protein-quantification-unit";
    private static final String PEPTIDE_QUANTIFICATION_UNIT = "peptide-quantification-unit";
    private static final String SMALL_MOLECULE_QUANTIFICATION_UNIT = "smallmolecule-quantification-unit";
    /**
     * Instrument metadata.
     */
    private static final String INSTRUMENT_NAME = "instrument[%d]-name";
    private static final String INSTRUMENT_SOURCE = "instrument[%d]-source";
    private static final String INSTRUMENT_ANALYZER = "instrument[%d]-analyzer[%d]";
    private static final String INSTRUMENT_DETECTOR = "instrument[%d]-detector";
    private static final String CONTACT_NAME = "contact[%d]-name";
    private static final String CONTACT_AFFILIATION = "contact[%d]-affiliation";
    private static final String CONTACT_EMAIL = "contact[%d]-email";
    /**
     * mztab Json mapping
     */
    private static final int PROTEIN_SEARCH_ENGINE_SCORE_ALIGNMENT = 0;
    private static final int PEPTIDE_SEARCH_ENGINE_SCORE_ALIGNMENT = 1;
    private static final int PSM_SEARCH_ENGINE_SCORE_ALIGNMENT = 2;
    private static final int SOFTWARE_ALIGNMENT = 4;
    /**
     * protein section
     */
    private static final String ACCESSION = "accession";
    private static final String TAXID = "taxid";
    private static final String SPECIES = "species";
    private static final String DATABASE = "database";
    private static final String DATABASE_VERSION = "database_version";
    private static final String SEARCH_ENGINE = "search_engine";
    private static final String BEST_SEARCH_ENGINE_SCORE = "best_search_engine_score[%d]";
    private static final String AMBIGUITY_MEMBERS = "ambiguity_members";
    private static final String MODIFICATIONS = "modifications";
    private static final String PROTEIN_COVERAGE = "protein_coverage";
    private static final String PROTEIN_ABUNDANCE_STUDY_VARIABLE = "protein_abundance_study_variable[%d]";
    private static final String PROTEIN_ABUNDANCE_STDEV_STUDY_VARIABLE = "protein_abundance_stdev_study_variable[%d]";
    private static final String PROTEIN_ABUNDANCE_STD_ERROR_STUDY_VARIABLE = "protein_abundance_std_error_study_variable[%d]";
    private static final String SEARCH_ENGINE_SCORE_MS_RUN = "search_engine_score[%d]_ms_run[%d]";
    private static final String NUM_PSMS_MS_RUN = "num_psms_ms_run[%d]";
    private static final String NUM_PEPTIDES_DISTINCT_MS_RUN = "num_peptides_distinct_ms_run[%d]";
    private static final String NUM_PEPTIDE_UNIQUE_MS_RUN = "num_peptide_unique_ms_run[%d]";
    private static final String PROTEIN_ABUNDANCE_ASSAY = "protein_abundance_assay[%d]";
    
    private final ObjectMapper mapper = new ObjectMapper();
    private List<MzTabParam> mzTabParams = new ArrayList<>();
    private final QuantificationSettingsService quantificationSettingsService;
    /**
     * The MzTabExport instance.
     */
    private MzTabExport mzTabExport;

    public MzTabExporter(QuantificationSettingsService quantificationSettingsService) {
        this.quantificationSettingsService = quantificationSettingsService;
    }

    /**
     * Inits the exporter; parses the mzTab JSON file into java objects.
     *
     * @throws IOException IOException thrown in case of an I/O related problem
     */
    @PostConstruct
    public void init() throws IOException {
        Resource mzTabJson = new ClassPathResource("config/mztab.json");
        JsonNode mzTabParamsNode = mapper.readTree(mzTabJson.getInputStream());

        //parse JSON node to a list of MzTabParam instances
        mzTabParams = parseJsonNode(mzTabParamsNode);
    }

    /**
     * Export the mzTabExport input to a mzTab file.
     *
     * @param mzTabExport the MzTabExport instance
     */
    public void export(MzTabExport mzTabExport) {
        this.mzTabExport = mzTabExport;
        switch (mzTabExport.getMzTabType()) {
            case QUANTIFICATION:
                switch (mzTabExport.getMzTabMode()) {
                    case SUMMARY:
                        break;
                    case COMPLETE:
                        break;
                    default:
                        break;
                }
                break;
            case IDENTIFICATION:
                switch (mzTabExport.getMzTabMode()) {
                    case SUMMARY:
                        break;
                    case COMPLETE:
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        try (FileOutputStream fos = new FileOutputStream(new File(mzTabExport.getExportDirectory(), mzTabExport.getFileName() + MZTAB_EXTENSION));
             OutputStreamWriter osw = new OutputStreamWriter(fos, Charset.forName("UTF-8").newEncoder());
             BufferedWriter bw = new BufferedWriter(osw);
             PrintWriter pw = new PrintWriter(bw)) {

            pw.println("under development");

        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private String constructMetadata() {
        StringBuilder metada = new StringBuilder();

        //version, type, mode and description
        metada.append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(MZTAB_VERSION).append(COLUMN_DELIMITER).append(VERSION).append(System.lineSeparator());
        metada.append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(MZTAB_MODE).append(COLUMN_DELIMITER).append(mzTabExport.getMzTabMode().mzTabName()).append(System.lineSeparator());
        metada.append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(MZTAB_TYPE).append(COLUMN_DELIMITER).append(mzTabExport.getMzTabType().mzTabName()).append(System.lineSeparator());
        metada.append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(DESCRIPTION).append(COLUMN_DELIMITER).append(mzTabExport.getDescription()).append(System.lineSeparator());
        //run locations
        for (int i = 0; i < mzTabExport.getRuns().size(); i++) {
            metada.append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(String.format(RUN_LOCATION, i+1)).append(COLUMN_DELIMITER).append(mzTabExport.getRuns().get(i).getStorageLocation()).append(System.lineSeparator());
        }
        //protein quantification unit (relative quantification unit from mztab.json)
        metada.append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(PROTEIN_QUANTIFICATION_UNIT).append(COLUMN_DELIMITER).append(
                createOntology(mzTabParams.get(3).getMzTabParamOptions().get(1).getOntology(),mzTabParams.get(3).getMzTabParamOptions().get(1).getAccession(), mzTabParams.get(3).getMzTabParamOptions().get(1).getName())).append(System.lineSeparator());
        
        //peptide quantification unit (relative quantification unit from mztab.json)(same with protein quant unit)
        metada.append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(PEPTIDE_QUANTIFICATION_UNIT).append(COLUMN_DELIMITER).append(
                createOntology(mzTabParams.get(3).getMzTabParamOptions().get(1).getOntology(),mzTabParams.get(3).getMzTabParamOptions().get(1).getAccession(), mzTabParams.get(3).getMzTabParamOptions().get(1).getName())).append(System.lineSeparator());
        
        //software
        metada.append(setSoftwareAndEngineScore(SOFTWARE, SOFTWARE_ALIGNMENT));

        // protein search engine score
        metada.append(setSoftwareAndEngineScore(PROTEIN_SEARCH_ENGINE_SCORE, PROTEIN_SEARCH_ENGINE_SCORE_ALIGNMENT));
        
        // peptide search engine score
        metada.append(setSoftwareAndEngineScore(PEPTIDE_SEARCH_ENGINE_SCORE, PEPTIDE_SEARCH_ENGINE_SCORE_ALIGNMENT));
        
        // psm search engine score
        metada.append(setSoftwareAndEngineScore(PSM_SEARCH_ENGINE_SCORE, PSM_SEARCH_ENGINE_SCORE_ALIGNMENT));
        
        // fixed modifications
        int counter = 1;
        for (int i = 0; i < mzTabExport.getRuns().size(); i++) {
            for(SearchParametersHasModification searchParametersHasModification : getSearchAndValidationSettings(mzTabExport.getRuns().get(i)).getSearchParameters().getSearchParametersHasModifications()){
                if(searchParametersHasModification.getModificationType().equals(ModificationType.FIXED) && searchParametersHasModification.getSearchModification().getAccession() != null){
                    metada.append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(String.format(FIXED_MOD, counter)).append(COLUMN_DELIMITER)
                        .append(createOntology(StringUtils.substringBefore(searchParametersHasModification.getSearchModification().getAccession(), ":"),searchParametersHasModification.getSearchModification().getAccession(), searchParametersHasModification.getSearchModification().getName())).append(System.lineSeparator());
                    counter++;
                }
            } 
        }
        
        // variable modifications
        counter = 1;
        for (int i = 0; i < mzTabExport.getRuns().size(); i++) {
            for(SearchParametersHasModification searchParametersHasModification : getSearchAndValidationSettings(mzTabExport.getRuns().get(i)).getSearchParameters().getSearchParametersHasModifications()){
                if(searchParametersHasModification.getModificationType().equals(ModificationType.VARIABLE) && searchParametersHasModification.getSearchModification().getAccession() != null){
                    metada.append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(String.format(VARIABLE_MOD, counter)).append(COLUMN_DELIMITER)
                        .append(createOntology(StringUtils.substringBefore(searchParametersHasModification.getSearchModification().getAccession(), ":"),searchParametersHasModification.getSearchModification().getAccession(), searchParametersHasModification.getSearchModification().getName())).append(System.lineSeparator());
                    counter++;
                }
            } 
        }
         
        // quantification method
        metada.append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(QUANTIFICATION_METHOD).append(COLUMN_DELIMITER).append(
                createOntology(StringUtils.substringBefore(getQuantificationSettings(mzTabExport.getRuns().get(0)).getQuantificationMethodCvParam().getAccession(), ":"),
                    getQuantificationSettings(mzTabExport.getRuns().get(0)).getQuantificationMethodCvParam().getAccession(), getQuantificationSettings(mzTabExport.getRuns().get(0)).getQuantificationMethodCvParam().getName())).append(System.lineSeparator());
        
        //assay quantification reagents
        for (int i = 0; i < mzTabExport.getRuns().size(); i++) {
            counter = 0;
            for(QuantificationMethodHasReagent quantificationMethodHasReagent : getQuantificationSettings(mzTabExport.getRuns().get(i)).getQuantificationMethodCvParam().getQuantificationMethodHasReagents()){
                int assayNumber = mzTabExport.getAnalyticalRunsAssaysRefs().get(mzTabExport.getRuns().get(i))[counter];
                metada.append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(String.format(ASSAY_QUANTIFICATION_REAGENT, assayNumber)).append(COLUMN_DELIMITER).
                        append(createOntology(StringUtils.substringBefore(quantificationMethodHasReagent.getQuantificationReagent().getAccession(), ":"), quantificationMethodHasReagent.getQuantificationReagent().getAccession(), quantificationMethodHasReagent.getQuantificationReagent().getName())).append(System.lineSeparator());
                counter++;
            }
            mzTabExport.getAnalyticalRunsAssaysRefs().get(mzTabExport.getRuns().get(i));
            
        }
        // assay ms run reference
        for (int i = 0; i < mzTabExport.getRuns().size(); i++) {
            for(int j = 0; j < getQuantificationSettings(mzTabExport.getRuns().get(i)).getQuantificationMethodCvParam().getQuantificationMethodHasReagents().size(); j++){
                int assayNumber = mzTabExport.getAnalyticalRunsAssaysRefs().get(mzTabExport.getRuns().get(i))[j];
                metada.append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(String.format(ASSAY_RUN_REF, assayNumber)).append(COLUMN_DELIMITER).
                        append(String.format(MS_RUN_REF, i+1)).append(System.lineSeparator());
            }  
        }
        
        // study variable assay references
        counter = 1;
        for(Map.Entry<String, int[]> studyVariablesAssaysRefs : mzTabExport.getStudyVariablesAssaysRefs().entrySet()) {
            String[] assay = new String[]{};
            for(int i = 0; i < studyVariablesAssaysRefs.getValue().length; i++){
                assay[i] = String.format(ASSAY, studyVariablesAssaysRefs.getValue()[i]);   
            }
            String assays = StringUtils.join(assay, ',');
            metada.append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(String.format(STUDY_VARIABLE_ASSAY_REFS, counter)).append(COLUMN_DELIMITER).
                        append(assays).append(System.lineSeparator());
            counter++;
        }
        
        // study variable description
        counter = 1;
        for(Map.Entry<String, int[]> studyVariablesAssaysRefs : mzTabExport.getStudyVariablesAssaysRefs().entrySet()) {
            metada.append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(String.format(STUDY_VARIABLE_DESCRIPTION, counter)).append(COLUMN_DELIMITER).
                    append(studyVariablesAssaysRefs.getKey()).append(System.lineSeparator());
            counter++;
        }
        return metada.toString();
    }
    
    private String constructProteins(){
        StringBuilder proteins = new StringBuilder();
        // protein headers
        proteins.append(PROTEINS_HEADER_PREFIX).append(COLUMN_DELIMITER).append(MZTAB_VERSION).append(COLUMN_DELIMITER).append(VERSION).append(System.lineSeparator());
        
        return proteins.toString();
    }

    /**
     * This method parses the JSON root node and returns a list of MzTabParam instances.
     *
     * @param jsonNode the root JsonNode
     * @return the list of MzTabParam instances
     * @throws IOException thrown in case of an I/O related problem
     */
    private List<MzTabParam> parseJsonNode(JsonNode jsonNode) throws IOException {
        List<MzTabParam> mzTabParamList = new ArrayList<>();

        Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();

            MzTabParam mzTabParam = new MzTabParam(entry.getKey());

            JsonNode nameNode = entry.getValue().get("name");
            mzTabParam.setUserFriendlyName(nameNode.get(JSON_NAME).asText());

            Iterator<JsonNode> optionElements = entry.getValue().get("values").elements();
            while (optionElements.hasNext()) {
                MzTabParamOption mzTabParamOption = mapper.treeToValue(optionElements.next(), MzTabParamOption.class);
                mzTabParam.addOption(mzTabParamOption);
            }

            mzTabParamList.add(mzTabParam);
        }

        return mzTabParamList;
    }

    /**
     * Set software and engine scores of the meta data section.
     * @param metadata
     * @param field
     * @param alignment
     * @return 
     */
    private String setSoftwareAndEngineScore(String field, int alignment) {
        StringBuilder metadata = new StringBuilder();
        List<String> softwares = new ArrayList<>();
        int counter = 1;
        for (int i = 0; i < mzTabExport.getRuns().size(); i++) {
            String software = getQuantificationSettings(mzTabExport.getRuns().get(i)).getQuantificationEngine().getName();
            if(!softwares.contains(software)){
                if(software.equals("PEPTIDESHAKER")){
                    metadata.append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(String.format(field, counter)).append(COLUMN_DELIMITER)
                        .append(createOntology(mzTabParams.get(alignment).getMzTabParamOptions().get(2).getOntology(),mzTabParams.get(alignment).getMzTabParamOptions().get(2).getAccession(), mzTabParams.get(alignment).getMzTabParamOptions().get(2).getName())).append(System.lineSeparator());
                }else if(software.equals("MAXQUANT")){
                    metadata.append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(String.format(field, counter)).append(COLUMN_DELIMITER)
                        .append(createOntology(mzTabParams.get(alignment).getMzTabParamOptions().get(1).getOntology(),mzTabParams.get(alignment).getMzTabParamOptions().get(1).getAccession(), mzTabParams.get(alignment).getMzTabParamOptions().get(1).getName())).append(System.lineSeparator()); 
                }
                counter++;
                softwares.add(software);
            }            
        }
        return metadata.toString();
    }

    /**
     * Create ontology list with the given variables.
     * @param ontology
     * @param accession
     * @param name
     * @return ontology list string
     */
    private String createOntology(String ontology, String accession, String name){
        StringBuilder ontologyBuilder = new StringBuilder();
        return ontologyBuilder.append(OPEN_BRACKET).append(ontology).append(COMMA_SEPARATOR).append(accession).append(COMMA_SEPARATOR)
                .append(name).append(COMMA_SEPARATOR).append(CLOSE_BRACKET).toString();
    }
    
    /**
     * Get the quantification settings
     * @param analyticalRuns
     * @return quantificationSettingsMap
     */
    private QuantificationSettings getQuantificationSettings(AnalyticalRun analyticalRun){
        //return quantificationSettingsService.getbyAnalyticalRun(analyticalRun);
        return analyticalRun.getQuantificationSettings();
    }
    
     /**
     * Get the search and validation settings
     * @param analyticalRuns
     * @return quantificationSettingsMap
     */
    private SearchAndValidationSettings getSearchAndValidationSettings(AnalyticalRun analyticalRun){
        //return quantificationSettingsService.getbyAnalyticalRun(analyticalRun);
        return analyticalRun.getSearchAndValidationSettings();
    }
}

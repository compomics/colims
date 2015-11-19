/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.io.mztab;

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

    private final ObjectMapper mapper = new ObjectMapper();
    private List<MzTabParam> mzTabParams = new ArrayList<>();
    /**
     * The MzTabExport instance.
     */
    private MzTabExport mzTabExport;

    /**
     * Inits the exporter; parses the mzTab json file into java objects.
     *
     * @throws IOException IOException thrown in case of an I/O related problem
     */
    @PostConstruct
    public void init() throws IOException {
        Resource mzTabJson = new ClassPathResource("config/mztab.json");
        JsonNode mzTabParamsNode = mapper.readTree(mzTabJson.getInputStream());

        //parse Json node to a list of MzTabParam instances
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
            metada.append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(String.format(RUN_LOCATION, i)).append(COLUMN_DELIMITER).append(mzTabExport.getRuns().get(i).getStorageLocation()).append(System.lineSeparator());
        }
        //search engine scores

        return metada.toString();
    }

    /**
     * This method parses the json root node and returns a list of MzTabParam instances.
     *
     * @param jsonNode the root JsonNode
     * @return the list of MzTabParam instances
     * @throws IOException thrown in case of an I/O related problem
     */
    private List<MzTabParam> parseJsonNode(JsonNode jsonNode) throws IOException {
        List<MzTabParam> mzTabParams = new ArrayList<>();

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

            mzTabParams.add(mzTabParam);
        }

        return mzTabParams;
    }

    /**
     * Get the protein search engine score line.
     *
     * @return the protein search engine score String value
     */
    private String getProteinSearchEngineScore() {
        StringBuilder searchEngineScore = new StringBuilder();

        searchEngineScore.append(METADATA_PREFIX).append(COLUMN_DELIMITER)
                .append(String.format(PROTEIN_SEARCH_ENGINE_SCORE, 1)).append(COLUMN_DELIMITER);

        return searchEngineScore.toString();
    }

}

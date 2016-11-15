/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.io.mztab;

import com.compomics.colims.core.service.FastaDbService;
import com.compomics.colims.core.service.PeptideService;
import com.compomics.colims.core.service.ProteinGroupQuantLabeledService;
import com.compomics.colims.core.service.ProteinGroupQuantService;
import com.compomics.colims.core.service.ProteinGroupService;
import com.compomics.colims.core.service.QuantificationSettingsService;
import com.compomics.colims.core.service.SearchAndValidationSettingsService;
import com.compomics.colims.core.service.UniProtService;
import com.compomics.colims.core.util.AccessionConverter;
import com.compomics.colims.core.util.ProteinCoverage;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.PeptideHasProteinGroup;
import com.compomics.colims.model.ProteinGroupHasProtein;
import com.compomics.colims.model.ProteinGroupQuantLabeled;
import com.compomics.colims.model.QuantificationMethodHasReagent;
import com.compomics.colims.model.QuantificationSettings;
import com.compomics.colims.model.SearchAndValidationSettings;
import com.compomics.colims.model.SearchParametersHasModification;
import com.compomics.colims.model.enums.FastaDbType;
import com.compomics.colims.model.enums.ModificationType;
import com.compomics.colims.repository.hibernate.ProteinGroupDTO;
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
import java.util.HashMap;
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
    private static final String COLUMN_DELIMITER = "\t";
    private static final String COMMENT_PREFIX = "COM";
    private static final String METADATA_PREFIX = "MTD";
    private static final String PROTEINS_HEADER_PREFIX = "PRH";
    private static final String PROTEINS_PREFIX = "PRT";
    private static final String OPEN_BRACKET = "[";
    private static final String CLOSE_BRACKET = "]";
    private static final String COMMA_SEPARATOR = ", ";
    private static final String VERTICAL_BAR = "|";
    private static final String UNLABELED_SAMPLE = "unlabeled sample";
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
    /**
     * assayReagentRef (key : assay, value : quantification reagent)
     */
    private final Map<String, String> assayReagentRef = new HashMap<>();
    /**
     * assayAnalyticalRunRef (key : assay, value : analytical run)
     */
    private final Map<String, AnalyticalRun> assayAnalyticalRunRef = new HashMap<>();
    private final QuantificationSettingsService quantificationSettingsService;
    private final ProteinGroupService proteinGroupService;
    private final PeptideService peptideService;
    private final ProteinGroupQuantLabeledService proteinGroupQuantLabeledService;
    private final ProteinGroupQuantService proteinGroupQuantService;
    private final SearchAndValidationSettingsService searchAndValidationSettingsService;
    private final FastaDbService fastaDbService;
    private final UniProtService uniProtService;

    /**
     * The MzTabExport instance.
     */
    private MzTabExport mzTabExport;

    public MzTabExporter(QuantificationSettingsService quantificationSettingsService, ProteinGroupService proteinGroupService,
            SearchAndValidationSettingsService searchAndValidationSettingsService, FastaDbService fastaDbService, UniProtService uniProtService,
            ProteinGroupQuantLabeledService proteinGroupQuantLabeledService, ProteinGroupQuantService proteinGroupQuantService, PeptideService peptideService) {
        this.quantificationSettingsService = quantificationSettingsService;
        this.proteinGroupService = proteinGroupService;
        this.searchAndValidationSettingsService = searchAndValidationSettingsService;
        this.fastaDbService = fastaDbService;
        this.uniProtService = uniProtService;
        this.proteinGroupQuantLabeledService = proteinGroupQuantLabeledService;
        this.proteinGroupQuantService = proteinGroupQuantService;
        this.peptideService = peptideService;
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
    public void export(MzTabExport mzTabExport) throws IOException {
        this.mzTabExport = mzTabExport;
        switch (mzTabExport.getMzTabType()) {
            case QUANTIFICATION:
                switch (mzTabExport.getMzTabMode()) {
                    case SUMMARY:
                        break;
                    case COMPLETE:
                        constructMetadata();
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

            pw.println(constructMetadata());
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private String constructMetadata() throws IOException {
        StringBuilder metada = new StringBuilder();

        //version, type, mode and description
        metada.append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(MZTAB_VERSION).append(COLUMN_DELIMITER).append(VERSION).append(System.lineSeparator());
        metada.append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(MZTAB_MODE).append(COLUMN_DELIMITER).append(mzTabExport.getMzTabMode().mzTabName()).append(System.lineSeparator());
        metada.append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(MZTAB_TYPE).append(COLUMN_DELIMITER).append(mzTabExport.getMzTabType().mzTabName()).append(System.lineSeparator());
        metada.append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(DESCRIPTION).append(COLUMN_DELIMITER).append(mzTabExport.getDescription()).append(System.lineSeparator());
        //run locations
        for (int i = 0; i < mzTabExport.getRuns().size(); i++) {
            metada.append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(String.format(RUN_LOCATION, i + 1)).append(COLUMN_DELIMITER).append(mzTabExport.getRuns().get(i).getStorageLocation()).append("\\").append(mzTabExport.getRuns().get(i).getName()).append(System.lineSeparator());
        }
        //protein quantification unit (relative quantification unit from mztab.json)
        metada.append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(PROTEIN_QUANTIFICATION_UNIT).append(COLUMN_DELIMITER).append(
                createOntology(mzTabParams.get(3).getMzTabParamOptions().get(1).getOntology(), mzTabParams.get(3).getMzTabParamOptions().get(1).getAccession(), mzTabParams.get(3).getMzTabParamOptions().get(1).getName())).append(System.lineSeparator());

        //peptide quantification unit (relative quantification unit from mztab.json)(same with protein quant unit)
        metada.append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(PEPTIDE_QUANTIFICATION_UNIT).append(COLUMN_DELIMITER).append(
                createOntology(mzTabParams.get(3).getMzTabParamOptions().get(1).getOntology(), mzTabParams.get(3).getMzTabParamOptions().get(1).getAccession(), mzTabParams.get(3).getMzTabParamOptions().get(1).getName())).append(System.lineSeparator());

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
        List<String> modifications = new ArrayList<>();
        for (int i = 0; i < mzTabExport.getRuns().size(); i++) {
            for (SearchParametersHasModification searchParametersHasModification : getSearchAndValidationSettings(mzTabExport.getRuns().get(i)).getSearchParameters().getSearchParametersHasModifications()) {
                if (searchParametersHasModification.getModificationType().equals(ModificationType.FIXED) && searchParametersHasModification.getSearchModification().getAccession() != null
                        && !modifications.contains(searchParametersHasModification.getSearchModification().getName())) {
                    modifications.add(searchParametersHasModification.getSearchModification().getName());
                    metada.append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(String.format(FIXED_MOD, counter)).append(COLUMN_DELIMITER)
                            .append(createOntology(StringUtils.substringBefore(searchParametersHasModification.getSearchModification().getAccession(), ":"), searchParametersHasModification.getSearchModification().getAccession(), searchParametersHasModification.getSearchModification().getName())).append(System.lineSeparator());
                    counter++;
                }
            }
        }

        // variable modifications
        counter = 1;
        modifications = new ArrayList<>();
        for (int i = 0; i < mzTabExport.getRuns().size(); i++) {
            for (SearchParametersHasModification searchParametersHasModification : getSearchAndValidationSettings(mzTabExport.getRuns().get(i)).getSearchParameters().getSearchParametersHasModifications()) {
                if (searchParametersHasModification.getModificationType().equals(ModificationType.VARIABLE) && searchParametersHasModification.getSearchModification().getAccession() != null
                        && !modifications.contains(searchParametersHasModification.getSearchModification().getName())) {
                    modifications.add(searchParametersHasModification.getSearchModification().getName());
                    metada.append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(String.format(VARIABLE_MOD, counter)).append(COLUMN_DELIMITER)
                            .append(createOntology(StringUtils.substringBefore(searchParametersHasModification.getSearchModification().getAccession(), ":"), searchParametersHasModification.getSearchModification().getAccession(), searchParametersHasModification.getSearchModification().getName())).append(System.lineSeparator());
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
            // label free
            if (getQuantificationSettings(mzTabExport.getRuns().get(i)).getQuantificationMethodCvParam().getQuantificationMethodHasReagents().isEmpty()) {
                int assayNumber = mzTabExport.getAnalyticalRunsAssaysRefs().get(mzTabExport.getRuns().get(i))[counter];
                metada.append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(String.format(ASSAY_QUANTIFICATION_REAGENT, assayNumber)).append(COLUMN_DELIMITER).
                        append(createOntology(mzTabParams.get(5).getMzTabParamOptions().get(2).getOntology(), mzTabParams.get(5).getMzTabParamOptions().get(2).getAccession(), mzTabParams.get(5).getMzTabParamOptions().get(2).getName())).append(System.lineSeparator());
                assayReagentRef.put(String.format(ASSAY, assayNumber), mzTabParams.get(5).getMzTabParamOptions().get(2).getName());

            }
            // labeled
            for (QuantificationMethodHasReagent quantificationMethodHasReagent : getQuantificationSettings(mzTabExport.getRuns().get(i)).getQuantificationMethodCvParam().getQuantificationMethodHasReagents()) {
                int assayNumber = mzTabExport.getAnalyticalRunsAssaysRefs().get(mzTabExport.getRuns().get(i))[counter];
                metada.append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(String.format(ASSAY_QUANTIFICATION_REAGENT, assayNumber)).append(COLUMN_DELIMITER).
                        append(createOntology(StringUtils.substringBefore(quantificationMethodHasReagent.getQuantificationReagent().getAccession(), ":"), quantificationMethodHasReagent.getQuantificationReagent().getAccession(), quantificationMethodHasReagent.getQuantificationReagent().getName())).append(System.lineSeparator());
                assayReagentRef.put(String.format(ASSAY, assayNumber), quantificationMethodHasReagent.getQuantificationReagent().getName());
                counter++;
            }
            mzTabExport.getAnalyticalRunsAssaysRefs().get(mzTabExport.getRuns().get(i));

        }
        // assay ms run reference
        for (int i = 0; i < mzTabExport.getRuns().size(); i++) {
            // label free
            if (getQuantificationSettings(mzTabExport.getRuns().get(i)).getQuantificationMethodCvParam().getQuantificationMethodHasReagents().isEmpty()) {
                int assayNumber = mzTabExport.getAnalyticalRunsAssaysRefs().get(mzTabExport.getRuns().get(i))[0];
                metada.append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(String.format(ASSAY_RUN_REF, assayNumber)).append(COLUMN_DELIMITER).
                        append(String.format(MS_RUN_REF, i + 1)).append(System.lineSeparator());
                assayAnalyticalRunRef.put(String.format(ASSAY, assayNumber), mzTabExport.getRuns().get(i));
            }
            // labeled
            for (int j = 0; j < getQuantificationSettings(mzTabExport.getRuns().get(i)).getQuantificationMethodCvParam().getQuantificationMethodHasReagents().size(); j++) {
                int assayNumber = mzTabExport.getAnalyticalRunsAssaysRefs().get(mzTabExport.getRuns().get(i))[j];
                metada.append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(String.format(ASSAY_RUN_REF, assayNumber)).append(COLUMN_DELIMITER).
                        append(String.format(MS_RUN_REF, i + 1)).append(System.lineSeparator());
                assayAnalyticalRunRef.put(String.format(ASSAY, assayNumber), mzTabExport.getRuns().get(i));
            }
        }

        // study variable assay references
        counter = 1;
        for (Map.Entry<String, int[]> studyVariablesAssaysRefs : mzTabExport.getStudyVariablesAssaysRefs().entrySet()) {
            String[] assay = new String[studyVariablesAssaysRefs.getValue().length];
            for (int i = 0; i < studyVariablesAssaysRefs.getValue().length; i++) {
                assay[i] = String.format(ASSAY, studyVariablesAssaysRefs.getValue()[i]);
            }
            String assays = StringUtils.join(assay, ',');
            metada.append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(String.format(STUDY_VARIABLE_ASSAY_REFS, counter)).append(COLUMN_DELIMITER).
                    append(assays).append(System.lineSeparator());
            counter++;
        }

        // study variable description
        counter = 1;
        for (Map.Entry<String, int[]> studyVariablesAssaysRefs : mzTabExport.getStudyVariablesAssaysRefs().entrySet()) {
            metada.append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(String.format(STUDY_VARIABLE_DESCRIPTION, counter)).append(COLUMN_DELIMITER).
                    append(studyVariablesAssaysRefs.getKey()).append(System.lineSeparator());
            counter++;
        }

        metada.append(constructProteins());

        return metada.toString();
    }

    private String constructProteins() throws IOException {
        StringBuilder proteins = new StringBuilder();
        // protein headers
        proteins.append(PROTEINS_HEADER_PREFIX).append(COLUMN_DELIMITER).append(ACCESSION).append(COLUMN_DELIMITER).append(DESCRIPTION)
                .append(COLUMN_DELIMITER).append(TAXID).append(COLUMN_DELIMITER).append(SPECIES).append(COLUMN_DELIMITER).append(DATABASE)
                .append(COLUMN_DELIMITER).append(DATABASE_VERSION).append(COLUMN_DELIMITER).append(SEARCH_ENGINE).append(COLUMN_DELIMITER);
        // create best search engine score headers. Check if there is different search engine
        List<String> softwares = new ArrayList<>();
        int counter = 1;
        for (int i = 0; i < mzTabExport.getRuns().size(); i++) {
            String software = getQuantificationSettings(mzTabExport.getRuns().get(i)).getQuantificationEngine().getName();
            if (!softwares.contains(software)) {
                proteins.append(String.format(BEST_SEARCH_ENGINE_SCORE, counter)).append(COLUMN_DELIMITER);
                counter++;
                softwares.add(software);
            }
        }
        proteins.append(AMBIGUITY_MEMBERS).append(COLUMN_DELIMITER).append(MODIFICATIONS).append(COLUMN_DELIMITER).append(PROTEIN_COVERAGE).append(COLUMN_DELIMITER);

        for (int i = 0; i < mzTabExport.getStudyVariablesAssaysRefs().size(); i++) {
            proteins.append(String.format(PROTEIN_ABUNDANCE_STUDY_VARIABLE, i + 1)).append(COLUMN_DELIMITER);
            proteins.append(String.format(PROTEIN_ABUNDANCE_STDEV_STUDY_VARIABLE, i + 1)).append(COLUMN_DELIMITER);
            proteins.append(String.format(PROTEIN_ABUNDANCE_STD_ERROR_STUDY_VARIABLE, i + 1)).append(COLUMN_DELIMITER);
        }
        // add search engine score per run
        for (int i = 1; i < counter; i++) {
            for (int run = 0; run < mzTabExport.getRuns().size(); run++) {
                proteins.append(String.format(SEARCH_ENGINE_SCORE_MS_RUN, i, run + 1)).append(COLUMN_DELIMITER);
            }
        }
        for (int i = 0; i < mzTabExport.getRuns().size(); i++) {
            proteins.append(String.format(NUM_PSMS_MS_RUN, i + 1)).append(COLUMN_DELIMITER);
            proteins.append(String.format(NUM_PEPTIDES_DISTINCT_MS_RUN, i + 1)).append(COLUMN_DELIMITER);
            proteins.append(String.format(NUM_PEPTIDE_UNIQUE_MS_RUN, i + 1)).append(COLUMN_DELIMITER);
        }

        for (int i = 0; i < mzTabExport.getRuns().size(); i++) {
            for (int j = 0; j < mzTabExport.getAnalyticalRunsAssaysRefs().get(mzTabExport.getRuns().get(i)).length; j++) {
                proteins.append(String.format(PROTEIN_ABUNDANCE_ASSAY, mzTabExport.getAnalyticalRunsAssaysRefs().get(mzTabExport.getRuns().get(i))[j])).append(COLUMN_DELIMITER);
            }
        }
        proteins.append(System.lineSeparator());
        proteins.append(addProteinData());
        return proteins.toString();
    }

    /**
     * Fill protein section by adding data.
     *
     * @return proteins
     */
    private String addProteinData() throws IOException {
        StringBuilder proteins = new StringBuilder();
        List<Long> analyticalRunIds = new ArrayList<>();
        mzTabExport.getRuns().forEach(analyticalRun -> {
            analyticalRunIds.add(analyticalRun.getId());
        });
        List<ProteinGroupDTO> proteinList = getProteinGroupsForAnalyticalRuns(analyticalRunIds);
        SearchAndValidationSettings searchAndValidationSettings = searchAndValidationSettingsService.getbyAnalyticalRun(mzTabExport.getRuns().get(0));
        Map<FastaDb, FastaDbType> fastaDbs = fastaDbService.findBySearchAndValidationSettings(searchAndValidationSettings);
        // check if db is uniprot!
        for (int i = 0; i < proteinList.size(); i++) {
            // set prefix and accession
            proteins.append(PROTEINS_PREFIX).append(COLUMN_DELIMITER).append(proteinList.get(i).getMainAccession()).append(COLUMN_DELIMITER);
            // define uniprot map and uniprot accession list
            Map<String, String> uniProtMap = new HashMap<>();
            List<String> uniprotAccessions = new ArrayList<>();
            // we do not know the database and version.
            String database = "N/A";
            String databaseVersion = "N/A";
            for (FastaDb fastaDb : fastaDbs.keySet()) {
                if (fastaDbs.get(fastaDb).equals(FastaDbType.PRIMARY)) {
                    database = fastaDb.getDatabaseName();
                    databaseVersion = fastaDb.getVersion();
                    switch (fastaDb.getDatabaseName()) {
                        case "UNIPROT":
                            uniprotAccessions.add(proteinList.get(i).getMainAccession());
                            uniProtMap = uniProtService.getUniProtByAccession(uniprotAccessions.get(0));
                            break;
                        case "Not in the EMBL-EBI list":
                            break;
                        default:
                            uniprotAccessions = AccessionConverter.convertToUniProt(proteinList.get(i).getMainAccession(), fastaDb.getDatabaseName());
                            uniProtMap = uniProtService.getUniProtByAccession(uniprotAccessions.get(0));
                            break;
                    }
                }
            }
            // if uniprot map has values
            if (!uniProtMap.isEmpty()) {
                // set description, taxid, species, database, database version
                proteins.append(uniProtMap.get("description")).append(COLUMN_DELIMITER).append(uniProtMap.get("taxid")).append(COLUMN_DELIMITER).append(uniProtMap.get("species"))
                        .append(COLUMN_DELIMITER).append(database).append(COLUMN_DELIMITER).append(databaseVersion).append(COLUMN_DELIMITER);
            } else {
                for (FastaDb fastaDb : fastaDbs.keySet()) {
                    if (fastaDbs.get(fastaDb).equals(FastaDbType.ADDITIONAL)) {
                        database = fastaDb.getDatabaseName();
                        databaseVersion = fastaDb.getVersion();
                        switch (fastaDb.getDatabaseName()) {
                            case "UNIPROT":
                                uniprotAccessions.add(proteinList.get(i).getMainAccession());
                                uniProtMap = uniProtService.getUniProtByAccession(uniprotAccessions.get(0));
                                break;
                            case "Not in the EMBL-EBI list":
                                break;
                            default:
                                uniprotAccessions = AccessionConverter.convertToUniProt(proteinList.get(i).getMainAccession(), fastaDb.getDatabaseName());
                                uniProtMap = uniProtService.getUniProtByAccession(uniprotAccessions.get(0));
                                break;
                        }

                        if (!uniProtMap.isEmpty()) {
                            // set description, taxid, species, database, database version
                            proteins.append(uniProtMap.get("description")).append(COLUMN_DELIMITER).append(uniProtMap.get("taxid")).append(COLUMN_DELIMITER).append(uniProtMap.get("species"))
                                    .append(COLUMN_DELIMITER).append(COLUMN_DELIMITER).append(database).append(COLUMN_DELIMITER).append(databaseVersion).append(COLUMN_DELIMITER);
                            break;
                        }
                    }
                }
            }
            if (uniProtMap.isEmpty()) {
                // set description, taxid, species, database, database version
                proteins.append("N/A").append(COLUMN_DELIMITER).append("N/A").append(COLUMN_DELIMITER).append("N/A").append(COLUMN_DELIMITER).append("N/A")
                        .append(COLUMN_DELIMITER).append("N/A").append(COLUMN_DELIMITER);
            }

            // set search engine
            proteins.append(setSearchEngine());

            // set best search engine score. Check if there is different search engine
            List<String> softwares = new ArrayList<>();
            for (int j = 0; j < mzTabExport.getRuns().size(); j++) {
                String software = getQuantificationSettings(mzTabExport.getRuns().get(j)).getQuantificationEngine().getName();
                if (!softwares.contains(software)) {
                    proteins.append(proteinList.get(i).getProteinPostErrorProbability()).append(COLUMN_DELIMITER);
                    softwares.add(software);
                }
            }

            // set ambiguity members
            proteins.append(getAmbiguityMembers(proteinList.get(i).getId())).append(COLUMN_DELIMITER);

            // set modification (null, beacuse protein level modification is not reported)
            proteins.append("null").append(COLUMN_DELIMITER);

            // set protein coverage
            List<String> peptideSequences = new ArrayList<>();
            for (PeptideHasProteinGroup peptideHasProteinGroup : proteinGroupService.findById(proteinList.get(i).getId()).getPeptideHasProteinGroups()) {
                peptideSequences.add(peptideHasProteinGroup.getPeptide().getSequence());
            }
            proteins.append(ProteinCoverage.calculateProteinCoverage(proteinList.get(i).getMainSequence(), peptideSequences)).append(COLUMN_DELIMITER);

            //set protein abundance study variable,  protein abundance standard deviation study variable, protein abundance standard error study variable
            for (Map.Entry<String, int[]> studyVariablesAssaysRefs : mzTabExport.getStudyVariablesAssaysRefs().entrySet()) {
                double abundanceSum = 0.0;
                double[] abundanceArray = new double[studyVariablesAssaysRefs.getValue().length];
                double abundanceMean;
                double stdDev;
                for (int j = 0; j < studyVariablesAssaysRefs.getValue().length; j++) {
                    abundanceArray[j] = getProteinAbundanceForAssay(proteinList.get(i), String.format(ASSAY, studyVariablesAssaysRefs.getValue()[j]));
                    abundanceSum += abundanceArray[j];
                }
                if (studyVariablesAssaysRefs.getValue().length != 0) {
                    abundanceMean = abundanceSum / studyVariablesAssaysRefs.getValue().length;
                    // set protein abundance study variable
                    proteins.append(abundanceMean).append(COLUMN_DELIMITER);
                    abundanceSum = 0.0;
                    //find the square of each abundance's distance to the mean.
                    for (int arr = 0; arr < abundanceArray.length; arr++) {
                        abundanceArray[arr] = Math.pow((abundanceArray[arr] - abundanceMean), 2);
                        abundanceSum += abundanceArray[arr];
                    }
                    // calculate new abundance mean
                    abundanceMean = abundanceSum / abundanceArray.length;
                    stdDev = Math.sqrt(abundanceMean);
                    // set protein abundance standard deviation study variable
                    proteins.append(stdDev).append(COLUMN_DELIMITER);
                    // set protein abundance standard error study variable
                    proteins.append(stdDev / Math.sqrt(abundanceArray.length)).append(COLUMN_DELIMITER);
                } else {
                    proteins.append("null").append(COLUMN_DELIMITER).append("null").append(COLUMN_DELIMITER).append("null").append(COLUMN_DELIMITER);
                }
            }

            // add search engine score per run (both maxquant and peptideshaker do not provide search engine score per run)
            softwares.stream().forEach(software -> {
                mzTabExport.getRuns().stream().forEach(run -> {
                    proteins.append("null").append(COLUMN_DELIMITER);
                });
            });
            // set psms count, number of distinct peptide and unique peptide per run
            for (int j = 0; j < mzTabExport.getRuns().size(); j++) {
                List<Long> analyticalRunIdList = new ArrayList<>(1);
                analyticalRunIdList.add(mzTabExport.getRuns().get(j).getId());
                proteins.append(peptideService.getPeptideDTO(proteinList.get(i).getId(), analyticalRunIdList).size()).append(COLUMN_DELIMITER);
                proteins.append(peptideService.getDistinctPeptideSequence(proteinList.get(i).getId(), analyticalRunIdList).size()).append(COLUMN_DELIMITER);
                proteins.append(peptideService.getUniquePeptides(proteinList.get(i).getId(), analyticalRunIdList).size()).append(COLUMN_DELIMITER);
            }
            // set protein abundance for every assay
            for (int r = 0; r < mzTabExport.getRuns().size(); r++) {
                for (int j = 0; j < mzTabExport.getAnalyticalRunsAssaysRefs().get(mzTabExport.getRuns().get(r)).length; j++) {
                    proteins.append(getProteinAbundanceForAssay(proteinList.get(i), String.format(ASSAY, mzTabExport.getAnalyticalRunsAssaysRefs().get(mzTabExport.getRuns().get(r))[j]))).append(COLUMN_DELIMITER);
                }
            }
            proteins.append(System.lineSeparator());
        }
        return proteins.toString();
    }

    /**
     * This method parses the JSON root node and returns a list of MzTabParam
     * instances.
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
     *
     * @param field
     * @param alignment
     * @return
     */
    private String setSoftwareAndEngineScore(String field, int alignment) {
        StringBuilder metadata = new StringBuilder();
        List<String> softwares = new ArrayList<>();
        int counter = 1;
        for (int i = 0; i < mzTabExport.getRuns().size(); i++) {
            String software = getSearchAndValidationSettings(mzTabExport.getRuns().get(i)).getSearchEngine().getName();
            if (!softwares.contains(software)) {
                if (software.equals("PeptideShaker")) {
                    metadata.append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(String.format(field, counter)).append(COLUMN_DELIMITER)
                            .append(createOntology(mzTabParams.get(alignment).getMzTabParamOptions().get(1).getOntology(), mzTabParams.get(alignment).getMzTabParamOptions().get(1).getAccession(), mzTabParams.get(alignment).getMzTabParamOptions().get(1).getName())).append(System.lineSeparator());
                } else if (software.equals("MaxQuant")) {
                    metadata.append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(String.format(field, counter)).append(COLUMN_DELIMITER)
                            .append(createOntology(mzTabParams.get(alignment).getMzTabParamOptions().get(0).getOntology(), mzTabParams.get(alignment).getMzTabParamOptions().get(0).getAccession(), mzTabParams.get(alignment).getMzTabParamOptions().get(0).getName())).append(System.lineSeparator());
                }
                counter++;
                softwares.add(software);
            }
        }
        return metadata.toString();
    }

    /**
     * Set search engine.
     *
     * @param field
     * @param alignment
     * @return
     */
    private String setSearchEngine() {
        // TO DO working properly for search engine score??
        StringBuilder searchEngine = new StringBuilder();
        List<String> softwares = new ArrayList<>();
        for (int i = 0; i < mzTabExport.getRuns().size(); i++) {
            String software = getQuantificationSettings(mzTabExport.getRuns().get(i)).getQuantificationEngine().getName();
            if (!softwares.contains(software)) {
                if (software.equals("PeptideShaker")) {
                    searchEngine.append(createOntology(mzTabParams.get(SOFTWARE_ALIGNMENT).getMzTabParamOptions().get(1).getOntology(), mzTabParams.get(SOFTWARE_ALIGNMENT).getMzTabParamOptions().get(1).getAccession(), mzTabParams.get(SOFTWARE_ALIGNMENT).getMzTabParamOptions().get(1).getName())).append(VERTICAL_BAR);
                } else if (software.equals("MaxQuant")) {
                    searchEngine.append(createOntology(mzTabParams.get(SOFTWARE_ALIGNMENT).getMzTabParamOptions().get(0).getOntology(), mzTabParams.get(SOFTWARE_ALIGNMENT).getMzTabParamOptions().get(0).getAccession(), mzTabParams.get(SOFTWARE_ALIGNMENT).getMzTabParamOptions().get(0).getName())).append(VERTICAL_BAR);
                }
                softwares.add(software);
            }
        }
        searchEngine = searchEngine.deleteCharAt(searchEngine.length() - 1);

        searchEngine.append(COLUMN_DELIMITER);

        return searchEngine.toString();
    }

    /**
     * Create ontology list with the given variables.
     *
     * @param ontology
     * @param accession
     * @param name
     * @return ontology list string
     */
    private String createOntology(String ontology, String accession, String name) {
        StringBuilder ontologyBuilder = new StringBuilder();
        return ontologyBuilder.append(OPEN_BRACKET).append(ontology).append(COMMA_SEPARATOR).append(accession).append(COMMA_SEPARATOR)
                .append(name).append(COMMA_SEPARATOR).append(CLOSE_BRACKET).toString();
    }

    /**
     * Get the quantification settings
     *
     * @param analyticalRuns
     * @return quantificationSettingsMap
     */
    private QuantificationSettings getQuantificationSettings(AnalyticalRun analyticalRun) {
        //return quantificationSettingsService.getbyAnalyticalRun(analyticalRun);
        return analyticalRun.getQuantificationSettings();
    }

    /**
     * Get the search and validation settings
     *
     * @param analyticalRuns
     * @return quantificationSettingsMap
     */
    private SearchAndValidationSettings getSearchAndValidationSettings(AnalyticalRun analyticalRun) {
        //return quantificationSettingsService.getbyAnalyticalRun(analyticalRun);
        return analyticalRun.getSearchAndValidationSettings();
    }

    /**
     * Get protein groups for given list of analyticalRuns
     *
     * @param analyticalRunIds
     * @return list of ProteinGroupDTO object
     */
    private List<ProteinGroupDTO> getProteinGroupsForAnalyticalRuns(List<Long> analyticalRunIds) {
        return proteinGroupService.getProteinGroupsForRuns(analyticalRunIds);
    }

    /**
     * Get ambiguity members for given proteinGroupID
     *
     * @param proteinGroupId
     * @return ambiguity members as string
     */
    private String getAmbiguityMembers(Long proteinGroupId) {
        List<ProteinGroupHasProtein> proteinGroupHasProteins = proteinGroupService.getAmbiguityMembers(proteinGroupId);

        StringBuilder ambiguityMembers = new StringBuilder("");
        proteinGroupHasProteins.stream().forEach((proteinGroupHasProtein) -> {
            ambiguityMembers.append(proteinGroupHasProtein.getProteinAccession()).append(",");
        });
        if (ambiguityMembers.toString().equals("")) {
            return ambiguityMembers.toString();
        } else {
            return ambiguityMembers.deleteCharAt(ambiguityMembers.length() - 1).toString();
        }
    }

    /**
     * Get protein abundance for given assay, protein group and analyticalRun
     * Use assay to get the reagent.
     *
     * @param analyticalRun
     * @param proteinGroup
     * @param assay
     * @return protein abundance
     */
    private double getProteinAbundanceForAssay(ProteinGroupDTO proteinGroup, String assay) {
        double proteinAbundance = 0.0;
        AnalyticalRun analyticalRun = assayAnalyticalRunRef.get(assay);
        if (assayReagentRef.get(assay).equals(UNLABELED_SAMPLE)) {
            proteinAbundance = proteinGroupQuantService.getProteinGroupQuantForRunAndProteinGroup(analyticalRun.getId(), proteinGroup.getId()).getIntensity();
        } else {
            // get label from user interface.
            String label = mzTabExport.getQuantificationReagentLabelMatch().get(assayReagentRef.get(assay));
            List<ProteinGroupQuantLabeled> proteinGroupQuantLabeleds = proteinGroupQuantLabeledService.getProteinGroupQuantLabeledForRunAndProteinGroup(analyticalRun.getId(), proteinGroup.getId());
            for (ProteinGroupQuantLabeled proteinGroupQuantLabeled : proteinGroupQuantLabeleds) {
                if (proteinGroupQuantLabeled.getLabel().equals(label)) {
                    proteinAbundance = proteinGroupQuantLabeled.getLabelValue();
                }
            }
        }
        return proteinAbundance;
    }
}

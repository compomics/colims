/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.io.mztab;

import com.compomics.colims.core.io.fasta.FastaDbParser;
import com.compomics.colims.core.service.*;
import com.compomics.colims.core.util.PeptidePosition;
import com.compomics.colims.core.util.SequenceUtils;
import com.compomics.colims.core.util.UniprotProteinUtils;
import com.compomics.colims.model.*;
import com.compomics.colims.model.enums.FastaDbType;
import com.compomics.colims.model.enums.ModificationType;
import com.compomics.colims.model.enums.SearchEngineType;
import com.compomics.colims.repository.hibernate.ProteinGroupDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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
    private static final String PSM_HEADER_PREFIX = "PSH";
    private static final String PSM_PREFIX = "PSM";
    private static final String OPEN_BRACKET = "[";
    private static final String CLOSE_BRACKET = "]";
    private static final String COMMA_SEPARATOR = ", ";
    private static final String VERTICAL_BAR = "|";
    private static final String UNLABELED_SAMPLE = "unlabeled sample";
    /**
     * Metadata section.
     */
    private static final String MZTAB_VERSION = "mzTab-version";
    private static final String VERSION = "1.0.0";
    private static final String MZTAB_MODE = "mzTab-mode";
    private static final String MODE_SUMMARY = "Summary";
    private static final String MZTAB_COMPLETE = "Complete";
    private static final String MZTAB_TYPE = "mzTab-type";
    private static final String MZTAB_ID = "mzTab_ID";
    private static final String DESCRIPTION = "description";
    private static final String RUN_LOCATION = "ms_run[%d]-location";
    private static final String RUN_LOCATION_DESCRIPTION = "file://";
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
    private static final String PROTEIN_QUANTIFICATION_UNIT = "protein-quantification_unit";
    private static final String PEPTIDE_QUANTIFICATION_UNIT = "peptide-quantification_unit";
    private static final String SMALL_MOLECULE_QUANTIFICATION_UNIT = "smallmolecule-quantification_unit";
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
     * mztab Json mapping.
     */
    private static final int PROTEIN_SEARCH_ENGINE_SCORE_ALIGNMENT = 0;
    private static final int PEPTIDE_SEARCH_ENGINE_SCORE_ALIGNMENT = 1;
    private static final int PSM_SEARCH_ENGINE_SCORE_ALIGNMENT = 2;
    private static final int SOFTWARE_ALIGNMENT = 4;
    /**
     * Common headers for all sections.
     */
    private static final String ACCESSION = "accession";
    private static final String DATABASE = "database";
    private static final String DATABASE_VERSION = "database_version";
    private static final String SEARCH_ENGINE = "search_engine";
    private static final String MODIFICATIONS = "modifications";
    /**
     * Protein section.
     */
    private static final String TAXID = "taxid";
    private static final String SPECIES = "species";
    private static final String BEST_SEARCH_ENGINE_SCORE = "best_search_engine_score[%d]";
    private static final String AMBIGUITY_MEMBERS = "ambiguity_members";
    private static final String PROTEIN_COVERAGE = "protein_coverage";
    private static final String PROTEIN_ABUNDANCE_STUDY_VARIABLE = "protein_abundance_study_variable[%d]";
    private static final String PROTEIN_ABUNDANCE_STDEV_STUDY_VARIABLE = "protein_abundance_stdev_study_variable[%d]";
    private static final String PROTEIN_ABUNDANCE_STD_ERROR_STUDY_VARIABLE = "protein_abundance_std_error_study_variable[%d]";
    private static final String SEARCH_ENGINE_SCORE_MS_RUN = "search_engine_score[%d]_ms_run[%d]";
    private static final String NUM_PSMS_MS_RUN = "num_psms_ms_run[%d]";
    private static final String NUM_PEPTIDES_DISTINCT_MS_RUN = "num_peptides_distinct_ms_run[%d]";
    private static final String NUM_PEPTIDE_UNIQUE_MS_RUN = "num_peptides_unique_ms_run[%d]";
    private static final String PROTEIN_ABUNDANCE_ASSAY = "protein_abundance_assay[%d]";
    private static final String CONTAMINANT_PREFIX = "CON";
    private static final String CONTAMINANT_LONG_PREFIX = "CON__";
    /**
     * PSM section.
     */
    private static final String SEQUENCE = "sequence";
    private static final String PSM_ID = "PSM_ID";
    private static final String UNIQUE = "unique";
    private static final String SEARCH_ENGINE_SCORE = "search_engine_score[%d]";
    private static final String RETENTION_TIME = "retention_time";
    private static final String CHARGE = "charge";
    private static final String EXPERIMENTAL_MASS_TO_CHARGE = "exp_mass_to_charge";
    private static final String CALCULATED_MASS_TO_CHARGE = "calc_mass_to_charge";
    private static final String SPECTRA_REFERENCE = "spectra_ref";
    private static final String PRE = "pre";
    private static final String POST = "post";
    private static final String START = "start";
    private static final String END = "end";
    /**
     * JSON object mapper.
     */
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
    /**
     * analyticalRunIndexRef (key : analyticalRunId, value : index)
     */
    private final Map<Long, Integer> analyticalRunIndexRef = new HashMap<>();
    /**
     * Software name.
     */
    private String software;
    /**
     * parsed FASTA files (key : fastaDb, value : set of accessions)
     */
    Map<FastaDb, Set<String>> parsedFastas = new HashMap<>();
    /**
     * FASTA files with type (key : fastaDb, value : FastaDbType)
     */
    Map<FastaDb, FastaDbType> fastaDbs = new HashMap<>();

    private final ProteinGroupService proteinGroupService;
    private final PeptideService peptideService;
    private final ProteinGroupQuantService proteinGroupQuantService;
    private final SearchAndValidationSettingsService searchAndValidationSettingsService;
    private final FastaDbService fastaDbService;
    private final UniProtService uniProtService;
    private final FastaDbParser fastaDbParser;
    private final UniprotProteinUtils uniprotProteinUtils;
    private final QuantificationMethodService quantificationMethodService;
    /**
     * The MzTabExport instance.
     */
    private MzTabExport mzTabExport;

    @Autowired
    public MzTabExporter(ProteinGroupService proteinGroupService, SearchAndValidationSettingsService searchAndValidationSettingsService,
            FastaDbService fastaDbService, UniProtService uniProtService, ProteinGroupQuantService proteinGroupQuantService, PeptideService peptideService, FastaDbParser fastaDbParser,
            UniprotProteinUtils uniprotProteinUtils, QuantificationMethodService quantificationMethodService) {
        this.proteinGroupService = proteinGroupService;
        this.searchAndValidationSettingsService = searchAndValidationSettingsService;
        this.fastaDbService = fastaDbService;
        this.uniProtService = uniProtService;
        this.proteinGroupQuantService = proteinGroupQuantService;
        this.peptideService = peptideService;
        this.fastaDbParser = fastaDbParser;
        this.uniprotProteinUtils = uniprotProteinUtils;
        this.quantificationMethodService = quantificationMethodService;
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
     * @throws java.io.IOException
     */
    public void export(MzTabExport mzTabExport) throws IOException {
        this.mzTabExport = mzTabExport;

        try (FileOutputStream fos = new FileOutputStream(new File(mzTabExport.getExportDirectory(), mzTabExport.getFileName() + MZTAB_EXTENSION));
                OutputStreamWriter osw = new OutputStreamWriter(fos, Charset.forName("UTF-8").newEncoder());
                BufferedWriter bw = new BufferedWriter(osw);
                PrintWriter pw = new PrintWriter(bw)) {

            switch (mzTabExport.getMzTabType()) {
                case QUANTIFICATION:
                    switch (mzTabExport.getMzTabMode()) {
                        case SUMMARY:
                            constructMetadata(1, pw);
                            break;
                        case COMPLETE:
                            constructMetadata(2, pw);
                            break;
                        default:
                            break;
                    }
                    break;
                case IDENTIFICATION:
                    switch (mzTabExport.getMzTabMode()) {
                        case SUMMARY:
                            constructMetadata(3, pw);
                            break;
                        case COMPLETE:
                            constructMetadata(4, pw);
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
            pw.flush();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new IOException(e.getMessage());
        }
    }

    private void constructMetadata(int type, PrintWriter pw) throws IOException {
        //version, type, mode and description
        pw.println(new StringBuilder().append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(MZTAB_VERSION).append(COLUMN_DELIMITER).append(VERSION));
        pw.println(new StringBuilder().append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(MZTAB_MODE).append(COLUMN_DELIMITER).append(mzTabExport.getMzTabMode().mzTabName()));
        pw.println(new StringBuilder().append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(MZTAB_TYPE).append(COLUMN_DELIMITER).append(mzTabExport.getMzTabType().mzTabName()));
        pw.println(new StringBuilder().append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(DESCRIPTION).append(COLUMN_DELIMITER).append(mzTabExport.getDescription()));
        //run locations
        for (int i = 0; i < mzTabExport.getRuns().size(); i++) {
            analyticalRunIndexRef.put(mzTabExport.getRuns().get(i).getId(), i + 1);
            pw.println(new StringBuilder().append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(String.format(RUN_LOCATION, i + 1)).append(COLUMN_DELIMITER).append(RUN_LOCATION_DESCRIPTION).append(mzTabExport.getRuns().get(i).getStorageLocation()).append("\\").append(mzTabExport.getRuns().get(i).getName()));
        }
        if (type == 1 || type == 2) {
            //protein quantification unit (relative quantification unit from mztab.json)
            pw.println(new StringBuilder().append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(PROTEIN_QUANTIFICATION_UNIT).append(COLUMN_DELIMITER).append(
                    createOntology(mzTabParams.get(3).getMzTabParamOptions().get(1).getOntology(), mzTabParams.get(3).getMzTabParamOptions().get(1).getAccession(), mzTabParams.get(3).getMzTabParamOptions().get(1).getName())));

            //peptide quantification unit (relative quantification unit from mztab.json)(same with protein quant unit)
            pw.println(new StringBuilder().append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(PEPTIDE_QUANTIFICATION_UNIT).append(COLUMN_DELIMITER).append(
                    createOntology(mzTabParams.get(3).getMzTabParamOptions().get(1).getOntology(), mzTabParams.get(3).getMzTabParamOptions().get(1).getAccession(), mzTabParams.get(3).getMzTabParamOptions().get(1).getName())));

        }

        //set softwares
        setSoftware();

        //software
        if (type == 2 || type == 4) {
            pw.println(new StringBuilder().append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(String.format(SOFTWARE, 1)).append(COLUMN_DELIMITER).append(getSearchEngine()).append(COLUMN_DELIMITER));
        }

        // protein search engine score
        pw.println(new StringBuilder().append(getEngineScore(PROTEIN_SEARCH_ENGINE_SCORE, PROTEIN_SEARCH_ENGINE_SCORE_ALIGNMENT)));

        // peptide search engine score
        pw.println(new StringBuilder().append(getEngineScore(PEPTIDE_SEARCH_ENGINE_SCORE, PEPTIDE_SEARCH_ENGINE_SCORE_ALIGNMENT)));

        // psm search engine score
        pw.println(new StringBuilder().append(getEngineScore(PSM_SEARCH_ENGINE_SCORE, PSM_SEARCH_ENGINE_SCORE_ALIGNMENT)));

        // fixed modifications
        int counter = 1;
        List<String> modifications = new ArrayList<>();
        for (int i = 0; i < mzTabExport.getRuns().size(); i++) {
            for (SearchParametersHasModification searchParametersHasModification : getSearchAndValidationSettings(mzTabExport.getRuns().get(i)).getSearchParameters().getSearchParametersHasModifications()) {
                if (searchParametersHasModification.getModificationType().equals(ModificationType.FIXED) && searchParametersHasModification.getSearchModification().getAccession() != null
                        && !modifications.contains(searchParametersHasModification.getSearchModification().getName())) {
                    modifications.add(searchParametersHasModification.getSearchModification().getName());
                    pw.println(new StringBuilder().append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(String.format(FIXED_MOD, counter)).append(COLUMN_DELIMITER)
                            .append(createOntology(StringUtils.substringBefore(searchParametersHasModification.getSearchModification().getAccession(), ":"), searchParametersHasModification.getSearchModification().getAccession(), searchParametersHasModification.getSearchModification().getName())));
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
                    pw.println(new StringBuilder().append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(String.format(VARIABLE_MOD, counter)).append(COLUMN_DELIMITER)
                            .append(createOntology(StringUtils.substringBefore(searchParametersHasModification.getSearchModification().getAccession(), ":"), searchParametersHasModification.getSearchModification().getAccession(), searchParametersHasModification.getSearchModification().getName())));
                    counter++;
                }
            }
        }

        // quantification method
        if (type == 2) {
            pw.println(new StringBuilder().append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(QUANTIFICATION_METHOD).append(COLUMN_DELIMITER).append(
                    createOntology(StringUtils.substringBefore(getQuantificationSettings(mzTabExport.getRuns().get(0)).getQuantificationMethod().getAccession(), ":"),
                            getQuantificationSettings(mzTabExport.getRuns().get(0)).getQuantificationMethod().getAccession(), getQuantificationSettings(mzTabExport.getRuns().get(0)).getQuantificationMethod().getName())));
        }

        //assay quantification reagents
        if (type == 1 || type == 2) {
            for (int i = 0; i < mzTabExport.getRuns().size(); i++) {
                List<QuantificationMethodHasReagent> quantificationMethodHasReagents = quantificationMethodService.fetchQuantificationMethodHasReagents(
                        getQuantificationSettings(mzTabExport.getRuns().get(i)).getQuantificationMethod());
                counter = 0;
                // label free
                if (quantificationMethodHasReagents.isEmpty()) {
                    int assayNumber = mzTabExport.getAnalyticalRunsAssaysRefs().get(mzTabExport.getRuns().get(i))[counter];
                    pw.println(new StringBuilder().append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(String.format(ASSAY_QUANTIFICATION_REAGENT, assayNumber)).append(COLUMN_DELIMITER).
                            append(createOntology(mzTabParams.get(5).getMzTabParamOptions().get(0).getOntology(), mzTabParams.get(5).getMzTabParamOptions().get(0).getAccession(), mzTabParams.get(5).getMzTabParamOptions().get(0).getName())));
                    assayReagentRef.put(String.format(ASSAY, assayNumber), mzTabParams.get(5).getMzTabParamOptions().get(0).getName());
                }
                // labeled
                for (QuantificationMethodHasReagent quantificationMethodHasReagent : quantificationMethodHasReagents) {
                    int assayNumber = mzTabExport.getAnalyticalRunsAssaysRefs().get(mzTabExport.getRuns().get(i))[counter];
                    pw.println(new StringBuilder().append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(String.format(ASSAY_QUANTIFICATION_REAGENT, assayNumber)).append(COLUMN_DELIMITER).
                            append(createOntology(StringUtils.substringBefore(quantificationMethodHasReagent.getQuantificationReagent().getAccession(), ":"), quantificationMethodHasReagent.getQuantificationReagent().getAccession(), quantificationMethodHasReagent.getQuantificationReagent().getName())));
                    assayReagentRef.put(String.format(ASSAY, assayNumber), quantificationMethodHasReagent.getQuantificationReagent().getName());
                    counter++;
                }
                mzTabExport.getAnalyticalRunsAssaysRefs().get(mzTabExport.getRuns().get(i));
            }
        }

        // assay ms run reference
        for (int i = 0; i < mzTabExport.getRuns().size(); i++) {
            if (mzTabExport.getRuns().get(i).getSearchAndValidationSettings().getSearchEngine().getSearchEngineType().equals(SearchEngineType.MAXQUANT)) {
                List<QuantificationMethodHasReagent> quantificationMethodHasReagents = quantificationMethodService.fetchQuantificationMethodHasReagents(
                        getQuantificationSettings(mzTabExport.getRuns().get(i)).getQuantificationMethod());
                // label free
                if (quantificationMethodHasReagents.isEmpty()) {
                    int assayNumber = mzTabExport.getAnalyticalRunsAssaysRefs().get(mzTabExport.getRuns().get(i))[0];
                    pw.println(new StringBuilder().append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(String.format(ASSAY_RUN_REF, assayNumber)).append(COLUMN_DELIMITER).
                            append(String.format(MS_RUN_REF, i + 1)));
                    assayAnalyticalRunRef.put(String.format(ASSAY, assayNumber), mzTabExport.getRuns().get(i));
                }
                // labeled
                for (int j = 0; j < quantificationMethodHasReagents.size(); j++) {
                    int assayNumber = mzTabExport.getAnalyticalRunsAssaysRefs().get(mzTabExport.getRuns().get(i))[j];
                    pw.println(new StringBuilder().append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(String.format(ASSAY_RUN_REF, assayNumber)).append(COLUMN_DELIMITER).
                            append(String.format(MS_RUN_REF, i + 1)));
                    assayAnalyticalRunRef.put(String.format(ASSAY, assayNumber), mzTabExport.getRuns().get(i));
                }
            } else {

                int assayNumber = 1;
                pw.println(new StringBuilder().append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(String.format(ASSAY_RUN_REF, assayNumber)).append(COLUMN_DELIMITER).
                        append(String.format(MS_RUN_REF, i + 1)));
                assayAnalyticalRunRef.put(String.format(ASSAY, assayNumber), mzTabExport.getRuns().get(i));
            }

        }

        // study variable assay references
        if (type == 1 || type == 2) {
            counter = 1;
            for (Map.Entry<String, int[]> studyVariablesAssaysRefs : mzTabExport.getStudyVariablesAssaysRefs().entrySet()) {
                String[] assay = new String[studyVariablesAssaysRefs.getValue().length];
                for (int i = 0; i < studyVariablesAssaysRefs.getValue().length; i++) {
                    assay[i] = String.format(ASSAY, studyVariablesAssaysRefs.getValue()[i]);
                }
                String assays = StringUtils.join(assay, ',');
                pw.println(new StringBuilder().append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(String.format(STUDY_VARIABLE_ASSAY_REFS, counter)).append(COLUMN_DELIMITER).
                        append(assays));
                counter++;
            }
        }

        // study variable description
        counter = 1;
        for (Map.Entry<String, int[]> studyVariablesAssaysRefs : mzTabExport.getStudyVariablesAssaysRefs().entrySet()) {
            pw.println(new StringBuilder().append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(String.format(STUDY_VARIABLE_DESCRIPTION, counter)).append(COLUMN_DELIMITER).
                    append(studyVariablesAssaysRefs.getKey()));
            counter++;
        }

        constructProteins(type, pw);
        constructPSM(pw);

    }

    private void constructProteins(int type, PrintWriter pw) throws IOException {
        StringBuilder proteins = new StringBuilder();
        // protein headers
        proteins.append(PROTEINS_HEADER_PREFIX).append(COLUMN_DELIMITER).append(ACCESSION).append(COLUMN_DELIMITER).append(DESCRIPTION)
                .append(COLUMN_DELIMITER).append(TAXID).append(COLUMN_DELIMITER).append(SPECIES).append(COLUMN_DELIMITER).append(DATABASE)
                .append(COLUMN_DELIMITER).append(DATABASE_VERSION).append(COLUMN_DELIMITER).append(SEARCH_ENGINE).append(COLUMN_DELIMITER);
        // create best search engine score headers. Check if there is different search engine

        proteins.append(String.format(BEST_SEARCH_ENGINE_SCORE, 1)).append(COLUMN_DELIMITER);

        proteins.append(AMBIGUITY_MEMBERS).append(COLUMN_DELIMITER).append(MODIFICATIONS).append(COLUMN_DELIMITER);

        if (type == 2 || type == 4) {
            proteins.append(PROTEIN_COVERAGE).append(COLUMN_DELIMITER);
        }

        if (type == 1 || type == 2) {
            for (int i = 0; i < mzTabExport.getStudyVariablesAssaysRefs().size(); i++) {
                proteins.append(String.format(PROTEIN_ABUNDANCE_STUDY_VARIABLE, i + 1)).append(COLUMN_DELIMITER);
                proteins.append(String.format(PROTEIN_ABUNDANCE_STDEV_STUDY_VARIABLE, i + 1)).append(COLUMN_DELIMITER);
                proteins.append(String.format(PROTEIN_ABUNDANCE_STD_ERROR_STUDY_VARIABLE, i + 1)).append(COLUMN_DELIMITER);
            }
        }

        // add search engine score per run
        if (type == 2 || type == 4) {
            for (int run = 0; run < mzTabExport.getRuns().size(); run++) {
                proteins.append(String.format(SEARCH_ENGINE_SCORE_MS_RUN, 1, run + 1)).append(COLUMN_DELIMITER);
            }
        }
        if (type == 4) {
            for (int i = 0; i < mzTabExport.getRuns().size(); i++) {
                proteins.append(String.format(NUM_PSMS_MS_RUN, i + 1)).append(COLUMN_DELIMITER);
                proteins.append(String.format(NUM_PEPTIDES_DISTINCT_MS_RUN, i + 1)).append(COLUMN_DELIMITER);
                proteins.append(String.format(NUM_PEPTIDE_UNIQUE_MS_RUN, i + 1)).append(COLUMN_DELIMITER);
            }
        }
        if (type == 1 || type == 2) {
            for (int i = 0; i < mzTabExport.getRuns().size(); i++) {
                for (int j = 0; j < mzTabExport.getAnalyticalRunsAssaysRefs().get(mzTabExport.getRuns().get(i)).length; j++) {
                    proteins.append(String.format(PROTEIN_ABUNDANCE_ASSAY, mzTabExport.getAnalyticalRunsAssaysRefs().get(mzTabExport.getRuns().get(i))[j])).append(COLUMN_DELIMITER);
                }
            }
        }

        pw.println(proteins.toString());
        addProteinData(type, pw);
    }

    /**
     * Fill protein section by adding data.
     *
     * @return proteins
     */
    private void addProteinData(int type, PrintWriter pw) throws IOException {
        List<Long> analyticalRunIds = new ArrayList<>();
        mzTabExport.getRuns().forEach(analyticalRun -> analyticalRunIds.add(analyticalRun.getId()));
        List<ProteinGroupDTO> proteinList = getProteinGroupsForAnalyticalRuns(analyticalRunIds);
        List<String> accessions = new ArrayList<>();
        //parse fasta files
        parseFastaFiles();
        for (ProteinGroupDTO aProteinList : proteinList) {
            if (!accessions.contains(aProteinList.getMainAccession())) {
                StringBuilder proteins = new StringBuilder();
                // set prefix and accession
                proteins.append(PROTEINS_PREFIX).append(COLUMN_DELIMITER).append(aProteinList.getMainAccession()).append(COLUMN_DELIMITER);

                // we do not know which fasta file protein sequence comes from.
                FastaDb fastaFile = findFastaDb(aProteinList.getMainAccession());

                //(key: information type; value: protein sequence and functional information)
                // no need to contaminant prefix because accession converter handles the prefix.
                Map<String, String> uniProtMap = uniprotProteinUtils.getFastaDbUniprotInformation(aProteinList.getMainAccession(), fastaFile);

                // if uniprot map has values
                if (!uniProtMap.isEmpty()) {
                    // set description, taxid, species, database, database version
                    proteins.append(uniProtMap.get("description")).append(COLUMN_DELIMITER).append(uniProtMap.get("taxid")).append(COLUMN_DELIMITER).append(uniProtMap.get("species"))
                            .append(COLUMN_DELIMITER).append(fastaFile.getDatabaseName()).append(COLUMN_DELIMITER).append(fastaFile.getVersion()).append(COLUMN_DELIMITER);
                } else {
                    // set N/A (for description, taxid, species) , database, database version
                    proteins.append("N/A").append(COLUMN_DELIMITER).append("N/A").append(COLUMN_DELIMITER).append("N/A").append(COLUMN_DELIMITER).append(fastaFile.getDatabaseName())
                            .append(COLUMN_DELIMITER).append(fastaFile.getVersion()).append(COLUMN_DELIMITER);
                }

                // set search engine
                proteins.append(getSearchEngine()).append(COLUMN_DELIMITER);

                // set best search engine score for protein.
                proteins.append(aProteinList.getProteinPostErrorProbability()).append(COLUMN_DELIMITER);

                // set ambiguity members
                proteins.append(getAmbiguityMembers(aProteinList.getId())).append(COLUMN_DELIMITER);

                // set modification (null, beacuse protein level modification is not reported)
                proteins.append("null").append(COLUMN_DELIMITER);

                // set protein coverage
                if (type == 2 || type == 4) {
                    Set<String> peptideSequences = new HashSet<>();
                    for (PeptideHasProteinGroup peptideHasProteinGroup : proteinGroupService.findById(aProteinList.getId()).getPeptideHasProteinGroups()) {
                        peptideSequences.add(peptideHasProteinGroup.getPeptide().getSequence());
                    }
                    proteins.append(SequenceUtils.calculateProteinCoverage(aProteinList.getMainSequence(), peptideSequences)).append(COLUMN_DELIMITER);

                }

                //set protein abundance study variable,  protein abundance standard deviation study variable, protein abundance standard error study variable
                if (type == 1 || type == 2) {
                    for (Map.Entry<String, int[]> studyVariablesAssaysRefs : mzTabExport.getStudyVariablesAssaysRefs().entrySet()) {
                        double abundanceSum = 0.0;
                        double[] abundanceArray = new double[studyVariablesAssaysRefs.getValue().length];
                        double abundanceMean;
                        double stdDev;
                        for (int j = 0; j < studyVariablesAssaysRefs.getValue().length; j++) {
                            abundanceArray[j] = getProteinAbundanceForAssay(aProteinList, String.format(ASSAY, studyVariablesAssaysRefs.getValue()[j]));
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
                }

                // add search engine score per run (both maxquant and peptideshaker do not provide search engine score per run)
                if (type == 2 || type == 4) {
                    mzTabExport.getRuns().stream().forEach(run -> {
                        proteins.append("null").append(COLUMN_DELIMITER);
                    });
                }

                // set psms count, number of distinct peptide and unique peptide per run
                if (type == 4) {
                    for (int j = 0; j < mzTabExport.getRuns().size(); j++) {
                        List<Long> analyticalRunIdList = new ArrayList<>(1);
                        analyticalRunIdList.add(mzTabExport.getRuns().get(j).getId());
                        proteins.append(peptideService.getPeptideDTOs(aProteinList.getId(), analyticalRunIdList).size()).append(COLUMN_DELIMITER);
                        proteins.append(peptideService.getDistinctPeptideSequence(aProteinList.getId(), analyticalRunIdList).size()).append(COLUMN_DELIMITER);
                        proteins.append(peptideService.getUniquePeptides(aProteinList.getId(), analyticalRunIdList).size()).append(COLUMN_DELIMITER);
                    }
                }

                // set the protein abundance for every assay
                if (type == 1 || type == 2) {
                    for (int r = 0; r < mzTabExport.getRuns().size(); r++) {
                        for (int j = 0; j < mzTabExport.getAnalyticalRunsAssaysRefs().get(mzTabExport.getRuns().get(r)).length; j++) {
                            proteins.append(getProteinAbundanceForAssay(aProteinList, String.format(ASSAY, mzTabExport.getAnalyticalRunsAssaysRefs().get(mzTabExport.getRuns().get(r))[j]))).append(COLUMN_DELIMITER);
                        }
                    }
                }

                pw.println(proteins);
                accessions.add(aProteinList.getMainAccession());
            }
        }
    }

    private void constructPSM(PrintWriter pw) throws IOException {
        StringBuilder psms = new StringBuilder();

        psms.append(PSM_HEADER_PREFIX).append(COLUMN_DELIMITER).append(SEQUENCE).append(COLUMN_DELIMITER).append(PSM_ID).append(COLUMN_DELIMITER).append(ACCESSION)
                .append(COLUMN_DELIMITER).append(UNIQUE).append(COLUMN_DELIMITER).append(DATABASE).append(COLUMN_DELIMITER).append(DATABASE_VERSION)
                .append(COLUMN_DELIMITER).append(SEARCH_ENGINE).append(COLUMN_DELIMITER).append(String.format(SEARCH_ENGINE_SCORE, 1)).append(COLUMN_DELIMITER)
                .append(MODIFICATIONS).append(COLUMN_DELIMITER).append(RETENTION_TIME).append(COLUMN_DELIMITER).append(CHARGE).append(COLUMN_DELIMITER)
                .append(EXPERIMENTAL_MASS_TO_CHARGE).append(COLUMN_DELIMITER).append(CALCULATED_MASS_TO_CHARGE).append(COLUMN_DELIMITER).append(SPECTRA_REFERENCE)
                .append(COLUMN_DELIMITER).append(PRE).append(COLUMN_DELIMITER).append(POST).append(COLUMN_DELIMITER).append(START).append(COLUMN_DELIMITER).append(END);
        pw.println(psms);
        addPSMData(pw);
    }

    /**
     * Fill PSM section by adding data.
     *
     * @return proteins
     */
    private void addPSMData(PrintWriter pw) throws IOException {
        List<Long> analyticalRunIds = new ArrayList<>();
        mzTabExport.getRuns().forEach(analyticalRun -> analyticalRunIds.add(analyticalRun.getId()));
        Map<PeptideHasProteinGroup, AnalyticalRun> peptideHasProteinGroups = getPeptideHasProteinGroupForAnalyticalRuns(analyticalRunIds);
        for (Map.Entry<PeptideHasProteinGroup, AnalyticalRun> peptideHasProteinGroup : peptideHasProteinGroups.entrySet()) {
            StringBuilder psms = new StringBuilder();
            ProteinGroupHasProtein mainProteinGroupHasProtein = proteinGroupService.getMainProteinGroupHasProtein(peptideHasProteinGroup.getKey().getProteinGroup().getId());

            psms.append(PSM_PREFIX).append(COLUMN_DELIMITER).append(peptideHasProteinGroup.getKey().getPeptide().getSequence()).append(COLUMN_DELIMITER).append(peptideHasProteinGroup.getKey().getPeptide().getId())
                    .append(COLUMN_DELIMITER).append(mainProteinGroupHasProtein.getProteinAccession())
                    .append(COLUMN_DELIMITER);

            // if peptide was seen more than once in peptideHasProteinGroups list, it is not unique
            if (isPeptideUnique(peptideHasProteinGroups, peptideHasProteinGroup.getKey().getPeptide())) {
                psms.append("1").append(COLUMN_DELIMITER);
            } else {
                psms.append("0").append(COLUMN_DELIMITER);
            }

            FastaDb fastaFile = findFastaDb(mainProteinGroupHasProtein.getProteinAccession());

            // database and version
            psms.append(fastaFile.getDatabaseName()).append(COLUMN_DELIMITER).append(fastaFile.getVersion()).append(COLUMN_DELIMITER);

            // search engine
            psms.append(getSearchEngine()).append(COLUMN_DELIMITER);

            // search engine score
            psms.append(peptideHasProteinGroup.getKey().getPeptide().getPsmProbability()).append(COLUMN_DELIMITER);

            // modifications 
            psms.append(getModifications(peptideHasProteinGroup.getKey().getPeptide())).append(COLUMN_DELIMITER);

            //retention time
            psms.append(peptideHasProteinGroup.getKey().getPeptide().getSpectrum().getRetentionTime()).append(COLUMN_DELIMITER);

            //charge
            psms.append(peptideHasProteinGroup.getKey().getPeptide().getCharge()).append(COLUMN_DELIMITER);

            // experimental mass to charge
            psms.append(peptideHasProteinGroup.getKey().getPeptide().getSpectrum().getMzRatio()).append(COLUMN_DELIMITER);

            // calculated mass to charge
            psms.append(peptideHasProteinGroup.getKey().getPeptide().getTheoreticalMass()).append(COLUMN_DELIMITER);

            // spectra reference
            psms.append(createSpectraRef(peptideHasProteinGroup.getKey().getPeptide().getSpectrum(), peptideHasProteinGroup.getValue())).append(COLUMN_DELIMITER);

            List<PeptidePosition> peptidePositions = SequenceUtils.getPeptidePositions(mainProteinGroupHasProtein.getProtein().getSequence(), peptideHasProteinGroup.getKey().getPeptide().getSequence());
            // preceding amino acid (pre)
            psms.append(peptidePositions.get(0).getPreAA()).append(COLUMN_DELIMITER);

            // following amino acid (post)
            psms.append(peptidePositions.get(0).getPostAA()).append(COLUMN_DELIMITER);

            // start position of the peptide
            psms.append(peptidePositions.get(0).getStartPosition()).append(COLUMN_DELIMITER);

            // end position of the peptide
            psms.append(peptidePositions.get(0).getEndPosition()).append(COLUMN_DELIMITER);

            pw.println(psms);
        }
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
     * Get engine scores of the meta data section.
     *
     * @param field
     * @param alignment
     * @return
     */
    private String getEngineScore(String field, int alignment) {
        // TO DO working properly for search engine score??
        StringBuilder metadata = new StringBuilder();
        int counter = 1;

        if (software.equals("PeptideShaker")) {
            metadata.append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(String.format(field, counter)).append(COLUMN_DELIMITER)
                    .append(createOntology(mzTabParams.get(alignment).getMzTabParamOptions().get(2).getOntology(), mzTabParams.get(alignment).getMzTabParamOptions().get(2).getAccession(), mzTabParams.get(alignment).getMzTabParamOptions().get(2).getName()));
        } else if (software.equals("MaxQuant")) {
            metadata.append(METADATA_PREFIX).append(COLUMN_DELIMITER).append(String.format(field, counter)).append(COLUMN_DELIMITER)
                    .append(createOntology(mzTabParams.get(alignment).getMzTabParamOptions().get(1).getOntology(), mzTabParams.get(alignment).getMzTabParamOptions().get(1).getAccession(), mzTabParams.get(alignment).getMzTabParamOptions().get(1).getName()));
        }

        return metadata.toString();
    }

    /**
     * Get search engine.
     *
     * @return searchEngine
     */
    private String getSearchEngine() {
        StringBuilder searchEngine = new StringBuilder();

        if (software.equals("PeptideShaker")) {
            searchEngine.append(createOntology(mzTabParams.get(SOFTWARE_ALIGNMENT).getMzTabParamOptions().get(1).getOntology(), mzTabParams.get(SOFTWARE_ALIGNMENT).getMzTabParamOptions().get(1).getAccession(), mzTabParams.get(SOFTWARE_ALIGNMENT).getMzTabParamOptions().get(1).getName())).append(VERTICAL_BAR);
        } else if (software.equals("MaxQuant")) {
            searchEngine.append(createOntology(mzTabParams.get(SOFTWARE_ALIGNMENT).getMzTabParamOptions().get(0).getOntology(), mzTabParams.get(SOFTWARE_ALIGNMENT).getMzTabParamOptions().get(0).getAccession(), mzTabParams.get(SOFTWARE_ALIGNMENT).getMzTabParamOptions().get(0).getName())).append(VERTICAL_BAR);
        }

        searchEngine = searchEngine.deleteCharAt(searchEngine.length() - 1);

        return searchEngine.toString();
    }

    /**
     * Only one software can be used. (MaxQuant or PeptideShaker)
     */
    private void setSoftware() {
        software = getSearchAndValidationSettings(mzTabExport.getRuns().get(0)).getSearchEngine().getName();
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
        return OPEN_BRACKET + ontology + COMMA_SEPARATOR + accession + COMMA_SEPARATOR
                + name + COMMA_SEPARATOR + CLOSE_BRACKET;
    }

    /**
     * Get the quantification settings.
     *
     * @param analyticalRun
     * @return quantificationSettingsMap
     */
    private QuantificationSettings getQuantificationSettings(AnalyticalRun analyticalRun) {
        return analyticalRun.getQuantificationSettings();
    }

    /**
     * Get the search and validation settings.
     *
     * @param analyticalRun the {@link AnalyticalRun} instance
     * @return quantificationSettingsMap
     */
    private SearchAndValidationSettings getSearchAndValidationSettings(AnalyticalRun analyticalRun) {
        return analyticalRun.getSearchAndValidationSettings();
    }

    /**
     * Get protein groups for given list of analyticalRuns.
     *
     * @param analyticalRunIds
     * @return list of ProteinGroupDTO object
     */
    private List<ProteinGroupDTO> getProteinGroupsForAnalyticalRuns(List<Long> analyticalRunIds) {
        return proteinGroupService.getProteinGroupDTOsForRuns(analyticalRunIds);
    }

    /**
     * Get PeptideHasProteinGroup for the given list of analytical runs.
     *
     * @param analyticalRunIds
     * @return list of PeptideHasProteinGroup
     */
    private Map<PeptideHasProteinGroup, AnalyticalRun> getPeptideHasProteinGroupForAnalyticalRuns(List<Long> analyticalRunIds) {
        return peptideService.getPeptideHasProteinGroupByAnalyticalRunId(analyticalRunIds);
    }

    /**
     * Get ambiguity members for given proteinGroupID.
     *
     * @param proteinGroupId
     * @return ambiguity members as string
     */
    private String getAmbiguityMembers(Long proteinGroupId) {
        List<ProteinGroupHasProtein> proteinGroupHasProteins = proteinGroupService.getAmbiguityMembers(proteinGroupId);

        StringBuilder ambiguityMembers = new StringBuilder("");
        proteinGroupHasProteins.stream().forEach((proteinGroupHasProtein) -> ambiguityMembers.append(proteinGroupHasProtein.getProteinAccession()).append(","));
        if (ambiguityMembers.toString().equals("")) {
            return null;
        } else {
            return ambiguityMembers.deleteCharAt(ambiguityMembers.length() - 1).toString();
        }
    }

    /**
     * Get protein abundance for given assay, protein group and analyticalRun
     * Use assay to get the reagent.
     *
     * @param proteinGroup
     * @param assay
     * @return protein abundance
     */
    private double getProteinAbundanceForAssay(ProteinGroupDTO proteinGroup, String assay) throws IOException {
        double proteinAbundance = 0.0;
        AnalyticalRun analyticalRun = assayAnalyticalRunRef.get(assay);
        if (assayReagentRef.get(assay).equals(UNLABELED_SAMPLE)) {
            proteinAbundance = proteinGroupQuantService.getProteinGroupQuantForRunAndProteinGroup(analyticalRun.getId(), proteinGroup.getId()).getIntensity();
        } else {
            //get the label from the user interface.
            String label = mzTabExport.getQuantificationReagentLabelMatch().get(assayReagentRef.get(assay));
            ProteinGroupQuant proteinGroupQuant = proteinGroupQuantService.getProteinGroupQuantForRunAndProteinGroup(analyticalRun.getId(), proteinGroup.getId());
            if (proteinGroupQuant != null) {
                //deserialize the intensities json string
                Map<String, Double> intensities = mapper.readValue(proteinGroupQuant.getLabels(), new TypeReference<Map<String, Double>>() {
                });
                Optional<String> foundLabel = intensities.keySet().stream().filter(dbLabel -> dbLabel.equals(label)).findFirst();
                if (foundLabel.isPresent()) {
                    proteinAbundance = intensities.get(foundLabel.get());
                }
            }
        }

        return proteinAbundance;
    }

    /**
     * find database and version only first runs primary FASTA file.
     *
     * @return map (key:index ; value:database and version)
     */
//    private Map<Integer, String> findDatabaseAndVersion() {
//        // key:database ; value:version
//        Map<Integer, String> databaseVersion = new HashMap<>();
//
//        SearchAndValidationSettings searchAndValidationSettings = searchAndValidationSettingsService.getByAnalyticalRun(mzTabExport.getRuns().get(0));
//        Map<FastaDb, FastaDbType> fastaDbs = fastaDbService.findBySearchAndValidationSettings(searchAndValidationSettings);
//
//        for (Map.Entry<FastaDb, FastaDbType> fastaDbEntry : fastaDbs.entrySet()) {
//            if (fastaDbEntry.getValue().equals(FastaDbType.PRIMARY)) {
//                databaseVersion.put(0, fastaDbEntry.getKey().getDatabaseName());
//                databaseVersion.put(1, fastaDbEntry.getKey().getVersion());
//            }
//        }
//        return databaseVersion;
//    }

    /**
     * Create spectra reference for given spectrum.
     *
     * @param spectrum
     * @param analyticalRun
     * @return
     */
    private String createSpectraRef(Spectrum spectrum, AnalyticalRun analyticalRun) {
        return String.format(MS_RUN_REF, analyticalRunIndexRef.get(analyticalRun.getId())) + ":index=" + spectrum.getScanNumber();
    }

    /**
     * Check if given peptide is unique.
     *
     * @param peptideHasProteinGroups
     * @param peptide
     * @return true or false
     */
    private boolean isPeptideUnique(Map<PeptideHasProteinGroup, AnalyticalRun> peptideHasProteinGroups, Peptide peptide) {
        List<Peptide> peptides = new ArrayList<>();
        peptideHasProteinGroups.keySet().stream().filter(p -> Objects.equals(p.getPeptide().getId(), peptide.getId())).forEach(peptideHasProteinGroup -> peptides.add(peptideHasProteinGroup.getPeptide()));
        return peptides.size() == 1;
    }

    /**
     * Get modifications for given PSM.
     *
     * @param peptide
     * @return modifications
     */
    private String getModifications(Peptide peptide) {
        StringBuilder modifications = new StringBuilder();
        peptideService.fetchPeptideHasModifications(peptide);
        for (PeptideHasModification peptideHasModification : peptide.getPeptideHasModifications()) {
            if (peptideHasModification.getModification().getAccession() == null) {
                return null;
            }
            modifications.append(peptideHasModification.getLocation());
            if (software.equals("PeptideShaker")) {
                modifications.append(createOntology(mzTabParams.get(5).getMzTabParamOptions().get(1).getOntology(), mzTabParams.get(5).getMzTabParamOptions().get(1).getAccession(), mzTabParams.get(5).getMzTabParamOptions().get(1).getName()));
            } else if (software.equals("MaxQuant")) {
                modifications.append(createOntology(mzTabParams.get(5).getMzTabParamOptions().get(0).getOntology(), mzTabParams.get(5).getMzTabParamOptions().get(0).getAccession(), mzTabParams.get(5).getMzTabParamOptions().get(0).getName()));
            }
            modifications = modifications.deleteCharAt(modifications.length() - 1);
            modifications.append(peptideHasModification.getProbabilityScore()).append(CLOSE_BRACKET).append("-");
            modifications.append(peptideHasModification.getModification().getAccession()).append(COMMA_SEPARATOR);
        }
        if (modifications.length() > 0) {
            return modifications.deleteCharAt(modifications.length() - 1).toString();
        } else {
            return null;
        }

    }

    /**
     * Find which FASTA file given protein sequence comes from.
     *
     * @param proteinAccession protein accession
     * @return FASTA file
     */
    private FastaDb findFastaDb(String proteinAccession) {
        // we do not know which fasta file protein sequence comes from.
        FastaDb fastaFile = new FastaDb();
        if (proteinAccession.contains(CONTAMINANT_PREFIX)) {
            for (FastaDb fastaDb : fastaDbs.keySet()) {
                if (fastaDbs.get(fastaDb).equals(FastaDbType.CONTAMINANTS)) {
                    fastaFile = fastaDb;
                }
            }
        } else {
            for (FastaDb fastaDb : parsedFastas.keySet()) {
                if (parsedFastas.get(fastaDb).contains(proteinAccession)) {
                    fastaFile = fastaDb;
                    break;
                }
            }
        }

        return fastaFile;
    }

    /**
     * Parse all FASTA files found by search and validation settings
     *
     * @throws IOException
     */
    private void parseFastaFiles() throws IOException {
        SearchAndValidationSettings searchAndValidationSettings = searchAndValidationSettingsService.getByAnalyticalRun(mzTabExport.getRuns().get(0));
        fastaDbs = fastaDbService.findBySearchAndValidationSettings(searchAndValidationSettings);

        LinkedHashMap<FastaDb, Path> fastaDbsWithPath = new LinkedHashMap();
        fastaDbs.keySet().stream().filter((fastaDb) -> (fastaDbs.get(fastaDb).equals(FastaDbType.PRIMARY))).forEach((fastaDb) -> {
            //make the path absolute and check if it exists
            Path absoluteFastaDbPath = mzTabExport.getFastaDirectory().resolve(fastaDb.getFilePath());
            if (!Files.exists(absoluteFastaDbPath)) {
                throw new IllegalArgumentException("The FASTA DB file " + absoluteFastaDbPath + " doesn't exist.");
            }
            fastaDbsWithPath.put(fastaDb, absoluteFastaDbPath);
        });

        fastaDbs.keySet().stream().filter((fastaDb) -> (fastaDbs.get(fastaDb).equals(FastaDbType.ADDITIONAL))).forEach((fastaDb) -> {
            //make the path absolute and check if it exists
            Path absoluteFastaDbPath = mzTabExport.getFastaDirectory().resolve(fastaDb.getFilePath());
            if (!Files.exists(absoluteFastaDbPath)) {
                throw new IllegalArgumentException("The FASTA DB file " + absoluteFastaDbPath + " doesn't exist.");
            }
            fastaDbsWithPath.put(fastaDb, absoluteFastaDbPath);
        });

        fastaDbs.keySet().stream().filter((fastaDb) -> (fastaDbs.get(fastaDb).equals(FastaDbType.CONTAMINANTS))).forEach((fastaDb) -> {
            //make the path absolute and check if it exists
            Path absoluteFastaDbPath = mzTabExport.getFastaDirectory().resolve(fastaDb.getFilePath());
            if (!Files.exists(absoluteFastaDbPath)) {
                throw new IllegalArgumentException("The FASTA DB file " + absoluteFastaDbPath + " doesn't exist.");
            }
            fastaDbsWithPath.put(fastaDb, absoluteFastaDbPath);
        });

        parsedFastas = fastaDbParser.parseAccessions(fastaDbsWithPath, getSearchAndValidationSettings(mzTabExport.getRuns().get(0)).getSearchEngine().getSearchEngineType());
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.io.mztab;

import com.compomics.colims.model.AnalyticalRun;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * @author Niels Hulstaert
 */
@Component("mzTabExporter")
public class MzTabExporter {

    private static final Logger LOGGER = Logger.getLogger(MzTabExporter.class);
    private static final String MZTAB_EXTENSION = ".mzTab";
    private static final String COLUMN_DELIMETER = "/t";
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

    public void exportAnalyticalRun(File exportDirectory, AnalyticalRun analyticalRun) {
        try (FileOutputStream fos = new FileOutputStream(new File(exportDirectory, analyticalRun.getName() + MZTAB_EXTENSION));
             OutputStreamWriter osw = new OutputStreamWriter(fos, Charset.forName("UTF-8").newEncoder());
             BufferedWriter bw = new BufferedWriter(osw);
             PrintWriter pw = new PrintWriter(bw)) {

            pw.println("under development");

        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void exportAnalyticalRun(File exportDirectory, String title, AnalyticalRun analyticalRun) {
        try (FileOutputStream fos = new FileOutputStream(new File(exportDirectory, title + MZTAB_EXTENSION));
             OutputStreamWriter osw = new OutputStreamWriter(fos, Charset.forName("UTF-8").newEncoder());
             BufferedWriter bw = new BufferedWriter(osw);
             PrintWriter pw = new PrintWriter(bw)) {

            pw.println("under development");

        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void exportAnalyticalRun(File exportDirectory, String title, List<AnalyticalRun> analyticalRuns) {
        try (FileOutputStream fos = new FileOutputStream(new File(exportDirectory, title + MZTAB_EXTENSION));
             OutputStreamWriter osw = new OutputStreamWriter(fos, Charset.forName("UTF-8").newEncoder());
             BufferedWriter bw = new BufferedWriter(osw);
             PrintWriter pw = new PrintWriter(bw)) {

            pw.println("under development");


        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }


}

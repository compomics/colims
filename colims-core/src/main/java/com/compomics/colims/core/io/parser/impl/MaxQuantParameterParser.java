package com.compomics.colims.core.io.parser.impl;

import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.identification.SearchParameters;
import com.compomics.util.preferences.ModificationProfile;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 *
 * @author Davy
 */
@Component("maxQuantParameterParser")
public class MaxQuantParameterParser {

    /**
     * parses the settings for the search per run searched
     *
     * @param maxQuantTextFolder the folder containing the textual max quant
     * output files
     * @return a {@code Map} that, for each raw file, contains the settings for
     * that search
     * @throws IOException
     */
    public Map<String, SearchParameters> parse(File maxQuantTextFolder) throws IOException {
        File parameterFile = new File(maxQuantTextFolder, "parameters.txt");
        File summaryFile = new File(maxQuantTextFolder, "summary.txt");

        Map<String, String> values;
        Map<String, SearchParameters> runParams = new HashMap<>();
        SearchParameters globalParameters = new SearchParameters();
        TabularFileLineValuesIterator parameterIter = new TabularFileLineValuesIterator(parameterFile);

        while (parameterIter.hasNext()) {
            // for some reason this file was built vertically
            values = parameterIter.next();
            if (values.containsKey(ParametersHeaders.KEEP_LOW_SCORING_PEPTIDES.column)) {
                globalParameters.setDiscardLowQualitySpectra(Boolean.getBoolean(values.get(ParametersHeaders.KEEP_LOW_SCORING_PEPTIDES.column).toLowerCase(Locale.US)));
            } else if (values.containsKey(ParametersHeaders.FASTA_FILE.column)) {
                globalParameters.setFastaFile(new File(values.get(ParametersHeaders.FASTA_FILE.column)));
            } else if (values.containsKey(ParametersHeaders.MIN_PEP_LENGTH.column)) {
                globalParameters.setMinPeptideLength(Integer.parseInt(values.get(ParametersHeaders.MIN_PEP_LENGTH.column)));
            } else if (values.containsKey(ParametersHeaders.MAX_PEP_PEP.column)) {
                globalParameters.setMaxEValue(Double.parseDouble(values.get(ParametersHeaders.MAX_PEP_PEP.column)));
            } else if (values.containsKey(ParametersHeaders.FTMS_MS_MS_TOLERANCE.column)) {
                globalParameters.setFragmentIonAccuracy(Double.parseDouble(values.get(ParametersHeaders.FTMS_MS_MS_TOLERANCE.column).split(" ")[0]));
            }
        }

        TabularFileLineValuesIterator summaryIter = new TabularFileLineValuesIterator(summaryFile);

        while (summaryIter.hasNext()) {
            SearchParameters runParameters = new SearchParameters();
            values = summaryIter.next();
            //runParams.put(values.get(SummaryHeaders.RAW_FILE.column), (SearchParameters)globalParameters.clone());
            ModificationProfile runModifications = new ModificationProfile();
            if (values.containsKey(SummaryHeaders.FIXED_MODS.column)) {
                for (String fixedMod : values.get(SummaryHeaders.FIXED_MODS.column).split(";")) {
                    PTM fixedPTM = new PTM();
                    fixedPTM.setName(fixedMod);
                    runModifications.addFixedModification(fixedPTM);

                }
            }
            if (values.containsKey(SummaryHeaders.VAR_MODS.column)) {

                for (String varMod : values.get(SummaryHeaders.VAR_MODS.column).split(";")) {
                    PTM varPTM = new PTM();
                    varPTM.setName(varMod);
                    runModifications.addVariableModification(varPTM);
                }
            }
            runParameters.setModificationProfile(runModifications);
            //runParameters.setEnzyme(values.get(SummaryHeaders.PROTEASE.column));
            MaxQuantParameterFileAggregator tarredParameters = new MaxQuantParameterFileAggregator(summaryFile, parameterFile);
            runParameters.setParametersFile(tarredParameters.getTarredParaMeterFiles());
        }
        return runParams;
    }

    //DAMN YOU MAX QUUAAAAAANT
    private enum ParametersHeaders implements HeaderEnum{

        VERSION("Version"),
        FIXED_MODIFICATIONS("Fixed modifications"),
        RANDOMIZE("Randomize"),
        DIGEST_AA("Special AAs"),
        INCLUDE_CONTAMINANTS("Include contaminants"),
        //FOURIER TRANSFORM
        FTMS_MS_MS_TOLERANCE("MS/MS tol. (FTMS)"),
        FTMS_PEAKS_PER_100_DALTON("Top MS/MS peaks per 100 Da. (FTMS)"),
        FTMS_DEISOTOPING("MS/MS deisotoping (FTMS)"),
        ITMS_MS_MS_TOLERANCE("MS/MS tol. (ITMS)"),
        ITMS_PEAKS_PER_100_DALTON("Top MS/MS peaks per 100 Da. (ITMS)"),
        ITMS_DEISOTOPING("MS/MS deisotoping (ITMS)"),
        //TIME OF FLIGHT
        TOF_MS_MS_TOLERANCE("MS/MS tol. (TOF)"),
        TOF_PEAKS_PER_100_DALTON("Top MS/MS peaks per 100 Da. (TOF)"),
        TOF_DEISOTOPING("MS/MS deisotoping (TOF)"),
        //OTHER
        UNKNOWN_MS_MS_TOLERANCE("MS/MS tol. (Unknown)"),
        UNKNOWN_PEAKS_PER_100_DALTON("Top MS/MS peaks per 100 Da. (Unknown)"),
        UNKNOWN_DEISOTOPING("MS/MS deisotoping (Unknown)"),
        //quality
        PEP_FDR("Peptide FDR"),
        MAX_PEP_PEP("Max. peptide PEP"),
        PROTEIN_FDR("Protein FDR"),
        SITE_FDR("Site FDR"),
        NORMALIZED_OCCUPANCY_RATIOS("Use Normalized Ratios For Occupancy"),
        SEPARATE_SITE_FDR("Apply site FDR separately"),
        MIN_PEP_LENGTH("Min. peptide Length"),
        MIN_SCORE("Min. score"),
        MIN_UNIQUE_PEPTIDES("Min. unique peptides"),
        MIN_RAZOR_PEPTIDES("Min. razor peptides"),
        MIN_PEPTIDES("Min. peptides"),
        USE_UNMOD("Use only unmodified peptides and"),
        USE_PEPTIDES_MODDED_WITH("Modifications included in protein quantification"),
        PEPTIDES_USE_FOR_QUANT("Peptides used for protein quantification"),
        DISCARD_UNMOD_COUNTERPART_PEPTIDES("Discard unmodified counterpart peptides"),
        MIN_PEP_COUNT_FOR_RATIO("Min. ratio count"),
        LFQ_MIN_RATIO_COUNT("Lfq min. ratio count"),
        KEEP_LOW_SCORING_PEPTIDES("Keep low-scoring versions of identified peptides"),
        RECAL_MS_MS("MS/MS recalibration"),
        CUT_PEAKS("Cut peaks"),
        //quant
        SITE_QUANTITATION_METHOD("Site quantification"),
        RE_QUANTIFY("Re-quantify"),
        LABEL_FREE("Label-free protein quantification"),
        IBAQ("iBAQ"),
        LOG_IBAQ("iBAQ log fit"),
        ADVANCED_RATIOS("Advanced ratios"),
        //mass spec
        MATCH_BETWEEN_RUNS("Match between runs"),
        TIME_WINDOW_IN_MINUTES("Time window [min]"),
        FIND_DEPENDENT_PEPTIDES("Find dependent peptides"),
        LABELED_AA_FILTERING("Labeled amino acid filtering"),
        RETENTION_TIME_SHIFT("RT shift"),
        //AIF
        AIF_CORRELATION("AIF correlation"),
        FIRST_PASS_AIF_CORRELATION("First pass AIF correlation"),
        AIF_TOPX("AIF topx"),
        AIF_MIN_MASS("AIF min mass"),
        AIF_SIL_WEIGHT("AIF SIL weight"),
        AIF_ISOBAR_WEIGHT("AIF ISO weight"),
        AIF_ITERATIVE("AIF iterative"),
        AIF_THRESHOLD_FDR("AIF threshold FDR"),
        //FASTA
        FASTA_FILE("Fasta file"),
        FIRST_SEARCH_FASTA_FILE("First search fasta file"),
        //mods
        MOD_SITE_HEADERS("Site tables");
        public String column;

        private ParametersHeaders(String column) {
            this.column = column;
        }

        @Override
        public String[] returnPossibleColumnNames() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void setColumnReference(int columnReference) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getColumnName() throws HeaderEnumNotInitialisedException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    private enum SummaryHeaders {

        RAW_FILE("Raw file"),
        INSTRUMENT("Instrument"),
        TUNE_FILE("Tune file"),
        PROTEASE("Protease"),
        PROTEASE_FIRST_SEARCH("Protease first search"),
        USE_PROTEASE_FIRST_SEARCH("Use protease first search"),
        FIXED_MODS("Fixed modifications"),
        VAR_MODS("Variable modifications"),
        VAR_MODS_FIRST_SEARCH("Variable modifications first search"),
        USE_VAR_MODS_FIRST_SEARCH("Use variable modifications first search"),
        MULTIPLICITY("Multiplicity"),
        MAX_MISCLEAVAGES("Max. missed cleavages"),
        N_TERMINUS_LABEL("Labels0"),
        LS_MS_RUN_TYPE("LC-MS run type"),
        TIME_DEPENDENT_CALIBRATION("Time-dependent recalibration"),
        NUMBER_OF_MS_SCANS("MS"),
        NUMBER_OF_MS_MS_SCANS("MS/MS"),
        SUBMITTED_MS_MS("MS/MS Submitted"),
        SIL_SUBMITTED_MS_MS("MS/MS Submitted (SIL)"),
        ISO_SUBMITTED_MS_MS("MS/MS Submitted (ISO)"),
        PEAK_SUBMITTED_MS_MS("MS/MS Submitted (PEAK)"),
        POLYMER_MS_MS("MS/MS on Polymers"),
        IDENTIFIED_MS_MS("MS/MS Identified"),
        SIL_IDENTIFIED_MS_MS("MS/MS Identified (SIL)"),
        ISO_IDENTIFIED_MS_MS("MS/MS Identified (ISO)"),
        PEAK_IDENTIFIED_MS_MS("MS/MS Identified (PEAK)"),
        PERCENTAGE_IDENTIFIED_MS_MS("MS/MS Identified [%]"),
        PERCENTAGE_SIL_IDENTIFIED_MS_MS("MS/MS Identified (SIL) [%]"),
        PERCENTAGE_ISO_IDENTIFIED_MS_MS("MS/MS Identified (ISO) [%]"),
        PERCENTAGE_PEAK_IDENTIFIED_MS_MS("MS/MS Identified (PEAK) [%]"),
        IDENTIFIED_PEPTIDE_SEQUENCES("Peptide Sequences Identified"),
        NUMBER_OF_PEAKS("Peaks"),
        NUMBER_OF_SEQUENCED_PEAKS("Peaks Sequenced"),
        PERCENTAGE_OF_PEAKS_SEQUENCES("Peaks Sequenced [%]"),
        NUMBER_OF_RECURRING_PEAKS("Peaks Repeatedly Sequenced"),
        PERCENTAGE_OF_RECURRING_PEAKS("Peaks Repeatedly Sequenced [%]"),
        ISOTOPE_PATTERNS("Isotope Patterns"),
        NUMBER_OF_SEQUENCED_ISOTOPE_PATTERNS("Isotope Patterns Sequenced"),
        NUMBER_OF_MULTPILE_CHARGED_SEQUENCED_ISOTOPE_PATTERNS("Isotope Patterns Sequenced (z>1)"),
        PERCENTAGE_OF_ISOTOPE_PATTERNS_SEQUENCED("Isotope Patterns Sequenced [%]"),
        PERCENTAGE_OF_MULTPILE_CHARGED_SEQUENCED_ISOTOPE_PATTERNS("Isotope Patterns Sequenced (z>1) [%]"),
        NUMBER_OF_REPEATING_ISOTOPE_PATTERNS("Isotope Patterns Repeatedly Sequenced"),
        PERCENTAGE_OF_REPEATING_ISOTOPE_PATTERNS("Isotope Patterns Repeatedly Sequenced [%]"),
        RECALIBRATED("Recalibrated"),
        AVERAGE_ABSOLUTE_MASS_DEVIATION("Av. Absolute Mass Deviation"),
        MASS_STANDARD_DEVEVIATION("Mass Standard Deviation"),
        LABELFREE_INTENSITY_NORMALISATION_FACTOR("Label free norm param");
        public String column;

        private SummaryHeaders(String column) {
            this.column = column;
        }
    }
}

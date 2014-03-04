package com.compomics.colims.core.io.maxquant;

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
    private enum ParametersHeaders implements HeaderEnum {

        VERSION(new String[]{"Version"}),
        FIXED_MODIFICATIONS(new String[]{"Fixed modifications"}),
        RANDOMIZE(new String[]{"Randomize"}),
        DIGEST_AA(new String[]{"Special AAs"}),
        INCLUDE_CONTAMINANTS(new String[]{"Include contaminants"}),
        //FOURIER TRANSFORM
        FTMS_MS_MS_TOLERANCE(new String[]{"MS/MS tol. (FTMS)"}),
        FTMS_PEAKS_PER_100_DALTON(new String[]{"Top MS/MS peaks per 100 Da. (FTMS)"}),
        FTMS_DEISOTOPING(new String[]{"MS/MS deisotoping (FTMS)"}),
        ITMS_MS_MS_TOLERANCE(new String[]{"MS/MS tol. (ITMS)"}),
        ITMS_PEAKS_PER_100_DALTON(new String[]{"Top MS/MS peaks per 100 Da. (ITMS)"}),
        ITMS_DEISOTOPING(new String[]{"MS/MS deisotoping (ITMS)"}),
        //TIME OF FLIGHT
        TOF_MS_MS_TOLERANCE(new String[]{"MS/MS tol. (TOF)"}),
        TOF_PEAKS_PER_100_DALTON(new String[]{"Top MS/MS peaks per 100 Da. (TOF)"}),
        TOF_DEISOTOPING(new String[]{"MS/MS deisotoping (TOF)"}),
        //OTHER
        UNKNOWN_MS_MS_TOLERANCE(new String[]{"MS/MS tol. (Unknown)"}),
        UNKNOWN_PEAKS_PER_100_DALTON(new String[]{"Top MS/MS peaks per 100 Da. (Unknown)"}),
        UNKNOWN_DEISOTOPING(new String[]{"MS/MS deisotoping (Unknown)"}),
        //quality
        PEP_FDR(new String[]{"Peptide FDR"}),
        MAX_PEP_PEP(new String[]{"Max. peptide PEP"}),
        PROTEIN_FDR(new String[]{"Protein FDR"}),
        SITE_FDR(new String[]{"Site FDR"}),
        NORMALIZED_OCCUPANCY_RATIOS(new String[]{"Use Normalized Ratios For Occupancy"}),
        SEPARATE_SITE_FDR(new String[]{"Apply site FDR separately"}),
        MIN_PEP_LENGTH(new String[]{"Min. peptide Length"}),
        MIN_SCORE(new String[]{"Min. score"}),
        MIN_UNIQUE_PEPTIDES(new String[]{"Min. unique peptides"}),
        MIN_RAZOR_PEPTIDES(new String[]{"Min. razor peptides"}),
        MIN_PEPTIDES(new String[]{"Min. peptides"}),
        USE_UNMOD(new String[]{"Use only unmodified peptides and"}),
        USE_PEPTIDES_MODDED_WITH(new String[]{"Modifications included in protein quantification"}),
        PEPTIDES_USE_FOR_QUANT(new String[]{"Peptides used for protein quantification"}),
        DISCARD_UNMOD_COUNTERPART_PEPTIDES(new String[]{"Discard unmodified counterpart peptides"}),
        MIN_PEP_COUNT_FOR_RATIO(new String[]{"Min. ratio count"}),
        LFQ_MIN_RATIO_COUNT(new String[]{"Lfq min. ratio count"}),
        KEEP_LOW_SCORING_PEPTIDES(new String[]{"Keep low-scoring versions of identified peptides"}),
        RECAL_MS_MS(new String[]{"MS/MS recalibration"}),
        CUT_PEAKS(new String[]{"Cut peaks"}),
        //quant
        SITE_QUANTITATION_METHOD(new String[]{"Site quantification"}),
        RE_QUANTIFY(new String[]{"Re-quantify"}),
        LABEL_FREE(new String[]{"Label-free protein quantification"}),
        IBAQ(new String[]{"iBAQ"}),
        LOG_IBAQ(new String[]{"iBAQ log fit"}),
        ADVANCED_RATIOS(new String[]{"Advanced ratios"}),
        //mass spec
        MATCH_BETWEEN_RUNS(new String[]{"Match between runs"}),
        TIME_WINDOW_IN_MINUTES(new String[]{"Time window [min]"}),
        FIND_DEPENDENT_PEPTIDES(new String[]{"Find dependent peptides"}),
        LABELED_AA_FILTERING(new String[]{"Labeled amino acid filtering"}),
        RETENTION_TIME_SHIFT(new String[]{"RT shift"}),
        //AIF
        AIF_CORRELATION(new String[]{"AIF correlation"}),
        FIRST_PASS_AIF_CORRELATION(new String[]{"First pass AIF correlation"}),
        AIF_TOPX(new String[]{"AIF topx"}),
        AIF_MIN_MASS(new String[]{"AIF min mass"}),
        AIF_SIL_WEIGHT(new String[]{"AIF SIL weight"}),
        AIF_ISOBAR_WEIGHT(new String[]{"AIF ISO weight"}),
        AIF_ITERATIVE(new String[]{"AIF iterative"}),
        AIF_THRESHOLD_FDR(new String[]{"AIF threshold FDR"}),
        //FASTA
        FASTA_FILE(new String[]{"Fasta file"}),
        FIRST_SEARCH_FASTA_FILE(new String[]{"First search fasta file"}),
        //mods
        MOD_SITE_HEADERS(new String[]{"Site tables"});
        public String column;
        private String[] columnNames;
        private int columnReference = -1;

        private ParametersHeaders(final String[] fieldnames) {
            columnNames = fieldnames;
        }

        @Override
        public final String[] returnPossibleColumnNames() {
            return columnNames;
        }

        @Override
        public final void setColumnReference(int columnReference) {
            this.columnReference = columnReference;
        }

        @Override
        public final String getColumnName() throws HeaderEnumNotInitialisedException {
            if (columnNames != null) {
                if (columnReference < 0 || columnReference > (columnNames.length - 1) && columnNames.length > 0) {
                    return columnNames[0].toLowerCase(Locale.US);
                } else if (columnNames.length < 0) {
                    throw new HeaderEnumNotInitialisedException("header enum not initialised");
                } else {
                    return columnNames[columnReference].toLowerCase(Locale.US);
                }
            } else {
                throw new HeaderEnumNotInitialisedException("array was null");
            }
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

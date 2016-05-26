package com.compomics.colims.distributed.io.maxquant.headers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by Iain on 02/12/2014.
 */
public enum MaxQuantEvidenceHeaders implements HeaderEnum {

    ACETYL_K(new String[]{"Acetyl (K)"}),
    ACETYL_K_PROBABILITIES(new String[]{"Acetyl (K) Probabilities"}),
    ACETYL_K_SCORE_DIFFS(new String[]{"Acetyl (K) Score Diffs"}),
    ACETYL_PROTEIN_N_TERM(new String[]{"Acetyl (Protein N-term)"}),
    AIF_MS_MS_IDS(new String[]{"AIF MS/MS IDs"}),
    B_MIXTURE(new String[]{"B mixture"}),
    BASE_PEAK_FRACTION(new String[]{"Base peak fraction"}),
    BEST_MS_MS(new String[]{"Best MS/MS"}),
    CALIBRATED_RETENTION_TIME(new String[]{"Calibrated Retention Time"}),
    CALIBRATED_RETENTION_TIME_START(new String[]{"Calibrated Retention Time Start"}),
    CALIBRATED_RETENTION_TIME_FINISH(new String[]{"Calibrated Retention Time Finish"}),
    CHARGE(new String[]{"Charge"}),
    COMBINATORICS(new String[]{"Combinatorics"}),
    CONTAMINANT(new String[]{"Contaminant"}),
    DELTA_SCORE(new String[]{"Delta score"}),
    EXPERIMENT(new String[]{"Experiment"}),
    FASTA_HEADERS(new String[]{"FASTA headers"}),
    FRACTION(new String[]{"Fraction"}),
    FRACTION_OF_TOTAL_SPECTRUM(new String[]{"Fraction of total spectrum"}),
    GENE_NAMES(new String[]{"Gene Names"}),
    ID(new String[]{"id"}),
    INTENSITY(new String[]{"Intensity"}),
    INTENSITY_L(new String[]{"Intensity L"}),
    INTENSITY_M(new String[]{"Intensity M"}),
    INTENSITY_H(new String[]{"Intensity H"}),
    K_COUNT(new String[]{"K Count"}),
    LABELING_STATE(new String[]{"Labeling State"}),
    LEADING_PROTEINS(new String[]{"Leading Proteins"}),
    LEADING_RAZOR_PROTEIN(new String[]{"Leading Razor Protein"}),
    LENGTH(new String[]{"Length"}),
    M_Z(new String[]{"m/z"}),
    MASS(new String[]{"Mass"}),
    MASS_ERROR_PPM(new String[]{"Mass Error [ppm]"}),
    MAX_INTENSITY_M_Z_0(new String[]{"Max Intensity m/z 0"}),
    MAX_INTENSITY_M_Z_1(new String[]{"Max Intensity m/z 1"}),
    MATCH_TIME_DIFFERENCE(new String[]{"Match Time Difference"}),
    MOD_PEPTIDE_ID(new String[]{"Mod. peptide ID"}),
    MODIFICATIONS(new String[]{"Modifications"}),
    MODIFIED_SEQUENCE(new String[]{"Modified Sequence"}),
    MS_MS_COUNT(new String[]{"MS/MS Count"}),
    MS_MS_IDS(new String[]{"MS/MS IDs"}),
    MS_MS_M_Z(new String[]{"MS/MS m/z"}),
    MS_MS_SCAN_NUMBER(new String[]{"MS/MS Scan Number"}),
    NORMALIZED_RATIO(new String[]{"Ratio H/L normalized"}),
    OXIDATION_M(new String[]{"Oxidation (M)"}),
    OXIDATION_M_PROBABILITIES(new String[]{"Oxidation (M) Probabilities"}),
    OXIDATION_M_SCORE_DIFFS(new String[]{"Oxidation (M) Score Diffs"}),
    OXIDATION_M_SITE_IDS(new String[]{"Oxidation (M) site IDs"}),
    PEP(new String[]{"PEP"}),
    PEPTIDE_ID(new String[]{"Peptide ID"}),
    PIF(new String[]{"PIF"}),
    PROTEIN_NAMES(new String[]{"Protein Names"}),
    PROTEIN_DESCRIPTIONS(new String[]{"Protein Descriptions"}),
    PROTEIN_GROUP_IDS(new String[]{"Protein group IDs"}),
    PROTEINS(new String[]{"Proteins"}),
    RATIO_HL(new String[]{"Ratio H/L"}),
    RATIO_NORMALIZED_HL(new String[]{"Ratio H/L normalized"}),
    RATIO_SHIFT_HL(new String[]{"Ratio H/L shift"}),
    RAW_FILE(new String[]{"Raw File"}),
    RESOLUTION(new String[]{"Resolution"}),
    RETENTION_LENGTH(new String[]{"Retention Length"}),
    RETENTION_TIME(new String[]{"Retention Time"}),
    RETENTION_TIME_CALIBRATION(new String[]{"Retention Time Calibration"}),
    REVERSE(new String[]{"Reverse"}),
    SCORE(new String[]{"Score"}),
    SEQUENCE(new String[]{"Sequence"}),
    TYPE(new String[]{"Type"}),
    UNCALIBRATED_CALIBRATED_M_Z_PPM(new String[]{"Uncalibrated - Calibrated m/z [ppm]"}),
    UNCALIBRATED_MASS_ERROR_PPM(new String[]{"Uncalibrated Mass Error [ppm]"}),
    UNIPROT(new String[]{"Uniprot"});

    /**
     * The list of header values for the enum value.
     */
    private List<String> headerValues;
    /**
     * The default header value index.
     */
    private int defaultHeaderValueIndex = 0;

    /**
     * Constructor.
     *
     * @param headerValueArray the array header values
     */
    MaxQuantEvidenceHeaders(final String[] headerValueArray) {

        headerValues = new ArrayList<>(headerValueArray.length);
        Arrays.stream(headerValueArray).forEach(e -> headerValues.add(e.toLowerCase(Locale.US)));
    }

    @Override
    public List<String> getPossibleValues() {
        return this.headerValues;
    }

    @Override
    public void setParsedValue(int index) {
        this.defaultHeaderValueIndex = index;
    }

    @Override
    public String getValue() {
        return headerValues.get(defaultHeaderValueIndex);
    }
}

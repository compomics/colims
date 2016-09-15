package com.compomics.colims.distributed.io.maxquant.headers;

import java.util.EnumMap;

/**
 * Created by Iain on 02/12/2014.
 */
public class MaxQuantEvidenceHeaders extends AbstractMaxQuantHeaders<MaxQuantEvidenceHeaders.Headers> {

    public enum Headers {
        ACETYL_K,
        ACETYL_K_PROBABILITIES,
        ACETYL_K_SCORE_DIFFS,
        ACETYL_PROTEIN_N_TERM,
        AIF_MS_MS_IDS,
        B_MIXTURE,
        BASE_PEAK_FRACTION,
        BEST_MS_MS,
        CALIBRATED_RETENTION_TIME,
        CALIBRATED_RETENTION_TIME_START,
        CALIBRATED_RETENTION_TIME_FINISH,
        CHARGE,
        COMBINATORICS,
        CONTAMINANT,
        DELTA_SCORE,
        EXPERIMENT,
        FASTA_HEADERS,
        FRACTION,
        FRACTION_OF_TOTAL_SPECTRUM,
        GENE_NAMES,
        ID,
        INTENSITY,
        INTENSITY_L,
        INTENSITY_M,
        INTENSITY_H,
        K_COUNT,
        LABELING_STATE,
        LEADING_PROTEINS,
        LEADING_RAZOR_PROTEIN,
        LENGTH,
        M_Z,
        MASS,
        MASS_ERROR_PPM,
        MAX_INTENSITY_M_Z_0,
        MAX_INTENSITY_M_Z_1,
        MATCH_TIME_DIFFERENCE,
        MOD_PEPTIDE_ID,
        MODIFICATIONS,
        MODIFIED_SEQUENCE,
        MS_MS_COUNT,
        MS_MS_IDS,
        MS_MS_M_Z,
        MS_MS_SCAN_NUMBER,
        NORMALIZED_RATIO,
        OXIDATION_M,
        OXIDATION_M_PROBABILITIES,
        OXIDATION_M_SCORE_DIFFS,
        OXIDATION_M_SITE_IDS,
        PEP,
        PEPTIDE_ID,
        PIF,
        PROTEIN_NAMES,
        PROTEIN_DESCRIPTIONS,
        PROTEIN_GROUP_IDS,
        PROTEINS,
        RATIO_HL,
        RATIO_NORMALIZED_HL,
        RATIO_SHIFT_HL,
        RAW_FILE,
        RESOLUTION,
        RETENTION_LENGTH,
        RETENTION_TIME,
        RETENTION_TIME_CALIBRATION,
        REVERSE,
        SCORE,
        SEQUENCE,
        TYPE,
        UNCALIBRATED_CALIBRATED_M_Z_PPM,
        UNCALIBRATED_MASS_ERROR_PPM,
        UNIPROT
    }

    public MaxQuantEvidenceHeaders() {
        super(Headers.class, new EnumMap<>(Headers.class), "maxquant/evidence_headers.txt");
    }

    public static void main(String[] args) {
        MaxQuantEvidenceHeaders maxQuantEvidenceHeaders = new MaxQuantEvidenceHeaders();
    }
}

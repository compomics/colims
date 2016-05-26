package com.compomics.colims.distributed.io.maxquant.headers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by Iain on 03/12/2014.
 */
public enum MaxQuantParameterHeaders implements HeaderEnum {

    ADVANCED_RATIOS(new String[]{"Advanced ratios"}),
    AIF_CORRELATION(new String[]{"AIF correlation"}),
    AIF_ISOBAR_WEIGHT(new String[]{"AIF ISO weight"}),
    AIF_ITERATIVE(new String[]{"AIF iterative"}),
    AIF_MIN_MASS(new String[]{"AIF min mass"}),
    AIF_SIL_WEIGHT(new String[]{"AIF SIL weight"}),
    AIF_THRESHOLD_FDR(new String[]{"AIF threshold FDR"}),
    AIF_TOPX(new String[]{"AIF topx"}),
    CUT_PEAKS(new String[]{"Cut peaks"}),
    DISCARD_UNMOD_COUNTERPART_PEPTIDES(new String[]{"Discard unmodified counterpart peptides"}),
    DIGEST_AA(new String[]{"Special AAs"}),
    FASTA_FILE(new String[]{"Fasta file"}),
    FIND_DEPENDENT_PEPTIDES(new String[]{"Find dependent peptides"}),
    FIRST_PASS_AIF_CORRELATION(new String[]{"First pass AIF correlation"}),
    FIRST_SEARCH_FASTA_FILE(new String[]{"First search fasta file"}),
    FIXED_MODIFICATIONS(new String[]{"Fixed modifications"}),
    FTMS_DEISOTOPING(new String[]{"MS/MS deisotoping (FTMS)"}),
    FTMS_MS_MS_TOLERANCE(new String[]{"MS/MS tol. (FTMS)"}),
    FTMS_PEAKS_PER_100_DALTON(new String[]{"Top MS/MS peaks per 100 Da. (FTMS)"}),
    IBAQ(new String[]{"iBAQ"}),
    INCLUDE_CONTAMINANTS(new String[]{"Include contaminants"}),
    ITMS_DEISOTOPING(new String[]{"MS/MS deisotoping (ITMS)"}),
    ITMS_MS_MS_TOLERANCE(new String[]{"MS/MS tol. (ITMS)"}),
    ITMS_PEAKS_PER_100_DALTON(new String[]{"Top MS/MS peaks per 100 Da. (ITMS)"}),
    KEEP_LOW_SCORING_PEPTIDES(new String[]{"Keep low-scoring versions of identified peptides"}),
    LABEL_FREE(new String[]{"Label-free protein quantification"}),
    LABELED_AA_FILTERING(new String[]{"Labeled amino acid filtering"}),
    LFQ_MIN_RATIO_COUNT(new String[]{"Lfq min. ratio count"}),
    LOG_IBAQ(new String[]{"iBAQ log fit"}),
    MATCH_BETWEEN_RUNS(new String[]{"Match between runs"}),
    MAX_PEP_PEP(new String[]{"Max. peptide PEP"}),
    MIN_PEP_COUNT_FOR_RATIO(new String[]{"Min. ratio count"}),
    MIN_PEP_LENGTH(new String[]{"Min. peptide Length"}),
    MIN_PEPTIDES(new String[]{"Min. peptides"}),
    MIN_RAZOR_PEPTIDES(new String[]{"Min. razor peptides"}),
    MIN_SCORE(new String[]{"Min. score"}),
    MIN_UNIQUE_PEPTIDES(new String[]{"Min. unique peptides"}),
    MOD_SITE_HEADERS(new String[]{"Site tables"}),
    NORMALIZED_OCCUPANCY_RATIOS(new String[]{"Use Normalized Ratios For Occupancy"}),
    PEP_FDR(new String[]{"FDR Peptide"}),
    PEPTIDES_USE_FOR_QUANT(new String[]{"Peptides used for protein quantification"}),
    PROTEIN_FDR(new String[]{"Protein FDR"}),
    RANDOMIZE(new String[]{"Randomize"}),
    RE_QUANTIFY(new String[]{"Re-quantify"}),
    RECAL_MS_MS(new String[]{"MS/MS recalibration"}),
    RETENTION_TIME_SHIFT(new String[]{"RT shift"}),
    SEPARATE_SITE_FDR(new String[]{"Apply site FDR separately"}),
    SITE_FDR(new String[]{"Site FDR"}),
    SITE_QUANTITATION_METHOD(new String[]{"Site quantification"}),
    TOF_DEISOTOPING(new String[]{"MS/MS deisotoping (TOF)"}),
    TOF_MS_MS_TOLERANCE(new String[]{"MS/MS tol. (TOF)"}),
    TOF_PEAKS_PER_100_DALTON(new String[]{"Top MS/MS peaks per 100 Da. (TOF)"}),
    TIME_WINDOW_IN_MINUTES(new String[]{"Time window [min]"}),
    UNKNOWN_DEISOTOPING(new String[]{"MS/MS deisotoping (Unknown)"}),
    UNKNOWN_MS_MS_TOLERANCE(new String[]{"MS/MS tol. (Unknown)"}),
    UNKNOWN_PEAKS_PER_100_DALTON(new String[]{"Top MS/MS peaks per 100 Da. (Unknown)"}),
    USE_UNMOD(new String[]{"Use only unmodified peptides and"}),
    USE_PEPTIDES_MODDED_WITH(new String[]{"Modifications included in protein quantification"}),
    VERSION(new String[]{"Version"});

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
    MaxQuantParameterHeaders(final String[] headerValueArray) {

        headerValues = new ArrayList<>(headerValueArray.length);
        Arrays.stream(headerValueArray).forEach(e -> headerValues.add(e.toLowerCase(Locale.US)));
    }

    @Override
    public List<String> getPossibleValues() {
        return this.headerValues;
    }

    @Override
    public void setParsedValue(int index) {
        defaultHeaderValueIndex = index;
    }

    @Override
    public String getValue() {
        return headerValues.get(defaultHeaderValueIndex);
    }
}

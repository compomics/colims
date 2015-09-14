package com.compomics.colims.distributed.io.maxquant.headers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by Iain on 03/12/2014.
 */
public enum MaxQuantMSMSHeaders implements HeaderEnum {
    ACETYL_PROTEIN_N_TERM(new String[]{"Acetyl (Protein N-term)"}),
    BASE_PEAK_FRACTION(new String[]{"Base peak fraction"}),
    CHARGE(new String[]{"Charge"}),
    COMBINATORICS(new String[]{"Combinatorics"}),
    DELTA_SCORE(new String[]{"Delta score"}),
    EVIDENCE_ID(new String[]{"Evidence ID"}),
    FRACTION_OF_TOTAL_SPECTRUM(new String[]{"Fraction of total spectrum"}),
    FRAGMENTATION(new String[]{"Fragmentation"}),
    GENE_NAMES(new String[]{"Gene Names"}),
    ID(new String[]{"id"}),
    INTENSITIES(new String[]{"Intensities"}),
    INTENSITY_COVERAGE(new String[]{"Intensity coverage"}),
    ISOTOPE_INDEX(new String[]{"Isotope Index"}),
    LENGTH(new String[]{"Length"}),
    M_Z(new String[]{"m/z"}),
    MASS(new String[]{"Mass"}),
    MASSES(new String[]{"Masses"}),
    MASS_ANALYZER(new String[]{"Mass analyzer"}),
    MASS_DEVIATIONS_DA(new String[]{"Mass Deviations [Da]"}),
    MASS_DEVIATIONS_PPM(new String[]{"Mass Deviations [ppm]"}),
    MASS_ERROR_PPM(new String[]{"Mass Error [ppm]"}),
    MATCHES(new String[]{"Matches"}),
    MISSED_CLEAVAGES(new String[]{"Missed Cleavages"}),
    MOD_PEPTIDE_ID(new String[]{"Mod. Peptide ID"}),
    MODIFICATIONS(new String[]{"Modifications"}),
    MODIFIED_SEQUENCE(new String[]{"Modified Sequence"}),
    NEUTRAL_LOSS_LEVEL(new String[]{"Neutral loss level"}),
    NUMBER_OF_MATCHES(new String[]{"Number of Matches"}),
    OXIDATION_M(new String[]{"Oxidation (M)"}),
    OXIDATION_M_PROBABILITIES(new String[]{"Oxidation (M) Probabilities"}),
    OXIDATION_M_SCORE_DIFFS(new String[]{"Oxidation (M) Score Diffs"}),
    OXIDATION_M_SITE_IDS(new String[]{"Oxidation (M) Site IDs"}),
    PEAK_COVERAGE(new String[]{"Peak coverage"}),
    PEP(new String[]{"PEP"}),
    PEPTIDE_ID(new String[]{"Peptide ID"}),
    PIF(new String[]{"PIF"}),
    PRECURSOR(new String[]{"Precursor"}),
    PRECURSOR_APEX_FRACTION(new String[]{"Precursor Apex Fraction"}),
    PRECURSOR_APEX_OFFSET(new String[]{"Precursor Apex Offset"}),
    PRECURSOR_FULL_SCANNUMBER(new String[]{"Precursor Full ScanNumber"}),
    PRECURSOR_INTENSITY(new String[]{"Precursor Intensity"}),
    PROTEINS(new String[]{"Proteins"}),
    PROTEIN_GROUP_IDS(new String[]{"Protein Group IDs"}),
    PROTEIN_NAMES(new String[]{"Protein Names"}),
    RAW_FILE(new String[]{"Raw File"}),
    RETENTION_TIME(new String[]{"Retention Time"}),
    REVERSE(new String[]{"Reverse"}),
    SCAN_EVENT_NUMBER(new String[]{"Scan event number"}),
    SCAN_TYPE(new String[]{"Scan Type"}),
    SCAN_NUMBER(new String[]{"Scan Number"}),
    SCORE(new String[]{"Score"}),
    SEQUENCE(new String[]{"Sequence"}),
    SIMPLE_MASS_ERROR_PPM(new String[]{"Simple Mass Error [ppm]"}),
    TYPE(new String[]{"Type"}),
    UNIPROT(new String[]{"Uniprot"});

    List<String> columnNames;
    int standardColumnNameIndex = 0;

    /**
     * Private constructor.
     *
     * @param fieldnames the array of field names
     */
    MaxQuantMSMSHeaders(final String[] fieldnames) {

        columnNames = new ArrayList<>(fieldnames.length);
        Arrays.stream(fieldnames).forEach(e -> columnNames.add(e.toLowerCase(Locale.US)));
    }

    @Override
    public List<String> allPossibleColumnNames() {
        return this.columnNames;
    }

    @Override
    public void setColumnNameNumber(int columnNameNumber) {
        standardColumnNameIndex = columnNameNumber;
    }

    @Override
    public String getColumnName(int columnNameNumber) {
        if(columnNameNumber < 0  || columnNameNumber > columnNames.size()){
            return columnNames.get(0);
        }
        return columnNames.get(columnNameNumber);
    }


    @Override
    public String getDefaultColumnName() {
        return columnNames.get(standardColumnNameIndex);
    }
}
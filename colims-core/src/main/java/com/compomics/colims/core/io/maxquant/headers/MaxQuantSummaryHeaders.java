package com.compomics.colims.core.io.maxquant.headers;

import java.util.Locale;

/**
 * Created by Iain on 03/12/2014.
 */
public enum MaxQuantSummaryHeaders implements HeaderEnum {
    AV_ABSOLUTE_MASS_DEVIATION(new String[]{"Av. Absolute Mass Deviation"}),
    FIXED_MODIFICATIONS(new String[]{"Fixed modifications"}),
    INSTRUMENT(new String[]{"Instrument"}),
    ISOTOPE_PATTERNS(new String[]{"Isotope Patterns"}),
    ISOTOPE_PATTERNS_SEQUENCED(new String[]{"Isotope Patterns Sequenced"}),
    ISOTOPE_PATTERNS_SEQUENCED_PERCENTAGE(new String[]{"Isotope Patterns Sequenced [%]"}),
    ISOTOPE_PATTERNS_SEQUENCED_Z_1(new String[]{"Isotope Patterns Sequenced (z>1)"}), // NUMBER_OF_MULTIPLE_CHARGED_SEQUENCED_ISOTOPE_PATTERNS
    ISOTOPE_PATTERNS_SEQUENCED_Z_1_PERCENTAGE(new String[]{"Isotope Patterns Sequenced (z>1) [%]"}),
    ISOTOPE_PATTERNS_REPEATEDLY_SEQUENCED(new String[]{"Isotope Patterns Repeatedly Sequenced"}),
    ISOTOPE_PATTERNS_REPEATEDLY_SEQUENCED_PERCENTAGE(new String[]{"Isotope Patterns Repeatedly Sequenced [%]"}),
    LABEL_FREE_INTENSITY_NORM_PARAM(new String[]{"Label free norm param"}), // LABEL_FREE_INTENSITY_NORMALISATION_FACTOR
    LABELS0(new String[]{"Labels0"}),  // N_TERMINUS_LABEL
    LC_MS_RUN_TYPE(new String[]{"LC-MS run type"}),
    MASS_STANDARD_DEVIATION(new String[]{"Mass Standard Deviation"}),
    MAX_MISSED_CLEAVAGES(new String[]{"Max. missed cleavages"}),
    MS(new String[]{"MS"}), // NUMBER_OF_MS_SCANS
    MS_MS(new String[]{"MS/MS"}), // NUMBER_OF_MS_MS_SCANS
    MS_MS_IDENTIFIED(new String[]{"MS/MS Identified"}),
    MS_MS_IDENTIFIED_ISO(new String[]{"MS/MS Identified (ISO)"}),
    MS_MS_IDENTIFIED_ISO_PERCENTAGE(new String[]{"MS/MS Identified (ISO) [%]"}),
    MS_MS_IDENTIFIED_PEAK(new String[]{"MS/MS Identified (PEAK)"}),
    MS_MS_IDENTIFIED_PEAK_PERCENTAGE(new String[]{"MS/MS Identified (PEAK) [%]"}),
    MS_MS_IDENTIFIED_PERCENTAGE(new String[]{"MS/MS Identified [%]"}),
    MS_MS_IDENTIFIED_SIL(new String[]{"MS/MS Identified (SIL)"}),
    MS_MS_IDENTIFIED_SIL_PERCENTAGE(new String[]{"MS/MS Identified (SIL) [%]"}),
    MS_MS_ON_POLYMERS(new String[]{"MS/MS on Polymers"}),
    MS_MS_SUBMITTED(new String[]{"MS/MS Submitted"}),
    MS_MS_SUBMITTED_ISO(new String[]{"MS/MS Submitted (ISO)"}),
    MS_MS_SUBMITTED_PEAK(new String[]{"MS/MS Submitted (PEAK)"}),
    MS_MS_SUBMITTED_SIL(new String[]{"MS/MS Submitted (SIL)"}),
    MULTIPLICITY(new String[]{"Multiplicity"}),
    PEAKS(new String[]{"Peaks"}),
    PEAKS_REPEATEDLY_SEQUENCED(new String[]{"Peaks Repeatedly Sequenced"}),
    PEAKS_REPEATEDLY_SEQUENCED_PERCENTAGE(new String[]{"Peaks Repeatedly Sequenced [%]"}),
    PEAKS_SEQUENCED(new String[]{"Peaks Sequenced"}),
    PEAKS_SEQUENCED_PERCENTAGE(new String[]{"Peaks Sequenced [%]"}),
    PEPTIDE_SEQUENCES_IDENTIFIED(new String[]{"Peptide Sequences Identified"}),
    PROTEASE(new String[]{"Protease"}),
    PROTEASE_FIRST_SEARCH(new String[]{"Protease first search"}),
    RAW_FILE(new String[]{"Raw file"}),
    RECALIBRATED(new String[]{"Recalibrated"}),
    TIME_DEPENDENT_CALIBRATION(new String[]{"Time-dependent recalibration"}),
    TUNE_FILE(new String[]{"Tune file"}),
    USE_PROTEASE_FIRST_SEARCH(new String[]{"Use protease first search"}),
    USE_VARIABLE_MODIFICATIONS_FIRST_SEARCH(new String[]{"Use variable modifications first search"}),
    VARIABLE_MODIFICATIONS(new String[]{"Variable modifications"}),
    VARIABLE_MODIFICATIONS_FIRST_SEARCH(new String[]{"Variable modifications first search"});

    String[] columnNames;

    MaxQuantSummaryHeaders(final String[] fieldnames) {
        this.columnNames = fieldnames;
    }

    @Override
    public String[] allPossibleColumnNames() {
        return this.columnNames;
    }

    @Override
    public void setColumnReference(int columnReference) {

    }

    @Override
    public String getColumnName() throws HeaderEnumNotInitialisedException {
        return columnNames[0].toLowerCase(Locale.US);
    }
}

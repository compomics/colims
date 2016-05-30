package com.compomics.colims.distributed.io.maxquant;

/**
 * This enum class contains some MaxQuant constants (directory names, etc...).
 * <p/>
 * Created by Niels Hulstaert on 14/01/16.
 */
public enum MaxQuantConstants {

    /**
     * The andromeda directory name.
     */
    ANDROMEDA_DIRECTORY("andromeda"),
    /**
     * The txt directory name.
     */
    TXT_DIRECTORY("txt"),
    /**
     * The msms file name.
     */
    SUMMARY_FILE("summary.txt"),
    /**
     * The msms file name.
     */
    PARAMETERS_FILE("parameters.txt"),
    /**
     * The msms file name.
     */
    MSMS_FILE("msms.txt"),
    /**
     * The evidence file name.
     */
    EVIDENCE_FILE("evidence.txt"),
    /**
     * The proteinGroups file name.
     */
    PROTEIN_GROUPS_FILE("proteinGroups.txt"),
    /**
     * The apl summary file name. This file contains the names of the different apl files (spectrum files) and the
     * matching parameter files.
     */
    APL_SUMMARY_FILE("aplfiles"),
    /**
     * The tab delimiter in MaxQuant parameter files.
     */
    PARAM_TAB_DELIMITER("\t"),
    /**
     * The equals sign delimiter in MaxQuant parameter files.
     */
    PARAM_EQUALS_DELIMITER("=");

    public enum Analyzer {

        /**
         * Fourier transform ion cyclotron resonance mass spectrometer.
         */
        FTMS("FTMS"),
        /**
         * Ion trap mass spectrometer.
         */
        ITMS("ITMS"),
        /**
         * Time-of-Flight mass spectrometer.
         */
        TOF("TOF"),
        /**
         * Time-of-Flight mass spectrometer.
         */
        UNKNOWN("Unknown");

        /**
         * The constant value;
         */
        private String value;

        /**
         * Constructor.
         *
         * @param value the constant value
         */
        Analyzer(String value) {
            this.value = value;
        }

        /**
         * Get the constant value.
         *
         * @return the constant value
         */
        public String value() {
            return value;
        }
    }

    /**
     * The constant value;
     */
    private String value;

    /**
     * Constructor.
     *
     * @param value the constant value
     */
    MaxQuantConstants(String value) {
        this.value = value;
    }

    /**
     * Get the constant value.
     *
     * @return the constant value
     */
    public String value() {
        return value;
    }
}

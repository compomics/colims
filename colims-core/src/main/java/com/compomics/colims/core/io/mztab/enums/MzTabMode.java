package com.compomics.colims.core.io.mztab.enums;

/**
 * The mzTab mode enum.
 * <p/>
 * Created by niels on 6/03/15.
 */
public enum MzTabMode {

    /**
     * Used to report  final results (e.g. quantification data at the level of study variables).
     */
    SUMMARY("Summary"),
    /**
     * Used if all quantification data is provided (e.g. quantification on the assay level and on the study variable
     * level).
     */
    COMPLETE("Complete");

    /**
     * How the enum will be shown in the GUI and in the mzTab file.
     */
    private final String mzTabName;

    /**
     * Constructor.
     *
     * @param mzTabName the enum name as shown in the GUI and in the mzTab file.
     */
    private MzTabMode(String mzTabName) {
        this.mzTabName = mzTabName;
    }

    public String mzTabName() {
        return mzTabName;
    }
}

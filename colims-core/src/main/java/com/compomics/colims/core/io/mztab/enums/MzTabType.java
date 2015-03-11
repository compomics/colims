package com.compomics.colims.core.io.mztab.enums;

/**
 * The mzTab type enum.
 * <p/>
 * Created by niels on 6/03/15.
 */
public enum MzTabType {

    /**
     * Used to report raw peptide, protein and small molecule identifications.
     */
    IDENTIFICATION("Identification"),
    /**
     * Used for quantification results (which optionally might contain identification results about the quantified
     * protein/peptide or small molecules).
     */
    QUANTIFICATION("Quantification");

    /**
     * How the enum will be shown in the GUI and in the mzTab file.
     */
    private final String mzTabName;

    /**
     * Constructor.
     *
     * @param mzTabName the enum name as shown in the GUI and in the mzTab file.
     */
    private MzTabType(String mzTabName) {
        this.mzTabName = mzTabName;
    }

    public String mzTabName() {
        return mzTabName;
    }

    /**
     * Get the MzTabType enum by its mzTab name. Return null if no
     * enum value could be matched.
     *
     * @param mzTabName the mzTab name value
     * @return the MzTabType enum value
     */
    public static MzTabType getByMzTabName(final String mzTabName) {
        MzTabType foundMzTabType = null;

        //iterate over enum values
        for (MzTabType mzTabType : values()) {
            if (mzTabType.mzTabName().equals(mzTabName)) {
                foundMzTabType = mzTabType;
            }
        }

        return foundMzTabType;
    }
}

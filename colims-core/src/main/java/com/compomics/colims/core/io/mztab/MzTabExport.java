package com.compomics.colims.core.io.mztab;

import com.compomics.colims.core.io.mztab.enums.MzTabMode;
import com.compomics.colims.core.io.mztab.enums.MzTabType;

/**
 * This wrapper class contains all necessary information (provided by the user) to export and write a mzTab file.
 * <p/>
 * Created by niels on 6/03/15.
 */
public class MzTabExport {

    private static final String STUDY_VARIABLE_ASSAY_REFS = "study_variable[%d]-assay_refs";
    private static final String STUDY_VARIABLE_DESCRIPTION = "study_variable[%d]-description";

    /**
     * The mzTab mode.
     */
    private MzTabMode mzTabMode;
    /**
     * The mzTab type.
     */
    private MzTabType mzTabType;



}

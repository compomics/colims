package com.compomics.colims.core.io.mztab;

import com.compomics.colims.core.io.mztab.enums.MzTabMode;
import com.compomics.colims.core.io.mztab.enums.MzTabType;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Sample;

import java.util.List;

/**
 * This wrapper class contains all necessary information (provided by the user)
 * to write and export an mzTab file.
 * <p/>
 * Created by niels on 6/03/15.
 */
public class MzTabExport {

    /**
     * The title of the mzTab file.
     */
    private String title;
    /**
     * The mzTab mode.
     */
    private MzTabMode mzTabMode;
    /**
     * The mzTab type.
     */
    private MzTabType mzTabType;
    /**
     * The description String.
     */
    private String description;
    /**
     * The samples to export.
     */
    private List<Sample> samples;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MzTabMode getMzTabMode() {
        return mzTabMode;
    }

    public void setMzTabMode(MzTabMode mzTabMode) {
        this.mzTabMode = mzTabMode;
    }

    public MzTabType getMzTabType() {
        return mzTabType;
    }

    public void setMzTabType(MzTabType mzTabType) {
        this.mzTabType = mzTabType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Sample> getSamples() {
        return samples;
    }

    public void setSamples(List<Sample> samples) {
        this.samples = samples;
    }
}

package com.compomics.colims.core.io.mztab;

import com.compomics.colims.core.io.mztab.enums.MzTabMode;
import com.compomics.colims.core.io.mztab.enums.MzTabType;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Sample;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This wrapper class contains all necessary information (provided by the user)
 * to write and export an mzTab file.
 * <p/>
 * Created by niels on 6/03/15.
 */
public class MzTabExport {

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
     * The mzTab export file.
     */
    private File mzTabFile;
    /**
     * The FASTA files directory.
     */
    private Path fastaDirectory;
    /**
     * The samples to export.
     */
    private List<Sample> samples = new ArrayList<>();
    /**
     * The runs to export.
     */
    private List<AnalyticalRun> runs = new ArrayList<>();
    /**
     * Link between study variables and assays.
     */
    private Map<String, int[]> studyVariablesAssaysRefs = new HashMap<>();
    /**
     * Link between analytical runs and assays.
     */
    private Map<AnalyticalRun, int[]> analyticalRunsAssaysRefs = new HashMap<>();
    /**
     * Match between quantification labels and Reagents (key: reagent, value:
     * label)
     */
    private Map<String, String> quantificationReagentLabelMatch = new HashMap<>();

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

    public File getMzTabFile() {
        return mzTabFile;
    }

    public void setMzTabFile(File mzTabFile) {
        this.mzTabFile = mzTabFile;
    }    

    public Path getFastaDirectory() {
        return fastaDirectory;
    }

    public void setFastaDirectory(Path fastaDirectory) {
        this.fastaDirectory = fastaDirectory;
    }

    public List<Sample> getSamples() {
        return samples;
    }

    public void setSamples(List<Sample> samples) {
        this.samples = samples;
    }

    public List<AnalyticalRun> getRuns() {
        return runs;
    }

    public void setRuns(List<AnalyticalRun> runs) {
        this.runs = runs;
    }

    public Map<String, int[]> getStudyVariablesAssaysRefs() {
        return studyVariablesAssaysRefs;
    }

    public void setStudyVariablesAssaysRefs(Map<String, int[]> studyVariablesAssaysRefs) {
        this.studyVariablesAssaysRefs = studyVariablesAssaysRefs;
    }

    public Map<AnalyticalRun, int[]> getAnalyticalRunsAssaysRefs() {
        return analyticalRunsAssaysRefs;
    }

    public void setAnalyticalRunsAssaysRefs(Map<AnalyticalRun, int[]> analyticalRunsAssaysRefs) {
        this.analyticalRunsAssaysRefs = analyticalRunsAssaysRefs;
    }

    public Map<String, String> getQuantificationReagentLabelMatch() {
        return quantificationReagentLabelMatch;
    }

    public void setQuantificationReagentLabelMatch(Map<String, String> quantificationReagentLabelMatch) {
        this.quantificationReagentLabelMatch = quantificationReagentLabelMatch;
    }

}

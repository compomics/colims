package com.compomics.colims.core.io;

import com.compomics.colims.model.enums.FastaDbType;
import java.io.File;
import java.util.EnumMap;
import java.util.List;

/**
 * The class represents the archived PeptideShaker .cps file and all other
 * objects necessary for importing the PS experiment.
 *
 * @author Niels Hulstaert
 */
public class PeptideShakerImport extends DataImport {

    private static final long serialVersionUID = 6181185980673938925L;

    /**
     * The PeptideShaker .cps file.
     */
    private File peptideShakerCpsArchive;
    /**
     * The list of mgf files.
     */
    private List<File> mgfFiles;

    /**
     * No-arg constructor.
     */
    public PeptideShakerImport() {
    }

    /**
     * Constructor.
     *
     * @param peptideShakerCpsArchive the PeptideShaker .cps file
     * @param fastaDbIds the FASTA database IDs map
     * @param mgfFiles the list of MGF files
     */
    public PeptideShakerImport(final File peptideShakerCpsArchive, final EnumMap<FastaDbType, Long> fastaDbIds, final List<File> mgfFiles) {
        super(fastaDbIds);
        this.peptideShakerCpsArchive = peptideShakerCpsArchive;
        this.mgfFiles = mgfFiles;
    }

    public File getPeptideShakerCpsArchive() {
        return peptideShakerCpsArchive;
    }

    public void setPeptideShakerCpsArchive(File peptideShakerCpsArchive) {
        this.peptideShakerCpsArchive = peptideShakerCpsArchive;
    }

    public List<File> getMgfFiles() {
        return mgfFiles;
    }

    public void setMgfFiles(List<File> mgfFiles) {
        this.mgfFiles = mgfFiles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        PeptideShakerImport that = (PeptideShakerImport) o;

        if (peptideShakerCpsArchive != null ? !peptideShakerCpsArchive.equals(that.peptideShakerCpsArchive) : that.peptideShakerCpsArchive != null) {
            return false;
        }
        return !(mgfFiles != null ? !mgfFiles.equals(that.mgfFiles) : that.mgfFiles != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (peptideShakerCpsArchive != null ? peptideShakerCpsArchive.hashCode() : 0);
        result = 31 * result + (mgfFiles != null ? mgfFiles.hashCode() : 0);
        return result;
    }
}

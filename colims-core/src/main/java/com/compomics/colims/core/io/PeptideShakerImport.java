package com.compomics.colims.core.io;

import com.compomics.colims.model.enums.FastaDbType;

import java.nio.file.Path;
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
     * The PeptideShaker .cpsx file.
     */
    private Path peptideShakerCpsxArchive;
    /**
     * The list of mgf files.
     */
    private List<Path> mgfFiles;

    /**
     * No-arg constructor.
     */
    public PeptideShakerImport() {
    }

    /**
     * Constructor.
     *
     * @param peptideShakerCpsxArchive the PeptideShaker .cps file
     * @param fastaDbIds               the FASTA database IDs map
     * @param mgfFiles                 the list of MGF files
     */
    public PeptideShakerImport(final Path peptideShakerCpsxArchive, final EnumMap<FastaDbType, List<Long>> fastaDbIds, final List<Path> mgfFiles) {
        super(fastaDbIds);
        this.peptideShakerCpsxArchive = peptideShakerCpsxArchive;
        this.mgfFiles = mgfFiles;
    }

    public Path getPeptideShakerCpsxArchive() {
        return peptideShakerCpsxArchive;
    }

    public void setPeptideShakerCpsxArchive(Path peptideShakerCpsxArchive) {
        this.peptideShakerCpsxArchive = peptideShakerCpsxArchive;
    }

    public List<Path> getMgfFiles() {
        return mgfFiles;
    }

    public void setMgfFiles(List<Path> mgfFiles) {
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

        if (peptideShakerCpsxArchive != null ? !peptideShakerCpsxArchive.equals(that.peptideShakerCpsxArchive) : that.peptideShakerCpsxArchive != null) {
            return false;
        }
        return !(mgfFiles != null ? !mgfFiles.equals(that.mgfFiles) : that.mgfFiles != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (peptideShakerCpsxArchive != null ? peptideShakerCpsxArchive.hashCode() : 0);
        result = 31 * result + (mgfFiles != null ? mgfFiles.hashCode() : 0);
        return result;
    }
}

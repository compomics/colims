package com.compomics.colims.client.model.tableformat;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import com.compomics.colims.core.service.PeptideService;
import com.compomics.colims.core.service.SpectrumService;
import com.compomics.colims.model.*;
import com.google.common.base.Joiner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
@Component
public class PsmTableFormat implements AdvancedTableFormat<Spectrum> {
    SpectrumService spectrumService;
    PeptideService peptideService;

    private static final String[] columnNames = {"ID", "Charge", "M/Z ratio", "Intensity", "Retention time", "Peptide sequence", "Confidence", "Protein accessions"};
    private static final String NOT_APPLICABLE = "N/A";

    public static final int SPECTRUM_ID = 0;
    public static final int PRECURSOR_CHARGE = 1;
    public static final int PRECURSOR_MZRATIO = 2;
    public static final int PRECURSOR_INTENSITY = 3;
    public static final int RETENTION_TIME = 4;
    public static final int PEPTIDE_SEQUENCE = 5;
    public static final int PSM_CONFIDENCE = 6;
    public static final int PROTEIN_ACCESSIONS = 7;

    public PsmTableFormat(SpectrumService spectrumService, PeptideService peptideService) {
        this.spectrumService = spectrumService;
        this.peptideService = peptideService;
    }

    public PsmTableFormat() {
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case SPECTRUM_ID:
                return Long.class;
            case PRECURSOR_CHARGE:
                return Integer.class;
            case PRECURSOR_MZRATIO:
                return Double.class;
            case PRECURSOR_INTENSITY:
                return Double.class;
            case RETENTION_TIME:
                return Double.class;
            case PEPTIDE_SEQUENCE:
                return String.class;
            case PSM_CONFIDENCE:
                return Double.class;
            case PROTEIN_ACCESSIONS:
                return String.class;
            default:
                throw new IllegalArgumentException("Unexpected column number " + column);
        }
    }

    @Override
    public Comparator getColumnComparator(int column) {
        return GlazedLists.comparableComparator();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getColumnValue(Spectrum spectrum, int column) {
        Peptide peptide = spectrumService.getRepresentativePeptide(spectrum);

        switch (column) {
            case SPECTRUM_ID:
                return spectrum.getId();
            case PRECURSOR_CHARGE:
                return spectrum.getCharge();
            case PRECURSOR_MZRATIO:
                return spectrum.getMzRatio();
            case PRECURSOR_INTENSITY:
                return spectrum.getIntensity();
            case RETENTION_TIME:
                return spectrum.getRetentionTime();
            case PEPTIDE_SEQUENCE:
                return (peptide != null) ? peptide.getSequence() : NOT_APPLICABLE;
            case PSM_CONFIDENCE:                
                double confidence = (peptide != null) ? 100.0 * (1 - peptide.getPsmPostErrorProbability()) : 0.0;
                if (confidence <= 0) {
                    confidence = 0;
                }
                return confidence;
            case PROTEIN_ACCESSIONS:
                return (peptide != null) ? getProteinAccessions(peptide) : NOT_APPLICABLE;
            default:
                throw new IllegalArgumentException("Unexpected column number " + column);
        }
    }

    /**
     * Get the protein accessions as a concatenated String.
     *
     * @param peptide the Peptide instance
     * @return the concatenated protein accessions
     */
    private String getProteinAccessions(Peptide peptide) {
        Joiner joiner = Joiner.on(", ");
        return joiner.join(peptideService.getProteinAccessionsForPeptide(peptide));
    }
}
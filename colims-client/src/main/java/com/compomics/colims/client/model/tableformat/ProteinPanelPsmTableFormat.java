package com.compomics.colims.client.model.tableformat;

import ca.odell.glazedlists.GlazedLists;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.Spectrum;

import java.util.Comparator;

/**
 * Spectrum table format for the protein panel
 *
 * Created by Iain on 27/07/2015.
 */
public class ProteinPanelPsmTableFormat extends PsmTableFormat {
    private static final String[] columnNames = {"ID", "Charge", "M/Z ratio", "Intensity", "Retention time", "Confidence"};

    public static final int SPECTRUM_ID = 0;
    public static final int PRECURSOR_CHARGE = 1;
    public static final int PRECURSOR_MZRATIO = 2;
    public static final int PRECURSOR_INTENSITY = 3;
    public static final int RETENTION_TIME = 4;
    public static final int PSM_CONFIDENCE = 5;

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
            case PSM_CONFIDENCE:
                return Double.class;
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
        Peptide peptide = (!spectrum.getPeptides().isEmpty()) ? spectrum.getPeptides().get(0) : null;

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
            case PSM_CONFIDENCE:
                double confidence = (peptide != null) ? 100.0 * (1 - peptide.getPsmPostErrorProbability()) : 0.0;
                if (confidence <= 0) {
                    confidence = 0;
                }
                return confidence;
            default:
                throw new IllegalArgumentException("Unexpected column number " + column);
        }
    }
}

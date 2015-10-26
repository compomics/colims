package com.compomics.colims.client.model.table.format;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.Spectrum;

import java.util.Comparator;

/**
 * Spectrum table format for the protein panel.
 * <p/>
 * Created by Iain on 27/07/2015.
 */
public class ProteinPanelPsmTableFormat implements AdvancedTableFormat<Peptide> {

    private static final String[] columnNames = {"ID", "Charge", "M/Z ratio", "Mass Error", "Confidence"};

    public static final int SPECTRUM_ID = 0;
    public static final int CHARGE = 1;
    public static final int PRECURSOR_MZRATIO = 2;
    public static final int MASS_ERROR = 3;
    public static final int PSM_CONFIDENCE = 4;

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case SPECTRUM_ID:
                return Long.class;
            case CHARGE:
                return Integer.class;
            case PRECURSOR_MZRATIO:
                return Double.class;
            case MASS_ERROR:
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
    public Object getColumnValue(Peptide peptide, int column) {
        Spectrum spectrum = peptide.getSpectrum();

        switch (column) {
            case SPECTRUM_ID:
                return spectrum.getId();
            case CHARGE:
                return peptide.getCharge() != null ? peptide.getCharge() : spectrum.getCharge();
            case PRECURSOR_MZRATIO:
                return spectrum.getMzRatio();
            case MASS_ERROR:
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

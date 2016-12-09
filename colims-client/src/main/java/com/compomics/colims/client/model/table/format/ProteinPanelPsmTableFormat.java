package com.compomics.colims.client.model.table.format;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import com.compomics.colims.client.renderer.PeptideSequenceRenderer;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.SearchParameters;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.enums.MassAccuracyType;
import com.compomics.util.experiment.biology.ions.PrecursorIon;
import com.compomics.util.experiment.identification.matches.IonMatch;
import com.compomics.util.experiment.massspectrometry.Charge;
import com.compomics.util.experiment.massspectrometry.Peak;

import java.util.Comparator;

/**
 * Spectrum table format for the protein panel.
 * <p/>
 * Created by Iain on 27/07/2015.
 */
public class ProteinPanelPsmTableFormat implements AdvancedTableFormat<Peptide> {

    private static final String[] COLUMN_NAMES = {"ID", "Sequence", "Charge", "M/Z ratio", "Mass Error", "Retention Time", "Confidence"};

    public static final int SPECTRUM_ID = 0;
    public static final int SEQUENCE = 1;
    public static final int PRECURSOR_CHARGE = 2;
    public static final int PRECURSOR_MZRATIO = 3;
    public static final int PRECURSOR_MASS_ERROR = 4;
    public static final int RETENTION_TIME = 5;
    public static final int PSM_CONFIDENCE = 6;

    /**
     * The search parameters of the given run. They hold the mass error accuracy
     * type.
     */
    private SearchParameters searchParameters;

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case SPECTRUM_ID:
                return String.class;
            case SEQUENCE:
                return String.class;
            case PRECURSOR_CHARGE:
                return Integer.class;
            case PRECURSOR_MZRATIO:
                return Double.class;
            case PRECURSOR_MASS_ERROR:
                return Double.class;
            case RETENTION_TIME:
                return Double.class;
            case PSM_CONFIDENCE:
                return Double.class;
            default:
                throw new IllegalArgumentException("Unexpected column number " + column);
        }
    }

    public void setSearchParameters(SearchParameters searchParameters) {
        this.searchParameters = searchParameters;
    }

    @Override
    public Comparator getColumnComparator(int column) {
        return GlazedLists.comparableComparator();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    @Override
    public Object getColumnValue(Peptide peptide, int column) {
        Spectrum spectrum = peptide.getSpectrum();
        int charge = peptide.getCharge() != null ? peptide.getCharge() : spectrum.getCharge();

        switch (column) {
            case SPECTRUM_ID:
                return spectrum.getId();
            case SEQUENCE:
                return PeptideSequenceRenderer.getAnnotatedHtmlSequence(peptide.getSequence(), peptide.getPeptideHasModifications());
            case PRECURSOR_CHARGE:
                return charge;
            case PRECURSOR_MZRATIO:
                return spectrum.getMzRatio();
            case PRECURSOR_MASS_ERROR:
                int sign = charge > 0 ? Charge.PLUS : Charge.MINUS;
                IonMatch ionMatch = new IonMatch(new Peak(spectrum.getMzRatio(), 0, 0), new PrecursorIon(peptide.getTheoreticalMass()), charge);
                boolean isPpm = searchParameters.getPrecMassToleranceUnit() == MassAccuracyType.PPM;
                return Math.abs(ionMatch.getError(isPpm));
            case RETENTION_TIME:
                return spectrum.getRetentionTime();
            case PSM_CONFIDENCE:
                double confidence = (peptide.getPsmPostErrorProbability() != null) ? 100.0 * (1 - peptide.getPsmPostErrorProbability()) : 0.0;
                if (confidence <= 0) {
                    confidence = 0;
                }
                return confidence;
            default:
                throw new IllegalArgumentException("Unexpected column number " + column);
        }
    }
}

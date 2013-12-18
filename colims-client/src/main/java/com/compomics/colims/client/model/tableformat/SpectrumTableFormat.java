package com.compomics.colims.client.model.tableformat;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import com.compomics.colims.model.Spectrum;
import java.util.Comparator;

/**
 *
 * @author Niels Hulstaert
 */
public class SpectrumTableFormat implements AdvancedTableFormat<Spectrum> {

    private static final String[] columnNames = {"Id", "Accession", "Precursor charge", "Precursor M/Z ratio", "Peptide sequence"};
    public static final int SPECTRUM_ID = 0;
    public static final int ACCESSION = 1;
    public static final int PRECURSOR_CHARGE = 2;
    public static final int PRECURSOR_MZRATIO = 3;
    public static final int PEPTIDE_SEQUENCE = 4;

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case SPECTRUM_ID:
                return Long.class;
            case ACCESSION:
                return String.class;
            case PRECURSOR_CHARGE:
                return Integer.class;
            case PRECURSOR_MZRATIO:
                return Double.class;
            case PEPTIDE_SEQUENCE:
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
        switch (column) {
            case SPECTRUM_ID:
                return spectrum.getId();
            case ACCESSION:
                return spectrum.getAccession();
            case PRECURSOR_CHARGE:
                return spectrum.getCharge();
            case PRECURSOR_MZRATIO:
                return spectrum.getMzRatio();
            case PEPTIDE_SEQUENCE:
                if (spectrum.getPeptides().isEmpty()) {
                    return "N/A";
                } else {
                    return spectrum.getPeptides().get(0).getSequence();
                }
            default:
                throw new IllegalArgumentException("Unexpected column number " + column);
        }
    }
}
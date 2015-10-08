package com.compomics.colims.client.model.tableformat;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import com.compomics.colims.client.model.PeptideTableRow;

import java.util.Comparator;

/**
 * Created by Iain on 23/06/2015.
 */
public class PeptideTableFormat implements AdvancedTableFormat<PeptideTableRow> {

    private static final String[] columnNames = {"Sequence", "Charge", "Spectra"};

    public static final int SEQUENCE = 0;
    public static final int CHARGE = 1;
    public static final int SPECTRA = 2;

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case SEQUENCE:
                return String.class;
            case CHARGE:
                return Integer.class;
            case SPECTRA:
                return Long.class;
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
    public Object getColumnValue(PeptideTableRow peptide, int column) {
        switch (column) {
            case SEQUENCE:
                return "<html>" + peptide.getAnnotatedSequence() + "</html>";
            case CHARGE:
                return peptide.getCharge();
            case SPECTRA:
                return peptide.getSpectrumCount();
            default:
                throw new IllegalArgumentException("Unexpected column number " + column);
        }
    }
}

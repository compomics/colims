package com.compomics.colims.client.model.tableformat;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.Protein;

import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Created by Iain on 23/06/2015.
 */
public class PeptideTableFormat implements AdvancedTableFormat<Peptide> {

    private static final String[] columnNames = {"ID"};

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0:
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
    public Object getColumnValue(Peptide peptide, int column) {
        switch (column) {
            case 0:
                return peptide.getId();
            default:
                throw new IllegalArgumentException("Unexpected column number " + column);
        }
    }
}
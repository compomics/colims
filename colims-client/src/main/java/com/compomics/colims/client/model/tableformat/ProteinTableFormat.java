package com.compomics.colims.client.model.tableformat;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import com.compomics.colims.model.Protein;

import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Created by Iain on 23/06/2015.
 */
public class ProteinTableFormat implements AdvancedTableFormat<Protein> {

    private static final String[] columnNames = {"ID", "Accession", "Sequence"};

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0:
                return Long.class;
            case 1:
                return String.class;
            case 2:
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
    public Object getColumnValue(Protein protein, int column) {
        switch (column) {
            case 0:
                return protein.getId();
            case 1:
                return protein.getProteinAccessions()
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            case 2:
                return protein.getSequence();
            default:
                throw new IllegalArgumentException("Unexpected column number " + column);
        }
    }
}

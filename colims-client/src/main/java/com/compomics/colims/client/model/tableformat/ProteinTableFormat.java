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

    public static final int ID = 0;
    public static final int ACCESSION = 1;
    public static final int SEQUENCE = 2;

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case ID:
                return Long.class;
            case ACCESSION:
                return String.class;
            case SEQUENCE:
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
            case ID:
                return protein.getId();
            case ACCESSION:
                return protein.getProteinAccessions()
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            case SEQUENCE:
                return protein.getSequence();
            default:
                throw new IllegalArgumentException("Unexpected column number " + column);
        }
    }
}

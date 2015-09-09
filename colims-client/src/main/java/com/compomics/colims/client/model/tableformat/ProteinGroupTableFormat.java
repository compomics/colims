package com.compomics.colims.client.model.tableformat;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import com.compomics.colims.model.Protein;
import com.compomics.colims.model.ProteinGroup;

import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Created by Iain on 23/06/2015.
 */
public class ProteinGroupTableFormat implements AdvancedTableFormat<ProteinGroup> {

    private static final String[] columnNames = {"ID", "Accessions", "Sequence"};

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
    public Object getColumnValue(ProteinGroup proteinGroup, int column) {
        switch (column) {
            case ID:
                return proteinGroup.getId();
            case ACCESSION:
                return proteinGroup.getProteinAccessions()
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            case SEQUENCE:
                return proteinGroup.getMainProtein().getSequence();
            default:
                throw new IllegalArgumentException("Unexpected column number " + column);
        }
    }
}

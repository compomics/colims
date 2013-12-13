package com.compomics.colims.client.model.format;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import com.compomics.colims.model.Sample;
import java.util.Comparator;

/**
 *
 * @author Niels Hulstaert
 */
public class SamplesTableFormat implements AdvancedTableFormat<Sample> {

    private static final String[] columnNames = {"Id", "Name", "Condition", "Number of runs"};
    public static final int SAMPLE_ID = 0;
    public static final int NAME = 1;
    public static final int CONDITION = 2;
    public static final int NUMBER_OF_RUNS = 3;

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case SAMPLE_ID:
                return Long.class;
            case NAME:
                return String.class;
            case CONDITION:
                return String.class;
            case NUMBER_OF_RUNS:
                return Integer.class;
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
        return 4;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getColumnValue(Sample sample, int column) {
        switch (column) {
            case SAMPLE_ID:
                return sample.getId();
            case NAME:
                return sample.getName();
            case CONDITION:
                return sample.getCondition();
            case NUMBER_OF_RUNS:
                return sample.getAnalyticalRuns().size();
            default:
                throw new IllegalArgumentException("Unexpected column number " + column);
        }
    }
}
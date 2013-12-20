package com.compomics.colims.client.model.tableformat;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import com.compomics.colims.model.Sample;
import java.text.SimpleDateFormat;
import java.util.Comparator;

/**
 *
 * @author Niels Hulstaert
 */
public class SampleSimpleTableFormat implements AdvancedTableFormat<Sample> {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    private static final String[] columnNames = {"Id", "Name", "# runs"};
    public static final int SAMPLE_ID = 0;
    public static final int NAME = 1;
    public static final int NUMBER_OF_RUNS = 2;

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case SAMPLE_ID:
                return Long.class;
            case NAME:
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
        return columnNames.length;
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
            case NUMBER_OF_RUNS:
                return sample.getAnalyticalRuns().size();
            default:
                throw new IllegalArgumentException("Unexpected column number " + column);
        }
    }
}
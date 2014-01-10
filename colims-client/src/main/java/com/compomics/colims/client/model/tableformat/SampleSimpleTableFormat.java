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
    private static final String[] COLUMN_NAMES = {"Id", "Name", "# runs"};
    public static final int SAMPLE_ID = 0;
    public static final int NAME = 1;
    public static final int NUMBER_OF_RUNS = 2;

    @Override
    public Class getColumnClass(final int column) {
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
    public Comparator getColumnComparator(final int column) {
        return GlazedLists.comparableComparator();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(final int column) {
        return COLUMN_NAMES[column];
    }

    @Override
    public Object getColumnValue(final Sample sample, final int column) {
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
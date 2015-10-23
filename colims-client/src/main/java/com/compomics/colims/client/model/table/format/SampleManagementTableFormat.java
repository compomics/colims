package com.compomics.colims.client.model.table.format;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import com.compomics.colims.model.Sample;
import java.text.SimpleDateFormat;
import java.util.Comparator;

/**
 *
 * @author Niels Hulstaert
 */
public class SampleManagementTableFormat implements AdvancedTableFormat<Sample> {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    private static final String[] COLUMN_NAMES = {"Id", "Name", "Condition", "Protocol", "Created", "# runs"};
    private static final String NOT_APPLICABLE = "N/A";
    public static final int SAMPLE_ID = 0;
    public static final int NAME = 1;
    public static final int CONDITION = 2;
    public static final int PROTOCOL = 3;
    public static final int CREATED = 4;
    public static final int NUMBER_OF_RUNS = 5;

    @Override
    public Class getColumnClass(final int column) {
        switch (column) {
            case SAMPLE_ID:
                return Long.class;
            case NAME:
                return String.class;
            case CONDITION:
                return String.class;
            case PROTOCOL:
                return String.class;
            case CREATED:
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
            case CONDITION:
                return (sample.getCondition() != null) ? sample.getCondition() : NOT_APPLICABLE;
            case PROTOCOL:
                return (sample.getProtocol() != null) ? sample.getProtocol().toString() : NOT_APPLICABLE;
            case CREATED:
                return DATE_FORMAT.format(sample.getCreationDate());        
            case NUMBER_OF_RUNS:
                return sample.getAnalyticalRuns().size();
            default:
                throw new IllegalArgumentException("Unexpected column number " + column);
        }
    }
}
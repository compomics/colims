package com.compomics.colims.client.model.format;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import com.compomics.colims.model.Experiment;
import com.compomics.colims.model.Project;
import java.util.Comparator;

/**
 *
 * @author Niels Hulstaert
 */
public class ExperimentsOverviewTableFormat implements AdvancedTableFormat<Experiment> {

    private static final String[] columnNames = {"Id", "Title", "Number", "Number of samples"};
    public static final int PROJECT_ID = 0;
    public static final int TITLE = 1;
    public static final int NUMBER = 2;
    public static final int NUMBER_OF_SAMPLES = 3;

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case PROJECT_ID:
                return Long.class;
            case TITLE:
                return String.class;
            case NUMBER:
                return Long.class;
            case NUMBER_OF_SAMPLES:
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
    public Object getColumnValue(Experiment experiment, int column) {
        switch (column) {
            case PROJECT_ID:
                return experiment.getId();
            case TITLE:
                return experiment.getTitle();
            case NUMBER:
                return experiment.getNumber();
            case NUMBER_OF_SAMPLES:
                return experiment.getSamples().size();
            default:
                throw new IllegalArgumentException("Unexpected column number " + column);
        }
    }
}
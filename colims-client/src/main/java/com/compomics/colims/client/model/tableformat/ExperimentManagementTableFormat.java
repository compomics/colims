package com.compomics.colims.client.model.tableformat;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import com.compomics.colims.model.Experiment;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 *
 * @author Niels Hulstaert
 */
public class ExperimentManagementTableFormat implements AdvancedTableFormat<Experiment> {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    private static final String[] columnNames = {"Id", "Title", "Number", "Created", "# samples"};
    private static final String NOT_APPLICABLE = "N/A";
    public static final int EXPERIMENT_ID = 0;
    public static final int TITLE = 1;
    public static final int NUMBER = 2;
    public static final int CREATED = 3;
    public static final int NUMBER_OF_SAMPLES = 4;

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case EXPERIMENT_ID:
                return Long.class;
            case TITLE:
                return String.class;
            case NUMBER:
                return Long.class;
            case CREATED:
                return String.class;    
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
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getColumnValue(Experiment experiment, int column) {
        switch (column) {
            case EXPERIMENT_ID:
                return experiment.getId();
            case TITLE:
                return experiment.getTitle();
            case NUMBER:
                return experiment.getNumber();
            case CREATED:
                return DATE_FORMAT.format(experiment.getCreationdate());    
            case NUMBER_OF_SAMPLES:
                return experiment.getSamples().size();
            default:
                throw new IllegalArgumentException("Unexpected column number " + column);
        }
    }
}
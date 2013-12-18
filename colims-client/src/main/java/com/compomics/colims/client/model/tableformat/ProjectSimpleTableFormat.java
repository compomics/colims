package com.compomics.colims.client.model.tableformat;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import com.compomics.colims.model.Project;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 *
 * @author Niels Hulstaert
 */
public class ProjectSimpleTableFormat implements AdvancedTableFormat<Project> {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    private static final String[] columnNames = {"Id", "Title", "Label", "# exp"};
    private static final String NOT_APPLICABLE = "N/A";
    public static final int PROJECT_ID = 0;
    public static final int TITLE = 1;
    public static final int LABEL = 2;
    public static final int NUMBER_OF_EXPERIMENTS = 3;

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case PROJECT_ID:
                return Long.class;
            case TITLE:
                return String.class;
            case LABEL:
                return String.class;
            case NUMBER_OF_EXPERIMENTS:
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
    public Object getColumnValue(Project project, int column) {
        switch (column) {
            case PROJECT_ID:
                return project.getId();
            case TITLE:
                return project.getTitle();
            case LABEL:
                return project.getLabel();
            case NUMBER_OF_EXPERIMENTS:
                return project.getExperiments().size();
            default:
                throw new IllegalArgumentException("Unexpected column number " + column);
        }
    }
}
package com.compomics.colims.client.model.table.format;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import com.compomics.colims.model.Project;
import java.util.Comparator;

/**
 *
 * @author Niels Hulstaert
 */
public class ProjectSimpleTableFormat implements AdvancedTableFormat<Project> {

    private static final String[] COLUMN_NAMES = {"Id", "Title", "Label", "# exp"};
    public static final int PROJECT_ID = 0;
    public static final int TITLE = 1;
    public static final int LABEL = 2;
    public static final int NUMBER_OF_EXPERIMENTS = 3;

    @Override
    public Class getColumnClass(final int column) {
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
    public Object getColumnValue(final Project project, final int column) {
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
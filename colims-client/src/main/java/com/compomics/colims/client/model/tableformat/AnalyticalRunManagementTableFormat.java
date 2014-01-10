package com.compomics.colims.client.model.tableformat;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import com.compomics.colims.client.spring.ApplicationContextProvider;
import com.compomics.colims.core.service.SpectrumService;
import com.compomics.colims.model.AnalyticalRun;
import java.text.SimpleDateFormat;
import java.util.Comparator;

/**
 *
 * @author Niels Hulstaert
 */
public class AnalyticalRunManagementTableFormat implements AdvancedTableFormat<AnalyticalRun> {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    private static final String[] COLUMN_NAMES = {"Id", "Name", "Start date", "Created", "# spectra"};
    public static final int RUN_ID = 0;
    public static final int NAME = 1;
    public static final int START_DATE = 2;
    public static final int CREATED = 3;
    public static final int NUMBER_OF_SPECTRA = 4;
    private SpectrumService spectrumService;

    public AnalyticalRunManagementTableFormat() {
        spectrumService = ApplicationContextProvider.getInstance().getApplicationContext().getBean("spectrumService", SpectrumService.class);
    }    
    
    @Override
    public Class getColumnClass(final int column) {
        switch (column) {
            case RUN_ID:
                return Long.class;
            case NAME:
                return String.class;
            case START_DATE:
                return String.class;
            case CREATED:
                return String.class;
            case NUMBER_OF_SPECTRA:
                return Long.class;
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
    public Object getColumnValue(final AnalyticalRun analyticalRun, final int column) {
        switch (column) {
            case RUN_ID:
                return analyticalRun.getId();
            case NAME:
                return analyticalRun.getName();
            case START_DATE:
                 String startDateString = (analyticalRun.getStartDate() != null) ? DATE_FORMAT.format(analyticalRun.getStartDate()) : "N/A";   
                 return startDateString;
            case CREATED:
                return DATE_FORMAT.format(analyticalRun.getCreationdate());
            case NUMBER_OF_SPECTRA:
                return spectrumService.countSpectraByAnalyticalRun(analyticalRun);
            default:
                throw new IllegalArgumentException("Unexpected column number " + column);
        }
    }
}
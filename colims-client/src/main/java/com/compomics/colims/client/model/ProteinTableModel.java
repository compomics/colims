package com.compomics.colims.client.model;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.gui.TableFormat;
import com.compomics.colims.client.model.tableformat.ProteinTableFormat;
import com.compomics.colims.core.config.ApplicationContextProvider;
import com.compomics.colims.core.service.ProteinService;
import com.compomics.colims.model.AnalyticalRun;

import java.util.List;

/**
 * Created by Iain on 19/06/2015.
 */
public class ProteinTableModel extends PagingTableModel {

    /**
     * The ProteinService instance.
     */
    private final ProteinService proteinService;

    public ProteinTableModel(EventList source, TableFormat tableFormat) {
        super(source, tableFormat);
        proteinService = ApplicationContextProvider.getInstance().getBean("proteinService");
    }

    @Override
    public String getColumnDbName(int column) {
        switch (column) {
            case ProteinTableFormat.ID:
                return "protein.id";
            case ProteinTableFormat.SEQUENCE:
                return "protein.protein_sequence";
            case ProteinTableFormat.ACCESSION:
                return "protein_accession.accession";
            default:
                throw new IllegalArgumentException("Unexpected column number " + column);
        }
    }

    /**
     * Updates the row count and returns a list of spectra for the given search parameters.
     *
     * @param analyticalRun The run from which spectra are queried
     * @return List of Spectrum objects
     */
    public List getRows(AnalyticalRun analyticalRun) {
        rowCount = proteinService.getProteinCountForRun(analyticalRun, filter);

        if (rowCount < page * perPage) {
            page = getMaxPage();
        }

        return proteinService.getPagedProteinsForRun(analyticalRun, page * perPage, perPage, sortColumn, sortDirection, filter);
    }

    public void reset(final AnalyticalRun analyticalRun) {
        super.reset(analyticalRun == null ? 0 : proteinService.getProteinCountForRun(analyticalRun, filter));
    }
}

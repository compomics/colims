package com.compomics.colims.client.model;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.gui.TableFormat;
import com.compomics.colims.client.model.tableformat.ProteinGroupTableFormat;
import com.compomics.colims.core.config.ApplicationContextProvider;
import com.compomics.colims.core.service.ProteinGroupService;
import com.compomics.colims.model.AnalyticalRun;

import java.util.List;

/**
 * Created by Iain on 19/06/2015.
 */
public class ProteinGroupTableModel extends PagingTableModel {

    /**
     * The ProteinService instance.
     */
    private final ProteinGroupService proteinGroupService;

    public ProteinGroupTableModel(EventList source, TableFormat tableFormat) {
        super(source, tableFormat);
        proteinGroupService = ApplicationContextProvider.getInstance().getBean("proteinGroupService");
    }

    @Override
    public String getColumnDbName(int column) {
        switch (column) {
            case ProteinGroupTableFormat.ID:
                return "protein_group.id";
            case ProteinGroupTableFormat.SEQUENCE:
                return "protein.protein_sequence";
            case ProteinGroupTableFormat.ACCESSION:
                return "protein_accession.accession";
            case ProteinGroupTableFormat.PEP:
                return "protein_group.protein_post_error_prob";
            case ProteinGroupTableFormat.PROBABILITY:
                return "protein_group.protein_prob";
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
        rowCount = proteinGroupService.getProteinGroupCountForRun(analyticalRun, filter);

        if (rowCount < page * perPage) {
            page = getMaxPage();
        }

        return proteinGroupService.getPagedProteinGroupsForRun(analyticalRun, page * perPage, perPage, sortColumn, sortDirection, filter);
    }

    public void reset(final AnalyticalRun analyticalRun) {
        super.reset(analyticalRun == null ? 0 : proteinGroupService.getProteinGroupCountForRun(analyticalRun, filter));
    }
}

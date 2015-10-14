package com.compomics.colims.client.model;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.gui.TableFormat;
import com.compomics.colims.client.model.tableformat.ProteinGroupTableFormat;
import com.compomics.colims.core.config.ApplicationContextProvider;
import com.compomics.colims.core.service.ProteinGroupService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.ProteinGroup;
import com.compomics.colims.repository.hibernate.model.ProteinGroupForRun;

import java.util.List;

/**
 * This class represents the paged protein group table model.
 * <p/>
 * Created by Iain on 19/06/2015.
 */
public class ProteinGroupTableModel extends PagingTableModel {

    /**
     * The ProteinGroupService instance.
     */
    private final ProteinGroupService proteinGroupService;

    /**
     * Constructor.
     *
     * @param source      the EventList that holds the table data
     * @param tableFormat the TableFormat instance of the table
     */
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
     * Updates the row count and returns a list of protein groups for the given search criteria.
     *
     * @param analyticalRun the run where the protein groups are associated with
     * @return the list of ProteinGroup instances
     */
    public List<ProteinGroupForRun> getRows(AnalyticalRun analyticalRun) {
        rowCount = proteinGroupService.getProteinGroupCountForRun(analyticalRun, filter);

        if (rowCount < page * perPage) {
            page = getMaxPage();
        }

        return proteinGroupService.getPagedProteinGroupsForRun(analyticalRun, page * perPage, perPage, sortColumn, sortDirection.queryValue(), filter);
    }

    /**
     * Reset the table model with the protein groups associated with the specified analytical run.
     *
     * @param analyticalRun the run where the protein groups are associated with
     */
    public void reset(final AnalyticalRun analyticalRun) {
        super.reset(analyticalRun == null ? 0 : proteinGroupService.getProteinGroupCountForRun(analyticalRun, filter));
    }
}

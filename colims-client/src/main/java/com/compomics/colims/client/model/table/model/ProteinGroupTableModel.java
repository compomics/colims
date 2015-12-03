package com.compomics.colims.client.model.table.model;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.gui.TableFormat;
import com.compomics.colims.client.model.table.format.ProteinGroupTableFormat;
import com.compomics.colims.core.config.ApplicationContextProvider;
import com.compomics.colims.core.service.ProteinGroupService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.repository.hibernate.ProteinGroupDTO;

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
     * @param source                 the EventList that holds the table data
     * @param tableFormat            the TableFormat instance of the table
     * @param perPage                the number of rows per page
     * @param defaultSortColumnIndex the default sort column
     */
    public ProteinGroupTableModel(EventList source, TableFormat tableFormat, int perPage, int defaultSortColumnIndex) {
        super(source, tableFormat, perPage, defaultSortColumnIndex);
        proteinGroupService = ApplicationContextProvider.getInstance().getBean("proteinGroupService");
    }

    @Override
    public String getColumnDbName(int column) {
        switch (column) {
            case ProteinGroupTableFormat.ID:
                return "id";
            case ProteinGroupTableFormat.SEQUENCE:
                return "mainSequence";
            case ProteinGroupTableFormat.ACCESSION:
                return "mainAccession";
            case ProteinGroupTableFormat.NUMBER_OF_DISTINCT_PEPTIDE_SEQUENCES:
                return "distinctPeptideSequenceCount";
            case ProteinGroupTableFormat.NUMBER_OF_SPECTRA:
                return "spectrumCount";
            case ProteinGroupTableFormat.CONFIDENCE:
                return "proteinPostErrorProbability";
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
    public List<ProteinGroupDTO> getRows(AnalyticalRun analyticalRun) {
        rowCount = proteinGroupService.getProteinGroupCountForRun(analyticalRun, filter);

        if (rowCount < page * perPage) {
            page = getMaxPage();
        }

        return proteinGroupService.getPagedProteinGroupsForRun(analyticalRun, page * perPage, perPage, getColumnDbName(sortColumnIndex), sortDirection, filter);
    }

    /**
     * Reset the table model with the protein groups associated with the specified analytical run.
     *
     * @param analyticalRun the run where the protein groups are associated with
     */
    public void reset(final AnalyticalRun analyticalRun) {
        if (analyticalRun == null) {
            super.reset();
        } else {
            super.reset(proteinGroupService.getProteinGroupCountForRun(analyticalRun, filter));
        }
    }
}

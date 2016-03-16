package com.compomics.colims.client.model.table.model;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.gui.TableFormat;
import com.compomics.colims.client.model.table.format.PsmTableFormat;
import com.compomics.colims.core.config.ApplicationContextProvider;
import com.compomics.colims.core.service.SpectrumService;
import com.compomics.colims.model.AnalyticalRun;

import java.util.List;

/**
 * Table model to implement search and sort method.
 *
 * @author Iain
 */
public class PsmTableModel extends AdvancedPagingTableModel {

    /**
     * The SpectrumService instance.
     */
    private final SpectrumService spectrumService;

    /**
     * Constructor time.
     *
     * @param source      Something for the parent
     * @param tableFormat Something for the parent
     */
    public PsmTableModel(final EventList source, final TableFormat tableFormat) {
        super(source, tableFormat, 20, PsmTableFormat.SPECTRUM_ID);
        spectrumService = ApplicationContextProvider.getInstance().getBean("spectrumService");
    }

    /**
     * Updates the row count and returns a list of spectra for the given search parameters.
     *
     * @param analyticalRun The run from which spectra are queried
     * @return List of Spectrum objects
     */
    public List getRows(final AnalyticalRun analyticalRun) {
        rowCount = spectrumService.getSpectraCountForRun(analyticalRun, getColumnDbName(sortColumnIndex), filter);

        if (rowCount < page * perPage) {
            page = getMaxPage();
        }

        return spectrumService.getPagedSpectra(analyticalRun, page * perPage, perPage, getColumnDbName(sortColumnIndex), sortDirection.queryValue(), filter);
    }

    /**
     * Reset the table to default values, either empty or with data.
     *
     * @param analyticalRun An optional run to obtain spectra from
     */
    public void reset(final AnalyticalRun analyticalRun) {
        if (analyticalRun == null) {
            super.reset(0);
        } else {
            super.reset(spectrumService.getSpectraCountForRun(analyticalRun, getColumnDbName(sortColumnIndex), filter));
        }
    }

    /**
     * Get the database column name from the table column.
     *
     * @param column Column index
     * @return Database column name
     */
    @Override
    public String getColumnDbName(final int column) {
        switch (column) {
            case PsmTableFormat.SPECTRUM_ID:
                return "spectrum.id";
            case PsmTableFormat.PRECURSOR_CHARGE:
                return "spectrum.charge";
            case PsmTableFormat.PRECURSOR_MZRATIO:
                return "mz_ratio";
            case PsmTableFormat.PRECURSOR_INTENSITY:
                return "intensity";
            case PsmTableFormat.RETENTION_TIME:
                return "retention_time";
            case PsmTableFormat.PEPTIDE_SEQUENCE:
                return "peptide.peptide_sequence";
            case PsmTableFormat.PSM_CONFIDENCE:
                return "peptide.psm_post_error_prob";
            case PsmTableFormat.PROTEIN_ACCESSIONS:
                return "protein_accession.accession";
            default:
                throw new IllegalArgumentException("Unexpected column number " + column);
        }
    }
}

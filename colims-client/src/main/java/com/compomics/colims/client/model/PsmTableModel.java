package com.compomics.colims.client.model;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.gui.TableFormat;
import com.compomics.colims.client.model.tableformat.PsmTableFormat;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.repository.SpectrumRepository;

import java.util.List;

/**
 * Table model to implement search and sort methods Created by Iain on
 * 24/04/2015.
 */
public class PsmTableModel extends PagingTableModel {

    private final SpectrumRepository spectrumRepository;

    /**
     * Constructor time.
     *
     * @param source Something for the parent
     * @param tableFormat Something for the parent
     * @param spectrumRepository A spectrum repository to query for rows
     */
    public PsmTableModel(final EventList source, final TableFormat tableFormat, final SpectrumRepository spectrumRepository) {
        super(source, tableFormat);

        this.spectrumRepository = spectrumRepository;
    }

    /**
     * Updates the row count and returns a list of spectra for the given search
     * parameters.
     *
     * @param analyticalRun The run from which spectra are queried
     * @return List of Spectrum objects
     */
    public List getRows(final AnalyticalRun analyticalRun) {
        rowCount = spectrumRepository.getSpectraCountForRun(analyticalRun, sortColumn, filter);

        if (rowCount < page * perPage) {
            page = getMaxPage();
        }

        return spectrumRepository.getPagedSpectra(analyticalRun, page * perPage, perPage, sortColumn, sortDirection, filter);
    }

    /**
     * Reset the table to default values, either empty or with data.
     *
     * @param analyticalRun An optional run to obtain spectra from
     */
    public void reset(final AnalyticalRun analyticalRun) {
        super.reset(analyticalRun == null ? 0 : spectrumRepository.getSpectraCountForRun(analyticalRun, sortColumn, filter));
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

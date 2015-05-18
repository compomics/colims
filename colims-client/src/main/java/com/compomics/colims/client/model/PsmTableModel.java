package com.compomics.colims.client.model;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.repository.SpectrumRepository;

import java.util.List;

/**
 * Table model to implement search and sort methods
 * Created by Iain on 24/04/2015.
 */
public class PsmTableModel extends DefaultEventTableModel {
    private SpectrumRepository spectrumRepository;

    public static final int SPECTRUM_ID = 0;
    public static final int PRECURSOR_CHARGE = 1;
    public static final int PRECURSOR_MZRATIO = 2;
    public static final int PRECURSOR_INTENSITY = 3;
    public static final int RETENTION_TIME = 4;
    public static final int PEPTIDE_SEQUENCE = 5;
    public static final int PSM_CONFIDENCE = 6;
    public static final int PROTEIN_ACCESSIONS = 7;

    private int page;
    private int perPage;
    private int rowCount;
    public String sortColumn;
    public String sortDirection;
    public String filter;

    /**
     * Constructor time
     * @param source Something for the parent
     * @param tableFormat Something for the parent
     * @param spectrumRepository A spectrum repository to query for rows
     */
    public PsmTableModel(EventList source, TableFormat tableFormat, SpectrumRepository spectrumRepository) {
        super(source, tableFormat);
        reset(null);

        this.spectrumRepository = spectrumRepository;
    }

    /**
     * Updates the row count and returns a list of spectra for the given search parameters
     * @param analyticalRun The run from which spectra are queried
     * @return List of Spectrum objects
     */
    public List getRows(AnalyticalRun analyticalRun) {
        rowCount = spectrumRepository.getSpectraCountForRun(analyticalRun, sortColumn, filter);

        if (rowCount < page * perPage) {
            page = getMaxPage();
        }

        return spectrumRepository.getPagedSpectra(analyticalRun, page * perPage, perPage, sortColumn, sortDirection, filter);
    }

    /**
     * Builds a string to display the current page
     * @return Page x of y
     */
    public String getPageIndicator() {
        return String.format("Page %d of %d", page + 1, getMaxPage() + 1);
    }

    /**
     * Reset the table to default values, either empty or with data
     * @param analyticalRun An optional run to obtain spectra from
     */
    public void reset(AnalyticalRun analyticalRun) {
        page = 0;
        perPage = 20;
        sortColumn = getColumnDbName(0);
        sortDirection = "asc";
        filter = "";
        rowCount = analyticalRun == null ? 0 : spectrumRepository.getSpectraCountForRun(analyticalRun, sortColumn, filter);
    }

    /**
     * Update the sort column or reverse direction if same column specified
     * @param index Column index
     */
    public void updateSort(int index) {
        // TODO: further reduce this
        String column = getColumnDbName(index);

        if (column.equals(sortColumn)) {
            sortDirection = sortDirection.equals("asc") ? "desc" : "asc";
        } else {
            sortColumn = column;
            sortDirection = "asc";
        }
    }

    /**
     * Get the database column name from the table column
     * @param column Column index
     * @return Database column name
     */
    public String getColumnDbName(int column) {
        switch (column) {
            case SPECTRUM_ID:
                return "spectrum.id";
            case PRECURSOR_CHARGE:
                return "spectrum.charge";
            case PRECURSOR_MZRATIO:
                return "mz_ratio";
            case PRECURSOR_INTENSITY:
                return "intensity";
            case RETENTION_TIME:
                return "retention_time";
            case PEPTIDE_SEQUENCE:
                return "peptide.peptide_sequence";
            case PSM_CONFIDENCE:
                return "peptide.psm_post_error_prob";
            case PROTEIN_ACCESSIONS:
                return "protein_accession.accession";
            default:
                throw new IllegalArgumentException("Unexpected column number " + column);
        }
    }

    /**
     * Whether the current page is the last page
     * @return Yay or nay
     */
    public boolean isMaxPage() {
        return page == getMaxPage();
    }

    /**
     * Calculate the highest page in the current data set
     * @return Highest page number
     */
    public int getMaxPage() {
        return (int) Math.floor(rowCount / perPage);
    }

    /**
     * Get current page
     * @return Current page
     */
    public int getPage() {
        return page;
    }

    /**
     * Set the current page (within acceptable range)
     * @param page Page number
     */
    public void setPage(int page) {
        if (page <= getMaxPage() && page > 0) {
            this.page = page;
        }
    }

    /**
     * Update filter text
     * @param filter Filter string
     */
    public void setFilter(String filter) {
        this.filter = filter;
    }
}

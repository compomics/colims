package com.compomics.colims.client.model;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.gui.TableFormat;
import com.compomics.colims.client.model.tableformat.PsmTableFormat;

/**
 * Table model for spectrum table on the protein panel.
 * <p/>
 * Created by Iain on 27/07/2015.
 */
public class ProteinPanelPsmTableModel extends PsmTableModel {

    /**
     * Constructor time.
     *
     * @param source      Something for the parent
     * @param tableFormat Something for the parent
     */
    public ProteinPanelPsmTableModel(EventList source, TableFormat tableFormat) {
        super(source, tableFormat);
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
            case PsmTableFormat.PSM_CONFIDENCE:
                return "peptide.psm_post_error_prob";
            default:
                throw new IllegalArgumentException("Unexpected column number " + column);
        }
    }
}

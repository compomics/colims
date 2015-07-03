package com.compomics.colims.client.model;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.gui.TableFormat;
import com.compomics.colims.core.service.ProteinService;
import com.compomics.colims.repository.ProteinRepository;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Iain on 19/06/2015.
 */
public class ProteinTableModel extends PagingTableModel {

    public ProteinTableModel(EventList source, TableFormat tableFormat) {
        super(source, tableFormat);
    }

    @Override
    public String getColumnDbName(int column) {
        return null;
    }

    /**
     * Updates the row count and returns a list of spectra for the given search
     * parameters.
     *
     * @param analyticalRun The run from which spectra are queried
     * @return List of Spectrum objects
     */
    public List getRows(ProteinRepository proteinRepository) {
        return new ArrayList<>();
    }
}

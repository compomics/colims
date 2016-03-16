/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.client.model.table.model;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.gui.TableFormat;

/**
 *
 * @author Niels Hulstaert
 */
public class OlsSearchResultTableModel extends SimplePagingTableModel {

    /**
     * Constructor.
     *
     * @param source the source EventList
     * @param tableFormat the table format
     * @param perPage the page size
     */
    public OlsSearchResultTableModel(EventList source, TableFormat tableFormat, int perPage) {
        super(source, tableFormat, perPage);
    }

}

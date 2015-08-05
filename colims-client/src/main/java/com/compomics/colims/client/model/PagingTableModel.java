package com.compomics.colims.client.model;


import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.DefaultEventTableModel;

/**
 * Created by Iain on 19/06/2015.
 */
public abstract class PagingTableModel extends DefaultEventTableModel {

    int page;
    int perPage;
    int rowCount;
    String sortColumn;
    String sortDirection;
    String filter;

    public PagingTableModel(EventList source, TableFormat tableFormat) {
        super(source, tableFormat);
        reset(0);
    }

    /**
     * Reset the table to default values, either empty or with data.
     *
     * @param rows Number of rows to populate table with
     */
    public void reset(int rows) {
        page = 0;
        perPage = 20;
        sortColumn = getColumnDbName(0);
        sortDirection = "asc";
        filter = "";
        rowCount = rows;
    }

    /**
     * Get the database column name from the table column.
     *
     * @param column Column index
     * @return Database column name
     */
    public abstract String getColumnDbName(final int column);

    /**
     * Update the sort column or reverse direction if same column specified.
     *
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
     * Whether the current page is the last page.
     *
     * @return Yay or nay
     */
    public boolean isMaxPage() {
        return page == getMaxPage();
    }

    /**
     * Calculate the highest page in the current data set.
     *
     * @return Highest page number
     */
    public int getMaxPage() {
        return (int) Math.floor(rowCount / perPage);
    }

    /**
     * Get current page.
     *
     * @return Current page
     */
    public int getPage() {
        return page;
    }

    /**
     * Set the current page (within acceptable range).
     *
     * @param page Page number
     */
    public void setPage(int page) {
        if (page <= getMaxPage() && page >= 0) {
            this.page = page;
        }
    }

    /**
     * Get the number of rows displayed per page
     *
     * @return That value
     */
    public int getPerPage() {
        return perPage;
    }

    /**
     * Update filter text.
     *
     * @param filter Filter string
     */
    public void setFilter(final String filter) {
        this.filter = filter;
    }

    /**
     * Builds a string to display the current page.
     *
     * @return Page x of y
     */
    public String getPageIndicator() {
        return String.format("Page %d of %d", page + 1, getMaxPage() + 1);
    }
}

package com.compomics.colims.client.model;


import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import com.compomics.colims.repository.hibernate.SortDirection;

/**
 * This class represents a paged table model with sort and filter capabilities.
 * <p/>
 * Created by Iain on 19/06/2015.
 */
public abstract class PagingTableModel extends DefaultEventTableModel {

    /**
     * The current page.
     */
    protected int page;
    /**
     * The number of rows per page (page length).
     */
    protected int perPage;
    /**
     * The total number of rows to populate the table with.
     */
    protected long rowCount;
    /**
     * The name of the column that will be sorted on.
     */
    protected String sortColumn;
    /**
     * The direction in which the sort column has to be sorted.
     */
    protected SortDirection sortDirection;
    /**
     * The filter value.
     */
    protected String filter;

    public PagingTableModel(EventList source, TableFormat tableFormat) {
        super(source, tableFormat);
        reset(0);
    }

    /**
     * Reset the table to default values, either empty or with data.
     *
     * @param rows number of rows to populate the table with
     */
    public void reset(long rows) {
        page = 0;
        perPage = 20;
        sortColumn = getColumnDbName(0);
        sortDirection = SortDirection.ASCENDING;
        filter = "";
        rowCount = rows;
    }

    /**
     * Get the database column name from the table column.
     *
     * @param column Column index
     * @return database column name
     */
    public abstract String getColumnDbName(final int column);

    /**
     * Update the sort column or reverse direction if same column specified.
     *
     * @param index column index
     */
    public void updateSort(int index) {
        //TODO: further reduce this
        String column = getColumnDbName(index);

        if (column.equals(sortColumn)) {
            sortDirection = sortDirection == SortDirection.ASCENDING ? SortDirection.DESCENDING : SortDirection.ASCENDING;
        } else {
            sortColumn = column;
            sortDirection = SortDirection.ASCENDING;
        }
    }

    /**
     * Whether the current page is the last page.
     *
     * @return yay or nay
     */
    public boolean isMaxPage() {
        return page == getMaxPage();
    }

    /**
     * Calculate the highest page in the current data set.
     *
     * @return highest page number
     */
    public int getMaxPage() {
        return (int) Math.floor(rowCount / perPage);
    }

    /**
     * Get current page.
     *
     * @return current page
     */
    public int getPage() {
        return page;
    }

    /**
     * Set the current page (within acceptable range).
     *
     * @param page the page number
     */
    public void setPage(int page) {
        if (page <= getMaxPage() && page >= 0) {
            this.page = page;
        }
    }

    /**
     * Get the number of rows displayed per page.
     *
     * @return the number of rows displayed per page
     */
    public int getPerPage() {
        return perPage;
    }

    /**
     * Set the number of rows displayed per page.
     *
     * @param perPage the page length
     */
    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }

    /**
     * Update filter text.
     *
     * @param filter the filter string
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

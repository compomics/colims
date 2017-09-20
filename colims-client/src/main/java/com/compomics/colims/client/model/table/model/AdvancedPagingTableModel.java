package com.compomics.colims.client.model.table.model;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import com.compomics.colims.repository.hibernate.SortDirection;

/**
 * This class represents a paged table model with sort and filter capabilities.
 * <p/>
 * Created by Iain on 19/06/2015.
 */
public abstract class AdvancedPagingTableModel extends DefaultEventTableModel {

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
     * The index of the default sorting column.
     */
    protected final int defaultSortColumnIndex;
    /**
     * The index of the column that will be sorted on.
     */
    protected int sortColumnIndex;
    /**
     * The direction in which the sort column has to be sorted.
     */
    protected SortDirection sortDirection;
    /**
     * The filter value.
     */
    protected String filter;

    /**
     * Constructor.
     *
     * @param source the source event list
     * @param tableFormat the table format
     * @param perPage the number of rows per page
     * @param defaultSortColumnIndex the default sort column
     */
    public AdvancedPagingTableModel(EventList source, TableFormat tableFormat, int perPage, int defaultSortColumnIndex) {
        super(source, tableFormat);
        this.perPage = perPage;
        this.defaultSortColumnIndex = defaultSortColumnIndex;
        this.sortColumnIndex = defaultSortColumnIndex;
        this.rowCount = 0;
        this.page = 0;
        this.sortDirection = SortDirection.ASCENDING;
        this.filter = "";
    }

    /**
     * Reset the table to default values, for the given row count.
     *
     * @param rowCount number of rows to populate the table with
     */
    public void reset(long rowCount) {
        this.rowCount = rowCount;
        this.page = 0;
        this.sortDirection = SortDirection.ASCENDING;
        this.filter = "";
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
     * @param sortColumnIndex the sort column index
     */
    public void updateSort(int sortColumnIndex) {
        if (this.sortColumnIndex == sortColumnIndex) {
            sortDirection = sortDirection == SortDirection.ASCENDING ? SortDirection.DESCENDING : SortDirection.ASCENDING;
        } else {
            this.sortColumnIndex = sortColumnIndex;
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

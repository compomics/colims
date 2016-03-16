package com.compomics.colims.client.model.table.model;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.DefaultEventTableModel;

/**
 * This class represents a paged table model.
 * <p/>
 * Created by Niels Hulstaert.
 */
public abstract class SimplePagingTableModel extends DefaultEventTableModel {

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
     * Constructor.
     *
     * @param source the source event list
     * @param tableFormat the table format
     * @param perPage the number of rows per page
     */
    public SimplePagingTableModel(EventList source, TableFormat tableFormat, int perPage) {
        super(source, tableFormat);
        this.perPage = perPage;
        this.rowCount = 0;
        this.page = 0;
    }

    /**
     * Reset the table to default values, for the given row count.
     *
     * @param rowCount number of rows to populate the table with
     */
    public void reset(long rowCount) {
        this.rowCount = rowCount;
        this.page = 0;
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
     * Builds a string to display the current page.
     *
     * @return Page x of y
     */
    public String getPageIndicator() {
        return String.format("Page %d of %d", page + 1, getMaxPage() + 1);
    }
}

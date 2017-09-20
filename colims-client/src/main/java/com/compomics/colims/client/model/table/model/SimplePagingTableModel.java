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
     * The current page index, zero-based.
     */
    protected int page;
    /**
     * The index of the last page, zero-based.
     */
    protected int lastPage;
    /**
     * The number of rows per page.
     */
    protected final int pageSize;
    /**
     * The total number of rows.
     */
    protected int rowCount;

    /**
     * Constructor.
     *
     * @param source the source event list
     * @param tableFormat the table format
     * @param pageSize the number of rows per page
     */
    public SimplePagingTableModel(EventList source, TableFormat tableFormat, int pageSize) {
        super(source, tableFormat);
        this.pageSize = pageSize;
        this.rowCount = 0;
        this.page = 0;
    }

    /**
     * Initialize the table with a new row count.
     *
     * @param rowCount the total number of rows
     */
    public void init(int rowCount) {
        this.rowCount = rowCount;
        this.page = 0;
        this.lastPage = (int) Math.floor(rowCount / pageSize);
    }

    /**
     * Get the current page index, zero-based.
     *
     * @return current page index
     */
    public int getPage() {
        return page;
    }

    /**
     * Set the current page (within acceptable range), zero-based.
     *
     * @param page the page number
     */
    public void setPage(int page) {
        if (page <= getLastPage() && page >= 0) {
            this.page = page;
        } else {
            throw new IllegalArgumentException("Invalid page index: " + page);
        }
    }

    /**
     * Get the index of the first row of the previous page, zero-based.
     *
     * @return the index of the first row on the previous page
     */
    public int getPreviousPageFirstRow() {
        return (page - 1) * pageSize;
    }

    /**
     * Get the index of the first row of the next page, zero-based.
     *
     * @return the index of the first row on the next page
     */
    public int getNextPageFirstRow() {
        return (page + 1) * pageSize;
    }

    /**
     * Get the index of the last page, zero-based.
     *
     * @return the index of the last page
     */
    public int getLastPage() {
        return lastPage;
    }

    /**
     * Get the index of the first row of the last page, zero-based.
     *
     * @return the index of the last page
     */
    public int getLastPageFirstRow() {
        return lastPage * pageSize;
    }

    /**
     * Builds a string to display the current page.
     *
     * @return Page x of y
     */
    public String getPageIndicator() {
        return String.format("Page %d of %d", page + 1, getLastPage() + 1);
    }
}

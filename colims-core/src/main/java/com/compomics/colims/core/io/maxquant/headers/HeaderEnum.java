package com.compomics.colims.core.io.maxquant.headers;

/**
 *
 * @author Davy
 */
public interface HeaderEnum {

    /**
     * 
     * @return returns the defined possible headers for a given enum value 
     */
    String[] allPossibleColumnNames();

    /**
     * sets the header used in the file to parse
     * @param columnReference sets the index of the possible header in the array of possibilities defined
     */
    void setColumnReference(int columnReference);

    /**
     * returns the selected header name for the given enumeration
     * @return the header name
     * @throws HeaderEnumNotInitialisedException if the array of possible names is not filled
     */
    String getColumnName() throws HeaderEnumNotInitialisedException;
}

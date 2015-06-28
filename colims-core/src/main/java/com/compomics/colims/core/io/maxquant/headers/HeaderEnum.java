package com.compomics.colims.core.io.maxquant.headers;

import java.util.List;

/**
 *
 * @author Davy
 */
public interface HeaderEnum {


    /**
     * 
     * @return returns the defined possible headers for a given enum value 
     */
    List<String> allPossibleColumnNames();

    void setColumnNameNumber(int columnNameNumber);

    String getColumnName(int columnNameNumber);

    String getDefaultColumnName();

}

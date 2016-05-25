package com.compomics.colims.distributed.io.maxquant.headers;

import java.util.List;

/**
 * This interface is implemented by all MaxQuant header enums to be able to cope with possible changes in
 * naming/ordering of colums or parameters in the MaxQuant files.
 *
 * @author Davy
 */
public interface HeaderEnum {

    /**
     * This method returns 
     *
     * @return the defined possible headers for a given enum value
     */
    List<String> allPossibleColumnNames();

    void setColumnNameNumber(int columnNameNumber);

    String getColumnName(int columnNameNumber);

    String getDefaultColumnName();

}

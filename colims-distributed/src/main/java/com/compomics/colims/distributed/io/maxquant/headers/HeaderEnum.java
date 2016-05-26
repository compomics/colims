package com.compomics.colims.distributed.io.maxquant.headers;

import java.util.List;

/**
 * This interface is implemented by all MaxQuant header enums to be able to cope with possible changes in
 * naming/ordering of column/parameter header values in the different MaxQuant files.
 *
 * @author Davy
 */
public interface HeaderEnum {

    /**
     * This method returns all possible header values for a given header.
     *
     * @return the possible header values for a given header
     */
    List<String> getPossibleValues();

    /**
     * Set the parsed header value by index.
     *
     * @param index the parsed header value index
     */
    void setParsedValue(int index);

    /**
     * Get the parsed header value.
     *
     * @return the parsed header value
     */
    String getValue();

}

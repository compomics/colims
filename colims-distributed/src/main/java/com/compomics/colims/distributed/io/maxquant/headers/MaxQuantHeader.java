package com.compomics.colims.distributed.io.maxquant.headers;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class represents a single header entry of a MaxQuant tab separated identification file.
 * <p>
 * Created by Niels Hulstaert on 13/09/16.
 */
public class MaxQuantHeader {

    /**
     * Boolean that indicates whether the header is mandatory for parsing or not.
     */
    private boolean mandatory;
    /**
     * The list with all possible header values for this header.
     */
    private List<String> values;
    /**
     * The header value index.
     */
    private int headerValueIndex = 0;

    /**
     * No-arg constructor.
     */
    public MaxQuantHeader() {
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    /**
     * Set the index of the parsed header value.
     *
     * @param headerValueIndex the parsed header value index
     */
    public void setParsedValue(int headerValueIndex) {
        if (headerValueIndex >= values.size()) {
            throw new IllegalArgumentException("The given index " + headerValueIndex + " exceeds the number of possible header values.");
        }
        this.headerValueIndex = headerValueIndex;
    }

}

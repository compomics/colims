package com.compomics.colims.distributed.io.maxquant.headers;

import java.util.List;

/**
 * This class represents a single header entry of a MaxQuant file.
 * <p>
 * Created by Niels Hulstaert on 13/09/16.
 */
public class MaxQuantHeader {

    /**
     * The header name as found in the JSON file. This name should be the String value of the corresponding enum.
     */
    String name;
    /**
     * Boolean that indicates whether the header is mandatory for parsing or not.
     */
    private final boolean mandatory;
    /**
     * The list with all possible header values for this header.
     */
    private final List<String> values;
    /**
     * The header value index.
     */
    private int headerValueIndex = 0;

    /**
     * Constructor.
     *
     * @param name      the header name
     * @param mandatory is the header mandatory
     * @param values    the possible header values
     */
    public MaxQuantHeader(String name, boolean mandatory, List<String> values) {
        this.name = name;
        this.mandatory = mandatory;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public List<String> getValues() {
        return values;
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

    /**
     * Get the header value.
     *
     * @return the header value;
     */
    public String getValue() {
        return values.get(headerValueIndex);
    }

}

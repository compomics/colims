package com.compomics.colims.distributed.io.maxquant.headers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * This class represents a single header entry of a MaxQuant tab separated identification file.
 * <p>
 * Created by Niels Hulstaert on 13/09/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaxQuantHeader {

    /**
     * The header name.
     */
    private String name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public int getHeaderValueIndex() {
        return headerValueIndex;
    }

    public void setHeaderValueIndex(int headerValueIndex) {
        this.headerValueIndex = headerValueIndex;
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
     * Get the parsed header value.
     *
     * @return the parsed header value.
     */
    public String getValue() {
        return null;
    }

}

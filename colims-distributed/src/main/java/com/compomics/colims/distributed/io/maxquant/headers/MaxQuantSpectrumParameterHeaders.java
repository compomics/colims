package com.compomics.colims.distributed.io.maxquant.headers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by niels.
 */
public enum MaxQuantSpectrumParameterHeaders implements HeaderEnum {

    ENZYMES(new String[]{"enzymes"}),
    VARIABLE_MODIFICATIONS(new String[]{"variableModifications"}),
    FIXED_MODIFICATIONS(new String[]{"fixedModifications"}),
    PEPTIDE_MASS_TOLERANCE(new String[]{"mainSearchTol"}),
    PEPTIDE_MASS_TOLERANCE_UNIT(new String[]{"searchTolInPpm"}),
    FRAGMENT_MASS_TOLERANCE(new String[]{"firstSearchTol"}),
    MAX_MISSED_CLEAVAGES(new String[]{"maxMissedCleavages"}),
    MAX_CHARGE(new String[]{"maxCharge"});

    /**
     * The list of header values for the enum value.
     */
    private List<String> headerValues;
    /**
     * The default header value index.
     */
    private int defaultHeaderValueIndex = 0;

    /**
     * Constructor.
     *
     * @param headerValueArray the array header values
     */
    MaxQuantSpectrumParameterHeaders(final String[] headerValueArray) {
        this.headerValues = new ArrayList<>(headerValueArray.length);
        Arrays.stream(headerValueArray).forEach(e -> this.headerValues.add(e.toLowerCase(Locale.US)));
    }

    @Override
    public List<String> getValues() {
        return this.headerValues;
    }

    @Override
    public void setParsedValue(int headerValueIndex) {
        defaultHeaderValueIndex = headerValueIndex;
    }

    @Override
    public String getValue() {
        return headerValues.get(defaultHeaderValueIndex);
    }
}

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
    VARIABLE_MODIFICATIONS(new String[]{"variable modifications"}),
    FIXED_MODIFICATIONS(new String[]{"fixed modifications"}),
    PEPTIDE_MASS_TOLERANCE(new String[]{"peptide mass tolerance"}),
    PEPTIDE_MASS_TOLERANCE_UNIT(new String[]{"peptide mass tolerance Unit"}),
    FRAGMENT_MASS_TOLERANCE(new String[]{"fragment mass tolerance"}),
    FRAGMENT_MASS_TOLERANCE_UNIT(new String[]{"fragment mass tolerance Unit"}),
    MAX_MISSED_CLEAVAGES(new String[]{"max missed cleavages"}),
    MAX_CHARGE(new String[]{"max charge"});

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
    public List<String> getPossibleValues() {
        return this.headerValues;
    }

    @Override
    public void setParsedValue(int index) {
        defaultHeaderValueIndex = index;
    }

    @Override
    public String getValue() {
        return headerValues.get(defaultHeaderValueIndex);
    }
}

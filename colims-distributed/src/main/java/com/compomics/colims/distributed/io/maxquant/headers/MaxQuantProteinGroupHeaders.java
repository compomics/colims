package com.compomics.colims.distributed.io.maxquant.headers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by Iain on 03/03/2015.
 */
public enum MaxQuantProteinGroupHeaders implements HeaderEnum {

    ACCESSION(new String[]{"Protein IDs"}),
    BESTMSMS(new String[]{"Best MS/MS"}),
    CONTAMINANT(new String[]{"Potential contaminant"}),
    EVIDENCEIDS(new String[]{"Evidence IDs"}),
    FASTAHEADER(new String[]{"Fasta headers"}),
    ID(new String[]{"id"}),
    MSMSIDS(new String[]{"MS/MS IDs"}),
    PEP(new String[]{"PEP"}),
    PEPTIDEIDS(new String[]{"Peptide IDs"}),
    REVERSE(new String[]{"Reverse"}),
    IBAQ(new String[]{"iBAQ"}),
    LFQ_INTENSITY(new String[]{"LFQ intensity"}),
    INTENSITY(new String[]{"Intensity"}),
    MSMS_COUNT(new String[]{"MS/MS Count"}),
    REPORTER_INTENSITY_CORRECTED(new String[]{"Reporter intensity corrected"}),
    INTENSITY_L(new String[]{"Intensity L"}),
    INTENSITY_M(new String[]{"Intensity M"}),
    INTENSITY_H(new String[]{"Intensity H"});
    
    /**
     * The list of header values for the enum value.
     */
    private List<String> headerValues;
    /**
     * The header value index.
     */
    private int headerValueIndex = 0;

    /**
     * Constructor.
     *
     * @param headerValueArray the array header values
     */
    MaxQuantProteinGroupHeaders(final String[] headerValueArray) {
        headerValues = new ArrayList<>(headerValueArray.length);
        Arrays.stream(headerValueArray).forEach(e -> headerValues.add(e.toLowerCase(Locale.US)));
    }

    @Override
    public List<String> getPossibleValues() {
        return this.headerValues;
    }

    @Override
    public void setParsedValue(int index) {
        headerValueIndex = index;
    }

    @Override
    public String getValue() {
        return headerValues.get(headerValueIndex);
    }
}

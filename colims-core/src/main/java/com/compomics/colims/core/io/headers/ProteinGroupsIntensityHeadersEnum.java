/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.io.headers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author demet
 */
public enum ProteinGroupsIntensityHeadersEnum {

    RATIO_M_L("Ratio M/L"),
    RATIO_H_L("Ratio H/L"),
    RATIO_H_M("Ratio H/M"),
    RATIO_M_L_NORMALIZED("Ratio M/L normalized"),
    RATIO_H_L_NORMALIZED("Ratio H/L normalized"),
    RATIO_H_M_NORMALIZED("Ratio H/M normalized"),
    RATIO_M_L_VARIABILITY("Ratio M/L variability [%]"),
    RATIO_H_L_VARIABILITY("Ratio H/L variability [%]"),
    RATIO_H_M_VARIABILITY("Ratio H/M variability [%]"),
    RATIO_M_L_COUNT("Ratio M/L count"),
    RATIO_H_L_COUNT("Ratio H/L count"),
    RATIO_H_M_COUNT("Ratio H/M count"),
    RATIO_M_L_ISO_COUNT("Ratio M/L iso-count"),
    RATIO_H_L_ISO_COUNT("Ratio H/L iso-count"),
    RATIO_H_M_ISO_COUNT("Ratio H/M iso-count"),
    RATIO_M_L_TYPE("Ratio M/L type"),
    RATIO_H_L_TYPE("Ratio H/L type"),
    RATIO_H_M_TYPE("Ratio H/M type"),
    IBAQ_L("iBAQ L"),
    IBAQ_M("iBAQ M"),
    IBAQ_H("iBAQ H"),
    LFQ_L("LFQ intensity L"),
    LFQ_M("LFQ intensity M"),
    LFQ_H("LFQ intensity H");

    /**
     * The proteinGroups.txt header value.
     */
    private final String header;

    /**
     * Constructor.
     *
     * @param header the proteinGroups.txt header value
     */
    ProteinGroupsIntensityHeadersEnum(String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }

    /**
     * Get the (lower case) header values as a list.
     *
     * @return the list of header values
     */
    public static List<String> getHeaderValues() {
        List<String> headerValues = new ArrayList<>();
        Arrays.asList(values()).forEach(value -> headerValues.add(value.getHeader().toLowerCase()));

        return headerValues;
    }

}

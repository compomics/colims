/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.io.headers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author demet
 */
public enum ProteinGroupIntensityHeadersEnum{

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
 
    private String header;
    /**
     * The list of header values for the enum value.
     */
    private static Map<String,String> headerValues;

    /**
     * Constructor
     * @param header 
     */
    ProteinGroupIntensityHeadersEnum(String header){
        this.header = header;
    }
    
    public static Map<String,String> getHeaderValues() {
        headerValues = new HashMap<>();
        ProteinGroupIntensityHeadersEnum[] headersEnum = ProteinGroupIntensityHeadersEnum.values();
        Arrays.stream(headersEnum).forEach(e -> headerValues.put(e.header.toLowerCase(Locale.US), e.header));
        return headerValues;
    }

}

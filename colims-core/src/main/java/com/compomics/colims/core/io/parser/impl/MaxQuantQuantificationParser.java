/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.io.parser.impl;

import com.compomics.colims.model.Quantification;
import com.compomics.colims.model.QuantificationGroup;
import com.compomics.colims.model.enums.QuantificationWeight;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Kenneth
 */
public class MaxQuantQuantificationParser {

    /**
     * parses a max quant protein groups file into memory
     *
     * @param aProteinGroupsFile the file to parse
     * @return a Map key: the protein groupid, value: the ProteinMatch
     */
    public static Map<Integer, QuantificationGroup> parseMaxQuantQuantificationGroups(File aQuantificationFile) throws IOException, FileNotFoundException {
        Map<Integer, QuantificationGroup> quantificationGroupMap = new HashMap<>(1000);
        TabularFileLineValuesIterator iter = new TabularFileLineValuesIterator(aQuantificationFile);
        Map<String, String> quantificationLine;
        
        while (iter.hasNext()) {
            quantificationLine = iter.next();
            
            //parse heavy one
            String heavyIntensityAsString = quantificationLine.get(MaxQuantQuantificationParser.QuantificationGroupHeaders.HIGHINTENSITY.headerName);
            double heavyIntensity;
            if (heavyIntensityAsString.equalsIgnoreCase("nan")) {
                heavyIntensity = 0.0;
            } else {
                heavyIntensity = Double.parseDouble(heavyIntensityAsString);
            }
            
            //parse light one 
            String lightIntensityAsString = quantificationLine.get(MaxQuantQuantificationParser.QuantificationGroupHeaders.LOWINTENSITY.headerName);
            double lightIntensity;
            if (lightIntensityAsString.equalsIgnoreCase("nan")) {
                lightIntensity = 0.0;
            } else {
                lightIntensity = Double.parseDouble(lightIntensityAsString);
            }

            String[] spectrumIDsString = quantificationLine.get(MaxQuantQuantificationParser.QuantificationGroupHeaders.SPECTRUMIDS.headerName).split(";");

            int quantificationGroupID = Integer.parseInt(quantificationLine.get(MaxQuantQuantificationParser.QuantificationGroupHeaders.ID.headerName));

            QuantificationGroup quantGroup = new QuantificationGroup();

            List<Quantification> quantificationsList = new ArrayList<Quantification>();

            for (String aSpectrumID : spectrumIDsString) {
                int spectrumID = Integer.parseInt(aSpectrumID);

                Quantification lightQuant = new Quantification();
                Quantification heavyQuant = new Quantification();

                lightQuant.setIntensity(lightIntensity);
                lightQuant.setSpectrumKey(spectrumID);
                lightQuant.setWeight(QuantificationWeight.LIGHT);
                lightQuant.setQuantificationGroup(quantGroup);

                heavyQuant.setIntensity(heavyIntensity);
                heavyQuant.setSpectrumKey(spectrumID);
                heavyQuant.setWeight(QuantificationWeight.HEAVY);
                heavyQuant.setQuantificationGroup(quantGroup);

                quantificationsList.add(lightQuant);
                quantificationsList.add(heavyQuant);
            }
            
            quantGroup.setQuantifications(quantificationsList);
            quantificationGroupMap.put(quantificationGroupID, quantGroup);
        }
        
        return quantificationGroupMap;
    }

    private enum QuantificationGroupHeaders {

        ID("id"),
        LOWINTENSITY("Intensity L"),
        HIGHINTENSITY("Intensity H"),
        PEPTIDEID("Peptide ID"),
        SPECTRUMIDS("MS/MS IDs"),
        BESTSPECTRUMID("Best MS/MS");
        public String headerName;

        private QuantificationGroupHeaders(String aHeaderName) {
            headerName = aHeaderName;
        }
    }
}

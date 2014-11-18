/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.io.maxquant;

import com.compomics.colims.model.Quantification;
import com.compomics.colims.model.QuantificationGroup;
import com.compomics.colims.model.enums.QuantificationWeight;
import java.io.File;
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
     * @param aQuantificationFile the file to parse
     * @return a Map key: the spectrum ID, value: the list of quantifications
     */
    public static Map<Integer, List<Quantification>> parseMaxQuantQuantification(File aQuantificationFile) throws IOException {
        Map<Integer, List<Quantification>> processedSpectrumMap = new HashMap<>(1000);

        TabularFileLineValuesIterator iter = new TabularFileLineValuesIterator(aQuantificationFile);
        Map<String, String> quantificationLine;

        while (iter.hasNext()) {
            quantificationLine = iter.next();

            //parse heavy one
            String heavyIntensityAsString = quantificationLine.get(MaxQuantQuantificationParser.QuantificationGroupHeaders.HIGHINTENSITY.headerName);

            double heavyIntensity = 0.0;
            if (heavyIntensityAsString != null) {
                if (!heavyIntensityAsString.isEmpty() && !heavyIntensityAsString.equalsIgnoreCase("nan")) {
                    heavyIntensity = Double.parseDouble(heavyIntensityAsString);
                }
            }

            //parse light one 
            String lightIntensityAsString = quantificationLine.get(MaxQuantQuantificationParser.QuantificationGroupHeaders.LOWINTENSITY.headerName);

            double lightIntensity = 0.0;
            if (heavyIntensityAsString != null) {
                if (!lightIntensityAsString.isEmpty() && !lightIntensityAsString.equalsIgnoreCase("nan")) {
                    lightIntensity = Double.parseDouble(lightIntensityAsString);
                }
            }
            String[] spectrumIDsString = quantificationLine.get(MaxQuantQuantificationParser.QuantificationGroupHeaders.SPECTRUMIDS.headerName).split(";");

            //int quantificationGroupID = Integer.parseInt(quantificationLine.get(MaxQuantQuantificationParser.QuantificationGroupHeaders.ID.headerName));
            QuantificationGroup quantGroup = new QuantificationGroup();

            List<Quantification> quantificationsList = new ArrayList<>();

            for (String aSpectrumID : spectrumIDsString) {

                List<Quantification> spectrumSpecificQuantificationsList = new ArrayList<Quantification>();
                int spectrumID = Integer.parseInt(aSpectrumID);

                Quantification lightQuant = new Quantification();
                Quantification heavyQuant = new Quantification();

                lightQuant.setIntensity(lightIntensity);
                // lightQuant.setSpectrum(null);
                lightQuant.setWeight(QuantificationWeight.LIGHT);
//                lightQuant.setQuantificationGroup(quantGroup);

                heavyQuant.setIntensity(heavyIntensity);
                //  heavyQuant.setSpectrum(null);
                heavyQuant.setWeight(QuantificationWeight.HEAVY);
//                heavyQuant.setQuantificationGroup(quantGroup);

                spectrumSpecificQuantificationsList.add(lightQuant);
                spectrumSpecificQuantificationsList.add(heavyQuant);

                mergeIntoTotalResults(spectrumID, spectrumSpecificQuantificationsList, processedSpectrumMap);
                quantificationsList.addAll(spectrumSpecificQuantificationsList);
            }
//            quantGroup.setQuantifications(quantificationsList);

        }

        return processedSpectrumMap;
    }

    /**
     * merges the spectrum lines from a single result into the master returning
     * map
     *
     */
    private static void mergeIntoTotalResults(int spectrumID, List<Quantification> spectrumSpecificQuantificationsList, Map<Integer, List<Quantification>> processedSpectrumMap) {
        if (processedSpectrumMap.keySet().contains(spectrumID)) {
            List<Quantification> currentSpectrumQuantList = processedSpectrumMap.get(spectrumID);
            currentSpectrumQuantList.addAll(spectrumSpecificQuantificationsList);
        } else {
            processedSpectrumMap.put(spectrumID, spectrumSpecificQuantificationsList);
        }
    }

    private enum QuantificationGroupHeaders {

        ID("id"),
        LOWINTENSITY("Intensity L"),
        HIGHINTENSITY("Intensity H"),
        PEPTIDEID("Peptide ID"),
        SPECTRUMIDS("MS/MS IDs"),
        BESTSPECTRUMID("Best MS/MS");
        public final String headerName;

        private QuantificationGroupHeaders(String aHeaderName) {
            headerName = aHeaderName;
        }
    }
}

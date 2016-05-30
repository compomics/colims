package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.model.Spectrum;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Object to hold identified and unidentified spectra
 * @author  demet on 5/30/2016.
 */

public class MaxQuantSpectra {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(MaxQuantSpectra.class);

    /**
     * This map is used to link with apl files
     * The identified spectra map (key: Spectrum Key; value: spectrum).
     */
    private Map<String, Spectrum> spectra = new HashMap<>();

    /**
     * This map is used to link with msms files
     * The identified spectra map (key: Spectrum Key; value: spectrum).
     */
    private Map<Integer, Spectrum> identifiedSpectra = new HashMap<>();


    /**
     * The list of unidentified spectra.
     */
    private List<Spectrum> unidentifiedSpectra = new ArrayList<>();


    public MaxQuantSpectra() {
    }

    /**
     * Get the hashmap of identified spectra (for apl file)
     *
     * @return the identified spectra
     */
    public Map<String, Spectrum> getSpectra() {
        return spectra;
    }

    /**
     * Get the hashmap of identified spectra (for msms file)
     *
     * @return the identified spectra
     */
    public Map<Integer, Spectrum> getIdentifiedSpectra() {
        return identifiedSpectra;
    }

    /**
     * Get the list of unidentified spectra.
     *
     * @return the unidentified spectra
     */
    public List<Spectrum> getUnidentifiedSpectra() {
        return unidentifiedSpectra;
    }

    /**
     * Match the msmsIDs map with the spectra map
     * @param maxQuantSpectra
     * @param msmsIds
     */
    public void createIdentifiedSpectra(Map<String, Integer> msmsIds){
        getSpectra().entrySet().stream().forEach(e -> getIdentifiedSpectra().put(msmsIds.get(e.getKey()), e.getValue()));
    }
}

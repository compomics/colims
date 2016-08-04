package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.model.Spectrum;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Object to hold identified and unidentified aplSpectra
 * @author  demet on 5/30/2016.
 */

public class MaxQuantSpectra {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(MaxQuantSpectra.class);

    /**
     * This map is used to link with apl files
     * The aplSpectra map (key: Spectrum Key; value: spectrum).
     */
    private Map<String, Spectrum> aplSpectra = new HashMap<>();

    /**
     * This map is used to link with spectra IDs in msms file
     * The spectrumIDs map (key: Spectrum; value: list of msms ID)
     */
    private Map<Spectrum, List<Integer>> spectrumIDs = new HashMap<>();

    /**
     * The list of unidentified aplSpectra.
     */
    private List<Spectrum> unidentifiedSpectra = new ArrayList<>();

    /**
     * ommittedSpectraKeys to avoid keeping them as unidentified spectra
     */
    private List<String> ommittedSpectraKeys = new ArrayList<>();
    
  
    public MaxQuantSpectra() {
    }

    /**
     * Get the hashmap of aplSpectra (for apl file)
     *
     * @return the identified aplSpectra
     */
    public Map<String, Spectrum> getAplSpectra() {
        return aplSpectra;
    }

    /**
     * Get the hashmap of spectrumIDs
     *
     * @return spectrumIDs
     */
    public Map<Spectrum, List<Integer>> getSpectrumIDs() { return spectrumIDs; }


    /**
     * Get the list of unidentified aplSpectra.
     *
     * @return the unidentified aplSpectra
     */
    public List<Spectrum> getUnidentifiedSpectra() {
        return unidentifiedSpectra;
    }

    /**
     * Get ommittedSpectraKeys
     * @return ommittedSpectraKeys
     */
    public List<String> getOmmittedSpectraKeys() {
        return ommittedSpectraKeys;
    }
    
    /**
     * Clear max quant spectra.
     */
    public void clear(){
        aplSpectra.clear();
        spectrumIDs.clear();
        unidentifiedSpectra.clear();
    }

}

package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.model.Spectrum;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Object to hold identified and unidentified aplKeyToSpectrums
 *
 * @author demet on 5/30/2016.
 */

public class MaxQuantSpectra {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(MaxQuantSpectra.class);

    /**
     * This map is used to link with apl files.
     * The aplKeyToSpectrums map (key: Spectrum Key; value: spectrum).
     */
    private final Map<String, Spectrum> aplKeyToSpectrums = new HashMap<>();

    /**
     * This map is used to link with spectra IDs in msms file.
     * The spectrumToMsmsIds map (key: Spectrum; value: list of msms ID)
     */
    private final Map<Spectrum, List<Integer>> spectrumToMsmsIds = new HashMap<>();

    /**
     * The list of unidentified aplKeyToSpectrums.
     */
    private final List<Spectrum> unidentifiedSpectra = new ArrayList<>();

    /**
     * ommittedSpectrumKeys to avoid keeping them as unidentified spectra.
     */
    private final List<String> ommittedSpectrumKeys = new ArrayList<>();


    public MaxQuantSpectra() {
    }

    /**
     * Get the hashmap of aplKeyToSpectrums (for apl file).
     *
     * @return the identified aplKeyToSpectrums
     */
    public Map<String, Spectrum> getAplKeyToSpectrums() {
        return aplKeyToSpectrums;
    }

    /**
     * Get the hashmap of spectrumToMsmsIds.
     *
     * @return spectrumToMsmsIds
     */
    public Map<Spectrum, List<Integer>> getSpectrumToMsmsIds() {
        return spectrumToMsmsIds;
    }


    /**
     * Get the list of unidentified aplKeyToSpectrums.
     *
     * @return the unidentified aplKeyToSpectrums
     */
    public List<Spectrum> getUnidentifiedSpectra() {
        return unidentifiedSpectra;
    }

    /**
     * Get ommittedSpectrumKeys.
     *
     * @return ommittedSpectrumKeys
     */
    public List<String> getOmmittedSpectrumKeys() {
        return ommittedSpectrumKeys;
    }

    /**
     * Clear max quant spectra.
     */
    public void clear() {
        aplKeyToSpectrums.clear();
        spectrumToMsmsIds.clear();
        unidentifiedSpectra.clear();
        ommittedSpectrumKeys.clear();
    }

}

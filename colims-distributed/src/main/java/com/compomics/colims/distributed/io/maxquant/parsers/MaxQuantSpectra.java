package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.model.Spectrum;

import java.util.*;

/**
 * Object to hold identified and unidentified spectra.
 *
 * @author demet on 5/30/2016.
 */
public class MaxQuantSpectra {

    /**
     * This map links spectrum apl file keys with their associated {@link Spectrum} instance.
     * The spectra map (key: the apl file key (RAW file name + scan index); value: Spectrum).
     */
    private final Map<String, Spectrum> spectra = new HashMap<>();
    /**
     * This map links spectra apl keys with the msms.txt IDs.
     * The spectrumToPsms map (key: apl key; value: set of msms IDs)
     */
    private final Map<String, Set<Integer>> spectrumToPsms = new HashMap<>();
    /**
     * This map links run names with the associated spectrum apl keys.
     */
    private final Map<String, Set<String>> runToSpectrums = new HashMap<>();
    /**
     * The map of unidentified spectra (key: RAW file name; value: the list of associated unidentified {@link Spectrum}
     * instances).
     */
    private final Map<String, List<Spectrum>> unidentifiedSpectra = new HashMap<>();
    /**
     * The list of omitted spectrum keys to avoid keeping them as unidentified spectra.
     */
    private final List<String> omittedSpectrumKeys = new ArrayList<>();

    /**
     * No-arg constructor.
     */
    public MaxQuantSpectra() {
    }

    /**
     * Get the map of spectra (for apl file).
     *
     * @return the identified spectra
     */
    public Map<String, Spectrum> getSpectra() {
        return spectra;
    }

    /**
     * Get the map of spectrumToPsms.
     *
     * @return spectrumToPsms
     */
    public Map<String, Set<Integer>> getSpectrumToPsms() {
        return spectrumToPsms;
    }

    /**
     * Get the list of unidentified spectra.
     *
     * @return the unidentified spectra
     */
    public Map<String, List<Spectrum>> getUnidentifiedSpectra() {
        return unidentifiedSpectra;
    }

    /**
     * Get omittedSpectrumKeys.
     *
     * @return omittedSpectrumKeys
     */
    public List<String> getOmittedSpectrumKeys() {
        return omittedSpectrumKeys;
    }

    /**
     * Get the runToSpectrums.
     *
     * @return the runToSpectrums
     */
    public Map<String, Set<String>> getRunToSpectrums() {
        return runToSpectrums;
    }
}

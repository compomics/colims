/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.cache.impl;

import com.compomics.colims.core.cache.Cache;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author demet
 */

public class UniprotProteinCache extends LinkedHashMap<String, Map<String, String>> implements Cache<String, Map<String, String>>{

    private static final long serialVersionUID = 1L;

    private final Integer  uniprotMaxCashSize;

    public UniprotProteinCache(Integer uniprotMaxCashSize) {
        this.uniprotMaxCashSize = uniprotMaxCashSize;
    }

    /**
     * Puts the given protein group main accession and UniProt map coming from UniProt service.
     * If the maximum cache size is reached, the first added element is removed and replaced 
     * by the given UniProt map.
     * @param mainAccession the protein group main accession
     * @param uniprotMap map of UniProt information
     */
    @Override
    public void putInCache(String mainAccession, Map<String, String> uniprotMap) {
        this.put(mainAccession, uniprotMap);
    }

    /**
     * Gets the UniProt map by its key, mainAccession. If nothing is found, null is
     * returned.
     * @param mainAccession
     * @return the UniProt map
     */
    @Override
    public Map<String, String> getFromCache(String mainAccession) {
       return this.get(mainAccession);
    }

    @Override
    public int getCacheSize() {
        return this.size();
    }

    @Override
    public void clearCache() {
        this.clear();
    }
    
    @Override
    protected boolean removeEldestEntry(Map.Entry<String, Map<String, String>> eldest) {
        return this.size() >= uniprotMaxCashSize;
    }
}

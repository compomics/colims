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

    @Override
    public void putInCache(String key, Map<String, String> value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, String> getFromCache(String key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getCacheSize() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clearCache() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.cache;

/**
 *
 * @author demet, niels
 * @param <K>
 * @param <V>
 */
public interface Cache<K,V> {
    /**
     * Puts an entry in the cache
     *
     * @param key the key
     * @param value the value
     */
    void putInCache(K key, V value);

    /**
     * Gets a value from the cache, returns null if nothing was found
     *
     * @param key the key
     * @return the found value
     */
    V getFromCache(K key);

    /**
     * Gets the cache size.
     *
     * @return the cache size value
     */
    int getCacheSize();
    
    /**
     * Clears the cache
     */
    void clearCache();

}

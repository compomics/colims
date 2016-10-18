/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service;

import java.io.IOException;
import java.util.Map;
import org.springframework.web.client.RestClientException;

/**
 * This interface provides methods for accessing the UniProt service
 * 
 * @author demet
 */
public interface UniProtService {
    
    /**
     * Retrieve uniProt protein sequence and functional information by accession
     * Returns an empty list when nothing was found.
     * @param accession
     * @return the map of the information needed.
     * @throws IOException              in case of an I/O related problem
     */
    Map<String,String> getUniProtByAccession(String accession) throws RestClientException, IOException;
}

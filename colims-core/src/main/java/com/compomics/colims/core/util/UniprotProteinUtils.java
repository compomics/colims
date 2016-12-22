/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.util;

import com.compomics.colims.core.cache.Cache;
import com.compomics.colims.core.cache.impl.UniprotProteinCache;
import com.compomics.colims.core.io.fasta.FastaDbAccessionParser;
import com.compomics.colims.core.service.UniProtService;
import com.compomics.colims.model.FastaDb;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author demet
 */
@Component("uniprotProteinUtils")
public class UniprotProteinUtils {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(FastaDbAccessionParser.class);

    @Autowired
    private UniProtService uniProtService;
    
    @Value("${uniprot_map_cache.maximum_cache_size}")
    private Integer  uniprotMaxCashSize= 0;
    
    Cache cache;
    
    @PostConstruct
    public void init(){
        cache = new UniprotProteinCache(uniprotMaxCashSize);
    }
    
    /**
     * For given protein accession and fastaDb, this method converts accession
     * to UniProt accession, and by using UniProt accession it returns protein
     * sequence and functional information.
     *
     * @param accession main accession of the protein group
     * @param fastaDb the FastaDb where protein comes from
     * @return the map of protein information such as description, taxid,
     * species (key: information type; value: protein sequence and functional
     * information).
     * @throws java.io.IOException
     */
    public Map<String, String> getFastaDbUniprotInformation(String accession, FastaDb fastaDb) throws IOException {
        // define uniprot map 
        Map<String, String> uniProtMap = new HashMap<>();
        if(cache.getFromCache(accession) != null){
            uniProtMap = (Map<String, String>) cache.getFromCache(accession);
            return uniProtMap;
        } else {
            // define uniprot accession list
            List<String> uniprotAccessions = new ArrayList<>();

            switch (fastaDb.getDatabaseName()) {
                case "UNIPROT":
                    uniprotAccessions.add(accession);
                    uniProtMap = uniProtService.getUniProtByAccession(uniprotAccessions.get(0));
                    break;
                case "Not in the EMBL-EBI list":
                    break;
                default:
                    uniprotAccessions = AccessionConverter.convertToUniProt(accession, fastaDb.getDatabaseName());
                    uniProtMap = uniProtService.getUniProtByAccession(uniprotAccessions.get(0));
                    break;
            }

            cache.putInCache(accession, uniProtMap);

            return uniProtMap;
        }

    }
}

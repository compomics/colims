/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service;

import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.SearchAndValidationSettings;
import com.compomics.colims.model.enums.FastaDbType;
import java.util.List;
import java.util.Map;

/**
 * This interface provides service methods for the FastaDb class.
 *
 * @author Niels Hulstaert
 */
public interface FastaDbService extends GenericService<FastaDb, Long> {

    /**
     * Find the FastaDb instances by one or more types; return all the FASTA
     * databases that have been used as a given type in one or more searches.
     *
     * @param fastaDbTypes the list of FastaDbType instances
     * @return the found FastaDb instances
     */
    List<FastaDb> findByFastaDbType(List<FastaDbType> fastaDbTypes);
    
    /**
     * Get all distinct parse rules stored in the database.
     * @return list of distinct parse rules
     */
    List<String> getAllParseRules();
    
    /**
     * Find the FastaDb instances by Search and Validation Settings.
     * 
     * @param searchAndValidationSettings
     * @return the found FastaDb instances and their type.
     */
    Map<FastaDb, FastaDbType> findBySearchAndValidationSettings(SearchAndValidationSettings searchAndValidationSettings);

}

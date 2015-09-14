package com.compomics.colims.core.service;

import com.compomics.colims.model.ProteinAccession;
import com.compomics.colims.model.ProteinGroup;

import java.util.List;

/**
 * Created by Iain on 14/09/2015.
 */
public interface ProteinAccessionService extends GenericService<ProteinAccession, Long> {
    /**
     * Get all accessions associated with a group of proteins
     *
     * @param proteinGroup Protein group
     * @return List of ProteinAccessions
     */
    List<ProteinAccession> getAccessionsForProteinGroup(ProteinGroup proteinGroup);
}
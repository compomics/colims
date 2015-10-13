package com.compomics.colims.core.service;

import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.ProteinAccession;
import com.compomics.colims.model.ProteinGroup;

import java.util.List;

/**
 * This interface provides service methods for the ProteinAccession class.
 * <p/>
 * Created by Iain on 14/09/2015.
 */
public interface ProteinAccessionService extends GenericService<ProteinAccession, Long> {

    /**
     * Get all accession strings associated with the specified group of proteins.
     *
     * @param proteinGroup the protein group
     * @return the list of protein accession strings
     */
    List<String> getAccessionsForProteinGroup(ProteinGroup proteinGroup);

    /**
     * Get all protein accession strings associated with a given peptide.
     *
     * @param peptide the peptide to find accessions for
     * @return the list of accession strings
     */
    List<String> getProteinAccessionsForPeptide(Peptide peptide);
}

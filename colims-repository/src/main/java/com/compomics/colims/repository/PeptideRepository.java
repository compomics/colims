package com.compomics.colims.repository;

import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.Protein;

import java.util.List;

/**
 * This interface provides repository methods for the Peptide class.
 *
 * @author Niels Hulstaert
 */
public interface PeptideRepository extends GenericRepository<Peptide, Long> {
    List<Peptide> getPeptidesForProtein(Protein protein);
}

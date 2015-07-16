package com.compomics.colims.repository;

import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.colims.model.PeptideHasProtein;
import com.compomics.colims.model.Protein;

import java.util.List;

/**
 * This interface provides repository methods for the Peptide class.
 *
 * @author Niels Hulstaert
 */
public interface PeptideRepository extends GenericRepository<Peptide, Long> {
    /**
     * Return data about all peptides relating to a given protein. Uses a
     * list of spectra to ensure results relate to the current run.
     * @param protein A protein
     * @param spectrumIds List of spectrum ids to restrict results
     * @return
     */
    List<PeptideHasProtein> getPeptidesForProtein(Protein protein, List<Long> spectrumIds);

    /**
     * Get a list of Peptide objects matching the given sequence, limiting
     * results to the given list of spectrum ids
     * @param sequence Peptide sequence
     * @param spectrumIds List of spectrum ids to restrict results
     * @return
     */
    List getPeptidesFromSequence(String sequence, List<Long> spectrumIds);

    List<PeptideHasModification> getModificationsForMultiplePeptides(List<Peptide> peptides);
}

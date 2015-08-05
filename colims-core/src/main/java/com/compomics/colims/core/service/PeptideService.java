package com.compomics.colims.core.service;

import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.colims.model.PeptideHasProteinGroup;
import com.compomics.colims.model.Protein;

import java.util.List;

/**
 * This interface provides service methods for the Peptide class.
 *
 * @author Niels Hulstaert
 */
public interface PeptideService extends GenericService<Peptide, Long> {

    /**
     * Fetch the peptide peptideHasModifications.
     *
     * @param peptide the peptide entity
     */
    void fetchPeptideHasModifications(Peptide peptide);

    /**
     * Return data about all peptides relating to a given protein. Uses a list of spectra to ensure results relate to
     * the current run.
     *
     * @param protein     A protein
     * @param spectrumIds List of spectrum ids to restrict results
     * @return List of PeptideHasProteinGroup objects
     */
    List<PeptideHasProteinGroup> getPeptidesForProtein(Protein protein, List<Long> spectrumIds);

    /**
     * Get a list of Peptide objects matching the given sequence, limiting results to the given list of spectrum ids
     *
     * @param sequence    Peptide sequence
     * @param spectrumIds List of spectrum ids to restrict results
     * @return List of Peptides
     */
    List getPeptidesFromSequence(String sequence, List<Long> spectrumIds);

    /**
     * Get all modifications for a list of peptides (which are likely to have the same sequence)
     *
     * @param peptides A list of peptides to search on
     * @return List of PeptideHasModification objects
     */
    List<PeptideHasModification> getModificationsForMultiplePeptides(List<Peptide> peptides);
}

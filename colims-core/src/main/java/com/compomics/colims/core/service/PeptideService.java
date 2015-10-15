package com.compomics.colims.core.service;

import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.colims.model.Spectrum;

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

    /**
     * Get peptides linked to spectrum
     *
     * @param spectrum Spectrum with which to search peptides
     * @return List of peptide objects
     */
    List<Peptide> getPeptidesForSpectrum(Spectrum spectrum);
}

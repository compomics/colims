package com.compomics.colims.repository;

import com.compomics.colims.model.*;

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
     * @param proteinGroup  A protein
     * @param spectrumIds   List of spectrum ids to restrict results
     * @return List of PeptideHasProteinGroup objects
     */
    List<Peptide> getPeptidesForProteinGroup(ProteinGroup proteinGroup, List<Long> spectrumIds);

    /**
     * Get a list of Peptide objects matching the given sequence, limiting
     * results to the given list of spectrum ids
     * @param sequence Peptide sequence
     * @param spectrumIds List of spectrum ids to restrict results
     * @return List of Peptides
     */
    List getPeptidesFromSequence(String sequence, List<Long> spectrumIds);

    /**
     * Get all modifications for a list of peptides (which are likely to have the same sequence)
     * @param peptides A list of peptides to search on
     * @return List of PeptideHasModification objects
     */
    List<PeptideHasModification> getModificationsForMultiplePeptides(List<Peptide> peptides);

    /**
     * Get all peptides associated with a spectrum
     *
     * @param spectrum Spectrum to search peptides with
     * @return List of peptides
     */
    List<Peptide> getPeptidesForSpectrum(Spectrum spectrum);
}

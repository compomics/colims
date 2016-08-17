package com.compomics.colims.client.model.table.model;

import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.colims.repository.hibernate.PeptideDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Representation of a row in the peptides for the protein group table. One row
 * represents a distinct peptide sequence + unique modifications entity for a
 * given protein group.
 * <p/>
 * Created by Iain on 08/07/2015.
 */
public class PeptideTableRow {

    /**
     * The peptide sequence.
     */
    private final String sequence;
    /**
     * The main group protein sequence.
     */
    private final String proteinSequence;
    /**
     * The list of PeptideDTO instances related to this peptide instance.
     */
    private final List<PeptideDTO> peptideDTOs = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param peptideDTO the PeptideDTO instance
     * @param proteinSequence the main group protein sequence
     */
    public PeptideTableRow(PeptideDTO peptideDTO, String proteinSequence) {
        this.sequence = peptideDTO.getPeptide().getSequence();
        this.proteinSequence = proteinSequence;
        peptideDTOs.add(peptideDTO);
    }

    /**
     * Get the peptide sequence
     *
     * @return the peptide sequence String
     */
    public String getSequence() {
        return sequence;
    }

    /**
     * Get the main group protein sequence.
     *
     * @return the protein sequence String
     */
    public String getProteinSequence() {
        return proteinSequence;
    }

    /**
     * Add a peptideDTO instance.
     *
     * @param peptideDTO the PeptideDTO instance
     */
    public void addPeptideDTO(PeptideDTO peptideDTO) {
        peptideDTOs.add(peptideDTO);
    }

    /**
     * Get the number of spectra associated with this row.
     *
     * @return the number of spectra
     */
    public long getSpectrumCount() {
        return peptideDTOs.size();
    }

    /**
     * Calculate the peptide confidence based on the
     * peptidePostErrorProbability.
     *
     * @return the peptide confidence
     */
    public double getPeptideConfidence() {
        double confidence = 100.0 * (1 - peptideDTOs.get(0).getPeptidePostErrorProbability());
        if (confidence <= 0) {
            confidence = 0;
        }

        return confidence;
    }

    /**
     * Get the number of protein groups linked to this peptide row.
     *
     * @return the number of protein groups
     */
    public long getProteinGroupCount() {
        return peptideDTOs.get(0).getProteinGroupCount();
    }

    /**
     * Get the list of peptides for this row.
     *
     * @return the list of peptides
     */
    public List<Peptide> getPeptides() {
        List<Peptide> peptides = new ArrayList<>(peptideDTOs.size());
        peptides.addAll(peptideDTOs.stream().map(PeptideDTO::getPeptide).collect(Collectors.toList()));

        return peptides;
    }

    /**
     * Return the list of PeptideHasModification instances associated with the
     * first peptide(DTO) in the peptideDTOs list, assuming that all peptides
     * have the same modifications.
     *
     * @return the list of PeptideHasModification instances, can be an empty
     * list if the peptide has no modifications.
     */
    public List<PeptideHasModification> getPeptideHasModifications() {
        return peptideDTOs.get(0).getPeptide().getPeptideHasModifications();
    }

}

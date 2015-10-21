package com.compomics.colims.client.model;

import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.util.CompareUtils;
import com.compomics.colims.repository.hibernate.model.PeptideDTO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Representation of a row in the peptides for protein group table. One row represents a distinct peptide sequence +
 * modifications entity for a given protein group.
 * <p/>
 * Created by Iain on 08/07/2015.
 */
public class PeptideTableRow {

    /**
     * The peptide sequence.
     */
    private final String sequence;
    /**
     * The peptide annotated sequence.
     */
    private StringBuilder annotatedSequence;
    /**
     * The list of PeptideDTO instances related to this peptide instance.
     */
    private final List<PeptideDTO> peptideDTOs = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param peptideDTO the PeptideDTO instance
     */
    public PeptideTableRow(PeptideDTO peptideDTO) {
        sequence = peptideDTO.getPeptide().getSequence();
        annotatedSequence = new StringBuilder();
        peptideDTOs.add(peptideDTO);
    }

    /**
     * Add a peptideDTO instance. If the peptideDTOs list already contains a PeptideDTO object, they have to have the
     * same peptide sequence and modifications. If not, an IllegalArgumentException is thrown.
     *
     * @param peptideDTO the PeptideDTO instance
     * @throws IllegalArgumentException thrown in case trying to add an unequal PeptideDTO instance
     */
    public void addPeptideDTO(PeptideDTO peptideDTO) {
        if (peptideDTOs.isEmpty()) {
            peptideDTOs.add(peptideDTO);
        } else {
            if (equalsWithoutCharge(peptideDTO.getPeptide())) {
                peptideDTOs.add(peptideDTO);
            } else {
                throw new IllegalArgumentException("The PeptideDTO object does not match with the already added ones.");
            }
        }
    }

    public String getSequence() {
        return sequence;
    }

    public long getSpectrumCount() {
        return peptideDTOs.size();
    }

    /**
     * Calculate the peptide confidence based on the peptidePostErrorProbability.
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
     * Get the list of spectra.
     *
     * @return the list of spectra
     */
    public List<Spectrum> getSpectra() {
        List<Spectrum> spectra = new ArrayList<>(peptideDTOs.size());
        spectra.addAll(peptideDTOs.stream().map(peptideDTO -> peptideDTO.getPeptide().getSpectrum()).collect(Collectors.toList()));

        return spectra;
    }

    /**
     * Return the list of PeptideHasModification instances associated with the first peptide(DTO) in the peptideDTOs
     * list, assuming that all peptides have the same modifications.
     *
     * @return the list of PeptideHasModification instances, can be an empty list if the peptide has no modifications.
     */
    public List<PeptideHasModification> getPeptideHasModifications() {
        return peptideDTOs.get(0).getPeptide().getPeptideHasModifications();
    }

    /**
     * Get or create an annotated sequence for the peptide.
     *
     * @return Annotated string
     */
    public String getAnnotatedSequence() {
        if (annotatedSequence.length() > 0) {
            return annotatedSequence.toString();
        } else {
            annotatedSequence = new StringBuilder();

            int[] mods = new int[sequence.length()];

            for (PeptideHasModification phMod : peptideDTOs.get(0).getPeptide().getPeptideHasModifications()) {
                mods[phMod.getLocation()]++;
            }

            for (int i = 0; i < sequence.length(); ++i) {
                if (mods[i] > 0) {
                    annotatedSequence.append("<b>")
                            .append(sequence.charAt(i))
                            .append("</b>");
                } else {
                    annotatedSequence.append(sequence.charAt(i));
                }
            }

            return annotatedSequence.toString();
        }
    }

    /**
     * Check if the given peptide equals the first peptide in the peptideDTOs list, minus the charge.
     *
     * @param peptide the Peptide instance
     * @return true if equal
     */
    private boolean equalsWithoutCharge(Peptide peptide) {
        Peptide firstPeptide = peptideDTOs.get(0).getPeptide();

        if (!firstPeptide.getSequence().equals(peptide.getSequence())) return false;
        if (firstPeptide.getTheoreticalMass() != null ? !CompareUtils.equals(firstPeptide.getTheoreticalMass(), peptide.getTheoreticalMass()) : peptide.getTheoreticalMass() != null)
            return false;
        if (firstPeptide.getPsmProbability() != null ? !CompareUtils.equals(firstPeptide.getPsmProbability(), peptide.getPsmPostErrorProbability()) : peptide.getPsmProbability() != null)
            return false;
        if (firstPeptide.getPsmPostErrorProbability() != null ? !CompareUtils.equals(firstPeptide.getPsmPostErrorProbability(), peptide.getPsmPostErrorProbability()) : peptide.getPsmPostErrorProbability() != null) {
            return false;
        }
        //check the PeptideHasModification lists
        Set<PeptideHasModification> firstSet = new HashSet<>(firstPeptide.getPeptideHasModifications());
        Set<PeptideHasModification> secondSet = new HashSet<>(peptide.getPeptideHasModifications());
        return firstSet.equals(secondSet);
    }
}

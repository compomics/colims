package com.compomics.colims.client.model;

import com.compomics.colims.model.PeptideHasModification;
import com.compomics.colims.repository.hibernate.model.PeptideDTO;

import java.util.ArrayList;
import java.util.List;

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
    private String sequence;
    /**
     * The peptide annotated sequence.
     */
    private StringBuilder annotatedSequence;
    /**
     * The peptide charge.
     */
    private int charge;
    /**
     * The list of PeptideDTO instances related to this peptide instance.
     */
    private List<PeptideDTO> peptideDTOs = new ArrayList<>();

    public PeptideTableRow(PeptideDTO peptideDTO) {
        sequence = peptideDTO.getPeptide().getSequence();
        annotatedSequence = new StringBuilder();
        charge = peptideDTO.getPeptide().getCharge() != null ? peptideDTO.getPeptide().getCharge() : 0;
        peptideDTOs.add(peptideDTO);
    }

    public void addPeptideDTO(PeptideDTO peptideDTO) {
        peptideDTOs.add(peptideDTO);
    }

    public String getSequence() {
        return sequence;
    }

    public long getSpectrumCount() {
        return peptideDTOs.size();
    }

    public int getCharge() {
        return charge;
    }

    public List<PeptideHasModification> getPeptideHasModifications() {
        return peptideDTOs.get(0).getPeptide().getPeptideHasModifications();
    }

    /**
     * Get or create an annotated sequence for the peptide
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
}

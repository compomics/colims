package com.compomics.colims.client.model;

import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a row in the Peptides table Created by Iain on 08/07/2015.
 */
public class PeptideTableRow {

    private String sequence;
    private StringBuilder annotatedSequence;
    private int charge;
    private List<Peptide> peptides = new ArrayList<>();
    private List<PeptideHasModification> peptideHasModifications = new ArrayList<>();

    public PeptideTableRow(Peptide peptide) {
        sequence = peptide.getSequence();
        annotatedSequence = new StringBuilder();
        charge = peptide.getCharge() != null ? peptide.getCharge() : 0;
        peptides.add(peptide);
    }

    public void addPeptide(Peptide peptide) {
        peptides.add(peptide);
    }

    public String getSequence() {
        return sequence;
    }

    public List<PeptideHasModification> getPeptideHasModifications() {
        return peptideHasModifications;
    }

    public List<Peptide> getPeptides() {
        return peptides;
    }

    public long getSpectrumCount() {
        return peptides.size();
    }

    public int getCharge() {
        return charge;
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

            for (PeptideHasModification phMod : peptideHasModifications) {
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

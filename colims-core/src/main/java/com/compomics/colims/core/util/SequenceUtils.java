package com.compomics.colims.core.util;

import com.compomics.util.experiment.biology.AminoAcidPattern;
import com.compomics.util.preferences.SequenceMatchingPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * This utility class provides convenience methods for retrieving sequence related information (e.g. location of
 * peptides in protein sequences). It uses the Compomics Utilities methods internally.
 * <p/>
 * Created by Niels Hulstaert on 8/06/15.
 */
public class SequenceUtils {

    /**
     * Default {@link SequenceMatchingPreferences} instance;
     */
    private static final SequenceMatchingPreferences sequenceMatchingPreferences = new SequenceMatchingPreferences();

    /**
     * Private no-arg constructor.
     */
    private SequenceUtils() {
    }

    /**
     * Calculate position information wrapped in a {@link PeptidePosition} instance of a peptide sequence in a protein.
     * All possible occurrences of the peptide in the protein sequence are considered.
     *
     * @param proteinSequence the protein sequence
     * @param peptideSequence the peptide sequence
     * @return the list of PeptidePosition instance
     * @throws IllegalStateException error thrown if the peptide sequence could not be found in the protein sequence
     */
    public static List<PeptidePosition> getPeptidePositions(String proteinSequence, String peptideSequence) {
        List<PeptidePosition> peptidePositions = new ArrayList<>();

        List<Integer> peptideStartIndexes = getPeptideStartIndexes(proteinSequence, peptideSequence);

        if (!peptideStartIndexes.isEmpty()) {
            for (Integer peptideStart : peptideStartIndexes) {
                PeptidePosition peptidePosition = new PeptidePosition();

                //get start and end position
                peptidePosition.setStartPosition(peptideStart);
                peptidePosition.setEndPosition(peptideStart + peptideSequence.length() - 1);

                //get surrounding amino acids
                Character[] surroundingAAs = getSurroundingAAs(proteinSequence, peptideSequence, peptideStart);
                if (surroundingAAs[0] != null) {
                    peptidePosition.setPreAA(surroundingAAs[0]);
                }
                if (surroundingAAs[1] != null) {
                    peptidePosition.setPostAA(surroundingAAs[1]);
                }

                peptidePositions.add(peptidePosition);
            }
        } else {
            throw new IllegalStateException("The peptide sequence could not be found in the protein sequence.");
        }

        return peptidePositions;
    }

    /**
     * Get the peptide start indexes in the given protein sequence.
     *
     * @param proteinSequence the protein sequence
     * @param peptideSequence the peptide sequence
     * @return the list of peptide start indexes
     */
    public static List<Integer> getPeptideStartIndexes(String proteinSequence, String peptideSequence) {
        AminoAcidPattern aminoAcidPattern = new AminoAcidPattern(peptideSequence);

        return aminoAcidPattern.getIndexes(proteinSequence, sequenceMatchingPreferences);
    }

    /**
     * Returns the amino acids surrounding a peptide in the sequence of the given protein.
     *
     * @param proteinSequence the protein sequence
     * @param peptideSequence the sequence of the peptide of interest
     * @param startPosition   the peptide start position in the protein
     * @return the amino acids surrounding a peptide in the protein sequence
     */
    public static Character[] getSurroundingAAs(String proteinSequence, String peptideSequence, int startPosition) {
        Character[] surroundingAAs = new Character[2];

        //get the AA before the peptide
        if (startPosition != 1) {
            //correct for zero based index
            surroundingAAs[0] = proteinSequence.charAt(startPosition - 2);
        }

        //get the AA after the peptide
        if (startPosition + peptideSequence.length() - 1 != proteinSequence.length()) {
            //correct for zero based index
            surroundingAAs[1] = proteinSequence.charAt(startPosition + peptideSequence.length() - 1);
        }

        return surroundingAAs;
    }

}

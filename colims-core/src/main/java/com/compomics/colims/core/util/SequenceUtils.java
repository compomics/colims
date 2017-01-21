package com.compomics.colims.core.util;

import com.compomics.util.experiment.biology.AminoAcidPattern;
import com.compomics.util.experiment.biology.AminoAcidSequence;
import com.compomics.util.preferences.SequenceMatchingPreferences;

import java.util.*;

/**
 * This utility class provides convenience methods for retrieving sequence
 * related information (e.g. location of peptides in protein sequences). It uses
 * the Compomics Utilities methods internally.
 * <p/>
 * Created by Niels Hulstaert on 8/06/15.
 */
public class SequenceUtils {

    /**
     * Default {@link SequenceMatchingPreferences} instance;
     */
    private static final SequenceMatchingPreferences SEQUENCE_MATCHING_PREFERENCES = new SequenceMatchingPreferences();

    /**
     * Private no-arg constructor.
     */
    private SequenceUtils() {
    }

    /**
     * Calculate position information wrapped in a {@link PeptidePosition}
     * instance of a peptide sequence in a protein. All possible occurrences of
     * the peptide in the protein sequence are considered.
     *
     * @param proteinSequence the protein sequence
     * @param peptideSequence the peptide sequence
     * @return the list of PeptidePosition instances, the positions are one based
     * @throws IllegalStateException error thrown if the peptide sequence could not be found in the protein sequence
     */
    public static List<PeptidePosition> getPeptidePositions(String proteinSequence, String peptideSequence) {
        List<PeptidePosition> peptidePositions = new ArrayList<>();

        List<Integer> peptideStartIndexes = getPeptideStartIndexes(proteinSequence, peptideSequence);

        if (!peptideStartIndexes.isEmpty()) {
            peptideStartIndexes.stream().forEach(peptideStart -> {
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
            });
        } else {
            throw new IllegalStateException("Peptide sequence " + peptideSequence + " could not be found in protein sequence " + proteinSequence);
        }

        return peptidePositions;
    }

    /**
     * Get the peptide start indexes in the given protein sequence. If the peptide occurs more than once, multiple start
     * positions are returned. One based.
     *
     * @param proteinSequence the protein sequence
     * @param peptideSequence the peptide sequence
     * @return the list of peptide start indexes
     */
    public static List<Integer> getPeptideStartIndexes(String proteinSequence, String peptideSequence) {
        AminoAcidPattern aminoAcidPattern = new AminoAcidPattern(new AminoAcidSequence(peptideSequence));

        return aminoAcidPattern.getIndexes(proteinSequence, SEQUENCE_MATCHING_PREFERENCES);
    }

    /**
     * Returns the amino acids surrounding a peptide in the sequence of the
     * given protein.
     *
     * @param proteinSequence the protein sequence
     * @param peptideSequence the sequence of the peptide of interest
     * @param startPosition   the peptide start position in the protein, one based
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

    /**
     * Calculate protein coverage by using protein sequence and peptide sequence.
     *
     * @param proteinSequence  the protein sequence
     * @param peptideSequences the list op peptide sequences
     * @return the protein sequence coverage
     */
    public static double calculateProteinCoverage(String proteinSequence, List<String> peptideSequences) {
        TreeMap<Integer, Boolean> coverageAnnotations = mapPeptideSequences(proteinSequence, peptideSequences);

        int coveredLength = 0;
        int tempLength = 0;
        for (Map.Entry<Integer, Boolean> entry : coverageAnnotations.entrySet()) {
            if (entry.getValue()) {
                tempLength = entry.getKey();
            } else {
                coveredLength += entry.getKey() - tempLength + 1;
            }
        }

        return (double) coveredLength / proteinSequence.length();
    }

    /**
     * Maps a list of peptide sequences onto a protein sequence.
     *
     * @param protein  the protein to match
     * @param peptides the peptides to match
     * @return the map with peptide annotations (key: AA index (null based); value: true for start of covered area,
     * false for end)
     * @throws IllegalArgumentException if a peptide sequence doesn't occur on the protein
     */
    public static TreeMap<Integer, Boolean> mapPeptideSequences(String protein, List<String> peptides) throws IllegalArgumentException {
        TreeMap<Integer, Boolean> coverageAnnotations = new TreeMap<>();

        for (String peptide : peptides) {
            List<PeptidePosition> peptidePositions = getPeptidePositions(protein, peptide);
            for (PeptidePosition peptidePosition : peptidePositions) {
                //get the annotations preceding the peptide start position
                SortedMap<Integer, Boolean> headMap = coverageAnnotations.headMap(peptidePosition.getStartPosition());
                if (headMap.isEmpty() || !headMap.get(headMap.lastKey())) {
                    coverageAnnotations.put(peptidePosition.getStartPosition() - 1, true);
                }

                //get the annotations starting from the peptide start position
                SortedMap<Integer, Boolean> tailMap = coverageAnnotations.tailMap(peptidePosition.getStartPosition() + 1);
                if (tailMap.isEmpty()) {
                    coverageAnnotations.put(peptidePosition.getEndPosition() - 1, false);
                } else {
                    //iterate over the entries
                    for (Map.Entry<Integer, Boolean> entry : new TreeMap<>(tailMap).entrySet()) {
                        if (entry.getKey() < peptidePosition.getEndPosition()) {
                            coverageAnnotations.remove(entry.getKey());
                        } else if (entry.getValue()) {
                            coverageAnnotations.put(peptidePosition.getEndPosition() - 1, false);
                            break;
                        } else {
                            break;
                        }
                    }
                }
            }
        }

        return coverageAnnotations;
    }

    /**
     * Find start position of the peptide in the protein sequence.
     *
     * @param protein the protein sequence
     * @param peptide the peptide sequence
     * @return position
     */
    public static String findStartPositionOfPeptide(String protein, String peptide) {
        protein = protein.toUpperCase(Locale.US);
        peptide = peptide.toUpperCase(Locale.US);
        // N-terminus of the protein position is 1
        if (protein.contains(peptide)) {
            return String.valueOf(protein.indexOf(peptide) + 1);
        } else {
            return null;
        }
    }

    /**
     * Find end position of the peptide in the protein sequence.
     *
     * @param protein the protein sequence
     * @param peptide the peptide sequence
     * @return position
     */
    public static String findEndPositionOfPeptide(String protein, String peptide) {
        protein = protein.toUpperCase(Locale.US);
        peptide = peptide.toUpperCase(Locale.US);
        // N-terminus of the protein position is 1
        if (protein.contains(peptide)) {
            return String.valueOf(protein.indexOf(peptide) + peptide.length());
        } else {
            return null;
        }
    }

}

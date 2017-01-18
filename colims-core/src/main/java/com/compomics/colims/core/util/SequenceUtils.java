package com.compomics.colims.core.util;

import com.compomics.util.experiment.biology.AminoAcidPattern;
import com.compomics.util.experiment.biology.AminoAcidSequence;
import com.compomics.util.preferences.SequenceMatchingPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * @return the list of PeptidePosition instance
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
     * positions are returned.
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

    /**
     * Calculate protein coverage by using protein sequence and peptide sequence.
     *
     * @param proteinSequence the protein sequence
     * @param peptides        the list op peptide sequences
     * @return the protein sequence coverage
     */
    public static double calculateProteinCoverage(String proteinSequence, List<String> peptides) {
        String mappedProtein = mapPeptideSequences(proteinSequence, peptides);

        String mergedPeptideAnnotations = mergePeptideAnnotations(mappedProtein);

        double mergedPeptideSize = 0.0;

        Pattern p = Pattern.compile(Pattern.quote("[") + "(.*?)" + Pattern.quote("]"));

        Matcher m = p.matcher(mergedPeptideAnnotations);

        while (m.find()) {
            mergedPeptideSize += m.group(1).length();
        }

        double proteinSize = proteinSequence.length();

        double coverage = mergedPeptideSize / proteinSize;

        return coverage;
    }

    /**
     * Maps a group of peptides to a protein string.
     *
     * @param protein  the protein to match
     * @param peptides the peptides to match
     * @return the annotated protein sequence where [ denotes the start of a peptide and ] the end of a peptide
     * @throws IllegalArgumentException if a peptide did not belong to the protein
     */
    private static String mapPeptideSequences(String protein, List<String> peptides) throws IllegalArgumentException {
        return mapPeptideSequences(protein, '[', ']', peptides);
    }

    /**
     * Maps a group of peptides to a protein string denoted by the passed
     * delimiters.
     *
     * @param protein  the protein to match
     * @param opener   the starting delimiter for a peptide
     * @param closer   the closing delimiter for a peptide
     * @param peptides the peptides to match
     * @return the annotated protein sequence where peptides are denoted by the passed delimiters
     * @throws IllegalArgumentException if a peptide did not belong to the protein
     */
    private static String mapPeptideSequences(String protein, Character opener, Character closer, List<String> peptides) throws IllegalArgumentException {
        StringBuilder builder = new StringBuilder(protein);
        String holder = protein.toUpperCase(Locale.US);
        for (String aPeptide : peptides) {
            if (holder.contains(aPeptide.toUpperCase(Locale.US))) {
                int indexOfPeptide = holder.indexOf(aPeptide);
                String subsequenceHolder = holder.subSequence(0, indexOfPeptide).toString();
                String subsequenceBuilder = builder.subSequence(0, indexOfPeptide).toString().replace("[", "").replace("]", "");
                while (!subsequenceBuilder.equals(subsequenceHolder)) {
                    indexOfPeptide++;
                    subsequenceBuilder = builder.subSequence(0, indexOfPeptide).toString().replace("[", "").replace("]", "");
                }
                long count = builder.subSequence(0, indexOfPeptide).chars().filter(e -> e == '[' || e == ']').count();
                builder.insert((int) (holder.indexOf(aPeptide.toUpperCase(Locale.US)) + count), '[');
                count += builder.subSequence(indexOfPeptide + 1, indexOfPeptide + 1 + aPeptide.length()).chars().filter(e -> e == '[' || e == ']').count();
                builder.insert((int) (holder.indexOf(aPeptide.toUpperCase(Locale.US)) + 1 + count + aPeptide.length()), ']');

            } else {
                throw new IllegalArgumentException("peptide " + aPeptide + " was not found in the protein sequence");
            }
        }
        return builder.toString();
    }

    /**
     * Merges overlapping peptide annotations in a String.
     *
     * @param mappedString the string to merge, assumes delimiters are [ for start and ] for end
     * @return the merged string
     */
    private static String mergePeptideAnnotations(String mappedString) {
        return mergePeptideAnnotations(mappedString, '[', ']');
    }

    /**
     * Merges overlapping peptide annotations in a String.
     *
     * @param mappedString the string to merge
     * @param opener       the opening delimiter for a peptide
     * @param closer       the closing delimiter for a peptide
     * @return the merged string
     */
    private static String mergePeptideAnnotations(String mappedString, Character opener, Character closer) {
        int openframe = 0;
        StringBuilder builder = new StringBuilder(mappedString);

        for (int i = 0; i < builder.length(); i++) {
            if (openframe > 0 && builder.charAt(i) == opener) {
                builder.deleteCharAt(i);
                i -= 1;
                openframe += 1;
            } else if (openframe > 1 && builder.charAt(i) == closer) {
                builder.deleteCharAt(i);
                i -= 1;
                openframe -= 1;
                //combine both situations in 1
            } else if (openframe == 1 && builder.charAt(i) == closer) {
                openframe -= 1;
            } else if (builder.charAt(i) == opener) {
                openframe += 1;
            }
        }
        //if openframe > 0 something went wrong
        return builder.toString();
    }

    /**
     * Find Amino acid preceding the peptide in the protein sequence.
     *
     * @param protein
     * @param peptide
     * @return amino acid if peptide exists in given protein sequence.
     */
    public static String findAminoAcidPrecedingPeptide(String protein, String peptide) {
        protein = protein.toUpperCase(Locale.US);
        peptide = peptide.toUpperCase(Locale.US);
        if (protein.contains(peptide)) {
            // if peptide is N-terminal returns "-"
            if (protein.indexOf(peptide) > 0) {
                return String.valueOf(protein.charAt(protein.indexOf(peptide) - 1));
            } else {
                return "-";
            }
        } else {
            return null;
        }
    }

    /**
     * Find Amino acid following the peptide in the protein sequence.
     *
     * @param protein
     * @param peptide
     * @return amino acid if peptide exists in given protein sequence.
     */
    public static String findAminoAcidFollowingPeptide(String protein, String peptide) {
        protein = protein.toUpperCase(Locale.US);
        peptide = peptide.toUpperCase(Locale.US);
        if (protein.contains(peptide)) {
            // if peptide is C-terminal returns "-"
            if (protein.indexOf(peptide) + peptide.length() < protein.length()) {
                return String.valueOf(protein.charAt(protein.indexOf(peptide) + peptide.length()));
            } else {
                return "-";
            }
        } else {
            return null;
        }
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

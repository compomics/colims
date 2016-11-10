/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.util;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author demet, davy
 */
public class ProteinCoverage {

    /**
     * Calculate protein coverage by using protein sequence and peptide sequence
     *
     * @param protein
     * @param peptides
     * @return
     */
    public static double calculateProteinCoverage(String protein, List<String> peptides) {
        String mappedProtein = map(protein, peptides);

        String mergedPeptideAnnotations = mergePeptideAnnotations(mappedProtein);

        double mergedPeptideSize = 0.0;

        Pattern p = Pattern.compile(Pattern.quote("[") + "(.*?)" + Pattern.quote("]"));
    
        Matcher m = p.matcher(mergedPeptideAnnotations);
    
        while (m.find()) {
            mergedPeptideSize += m.group(1).length();
        }

        double proteinSize = protein.length();

        double coverage = mergedPeptideSize / proteinSize;

        return coverage;
    }

    /**
     * maps a group of peptides to a protein string
     *
     * @param protein the protein to match
     * @param peptides the peptides to match
     * @return the annotated protein sequence where [ denotes the start of a
     * peptide and ] the end of a peptide
     * @throws IllegalArgumentException if a peptide did not belong to the
     * protein
     */
    private static String map(String protein, List<String> peptides) throws IllegalArgumentException {
        return map(protein, '[', ']', peptides);
    }

    /**
     * maps a group of peptides to a protein string denoted by the passed
     * delimiters
     *
     * @param protein the protein to match
     * @param opener the starting delimiter for a peptide
     * @param closer the closing delimiter for a peptide
     * @param peptides the peptides to match
     * @return the annotated protein sequence where peptides are denoted by the
     * passed delimiters
     * @throws IllegalArgumentException if a peptide did not belong to the
     * protein
     */
    private static String map(String protein, Character opener, Character closer, List<String> peptides) throws IllegalArgumentException {
        StringBuilder builder = new StringBuilder(protein);
        String holder = protein.toUpperCase(Locale.US);
        for (String aPeptide : peptides) {
            if (holder.contains(aPeptide.toUpperCase(Locale.US))) {
                int indexOfPeptide = holder.indexOf(aPeptide);
                String subsequenceHolder = holder.subSequence(0, indexOfPeptide).toString();
                String subsequenceBuilder = builder.subSequence(0, indexOfPeptide).toString().replace("[", "").replace("]", "");
                while(!subsequenceBuilder.equals(subsequenceHolder)){
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
     * merges overlapping peptide annotations in a string
     *
     * @param mappedString the string to merge, assumes delimiters are [ for
     * start and ] for end
     * @return the merged string
     */
    private static String mergePeptideAnnotations(String mappedString) {
        return mergePeptideAnnotations(mappedString, '[', ']');
    }

    /**
     * merges overlapping peptide annotations in a string
     *
     * @param mappedString the string to merge
     * @param opener the opening delimiter for a peptide
     * @param closer the closing delimiter for a peptide
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
}

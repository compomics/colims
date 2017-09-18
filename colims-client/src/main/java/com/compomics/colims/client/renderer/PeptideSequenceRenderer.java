package com.compomics.colims.client.renderer;

import com.compomics.colims.model.Modification;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.util.Util;
import org.apache.commons.math.util.MathUtils;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Niels Hulstaert on 27/10/15.
 */
public class PeptideSequenceRenderer {

    private static final String HTML_OPEN_TAG = "<html>";
    private static final String HTML_CLOSE_TAG = "</html>";
    private static final String TERMINAL_MOD_DELIMITER = "_";
    private static final String N_TERMINAL_MOD = "N-terminal ";
    private static final String C_TERMINAL_MOD = "C-terminal ";
    private static final String FIXED_MOD = "fixed";
    private static final String NOT_AVAILABLE = "N/A";
    private static final String LINE_BREAK = "<br>";

    /**
     * Private constructor to prevent instantiation.
     */
    private PeptideSequenceRenderer() {
    }

    /**
     * Get the annotated peptide sequence in HTML format. Amino acids that carry
     * modifications are colored and bold.
     *
     * @param peptideSequence         the peptide sequence
     * @param peptideHasModifications the list of modifications
     * @return the annotated peptide sequence
     */
    public static String getAnnotatedHtmlSequence(String peptideSequence, List<PeptideHasModification> peptideHasModifications) {
        StringBuilder annotatedSequence = new StringBuilder(HTML_OPEN_TAG);

        Map<Integer, PeptideHasModification> orderedPeptideHasModifications = getOrderedPeptideHasModifications(peptideHasModifications);

        //check for a possible N-terminal modification
        if (orderedPeptideHasModifications.containsKey(0)) {
            Modification modification = orderedPeptideHasModifications.get(0).getModification();
            annotatedSequence.append(modification.getName()).append(TERMINAL_MOD_DELIMITER);
        }
        for (int i = 1; i <= peptideSequence.length(); ++i) {
            if (orderedPeptideHasModifications.containsKey(i)) {
                annotatedSequence.append("<b><span style=\"color:#")
                        .append(Util.color2Hex(Color.BLUE))
                        .append("\">")
                        .append(peptideSequence.charAt(i - 1))
                        .append("</span></b>");
            } else {
                annotatedSequence.append(peptideSequence.charAt(i - 1));
            }
        }
        //check for a possible C-terminal modification
        if (orderedPeptideHasModifications.containsKey(peptideSequence.length() + 1)) {
            Modification modification = orderedPeptideHasModifications.get(peptideSequence.length() + 1).getModification();
            annotatedSequence.append(TERMINAL_MOD_DELIMITER).append(modification.getName());
        }

        annotatedSequence.append(HTML_CLOSE_TAG);

        return annotatedSequence.toString();
    }

    /**
     * Get the tooltip string in HTML format.
     *
     * @param peptideSequence         the peptide sequence
     * @param peptideHasModifications the list of modifications
     * @param showScore               show the modification score or not
     * @return the annotated peptide sequence
     */
    public static String getModificationsHtmlToolTip(String peptideSequence, List<PeptideHasModification> peptideHasModifications, boolean showScore) {
        StringBuilder tooltip = new StringBuilder(HTML_OPEN_TAG);

        Map<Integer, PeptideHasModification> orderedPeptideHasModifications = getOrderedPeptideHasModifications(peptideHasModifications);
        orderedPeptideHasModifications.entrySet().stream().forEach(entry -> {
            if (entry.getKey() == 0) {
                tooltip.append(N_TERMINAL_MOD).append(entry.getValue().getModification().getName());
            } else if (entry.getKey() <= peptideSequence.length()) {
                tooltip.append(entry.getKey())
                        .append(": ")
                        .append(entry.getValue().getModification().getName());
            } else {
                tooltip.append(C_TERMINAL_MOD).append(entry.getValue().getModification().getName());
            }
            if (showScore) {
                tooltip.append(getModificationScore(entry.getValue()));
            }
            tooltip.append(LINE_BREAK);
        });

        return tooltip.substring(0, tooltip.lastIndexOf(LINE_BREAK));
    }

    /**
     * This method orders the list of PeptideHasModification instances and
     * returns an sorted map with the modification locations as keys.
     *
     * @param peptideHasModifications the list of PeptideHasModification instances
     * @return the sorted map
     */
    private static TreeMap<Integer, PeptideHasModification> getOrderedPeptideHasModifications(List<PeptideHasModification> peptideHasModifications) {
        TreeMap<Integer, PeptideHasModification> orderedPeptideHasModifications = new TreeMap<>();

        peptideHasModifications.forEach((peptideHasModification) -> {
            PeptideHasModification previous = orderedPeptideHasModifications.put(peptideHasModification.getLocation(), peptideHasModification);
            if (previous != null) {
                throw new IllegalStateException("More than on modification for the same location " + peptideHasModification.getModification());
            }
        });

        return orderedPeptideHasModifications;
    }

    /**
     * Get the modification probability score as a String. If the modification
     * is fixed or no score is available, an appropriate value is returned.
     *
     * @param peptideHasModification the PeptideHasModification instance
     * @return the modification score String
     */
    private static String getModificationScore(PeptideHasModification peptideHasModification) {
        StringBuilder modificationScore = new StringBuilder(" (");

        if (peptideHasModification.getProbabilityScore() != null) {
            modificationScore.append(MathUtils.round(peptideHasModification.getProbabilityScore(), 2));
        } else {
            modificationScore.append(NOT_AVAILABLE);
        }
        modificationScore.append(")");

        return modificationScore.toString();
    }

}

package com.compomics.colims.client.renderer;

import com.compomics.colims.model.Modification;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.colims.model.enums.ModificationType;
import com.compomics.util.Util;
import org.apache.commons.math.util.MathUtils;

import java.awt.*;
import java.util.ArrayList;
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
     * Get the annotated peptide sequence in HTML format. Amino acids that carry modifications are colored and bold.
     *
     * @param peptideSequence         the peptide sequence
     * @param peptideHasModifications the list of modifications
     * @return the annotated peptide sequence
     */
    public static String getAnnotatedHtmlSequence(String peptideSequence, List<PeptideHasModification> peptideHasModifications) {
        StringBuilder annotatedSequence = new StringBuilder(HTML_OPEN_TAG);

        Map<Integer, List<PeptideHasModification>> orderedPeptideHasModifications = getOrderedPeptideHasModifications(peptideHasModifications);

        //check for a possible N-terminal modification
        if (orderedPeptideHasModifications.containsKey(Integer.valueOf(0))) {
            Modification modification = orderedPeptideHasModifications.get(Integer.valueOf(0)).get(0).getModification();
            annotatedSequence.append(modification.getName()).append(TERMINAL_MOD_DELIMITER);
        }
        for (int i = 1; i <= peptideSequence.length(); ++i) {
            if (orderedPeptideHasModifications.containsKey(Integer.valueOf(i))) {
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
        if (orderedPeptideHasModifications.containsKey(Integer.valueOf(peptideSequence.length() + 1))) {
            Modification modification = orderedPeptideHasModifications.get(peptideSequence.length() + 1).get(0).getModification();
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
     * @return the annotated peptide sequence
     */
    public static String getModificationsHtmlToolTip(String peptideSequence, List<PeptideHasModification> peptideHasModifications) {
        StringBuilder tooltip = new StringBuilder(HTML_OPEN_TAG);

        Map<Integer, List<PeptideHasModification>> orderedPeptideHasModifications = getOrderedPeptideHasModifications(peptideHasModifications);
        for (Map.Entry<Integer, List<PeptideHasModification>> entry : orderedPeptideHasModifications.entrySet()) {
            if (entry.getKey().intValue() == 0) {
                tooltip.append(N_TERMINAL_MOD).append(entry.getValue().get(0).getModification().getName())
                        .append(getModifationScore(entry.getValue().get(0)))
                        .append(LINE_BREAK);
            } else if (entry.getKey().intValue() <= peptideSequence.length()) {
                for (PeptideHasModification peptideHasModification : entry.getValue()) {
                    tooltip.append(entry.getKey())
                            .append(": ")
                            .append(entry.getValue().get(0).getModification().getName())
                            .append(getModifationScore(peptideHasModification));
                }
            } else {
                tooltip.append(C_TERMINAL_MOD).append(entry.getValue().get(0).getModification().getName())
                        .append(getModifationScore(entry.getValue().get(0)))
                        .append(LINE_BREAK);
            }
            tooltip.append(HTML_CLOSE_TAG);
        }

        return tooltip.toString();
    }

    private static Map<Integer, List<PeptideHasModification>> getOrderedPeptideHasModifications(List<PeptideHasModification> peptideHasModifications) {
        Map<Integer, List<PeptideHasModification>> orderedPeptideHasModifications = new TreeMap<>();

        for (PeptideHasModification peptideHasModification : peptideHasModifications) {
            Integer location = peptideHasModification.getLocation();
            if (orderedPeptideHasModifications.containsKey(location)) {
                orderedPeptideHasModifications.get(location).add(peptideHasModification);
            } else {
                List<PeptideHasModification> modifications = new ArrayList<>();
                modifications.add(peptideHasModification);
                orderedPeptideHasModifications.put(location, modifications);
            }
        }

        return orderedPeptideHasModifications;
    }

    private static String getModifationScore(PeptideHasModification peptideHasModification) {
        StringBuilder modificationScore = new StringBuilder(" (");

        if (peptideHasModification.getModificationType().equals(ModificationType.FIXED)) {
            modificationScore.append(FIXED_MOD);
        } else if (peptideHasModification.getProbabilityScore() != null) {
            modificationScore.append(MathUtils.round(peptideHasModification.getProbabilityScore(), 2));
        } else {
            modificationScore.append(NOT_AVAILABLE);
        }
        modificationScore.append(")");

        return modificationScore.toString();
    }

}

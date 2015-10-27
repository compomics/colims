package com.compomics.colims.client.renderer;

import com.compomics.colims.model.PeptideHasModification;
import com.compomics.util.Util;

import java.awt.*;
import java.util.List;

/**
 * Created by Niels Hulstaert on 27/10/15.
 */
public class PeptideSequenceRenderer {

    /**
     * Get the annotated peptide sequence. Amino acids that carry modifications are colored and bold.
     *
     * @param peptideSequence         the peptide sequence
     * @param peptideHasModifications the list of modifications
     * @return the annotated peptide sequence
     */
    public static String getAnnotatedPeptideSequence(String peptideSequence, List<PeptideHasModification> peptideHasModifications) {
        StringBuilder annotatedSequence = new StringBuilder();

        int[] mods = new int[peptideSequence.length()];

        for (PeptideHasModification phMod : peptideHasModifications) {
            mods[phMod.getLocation()]++;
        }

        for (int i = 0; i < peptideSequence.length(); ++i) {
            if (mods[i] > 0) {
                annotatedSequence.append("<b><span style=\"color:#")
                        .append(Util.color2Hex(Color.BLUE))
                        .append("\">")
                        .append(peptideSequence.charAt(i))
                        .append("</span></b>");
            } else {
                annotatedSequence.append(peptideSequence.charAt(i));
            }
        }

        return annotatedSequence.toString();
    }


}

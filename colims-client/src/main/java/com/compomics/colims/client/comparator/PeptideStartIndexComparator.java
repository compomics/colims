/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.client.comparator;

import com.compomics.colims.client.model.table.model.PeptideTableRow;
import com.compomics.colims.core.util.SequenceUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * This comparator compares peptide start indexes (on the protein) of peptide
 * table row instances.
 *
 * @author Niels Hulstaert
 */
public class PeptideStartIndexComparator implements Comparator<PeptideTableRow> {

    @Override
    public int compare(PeptideTableRow firstPeptideTableRow, PeptideTableRow secondPeptideTableRow) {
        ArrayList<Integer> firstPeptideIndexes = (ArrayList<Integer>) SequenceUtils.getPeptideStartIndexes(firstPeptideTableRow.getProteinSequence(), firstPeptideTableRow.getSequence());
        Collections.sort(firstPeptideIndexes);
        ArrayList<Integer> secondPeptideIndexes = (ArrayList<Integer>) SequenceUtils.getPeptideStartIndexes(secondPeptideTableRow.getProteinSequence(), secondPeptideTableRow.getSequence());
        Collections.sort(secondPeptideIndexes);

        //compare the first indexes
        return firstPeptideIndexes.get(0).compareTo(secondPeptideIndexes.get(0));
    }

}

package com.compomics.colims.client.model.table.format;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import com.compomics.colims.client.model.table.model.PeptideTableRow;
import com.compomics.colims.client.renderer.PeptideSequenceRenderer;
import com.compomics.colims.core.util.SequenceUtils;
import no.uib.jsparklines.data.StartIndexes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Iain on 23/06/2015.
 */
public class PeptideTableFormat implements AdvancedTableFormat<PeptideTableRow> {

    private static final String[] COLUMN_NAMES = {"Sequence", "PI", "Start", "Spectra", "Confidence"};

    public static final int SEQUENCE = 0;
    public static final int PROTEIN_INFERENCE = 1;
    public static final int START = 2;
    public static final int NUMBER_OF_SPECTRA = 3;
    public static final int CONFIDENCE = 4;

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case SEQUENCE:
                return String.class;
            case PROTEIN_INFERENCE:
                return Long.class;
            case START:
                return StartIndexes.class;
            case NUMBER_OF_SPECTRA:
                return Integer.class;
            case CONFIDENCE:
                return Double.class;
            default:
                throw new IllegalArgumentException("Unexpected column number " + column);
        }
    }

    @Override
    public Comparator getColumnComparator(int column) {
        return GlazedLists.comparableComparator();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    @Override
    public Object getColumnValue(PeptideTableRow peptideTableRow, int column) {
        switch (column) {
            case SEQUENCE:
                return PeptideSequenceRenderer.getAnnotatedHtmlSequence(peptideTableRow.getSequence(), peptideTableRow.getPeptideHasModifications());
            case PROTEIN_INFERENCE:
                return peptideTableRow.getProteinGroupCount();
            case START:
                ArrayList<Integer> indexes = (ArrayList<Integer>) SequenceUtils.getPeptideStartIndexes(peptideTableRow.getProteinSequence(), peptideTableRow.getSequence());
                Collections.sort(indexes);
                return new StartIndexes(indexes);
            case NUMBER_OF_SPECTRA:
                return peptideTableRow.getSpectrumCount();
            case CONFIDENCE:
                return peptideTableRow.getPeptideConfidence();
            default:
                throw new IllegalArgumentException("Unexpected column number " + column);
        }
    }
}

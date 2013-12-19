package com.compomics.colims.client.model.tableformat;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import com.compomics.colims.model.Spectrum;
import java.util.Comparator;

/**
 *
 * @author Niels Hulstaert
 */
public class PsmTableFormat implements AdvancedTableFormat<Spectrum> {

    private static final String[] columnNames = {"Id", "Charge", "M/Z ratio", "Retention time", "Peptide sequence", "Protein accession"};
    private static final String NOT_APPLICABLE = "N/A";
    public static final int SPECTRUM_ID = 0;
    public static final int PRECURSOR_CHARGE = 1;
    public static final int PRECURSOR_MZRATIO = 2;
    public static final int RETENTION_TIME = 3;
    public static final int PEPTIDE_SEQUENCE = 4;
    public static final int PROTEIN_ACCESSION = 5;

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case SPECTRUM_ID:
                return Long.class;
            case PRECURSOR_CHARGE:
                return Integer.class;
            case PRECURSOR_MZRATIO:
                return Double.class;
            case RETENTION_TIME:
                return Double.class;
            case PEPTIDE_SEQUENCE:
                return String.class;
            case PROTEIN_ACCESSION:
                return String.class;
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
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getColumnValue(Spectrum spectrum, int column) {
        switch (column) {
            case SPECTRUM_ID:
                return spectrum.getId();
            case PRECURSOR_CHARGE:
                return spectrum.getCharge();
            case PRECURSOR_MZRATIO:
                return spectrum.getMzRatio();
            case RETENTION_TIME:
                return spectrum.getRetentionTime();
            case PEPTIDE_SEQUENCE:
                String sequence = (!spectrum.getPeptides().isEmpty()) ? spectrum.getPeptides().get(0).getSequence() : NOT_APPLICABLE;
                return sequence;
            case PROTEIN_ACCESSION:                
                return "ACCESSION";    
            default:
                throw new IllegalArgumentException("Unexpected column number " + column);
        }
    }
}
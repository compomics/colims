package com.compomics.colims.client.model.table.format;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import com.compomics.colims.model.enums.SearchEngineType;
import com.compomics.colims.repository.hibernate.ProteinGroupDTO;

import java.util.Comparator;

/**
 * This class represents the format of ProteinGroup instances shown in the
 * protein group table.
 * <p/>
 * Created by Iain on 23/06/2015.
 */
public class ProteinGroupTableFormat implements AdvancedTableFormat<ProteinGroupDTO> {

    private static final String[] COLUMN_NAMES = {"ID", "Accession", "Sequence", "Distinct peptide seq.", "Spectra", "Confidence"};
    public static final int ID = 0;
    public static final int ACCESSION = 1;
    public static final int SEQUENCE = 2;
    public static final int NUMBER_OF_DISTINCT_PEPTIDE_SEQUENCES = 3;
    public static final int NUMBER_OF_SPECTRA = 4;
    public static final int CONFIDENCE = 5;

    /**
     * The search engine type.
     */
    private SearchEngineType searchEngineType;

    /**
     * Set the search engine type.
     *
     * @param searchEngineType
     */
    public void setSearchEngineType(SearchEngineType searchEngineType) {
        this.searchEngineType = searchEngineType;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case ID:
                return Long.class;
            case ACCESSION:
                return String.class;
            case SEQUENCE:
                return String.class;
            case NUMBER_OF_DISTINCT_PEPTIDE_SEQUENCES:
                return Long.class;
            case NUMBER_OF_SPECTRA:
                return Long.class;
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
    public Object getColumnValue(ProteinGroupDTO proteinGroupDTO, int column) {
        switch (column) {
            case ID:
                return proteinGroupDTO.getId();
            case ACCESSION:
                return proteinGroupDTO.getMainAccession();
            case SEQUENCE:
                return proteinGroupDTO.getMainSequence();
            case NUMBER_OF_DISTINCT_PEPTIDE_SEQUENCES:
                return proteinGroupDTO.getDistinctPeptideSequenceCount();
            case NUMBER_OF_SPECTRA:
                return proteinGroupDTO.getSpectrumCount();
            case CONFIDENCE:
                switch (searchEngineType) {
                    case PEPTIDESHAKER:
                        proteinGroupDTO.getProteinConfidence();
                        break;
                    case MAXQUANT:
                        if (proteinGroupDTO.getProteinPostErrorProbability() != null) {
                            return proteinGroupDTO.getProteinPostErrorProbability();
                        } else {
                            return 0.0;
                        }
                    default:
                        break;
                }
                return proteinGroupDTO.getProteinConfidence();
            default:
                throw new IllegalArgumentException("Unexpected column number " + column);
        }
    }
}

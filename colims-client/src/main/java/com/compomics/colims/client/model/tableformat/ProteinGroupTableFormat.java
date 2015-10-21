package com.compomics.colims.client.model.tableformat;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import com.compomics.colims.core.config.ApplicationContextProvider;
import com.compomics.colims.core.service.ProteinAccessionService;
import com.compomics.colims.core.service.ProteinGroupService;
import com.compomics.colims.repository.hibernate.model.ProteinGroupDTO;

import java.util.Comparator;

/**
 * This class represents the format of ProteinGroup instances shown in the protein group table.
 * <p/>
 * Created by Iain on 23/06/2015.
 */
public class ProteinGroupTableFormat implements AdvancedTableFormat<ProteinGroupDTO> {

    private static final String[] columnNames = {"ID", "Accession", "Sequence", "Distinct peptide seq.", "Spectra", "Confidence"};
    public static final int ID = 0;
    public static final int ACCESSION = 1;
    public static final int SEQUENCE = 2;
    public static final int NUMBER_OF_DISTINCT_PEPTIDE_SEQUENCES = 3;
    public static final int NUMBER_OF_SPECTRA = 4;
    public static final int CONFIDENCE = 5;

    /**
     * The ProteinAccessionService instance.
     */
    private ProteinAccessionService proteinAccessionService;
    /**
     * The ProteinGroupService instance.
     */
    private ProteinGroupService proteinGroupService;

    /**
     * No-arg constructor. The service beans are retrieved from the application context and assigned to the
     * corresponding class fields.
     */
    public ProteinGroupTableFormat() {
        this.proteinAccessionService = ApplicationContextProvider.getInstance().getApplicationContext().getBean(ProteinAccessionService.class);
        this.proteinGroupService = ApplicationContextProvider.getInstance().getApplicationContext().getBean(ProteinGroupService.class);
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
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
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
                return proteinGroupDTO.getProteinConfidence();
            default:
                throw new IllegalArgumentException("Unexpected column number " + column);
        }
    }
}

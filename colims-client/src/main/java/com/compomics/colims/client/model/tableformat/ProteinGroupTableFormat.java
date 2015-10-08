package com.compomics.colims.client.model.tableformat;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import com.compomics.colims.core.config.ApplicationContextProvider;
import com.compomics.colims.core.service.ProteinAccessionService;
import com.compomics.colims.core.service.ProteinGroupService;
import com.compomics.colims.model.ProteinGroup;

import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Created by Iain on 23/06/2015.
 */
public class ProteinGroupTableFormat implements AdvancedTableFormat<ProteinGroup> {
    private ProteinAccessionService proteinAccessionService;
    private ProteinGroupService proteinGroupService;

    private static final String[] columnNames = {"ID", "Accessions", "Probability", "Post-Error Probability", "Main Protein Sequence"};

    public static final int ID = 0;
    public static final int ACCESSION = 1;
    public static final int PROBABILITY = 2;
    public static final int PEP = 3;
    public static final int SEQUENCE = 4;

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
            case PROBABILITY:
                return Double.class;
            case PEP:
                return Double.class;
            case SEQUENCE:
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
    public Object getColumnValue(ProteinGroup proteinGroup, int column) {
        switch (column) {
            case ID:
                return proteinGroup.getId();
            case ACCESSION:
                return proteinAccessionService.getAccessionsForProteinGroup(proteinGroup)
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            case PROBABILITY:
                return proteinGroup.getProteinProbability();
            case PEP:
                return proteinGroup.getProteinPostErrorProbability();
            case SEQUENCE:
                return proteinGroupService.getMainProteinSequence(proteinGroup);
            default:
                throw new IllegalArgumentException("Unexpected column number " + column);
        }
    }
}

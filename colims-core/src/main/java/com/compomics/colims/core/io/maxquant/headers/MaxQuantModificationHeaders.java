package com.compomics.colims.core.io.maxquant.headers;

import java.util.Locale;

/**
 * Created by Iain on 02/12/2014.
 */
public enum MaxQuantModificationHeaders implements HeaderEnum {
    ACETYL_PROTEIN_N_TERM(new String[]{"Acetyl (Protein N-term)"}),
    ACETYL_K(new String[]{"Acetyl (K)"}),
    OXIDATION_M(new String[]{"Oxidation (M)"});

    String[] columnNames;

    private MaxQuantModificationHeaders(final String[] fieldnames) {
        this.columnNames = fieldnames;
    }

    @Override
    public String[] allPossibleColumnNames() {
        return this.columnNames;
    }

    @Override
    public void setColumnReference(int columnReference) {

    }

    @Override
    public String getColumnName() throws HeaderEnumNotInitialisedException {
        return columnNames[0].toLowerCase(Locale.US);
    }
}

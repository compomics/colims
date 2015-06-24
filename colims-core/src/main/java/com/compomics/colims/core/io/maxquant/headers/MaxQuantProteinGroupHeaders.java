package com.compomics.colims.core.io.maxquant.headers;

import java.util.Locale;

/**
 * Created by Iain on 03/03/2015.
 */
public enum  MaxQuantProteinGroupHeaders implements HeaderEnum {
    ACCESSION(new String[]{"Protein IDs"}),
    BESTMSMS(new String[]{"Best MS/MS"}),
    CONTAMINANT(new String[]{"Potential contaminant"}),
    EVIDENCEIDS(new String[]{"Evidence IDs"}),
    FASTAHEADER(new String[]{"Fasta headers"}),
    ID(new String[]{"id"}),
    MSMSIDS(new String[]{"MS/MS IDs"}),
    PEPTIDEIDS(new String[]{"Peptide IDs"}),
    REVERSE(new String[]{"Reverse"});

    protected final String[] columnNames;

    MaxQuantProteinGroupHeaders(final String[] fieldnames) {
        columnNames = fieldnames;
    }

    @Override
    public String[] allPossibleColumnNames() {
        return new String[0];
    }

    @Override
    public void setColumnReference(int columnReference) {}

    @Override
    public String getColumnName() throws HeaderEnumNotInitialisedException {
        return columnNames[0].toLowerCase(Locale.US);
    }
}

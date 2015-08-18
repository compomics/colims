package com.compomics.colims.distributed.io.maxquant.headers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    PEP(new String[]{"PEP"}),
    PEPTIDEIDS(new String[]{"Peptide IDs"}),
    REVERSE(new String[]{"Reverse"});


    List<String> columnNames;
    int standardColumnNameIndex = 0;

    /**
     * Private constructor.
     *
     * @param fieldnames the array of field names
     */
    MaxQuantProteinGroupHeaders(final String[] fieldnames) {

        columnNames = new ArrayList<>(fieldnames.length);
        Arrays.stream(fieldnames).forEach(e -> columnNames.add(e.toLowerCase(Locale.US)));
    }

    @Override
    public List<String> allPossibleColumnNames() {
        return this.columnNames;
    }

    @Override
    public void setColumnNameNumber(int columnNameNumber) {
        standardColumnNameIndex = columnNameNumber;
    }

    @Override
    public String getColumnName(int columnNameNumber) {
        if(columnNameNumber < 0  || columnNameNumber > columnNames.size()){
            return columnNames.get(0);
        }
        return columnNames.get(columnNameNumber);
    }


    @Override
    public String getDefaultColumnName() {
        return columnNames.get(standardColumnNameIndex);
    }
}

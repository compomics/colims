package com.compomics.colims.core.io.maxquant.headers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by Iain on 02/12/2014.
 */
public enum MaxQuantModificationHeaders implements HeaderEnum {
    ACETYL_PROTEIN_N_TERM(new String[]{"Acetyl (Protein N-term)"}),
    ACETYL_K(new String[]{"Acetyl (K)"}),
    OXIDATION_M(new String[]{"Oxidation (M)"});


    List<String> columnNames;
    int standardColumnNameIndex = 0;

    /**
     * Private constructor.
     *
     * @param fieldnames the array of field names
     */
    MaxQuantModificationHeaders(final String[] fieldnames) {

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

package com.compomics.colims.core.io.maxquant;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 *
 * @author Davy
 */
@Component("maxQuantPeptideParser")
public class MaxQuantPeptideParser {

    private Map<Integer, MaxQuantPeptideAssertion> parsedPeptideAssertions = new HashMap<>();

    public Map<Integer, MaxQuantPeptideAssertion> parse(File aPeptidesFile) throws IOException, UnparseableException {
        TabularFileLineValuesIterator peptidesIter = new TabularFileLineValuesIterator(aPeptidesFile, PeptidesHeaders.values());
        while (peptidesIter.hasNext()) {
            Map<String, String> values = peptidesIter.next();
            MaxQuantPeptideAssertion anAssertion = parsePeptideAssertion(values);
        }
        return parsedPeptideAssertions;
    }

    private MaxQuantPeptideAssertion parsePeptideAssertion(Map<String, String> values) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public enum PeptidesHeaders implements HeaderEnum {

        ;

        protected String[] columnNames;
        protected int columnReference = -1;

        private PeptidesHeaders(final String[] fieldnames) {
            columnNames = fieldnames;
        }

        @Override
        public final String[] returnPossibleColumnNames() {
            return columnNames;
        }

        @Override
        public final void setColumnReference(int columnReference) {
            this.columnReference = columnReference;
        }

        @Override
        public final String getColumnName() throws HeaderEnumNotInitialisedException {
            if (columnNames != null) {
                if (columnReference < 0 || columnReference > (columnNames.length - 1) && columnNames.length > 0) {
                    return columnNames[0];
                } else if (columnNames.length < 0) {
                    throw new HeaderEnumNotInitialisedException("header enum not initialised");
                } else {
                    return columnNames[columnReference].toLowerCase(Locale.US);
                }
            } else {
                throw new HeaderEnumNotInitialisedException("array was null");
            }
        }
    }
}

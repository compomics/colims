package com.compomics.colims.model.comparator;

import com.compomics.colims.model.SearchParametersHasModification;

import java.io.Serializable;
import java.util.Comparator;

/**
 * This comparator compares the modification names of SearchParametersHasModification entity instances.
 *
 * @author Niels Hulstaert
 */
public class SearchParameterHasModNameComparator implements Comparator<SearchParametersHasModification>, Serializable {

    @Override
    public int compare(SearchParametersHasModification o1, SearchParametersHasModification o2) {
        return o1.getSearchModification().getName().compareTo(o2.getSearchModification().getName());
    }
}

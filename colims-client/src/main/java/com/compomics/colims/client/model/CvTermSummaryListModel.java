package com.compomics.colims.client.model;

import com.compomics.colims.model.CvTerm;
import com.compomics.colims.model.enums.CvTermType;
import java.util.EnumMap;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 *
 * @author Niels Hulstaert
 */
public class CvTermSummaryListModel<T extends CvTerm> extends AbstractListModel {

    /**
     * The EnumMap containing the single CV terms (key: cvTermType; value: a
     * single CV term or null).
     */
    private EnumMap<CvTermType, T> singleCvTerms;
    /**
     * The EnumMap containing the multiple CV terms (key: cvTermType; value: a
     * list of multiple CV terms, can be empty).
     */
    private EnumMap<CvTermType, List<T>> multipleCvTerms;
    /**
     * The single CV term key array for indexing purposes
     */
    private CvTermType[] singleCvTermKeys;
    /**
     * The multiple CV term key array for indexing purposes
     */
    private CvTermType[] multipleCvTermKeys;

    public CvTermSummaryListModel() {
        singleCvTerms = new EnumMap<>(CvTermType.class);
        multipleCvTerms = new EnumMap<>(CvTermType.class);
    }

    public EnumMap<CvTermType, T> getSingleCvTerms() {
        return singleCvTerms;
    }

    public EnumMap<CvTermType, List<T>> getMultipleCvTerms() {
        return multipleCvTerms;
    }

    /**
     * Update the model with EnumMaps for the given CV term type of a single CV
     * term. The CV term instance can be null.
     *
     * @param cvTermType 
     * @param t 
     */
    public void updateSingleCvTerm(CvTermType cvTermType, T t) {
        singleCvTerms.put(cvTermType, t);
        this.fireContentsChanged(this, 0, getSize());
    }
    
    /**
     * Update the model with EnumMaps for the given CV term type of a multiple CV
     * term. The CV term list can be empty.
     *
     * @param cvTermType 
     * @param cvTerms  
     */
    public void updateMultipleCvTerm(CvTermType cvTermType, List<T> cvTerms) {
        multipleCvTerms.put(cvTermType, cvTerms);
        this.fireContentsChanged(this, 0, getSize());
    }

    /**
     * Update the model with EnumMaps for the single and multiple CV terms
     *
     * @param singleCvTerms
     * @param multipleCvTerms
     */
    public void update(EnumMap<CvTermType, T> singleCvTerms, EnumMap<CvTermType, List<T>> multipleCvTerms) {
        this.singleCvTerms = singleCvTerms;
        this.multipleCvTerms = multipleCvTerms;
        singleCvTermKeys = singleCvTerms.keySet().toArray(new CvTermType[singleCvTerms.size()]);
        multipleCvTermKeys = multipleCvTerms.keySet().toArray(new CvTermType[multipleCvTerms.size()]);
        this.fireContentsChanged(this, 0, getSize());
    }

    /**
     * Check if the given cvTermType can hold multiple CV term values or just
     * one.
     *
     * @param cvTermType
     * @return
     */
    public boolean isSingleCvTerm(CvTermType cvTermType) {
        boolean isSingleCvTerm = false;
        if (singleCvTerms.containsKey(cvTermType)) {
            isSingleCvTerm = true;
        }
        return isSingleCvTerm;
    }

    @Override
    public int getSize() {
        return singleCvTerms.size() + multipleCvTerms.size();
    }

    @Override
    public Object getElementAt(int index) {
        Object element;
        if (index < singleCvTermKeys.length) {
            element = singleCvTermKeys[index];
        } else {
            element = multipleCvTermKeys[index - singleCvTermKeys.length];
        }
        return element;
    }
}

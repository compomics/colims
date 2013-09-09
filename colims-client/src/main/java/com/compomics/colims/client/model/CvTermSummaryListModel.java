package com.compomics.colims.client.model;

import com.compomics.colims.model.CvTerm;
import com.compomics.colims.model.enums.CvTermProperty;
import java.util.EnumMap;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 *
 * @author Niels Hulstaert
 */
public class CvTermSummaryListModel<T extends CvTerm> extends AbstractListModel {
    
    /**
     * The EnumMap containing the single CV terms (key: CvTermProperty; value: a single CV term or null).
     */    
    private EnumMap<CvTermProperty, T> singleCvTerms;
    /**
     * The EnumMap containing the multiple CV terms (key: CvTermProperty; value: a list of multiple CV terms, can be empty).
     */ 
    private EnumMap<CvTermProperty, List<T>> multipleCvTerms;
    /**
     * The single CV term key array for indexing purposes
     */
    private CvTermProperty[] singleCvTermKeys;
    /**
     * The multiple CV term key array for indexing purposes
     */
    private CvTermProperty[] multipleCvTermKeys;

    public CvTermSummaryListModel() {
        singleCvTerms = new EnumMap<>(CvTermProperty.class);        
        multipleCvTerms = new EnumMap<>(CvTermProperty.class);
    }

    public EnumMap<CvTermProperty, T> getSingleCvTerms() {
        return singleCvTerms;
    }

    public EnumMap<CvTermProperty, List<T>> getMultipleCvTerms() {
        return multipleCvTerms;
    }        

    /**
     * Update the model with EnumMaps for the single and multiple CV terms
     * 
     * @param singleCvTerms
     * @param multipleCvTerms 
     */
    public void update(EnumMap<CvTermProperty, T> singleCvTerms, EnumMap<CvTermProperty, List<T>> multipleCvTerms) {
        this.singleCvTerms = singleCvTerms;
        this.multipleCvTerms = multipleCvTerms;
        singleCvTermKeys = singleCvTerms.keySet().toArray(new CvTermProperty[singleCvTerms.size()]);
        multipleCvTermKeys = multipleCvTerms.keySet().toArray(new CvTermProperty[multipleCvTerms.size()]);
        this.fireContentsChanged(this, 0, getSize());
    }

    /**
     * Check if the given CvTermProperty can hold multiple CV term values or just one.
     * 
     * @param cvTermProperty
     * @return 
     */
    public boolean isSingleCvTerm(CvTermProperty cvTermProperty) {
        boolean isSingleCvTerm = false;
        if (singleCvTerms.containsKey(cvTermProperty)) {
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

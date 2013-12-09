    package com.compomics.colims.model.enums;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
public enum CvTermType {

    CV_TERM_TYPE(null),
        INSTRUMENT_CV_TERM_TYPE(CV_TERM_TYPE),
            SOURCE(INSTRUMENT_CV_TERM_TYPE),
            DETECTOR(INSTRUMENT_CV_TERM_TYPE),
            ANALYZER(INSTRUMENT_CV_TERM_TYPE),
        MATERIAL_CV_TERM_TYPE(CV_TERM_TYPE),
            SPECIES(MATERIAL_CV_TERM_TYPE),
            TISSUE(MATERIAL_CV_TERM_TYPE),
            CELL_TYPE(MATERIAL_CV_TERM_TYPE),
            COMPARTMENT(MATERIAL_CV_TERM_TYPE),
        PROTOCOL_CV_TERM_TYPE(CV_TERM_TYPE),
            REDUCTION(PROTOCOL_CV_TERM_TYPE),
            ENZYME(PROTOCOL_CV_TERM_TYPE),
            CHEMICAL_LABELING(PROTOCOL_CV_TERM_TYPE),
            CELL_BASED(PROTOCOL_CV_TERM_TYPE),
            OTHER(PROTOCOL_CV_TERM_TYPE);
            
    private CvTermType parent = null;
    private List<CvTermType> children = new ArrayList<>();

    private CvTermType(CvTermType parent) {
        this.parent = parent;
        if (this.parent != null) {
            this.parent.addChild(this);
        }
    }    

    public CvTermType[] getChildrenAsArray() {
        return children.toArray(new CvTermType[children.size()]);
    }
    
    public List<CvTermType> getChildren() {
        return children;
    }

    public CvTermType getParent() {
        return parent;
    }        

    public CvTermType[] allChildren() {
        List<CvTermType> list = new ArrayList<>();
        addChildren(this, list);
        return list.toArray(new CvTermType[list.size()]);
    }
    
    private void addChild(CvTermType child) {
        this.children.add(child);
    }

    private static void addChildren(CvTermType root, List<CvTermType> list) {
        list.addAll(root.children);
        for (CvTermType child : root.children) {
            addChildren(child, list);
        }
    }
}

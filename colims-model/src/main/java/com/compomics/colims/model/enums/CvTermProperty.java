package com.compomics.colims.model.enums;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
public enum CvTermProperty {

    CV_TERM_PROPERTY(null),
        INSTRUMENT_CV_TERM_PROPERTY(CV_TERM_PROPERTY),
            SOURCE(INSTRUMENT_CV_TERM_PROPERTY),
            DETECTOR(INSTRUMENT_CV_TERM_PROPERTY),
            ANALYZER(INSTRUMENT_CV_TERM_PROPERTY),
        MATERIAL_CV_TERM_PROPERTY(CV_TERM_PROPERTY),
            SPECIES(MATERIAL_CV_TERM_PROPERTY),
            TISSUE(MATERIAL_CV_TERM_PROPERTY),
            CELL_TYPE(MATERIAL_CV_TERM_PROPERTY),
            COMPARTMENT(MATERIAL_CV_TERM_PROPERTY),
        PROTOCOL_CV_TERM_PROPERTY(CV_TERM_PROPERTY),
            REDUCTION(PROTOCOL_CV_TERM_PROPERTY),
            ENZYME(PROTOCOL_CV_TERM_PROPERTY),
            CHEMICAL_LABELING(PROTOCOL_CV_TERM_PROPERTY),
            CELL_BASED(PROTOCOL_CV_TERM_PROPERTY),
            OTHER(PROTOCOL_CV_TERM_PROPERTY);
            
    private CvTermProperty parent = null;
    private List<CvTermProperty> children = new ArrayList<>();

    private CvTermProperty(CvTermProperty parent) {
        this.parent = parent;
        if (this.parent != null) {
            this.parent.addChild(this);
        }
    }    

    public CvTermProperty[] getChildrenAsArray() {
        return children.toArray(new CvTermProperty[children.size()]);
    }
    
    public List<CvTermProperty> getChildren() {
        return children;
    }

    public CvTermProperty[] allChildren() {
        List<CvTermProperty> list = new ArrayList<>();
        addChildren(this, list);
        return list.toArray(new CvTermProperty[list.size()]);
    }
    
    private void addChild(CvTermProperty child) {
        this.children.add(child);
    }

    private static void addChildren(CvTermProperty root, List<CvTermProperty> list) {
        list.addAll(root.children);
        for (CvTermProperty child : root.children) {
            addChildren(child, list);
        }
    }
}

package com.compomics.colims.model.enums;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
public enum CvParamType {

    CV_PARAM_TYPE(null, null),
        INSTRUMENT_CV_PARAM_TYPE(CV_PARAM_TYPE, null),
            TYPE(INSTRUMENT_CV_PARAM_TYPE, true),
            SOURCE(INSTRUMENT_CV_PARAM_TYPE, true),
            DETECTOR(INSTRUMENT_CV_PARAM_TYPE, true),
            ANALYZER(INSTRUMENT_CV_PARAM_TYPE, true),
        MATERIAL_CV_PARAM_TYPE(CV_PARAM_TYPE, null),
            SPECIES(MATERIAL_CV_PARAM_TYPE, true),
            TISSUE(MATERIAL_CV_PARAM_TYPE, false),
            CELL_TYPE(MATERIAL_CV_PARAM_TYPE, false),
            COMPARTMENT(MATERIAL_CV_PARAM_TYPE, false),
        PROTOCOL_CV_PARAM_TYPE(CV_PARAM_TYPE, null),
            REDUCTION(PROTOCOL_CV_PARAM_TYPE, false),
            ENZYME(PROTOCOL_CV_PARAM_TYPE, false),
            CHEMICAL_LABELING(PROTOCOL_CV_PARAM_TYPE, false),
            CELL_BASED(PROTOCOL_CV_PARAM_TYPE, false),
            OTHER(PROTOCOL_CV_PARAM_TYPE, false),
        QUANT_PARAM_CV_PARAM(CV_PARAM_TYPE, null),
            METHOD(QUANT_PARAM_CV_PARAM, false),
            REAGENT(QUANT_PARAM_CV_PARAM, false),
            PROTEIN_UNIT(QUANT_PARAM_CV_PARAM, false),
            PEPTIDE_UNIT(QUANT_PARAM_CV_PARAM, false),
            SMALL_MOLECULE_UNIT(QUANT_PARAM_CV_PARAM, false),
        SEARCH_PARAM_CV_PARAM(CV_PARAM_TYPE, null),
            SEARCH_PARAM_ENZYME(SEARCH_PARAM_CV_PARAM, false),
            MISSED_CLEAVAGES(SEARCH_PARAM_CV_PARAM, false),
            THRESHOLD(SEARCH_PARAM_CV_PARAM, false),
            FRAGMENT_MASS_TOLERANCE_MINUS(SEARCH_PARAM_CV_PARAM, false),
            FRAGMENT_MASS_TOLERANCE_PLUS(SEARCH_PARAM_CV_PARAM, false),
            PRECURSOR_MASS_TOLERANCE(SEARCH_PARAM_CV_PARAM, false)
            ;

    /**
     * The parent CV param.
     */
    private CvParamType parent = null;
    /**
     * Is the CV param mandatory.
     */
    private Boolean mandatory = false;
    /**
     * The child CV params.
     */
    private List<CvParamType> children = new ArrayList<>();

    private CvParamType(CvParamType parent, Boolean mandatory) {
        this.parent = parent;
        this.mandatory = mandatory;
        if (this.parent != null) {
            this.parent.addChild(this);
        }
    }

    public List<CvParamType> getChildren() {
        return children;
    }

    public CvParamType getParent() {
        return parent;
    }

    public Boolean isMandatory() {
        return mandatory;
    }

    public CvParamType[] allChildren() {
        List<CvParamType> list = new ArrayList<>();
        addChildren(this, list);
        return list.toArray(new CvParamType[list.size()]);
    }

    public CvParamType[] getChildrenAsArray() {
        return children.toArray(new CvParamType[children.size()]);
    }

    @Override
    public String toString() {
        if (mandatory) {
            return name() + "*";
        } else {
            return name();
        }
    }

    private void addChild(CvParamType child) {
        this.children.add(child);
    }

    private static void addChildren(CvParamType root, List<CvParamType> list) {
        list.addAll(root.children);
        for (CvParamType child : root.children) {
            addChildren(child, list);
        }
    }
}

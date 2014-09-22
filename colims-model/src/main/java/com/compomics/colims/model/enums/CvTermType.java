package com.compomics.colims.model.enums;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
public enum CvTermType {

    CV_TERM_TYPE(null, null),
        INSTRUMENT_CV_TERM_TYPE(CV_TERM_TYPE, null),
            TYPE(INSTRUMENT_CV_TERM_TYPE, true),
            SOURCE(INSTRUMENT_CV_TERM_TYPE, true),
            DETECTOR(INSTRUMENT_CV_TERM_TYPE, true),
            ANALYZER(INSTRUMENT_CV_TERM_TYPE, true),
        MATERIAL_CV_TERM_TYPE(CV_TERM_TYPE, null),
            SPECIES(MATERIAL_CV_TERM_TYPE, true),
            TISSUE(MATERIAL_CV_TERM_TYPE, false),
            CELL_TYPE(MATERIAL_CV_TERM_TYPE, false),
            COMPARTMENT(MATERIAL_CV_TERM_TYPE, false),
        PROTOCOL_CV_TERM_TYPE(CV_TERM_TYPE, null),
            REDUCTION(PROTOCOL_CV_TERM_TYPE, false),
            ENZYME(PROTOCOL_CV_TERM_TYPE, false),
            CHEMICAL_LABELING(PROTOCOL_CV_TERM_TYPE, false),
            CELL_BASED(PROTOCOL_CV_TERM_TYPE, false),
            OTHER(PROTOCOL_CV_TERM_TYPE, false),
        QUANT_PARAM_CV_TERM(CV_TERM_TYPE, null),
            METHOD(QUANT_PARAM_CV_TERM, false),
            REAGENT(QUANT_PARAM_CV_TERM, false),
            PROTEIN_UNIT(QUANT_PARAM_CV_TERM, false),
            PEPTIDE_UNIT(QUANT_PARAM_CV_TERM, false),
            SMALL_MOLECULE_UNIT(QUANT_PARAM_CV_TERM, false),
        SEARCH_PARAM_CV_TERM(CV_TERM_TYPE, null),
            SEARCH_PARAM_ENZYME(SEARCH_PARAM_CV_TERM, false),
            FRAGMENT_MASS_TOLERANCE(SEARCH_PARAM_CV_TERM, false),
            PRECURSOR_MASS_TOLERANCE(SEARCH_PARAM_CV_TERM, false),
            ;

    /**
     * The parent CV term.
     */
    private CvTermType parent = null;
    /**
     * Is the CV term mandatory.
     */
    private Boolean mandatory = false;
    /**
     * The child CV terms.
     */
    private List<CvTermType> children = new ArrayList<>();

    private CvTermType(CvTermType parent, Boolean mandatory) {
        this.parent = parent;
        this.mandatory = mandatory;
        if (this.parent != null) {
            this.parent.addChild(this);
        }
    }

    public List<CvTermType> getChildren() {
        return children;
    }

    public CvTermType getParent() {
        return parent;
    }

    public Boolean isMandatory() {
        return mandatory;
    }

    public CvTermType[] allChildren() {
        List<CvTermType> list = new ArrayList<>();
        addChildren(this, list);
        return list.toArray(new CvTermType[list.size()]);
    }

    public CvTermType[] getChildrenAsArray() {
        return children.toArray(new CvTermType[children.size()]);
    }

    @Override
    public String toString() {
        if (mandatory) {
            return name() + "*";
        } else {
            return name();
        }
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

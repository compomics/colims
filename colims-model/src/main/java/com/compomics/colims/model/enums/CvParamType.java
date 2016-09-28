package com.compomics.colims.model.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * This enum contains the different CV term types, organized by parent CV term.
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
            SEARCH_TYPE(SEARCH_PARAM_CV_PARAM, false),
            THRESHOLD(SEARCH_PARAM_CV_PARAM, false),
            SEARCH_MODIFICATION(SEARCH_PARAM_CV_PARAM, false),
            SEARCH_PARAM_OTHER(SEARCH_PARAM_CV_PARAM, false);

    /**
     * The parent CV parameter type.
     */
    private CvParamType parent = null;
    /**
     * Is the CV parameter type mandatory for it's parent entity.
     */
    private Boolean mandatory = false;
    /**
     * The child CV parameter types.
     */
    private final List<CvParamType> children = new ArrayList<>();

    /**
     * Private constructor.
     *
     * @param parent the parent CV term type
     * @param mandatory whether or not the CV term type is mandatory
     */
    CvParamType(final CvParamType parent, final Boolean mandatory) {
        this.parent = parent;
        this.mandatory = mandatory;
        if (this.parent != null) {
            this.parent.addChild(this);
        }
    }

    /**
     * Get all children of this instance.
     *
     * @return the list of child CV parameter types
     */
    public List<CvParamType> getChildren() {
        return children;
    }

    /**
     * Get the parent of this instance.
     *
     * @return the parent cv param type
     */
    public CvParamType getParent() {
        return parent;
    }

    /**
     * Check whether this cv param type is a mandatory one or not.
     *
     * @return true or false
     */
    public Boolean isMandatory() {
        return mandatory;
    }

    /**
     * Return all child CV term types of this CV term type as an array.
     *
     * @return the array of CV term type children
     */
    public CvParamType[] allChildren() {
        List<CvParamType> list = new ArrayList<>();
        addChildren(this, list);
        return list.toArray(new CvParamType[list.size()]);
    }

    /**
     * Return the child CV term types of this CV term type (not the sub types)
     * as an array.
     *
     * @return the array of CV term type children
     */
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

    /**
     * Add a child CV term type to this (parent) CV term type.
     *
     * @param child the child CV term type
     */
    private void addChild(final CvParamType child) {
        this.children.add(child);
    }

    /**
     * Add child CV term types to this (parent) CV term type in a recursive way.
     *
     * @param parent the parent CV term type
     * @param list the list of child CV term types
     */
    private static void addChildren(final CvParamType parent, final List<CvParamType> list) {
        list.addAll(parent.children);
        parent.children.stream().forEach((child) -> {
            addChildren(child, list);
        });
    }
}

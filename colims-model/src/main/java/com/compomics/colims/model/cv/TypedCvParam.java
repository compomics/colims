package com.compomics.colims.model.cv;

import com.compomics.colims.model.enums.CvParamType;

import javax.persistence.*;

/**
 * Parent class for typed CV parameter entities.
 *
 * @author Niels Hulstaert
 */
@MappedSuperclass
public abstract class TypedCvParam extends CvParam {

    private static final long serialVersionUID = 5594723532938658371L;

    /**
     * The CV parameter type.
     */
    @Basic(optional = false)
    @Column(name = "cv_property", nullable = false)
    @Enumerated(EnumType.STRING)
    protected CvParamType cvParamType;

    /**
     * No-arg constructor.
     */
    public TypedCvParam() {
    }

    /**
     * Constructor.
     *
     * @param cvParamType the CV parameter type
     * @param label       the ontology label
     * @param accession   the CV term accession
     * @param name        the CV term name
     */
    public TypedCvParam(final CvParamType cvParamType, final String label, final String accession, final String name) {
        super(label, accession, name);
        this.cvParamType = cvParamType;
    }

    public CvParamType getCvParamType() {
        return cvParamType;
    }

    public void setCvParamType(final CvParamType cvParamType) {
        this.cvParamType = cvParamType;
    }

}

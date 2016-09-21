package com.compomics.colims.model.cv;

import com.compomics.colims.model.enums.CvParamType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

/**
 * Parent class for auditable and typed CV parameter entities.
 *
 * @author Niels Hulstaert
 */
@MappedSuperclass
public abstract class AuditableTypedCvParam extends AuditableCvParam {

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
    public AuditableTypedCvParam() {
    }

    /**
     * Constructor.
     *
     * @param cvParamType the CV parameter type
     * @param label the ontology label
     * @param accession the CV term accession
     * @param name the CV term name
     */
    public AuditableTypedCvParam(final CvParamType cvParamType, final String label, final String accession, final String name) {
        super(label, accession, name);
        this.cvParamType = cvParamType;
    }

    public CvParamType getCvParamType() {
        return cvParamType;
    }

    public void setcvTermType(final CvParamType cvParamType) {
        this.cvParamType = cvParamType;
    }

}

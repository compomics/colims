package com.compomics.colims.model.cv;

import com.compomics.colims.model.enums.CvParamType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

/**
 *
 * @author Niels Hulstaert
 */
@MappedSuperclass
public abstract class AuditableTypedCvParam extends AuditableCvParam {

    private static final long serialVersionUID = 5594723532938658371L;

    @Basic(optional = false)
    @Column(name = "cv_property", nullable = false)
    @Enumerated(EnumType.STRING)
    protected CvParamType cvParamType;

    public AuditableTypedCvParam() {
    }

    public AuditableTypedCvParam(final CvParamType cvParamType, final String ontology, final String label, final String accession, final String name) {
        super(ontology, label, accession, name);
        this.cvParamType = cvParamType;
    }

    public CvParamType getCvParamType() {
        return cvParamType;
    }

    public void setcvTermType(final CvParamType cvParamType) {
        this.cvParamType = cvParamType;
    }

}

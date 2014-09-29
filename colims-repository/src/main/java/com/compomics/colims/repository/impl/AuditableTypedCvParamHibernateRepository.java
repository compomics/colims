package com.compomics.colims.repository.impl;

import com.compomics.colims.model.cv.AuditableTypedCvParam;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.compomics.colims.model.enums.CvParamType;
import com.compomics.colims.repository.AuditableTypedCvParamRepository;
import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("auditableTypedCvParamRepository")
public class AuditableTypedCvParamHibernateRepository extends GenericHibernateRepository<AuditableTypedCvParam, Long> implements AuditableTypedCvParamRepository {

    @Override
    public AuditableTypedCvParam findByAccession(final String accession, final CvParamType cvTermType) {
        return findUniqueByCriteria(Restrictions.eq("accession", accession), Restrictions.eq("cvParamType", cvTermType));
    }

    @Override
    public List<AuditableTypedCvParam> findByCvParamType(final CvParamType cvTermType) {
        return findByCriteria(Restrictions.eq("cvParamType", cvTermType));
    }
}

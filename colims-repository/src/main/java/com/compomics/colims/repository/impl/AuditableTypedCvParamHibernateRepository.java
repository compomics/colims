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
    public AuditableTypedCvParam getReference(Long id) {
        throw new UnsupportedOperationException("This method is not supported for mappedsuperclass instances.");
    }

    @Override
    public AuditableTypedCvParam findById(Long id) {
        throw new UnsupportedOperationException("This method is not supported for mappedsuperclass instances.");
    }

    @Override
    public AuditableTypedCvParam findByAccession(final String accession, final CvParamType cvParamType) {
        return findUniqueByCriteria(Restrictions.eq("accession", accession), Restrictions.eq("cvParamType", cvParamType));
    }

    @Override
    public AuditableTypedCvParam findByName(final String name, final CvParamType cvParamType, final boolean ignoreCase) {
        if (ignoreCase) {
            return findUniqueByCriteria(Restrictions.eq("name", name).ignoreCase(), Restrictions.eq("cvParamType", cvParamType));
        } else {
            return findUniqueByCriteria(Restrictions.eq("name", name), Restrictions.eq("cvParamType", cvParamType));
        }
    }

    @Override
    public List<AuditableTypedCvParam> findByCvParamType(final CvParamType cvParamType) {
        return findByCriteria(Restrictions.eq("cvParamType", cvParamType));
    }

    @Override
    public AuditableTypedCvParam getMappedSuperclassReference(Class entityClass, Long id) {
        return (AuditableTypedCvParam) getEntityManager().getReference(entityClass, id);
    }

}

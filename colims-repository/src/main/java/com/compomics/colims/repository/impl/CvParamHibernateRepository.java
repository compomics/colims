package com.compomics.colims.repository.impl;

import com.compomics.colims.model.cv.CvParam;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.compomics.colims.repository.CvParamRepository;
import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Repository("cvParamRepository")
public class CvParamHibernateRepository extends GenericHibernateRepository<CvParam, Long> implements CvParamRepository {

    @Override
    public CvParam getMappedSuperclassReference(Class<? extends CvParam> entityClass, Long id) {
        return getEntityManager().getReference(entityClass, id);
    }

    @Override
    public List<CvParam> findByAccession(final String accession) {
        return findByCriteria(Restrictions.eq("accession", accession));
    }

    @Override
    public List<CvParam> findByName(final String name, final boolean ignoreCase) {
        if (ignoreCase) {
            return findByCriteria(Restrictions.eq("name", name).ignoreCase());
        } else {
            return findByCriteria(Restrictions.eq("name", name));
        }

    }

}

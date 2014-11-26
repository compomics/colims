package com.compomics.colims.repository.impl;

import com.compomics.colims.model.cv.TypedCvParam;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.compomics.colims.model.enums.CvParamType;
import com.compomics.colims.repository.TypedCvParamRepository;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Repository("typedCvParamRepository")
public class TypedCvParamHibernateRepository extends GenericHibernateRepository<TypedCvParam, Long> implements TypedCvParamRepository {

    @Override
    public TypedCvParam findByAccession(final String accession, final CvParamType cvParamType) {
        return findUniqueByCriteria(Restrictions.eq("accession", accession), Restrictions.eq("cvParamType", cvParamType));
    }

    @Override
    public TypedCvParam findByName(final String name, final CvParamType cvParamType, final boolean ignoreCase) {
        if (ignoreCase) {
            return findUniqueByCriteria(Restrictions.eq("name", name).ignoreCase(), Restrictions.eq("cvParamType", cvParamType));
        } else {
            return findUniqueByCriteria(Restrictions.eq("name", name), Restrictions.eq("cvParamType", cvParamType));
        }

    }

    @Override
    public List<TypedCvParam> findByCvParamType(final CvParamType cvParamType) {
        return findByCriteria(Restrictions.eq("cvParamType", cvParamType));
    }
}

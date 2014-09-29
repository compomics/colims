package com.compomics.colims.repository.impl;

import com.compomics.colims.model.cv.TypedCvParam;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.compomics.colims.model.enums.CvParamType;
import com.compomics.colims.repository.TypedCvParamRepository;
import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("typedCvParamRepository")
public class TypedCvParamHibernateRepository extends GenericHibernateRepository<TypedCvParam, Long> implements TypedCvParamRepository {

    @Override
    public TypedCvParam findByAccession(final String accession, final CvParamType cvTermType) {
        return findUniqueByCriteria(Restrictions.eq("accession", accession), Restrictions.eq("cvParamType", cvTermType));
    }

    @Override
    public List<TypedCvParam> findByCvParamType(final CvParamType cvTermType) {
        return findByCriteria(Restrictions.eq("cvParamType", cvTermType));
    }
}

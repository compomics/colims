package com.compomics.colims.repository.impl;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Modification;
import com.compomics.colims.repository.ModificationRepository;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Repository("modificationRepository")
public class ModificationHibernateRepository extends GenericHibernateRepository<Modification, Long> implements ModificationRepository {

    public static final String BASE_QUERY = "SELECT DISTINCT modification.id FROM modification"
            + " LEFT JOIN peptide_has_modification p_has_mod ON p_has_mod.l_modification_id = modification.id"
            + " LEFT JOIN peptide pep ON pep.id = p_has_mod.l_peptide_id"
            + " LEFT JOIN spectrum sp ON sp.id = pep.l_spectrum_id"
            + " WHERE sp.l_analytical_run_id = %1$d";

    @Override
    public Modification findByName(final String name) {
        List<Modification> modifications = findByCriteria(Restrictions.eq("name", name));
        if (!modifications.isEmpty()) {
            return modifications.get(0);
        } else {
            return null;
        }
    }

    @Override
    public Modification findByAccession(final String accession) {
        Criteria criteria = createCriteria(Restrictions.eq("accession", accession));
        criteria.setCacheable(true);
        return (Modification) criteria.uniqueResult();
    }

    @Override
    public Modification findByAlternativeAccession(String alternativeAccession) {
        List<Modification> modifications = findByCriteria(Restrictions.eq("alternativeAccession", alternativeAccession));
        if (!modifications.isEmpty()) {
            return modifications.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<Long> getModificationIdsForRun(AnalyticalRun analyticalRun) {
        List<Long> modificationIds = getCurrentSession()
                .createSQLQuery(String.format(BASE_QUERY, analyticalRun.getId()))
                .addScalar("modification.id", LongType.INSTANCE)
                .list();

        return modificationIds;
    }
}

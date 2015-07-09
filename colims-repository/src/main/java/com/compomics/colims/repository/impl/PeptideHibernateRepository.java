package com.compomics.colims.repository.impl;

import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasProtein;
import com.compomics.colims.model.Protein;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.compomics.colims.repository.PeptideRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 * @author Kenneth Verheggen
 */
@Repository("peptideRepository")
@Transactional
public class PeptideHibernateRepository extends GenericHibernateRepository<Peptide, Long> implements PeptideRepository {
    @Override
    public List<Object[]> getPeptidesForProtein(Protein protein, List<Long> spectrumIds) {
        // either get all ids first and do second query with projections
        // or write yet more sql

        return getCurrentSession()
            .createCriteria(PeptideHasProtein.class)
            .createAlias("peptide", "pep")
            .add(Restrictions.eq("protein", protein))
            .add(Restrictions.in("pep.spectrum.id", spectrumIds))
            .setProjection(Projections.projectionList()
                .add(Projections.groupProperty("pep.sequence")) // AAH THIS ISN'T IN THIS MODEL
                .add(Projections.count("id"))
                .add(Projections.property("pep.charge"))
            )
            .list();
    }

    @Override
    public List getPeptidesFromSequence(String sequence, List<Long> spectrumIds) {
        return getCurrentSession()
            .createCriteria(Peptide.class)
            .add(Restrictions.eq("sequence", sequence))
            .add(Restrictions.in("spectrum.id", spectrumIds))
            .list();
    }

}
package com.compomics.colims.repository.impl;

import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.colims.model.PeptideHasProtein;
import com.compomics.colims.model.Protein;
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
    public List<PeptideHasProtein> getPeptidesForProtein(Protein protein, List<Long> spectrumIds) {
        return getCurrentSession().createCriteria(PeptideHasProtein.class)
            .add(Restrictions.eq("protein", protein))
            .createCriteria("peptide")
            .add(Restrictions.in("spectrum.id", spectrumIds))
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

    @Override
    public List<PeptideHasModification> getModificationsForMultiplePeptides(List<Peptide> peptides) {
        return getCurrentSession()
            .createCriteria(PeptideHasModification.class)
            .add(Restrictions.in("peptide", peptides))
            .list();
    }
}
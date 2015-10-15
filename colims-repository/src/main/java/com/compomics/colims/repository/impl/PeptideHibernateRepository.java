package com.compomics.colims.repository.impl;

import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.colims.model.ProteinGroup;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.repository.PeptideRepository;
import com.google.common.base.Joiner;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * This interface provides repository methods for the Peptide class.
 *
 * @author Iain
 */
@Repository("peptideRepository")
@Transactional
public class PeptideHibernateRepository extends GenericHibernateRepository<Peptide, Long> implements PeptideRepository {

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

    @Override
    public List<Peptide> getPeptidesForSpectrum(Spectrum spectrum) {
        return createCriteria()
                .add(Restrictions.eq("spectrum", spectrum))
                .list();
    }
}
package com.compomics.colims.repository.impl;

import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasProtein;
import com.compomics.colims.model.Protein;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.compomics.colims.repository.PeptideRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Kenneth Verheggen
 */
@Repository("peptideRepository")
@Transactional
public class PeptideHibernateRepository extends GenericHibernateRepository<Peptide, Long> implements PeptideRepository {
    @Override
    public List<Peptide> getPeptidesForProtein(Protein protein) {
        List<PeptideHasProtein> peptideHasProteins = getCurrentSession().createCriteria(PeptideHasProtein.class).add(Restrictions.eq("protein", protein)).list();

        return peptideHasProteins.stream().map(PeptideHasProtein::getPeptide).collect(Collectors.toList());
    }
}
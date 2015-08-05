package com.compomics.colims.repository.impl;

import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.colims.model.PeptideHasProteinGroup;
import com.compomics.colims.model.Protein;
import com.compomics.colims.repository.PeptideRepository;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * This interface provides repository methods for the Peptide class.
 *
 * @author Kenneth Verheggen
 */
@Repository("peptideRepository")
@Transactional
public class PeptideHibernateRepository extends GenericHibernateRepository<Peptide, Long> implements PeptideRepository {

    @Override
    public List<PeptideHasProteinGroup> getPeptidesForProtein(Protein protein, List<Long> spectrumIds) {
        Criteria criteria = getCurrentSession().createCriteria(PeptideHasProteinGroup.class)
//                .createCriteria("peptide")
//                .add(Restrictions.in("spectrum.id", spectrumIds))
                .createCriteria("proteinGroup", "l_protein_group_id")
                .createAlias("proteinGroupHasProteins", "proteinGroupHasProteinsAlias")
                .add(Restrictions.eq("proteinGroupHasProteinsAlias.protein", protein));

//        criteria.createAlias("peptideHasProtein.proteinGroup", "proteinGroup");
//        criteria.createAlias("proteinGroup.proteinGroupHasProtein", "proteinGroupHasProtein");
//        criteria.createAlias("proteinGroupHasProtein.protein", "protein");
//        criteria.add(Restrictions.eq("proteinGroupHasProteinsAlias.protein", protein))
//                .createCriteria("peptide")
//                .add(Restrictions.in("spectrum.id", spectrumIds));
        return criteria.list();
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
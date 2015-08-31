package com.compomics.colims.repository.impl;

import com.compomics.colims.model.*;
import com.compomics.colims.repository.PeptideRepository;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
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
                .createCriteria("peptide")
                .add(Restrictions.in("spectrum.id", spectrumIds))
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

    @Override
    public List<String> getProteinAccessionsForPeptide(Peptide peptide) {
        // decent temporary solution
        List stuff = getCurrentSession()
            .createSQLQuery("SELECT protein_accession  FROM peptide" +
                " LEFT OUTER JOIN peptide_has_protein_group ON peptide.id = peptide_has_protein_group.l_peptide_id" +
                " LEFT OUTER JOIN protein_group_has_protein ON peptide_has_protein_group.l_protein_group_id = protein_group_has_protein.l_protein_group_id" +
                " WHERE peptide.id = " + peptide.getId())
            .list();

        return stuff;
    }
}
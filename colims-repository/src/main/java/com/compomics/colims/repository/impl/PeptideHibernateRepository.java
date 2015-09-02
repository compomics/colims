package com.compomics.colims.repository.impl;

import com.compomics.colims.model.*;
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
    public List<Peptide> getPeptidesForProtein(Protein protein, List<Long> spectrumIds) {
        List<Peptide> peptides = new ArrayList<>();
        Joiner joiner = Joiner.on(",");

        String query = "SELECT DISTINCT peptide.id FROM peptide"
            + " LEFT JOIN peptide_has_protein_group ON peptide_has_protein_group.l_peptide_id = peptide.id"
            + " LEFT JOIN protein_group ON protein_group.id = peptide_has_protein_group.l_protein_group_id = protein_group.id"
            + " LEFT JOIN protein_group_has_protein ON protein_group_has_protein.l_protein_group_id = protein_group.id"
            + " WHERE l_spectrum_id IN (" + joiner.join(spectrumIds) + ")"
            + " AND protein_group_has_protein.l_protein_id = " + protein.getId();

        List idList = getCurrentSession()
            .createSQLQuery(query)
            .addScalar("peptide.id", LongType.INSTANCE)
            .list();

        if (idList.size() > 0) {
            peptides = createCriteria().add(Restrictions.in("id", idList)).list();
        }

        return peptides;
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
                " WHERE peptide.id = " + peptide.getId() +
                " AND protein_accession NOT NULL")
            .list();

        return stuff;
    }

    @Override
    public List<Peptide> getPeptidesForSpectrum(Spectrum spectrum) {
        return createCriteria()
            .add(Restrictions.eq("spectrum", spectrum))
            .list();
    }
}
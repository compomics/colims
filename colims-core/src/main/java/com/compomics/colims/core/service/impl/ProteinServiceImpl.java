package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.ProteinService;
import com.compomics.colims.model.Protein;
import com.compomics.colims.model.ProteinAccession;
import com.compomics.colims.repository.ProteinRepository;
import java.util.HashMap;
import org.hibernate.LazyInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author Niels Hulstaert
 */
@Service("proteinService")
@Transactional
public class ProteinServiceImpl implements ProteinService {

    /**
     * The map of cached proteins (key: sequence, value: the protein).
     */
    private final Map<String, Protein> cachedProteins = new HashMap<>();
    private final ProteinRepository proteinRepository;

    @Autowired
    public ProteinServiceImpl(ProteinRepository proteinRepository) {
        this.proteinRepository = proteinRepository;
    }

    @Override
    public Protein findBySequence(final String sequence) {
        return proteinRepository.findBySequence(sequence);
    }

    @Override
    public void fetchAccessions(Protein protein) {
        try {
            protein.getProteinAccessions().size();
        } catch (LazyInitializationException e) {
            List<ProteinAccession> proteinAccessions = proteinRepository.fetchProteinAccessions(protein.getId());
            protein.setProteinAccessions(proteinAccessions);
        }
    }

    @Override
    public Protein findById(final Long id) {
        return proteinRepository.findById(id);
    }

    @Override
    public List<Protein> findAll() {
        return proteinRepository.findAll();
    }

    @Override
    public long countAll() {
        return proteinRepository.countAll();
    }

    @Override
    public void persist(Protein entity) {
        proteinRepository.persist(entity);
    }

    @Override
    public Protein merge(Protein entity) {
        return proteinRepository.merge(entity);
    }

    @Override
    public void remove(Protein entity) {
        proteinRepository.remove(entity);
    }

    @Override
    public Protein getProtein(String sequence, String accession) {
        Protein targetProtein;

        //first, look in the newly added proteins map
        //@todo configure hibernate cache and check performance
        targetProtein = cachedProteins.get(sequence);
        if (targetProtein == null) {
            //check if the protein is found in the db
            targetProtein = findBySequence(sequence);

            if (targetProtein == null) {
                //map the utilities protein onto the Colims protein
                targetProtein = new Protein(sequence);
                ProteinAccession proteinAccession = new ProteinAccession(accession);

                //set entity associations
                proteinAccession.setProtein(targetProtein);
                targetProtein.getProteinAccessions().add(proteinAccession);
            } else {
                updateAccessions(targetProtein, accession);
            }

            //add to cached proteins
            cachedProteins.put(sequence, targetProtein);
        } else {
            updateAccessions(targetProtein, accession);
        }

        return targetProtein;
    }

    @Override
    public void clear() {
        cachedProteins.clear();
    }

    /**
     * Update the ProteinAccessions linked to a Protein.
     *
     * @param protein the Protein instance
     * @param accession the protein accession
     */
    private void updateAccessions(final Protein protein, final String accession) {
        //check if the protein accession is already linked to the protein
        boolean proteinAccessionPresent = false;

        //fetch the accessions if necessary
        fetchAccessions(protein);

        for (ProteinAccession proteinAccession : protein.getProteinAccessions()) {
            if (proteinAccession.getAccession().equals(accession)) {
                proteinAccessionPresent = true;
                break;
            }
        }

        if (!proteinAccessionPresent) {
            ProteinAccession proteinAccession = new ProteinAccession(accession);
            protein.getProteinAccessions().add(proteinAccession);

            //set entity associations
            proteinAccession.setProtein(protein);
            protein.getProteinAccessions().add(proteinAccession);
        }
    }

}

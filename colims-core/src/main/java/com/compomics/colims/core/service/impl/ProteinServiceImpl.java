package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.ProteinService;
import com.compomics.colims.model.Protein;
//import com.compomics.colims.model.ProteinAccession;
import com.compomics.colims.repository.ProteinRepository;
import java.util.HashMap;

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
    public Protein getProtein(String sequence, String description) {
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

            }
            if(targetProtein.getDescription() == null && description != null){
                targetProtein.setDescription(description);
            }
            //add to cached proteins
            cachedProteins.put(sequence, targetProtein);
        }else if(targetProtein.getDescription() == null && description != null){
            targetProtein.setDescription(description);
        }
        return targetProtein;
    }

    @Override
    public void clear() {
        cachedProteins.clear();
    }

}

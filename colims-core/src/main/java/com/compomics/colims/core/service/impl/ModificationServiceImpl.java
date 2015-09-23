package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.ModificationService;
import com.compomics.colims.model.Modification;
import com.compomics.colims.repository.ModificationRepository;
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.pride.CvTerm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Service("modificationService")
@Transactional
public class ModificationServiceImpl implements ModificationService {

    @Autowired
    private ModificationRepository modificationRepository;

    @Override
    public Modification findById(final Long id) {
        return modificationRepository.findById(id);
    }

    @Override
    public List<Modification> findAll() {
        return modificationRepository.findAll();
    }

    @Override
    public void save(final Modification entity) {
        modificationRepository.save(entity);
    }

    @Override
    public void update(final Modification entity) {
        modificationRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(final Modification entity) {
        modificationRepository.saveOrUpdate(entity);
    }

    @Override
    public void delete(final Modification entity) {
        modificationRepository.delete(entity);
    }

    @Override
    public Modification findByName(final String name) {
        return modificationRepository.findByName(name);
    }

    @Override
    public Modification findByAccession(final String accession) {
        return modificationRepository.findByAccession(accession);
    }

    @Override
    public long countAll() {
        return modificationRepository.countAll();
    }

    @Override
    public void addAllToPtmFactory() {
        List<Modification> modifications = modificationRepository.findAll();
        for (Modification modification : modifications) {
            //look if the Colims Modification instance has a corresponding PTM in the PTMFactory
            PTM foundPtm = findCorrespondingPtm(modification);
            if (foundPtm == null) {
                //add it to the PTMFactory as a user PTM
                PTM ptm = new PTM();
            }
        }
    }

    /**
     * Find the corresponding PTM for the given Colims Modification instance. Look for PTMs by CV term accession and/or
     * alternative accession.
     *
     * @param modification the Colims Modification instance
     * @return the Utilities PTM instance
     */
    private PTM findCorrespondingPtm(Modification modification) {
        PTMFactory ptmFactory = PTMFactory.getInstance();
        //iterate over the PTMs
        for (String ptmName : ptmFactory.getPTMs()) {
            PTM ptm = ptmFactory.getPTM(ptmName);

            //find by CV term accession first
            CvTerm cvTerm = ptm.getCvTerm();
            if (cvTerm != null && modification.getAccession().equals(cvTerm.getAccession())) {
                return ptm;
            }
            //find by modification name
            if (modification.getName().equals(ptmName)) {
                return ptm;
            }

        }

        return null;
    }
}

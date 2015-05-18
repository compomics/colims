package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.AnalyticalRunService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.repository.AnalyticalRunRepository;
import java.util.List;

import com.compomics.colims.repository.SpectrumRepository;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kenneth Verheggen
 */
@Service("analyticalRunService")
@Transactional
public class AnalyticalRunServiceImpl implements AnalyticalRunService {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(AnalyticalRunServiceImpl.class);

    @Autowired
    private AnalyticalRunRepository analyticalRunRepository;

    @Autowired
    private SpectrumRepository spectrumRepository;

    @Override
    public AnalyticalRun findById(final Long id) {
        return analyticalRunRepository.findById(id);
    }

    @Override
    public List<AnalyticalRun> findAll() {
        return analyticalRunRepository.findAll();
    }

    @Override
    public void save(final AnalyticalRun entity) {
        analyticalRunRepository.save(entity);
    }

    @Override
    public void update(final AnalyticalRun entity) {
        analyticalRunRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(final AnalyticalRun entity) {
        analyticalRunRepository.saveOrUpdate(entity);
    }

    @Override
    public void delete(final AnalyticalRun entity) {
        analyticalRunRepository.delete(entity);
    }

    @Override
    public long countAll() {
        return analyticalRunRepository.countAll();
    }

    @Override
    public void fetchSpectra(final AnalyticalRun analyticalRun) {
        try {
            //attach the analytical run to the new session
            analyticalRunRepository.saveOrUpdate(analyticalRun);
            if (!Hibernate.isInitialized(analyticalRun.getSpectrums())) {
                Hibernate.initialize(analyticalRun.getSpectrums());
            }
        } catch (HibernateException hbe) {
            LOGGER.error(hbe, hbe.getCause());
        }
    }
}

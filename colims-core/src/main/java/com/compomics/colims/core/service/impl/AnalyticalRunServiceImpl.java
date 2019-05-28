package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.AnalyticalRunService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.AnalyticalRunBinaryFile;
import com.compomics.colims.model.Instrument;
import com.compomics.colims.repository.AnalyticalRunRepository;
import com.compomics.colims.repository.InstrumentRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Kenneth Verheggen
 */
@Service("analyticalRunService")
@Transactional
public class AnalyticalRunServiceImpl implements AnalyticalRunService {

    private final AnalyticalRunRepository analyticalRunRepository;
    private final InstrumentRepository instrumentRepository;

    @Autowired
    public AnalyticalRunServiceImpl(AnalyticalRunRepository analyticalRunRepository, InstrumentRepository instrumentRepository) {
        this.analyticalRunRepository = analyticalRunRepository;
        this.instrumentRepository = instrumentRepository;
    }

    @Override
    public AnalyticalRun findById(final Long id) {
        return analyticalRunRepository.findById(id);
    }

    @Override
    public List<AnalyticalRun> findAll() {
        return analyticalRunRepository.findAll();
    }

    @Override
    public long countAll() {
        return analyticalRunRepository.countAll();
    }

    @Override
    public void persist(AnalyticalRun entity) {
        analyticalRunRepository.persist(entity);
    }

    @Override
    public AnalyticalRun merge(AnalyticalRun entity) {
        return analyticalRunRepository.merge(entity);
    }

    @Override
    public void remove(AnalyticalRun entity) {
        analyticalRunRepository.remove(entity);
    }

    @Override
    public List<AnalyticalRun> findBySampleId(Long sampleId) {
        return analyticalRunRepository.findBySampleId(sampleId);
    }

    @Override
    public void fetchInstrument(AnalyticalRun analyticalRun) {
        if (!Hibernate.isInitialized(analyticalRun.getInstrument())) {
            Instrument instrument = instrumentRepository.findByAnalyticalRunId(analyticalRun.getId());
            if (instrument != null) {
                analyticalRun.setInstrument(instrument);
            }
        }
//        try {
//            analyticalRun.getInstrument().getId();
//        } catch (LazyInitializationException e) {
//            Instrument instrument = instrumentRepository.findByAnalyticalRunId(analyticalRun.getId());
//            if (instrument != null) {
//                analyticalRun.setInstrument(instrument);
//            }
//        }
    }

    @Override
    public void fetchBinaryFiles(AnalyticalRun analyticalRun) {
        if (!Hibernate.isInitialized(analyticalRun.getBinaryFiles())) {
            //fetch the binary files
            List<AnalyticalRunBinaryFile> binaryFiles = analyticalRunRepository.fetchBinaryFiles(analyticalRun.getId());
            analyticalRun.setBinaryFiles(binaryFiles);
        }
    }

    @Override
    public void saveOrUpdate(AnalyticalRun analyticalRun) {
        analyticalRunRepository.saveOrUpdate(analyticalRun);
    }
}

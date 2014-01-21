package com.compomics.colims.core.service.impl.interceptable;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.colims.core.service.ProtocolService;
import com.compomics.colims.model.Protocol;
import com.compomics.colims.repository.ProtocolRepository;

/**
 *
 * @author Niels Hulstaert
 */
@Service("protocolService")
@Transactional
public class ProtocolServiceImpl implements ProtocolService {

    @Autowired
    private ProtocolRepository protocolRepository;

    @Override
    public Protocol findById(final Long id) {
        return protocolRepository.findById(id);
    }

    @Override
    public List<Protocol> findAll() {
        return protocolRepository.findAllOrderedByName();
    }

    @Override
    public void save(final Protocol entity) {
        protocolRepository.save(entity);
    }

    @Override
    public void delete(final Protocol entity) {
        protocolRepository.delete(entity);
    }

    @Override
    public Protocol findByName(final String name) {
        return protocolRepository.findByName(name);
    }

    @Override
    public void update(final Protocol entity) {
        protocolRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(final Protocol entity) {
        protocolRepository.saveOrUpdate(entity);
    }

    @Override
    public long countAll() {
        return protocolRepository.countAll();
    }
}

package com.compomics.colims.core.service.impl.interceptable;

import com.compomics.colims.core.service.ProtocolService;
import com.compomics.colims.model.Protocol;
import com.compomics.colims.repository.ProtocolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Service("protocolService")
@Transactional
public class ProtocolServiceImpl implements ProtocolService {

    @Autowired
    private ProtocolRepository protocolRepository;

    @Override
    public Protocol findById(final Long id) {
        Protocol protocol = protocolRepository.findById(id);

        //fetch the chemical labels
        protocol.getChemicalLabels().size();

        //fetch the other CV params
        protocol.getOtherCvParams().size();

        return protocol;
    }

    @Override
    public List<Protocol> findAll() {
        List<Protocol> protocols = protocolRepository.findAllOrderedByName();

        //fetch other CV params
        for (Protocol protocol : protocols) {
            protocol.getOtherCvParams().size();
        }

        return protocols;
    }

    @Override
    public Long countByName(final String name) {
        return protocolRepository.countByName(name);
    }

    @Override
    public long countAll() {
        return protocolRepository.countAll();
    }

    @Override
    public void persist(Protocol entity) {
        protocolRepository.persist(entity);
    }

    @Override
    public Protocol merge(Protocol entity) {
        return protocolRepository.merge(entity);
    }

    @Override
    public void remove(Protocol entity) {
        //get a reference to the entity
        Protocol reference = protocolRepository.getReference(entity.getId());
        protocolRepository.remove(reference);
    }
}

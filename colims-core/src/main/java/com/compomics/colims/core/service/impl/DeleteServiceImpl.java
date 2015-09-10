package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.distributed.model.DeleteDbTask;
import com.compomics.colims.core.service.DeleteService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.repository.ModificationRepository;
import com.compomics.colims.repository.ProteinRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by Niels Hulstaert on 9/09/15.
 */
public class DeleteServiceImpl implements DeleteService {

    @Autowired
    private ProteinRepository proteinRepository;
    @Autowired
    private ModificationRepository modificationRepository;


    @Override
    public void delete(DeleteDbTask deleteDbTask) {
        //check if the enitity to delete has analytical runs attached to it
    }

    /**
     * Delete the analytical run entry from the database with cascading of related child entities.
     *
     * @param analyticalRun the AnalyticalRun instance to delete
     */
    private void deleteAnalyticalRun(AnalyticalRun analyticalRun) {
        //get the IDs of the proteins linked to the run
        List<Long> proteinIds = proteinRepository.getProteinIdsForRun(analyticalRun);
        //get the IDs of the modifications linked to this run
        List<Long> modificationIds = modificationRepository.getModificationIdsForRun(analyticalRun);


    }
}

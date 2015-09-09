package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.distributed.model.DeleteDbTask;
import com.compomics.colims.core.service.DeleteService;
import com.compomics.colims.model.AnalyticalRun;

/**
 * Created by Niels Hulstaert on 9/09/15.
 */
public class DeleteServiceImpl implements DeleteService {

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

    }
}

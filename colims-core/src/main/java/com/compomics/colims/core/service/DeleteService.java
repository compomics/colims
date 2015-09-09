package com.compomics.colims.core.service;

import com.compomics.colims.core.distributed.model.DeleteDbTask;

/**
 * This interface provides service methods for the removal of database entries and relations (projects, experiments,
 * samples, ...).
 *
 * @author Niels Hulstaert
 */
public interface DeleteService {

    /**
     * Delete the database entity. This deletes the entity and cascade deletes the underlying entities that are not
     * related directly to other entities.
     *
     * @param deleteDbTask the DeleteDbTask instance
     */
    void delete(DeleteDbTask deleteDbTask);

}

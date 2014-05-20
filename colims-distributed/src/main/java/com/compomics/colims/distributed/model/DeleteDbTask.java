package com.compomics.colims.distributed.model;

/**
 *
 * @author Niels Hulstaert
 */
public class DeleteDbTask extends DbTask {

    public DeleteDbTask(Class dbEntityClass, Long enitityId, Long userId) {
        super(dbEntityClass, enitityId, userId);
    }

}

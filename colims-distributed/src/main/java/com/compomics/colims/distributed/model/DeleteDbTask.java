package com.compomics.colims.distributed.model;

/**
 * 
 *
 * @author Niels Hulstaert
 */
public class DeleteDbTask extends DbTask {

    private static final long serialVersionUID = 7751981732618552750L;

    /**
     * Constructor.
     *
     * @param dbEntityClass the class of the entity to delete
     * @param enitityId the ID of the entity in the database
     * @param userId the ID of the user in the database
     */
    public DeleteDbTask(Class dbEntityClass, Long enitityId, Long userId) {
        super(dbEntityClass, enitityId, userId);
    }

}

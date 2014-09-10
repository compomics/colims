package com.compomics.colims.distributed.model;

/**
 *
 * @author Niels Hulstaert
 */
public class DeleteDbTask extends DbTask {
    
    private static final long serialVersionUID = 7751981732618552750L;

    public DeleteDbTask(Class dbEntityClass, Long enitityId, Long userId) {
        super(dbEntityClass, enitityId, userId);
    }    

}

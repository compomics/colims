
package com.compomics.colims.distributed.model;

import com.compomics.colims.distributed.model.enums.DbEntityType;

/**
 *
 * @author Niels Hulstaert
 */
public class DeleteDbTask extends DbTask {

    public DeleteDbTask(DbEntityType dbEntityType, Long enitityId, Long userId) {
        super(dbEntityType, enitityId, userId);
    }
                   
}

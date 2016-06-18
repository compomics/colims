package com.compomics.colims.client.event.admin;

import com.compomics.colims.client.event.EntityChangeEvent;
import com.compomics.colims.model.cv.CvParam;

/**
 * This class is used for passing CV param change events from the
 * CvParamManagementController to other controllers.
 *
 * @author Niels Hulstaert
 */
public class CvParamChangeEvent extends EntityChangeEvent {

    /**
     * The CV param instance.
     */
    private final CvParam cvParam;

    /**
     * Constructor.
     *
     * @param type the entity change event type
     * @param cvParam the CV param instance
     */
    public CvParamChangeEvent(final Type type, final CvParam cvParam) {
        super(type);
        this.cvParam = cvParam;
    }

    public CvParam getCvParam() {
        return cvParam;
    }

}

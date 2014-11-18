package com.compomics.colims.client.event;

import com.compomics.colims.model.AnalyticalRun;

/**
 * @author Niels Hulstaert
 */
public class AnalyticalRunChangeEvent extends EntityChangeEvent {

    private final AnalyticalRun analyticalRun;

    /**
     * Constructor.
     *
     * @param type the change event type
     * @param analyticalRun the AnalyticalRun instance
     */
    public AnalyticalRunChangeEvent(final Type type, final AnalyticalRun analyticalRun) {
        super(type);
        this.analyticalRun = analyticalRun;
    }

    public AnalyticalRun getAnalyticalRun() {
        return analyticalRun;
    }
    
}

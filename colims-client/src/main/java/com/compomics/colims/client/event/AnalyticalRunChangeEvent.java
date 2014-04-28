package com.compomics.colims.client.event;

import com.compomics.colims.model.AnalyticalRun;

/**
 * @author Niels Hulstaert
 */
public class AnalyticalRunChangeEvent extends EntityChangeEvent {

    private final AnalyticalRun analyticalRun;

    /**
     *
     * @param type
     * @param analyticalRun
     */
    public AnalyticalRunChangeEvent(final Type type, final AnalyticalRun analyticalRun) {
        super(type);
        this.analyticalRun = analyticalRun;
    }

    /**
     *
     * @return
     */
    public AnalyticalRun getAnalyticalRun() {
        return analyticalRun;
    }
    
}

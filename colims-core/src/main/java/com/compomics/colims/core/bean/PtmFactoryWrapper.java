package com.compomics.colims.core.bean;

import com.compomics.util.experiment.biology.PTMFactory;
import org.springframework.stereotype.Component;

/**
 * Wrapper bean around the utilities PtmFactory to avoid calls to the
 * 'newInstance' method.
 *
 * @author Niels Hulstaert
 */
@Component("ptmFactoryWrapper")
public class PtmFactoryWrapper {

    /**
     * The PtmFactory instance.
     */
    private PTMFactory ptmFactory = PTMFactory.getInstance();

    public PTMFactory getPtmFactory() {
        return ptmFactory;
    }

}

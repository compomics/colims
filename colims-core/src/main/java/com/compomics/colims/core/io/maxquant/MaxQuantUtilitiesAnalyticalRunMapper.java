package com.compomics.colims.core.io.maxquant;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.Mapper;
import com.compomics.colims.model.AnalyticalRun;
import org.springframework.stereotype.Component;

/**
 *
 * @author Davy
 */
@Component("maxQuantUtilitiesAnalyticalRunMapper")
public class MaxQuantUtilitiesAnalyticalRunMapper implements Mapper<MaxQuantAnalyticalRun, AnalyticalRun> {

    @Override
    public void map(final MaxQuantAnalyticalRun source, final AnalyticalRun target) throws MappingException {
        target.setName(source.getAnalyticalRunName());
    }
}

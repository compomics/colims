package com.compomics.colims.core.mapper.impl.maxQuantToColims;

import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.core.io.parser.impl.MaxQuantAnalyticalRun;
import com.compomics.colims.core.mapper.Mapper;
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

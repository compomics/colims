package com.compomics.colims.core.io.maxquant.utilities_mappers;

import com.compomics.colims.core.io.Mapper;
import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.maxquant.MaxQuantAnalyticalRun;
import com.compomics.colims.model.AnalyticalRun;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Davy
 */
@Component("maxQuantUtilitiesAnalyticalRunMapper")
public class MaxQuantUtilitiesAnalyticalRunMapper implements Mapper<MaxQuantAnalyticalRun, AnalyticalRun> {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(MaxQuantUtilitiesAnalyticalRunMapper.class);

    @Override
    public void map(final MaxQuantAnalyticalRun source, final AnalyticalRun target) throws MappingException {
        target.setName(source.getAnalyticalRunName());
        try {
            target.setStorageLocation(source.getMaxQuantDirectory().getCanonicalPath());
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new MappingException(ex.getMessage(), ex.getCause());
        }
    }
}

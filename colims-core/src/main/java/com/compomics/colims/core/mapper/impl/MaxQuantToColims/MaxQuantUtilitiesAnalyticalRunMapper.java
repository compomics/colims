package com.compomics.colims.core.mapper.impl.MaxQuantToColims;

import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.core.io.parser.impl.MaxQuantAnalyticalRun;
import com.compomics.colims.core.mapper.Mapper;
import com.compomics.colims.core.mapper.impl.utilitiesToColims.UtilitiesSpectrumMapper;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.enums.FragmentationType;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Davy
 */
@Component("maxQuantUtilitiesAnalyticalRunMapper")
public class MaxQuantUtilitiesAnalyticalRunMapper implements Mapper<MaxQuantAnalyticalRun, AnalyticalRun> {

    @Override
    public void map(MaxQuantAnalyticalRun source, AnalyticalRun target) throws MappingException {
        target.setName(source.getAnalyticalRunName());
    }
}

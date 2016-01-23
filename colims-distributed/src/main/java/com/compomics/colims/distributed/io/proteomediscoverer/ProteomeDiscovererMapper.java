package com.compomics.colims.distributed.io.proteomediscoverer;


import com.compomics.colims.core.io.MappedData;
import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.ProteomeDiscovererImport;
import com.compomics.colims.distributed.io.DataMapper;
import com.compomics.colims.model.AnalyticalRun;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Davy Maddelein on 18/01/2016.
 */
public class ProteomeDiscovererMapper implements DataMapper<ProteomeDiscovererImport> {

    private static final Logger LOGGER = Logger.getLogger(ProteomeDiscovererMapper.class);


    @Override
    public MappedData mapData(ProteomeDiscovererImport dataImport) throws MappingException {

        LOGGER.info("started mapping for file: "+ dataImport.getProteomeDiscovererDatabase().getName());

        List<AnalyticalRun> runsInDb = new ArrayList<>();

        return null;
    }

    @Override
    public void clear() throws Exception {

    }
}

package com.compomics.colims.distributed.io.proteomediscoverer.parsers;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.distributed.io.maxquant.UnparseableException;
import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.enums.FastaDbType;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.EnumMap;

/**
 * Created by Davy Maddelein on 23/01/2016.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})

public class ProteomeDiscovererParserTest {

    @Autowired
    ProteomeDiscovererParser proteomeDiscovererParser;



        @Before
        public void setUp() throws MappingException, UnparseableException, IOException {
            FastaDb proteomeDiscovererFastaDb = new FastaDb();

            EnumMap<FastaDbType, FastaDb> fastaDbs = new EnumMap<>(FastaDbType.class);
            fastaDbs.put(FastaDbType.PRIMARY, proteomeDiscovererFastaDb);



        }

}

package com.compomics.colims.distributed.io.proteomediscoverer.parsers;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by Davy Maddelein on 23/01/2016.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})

public class ProteomeDiscovererParserTest {

    @Autowired
    ProteomeDiscovererParser proteomeDiscovererParser;


}

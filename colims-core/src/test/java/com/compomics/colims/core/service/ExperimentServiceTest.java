
package com.compomics.colims.core.service;

import com.compomics.colims.model.Experiment;
import com.compomics.colims.model.InstrumentCvParam;
import com.compomics.colims.model.enums.CvParamType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class ExperimentServiceTest {

    @Autowired
    private ExperimentService experimentService;

    @Test
    public void testBsllala() {
        Experiment experiment = experimentService.findByProjectIdAndTitle(1L, "ififi");

        System.out.println("fffffffff");
    }

}


package com.compomics.colims.core.service;

import com.compomics.colims.model.ProteinGroup;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
@Rollback
@Transactional
public class ProteinGroupServiceTest {

    @Autowired
    private ProteinGroupService proteinGroupService;

    @Test
    public void testGetProteinGroupsForRuns() {
        List<Long> runIds = new ArrayList<>();
        runIds.add(1L);

        List<ProteinGroup> proteinGroupsForRuns = proteinGroupService.getProteinGroupsForRuns(runIds);

        Assert.assertEquals(4, proteinGroupsForRuns.size());
    }

}

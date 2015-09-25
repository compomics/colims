package com.compomics.colims.repository;

import com.compomics.colims.model.Modification;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-repository-context.xml", "classpath:colims-repository-test-context.xml"})
@Transactional
public class ModificationRepositoryTest {

    @Autowired
    private ModificationRepository modificationRepository;

    @Test
    public void testFindByName() {
        //try to find a non existing modification
        Modification modification = modificationRepository.findByName("nonexisting");

        Assert.assertNull(modification);

        //find an existing modification
        modification = modificationRepository.findByName("methionine oxidation with neutral loss of 64 Da");

        Assert.assertNotNull(modification);
        //check the ID
        Assert.assertNotNull(modification.getId());
    }

    @Test
    public void testFindByAccession() {
        //try to find a non existing modification
        Modification modification = modificationRepository.findByAccession("nonexisting");

        Assert.assertNull(modification);

        //find an existing modification
        modification = modificationRepository.findByAccession("MOD:00935");

        Assert.assertNotNull(modification);
        //check the ID
        Assert.assertNotNull(modification.getId());
    }

    @Test
    public void testFindByUtilitiesPtmName() {
        //try to find a non existing modification
        Modification modification = modificationRepository.findByUtilitiesPtmName("nonexisting");

        Assert.assertNull(modification);

        //find an existing modification
        modification = modificationRepository.findByUtilitiesPtmName("test_utilities_name");

        Assert.assertNotNull(modification);
        //check the ID
        Assert.assertNotNull(modification.getId());
    }

    @Test
    public void testGetConstraintLessModificationIdsForRuns() {
        List<Long> runIds = new ArrayList<>();
        runIds.add(1L);

        List<Long> modificationIds = modificationRepository.getConstraintLessModificationIdsForRuns(runIds);

        Assert.assertEquals(1, modificationIds.size());
    }
}

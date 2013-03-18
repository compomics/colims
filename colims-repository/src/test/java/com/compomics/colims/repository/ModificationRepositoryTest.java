package com.compomics.colims.repository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.colims.model.Modification;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:colims-repository-context.xml", "classpath:colims-repository-test-context.xml" })
@Transactional
public class ModificationRepositoryTest {
    @Autowired
    ModificationRepository repo;

    String name = "modificationName";
    Long id;

    @Before
    public void saveSpectrum() {
        //Store an initial modification
        Modification modification = new Modification(name);
        repo.save(modification);

        //Ensure it's persisted
        Assert.assertNotNull("Identifier should be assigned now", id = modification.getId());
    }

    @Test
    public final void testFindByName() {
        //Find
        Modification modification = repo.findByName(name);

        //Compare expected values 
        Assert.assertNotNull(modification);
        Assert.assertEquals(name, modification.getName());
        Assert.assertEquals(id, modification.getId());
    }
}

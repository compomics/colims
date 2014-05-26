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
    private ModificationRepository modificationRepository;

    private String accession = "modificationAccession";
    private String name = "modificationName";
    private Long id;

    @Before
    public void saveSpectrum() {
        //Store an initial modification
        Modification modification = new Modification(name);
        modification.setAccession(accession);
        modificationRepository.save(modification);

        //Ensure it's stored
        Assert.assertNotNull("Identifier should be assigned now", id = modification.getId());
    }

    @Test
    public void testFindByName() {
        //Find
        Modification modification = modificationRepository.findByName(name);

        //Compare expected values 
        Assert.assertNotNull(modification);
        Assert.assertEquals(accession, modification.getAccession());
        Assert.assertEquals(name, modification.getName());
        Assert.assertEquals(id, modification.getId());
    }
    
    @Test
    public void testFindByAccession() {
        //Find
        Modification modification = modificationRepository.findByAccession(accession);

        //Compare expected values 
        Assert.assertNotNull(modification);
        Assert.assertEquals(accession, modification.getAccession());
        Assert.assertEquals(name, modification.getName());
        Assert.assertEquals(id, modification.getId());
    }
}

package com.compomics.colims.repository;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.colims.model.Protein;
import com.compomics.util.protein.Header.DatabaseType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:colims-repository-context.xml", "classpath:colims-repository-test-context.xml" })
@Transactional
public class ProteinRepositoryTest {
    @Autowired
    ProteinRepository repo;

    String accession = "ABCDEFGH1234";
    String sequence = "ACTGACTGACTGA";
    long id;

    @Before
    public void saveProtein() {
        //Setup and store protein
        Protein stored = new Protein();
        stored.setAccession(accession);
        stored.setDatabaseType(DatabaseType.Unknown);
        stored.setSequence(sequence);
        repo.save(stored);
        Assert.assertNotNull("Identifier should be assigned now", stored.getId());
        id = stored.getId();
    }

    @Test
    public final void testFindByAccession() {
        //Find protein
        Protein found = repo.findByAccession(accession);
        Assert.assertNotNull("Protein should be found by it's accession", found);
        Assert.assertEquals(accession, found.getAccession());
    }

    @Test
    public final void testGetEntityClass() {
        Assert.assertEquals(Protein.class, repo.getEntityClass());
    }

    @Test
    public final void testFindById() {
        Protein found = repo.findById(id);
        Assert.assertNotNull(found);
        Assert.assertEquals(id, found.getId().longValue());
        Assert.assertEquals(sequence, found.getSequence());
    }

    @Test
    public final void testFindByExample() {
        Protein example = new Protein();
        example.setAccession(accession);
        List<Protein> hits = repo.findByExample(example);
        Assert.assertEquals(1, hits.size());
        Assert.assertEquals(accession, hits.get(0).getAccession());
    }

    @Test
    public final void testCountByExample() {
        Protein example = new Protein();
        example.setAccession(accession);
        Assert.assertEquals(1, repo.countByExample(example));
    }

    @Test
    public final void testFindAll() {
        List<Protein> findAll = repo.findAll();
        Assert.assertEquals(1, findAll.size());
    }

    @Test
    public final void testCountAll() {
        Assert.assertEquals(1, repo.countAll());
    }
}

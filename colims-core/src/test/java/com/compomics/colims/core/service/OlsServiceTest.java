package com.compomics.colims.core.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.colims.model.Modification;
import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class OlsServiceTest {

    @Autowired
    private OlsService olsService;

    /**
     * Test the find modification by accession method from the OlsService.
     */
    @Test
    public void testFindModificationByAccession() {
        //try to find a non existing modification
        Modification modification = olsService.findModifiationByAccession("MOD:00935999");

        Assert.assertNull(modification);

        //try to find an existing modification
        modification = olsService.findModifiationByAccession("MOD:00935");

        Assert.assertNotNull(modification);
        Assert.assertEquals("MOD:00935", modification.getAccession());
        Assert.assertEquals("methionine oxidation with neutral loss of 64 Da", modification.getName());
        Assert.assertEquals(-63.998286, modification.getMonoIsotopicMassShift(), 0.001);
        Assert.assertEquals(-64.1, modification.getAverageMassShift(), 0.001);
    }

    /**
     * Test the find modification by exact name method from the OlsService.
     */
    @Test
    public void testFindModificationByExactName() {
        //try to find a non existing modification
        Modification modification = olsService.findModifiationByExactName("non existing modification");

        Assert.assertNull(modification);

        //try to find an existing modification
        modification = olsService.findModifiationByExactName("methionine oxidation with neutral loss of 64 Da");

        Assert.assertNotNull(modification);
        Assert.assertEquals("MOD:00935", modification.getAccession());
        Assert.assertEquals("methionine oxidation with neutral loss of 64 Da", modification.getName());
        Assert.assertEquals(-63.998286, modification.getMonoIsotopicMassShift(), 0.001);
        Assert.assertEquals(-64.1, modification.getAverageMassShift(), 0.001);
    }

    /**
     * Test the find modification by name method from the OlsService.
     */
    @Test
    public void testFindModificationByName() {
        //try to find a non existing modification
        List<Modification> modifications = olsService.findModifiationByName("non existing modification");
        Assert.assertTrue(modifications.isEmpty());

        //try to find an existing modification, the ols web service should return 3 mods
        modifications = olsService.findModifiationByName("oxidation of m");
        Assert.assertEquals(3, modifications.size());
    }

    /**
     * Test the find a modification by name and UNIMOD accession method from the OlsService.
     */
    @Test
    public void testFindModificationByNameAndUnimodAccession_1() {
        Modification modification = olsService.findModifiationByNameAndUnimodAccession("Phospho", "UNIMOD:21");
        Assert.assertNotNull(modification);
        Assert.assertNotNull(modification.getAlternativeAccession());
    }

    /**
     * Test the find a modification by name and UNIMOD accession method from the OlsService.
     */
    @Test
    public void testFindModificationByNameAndUnimodAccession_2() {
        Modification modification = olsService.findModifiationByNameAndUnimodAccession("Ammonia-loss", "UNIMOD:385");
        Assert.assertNotNull(modification);
        Assert.assertNotNull(modification.getAlternativeAccession());
    }

}

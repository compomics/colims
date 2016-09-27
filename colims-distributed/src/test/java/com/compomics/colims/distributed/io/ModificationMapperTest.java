package com.compomics.colims.distributed.io;

import com.compomics.colims.core.service.OlsService;
import com.compomics.colims.model.Modification;
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})
public class ModificationMapperTest {

    @Autowired
    private ModificationMapper modificationMapper;

    /**
     * Clear the modifications cache.
     */
    @After
    public void clearCache() {
        modificationMapper.clear();
    }

    /**
     * Test the mapper for a modification that is present in the Compomics Utilities {@link PTMFactory}.
     */
    @Test
    public void testMapByOntologyTerm1() {
        PTMFactory ptmFactory = PTMFactory.getInstance();
        PTM ptm1 = ptmFactory.getPTM("Carbamidomethylation of C");

        Modification modification = modificationMapper.mapByOntologyTerm(
                ptm1.getCvTerm().getOntology(),
                ptm1.getCvTerm().getAccession(),
                ptm1.getCvTerm().getName(),
                ptm1.getCvTerm().getValue(),
                ptm1.getName()
        );

        Assert.assertNotNull(modification);
        Assert.assertEquals("UNIMOD:4", modification.getAccession());
        Assert.assertEquals("Carbamidomethyl", modification.getName());
        Assert.assertEquals("Carbamidomethylation of C", modification.getUtilitiesName());
        Assert.assertNotNull(modification.getMonoIsotopicMassShift());
        Assert.assertNotNull(modification.getAverageMassShift());
    }

    /**
     * Test the mapper for a modification that is present UNIMOD.
     */
    @Test
    public void testMapByOntologyTerm2() {
        Modification modification = modificationMapper.mapByOntologyTerm(
                "UNIMOD",
                "UNIMOD:1580",
                "dHex(1)HexNAc(3)"
        );

        Assert.assertNotNull(modification);
        Assert.assertEquals("UNIMOD:1580", modification.getAccession());
        Assert.assertEquals("dHex(1)HexNAc(3)", modification.getName());
        Assert.assertNull(modification.getUtilitiesName());
        Assert.assertNotNull(modification.getMonoIsotopicMassShift());
        Assert.assertNotNull(modification.getAverageMassShift());
    }

    /**
     * Test the mapper for a modification that is found by the {@link OlsService}.
     */
    @Test
    public void testMapByOntologyTerm3() {
        Modification modification = modificationMapper.mapByOntologyTerm(
                "MOD",
                "MOD:00090",
                "L-alanine amide"
        );

        Assert.assertNotNull(modification);
        Assert.assertEquals("MOD:00090", modification.getAccession());
        Assert.assertEquals("L-alanine amide", modification.getName());
        Assert.assertNull(modification.getUtilitiesName());
        Assert.assertNotNull(modification.getMonoIsotopicMassShift());
        Assert.assertNotNull(modification.getAverageMassShift());
    }

    /**
     * Test the mapper for a modification that is present in the Compomics Utilities {@link PTMFactory}.
     */
    @Test
    public void testMapByName1() {
        Modification modification = modificationMapper.mapByName(
                "Carbamidomethylation of C"
        );

        Assert.assertNotNull(modification);
        Assert.assertEquals("UNIMOD:4", modification.getAccession());
        Assert.assertEquals("Carbamidomethyl", modification.getName());
        Assert.assertEquals("Carbamidomethylation of C", modification.getUtilitiesName());
        Assert.assertNotNull(modification.getMonoIsotopicMassShift());
        Assert.assertNotNull(modification.getAverageMassShift());
    }

    /**
     * Test the mapper for a modification that is present in UNIMOD.
     */
    @Test
    public void testMapByName2() {
        Modification modification = modificationMapper.mapByName(
                "dHex(1)HexNAc(3)"
        );

        Assert.assertNotNull(modification);
        Assert.assertEquals("UNIMOD:1580", modification.getAccession());
        Assert.assertEquals("dHex(1)HexNAc(3)", modification.getName());
        Assert.assertNull(modification.getUtilitiesName());
        Assert.assertNotNull(modification.getMonoIsotopicMassShift());
        Assert.assertNotNull(modification.getAverageMassShift());
    }

    /**
     * Test the mapper for a modification that is found by the {@link OlsService}.
     */
    @Test
    public void testMapByName3() {
        Modification modification = modificationMapper.mapByName(
                "L-alanine amide"
        );

        Assert.assertNotNull(modification);
        Assert.assertEquals("MOD:00090", modification.getAccession());
        Assert.assertEquals("L-alanine amide", modification.getName());
        Assert.assertNull(modification.getUtilitiesName());
        Assert.assertNotNull(modification.getMonoIsotopicMassShift());
        Assert.assertNotNull(modification.getAverageMassShift());
    }

}

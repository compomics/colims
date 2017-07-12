package com.compomics.colims.distributed.io;

import com.compomics.colims.core.service.OlsService;
import com.compomics.colims.model.SearchModification;
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
public class SearchModificationMapperTest {

    @Autowired
    private SearchModificationMapper searchModificationMapper;

    /**
     * Clear the modifications cache.
     */
    @After
    public void clearCache() {
        searchModificationMapper.clear();
    }

    /**
     * Test the mapper for a modification that is present in the Compomics Utilities {@link PTMFactory}.
     */
    @Test
    public void testMapByOntologyTerm1() {
        PTMFactory ptmFactory = PTMFactory.getInstance();
        PTM ptm1 = ptmFactory.getPTM("Carbamidomethylation of C");

        SearchModification searchModification = searchModificationMapper.mapByOntologyTerm(
                ptm1.getCvTerm().getOntology(),
                ptm1.getCvTerm().getAccession(),
                ptm1.getCvTerm().getName(),
                ptm1.getCvTerm().getValue(),
                ptm1.getName()
        );

        Assert.assertNotNull(searchModification);
        Assert.assertEquals("UNIMOD:4", searchModification.getAccession());
        Assert.assertEquals("Carbamidomethyl", searchModification.getName());
        Assert.assertEquals("Carbamidomethylation of C", searchModification.getUtilitiesName());
        Assert.assertNotNull(searchModification.getMonoIsotopicMassShift());
        Assert.assertNotNull(searchModification.getAverageMassShift());
    }

    /**
     * Test the mapper for a modification that is present UNIMOD.
     */
    @Test
    public void testMapByOntologyTerm2() {
        SearchModification searchModification = searchModificationMapper.mapByOntologyTerm(
                "UNIMOD",
                "UNIMOD:1580",
                "dHex(1)HexNAc(3)",
                null
        );

        Assert.assertNotNull(searchModification);
        Assert.assertEquals("UNIMOD:1580", searchModification.getAccession());
        Assert.assertEquals("dHex(1)HexNAc(3)", searchModification.getName());
        Assert.assertNull(searchModification.getUtilitiesName());
        Assert.assertNotNull(searchModification.getMonoIsotopicMassShift());
        Assert.assertNotNull(searchModification.getAverageMassShift());
    }

    /**
     * Test the mapper for a modification that is found by the {@link OlsService}.
     */
    @Test
    public void testMapByOntologyTerm3() {
        SearchModification searchModification = searchModificationMapper.mapByOntologyTerm(
                "MOD",
                "MOD:00090",
                "L-alanine amide",
                null
        );

        Assert.assertNotNull(searchModification);
        Assert.assertEquals("MOD:00090", searchModification.getAccession());
        Assert.assertEquals("L-alanine amide", searchModification.getName());
        Assert.assertNull(searchModification.getUtilitiesName());
        Assert.assertNotNull(searchModification.getMonoIsotopicMassShift());
        Assert.assertNotNull(searchModification.getAverageMassShift());
    }

    /**
     * Test the mapper for a modification that is found in the database.
     */
    @Test
    public void testMapByOntologyTerm4() {
        SearchModification searchModification = searchModificationMapper.mapByOntologyTerm(
                "MOD",
                "MOD:00696",
                "Phosphorylation of S",
                null
        );

        Assert.assertNotNull(searchModification);
        Assert.assertEquals("MOD:00696", searchModification.getAccession());
        Assert.assertEquals("phosphorylated residue", searchModification.getName());
        Assert.assertNotNull(searchModification.getUtilitiesName());
        Assert.assertNotNull(searchModification.getMonoIsotopicMassShift());
        Assert.assertNotNull(searchModification.getAverageMassShift());
    }

    /**
     * Test the mapper for a modification that is present in the Compomics Utilities {@link PTMFactory}.
     */
    @Test
    public void testMapByName1() {
        SearchModification searchModification = searchModificationMapper.mapByName(
                "Carbamidomethylation of C"
        );

        Assert.assertNotNull(searchModification);
        Assert.assertEquals("UNIMOD:4", searchModification.getAccession());
        Assert.assertEquals("Carbamidomethyl", searchModification.getName());
        Assert.assertEquals("Carbamidomethylation of C", searchModification.getUtilitiesName());
        Assert.assertNotNull(searchModification.getMonoIsotopicMassShift());
        Assert.assertNotNull(searchModification.getAverageMassShift());
    }

    /**
     * Test the mapper for a modification that is present in UNIMOD.
     */
    @Test
    public void testMapByName2() {
        SearchModification searchModification = searchModificationMapper.mapByName(
                "dHex(1)HexNAc(3)"
        );

        Assert.assertNotNull(searchModification);
        Assert.assertEquals("UNIMOD:1580", searchModification.getAccession());
        Assert.assertEquals("dHex(1)HexNAc(3)", searchModification.getName());
        Assert.assertNull(searchModification.getUtilitiesName());
        Assert.assertNotNull(searchModification.getMonoIsotopicMassShift());
        Assert.assertNotNull(searchModification.getAverageMassShift());
    }

    /**
     * Test the mapper for a modification that is found by the {@link OlsService}.
     */
    @Test
    public void testMapByName3() {
        SearchModification searchModification = searchModificationMapper.mapByName(
                "L-alanine amide"
        );

        Assert.assertNotNull(searchModification);
        Assert.assertEquals("MOD:00090", searchModification.getAccession());
        Assert.assertEquals("L-alanine amide", searchModification.getName());
        Assert.assertNull(searchModification.getUtilitiesName());
        Assert.assertNotNull(searchModification.getMonoIsotopicMassShift());
        Assert.assertNotNull(searchModification.getAverageMassShift());
    }

}

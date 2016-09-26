package com.compomics.colims.distributed.io.unimod;

import com.compomics.colims.model.Modification;
import com.compomics.colims.model.SearchModification;
import org.jdom2.JDOMException;
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
@ContextConfiguration(locations = {"classpath:colims-distributed-simple-test-context.xml"})
public class UnimodMarshallerTest {

    private static final String MOD_NAME = "DTT_C";
    private static final String MOD_ACCESSION = "UNIMOD:736";

    @Autowired
    private UnimodMarshaller unimodMarshaller;

    /**
     * Test the marshalling of the unimod.xml file.
     *
     * @throws JDOMException top level exception that can be thrown in case of a
     * problem in the JDOM classes.
     */
    @Test
    public void testMarshall() throws JDOMException {
        Assert.assertFalse(unimodMarshaller.getModifications().isEmpty());
    }

    /**
     * Test the retrieval of a modification from the marshaller of both
     * Modification and SearchModification instances.
     */
    @Test
    public void testGetModificationByAccession() {
        SearchModification searchModification = unimodMarshaller.getModificationByAccession(SearchModification.class, MOD_ACCESSION);
        Assert.assertNotNull(searchModification);

        Modification modification = unimodMarshaller.getModificationByAccession(Modification.class, MOD_ACCESSION);
        Assert.assertNotNull(modification);
        Assert.assertEquals(MOD_ACCESSION, modification.getAccession());
        Assert.assertEquals(MOD_NAME, modification.getName());
        Assert.assertEquals(120.0245, modification.getMonoIsotopicMassShift(), 0.0001);
        Assert.assertEquals(120.1701, modification.getAverageMassShift(), 0.0001);
    }

    /**
     * Test the retrieval of a modification from the marshaller of both
     * Modification and SearchModification instances.
     */
    @Test
    public void testGetModificationByName() {
        SearchModification searchModification = unimodMarshaller.getModificationByName(SearchModification.class, MOD_NAME);
        Assert.assertNotNull(searchModification);

        Modification modification = unimodMarshaller.getModificationByName(Modification.class, MOD_NAME);
        Assert.assertNotNull(modification);
        Assert.assertEquals(MOD_ACCESSION, modification.getAccession());
        Assert.assertEquals(MOD_NAME, modification.getName());
        Assert.assertEquals(120.0245, modification.getMonoIsotopicMassShift(), 0.0001);
        Assert.assertEquals(120.1701, modification.getAverageMassShift(), 0.0001);
    }

}

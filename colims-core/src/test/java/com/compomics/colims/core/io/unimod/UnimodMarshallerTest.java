package com.compomics.colims.core.io.unimod;

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
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
public class UnimodMarshallerTest {

    private static final String MOD_NAME = "DTT_C";

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
    public void testGetModificationByName() {
        SearchModification searchModification = unimodMarshaller.getModificationByName(SearchModification.class, MOD_NAME);
        Assert.assertNotNull(searchModification);

        Modification modification = unimodMarshaller.getModificationByName(Modification.class, MOD_NAME);
        Assert.assertNotNull(modification);
    }

}

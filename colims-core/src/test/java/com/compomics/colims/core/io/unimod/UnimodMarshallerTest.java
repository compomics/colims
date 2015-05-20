package com.compomics.colims.core.io.unimod;

import com.compomics.colims.model.Modification;
import com.compomics.colims.model.SearchModification;
import org.jdom2.JDOMException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import static org.junit.Assert.assertThat;

/**
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
public class UnimodMarshallerTest {

    private Resource unimodResource;
    @Autowired
    private UnimodMarshaller unimodMarshaller;

    public UnimodMarshallerTest() throws IOException {
        unimodResource = new ClassPathResource("unimod/unimod.xml");
    }

    /**
     * Test the marshalling of the unimod.xml file
     *
     * @throws JDOMException top level exception that can be thrown in case of a problem in the JDOM classes.
     */
    @Test
    public void testMarshall() throws JDOMException {
        Assert.assertFalse(unimodMarshaller.getModifications().isEmpty());
    }

    @Test
    public void testGetModificationByName() {
        SearchModification searchModification = unimodMarshaller.getModificationByName(SearchModification.class, "DTT_C");
        Assert.assertNotNull(searchModification);

        Modification modification = unimodMarshaller.getModificationByName(Modification.class, "DTT_C");
        Assert.assertNotNull(modification);
    }


}

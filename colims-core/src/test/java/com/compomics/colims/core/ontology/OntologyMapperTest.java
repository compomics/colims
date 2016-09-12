package com.compomics.colims.core.ontology;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;

/**
 * Created by Niels Hulstaert on 12/09/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-simple-test-context.xml"})
public class OntologyMapperTest {

    @Autowired
    private OntologyMapper ontologyMapper;

    @Test
    public void testGetExisitingMappedTerm() throws HttpClientErrorException, IOException {
        OntologyTerm mappedTerm = ontologyMapper.getMappedTerm(OntologyMapper.ResourceType.COLIMS, OntologyMapper.ResourceType.QUANTIFICATION_METHODS, "TMT");

        Assert.assertNotNull(mappedTerm);
        Assert.assertEquals("PRIDE:0000314", mappedTerm.getOboId());
    }

    @Test
    public void testGetNonExisitingMappedTerm() throws HttpClientErrorException, IOException {
        OntologyTerm mappedTerm = ontologyMapper.getMappedTerm(OntologyMapper.ResourceType.COLIMS, OntologyMapper.ResourceType.QUANTIFICATION_METHODS, "TMTTMT");

        Assert.assertNull(mappedTerm);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetMappedTermWithWrongArgument() throws HttpClientErrorException, IOException {
        OntologyTerm mappedTerm = ontologyMapper.getMappedTerm(OntologyMapper.ResourceType.COLIMS, OntologyMapper.ResourceType.MODIFICATIONS, "TMT");
    }

}

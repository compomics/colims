/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.util;

import java.io.IOException;
import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestClientException;

/**
 *
 * @author demet
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-simple-test-context.xml"})
public class AccessionConventerTest {
    
    @Autowired
    private AccessionConverter accessionConverter;
    
    @Test
    public void testGetUniProtByAccession() throws RestClientException, IOException{
        String accession = "A2AB72";
        String database = "CONTAMINANTS";
        List<String> uniProtAccessions = AccessionConverter.convertToUniProt(accession, database);
        assertThat(uniProtAccessions.get(0), is("B1ATJ5"));
        assertThat(uniProtAccessions.size(), is(1));

    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service;

import java.io.IOException;
import java.util.Map;
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
public class UniProtServiceTest {
    
    @Autowired
    private UniProtService uniProtService;
    
    @Test
    public void testGetUniProtByAccession() throws RestClientException, IOException{
        
        String accession = "P05787";
        
        Map<String, String> uniProt = uniProtService.getUniProtByAccession(accession);
        assertThat(uniProt.get("description"), is("Keratin, type II cytoskeletal 8"));
        assertThat(uniProt.get("species"), is("Homo sapiens"));
        assertThat(uniProt.get("taxid"), is("9606"));
    }
    
}

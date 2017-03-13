/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.util;

import com.compomics.colims.model.FastaDb;
import java.io.IOException;
import java.util.Map;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author demet
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-simple-test-context.xml"})
public class UniprotProteinUtilsTest {

    @Autowired
    private UniprotProteinUtils uniprotProteinUtils;
    
    @Test
    public void testGetFastaDbUniprotInformation() throws IOException{
        String accession = "P13746";
        FastaDb fastaDb = new FastaDb();
        fastaDb.setDatabaseName("SWISSPROT");
        
        Map<String, String> uniProtMap = uniprotProteinUtils.getFastaDbUniprotInformation(accession, fastaDb);
        
        // send the same accession and fasta to see if cache is working
        String accession1 = "P13746";
        FastaDb fastaDb1 = new FastaDb();
        fastaDb.setDatabaseName("SWISSPROT");
        
        Map<String, String> uniProtMap1 = uniprotProteinUtils.getFastaDbUniprotInformation(accession1, fastaDb1);
        
        assertThat(uniProtMap1.get("description"), is("HLA class I histocompatibility antigen, A-11 alpha chain"));
        assertThat(uniProtMap1.get("taxid"), is("9606"));
        assertThat(uniProtMap1.get("species"), is("Homo sapiens"));
    }
}

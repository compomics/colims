/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.util;

import com.compomics.colims.model.Protein;
import java.util.ArrayList;
import java.util.List;
import net.sf.ehcache.search.expression.LessThan;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Assert;
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
/**
 *
 * @author demet
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-simple-test-context.xml"})
public class ProteinCoverageTest {

    @Autowired
    private ProteinCoverage proteinCoverage;

    @Test
    public void testCalculateProteinCoverage() {
        String protein = "MATLKDQLIYNLLKEEQTPQNKITVVGVGAVGMACAISILMKDLADELALVDVIEDKLKG"
                + "EMMDLQHGSLFLRTPKIVSGKDYNVTANSKLVIITAGARQQEGESRLNLVQRNVNIFKFI"
                + "IPNVVKYSPNCKLLIVSNPVDILTYVAWKISGFPKNRVIGSGCNLDSARFRYLMGERLGV"
                + "HPLSCHGWVLGEHGDSSVPVWSGMNVAGVSLKTLHPDLGTDKDKEQWKEVHKQVVESAYE"
                + "VIKLKGYTSWAIGLSVADLAESIMKNLRRVHPVSTMIKGLYGIKDDVFLSVPCILGQNGI"
                + "SDLVKVTLTSEEEARLKKSADTLWGIQKELQF";
        
        String peptide1 = "ATLKDQLIYNLLK";
        
        String peptide2 = "LLIVSNPVDILTYVAWK";
        
        String peptide3 = "TLHPDLGTDKDKEQWK";
        
        List<String> peptides = new ArrayList<>();
        peptides.add(peptide1);
        peptides.add(peptide2);
        peptides.add(peptide3);
        
        double coverage = proteinCoverage.calculateProteinCoverage(protein, peptides);
        
        Assert.assertTrue(coverage > 0);
        Assert.assertTrue(coverage < 1);

    }
    
    @Test
    public void testCalculateProteinCoverageByOverlappingPeptides() {
        String protein = "ABCDEFGHIJKLMNOP";
               
        
        String peptide1 = "BCD";
        
        String peptide2 = "FGHIJK";
        
        String peptide3 = "HIJKLMNO";
        
        List<String> peptides = new ArrayList<>();
        peptides.add(peptide1);
        peptides.add(peptide2);
        peptides.add(peptide3); 
        
        double coverage = proteinCoverage.calculateProteinCoverage(protein, peptides);
        
        Assert.assertTrue(coverage > 0);
        Assert.assertTrue(coverage < 1);

    }
}

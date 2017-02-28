/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author demet
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-simple-test-context.xml"})
public class ProteinCoverageTest {


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

        double coverage = ProteinCoverage.calculateProteinCoverage(protein, peptides);

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

        double coverage = ProteinCoverage.calculateProteinCoverage(protein, peptides);

        Assert.assertTrue(coverage > 0);
        Assert.assertTrue(coverage < 1);

    }

    @Test
    public void testAminoAcidPrecedingPeptide() {
        String protein = "ABCDEFGHIJKLMNOP";

        String peptide = "ABCDEF";

        String aminoAcid = ProteinCoverage.findAminoAcidPrecedingPeptide(protein, peptide);

        Assert.assertTrue(aminoAcid.equals("-"));
    }

    @Test
    public void testAminoAcidFollowingPeptide() {
        String protein = "ABCDEFGHIJKLMNOP";

        String peptide = "FGHIJK";

        String aminoAcid = ProteinCoverage.findAminoAcidFollowingPeptide(protein, peptide);

        Assert.assertTrue(aminoAcid.equals("L"));
    }

    @Test
    public void testStartPositionOfPeptide() {
        String protein = "ABCDEFGHIJKLMNOP";

        String peptide = "ABCDEF";

        String aminoAcid = ProteinCoverage.findStartPositionOfPeptide(protein, peptide);

        Assert.assertTrue(aminoAcid.equals("1"));
    }

    @Test
    public void testEndPositionOfPeptide() {
        String protein = "ABCDEFGHIJKLMNOP";

        String peptide = "FGHIJK";

        String aminoAcid = ProteinCoverage.findEndPositionOfPeptide(protein, peptide);

        Assert.assertTrue(aminoAcid.equals("11"));
    }
}

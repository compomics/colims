package com.compomics.colims.client.model;

import com.compomics.colims.model.Modification;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.colims.repository.hibernate.PeptideDTO;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Testing the table row class.
 * <p/>
 * Created by Iain on 14/07/2015.
 */
public class PeptideDTOTest {

    private static PeptideDTO peptideDTO1;
    private static PeptideDTO peptideDTO2;
    private static PeptideDTO peptideDTO3;
    private static final List<PeptideDTO> PEPTIDE_DTO_LIST = new ArrayList<>();

    @BeforeClass
    public static void setup() {
        Modification modification1 = new Modification("testAccession1", "testName1");
        Modification modification2 = new Modification("testAccession2", "testName2");

        Peptide peptide1 = new Peptide();
        peptide1.setSequence("BREADBREADBREAD");
        peptide1.setCharge(2);

        PeptideHasModification peptideHasModification11 = new PeptideHasModification();
        peptideHasModification11.setLocation(5);
        peptideHasModification11.setModification(modification1);
        peptideHasModification11.setPeptide(peptide1);
        PeptideHasModification peptideHasModification12 = new PeptideHasModification();
        peptideHasModification12.setLocation(9);
        peptideHasModification12.setModification(modification2);
        peptideHasModification12.setPeptide(peptide1);

        peptide1.getPeptideHasModifications().add(peptideHasModification11);
        peptide1.getPeptideHasModifications().add(peptideHasModification12);

        peptideDTO1 = new PeptideDTO();
        peptideDTO1.setPeptide(peptide1);

        Peptide peptide2 = new Peptide();
        peptide2.setSequence("BREADBREADBREAD");

        peptideDTO2 = new PeptideDTO();
        peptideDTO2.setPeptide(peptide2);

        Peptide peptide3 = new Peptide();
        peptide3.setSequence("BREADBREADBREAD");
        peptide3.setCharge(3);

        PeptideHasModification peptideHasModification21 = new PeptideHasModification();
        peptideHasModification21.setLocation(5);
        peptideHasModification21.setModification(modification1);
        peptideHasModification21.setPeptide(peptide3);
        PeptideHasModification peptideHasModification22 = new PeptideHasModification();
        peptideHasModification22.setLocation(9);
        peptideHasModification22.setModification(modification2);
        peptideHasModification22.setPeptide(peptide1);

        peptide3.getPeptideHasModifications().add(peptideHasModification22);
        peptide3.getPeptideHasModifications().add(peptideHasModification21);

        peptideDTO3 = new PeptideDTO();
        peptideDTO3.setPeptide(peptide3);

        PEPTIDE_DTO_LIST.add(peptideDTO1);
    }

    /**
     * Test the addition of a PeptideDTO instance that holds a peptide with the same sequence but with different
     * modifications, the contains method should return false.
     */
    @Test
    public void testAddPeptideDTOMismatchToMap() {
        Assert.assertFalse(PEPTIDE_DTO_LIST.contains(peptideDTO2));
    }

    /**
     * Test the addition of a PeptideDTO instance that holds a peptide with only it's charge different from peptideDTO1,
     * the contains method should return true.
     */
    @Test
    public void testAddPeptideDTOMatchToMap() {
        Assert.assertTrue(PEPTIDE_DTO_LIST.contains(peptideDTO3));
    }

}
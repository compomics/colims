package com.compomics.colims.client.model;

import com.compomics.colims.model.Modification;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.colims.repository.hibernate.model.PeptideDTO;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Testing the table row class.
 * <p/>
 * Created by Iain on 14/07/2015.
 */
public class PeptideTableRowTest {

    private static PeptideDTO peptideDTO1;
    private static PeptideDTO peptideDTO2;
    private static PeptideDTO peptideDTO3;

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
    }

    /**
     * Trying to add a PeptideDTO instance that holds a peptide with the same sequence but with different modifications,
     * should throw an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddPeptideDTOMismatch() {
        PeptideTableRow peptideTableRow = new PeptideTableRow(peptideDTO1);
        peptideTableRow.addPeptideDTO(peptideDTO2);
    }

    /**
     * Test the addPeptideDTO method, peptideDTO3 should be added to the PeptideTableRow since only the charge is
     * different from peptideDTO1.
     */
    @Test
    public void testAddPeptideDTO() {
        PeptideTableRow peptideTableRow = new PeptideTableRow(peptideDTO1);
        peptideTableRow.addPeptideDTO(peptideDTO3);

        Assert.assertEquals(2, peptideTableRow.getSpectrumCount());
    }

    /**
     * Test the annotated sequence of a PeptideDTO instance that holds a peptide with a modification.
     */
    @Test
    public void testGetAnnotatedSequence() {
        PeptideTableRow peptideTableRow = new PeptideTableRow(peptideDTO1);

        String annotatedSequence = peptideTableRow.getAnnotatedSequence();

        assertThat(annotatedSequence.length(), is(peptideDTO1.getPeptide().getSequence().length() + 7));
        assertThat(annotatedSequence, containsString("<b>"));
        assertThat(annotatedSequence, is(peptideTableRow.getAnnotatedSequence()));
    }
}
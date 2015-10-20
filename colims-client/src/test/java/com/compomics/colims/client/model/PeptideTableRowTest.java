package com.compomics.colims.client.model;

import com.compomics.colims.model.Modification;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Testing the table row class
 *
 * Created by Iain on 14/07/2015.
 */
public class PeptideTableRowTest {

    @Test
    public void testGetAnnotatedSequence() throws Exception {
        Peptide peptideA = new Peptide();
        peptideA.setSequence("BREADBREADBREAD");

        Peptide peptideB = new Peptide();
        peptideB.setSequence("BREADBREADBREAD");

        Modification modification = new Modification();
        modification.setAccession("CAKECAKECAKE");

        List<PeptideHasModification> peptideHasModifications = new ArrayList<>();
        peptideHasModifications.add(new PeptideHasModification());
        peptideHasModifications.get(0).setLocation(5);
        peptideA.setPeptideHasModifications(peptideHasModifications);

        PeptideTableRow peptideTableRow = new PeptideTableRow(peptideA);
        peptideTableRow.addPeptideDTO(peptideB);
        peptideTableRow.getPeptideHasModifications().addAll(peptideA.getPeptideHasModifications());

        String annotatedSequence = peptideTableRow.getAnnotatedSequence();

        assertThat(annotatedSequence.length(), is(peptideA.getSequence().length() + 7));
        assertThat(annotatedSequence, containsString("<b>"));
        assertThat(annotatedSequence, is(peptideTableRow.getAnnotatedSequence()));
    }
}
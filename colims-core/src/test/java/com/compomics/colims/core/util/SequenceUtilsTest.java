
package com.compomics.colims.core.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Niels Hulstaert
 */
public class SequenceUtilsTest {

    private final String proteinSequence1 = "AAAAAAAAAABLENNARTMAAAAA";
    private final String proteinSequence2 = "LENNARTMAAAAAAAAAAAAAAAA";
    private final String proteinSequence3 = "AAAAAAAAAAAAAAAABLENNART";
    private final String proteinSequence4 = "AAAAAAAAAAAAAAAABLENNARTKAAAAAAAAAAAAAULENNARTVAAAAAA";
    private final String proteinSequence5 = "AAAAAAAAAAAAAAAABLENNARTKAAAATRANNELAAAULENNARTVAAAAAA";
    private final String peptideSequence = "LENNART";

    /**
     * The the throwing of an IllegalArgumentException in case a peptide sequence can not be found in a protein
     * sequence.
     */
    @Test(expected = IllegalStateException.class)
    public void testGetPeptidePositionsException() {
        String nonsencePeptideSequence = "IIJEFIJE";
        PeptidePosition peptidePosition = SequenceUtils.getPeptidePositions(proteinSequence1, nonsencePeptideSequence).get(0);
    }

    /**
     * Test the getPeptidePosition method for 4 protein sequence;
     * <pre>
     *     1. the peptide is not located at the beginning or the end of the protein
     *     2. the peptide is located at the beginning of the protein
     *     3. the peptide is located at the end of the protein
     *     4. the peptide occurs twice in the protein sequence
     * </pre>
     */
    @Test
    public void testGetPeptidePositions() {
        PeptidePosition peptidePosition1 = SequenceUtils.getPeptidePositions(proteinSequence1, peptideSequence).get(0);
        Assert.assertEquals(12, peptidePosition1.getStartPosition().intValue());
        Assert.assertEquals(18, peptidePosition1.getEndPosition().intValue());
        Assert.assertEquals(Character.valueOf('B'), peptidePosition1.getPreAA());
        Assert.assertEquals(Character.valueOf('M'), peptidePosition1.getPostAA());

        PeptidePosition peptidePosition2 = SequenceUtils.getPeptidePositions(proteinSequence2, peptideSequence).get(0);
        Assert.assertEquals(1, peptidePosition2.getStartPosition().intValue());
        Assert.assertEquals(peptideSequence.length(), peptidePosition2.getEndPosition().intValue());
        Assert.assertNull(peptidePosition2.getPreAA());
        Assert.assertEquals(Character.valueOf('M'), peptidePosition2.getPostAA());

        PeptidePosition peptidePosition3 = SequenceUtils.getPeptidePositions(proteinSequence3, peptideSequence).get(0);
        Assert.assertEquals(18, peptidePosition3.getStartPosition().intValue());
        Assert.assertEquals(proteinSequence3.length(), peptidePosition3.getEndPosition().intValue());
        Assert.assertEquals(Character.valueOf('B'), peptidePosition3.getPreAA());
        Assert.assertNull(peptidePosition3.getPostAA());

        //there should be 2 occurrences of the peptide in the protein sequence
        List<PeptidePosition> peptidePositions = SequenceUtils.getPeptidePositions(proteinSequence4, peptideSequence);
        Assert.assertEquals(2, peptidePositions.size());

        PeptidePosition peptidePosition41 = peptidePositions.get(0);
        Assert.assertEquals(18, peptidePosition41.getStartPosition().intValue());
        Assert.assertEquals(24, peptidePosition41.getEndPosition().intValue());
        Assert.assertEquals(Character.valueOf('B'), peptidePosition41.getPreAA());
        Assert.assertEquals(Character.valueOf('K'), peptidePosition41.getPostAA());

        PeptidePosition peptidePosition42 = peptidePositions.get(1);
        Assert.assertEquals(40, peptidePosition42.getStartPosition().intValue());
        Assert.assertEquals(46, peptidePosition42.getEndPosition().intValue());
        Assert.assertEquals(Character.valueOf('U'), peptidePosition42.getPreAA());
        Assert.assertEquals(Character.valueOf('V'), peptidePosition42.getPostAA());
    }

    @Test
    public void getPeptideStartIndexes() {
        List<Integer> peptideStartIndexes = SequenceUtils.getPeptideStartIndexes(proteinSequence4, peptideSequence);

        Assert.assertEquals(2, peptideStartIndexes.size());
        Assert.assertEquals(18, peptideStartIndexes.get(0).intValue());
        Assert.assertEquals(40, peptideStartIndexes.get(1).intValue());
    }

    @Test
    public void testGetSurroundingAAs() {
        Character[] surroundingAAs = SequenceUtils.getSurroundingAAs(proteinSequence1, peptideSequence, 12);

        Assert.assertEquals(Character.valueOf('B'), surroundingAAs[0]);
        Assert.assertEquals(Character.valueOf('M'), surroundingAAs[1]);

        surroundingAAs = SequenceUtils.getSurroundingAAs(proteinSequence2, peptideSequence, 1);

        Assert.assertNull(surroundingAAs[0]);
        Assert.assertEquals(Character.valueOf('M'), surroundingAAs[1]);

        surroundingAAs = SequenceUtils.getSurroundingAAs(proteinSequence3, peptideSequence, 18);

        Assert.assertEquals(Character.valueOf('B'), surroundingAAs[0]);
        Assert.assertNull(surroundingAAs[1]);
    }

    @Test
    public void testMapPeptideSequences() {
        Set<String> peptides = new HashSet<>();
        peptides.add(peptideSequence);

        TreeMap<Integer, Boolean> coverageAnnotations = SequenceUtils.mapPeptideSequences(proteinSequence1, peptides);
        Assert.assertEquals(2, coverageAnnotations.size());
        Assert.assertEquals(Boolean.TRUE, coverageAnnotations.get(11));
        Assert.assertEquals(Boolean.FALSE, coverageAnnotations.get(17));

        coverageAnnotations = SequenceUtils.mapPeptideSequences(proteinSequence2, peptides);
        Assert.assertEquals(2, coverageAnnotations.size());
        Assert.assertEquals(Boolean.TRUE, coverageAnnotations.get(0));
        Assert.assertEquals(Boolean.FALSE, coverageAnnotations.get(6));

        coverageAnnotations = SequenceUtils.mapPeptideSequences(proteinSequence3, peptides);
        Assert.assertEquals(2, coverageAnnotations.size());
        Assert.assertEquals(Boolean.TRUE, coverageAnnotations.get(17));
        Assert.assertEquals(Boolean.FALSE, coverageAnnotations.get(23));

        coverageAnnotations = SequenceUtils.mapPeptideSequences(proteinSequence4, peptides);
        Assert.assertEquals(4, coverageAnnotations.size());
        Assert.assertEquals(Boolean.TRUE, coverageAnnotations.get(17));
        Assert.assertEquals(Boolean.FALSE, coverageAnnotations.get(23));
        Assert.assertEquals(Boolean.TRUE, coverageAnnotations.get(39));
        Assert.assertEquals(Boolean.FALSE, coverageAnnotations.get(45));

        peptides.add("TRANNEL");
        peptides.add("ARTKAAA");
        coverageAnnotations = SequenceUtils.mapPeptideSequences(proteinSequence5, peptides);
        Assert.assertEquals(6, coverageAnnotations.size());
        Assert.assertEquals(Boolean.TRUE, coverageAnnotations.get(17));
        Assert.assertEquals(Boolean.FALSE, coverageAnnotations.get(27));
        Assert.assertEquals(Boolean.TRUE, coverageAnnotations.get(29));
        Assert.assertEquals(Boolean.FALSE, coverageAnnotations.get(35));
        Assert.assertEquals(Boolean.TRUE, coverageAnnotations.get(40));
        Assert.assertEquals(Boolean.FALSE, coverageAnnotations.get(46));

        peptides.add("ARTVAAA");
        coverageAnnotations = SequenceUtils.mapPeptideSequences(proteinSequence5, peptides);
        Assert.assertEquals(6, coverageAnnotations.size());
        Assert.assertEquals(Boolean.TRUE, coverageAnnotations.get(17));
        Assert.assertEquals(Boolean.FALSE, coverageAnnotations.get(27));
        Assert.assertEquals(Boolean.TRUE, coverageAnnotations.get(29));
        Assert.assertEquals(Boolean.FALSE, coverageAnnotations.get(35));
        Assert.assertEquals(Boolean.TRUE, coverageAnnotations.get(40));
        Assert.assertEquals(Boolean.FALSE, coverageAnnotations.get(50));
    }

    @Test
    public void testCalculateProteinCoverage() {
        Set<String> peptides = new HashSet<>();
        peptides.add(peptideSequence);

        double coverage = SequenceUtils.calculateProteinCoverage(proteinSequence1, peptides);
        Assert.assertEquals(0.2916, coverage, 0.001);

        coverage = SequenceUtils.calculateProteinCoverage(proteinSequence2, peptides);
        Assert.assertEquals(0.2916, coverage, 0.001);

        coverage = SequenceUtils.calculateProteinCoverage(proteinSequence3, peptides);
        Assert.assertEquals(0.2916, coverage, 0.001);

        coverage = SequenceUtils.calculateProteinCoverage(proteinSequence4, peptides);
        Assert.assertEquals(0.2641, coverage, 0.001);

        peptides.add("TRANNEL");
        peptides.add("ARTKAAA");
        coverage = SequenceUtils.calculateProteinCoverage(proteinSequence5, peptides);
        Assert.assertEquals(0.4629, coverage, 0.001);
    }
}

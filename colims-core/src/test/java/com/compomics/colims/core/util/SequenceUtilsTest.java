
package com.compomics.colims.core.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
public class SequenceUtilsTest {

    private String proteinSequence1 = "AAAAAAAAAABLENNARTMAAAAA";
    private String proteinSequence2 = "LENNARTMAAAAAAAAAAAAAAAA";
    private String proteinSequence3 = "AAAAAAAAAAAAAAAABLENNART";
    private String proteinSequence4 = "AAAAAAAAAAAAAAAABLENNARTKAAAAAAAAAAAAAULENNARTVAAAAAA";
    private String peptideSequence = "LENNART";
    private String nonsencePeptideSequence = "IIJEFIJE";

    /**
     * The the throwing of an IllegalArgumentException in case a peptide sequence can not be found in a protein
     * sequence.
     */
    @Test(expected = IllegalStateException.class)
    public void testGetPeptidePositionsException() {
        PeptidePosition peptidePosition = SequenceUtils.getPeptidePositions(proteinSequence1, nonsencePeptideSequence).get(0);
    }

    /**
     * Test the getPeptidePosition method for 4 protein sequence;
     * <pre>
     *     1. the peptide is not located at the beginning or the end of the protein
     *     2. the peptide is located at the beginning of the protein
     *     3. the peptide is located at the end of the protein
     *     4. the peptide occurs twice in the protein sequence
     * <pre/>
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

}

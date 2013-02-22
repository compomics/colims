package com.compomics.colims.core.io.parser.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.IteratorUtils;
import org.junit.Assert;
import org.junit.Test;

public class TabularFileLineValuesIteratorTest {
    File getFile(final String filename) {
        return new File(getClass().getClassLoader().getResource(filename).getFile());
    }

    @Test
    public void testTabularFileLineValuesIterator() throws IOException {
        // Create iterator for ELVI
        TabularFileLineValuesIterator elvi = new TabularFileLineValuesIterator(getFile("testdata/evidence_subset_10.tsv"));

        // Iterate over ELVI and assign values to a list for further inspection
        @SuppressWarnings("unchecked")
        List<Map<String, String>> list = IteratorUtils.toList(elvi);

        // Check properties of first and last item in list
        Map<String, String> first = list.get(0);
        Assert.assertEquals("0", first.get(EvidenceHeaders.id.column));
        Assert.assertEquals("0.83092", first.get(EvidenceHeaders.Retention_Length.column));
        Assert.assertEquals("20242", first.get(EvidenceHeaders.Resolution.column));
        Assert.assertEquals("pool3C", first.get(EvidenceHeaders.Experiment.column));
        Assert.assertEquals("", first.get(EvidenceHeaders.Oxidation_M_Site_IDs.column));
        Assert.assertEquals("0", first.get(EvidenceHeaders.PIF.column));
        Assert.assertEquals("751", first.get(EvidenceHeaders.Protein_Group_IDs.column));

        Map<String, String> last = list.get(list.size() - 1);
        Assert.assertEquals("8", last.get(EvidenceHeaders.id.column));
        Assert.assertEquals("0.9267", last.get(EvidenceHeaders.Retention_Length.column));
        Assert.assertEquals("21095.4", last.get(EvidenceHeaders.Resolution.column));
        Assert.assertEquals("pool4A", last.get(EvidenceHeaders.Experiment.column));
        Assert.assertEquals("", last.get(EvidenceHeaders.Oxidation_M_Site_IDs.column));
        Assert.assertEquals("0", last.get(EvidenceHeaders.PIF.column));
        Assert.assertEquals("1110", last.get(EvidenceHeaders.Protein_Group_IDs.column));
    }
}

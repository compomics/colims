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
    public void testTabularFileLineValuesIterator() throws IOException, HeaderEnumNotInitialisedException, UnparseableException {
        // Create iterator for ELVI
        TabularFileLineValuesIterator elvi = new TabularFileLineValuesIterator(getFile("testdata/evidence_subset_10.tsv"),EvidenceHeaders.values());

        // Iterate over ELVI and assign values to a list for further inspection
        @SuppressWarnings("unchecked")
        List<Map<String, String>> list = IteratorUtils.toList(elvi);

        // Check properties of first and last item in list
        Map<String, String> first = list.get(0);
        Assert.assertEquals("0", first.get(EvidenceHeaders.id.getColumnName()));
        Assert.assertEquals("0.83092", first.get(EvidenceHeaders.Retention_Length.getColumnName()));
        Assert.assertEquals("20242", first.get(EvidenceHeaders.Resolution.getColumnName()));
        Assert.assertEquals("pool3C", first.get(EvidenceHeaders.Experiment.getColumnName()));
        Assert.assertEquals("", first.get(EvidenceHeaders.Oxidation_M_Site_IDs.getColumnName()));
        Assert.assertEquals("0", first.get(EvidenceHeaders.PIF.getColumnName()));
        Assert.assertEquals("751", first.get(EvidenceHeaders.Protein_Group_IDs.getColumnName()));

        Map<String, String> last = list.get(list.size() - 1);
        Assert.assertEquals("8", last.get(EvidenceHeaders.id.getColumnName()));
        Assert.assertEquals("0.9267", last.get(EvidenceHeaders.Retention_Length.getColumnName()));
        Assert.assertEquals("21095.4", last.get(EvidenceHeaders.Resolution.getColumnName()));
        Assert.assertEquals("pool4A", last.get(EvidenceHeaders.Experiment.getColumnName()));
        Assert.assertEquals("", last.get(EvidenceHeaders.Oxidation_M_Site_IDs.getColumnName()));
        Assert.assertEquals("0", last.get(EvidenceHeaders.PIF.getColumnName()));
        Assert.assertEquals("1110", last.get(EvidenceHeaders.Protein_Group_IDs.getColumnName()));
    }

    @Test
    public void testGetHeaders() throws IOException {
        TabularFileLineValuesIterator tflvi = new TabularFileLineValuesIterator(getFile("testdata/msms_subset_1000.tsv"));

        String[] headers = tflvi.getHeaders();
        for (String header : headers) {
            String safe = header.replaceAll(" |\\[|\\]|/|\\(|\\)|-|\\.", "_");
            safe = safe.replace("__", "_").replace("__", "_");
            safe = safe.replaceFirst("_$", "");
            // Print headers safe for inclusion in an Enum
            // System.out.println(String.format("%s(\"%s\"),", safe, header));
        }

        Assert.assertEquals("Protein Group IDs", headers[1]);
        Assert.assertEquals("Neutral loss level", headers[53]);
    }
}

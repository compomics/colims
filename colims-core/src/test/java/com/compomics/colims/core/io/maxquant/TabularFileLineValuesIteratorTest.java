package com.compomics.colims.core.io.maxquant;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.compomics.colims.core.io.maxquant.headers.HeaderEnumNotInitialisedException;
import com.compomics.colims.core.io.maxquant.headers.MaxQuantEvidenceHeaders;
import org.apache.commons.collections.IteratorUtils;
import org.junit.Assert;
import org.junit.Test;

public class TabularFileLineValuesIteratorTest {

    @Test
    public void testTabularFileLineValuesIterator() throws IOException, HeaderEnumNotInitialisedException, UnparseableException {
        // Create iterator for ELVI
        TabularFileLineValuesIterator elvi = new TabularFileLineValuesIterator(new File(MaxQuantTestSuite.maxQuantTextFolder, "evidence_subset_10.tsv"), MaxQuantEvidenceHeaders.values());

        // Iterate over ELVI and assign values to a list for further inspection
        @SuppressWarnings("unchecked")
        List<Map<String, String>> list = IteratorUtils.toList(elvi);

        // Check properties of first and last item in list
        Map<String, String> first = list.get(0);
        Assert.assertEquals("0", first.get(MaxQuantEvidenceHeaders.ID.getColumnName()));
        Assert.assertEquals("0.83092", first.get(MaxQuantEvidenceHeaders.RETENTION_LENGTH.getColumnName()));
        Assert.assertEquals("20242", first.get(MaxQuantEvidenceHeaders.RESOLUTION.getColumnName()));
        Assert.assertEquals("pool3C", first.get(MaxQuantEvidenceHeaders.EXPERIMENT.getColumnName()));
        Assert.assertEquals("", first.get(MaxQuantEvidenceHeaders.OXIDATION_M_SITE_IDS.getColumnName()));
        Assert.assertEquals("0", first.get(MaxQuantEvidenceHeaders.PIF.getColumnName()));
        Assert.assertEquals("751", first.get(MaxQuantEvidenceHeaders.PROTEIN_GROUP_IDS.getColumnName()));

        Map<String, String> last = list.get(list.size() - 1);
        Assert.assertEquals("8", last.get(MaxQuantEvidenceHeaders.ID.getColumnName()));
        Assert.assertEquals("0.9267", last.get(MaxQuantEvidenceHeaders.RETENTION_LENGTH.getColumnName()));
        Assert.assertEquals("21095.4", last.get(MaxQuantEvidenceHeaders.RESOLUTION.getColumnName()));
        Assert.assertEquals("pool4A", last.get(MaxQuantEvidenceHeaders.EXPERIMENT.getColumnName()));
        Assert.assertEquals("", last.get(MaxQuantEvidenceHeaders.OXIDATION_M_SITE_IDS.getColumnName()));
        Assert.assertEquals("0", last.get(MaxQuantEvidenceHeaders.PIF.getColumnName()));
        Assert.assertEquals("1110", last.get(MaxQuantEvidenceHeaders.PROTEIN_GROUP_IDS.getColumnName()));
    }

    @Test
    public void testGetHeaders() throws IOException {
        TabularFileLineValuesIterator tflvi = new TabularFileLineValuesIterator(new File(MaxQuantTestSuite.maxQuantTextFolder, "msms_subset_1000.tsv"));

        String[] headers = tflvi.getHeaders();
        for (String header : headers) {
            String safe = header.replaceAll(" |\\[|\\]|/|\\(|\\)|-|\\.", "_");
            safe = safe.replace("__", "_").replace("__", "_");
            safe = safe.replaceFirst("_$", "");
            // Print headers safe for inclusion in an Enum
            // System.out.println(String.format("%s(\"%s\"),", safe, header));
        }

        Assert.assertEquals("protein group ids", headers[1]);
        Assert.assertEquals("neutral loss level", headers[53]);
    }
}

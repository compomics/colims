package com.compomics.colims.core.io.maxquant;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import com.compomics.colims.core.io.maxquant.headers.HeaderEnum;
import com.compomics.colims.core.io.maxquant.headers.HeaderEnumNotInitialisedException;
import com.compomics.colims.core.io.maxquant.headers.MaxQuantEvidenceHeaders;
import org.apache.commons.collections.IteratorUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
public class TabularFileLineValuesIteratorTest {

    @Test
    public void testTabularFileLineValuesIterator() throws IOException, HeaderEnumNotInitialisedException, UnparseableException {
        // TODO: REPLACE WITH create iterator then take first entry and check that it has a value for every enum header

        TabularFileLineValuesIterator iterator = new TabularFileLineValuesIterator(MaxQuantTestSuite.evidenceFile, MaxQuantEvidenceHeaders.values());

        Map<String, String> item = iterator.next();

        for (HeaderEnum header : MaxQuantEvidenceHeaders.values()) {
            assertThat(item.get(header.getColumnName()), not(null));
        }

        // Iterate over ELVI and assign values to a list for further inspection
        //@SuppressWarnings("unchecked")
//        List<Map<String, String>> list = IteratorUtils.toList(iterator);
//
//        // Check properties of first and last item in list
//        Map<String, String> first = list.get(0);
//        Assert.assertEquals("0", first.get(MaxQuantEvidenceHeaders.ID.getColumnName()));
//        Assert.assertEquals("0.83092", first.get(MaxQuantEvidenceHeaders.RETENTION_LENGTH.getColumnName()));
//        Assert.assertEquals("20242", first.get(MaxQuantEvidenceHeaders.RESOLUTION.getColumnName()));
//        Assert.assertEquals("pool3C", first.get(MaxQuantEvidenceHeaders.EXPERIMENT.getColumnName()));
//        Assert.assertEquals("", first.get(MaxQuantEvidenceHeaders.OXIDATION_M_SITE_IDS.getColumnName()));
//        Assert.assertEquals("0", first.get(MaxQuantEvidenceHeaders.PIF.getColumnName()));
//        Assert.assertEquals("751", first.get(MaxQuantEvidenceHeaders.PROTEIN_GROUP_IDS.getColumnName()));
//
//        Map<String, String> last = list.get(list.size() - 1);
//        Assert.assertEquals("8", last.get(MaxQuantEvidenceHeaders.ID.getColumnName()));
//        Assert.assertEquals("0.9267", last.get(MaxQuantEvidenceHeaders.RETENTION_LENGTH.getColumnName()));
//        Assert.assertEquals("21095.4", last.get(MaxQuantEvidenceHeaders.RESOLUTION.getColumnName()));
//        Assert.assertEquals("pool4A", last.get(MaxQuantEvidenceHeaders.EXPERIMENT.getColumnName()));
//        Assert.assertEquals("", last.get(MaxQuantEvidenceHeaders.OXIDATION_M_SITE_IDS.getColumnName()));
//        Assert.assertEquals("0", last.get(MaxQuantEvidenceHeaders.PIF.getColumnName()));
//        Assert.assertEquals("1110", last.get(MaxQuantEvidenceHeaders.PROTEIN_GROUP_IDS.getColumnName()));
    }

    @Test
    public void testGetHeaders() throws IOException {
        TabularFileLineValuesIterator iterator = new TabularFileLineValuesIterator(MaxQuantTestSuite.msmsFile);
        List<String> rawFile = Files.readAllLines(MaxQuantTestSuite.msmsFile.toPath());

        String[] headers = iterator.getHeaders();

        assertThat(rawFile.get(0).toLowerCase(), startsWith(headers[0]));
    }

    @Test(expected = IOException.class)
    public void testEmptyFile() throws IOException {
        TabularFileLineValuesIterator iterator = new TabularFileLineValuesIterator(new ClassPathResource("data/empty.tsv").getFile());
    }

    @Test
    public void testHasNext() throws IOException {
        TabularFileLineValuesIterator iterator = new TabularFileLineValuesIterator(new ClassPathResource("data/maxquant/evidence_subset_10.tsv").getFile());

        for (int i = 0; i < 9; ++i) {
            iterator.next();
        }

        assertFalse(iterator.hasNext());
    }
}

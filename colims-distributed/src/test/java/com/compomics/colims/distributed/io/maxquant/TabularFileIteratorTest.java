package com.compomics.colims.distributed.io.maxquant;

import com.compomics.colims.distributed.io.maxquant.headers.EvidenceHeaders;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertFalse;

public class TabularFileIteratorTest {

    EvidenceHeaders evidenceHeaders;

    public TabularFileIteratorTest() throws IOException {
        evidenceHeaders = new EvidenceHeaders();
    }

    @Test
    public void testTabularFileIterator() throws IOException {
        File evidenceSubsetFile = new ClassPathResource("data/maxquant/evidence_unit_test_1.txt").getFile();
        TabularFileIterator iterator = new TabularFileIterator(evidenceSubsetFile.toPath(), evidenceHeaders.getMandatoryHeaders());

        Assert.assertTrue(iterator.hasNext());
        Map<String, String> values = iterator.next();
        Assert.assertNotNull(values);
        Assert.assertEquals(59, values.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidFile() throws IOException {
        File emptyTsvFile = new ClassPathResource("data/maxquant/evidence_unit_test_2.txt").getFile();
        TabularFileIterator iterator = new TabularFileIterator(emptyTsvFile.toPath(), evidenceHeaders.getMandatoryHeaders());
    }

    @Test(expected = IOException.class)
    public void testEmptyFile() throws IOException {
        File emptyTsvFile = new ClassPathResource("data/maxquant/empty.txt").getFile();
        TabularFileIterator iterator = new TabularFileIterator(emptyTsvFile.toPath(), evidenceHeaders.getMandatoryHeaders());
    }

    @Test
    public void testHasNext() throws IOException {
        File evidenceSubsetFile = new ClassPathResource("data/maxquant/evidence_subset_10.txt").getFile();
        TabularFileIterator iterator = new TabularFileIterator(evidenceSubsetFile.toPath(), evidenceHeaders.getMandatoryHeaders());

        for (int i = 0; i < 9; ++i) {
            iterator.next();
        }

        assertFalse(iterator.hasNext());
    }

}

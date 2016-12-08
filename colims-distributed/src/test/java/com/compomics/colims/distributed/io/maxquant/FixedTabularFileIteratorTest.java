package com.compomics.colims.distributed.io.maxquant;

import com.compomics.colims.distributed.io.maxquant.headers.SummaryHeaders;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class FixedTabularFileIteratorTest {

    SummaryHeaders summaryHeaders;

    public FixedTabularFileIteratorTest() throws IOException {
        summaryHeaders = new SummaryHeaders();
    }

    @Test
    public void testFixedTabularFileIterator() throws IOException, UnparseableException {
        File summaryFile = new ClassPathResource("data/maxquant/summary_unit_test_1.txt").getFile();
        FixedTabularFileIterator iterator = new FixedTabularFileIterator(summaryFile.toPath(), summaryHeaders);

        Assert.assertTrue(iterator.hasNext());
        Map<String, String> values = iterator.next();
        Assert.assertNotNull(values);
        Assert.assertEquals(1, values.size());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidFile() throws IOException {
        File emptyTsvFile = new ClassPathResource("data/maxquant/summary_unit_test_2.txt").getFile();
        FixedTabularFileIterator iterator = new FixedTabularFileIterator(emptyTsvFile.toPath(), summaryHeaders);
    }

}

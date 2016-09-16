package com.compomics.colims.distributed.io.maxquant;

import com.compomics.colims.distributed.io.maxquant.headers.EvidenceHeaders;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertFalse;

public class TabularFileIteratorTest {

    EvidenceHeaders evidenceHeaders;

    public TabularFileIteratorTest() throws IOException {
        evidenceHeaders = new EvidenceHeaders();
    }

    @Test
    public void testTabularFileLineValuesIterator() throws IOException, UnparseableException {
        TabularFileIterator iterator = new TabularFileIterator(MaxQuantTestSuite.evidenceFile, evidenceHeaders.getMandatoryHeaders());

        System.out.println("------------");
    }

    @Test(expected = IOException.class)
    public void testEmptyFile() throws IOException {
        File emptyTsvFile = new ClassPathResource("data/maxquant/empty.tsv").getFile();
        TabularFileIterator iterator = new TabularFileIterator(emptyTsvFile.toPath(), evidenceHeaders.getMandatoryHeaders());
    }

    @Test
    public void testHasNext() throws IOException {
        File evidenceSubsetFile = new ClassPathResource("data/maxquant/evidence_subset_10.tsv").getFile();
        TabularFileIterator iterator = new TabularFileIterator(evidenceSubsetFile.toPath(), evidenceHeaders.getMandatoryHeaders());

        for (int i = 0; i < 9; ++i) {
            iterator.next();
        }

        assertFalse(iterator.hasNext());
    }

}

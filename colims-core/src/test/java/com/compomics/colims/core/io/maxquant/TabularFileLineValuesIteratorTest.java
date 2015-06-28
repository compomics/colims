package com.compomics.colims.core.io.maxquant;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import com.compomics.colims.core.io.maxquant.headers.HeaderEnum;
import com.compomics.colims.core.io.maxquant.headers.HeaderEnumNotInitialisedException;
import com.compomics.colims.core.io.maxquant.headers.MaxQuantEvidenceHeaders;
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
        TabularFileLineValuesIterator iterator = new TabularFileLineValuesIterator(MaxQuantTestSuite.evidenceFile, new HeaderEnum[]{
                MaxQuantEvidenceHeaders.ID,
                MaxQuantEvidenceHeaders.SEQUENCE
        });

        List<String> rawFile = Files.readAllLines(MaxQuantTestSuite.evidenceFile.toPath());

        Map<String, String> item = iterator.next();

        assertThat(item.get(MaxQuantEvidenceHeaders.ID.getDefaultColumnName()), notNullValue());
        assertFalse(item.get(MaxQuantEvidenceHeaders.ID.getDefaultColumnName()).isEmpty());
        assertThat(rawFile.get(1), startsWith(item.get(MaxQuantEvidenceHeaders.SEQUENCE.getDefaultColumnName())));
        assertTrue(rawFile.get(0).split("\t").length <= MaxQuantEvidenceHeaders.values().length);
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

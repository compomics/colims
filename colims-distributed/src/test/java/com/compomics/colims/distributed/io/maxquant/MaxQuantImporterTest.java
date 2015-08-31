package com.compomics.colims.distributed.io.maxquant;

import com.compomics.colims.core.io.MaxQuantImport;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.FastaDb;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Davy
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})

public class MaxQuantImporterTest {

    @Autowired
    MaxQuantImporter maxQuantImporter;

    /**
     * Test of map method, of class MaxQuantImporter.
     */
    @Test
    public void testMap() throws Exception {
        FastaDb maxQuantTestFastaDb = new FastaDb();
        maxQuantTestFastaDb.setName("uniprot-taxonomy%3A10090.fasta");
        maxQuantTestFastaDb.setFileName("uniprot-taxonomy%3A10090.fasta");
        maxQuantTestFastaDb.setFilePath(MaxQuantTestSuite.fastaFile.getAbsolutePath());

        MaxQuantImport maxQuantImport = new MaxQuantImport(MaxQuantTestSuite.maxQuantTextFolder, maxQuantTestFastaDb);
        List<AnalyticalRun> result = maxQuantImporter.importData(maxQuantImport);
        assertThat(result.size(), is(not(0)));
    }
}

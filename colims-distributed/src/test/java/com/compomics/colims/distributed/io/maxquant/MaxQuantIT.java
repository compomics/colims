package com.compomics.colims.distributed.io.maxquant;

import com.compomics.colims.core.io.MaxQuantImport;
import com.compomics.colims.model.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

/**
 * MaxQuant integration test
 *
 * @author Iain
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})
public class MaxQuantIT {

    private static final String maxQuantVersion = "1.5.2.8";
    private static final String fastaFilePath = "data/maxquant_" + maxQuantVersion + "/uniprot-taxonomy%3A10090.fasta";
    private static final String maxQuantTextFolderPath = "data/maxquant_" + maxQuantVersion;

    @Autowired
    MaxQuantImporter maxQuantImporter;

    /**
     * Test of map method, of class MaxQuantImporter.
     */
    @Test
    public void testMap() throws Exception {
        FastaDb maxQuantTestFastaDb = new FastaDb();
        ClassPathResource fastaResource = new ClassPathResource(fastaFilePath);
        maxQuantTestFastaDb.setName(fastaResource.getFilename());
        maxQuantTestFastaDb.setFileName(fastaResource.getFilename());
        maxQuantTestFastaDb.setFilePath(fastaResource.getFile().getPath());

        MaxQuantImport maxQuantImport = new MaxQuantImport(new ClassPathResource(maxQuantTextFolderPath).getFile(), maxQuantTestFastaDb);
        List<AnalyticalRun> result = maxQuantImporter.importData(maxQuantImport);

        assertThat(result.size(), is(1));
        assertThat(result.get(0).getSpectrums().size(), greaterThan(0));
        assertThat(result.get(0).getSearchAndValidationSettings().getFastaDb(), is(maxQuantTestFastaDb));
        assertThat(result.get(0).getQuantificationSettings(), notNullValue());
        // TODO: more assertions
    }
}

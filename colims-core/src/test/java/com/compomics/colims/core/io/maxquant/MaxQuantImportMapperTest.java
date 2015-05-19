package com.compomics.colims.core.io.maxquant;

import com.compomics.colims.core.io.MappingException;
import java.io.IOException;
import java.sql.SQLException;

import com.compomics.colims.core.io.maxquant.headers.HeaderEnumNotInitialisedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import java.util.List;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.FastaDb;
import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.springframework.core.io.ClassPathResource;

/**
 *
 * @author Davy
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})

public class MaxQuantImportMapperTest {

    @Autowired
    MaxQuantImporter maxQuantImporter;

    /**
     * Test of map method, of class MaxQuantImporter.
     * @throws java.io.IOException
     * @throws com.compomics.colims.core.io.maxquant.UnparseableException
     * @throws com.compomics.colims.core.io.maxquant.headers.HeaderEnumNotInitialisedException
     * @throws com.compomics.colims.core.io.MappingException
     * @throws java.sql.SQLException
     * @throws java.io.FileNotFoundException
     * @throws java.lang.ClassNotFoundException
     */
    @Test
    public void testMap() throws IOException, UnparseableException, HeaderEnumNotInitialisedException, MappingException, SQLException, ClassNotFoundException {
        FastaDb maxQuantTestFastaDb = new FastaDb();
        maxQuantTestFastaDb.setName(MaxQuantTestSuite.fastaFile.getName());
        maxQuantTestFastaDb.setFileName(MaxQuantTestSuite.fastaFile.getName());
        maxQuantTestFastaDb.setFilePath(MaxQuantTestSuite.fastaFile.getAbsolutePath());

        System.out.println("map");
        MaxQuantImport maxQuantImport = new MaxQuantImport(MaxQuantTestSuite.maxQuantTextFolder, maxQuantTestFastaDb);
        List<AnalyticalRun> result = maxQuantImporter.importData(maxQuantImport);
        assertThat(result.size(), is(not(0)));
    }
}

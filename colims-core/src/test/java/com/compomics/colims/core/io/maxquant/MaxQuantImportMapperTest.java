package com.compomics.colims.core.io.maxquant;

import com.compomics.colims.core.io.MappingException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

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
    MaxQuantImportMapper maxQuantImportMapper;

    private File maxQuantTextDirectory;
    private FastaDb maxQuantTestFastaDb;

    public MaxQuantImportMapperTest() throws IOException {
        maxQuantTextDirectory = new ClassPathResource("data/maxquant").getFile();
        
        File fastaFile = new ClassPathResource("data/maxquant/testfasta.fasta").getFile();
        maxQuantTestFastaDb = new FastaDb();
        maxQuantTestFastaDb.setName(fastaFile.getName());
        maxQuantTestFastaDb.setFileName(fastaFile.getName());
        maxQuantTestFastaDb.setFilePath(fastaFile.getAbsolutePath());                
    }

    /**
     * Test of map method, of class MaxQuantImportMapper.
     */
    @Test
    public void testMap() throws IOException, UnparseableException, HeaderEnumNotInitialisedException, MappingException, SQLException, FileNotFoundException, ClassNotFoundException {
        System.out.println("map");
        MaxQuantDataImport testImport = new MaxQuantDataImport(maxQuantTextDirectory, maxQuantTestFastaDb);
        List<AnalyticalRun> result = maxQuantImportMapper.map(testImport);
        assertThat(result.size(), is(not(0)));
    }
}

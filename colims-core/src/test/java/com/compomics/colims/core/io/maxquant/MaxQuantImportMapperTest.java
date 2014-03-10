package com.compomics.colims.core.io.maxquant;

import com.compomics.colims.core.io.MappingException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import java.io.File;
import java.util.List;
import com.compomics.colims.model.AnalyticalRun;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 *
 * @author Davy
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})

public class MaxQuantImportMapperTest {

    @Autowired
    MaxQuantImportMapper maxQuantImportMapper;

    private File maxQuantTextFolder;
    private Resource maxQuantTestFastaFile;

    public MaxQuantImportMapperTest() throws IOException {
        maxQuantTextFolder = new ClassPathResource("testdata").getFile();
        maxQuantTestFastaFile = new ClassPathResource("testdata/testfasta.fasta");
    }

    /**
     * Test of map method, of class MaxQuantImportMapper.
     */
    @Test
    public void testMap() throws IOException, UnparseableException, HeaderEnumNotInitialisedException, MappingException, SQLException, FileNotFoundException, ClassNotFoundException {
        System.out.println("map");
        MaxQuantDataImport testImport = new MaxQuantDataImport(maxQuantTextFolder, maxQuantTestFastaFile);
        List<AnalyticalRun> result = maxQuantImportMapper.map(testImport);
        assertThat(result.size(), is(not(0)));
    }
}

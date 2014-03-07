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

/**
 *
 * @author Davy
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})

public class MaxQuantImportMapperTest {

    @Autowired
    MaxQuantImportMapper maxQuantImportMapper;

    File maxQuantTextFolder;
    File maxQuantTestFastaFile;

    public MaxQuantImportMapperTest() {
        maxQuantTextFolder = new File(getClass().getClassLoader().getResource("data/maxquant").getPath());
        maxQuantTestFastaFile = new File(getClass().getClassLoader().getResource("data/maxquant/testfasta.fasta").getPath());
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

package com.compomics.colims.distributed.io.maxquant;

import com.compomics.colims.core.io.MaxQuantImport;
import com.compomics.colims.core.service.FastaDbService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.User;
import com.compomics.colims.repository.AuthenticationBean;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

/**
 * MaxQuant integration test.
 *
 * @author Iain
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})
@Transactional
@Rollback
public class MaxQuantIT {

    private static final String maxQuantVersion = "1.5.2.8";
    private static final String fastaFilePath = "data/maxquant_" + maxQuantVersion + "/uniprot-taxonomy%3A10090.fasta";
    private static final String maxQuantTextFolderPath = "data/maxquant_" + maxQuantVersion;

    @Autowired
    private FastaDbService fastaDbService;
    @Autowired
    private UserService userService;
    @Autowired
    MaxQuantImporter maxQuantImporter;
    @Autowired
    private AuthenticationBean authenticationBean;

    @Before
    public void setup() throws IOException, XmlPullParserException {
        //set admin user in authentication bean
        User adminUser = userService.findByName("admin");
        authenticationBean.setCurrentUser(adminUser);
    }

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

        //save the fasta db. We don't have it as an insert statement in the import.sql file
        //as the file path might be different depending on the OS
        fastaDbService.save(maxQuantTestFastaDb);

        MaxQuantImport maxQuantImport = new MaxQuantImport(new ClassPathResource(maxQuantTextFolderPath).getFile(), maxQuantTestFastaDb.getId());
        List<AnalyticalRun> result = maxQuantImporter.importData(maxQuantImport);

        assertThat(result.size(), is(1));
        assertThat(result.get(0).getSpectrums().size(), greaterThan(0));
        assertThat(result.get(0).getSearchAndValidationSettings().getFastaDb(), is(maxQuantTestFastaDb));
        assertThat(result.get(0).getQuantificationSettings(), notNullValue());
        // TODO: more assertions
    }
}

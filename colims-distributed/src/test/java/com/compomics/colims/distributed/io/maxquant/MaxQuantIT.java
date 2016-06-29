package com.compomics.colims.distributed.io.maxquant;

import com.compomics.colims.core.io.MappedData;
import com.compomics.colims.core.io.MaxQuantImport;
import com.compomics.colims.core.service.FastaDbService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.User;
import com.compomics.colims.model.UserBean;
import com.compomics.colims.model.enums.FastaDbType;
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
import java.util.EnumMap;
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

    @Autowired
    private FastaDbService fastaDbService;
    @Autowired
    private UserService userService;
    @Autowired
    MaxQuantMapper maxQuantImporter;
    @Autowired
    private UserBean userBean;

    @Before
    public void setup() throws IOException, XmlPullParserException {
        //set admin user in authentication bean
        User adminUser = userService.findByName("admin");
        userBean.setCurrentUser(adminUser);
    }

    /**
     * Test of map method, of class MaxQuantImporter.
     * @throws java.lang.Exception in case of an exception
     */
    @Test
    public void testMap() throws Exception {
        //persist the fasta db. We don't have it as an insert statement in the import.sql file
        //as the file path might be different depending on the OS
        fastaDbService.persist(MaxQuantTestSuite.testFastaDb);

        EnumMap<FastaDbType, Long> fastaDbIds = new EnumMap<>(FastaDbType.class);
        fastaDbIds.put(FastaDbType.PRIMARY, MaxQuantTestSuite.testFastaDb.getId());

        MaxQuantImport maxQuantImport = new MaxQuantImport(MaxQuantTestSuite.maxQuantDirectory, MaxQuantTestSuite.maxQuantCombinedDirectory, fastaDbIds);
        MappedData mappedData = maxQuantImporter.mapData(maxQuantImport);
        List<AnalyticalRun> analyticalRuns = mappedData.getAnalyticalRuns();

        assertThat(analyticalRuns.size(), is(6));
        assertThat(analyticalRuns.get(0).getSpectrums().size(), greaterThan(0));
        assertThat(analyticalRuns.get(0).getSearchAndValidationSettings().getSearchSettingsHasFastaDbs().get(0).getFastaDb(), is(MaxQuantTestSuite.testFastaDb));
   //     assertThat(analyticalRuns.get(0).getQuantificationSettings(), notNullValue());
        assertThat(analyticalRuns.get(0).getSpectrums().get(0).getPeptides().size(), greaterThan(0));
        // TODO: 6/6/2016 more test.. 

    }
}

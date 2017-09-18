package com.compomics.colims.distributed.io.maxquant;

import com.compomics.colims.core.io.MappedData;
import com.compomics.colims.core.io.MaxQuantImport;
import com.compomics.colims.core.service.FastaDbService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.User;
import com.compomics.colims.model.UserBean;
import com.compomics.colims.model.enums.FastaDbType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

/**
 * MaxQuant integration test. This class tests the main MaxQuant parsing classes {@link MaxQuantMapper} and {@link
 * com.compomics.colims.distributed.io.maxquant.parsers.MaxQuantParser}.
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
    private MaxQuantMapper maxQuantMapper;
    @Autowired
    private UserBean userBean;

    @Before
    public void setup() throws IOException {
        //set admin user in authentication bean
        User adminUser = userService.findByName("admin");
        userBean.setCurrentUser(adminUser);
    }

    /**
     * Test of the MaxQuant main mapper method.
     *
     * @throws java.lang.Exception in case of an exception
     */
    @Test
    public void testMap() throws Exception {
        //save the test FASTA to the database first
        fastaDbService.persist(MaxQuantTestSuite.spHumanFastaDb);
        //save the contaminants FASTA to the database first
        fastaDbService.persist(MaxQuantTestSuite.contaminantsFastaDb);

        EnumMap<FastaDbType, List<Long>> fastaDbIds = new EnumMap<>(FastaDbType.class);
        fastaDbIds.put(FastaDbType.PRIMARY, new ArrayList<>(Arrays.asList(MaxQuantTestSuite.spHumanFastaDb.getId())));
        fastaDbIds.put(FastaDbType.CONTAMINANTS, new ArrayList<>(Arrays.asList(MaxQuantTestSuite.contaminantsFastaDb.getId())));

        MaxQuantImport maxQuantImport = new MaxQuantImport(MaxQuantTestSuite.mqparFile,
                MaxQuantTestSuite.maxQuantCombinedDirectory, MaxQuantTestSuite.maxQuantCombinedDirectory, fastaDbIds, false, false, new ArrayList<>(), MaxQuantImport.SILAC);
        maxQuantMapper.clear();

        Path experimentsDirectory = new ClassPathResource("data/maxquant/maxquant_SILAC_integration").getFile().toPath();
        Path fastasDirectory = new ClassPathResource("data/maxquant/fasta").getFile().toPath();
        MappedData mappedData = maxQuantMapper.mapData(maxQuantImport, experimentsDirectory, fastasDirectory);
        List<AnalyticalRun> analyticalRuns = mappedData.getAnalyticalRuns();

        Assert.assertFalse(analyticalRuns.isEmpty());
    }
}

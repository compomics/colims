package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.MaxQuantImport;
import com.compomics.colims.core.service.FastaDbService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.distributed.io.maxquant.MaxQuantTestSuite;
import com.compomics.colims.distributed.io.maxquant.UnparseableException;
import com.compomics.colims.model.User;
import com.compomics.colims.model.UserBean;
import com.compomics.colims.model.enums.FastaDbType;
import org.jdom2.JDOMException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

/**
 * @author Davy
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})
@Transactional
@Rollback
public class MaxQuantParserTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserBean userBean;
    @Autowired
    FastaDbService fastaDbService;
    @Autowired
    MaxQuantParser maxQuantParser;

    @Before
    public void setup() throws IOException, XmlPullParserException {
        //set admin user in authentication bean
        User adminUser = userService.findByName("admin");
        userBean.setCurrentUser(adminUser);
    }

    @Test
    public void testParse() throws MappingException, UnparseableException, IOException, JDOMException {
        //@// TODO: 17/10/16 add relevant tests

        //save the test fasta to the database first
        fastaDbService.persist(MaxQuantTestSuite.testFastaDb);

        EnumMap<FastaDbType, List<Long>> fastaDbs = new EnumMap<>(FastaDbType.class);
        fastaDbs.put(FastaDbType.PRIMARY, new ArrayList<>(Arrays.asList(MaxQuantTestSuite.testFastaDb.getId())));

        maxQuantParser.clear();
        MaxQuantImport maxQuantImport = new MaxQuantImport(MaxQuantTestSuite.mqparFile,
                MaxQuantTestSuite.maxQuantCombinedDirectory, fastaDbs, false, false, new ArrayList<>(), "label free");
        maxQuantParser.parse(maxQuantImport);

        Assert.assertFalse(maxQuantParser.getAnalyticalRuns().isEmpty());
    }

}
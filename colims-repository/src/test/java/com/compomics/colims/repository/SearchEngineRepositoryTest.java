package com.compomics.colims.repository;

import com.compomics.colims.model.CvTerm;

import com.compomics.colims.model.Instrument;
import com.compomics.colims.model.InstrumentType;
import com.compomics.colims.model.SearchEngine;
import com.compomics.colims.model.enums.CvTermType;
import com.compomics.colims.model.enums.SearchEngineType;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-repository-context.xml", "classpath:colims-repository-test-context.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class SearchEngineRepositoryTest {

    @Autowired
    private SearchEngineRepository searchEngineRepository;    

    @Test
    public void testFindByNameAndVersion(){
        //expect to find no result with the given parameters
        SearchEngine foundSearchEngine = searchEngineRepository.findByNameAndVersion(SearchEngineType.PEPTIDESHAKER, "3.1.6");
        Assert.assertNull(foundSearchEngine);

        //expect to find a result
        foundSearchEngine = searchEngineRepository.findByNameAndVersion(SearchEngineType.PEPTIDESHAKER, "0.28.0");
        Assert.assertNotNull(foundSearchEngine);
    }
    
}

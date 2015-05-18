
package com.compomics.colims.core.service;

import com.compomics.colims.model.SearchEngine;
import com.compomics.colims.model.enums.SearchEngineType;
import org.junit.Assert;
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
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class SearchAndValidationSettingsServiceTest {

    @Autowired
    private SearchAndValidationSettingsService searchAndValidationSettingsService;

    /**
     * Test the getSearchEngine method.
     */
    @Test
    public void testGetSearchEngine() {
        //test with a known version
        SearchEngine searchEngine = searchAndValidationSettingsService.getSearchEngine(SearchEngineType.PEPTIDESHAKER, "0.0.0");

        Assert.assertNotNull(searchEngine);

        //test with an unknown version
        searchEngine = searchAndValidationSettingsService.getSearchEngine(SearchEngineType.PEPTIDESHAKER, "0.3.0");

        Assert.assertNotNull(searchEngine);
    }

}

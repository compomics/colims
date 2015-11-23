package com.compomics.colims.repository;

import com.compomics.colims.model.SearchParameters;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-repository-context.xml", "classpath:colims-repository-test-context.xml"})
@Transactional
@Rollback
public class SearchParametersRepositoryTest {

    @Autowired
    private SearchParametersRepository searchParametersRepository;

    @Test
    public void testFindByExample() {
        SearchParameters searchParametersExample = searchParametersRepository.findById(2L);

        List<SearchParameters> searchParameterses = searchParametersRepository.findByExample(searchParametersExample);

        //only the 1 entry should be returned, the 2 others are slightly different
        Assert.assertFalse(searchParameterses.isEmpty());
        Assert.assertEquals(1, searchParameterses.size());
        Assert.assertEquals(searchParametersExample.getId(), searchParameterses.get(0).getId());
    }

    @Test
    public void testGetConstraintLessSearchParameterIdsForRuns() {
        List<Long> runIds = new ArrayList<>();
        runIds.add(1L);

        List<Long> searchParametersIds = searchParametersRepository.getConstraintLessSearchParameterIdsForRuns(runIds);

        //3 entries: 1 linked to 2 runs, and 2 constraint less ones
        Assert.assertEquals(2, searchParametersIds.size());
    }

}

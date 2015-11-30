package com.compomics.colims.repository;

import com.compomics.colims.model.UserQuery;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-repository-context.xml", "classpath:colims-repository-test-context.xml"})
@Transactional
@Rollback
public class UserQueryRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private UserQueryRepository userQueryRepository;

    @Test
    public void testFindByUserIdAndQueryString() {
        UserQuery userQuery = userQueryRepository.findByUserIdAndQueryString(1L, "test user query string");

        Assert.assertNotNull(userQuery);
    }

    @Test
    public void testFindByUserId() {
        List<String> userQueries = userQueryRepository.findQueriesByUserId(1L);

        Assert.assertFalse(userQueries.isEmpty());
        Assert.assertEquals("test user query string 1", userQueries.get(0));
    }

    @Test
    public void testCountByUserId() {
        Long userQueryCount = userQueryRepository.countByUserId(1L);

        Assert.assertEquals(3L, userQueryCount.longValue());
    }

    @Test
    public void testExecuteUserQuery() {
        List<LinkedHashMap<String, Object>> results = userQueryRepository.executeUserQuery("select * from colims_user", 300);

        Assert.assertEquals(4, results.size());
    }

}

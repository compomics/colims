
package com.compomics.colims.core.service;

import com.compomics.colims.model.User;
import com.compomics.colims.model.UserQuery;
import com.compomics.colims.repository.UserQueryRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;


/**
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
@Rollback
@Transactional
public class UserQueryServiceTest {

    @Autowired
    private UserQueryService userQueryService;
    @Autowired
    private UserQueryRepository userQueryRepository;
    @Autowired
    private UserService userService;

    @Test
    public void testExecuteUserQuery() {
        User user = userService.findById(1L);

        List<LinkedHashMap<String, Object>> results = userQueryService.executeUserQuery(user, "select * from colims_user");

        List<String> queries = userQueryService.findQueriesByUserId(user.getId());
        Assert.assertEquals(4, queries.size());

        UserQuery userQuery = userQueryRepository.findByUserIdAndQueryString(1L, "select * from colims_user");
        Assert.assertNotNull(userQuery);
        //check the usage count
        Assert.assertEquals(1, userQuery.getUsageCount().intValue());

        //execute the query again, usage count should have been increased
        results = userQueryService.executeUserQuery(user, "select * from colims_user");

        queries = userQueryService.findQueriesByUserId(user.getId());
        Assert.assertEquals(4, queries.size());

        userQuery = userQueryRepository.findByUserIdAndQueryString(1L, "select * from colims_user");
        Assert.assertNotNull(userQuery);
        //check the usage count
        Assert.assertEquals(2, userQuery.getUsageCount().intValue());

        //execute another query, max number of stored queries is 4 so the least used one should be removed
        results = userQueryService.executeUserQuery(user, "select * from project");

        queries = userQueryService.findQueriesByUserId(user.getId());
        Assert.assertEquals(4, queries.size());
    }

}

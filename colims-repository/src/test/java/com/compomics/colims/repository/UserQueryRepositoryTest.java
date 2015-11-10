package com.compomics.colims.repository;

import com.compomics.colims.model.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

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

        Assert.assertNull(userQuery);
    }

}

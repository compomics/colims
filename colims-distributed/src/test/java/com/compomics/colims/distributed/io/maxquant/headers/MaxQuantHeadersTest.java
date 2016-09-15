package com.compomics.colims.distributed.io.maxquant.headers;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.util.List;

/**
 * Created by Niels Hulstaert on 14/09/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-simple-test-context.xml"})
public class MaxQuantHeadersTest {

    @Autowired
    private AbstractMaxQuantHeaders maxQuantHeaders;

    @Test
    public void testParse() throws HttpClientErrorException, IOException {
        for (List<MaxQuantHeader> headers : maxQuantHeaders.getHeadersMap().values()) {
            Assert.assertNotNull(maxQuantHeaders);
        }
    }

}

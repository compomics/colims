package com.compomics.colims.distributed.io.maxquant.headers;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Niels Hulstaert on 14/09/16.
 */
public class MaxQuantHeadersTest {

    private EvidenceHeaders evidenceHeaders;

    public MaxQuantHeadersTest() throws IOException {
        evidenceHeaders = new EvidenceHeaders();
    }

    @Test
    public void testParse() throws HttpClientErrorException, IOException {
        Assert.assertFalse(evidenceHeaders.getHeadersMap().isEmpty());
        for (Map.Entry<EvidenceHeader, MaxQuantHeader> entry : evidenceHeaders.getHeadersMap().entrySet()) {
            Assert.assertNotNull(entry.getValue());
            Assert.assertEquals(entry.getKey().name(), entry.getValue().getName());
        }
    }

}

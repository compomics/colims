package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.distributed.io.maxquant.MaxQuantTestSuite;
import com.compomics.colims.model.Spectrum;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-simple-test-context.xml"})
public class KeyValueWrapperTest {

    @Test
    public void testJsonParse() throws Exception {
        String str = "[ {\"foo\": \"bar\"}, {\"key\": true}, {\"15.23\" : 10.0} ]";

        ObjectMapper objectMapper = new ObjectMapper();
//        JavaType listWrappersType = objectMapper.getTypeFactory()
//                .constructCollectionType(List.class, KeyValueWrapper.class);
//        List<KeyValueWrapper> list = objectMapper.readValue(str, listWrappersType);
//
//        List<KeyValueWrapper> reverse = new ArrayList<>();
//        KeyValueWrapper<String, Double> k1 = new KeyValueWrapper<>();
//        k1.set("testKey1", 221.255);
//        KeyValueWrapper<String, Double> k2 = new KeyValueWrapper<>();
//        k2.set("testKey2", 221.255);
//        reverse.add(k1);
//        reverse.add(k2);

        Map<String, Double> testMap = new HashMap<>();
        testMap.put("testKey1", 23.5484);
        testMap.put("testKey2", 23.0);
        testMap.put("testKey3", null);

        String s = objectMapper.writeValueAsString(testMap);
        String str2 = "{\"foo\": \"bar\", \"key\": true, \"15.23\" : 10.0}";

        Map<String, ?> map = objectMapper.readValue(str2, Map.class);
        Map<String, Double> map2 = objectMapper.readValue(s, new TypeReference<Map<String, Double>>(){});

        System.out.println("test");

//        Assert.assertEquals(2864, maxQuantSpectra.getSpectrumToPsms().size());
//        Assert.assertTrue(maxQuantSpectra.getUnidentifiedSpectra().isEmpty());
    }

}

package com.compomics.colims.repository;

import com.compomics.colims.model.ProteinGroup;
import com.compomics.colims.model.ProteinGroupQuantLabeled;
import com.compomics.colims.repository.hibernate.ProteinGroupDTO;
import com.compomics.colims.repository.hibernate.SortDirection;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-repository-context.xml", "classpath:colims-repository-test-context.xml"})
@Transactional
@Rollback
public class ProteinGroupQuantLabeledTest {

    @Autowired
    private ProteinGroupQuantLabeledRepository proteinGroupQuantLabeledRepository;

    @Test
    public void testStoreJsonLabelsTest() throws IOException {
        //Assert.assertTrue(proteinGroupDTOs.isEmpty());

        ProteinGroupQuantLabeled proteinGroupQuantLabeled = new ProteinGroupQuantLabeled();

        String jsonString = "{\"k1\":\"v1\",\"k2\":\"v2\"}";

        proteinGroupQuantLabeled.setLabels(jsonString);

        proteinGroupQuantLabeledRepository.persist(proteinGroupQuantLabeled);

        System.out.println("ddddddddddd");
    }

}

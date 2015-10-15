package com.compomics.colims.repository;

import com.compomics.colims.model.ProteinAccession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

/**
 * Created by Iain on 14/09/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-repository-context.xml", "classpath:colims-repository-test-context.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ProteinAccessionRepositoryTest {

    @Autowired
    ProteinAccessionRepository proteinAccessionRepository;
    @Autowired
    PeptideRepository peptideRepository;
    @Autowired
    ProteinGroupRepository proteinGroupRepository;

    @Test
    public void testFindByAccession() throws Exception {
        List<ProteinAccession> proteinAccessions = proteinAccessionRepository.findByAccession("PROT_ACC_11");

        assertThat(proteinAccessions.size(), greaterThan(0));
    }
}
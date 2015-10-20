package com.compomics.colims.repository;

import com.compomics.colims.repository.hibernate.model.PeptideDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Iain on 14/07/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-repository-context.xml", "classpath:colims-repository-test-context.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class PeptideRepositoryTest {

    @Autowired
    private PeptideRepository peptideRepository;

    @Test
    public void testGetPeptideDTOByProteinGroupId() {
        List<PeptideDTO> peptideDTOs = peptideRepository.getPeptideDTOByProteinGroupId(2L);

        Assert.assertFalse(peptideDTOs.isEmpty());
        Assert.assertEquals(2, peptideDTOs.size());

        PeptideDTO peptideDTO = peptideDTOs.get(0);
        Assert.assertEquals(1L, peptideDTO.getProteinGroupCount());
        Assert.assertEquals(0.8, peptideDTO.getPeptideProbability(), 0.001);
        Assert.assertEquals(0.2, peptideDTO.getPeptidePostErrorProbability(), 0.001);
        Assert.assertNotNull(peptideDTO.getPeptide());

        peptideDTO = peptideDTOs.get(1);
        Assert.assertEquals(2L, peptideDTO.getProteinGroupCount());
        Assert.assertEquals(0.6, peptideDTO.getPeptideProbability(), 0.001);
        Assert.assertEquals(0.4, peptideDTO.getPeptidePostErrorProbability(), 0.001);
        Assert.assertNotNull(peptideDTO.getPeptide());
    }

}
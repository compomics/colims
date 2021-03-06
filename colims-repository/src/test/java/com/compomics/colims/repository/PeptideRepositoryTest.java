package com.compomics.colims.repository;

import com.compomics.colims.repository.hibernate.PeptideDTO;
import java.util.ArrayList;

import com.compomics.colims.repository.hibernate.PeptideMzTabDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Iain on 14/07/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-repository-context.xml", "classpath:colims-repository-test-context.xml"})
@Transactional
@Rollback
public class PeptideRepositoryTest {

    @Autowired
    private PeptideRepository peptideRepository;

    @Test
    public void testGetPeptideDTOsByProteinGroupId() {
        List<Long> analyticalRunIDs = new ArrayList<>();
        analyticalRunIDs.add(1L);
        List<PeptideDTO> peptideDTOs = peptideRepository.getPeptideDTOs(2L,analyticalRunIDs);

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

    @Test
    public void testGetDistinctPeptideSequences() {
        List<Long> analyticalRunIDs = new ArrayList<>();
        analyticalRunIDs.add(1L);
        List<String> peptideSequences = peptideRepository.getDistinctPeptideSequences(2L,analyticalRunIDs);

        Assert.assertFalse(peptideSequences.isEmpty());
        Assert.assertEquals(2, peptideSequences.size());
    }



    @Test
    public void testGetPeptideMzTabDTOs() {
        List<Long> analyticalRunIDs = new ArrayList<>();
        analyticalRunIDs.add(1L);
        List<PeptideMzTabDTO> peptideMzTabDTOS = peptideRepository.getPeptideMzTabDTOs(analyticalRunIDs);

        Assert.assertFalse(peptideMzTabDTOS.isEmpty());
        Assert.assertEquals(4, peptideMzTabDTOS.size());
    }

}
package com.compomics.colims.repository;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.repository.AnalyticalRunRepository;
import com.compomics.colims.repository.SpectrumRepository;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Iain on 13/04/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-repository-context.xml", "classpath:colims-repository-test-context.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class SpectrumRepositoryTest extends TestCase {

    @Autowired
    AnalyticalRunRepository analyticalRunRepository;

    @Autowired
    SpectrumRepository spectrumRepository;

    @Test
    public void testFiltering() throws Exception {
        AnalyticalRun analyticalRun = analyticalRunRepository.findById(1L);

        List spectrumList = spectrumRepository.getPagedSpectra(analyticalRun, 0, 10, "id", "asc", "ABCDEFGH");

        assertTrue(spectrumList.size() < analyticalRun.getSpectrums().size());
    }

    @Test
    public void testSorting() {
        AnalyticalRun analyticalRun = analyticalRunRepository.findById(1L);

        List<Spectrum> spectrumList = spectrumRepository.getPagedSpectra(analyticalRun, 0, 10, "retentionTime", "asc", null);

        assertTrue(spectrumList.get(0).getRetentionTime() <= analyticalRun.getSpectrums().get(0).getRetentionTime());
    }
}
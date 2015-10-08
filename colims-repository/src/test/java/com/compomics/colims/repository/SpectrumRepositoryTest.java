package com.compomics.colims.repository;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Spectrum;
import junit.framework.TestCase;
import org.junit.Before;
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

    private AnalyticalRun analyticalRun;

    @Before
    @Override
    public void setUp() throws Exception {
        this.analyticalRun = analyticalRunRepository.findById(1L);
    }

    @Test
    public void testFiltering() throws Exception {
        List spectrumList = spectrumRepository.getPagedSpectra(analyticalRun, 0, 10, "spectrum.id", "asc", "LENNART");

        assertTrue(spectrumList.size() < analyticalRun.getSpectrums().size());
    }

    @Test
    public void testSorting() {
        List<Spectrum> spectrumList = spectrumRepository.getPagedSpectra(analyticalRun, 0, 10, "retention_time", "desc", "");

        assertTrue(spectrumList.get(0).getRetentionTime() <= analyticalRun.getSpectrums().get(0).getRetentionTime());
    }

    @Test
    public void testGetSpectraCountForRun() throws Exception {
        int count = spectrumRepository.getSpectraCountForRun(analyticalRun, "spectrum.id", "");

        assertTrue(count > 0);
    }
}
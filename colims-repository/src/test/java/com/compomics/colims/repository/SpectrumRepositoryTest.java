package com.compomics.colims.repository;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Spectrum;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Iain on 13/04/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-repository-context.xml", "classpath:colims-repository-test-context.xml"})
@Transactional
@Rollback
public class SpectrumRepositoryTest {

    private AnalyticalRun analyticalRun;

    @Autowired
    AnalyticalRunRepository analyticalRunRepository;
    @Autowired
    SpectrumRepository spectrumRepository;

    @Before
    public void setUp() throws Exception {
        this.analyticalRun = analyticalRunRepository.findById(1L);
    }

    @Test
    public void testFiltering() throws Exception {
        List spectrumList = spectrumRepository.getPagedSpectra(analyticalRun, 0, 10, "spectrum.id", "asc", "LENNART");

        Assert.assertTrue(spectrumList.size() < analyticalRun.getSpectrums().size());
    }

    @Test
    public void testSorting() {
        List<Spectrum> spectrumList = spectrumRepository.getPagedSpectra(analyticalRun, 0, 10, "retention_time", "desc", "");

        Assert.assertTrue(spectrumList.get(1).getRetentionTime() <= analyticalRun.getSpectrums().get(0).getRetentionTime());
    }

    @Test
    public void testGetSpectraCountForRun() throws Exception {
        int count = spectrumRepository.getSpectraCountForRun(analyticalRun, "spectrum.id", "");

        Assert.assertTrue(count > 0);
    }

    @Test
    public void testGetSpectraProjections() {
        List<Long> analyticalRunIds = new ArrayList<>();
        analyticalRunIds.add(1L);
        Object[] spectraProjections = spectrumRepository.getSpectraProjections(analyticalRunIds);

        Assert.assertEquals(6, spectraProjections.length);
        Assert.assertEquals(24.3, (Double) spectraProjections[0], 0.01);
        Assert.assertEquals(26.0, (Double) spectraProjections[1], 0.01);
        Assert.assertEquals(555.3, (Double) spectraProjections[2], 0.01);
        Assert.assertEquals(1300.1, (Double) spectraProjections[3], 0.01);
        Assert.assertEquals(1L, ((Integer) spectraProjections[4]).intValue());
        Assert.assertEquals(3L, ((Integer) spectraProjections[5]).intValue());
    }

}

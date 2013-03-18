package com.compomics.colims.repository;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.repository.impl.GenericHibernateRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:colims-repository-context.xml", "classpath:colims-repository-test-context.xml" })
@Transactional
public class SpectrumRepositoryTest {
    @Repository
    public static class AnalyticalRunRepositoryForTesting extends GenericHibernateRepository<AnalyticalRun, Long> {
    }

    @Autowired
    AnalyticalRunRepositoryForTesting analyticalRunRepository;

    @Autowired
    SpectrumRepository repo;

    Long idSpectrum;
    Long idAnalyticalRun;

    @Before
    public void saveSpectrum() {
        //Setup analyticalRun
        AnalyticalRun analyticalRun = new AnalyticalRun();
        analyticalRun.setAccession("accessionAnalyticalRun");

        //Setup Spectrum
        Spectrum spectrum = new Spectrum();
        spectrum.setAccession("accessionSpectrum");
        spectrum.setCharge(1);
        spectrum.setScanNumber("scanNumber");
        spectrum.setMzRatio(123.45);
        spectrum.setAnalyticalRun(analyticalRun);

        //Persist AnalyticalRun and Spectrum through cascade
        analyticalRun.getSpectrums().add(spectrum);
        analyticalRunRepository.save(analyticalRun);

        //Ensure they're both persisted
        Assert.assertNotNull("Identifier should be assigned now", spectrum.getId());
        Assert.assertNotNull("Identifier should be assigned now", analyticalRun.getId());
        idSpectrum = spectrum.getId();
        idAnalyticalRun = analyticalRun.getId();
    }

    @Test
    public final void testFindSpectraByAnalyticalRunId() {
        //Find spectrum
        List<Spectrum> found = repo.findSpectraByAnalyticalRunId(idAnalyticalRun);
        Assert.assertNotNull("Spectrum should be found by it's analyticalrun.id", found);
        Assert.assertEquals(1, found.size());

        //Compare expected values for first (and only) spectrum
        Spectrum spectrum = found.get(0);
        Assert.assertEquals(idSpectrum, spectrum.getId());
        Assert.assertEquals(idAnalyticalRun, spectrum.getAnalyticalRun().getId());
    }
}

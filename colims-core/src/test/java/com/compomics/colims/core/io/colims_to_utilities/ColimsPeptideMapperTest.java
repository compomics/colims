/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.io.colims_to_utilities;

import com.compomics.colims.core.service.SpectrumService;
import com.compomics.colims.model.Spectrum;
import com.compomics.util.experiment.identification.matches.IonMatch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author niels
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
@Transactional
public class ColimsPeptideMapperTest {

    @Autowired
    private ColimsPeptideMapper colimsPeptideMapper;
    @Autowired
    private SpectrumService spectrumService;


    /**
     * Test of testMapFragmentAnnotations method.
     *
     * @throws Exception in case of an exception
     */
    @Test
    public void testMapFragmentAnnotations() throws IOException {
        Spectrum spectrum = spectrumService.findById(7L);

        ArrayList<IonMatch> ionMatches = colimsPeptideMapper.mapFragmentAnnotations(spectrum.getPeptides().get(0));

        Assert.assertEquals(23, ionMatches.size());
    }
}

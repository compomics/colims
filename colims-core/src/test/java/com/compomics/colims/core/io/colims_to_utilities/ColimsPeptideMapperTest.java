/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.io.colims_to_utilities;

import com.compomics.colims.core.service.PeptideService;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.SpectrumFile;
import com.compomics.colims.model.enums.FragmentationType;
import com.compomics.util.experiment.massspectrometry.Charge;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * @author niels
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
public class ColimsPeptideMapperTest {

    @Autowired
    private ColimsPeptideMapper colimsPeptideMapper;
    @Autowired
    private PeptideService peptideService;

    /**
     * Test of testMapFragmentAnnotations method.
     *
     * @throws Exception in case of an exception
     */
    @Test
    public void testMapFragmentAnnotations() {
        Peptide peptide = peptideService.findById(8L);

        colimsPeptideMapper.mapFragmentAnnotations(peptide);
        System.out.println("ddddd");
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.io.colims_to_utilities;

import com.compomics.colims.core.io.colims_to_utilities.ColimsPsmMapper;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasProtein;
import com.compomics.colims.model.Protein;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.SpectrumFile;
import com.compomics.colims.model.enums.FragmentationType;
import com.compomics.util.experiment.identification.matches.SpectrumMatch;
import com.compomics.util.experiment.massspectrometry.Charge;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Kenneth Verheggen
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
public class ColimsPsmMapperTest {

    @Autowired
    private ColimsPsmMapper colimsPsmMapper;

    public ColimsPsmMapperTest() {
    }

    /**
     * Test of map method, of class ColimsPsmMapper.
     */
    @Test
    public void testMap() throws Exception {
        System.out.println("Test mapping colims spectra to a list of spectrumMatch objects");

        Protein inputProtein = new Protein();
        inputProtein.setAccession("P0C9F1");
        inputProtein.setSequence("MVRLFHNPIKCLFYRGSRKTREKKLRKSLKKLNFYHPPGDCCQIYRLLENVPGGTYFITENMTNELIMIVKDSVDKKIKSVKLNFYGSYIKIHQHYYINIYMYLMRYTQIYKYPLICFNKYSYCNS");

        File inputSpectra = new ClassPathResource("data/peptideshaker/input_spectra.mgf").getFile();
        byte[] data = Files.readAllBytes(inputSpectra.toPath());
        SpectrumFile spectrumFile = new SpectrumFile();
        spectrumFile.setContent(data);
        List<SpectrumFile> spectrumFileList = new ArrayList<>();
        spectrumFileList.add(spectrumFile);

        Spectrum spectrum = new Spectrum();
        spectrum.setAccession("fake spectrum 1");
        spectrum.setFragmentationType(FragmentationType.CID);
        spectrum.setIntensity(1233123.32);
        spectrum.setSpectrumFiles(spectrumFileList);

        spectrum.setPeptides(null);

        List<PeptideHasProtein> peptideHasProtList = new ArrayList<>();
        List<Peptide> peptideList = new ArrayList<>();

        PeptideHasProtein mainPeptide = new PeptideHasProtein();
        Peptide aMainPeptide = new Peptide();
        aMainPeptide.setTheoreticalMass(33.3);
        aMainPeptide.setPsmProbability(96.5);
        aMainPeptide.setPeptideHasProteins(peptideHasProtList);
        aMainPeptide.setSequence("GGTYFITENMTNDLIMVVKDSVDKKIKS");
        mainPeptide.setPeptide(aMainPeptide);
        mainPeptide.setProtein(inputProtein);
        peptideHasProtList.add(mainPeptide);

        peptideList.add(aMainPeptide);
        spectrum.setPeptides(peptideList);
        spectrum.setCharge(3);
        List<SpectrumMatch> targetSpectrumMap = new ArrayList<>();
        colimsPsmMapper.map(spectrum, targetSpectrumMap);

        Assert.assertEquals(targetSpectrumMap.isEmpty(), false);
        Assert.assertEquals("fake spectrum 1", targetSpectrumMap.get(0).getKey());
        Assert.assertEquals(1, targetSpectrumMap.get(0).getAllAssumptions().size());
        Assert.assertEquals(96.5, targetSpectrumMap.get(0).getAllAssumptions().get(0).getScore(), 0.1);
        //@todo fix and uncomment this
//        Assert.assertEquals("GGTYFITENMTNDLIMVVKDSVDKKIKS", targetSpectrumMap.get(0).getAllAssumptions().get(0).getPeptide().getSequence());
        Assert.assertEquals(new Charge(1, 3), targetSpectrumMap.get(0).getAllAssumptions().get(0).getIdentificationCharge());
    }
}

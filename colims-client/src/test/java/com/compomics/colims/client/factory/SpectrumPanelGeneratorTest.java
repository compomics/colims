package com.compomics.colims.client.factory;

import com.compomics.colims.core.service.SpectrumService;
import com.compomics.colims.model.Spectrum;
import com.compomics.util.gui.spectrum.SpectrumPanel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.*;

import static org.junit.Assert.*;

/**
 * Created by Iain on 16/09/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-client-context.xml", "classpath:colims-client-test-context.xml"})
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class SpectrumPanelGeneratorTest {
    @Autowired
    SpectrumPanelGenerator spectrumPanelGenerator;
    @Autowired
    SpectrumService spectrumService;

    @Test
    public void testInit() throws Exception {
        Spectrum spectrum = spectrumService.findById(1L);
        spectrumPanelGenerator.init(spectrum);
    }

    @Test
    public void testDecorateSpectrumPanel() throws Exception {
        Spectrum spectrum = spectrumService.findById(1L);
        spectrumPanelGenerator.init(spectrum);

        JPanel spectrumPanel = new JPanel(null);

        spectrumPanelGenerator.decorateSpectrumPanel(spectrumPanel);
    }

    @Test
    public void testDecorateSecondaryPanel() throws Exception {

    }
}
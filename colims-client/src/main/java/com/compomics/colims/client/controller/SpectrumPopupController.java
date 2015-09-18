package com.compomics.colims.client.controller;

import com.compomics.colims.client.factory.SpectrumPanelGenerator;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.SpectrumPopupDialog;
import com.compomics.colims.core.io.colims_to_utilities.ColimsSpectrumMapper;
import com.compomics.colims.model.Spectrum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Controller for spectrum panel pop-up window
 *
 * Created by Iain on 28/07/2015.
 */
@Component
public class SpectrumPopupController implements Controllable {
    private SpectrumPopupDialog spectrumPopupDialog;

    @Autowired
    MainController mainController;
    @Autowired
    ColimsSpectrumMapper colimsSpectrumMapper;
    @Autowired
    SpectrumPanelGenerator spectrumPanelGenerator;

    @Override
    public void init() {
        spectrumPopupDialog = new SpectrumPopupDialog(mainController.getMainFrame(), true);
    }

    @Override
    public void showView() {
        GuiUtils.centerDialogOnComponent(mainController.getMainFrame(), spectrumPopupDialog);
        spectrumPopupDialog.setVisible(true);
    }

    /**
     * Update the panel for the given spectrum then display it
     *
     * @param spectrum A spectrum to show
     */
    public void updateView(Spectrum spectrum) {
        spectrumPanelGenerator.init(spectrum);
        spectrumPanelGenerator.decorateSpectrumPanel(spectrumPopupDialog.getSpectrumJPanel());
        spectrumPanelGenerator.decorateSecondaryPanel(spectrumPopupDialog.getSecondarySpectrumPlotsJPanel());

        showView();
    }
}

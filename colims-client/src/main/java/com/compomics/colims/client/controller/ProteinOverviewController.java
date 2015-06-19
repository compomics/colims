package com.compomics.colims.client.controller;

import com.compomics.colims.client.view.ProteinOverviewPanel;
import com.google.common.eventbus.EventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Iain on 19/06/2015.
 */
@Component
public class ProteinOverviewController implements Controllable {

    @Autowired
    private MainController mainController;
    @Autowired
    private EventBus eventBus;

    private ProteinOverviewPanel proteinOverviewPanel;

    @Override
    public void init() {
        eventBus.register(this);
        proteinOverviewPanel = new ProteinOverviewPanel(mainController.getMainFrame(), this);
    }

    @Override
    public void showView() {

    }

    public ProteinOverviewPanel getProteinOverviewPanel() {
        return proteinOverviewPanel;
    }
}

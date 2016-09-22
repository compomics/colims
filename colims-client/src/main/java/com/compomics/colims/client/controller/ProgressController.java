package com.compomics.colims.client.controller;

import com.compomics.colims.client.event.progress.ProgressEndEvent;
import com.compomics.colims.client.event.progress.ProgressStartEvent;
import com.compomics.colims.client.event.progress.ProgressUpdateEvent;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.util.gui.waiting.waitinghandlers.ProgressDialogX;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.awt.Toolkit;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The progress bar controller.
 *
 * @author Niels Hulstaert
 */
//@Component("progressController")
public class ProgressController implements Controllable {

    //model
    private int progress;
    //view
    private ProgressDialogX progressDialog = new ProgressDialogX(true);
    @Autowired
    private EventBus eventBus;

    @PostConstruct
    @Override
    public void init() {
        eventBus.register(this);
    }

    /**
     * Listen for ProgressStartEvent instances.
     *
     * @param progressStartEvent the progress start event
     */
    @Subscribe
    public void onProgressStartEvent(ProgressStartEvent progressStartEvent) {
        progressDialog = new ProgressDialogX(progressStartEvent.getParent(),
                Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/colims_icon.png")),
                Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/colims_icon.png")),
                true);

        progressDialog.setTitle(progressStartEvent.getHeaderText() + " Please Wait...");
        if (!progressStartEvent.isIsIndeterminate()) {
            progressDialog.getProgressBar().setMaximum(progressStartEvent.getNumberOfSteps() + 1);
            progress = 1;
        } else {
            progressDialog.getProgressBar().setIndeterminate(true);
            progressDialog.getProgressBar().setStringPainted(false);
        }

        GuiUtils.centerDialogOnComponent(progressStartEvent.getParent(), progressDialog);

        new Thread(() -> {
            try {
                progressDialog.setVisible(true);
            } catch (IndexOutOfBoundsException e) {
                // ignore
            }
        }, "ProgressDialog").start();
    }

    /**
     * Listen for ProgressUpdateEvent instances.
     *
     * @param progressUpdateEvent the progress update event
     */
    @Subscribe
    public void onProgressUpdateEvent(ProgressUpdateEvent progressUpdateEvent) {
        progressDialog.setString(progressUpdateEvent.getMessage());

        progressDialog.getProgressBar().setValue(progress);
        progress++;

        //repaint view
        progressDialog.validate();
        progressDialog.repaint();
    }

    /**
     * Listen for ProgressEndEvent instances.
     *
     * @param progressEndEvent the progress end event
     */
    @Subscribe
    public void onProgressEndEvent(ProgressEndEvent progressEndEvent) {
        progressDialog.setRunFinished();
        progressDialog.setVisible(Boolean.FALSE);
    }

    @Override
    public void showView() {
        //do nothing
    }
}

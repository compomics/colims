package com.compomics.colims.client.controller;

import com.compomics.colims.client.event.progress.ProgressStartEvent;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.util.gui.waiting.waitinghandlers.ProgressDialogX;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The progress bar controller.
 *
 * @author Niels Hulstaert
 */
@Component("progressController")
public class ProgressController extends WindowAdapter implements Controllable {

    //model
    private int progress;
    private boolean progressFinished;
    //view
    private ProgressDialogX progressDialog;
    //parent controller
    @Autowired
    private MainController mainController;
    @Autowired
    private EventBus eventBus;

    @Override
    public void init() {
        progressFinished = Boolean.FALSE;
    }

    /**
     * Listen for ProgressStartEvent instances.
     *
     * @param progressStartEvent
     */
    @Subscribe
    public void onProgressStartEvent(ProgressStartEvent progressStartEvent){

    }

    public void showProgressBar(int numberOfProgressSteps, String progressHeaderText) {
        progressDialog = new ProgressDialogX(mainController.getMainFrame(),
                Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/pride-asap.png")),
                Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/pride-asap-orange.png")),
                true);
        progressDialog.addWindowListener(this);

        progressDialog.getProgressBar().setMaximum(numberOfProgressSteps + 1);
        progressDialog.setTitle(progressHeaderText + " Please Wait...");
        progress = 1;
        GuiUtils.centerDialogOnComponent(mainController.getMainFrame(), progressDialog);
        progressFinished = Boolean.FALSE;

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    progressDialog.setVisible(true);
                } catch (IndexOutOfBoundsException e) {
                    // ignore
                }
            }
        }, "ProgressDialog").start();
    }

    public boolean isRunCancelled() {
        return progressDialog.isRunCanceled();
    }

    public void hideProgressDialog() {
        progressFinished = true;
        progressDialog.setRunFinished();
        progressDialog.setVisible(Boolean.FALSE);
    }

    public void setProgressInfoText(String progressInfoText) {
        progressDialog.setString(progressInfoText);

        progressDialog.getProgressBar().setValue(progress);
        progress++;

        //repaint view
        progressDialog.validate();
        progressDialog.repaint();
    }

    @Override
    public void showView() {
        //do nothing
    }

    @Override
    public void windowClosed(WindowEvent e) {
        super.windowClosed(e);
        if (!progressFinished) {
        }
    }

    @Override
    public void windowClosing(WindowEvent e) {
        super.windowClosing(e);
        e.getWindow().dispose();
        if (!progressFinished) {
        }
    }
}

package com.compomics.colims.client.controller;

import com.compomics.colims.client.event.progress.ProgressEndEvent;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.MzIdentMlExportDialog;
import com.compomics.colims.core.io.mzidentml.MzIdentMlExporter;
import com.google.common.eventbus.EventBus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * The mzIdentML export view controller.
 *
 * @author Niels Hulstaert
 */
@Component("mzIdentMlExportController")
@Lazy
public class MzIdentMlExportController implements Controllable {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(MzIdentMlExportController.class);

    private static final String MZIDENTML_EXTENSION = ".mzid";
    private static final String MZTAB_EXTENSION = ".mtab";

    //view
    private MzIdentMlExportDialog mzIdentMlExportDialog;
    //parent controller
    private final MainController mainController;
    //services
    private final EventBus eventBus;
    private final MzIdentMlExporter mzIdentMlExporter;

    @Autowired
    public MzIdentMlExportController(MzIdentMlExporter mzIdentMlExporter, EventBus eventBus, MainController mainController) {
        this.mzIdentMlExporter = mzIdentMlExporter;
        this.eventBus = eventBus;
        this.mainController = mainController;
    }

    /**
     * Get the view of this controller.
     *
     * @return the MzTabExportDialog
     */
    public MzIdentMlExportDialog getMzIdentMlExportDialog() {
        return mzIdentMlExportDialog;
    }

    @Override
    @PostConstruct
    public void init() {
        //init view
        mzIdentMlExportDialog = new MzIdentMlExportDialog(mainController.getMainFrame(), true);

        //register to event bus
        eventBus.register(this);

        //select the spectra export check box
        mzIdentMlExportDialog.getExportSpectraCheckBox().setSelected(true);

        //configure the export file choosers
        mzIdentMlExportDialog.getMzIdentMlExportChooser().setMultiSelectionEnabled(false);
        mzIdentMlExportDialog.getMzIdentMlExportChooser().setFileSelectionMode(JFileChooser.FILES_ONLY);

        mzIdentMlExportDialog.getMgfExportChooser().setMultiSelectionEnabled(false);
        mzIdentMlExportDialog.getMgfExportChooser().setFileSelectionMode(JFileChooser.FILES_ONLY);

        //add action listeners
        mzIdentMlExportDialog.getBrowseMzIdentMlButton().addActionListener(e -> {
            //in response to the button click, show open dialog
            int returnVal = mzIdentMlExportDialog.getMzIdentMlExportChooser().showOpenDialog(mzIdentMlExportDialog);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File mzIdentMlFile = mzIdentMlExportDialog.getMzIdentMlExportChooser().getSelectedFile();

                //show file path in textfield
                mzIdentMlExportDialog.getMzIdentMlTextField().setText(mzIdentMlFile.getAbsolutePath() + MZIDENTML_EXTENSION);
            }
        });

        mzIdentMlExportDialog.getBrowseMgfButton().addActionListener(e -> {
            //in response to the button click, show open dialog
            int returnVal = mzIdentMlExportDialog.getMgfExportChooser().showOpenDialog(mzIdentMlExportDialog);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File mgfFile = mzIdentMlExportDialog.getMgfExportChooser().getSelectedFile();

                //show file path in textfield
                mzIdentMlExportDialog.getMgfTextField().setText(mgfFile.getAbsolutePath() + MZTAB_EXTENSION);
            }
        });

        mzIdentMlExportDialog.getExportSpectraCheckBox().addActionListener(e -> {
            boolean selected = mzIdentMlExportDialog.getExportSpectraCheckBox().isSelected();
            mzIdentMlExportDialog.getBrowseMgfButton().setEnabled(selected);
            mzIdentMlExportDialog.getMgfTextField().setText("");
        });

        mzIdentMlExportDialog.getCloseButton().addActionListener(e -> mzIdentMlExportDialog.dispose());

    }

    @Override
    public void showView() {
        mzIdentMlExportDialog.getMzIdentMlTextField().setText("");
        mzIdentMlExportDialog.getExportSpectraCheckBox().setEnabled(true);
        mzIdentMlExportDialog.getMgfTextField().setText("");

        GuiUtils.centerDialogOnComponent(mainController.getMainFrame(), mzIdentMlExportDialog);
        mzIdentMlExportDialog.setVisible(true);
    }

    /**
     * Validate the user input.
     *
     * @return the list of validation messages.
     */
    private List<String> validate() {
        List<String> validationMessages = new ArrayList<>();

        return validationMessages;
    }

    /**
     * MzIdentML exporter swing worker.
     */
    private class MzIdentMlExporterWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {

//            LOGGER.info("Exporting mzIdentML file " + mzTabExport.getFileName() + " to directory " + mzTabExport.getExportDirectory());
//            try {
//                mzTabExport.setFastaDirectory(Paths.get(fastasPath));
//                mzTabExporter.export(mzTabExport);
//                ProgressEndEvent progressEndEvent = new ProgressEndEvent();
//                eventBus.post(progressEndEvent);
//                eventBus.post(new MessageEvent("Info", "Exporting mzTab file has finished", JOptionPane.INFORMATION_MESSAGE));
//                LOGGER.info("Finished exporting mzTab file " + mzTabExport.getFileName());
//            } catch (Exception e) {
//                eventBus.post(new MessageEvent("Error", e.getMessage(), JOptionPane.ERROR_MESSAGE));
//            }
            Thread.sleep(1000);

            return null;
        }

        @Override
        protected void done() {
            try {
                get();
            } catch (InterruptedException | ExecutionException ex) {
                LOGGER.error(ex.getMessage(), ex);
            } catch (CancellationException ex) {
                LOGGER.info("Cancelling mzIdentML export.");
            } finally {
                //hide progress dialog
                eventBus.post(new ProgressEndEvent());
                //hide export dialog
                mzIdentMlExportDialog.setVisible(false);
            }
        }
    }
}

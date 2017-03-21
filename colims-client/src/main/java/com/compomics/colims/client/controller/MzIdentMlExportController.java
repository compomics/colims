package com.compomics.colims.client.controller;

import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.event.progress.ProgressEndEvent;
import com.compomics.colims.client.event.progress.ProgressStartEvent;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.MzIdentMlExportDialog;
import com.compomics.colims.core.io.mzidentml.MzIdentMlExport;
import com.compomics.colims.core.io.mzidentml.MzIdentMlExporter;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.User;
import com.compomics.colims.model.UserBean;
import com.google.common.eventbus.EventBus;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private static final String MGF_EXTENSION = ".mgf";

    /**
     * The FASTA DBs location as provided in the distributed properties file.
     */
    @Value("${fastas.path}")
    private String fastasPath = "";
    //model
    private ObservableList<User> userBindingList;
    private BindingGroup bindingGroup;
    //view
    private MzIdentMlExportDialog mzIdentMlExportDialog;
    //parent controller
    private final MainController mainController;
    private final SampleRunsController sampleRunsController;
    //services
    private final UserService userService;
    private final EventBus eventBus;
    private final MzIdentMlExporter mzIdentMlExporter;
    private final UserBean userBean;

    @Autowired
    public MzIdentMlExportController(MzIdentMlExporter mzIdentMlExporter, EventBus eventBus, MainController mainController, SampleRunsController sampleRunsController, UserService userService, UserBean userBean) {
        this.mzIdentMlExporter = mzIdentMlExporter;
        this.eventBus = eventBus;
        this.mainController = mainController;
        this.sampleRunsController = sampleRunsController;
        this.userService = userService;
        this.userBean = userBean;
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
        mzIdentMlExportDialog = new MzIdentMlExportDialog(sampleRunsController.getSampleRunsDialog(), true);

        //register to event bus
        eventBus.register(this);

        //select the spectra export check box
        mzIdentMlExportDialog.getExportSpectraCheckBox().setSelected(true);

        //configure the export file choosers
        mzIdentMlExportDialog.getMzIdentMlExportChooser().setMultiSelectionEnabled(false);
        mzIdentMlExportDialog.getMzIdentMlExportChooser().setFileSelectionMode(JFileChooser.FILES_ONLY);

        mzIdentMlExportDialog.getMgfExportChooser().setMultiSelectionEnabled(false);
        mzIdentMlExportDialog.getMgfExportChooser().setFileSelectionMode(JFileChooser.FILES_ONLY);

        //add binding
        bindingGroup = new BindingGroup();

        userBindingList = ObservableCollections.observableList(userService.findAll());

        JComboBoxBinding ownerComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, userBindingList, mzIdentMlExportDialog.getUserComboBox());
        bindingGroup.addBinding(ownerComboBoxBinding);

        bindingGroup.bind();

        //add action listeners
        mzIdentMlExportDialog.getBrowseMzIdentMlButton().addActionListener(e -> {
            //in response to the button click, show open dialog
            int returnVal = mzIdentMlExportDialog.getMzIdentMlExportChooser().showOpenDialog(mzIdentMlExportDialog);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File mzIdentMlFile = mzIdentMlExportDialog.getMzIdentMlExportChooser().getSelectedFile();

                //show file path in textfield
                String mzIdentMlFilePath = mzIdentMlFile.getAbsolutePath().endsWith(MZIDENTML_EXTENSION) ? mzIdentMlFile.getAbsolutePath() : mzIdentMlFile.getAbsolutePath() + MZIDENTML_EXTENSION;
                mzIdentMlExportDialog.getMzIdentMlTextField().setText(mzIdentMlFilePath);
            }
        });

        mzIdentMlExportDialog.getBrowseMgfButton().addActionListener(e -> {
            //in response to the button click, show open dialog
            int returnVal = mzIdentMlExportDialog.getMgfExportChooser().showOpenDialog(mzIdentMlExportDialog);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File mgfFile = mzIdentMlExportDialog.getMgfExportChooser().getSelectedFile();

                //show file path in textfield
                String mgfFilePath = mgfFile.getAbsolutePath().endsWith(MGF_EXTENSION) ? mgfFile.getAbsolutePath() : mgfFile.getAbsolutePath() + MGF_EXTENSION;
                mzIdentMlExportDialog.getMgfTextField().setText(mgfFilePath);
            }
        });

        mzIdentMlExportDialog.getExportSpectraCheckBox().addActionListener(e -> {
            boolean selected = mzIdentMlExportDialog.getExportSpectraCheckBox().isSelected();
            mzIdentMlExportDialog.getBrowseMgfButton().setEnabled(selected);
            mzIdentMlExportDialog.getMgfTextField().setText("");
        });

        mzIdentMlExportDialog.getExportButton().addActionListener(e -> {
            List<String> validationMessages = validate();
            if (validationMessages.isEmpty()) {
                MzIdentMlExporterWorker mzIdentMlExporterWorker = new MzIdentMlExporterWorker();
                ProgressStartEvent progressStartEvent = new ProgressStartEvent(mainController.getMainFrame(), true, 1, "MzIdentMl export progress. ");
                eventBus.post(progressStartEvent);
                mzIdentMlExporterWorker.execute();
            } else {
                MessageEvent messageEvent = new MessageEvent("Validation failure", validationMessages, JOptionPane.WARNING_MESSAGE);
                eventBus.post(messageEvent);
            }
        });

        mzIdentMlExportDialog.getCloseButton().addActionListener(e -> mzIdentMlExportDialog.dispose());

    }

    @Override
    public void showView() {
        mzIdentMlExportDialog.getMzIdentMlTextField().setText("");
        mzIdentMlExportDialog.getExportSpectraCheckBox().setEnabled(true);
        mzIdentMlExportDialog.getMgfTextField().setText("");

        //set the selected item in the user combobox
        mzIdentMlExportDialog.getUserComboBox().getModel().setSelectedItem(userBean.getCurrentUser());

        GuiUtils.centerDialogOnComponent(sampleRunsController.getSampleRunsDialog(), mzIdentMlExportDialog);
        mzIdentMlExportDialog.setVisible(true);
    }

    /**
     * Validate the user input.
     *
     * @return the list of validation messages.
     */
    private List<String> validate() {
        List<String> validationMessages = new ArrayList<>();

        Path fastasDirectory = Paths.get(fastasPath);
        if (!Files.exists(fastasDirectory)) {
            throw new IllegalArgumentException("The FASTA DB files directory defined in the client properties file " + fastasDirectory + " doesn't exist.");
        }
        if (mzIdentMlExportDialog.getMzIdentMlTextField().getText().isEmpty()) {
            validationMessages.add("Please select an mzIdentML export file.");
        }
        if (mzIdentMlExportDialog.getExportSpectraCheckBox().isSelected() && mzIdentMlExportDialog.getMgfTextField().getText().isEmpty()) {
            validationMessages.add("Please select an MGF export file.");
        }

        return validationMessages;
    }

    /**
     * MzIdentML exporter swing worker.
     */
    private class MzIdentMlExporterWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            Path fastasDirectory = Paths.get(fastasPath);
            Path mzIdentMlExportPath = Paths.get(mzIdentMlExportDialog.getMzIdentMlTextField().getText());
            Path mgfExportPath = mzIdentMlExportDialog.getExportSpectraCheckBox().isSelected() ? Paths.get(mzIdentMlExportDialog.getMgfTextField().getText()) : null;
            User user = userBindingList.get(mzIdentMlExportDialog.getUserComboBox().getSelectedIndex());
            //fetch the institution
            userService.fetchInstitution(user);
            LOGGER.info("Exporting runs to mzIdentML file " + mzIdentMlExportPath);
            try {
                mzIdentMlExporter.export(new MzIdentMlExport(fastasDirectory, mzIdentMlExportPath, mgfExportPath, sampleRunsController.getSelectedAnalyticalRuns(), user));
                ProgressEndEvent progressEndEvent = new ProgressEndEvent();
                eventBus.post(progressEndEvent);
                eventBus.post(new MessageEvent("Info", "mzIdentML export to " + mzIdentMlExportPath + " has finished", JOptionPane.INFORMATION_MESSAGE));
                LOGGER.info("Finished exporting mzIdentML file " + mzIdentMlExportPath);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                String message;
                if (e.getMessage() != null) {
                    message = e.getMessage();
                } else {
                    message = ExceptionUtils.getStackTrace(e);
                }
                eventBus.post(new MessageEvent("Error", message, JOptionPane.ERROR_MESSAGE));
            }
            Thread.sleep(1000);

            return null;
        }

        @Override
        protected void done() {
            try {
                get();
            } catch (InterruptedException | ExecutionException ex) {
                LOGGER.error(ex.getMessage(), ex);
                eventBus.post(new MessageEvent("Error", ex.getMessage(), JOptionPane.ERROR_MESSAGE));
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

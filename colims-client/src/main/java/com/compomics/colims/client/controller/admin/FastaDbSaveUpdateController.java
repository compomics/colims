/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.client.controller.admin;

import com.compomics.colims.client.controller.Controllable;
import com.compomics.colims.client.event.EntityChangeEvent;
import com.compomics.colims.client.event.admin.CvParamChangeEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.admin.FastaDbSaveUpdatePanel;
import com.compomics.colims.core.service.CvParamService;
import com.compomics.colims.core.service.FastaDbService;
import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.TaxonomyCvParam;
import com.compomics.colims.model.cv.CvParam;
import com.compomics.util.io.filefilters.FastaFileFilter;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 *
 * @author demet
 */
@Component("fastaDbSaveUpdateController")
@Lazy
public class FastaDbSaveUpdateController implements Controllable {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(FastaDbManagementController.class);

    /**
     * The preselected ontology namespaces.
     */
    private static final List<String> PRESELECTED_ONTOLOGY_NAMESPACES = Arrays.asList("ncbitaxon");
    /**
     * The default taxonomy value for the taxonomy combo box.
     */
    private static final TaxonomyCvParam TAXONOMY_CV_PARAM_NONE = new TaxonomyCvParam("none", "none", "none", "none");

    //model
    private BindingGroup bindingGroup;
    private ObservableList<CvParam> taxonomyBindingList;
    //view
    private FastaDbSaveUpdatePanel fastaDbSaveUpdatePanel;

    private boolean saveUpdate = false;
    private FastaDb fastaDbToEdit;
    //services
    @Autowired
    private EventBus eventBus;
    @Autowired
    private CvParamService cvParamService;
    @Autowired
    private FastaDbService fastaDbService;
    @Autowired
    private FastaDbManagementController fastaDbManagementController;
    //child controller
    @Autowired
    @Lazy
    private CvParamManagementController cvParamManagementController;

    @Override
    @PostConstruct
    public void init() {
        //register to event bus
        eventBus.register(this);

        fastaDbSaveUpdatePanel = fastaDbManagementController.getFastaDbManagementDialog().getFastaDbSaveUpdatePanel();

        taxonomyBindingList = ObservableCollections.observableList(new ArrayList<>());
        taxonomyBindingList.add(TAXONOMY_CV_PARAM_NONE);
        taxonomyBindingList.addAll(cvParamService.findByCvParamByClass(TaxonomyCvParam.class));

        //init binding
        bindingGroup = new BindingGroup();

        JComboBoxBinding taxonomyComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, taxonomyBindingList, fastaDbSaveUpdatePanel.getTaxomomyComboBox());
        bindingGroup.addBinding(taxonomyComboBoxBinding);

        bindingGroup.bind();

        //init FASTA file selection
        //disable select multiple files
        fastaDbSaveUpdatePanel.getFastaFileChooser().setMultiSelectionEnabled(false);
        //set FASTA file filter
        fastaDbSaveUpdatePanel.getFastaFileChooser().setFileFilter(new FastaFileFilter());

        //add listeners
        fastaDbSaveUpdatePanel.getBrowseTaxonomyButton().addActionListener(e -> {
            List<CvParam> cvParams = cvParamService.findByCvParamByClass(TaxonomyCvParam.class);
            //update the CV param list
            cvParamManagementController.updateDialog(TaxonomyCvParam.class, PRESELECTED_ONTOLOGY_NAMESPACES, cvParams);

            cvParamManagementController.showView();
        });

        fastaDbSaveUpdatePanel.getBrowseFastaButton().addActionListener(e -> {
            //in response to the button click, show open dialog
            int returnVal = fastaDbSaveUpdatePanel.getFastaFileChooser().showOpenDialog(fastaDbSaveUpdatePanel);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File fastaFile = fastaDbSaveUpdatePanel.getFastaFileChooser().getSelectedFile();

                //show FASTA file name and path in textfields
                fastaDbSaveUpdatePanel.getFileNameTextField().setText(fastaFile.getName());
                fastaDbSaveUpdatePanel.getFilePathTextField().setText(fastaFile.getAbsolutePath());
            }
        });

        fastaDbSaveUpdatePanel.getSaveOrUpdateButton().addActionListener(e -> {
            //validate FASTA DB
            updateFastaToEdit();
            List<String> validationMessages = GuiUtils.validateEntity(fastaDbToEdit);
            if (validationMessages.isEmpty()) {

                int index;
                if (fastaDbToEdit.getId() != null) {
                    fastaDbToEdit = fastaDbService.merge(fastaDbToEdit);
                    index = fastaDbManagementController.getSelectedFastaDbIndex();
                } else {
                    fastaDbService.persist(fastaDbToEdit);
                    //add to fasta db list
                    fastaDbManagementController.addFastaDb(fastaDbToEdit);
                    index = fastaDbManagementController.getFastaDbListSize() - 1;
                }
                fastaDbManagementController.setSelectedFasta(index);
                fastaDbSaveUpdatePanel.getNameTextField().setEnabled(false);
                fastaDbSaveUpdatePanel.getSaveOrUpdateButton().setText("update");
                fastaDbSaveUpdatePanel.getFastaDbStateInfoLabel().setText("");

                saveUpdate = true;
                MessageEvent messageEvent = new MessageEvent("Fasta DB store confirmation", "Fasta DB " + fastaDbToEdit.getName() + " was stored successfully!", JOptionPane.INFORMATION_MESSAGE);
                eventBus.post(messageEvent);
            } else {
                MessageEvent messageEvent = new MessageEvent("Validation failure", validationMessages, JOptionPane.WARNING_MESSAGE);
                eventBus.post(messageEvent);
            }
        });

        fastaDbSaveUpdatePanel.getBackButton().addActionListener(e -> {
            if (!saveUpdate) {
                fastaDbManagementController.setSelectedFasta(-1);
            }
            saveUpdate = false;
            fastaDbManagementController.showOverviewPanel();
        });
    }

    @Override
    public void showView() {
        //do nothing
    }

    /**
     * Update the fasta save update panel with the selected fasta in the fasta
     * DB management dialog.
     *
     * @param fastaDb the FastaDb instance
     */
    public void updateView(final FastaDb fastaDb) {
        fastaDbToEdit = fastaDb;

        if (fastaDb.getId() != null) {
            fastaDbSaveUpdatePanel.getNameTextField().setEnabled(false);
            fastaDbSaveUpdatePanel.getSaveOrUpdateButton().setText("update");
            fastaDbSaveUpdatePanel.getFastaDbStateInfoLabel().setText("");
        } else {
            fastaDbSaveUpdatePanel.getNameTextField().setEnabled(true);
            fastaDbSaveUpdatePanel.getSaveOrUpdateButton().setText("save");
            fastaDbSaveUpdatePanel.getFastaDbStateInfoLabel().setText("");
        }
        fastaDbSaveUpdatePanel.getTaxomomyComboBox().getModel().setSelectedItem(fastaDb.getTaxonomy());

        fastaDbSaveUpdatePanel.getNameTextField().setText(fastaDbToEdit.getName());
        fastaDbSaveUpdatePanel.getFileNameTextField().setText(fastaDbToEdit.getFileName());
        fastaDbSaveUpdatePanel.getFilePathTextField().setText(fastaDbToEdit.getFilePath());
        fastaDbSaveUpdatePanel.getVersionTextField().setText(fastaDbToEdit.getVersion());
        fastaDbSaveUpdatePanel.getHeaderParseRuleTextField().setText(fastaDbToEdit.getHeaderParseRule());
    }

    /**
     * Listen to a CV param change event posted by the
     * CvParamManagementController. If the InstrumentManagementDialog is
     * visible, clear the selection in the CV param summary list.
     *
     * @param cvParamChangeEvent the CvParamChangeEvent instance
     */
    @Subscribe
    public void onCvParamChangeEvent(CvParamChangeEvent cvParamChangeEvent) {
        CvParam cvParam = cvParamChangeEvent.getCvParam();
        if (cvParam instanceof TaxonomyCvParam) {
            EntityChangeEvent.Type type = cvParamChangeEvent.getType();
            switch (type) {
                case CREATED:
                    taxonomyBindingList.add(cvParam);
                    break;
                case UPDATED:
                    taxonomyBindingList.set(taxonomyBindingList.indexOf(cvParam), cvParam);
                    break;
                case DELETED:
                    taxonomyBindingList.remove(cvParam);
                    break;
            }
        }
    }

    /**
     * Update the instance fields of the selected fastaDb in the fastaDb
     * management dialog.
     */
    private void updateFastaToEdit() {
        fastaDbToEdit.setName(fastaDbSaveUpdatePanel.getNameTextField().getText());
        fastaDbToEdit.setFileName(fastaDbSaveUpdatePanel.getFileNameTextField().getText());
        fastaDbToEdit.setFilePath(fastaDbSaveUpdatePanel.getFilePathTextField().getText());
        fastaDbToEdit.setVersion(fastaDbSaveUpdatePanel.getVersionTextField().getText());
        fastaDbToEdit.setHeaderParseRule(fastaDbSaveUpdatePanel.getHeaderParseRuleTextField().getText());

        int taxonomyIndex = fastaDbSaveUpdatePanel.getTaxomomyComboBox().getSelectedIndex();
        if (taxonomyIndex == 0) {
            fastaDbToEdit.setTaxonomy(null);
        } else {
            fastaDbToEdit.setTaxonomy((TaxonomyCvParam) taxonomyBindingList.get(taxonomyIndex));
        }
    }

    /**
     * Clear the FASTA DB detail fields.
     */
    public void clearFastaDbDetailFields() {
        fastaDbSaveUpdatePanel.getNameTextField().setEnabled(true);
        fastaDbSaveUpdatePanel.getNameTextField().setText("");
        fastaDbSaveUpdatePanel.getFileNameTextField().setText("");
        fastaDbSaveUpdatePanel.getFilePathTextField().setText("");
        fastaDbSaveUpdatePanel.getVersionTextField().setText("");
        fastaDbSaveUpdatePanel.getTaxomomyComboBox().setSelectedIndex(0);
        fastaDbSaveUpdatePanel.getFastaDbStateInfoLabel().setText("");
    }

}

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
import com.compomics.colims.client.view.admin.HeaderParseRuleAdditionDialog;
import com.compomics.colims.core.service.CvParamService;
import com.compomics.colims.core.service.FastaDbService;
import com.compomics.colims.core.util.PathUtils;
import com.compomics.colims.core.util.PropertiesUtil;
import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.TaxonomyCvParam;
import com.compomics.colims.model.cv.CvParam;
import com.compomics.util.io.filefilters.FastaFileFilter;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
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
    private static final List<String> PRESELECTED_ONTOLOGY_NAMESPACES = Collections.singletonList("ncbitaxon");
    /**
     * The default taxonomy value for the taxonomy combo box.
     */
    private static final TaxonomyCvParam TAXONOMY_CV_PARAM_NONE = new TaxonomyCvParam("none", "none", "none");
    /**
     * List to hold map of parse rule and explanation.
     */
    private final List<Map<String, String>> headerParseRuleList = new ArrayList<>();
    /**
     * The list of databases.
     */
    private final List<String> databaseList = new ArrayList<>();
    /**
     * The properties configuration to read properties file.
     */
    private PropertiesConfiguration config;
    /**
     * The FASTAs location as provided in the client properties file.
     */
    @Value("${fastas.path}")
    private String fastasPath = "";
    /**
     * set to hold database names from properties file.
     */
    private Set<Object> databaseSet = new TreeSet<>();
    //model
    private BindingGroup bindingGroup;
    private ObservableList<CvParam> taxonomyBindingList;
    private ObservableList<String> headerParseRuleBindingList;
    private ObservableList<String> databaseBindingList;
    private boolean saveUpdate = false;
    private FastaDb fastaDbToEdit;
    //view
    private FastaDbSaveUpdatePanel fastaDbSaveUpdatePanel;
    private HeaderParseRuleAdditionDialog headerParseRuleAdditionDialog;
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
        headerParseRuleAdditionDialog = new HeaderParseRuleAdditionDialog(fastaDbManagementController.getFastaDbManagementDialog(), true);
        taxonomyBindingList = ObservableCollections.observableList(new ArrayList<>());
        taxonomyBindingList.add(TAXONOMY_CV_PARAM_NONE);
        taxonomyBindingList.addAll(cvParamService.findByCvParamByClass(TaxonomyCvParam.class));

        //init binding
        bindingGroup = new BindingGroup();

        JComboBoxBinding taxonomyComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, taxonomyBindingList, fastaDbSaveUpdatePanel.getTaxomomyComboBox());
        bindingGroup.addBinding(taxonomyComboBoxBinding);

        headerParseRuleBindingList = ObservableCollections.observableList(new ArrayList<>());
        try {
            config = PropertiesUtil.parsePropertiesFile("config/header-parse-rule.properties");
        } catch (IOException | ConfigurationException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        populateHeaderParseRuleComboBox();

        JComboBoxBinding parseRuleComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, headerParseRuleBindingList, fastaDbSaveUpdatePanel.getHeaderParseRuleComboBox());
        bindingGroup.addBinding(parseRuleComboBoxBinding);

        databaseBindingList = ObservableCollections.observableList(new ArrayList<>());
        Properties allProperties = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            allProperties.load(loader.getResourceAsStream("config/embl-ebi-database.properties"));
            databaseSet = new TreeSet(allProperties.keySet());
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        populateDatabaseComboBox();

        JComboBoxBinding databaseComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, databaseBindingList, fastaDbSaveUpdatePanel.getDatabaseComboBox());
        bindingGroup.addBinding(databaseComboBoxBinding);
        bindingGroup.bind();

        Path fastasDirectory = Paths.get(fastasPath);
        if (!Files.exists(fastasDirectory)) {
            throw new IllegalArgumentException("The FASTA DB files directory defined in the client properties file " + fastasPath.toString() + " doesn't exist.");
        }

        //init FASTA file selection
        //disable select multiple files
        fastaDbSaveUpdatePanel.getFastaFileChooser().setMultiSelectionEnabled(false);
        //set FASTA file filter
        fastaDbSaveUpdatePanel.getFastaFileChooser().setFileFilter(new FastaFileFilter());

        fastaDbSaveUpdatePanel.getFastaFileChooser().setCurrentDirectory(fastasDirectory.toFile());

        //add listeners
        fastaDbSaveUpdatePanel.getBrowseTaxonomyButton().addActionListener(e -> {
            List<CvParam> cvParams = cvParamService.findByCvParamByClass(TaxonomyCvParam.class);
            //update the CV param list
            cvParamManagementController.updateDialog("FASTA DB taxonomy", TaxonomyCvParam.class, PRESELECTED_ONTOLOGY_NAMESPACES, cvParams);

            cvParamManagementController.showView();
        });

        fastaDbSaveUpdatePanel.getBrowseHeaderParseRuleButtton().addActionListener(l -> {
            headerParseRuleAdditionDialog.getParseRuleTextField().setText("");
            headerParseRuleAdditionDialog.getDescriptionTextField().setText("");
            GuiUtils.centerDialogOnComponent(fastaDbSaveUpdatePanel, headerParseRuleAdditionDialog);
            headerParseRuleAdditionDialog.setVisible(true);
        });

        headerParseRuleAdditionDialog.getSaveParseRuleButton().addActionListener(l -> {
            try {
                config = PropertiesUtil.addProperty(config, headerParseRuleAdditionDialog.getDescriptionTextField().getText(), headerParseRuleAdditionDialog.getParseRuleTextField().getText());
            } catch (ConfigurationException | IOException ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
            populateHeaderParseRuleComboBox();
            headerParseRuleAdditionDialog.dispose();
        });

        fastaDbSaveUpdatePanel.getBrowseFastaButton().addActionListener(e -> {
            //in response to the button click, show open dialog
            int returnVal = fastaDbSaveUpdatePanel.getFastaFileChooser().showOpenDialog(fastaDbSaveUpdatePanel);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    Path fullFastaPath = fastaDbSaveUpdatePanel.getFastaFileChooser().getSelectedFile().toPath();
                    Path relativeFastaPath = PathUtils.getRelativeChildPath(fastasDirectory, fullFastaPath);
                    fastaDbSaveUpdatePanel.getFileNameTextField().setText(relativeFastaPath.getFileName().toString());
                    fastaDbSaveUpdatePanel.getFilePathTextField().setText(relativeFastaPath.toString());
                } catch (IllegalArgumentException ex) {
                    MessageEvent messageEvent = new MessageEvent("Invalid FASTA location", "The FASTA location doesn't contain the fastas directory as defined in the properties file.", JOptionPane.WARNING_MESSAGE);
                    eventBus.post(messageEvent);
                }
            }
        });

        fastaDbSaveUpdatePanel.getSaveOrUpdateButton().addActionListener(e -> {
            //validate FASTA DB
            updateFastaToEdit();
            List<String> validationMessages = GuiUtils.validateEntity(fastaDbToEdit);
            if (validationMessages.isEmpty()) {
                if (fastaDbToEdit.getId() != null) {
                    fastaDbToEdit = fastaDbService.merge(fastaDbToEdit);
                    fastaDbManagementController.updateFastaDb();
                } else {
                    fastaDbService.persist(fastaDbToEdit);
                    fastaDbManagementController.addFastaDb(fastaDbToEdit);
                }
                fastaDbSaveUpdatePanel.getNameTextField().setEnabled(false);
                fastaDbSaveUpdatePanel.getSaveOrUpdateButton().setText("update");
                fastaDbSaveUpdatePanel.getFastaDbStateInfoLabel().setText("");

                saveUpdate = true;
                MessageEvent messageEvent = new MessageEvent("Fasta DB store confirmation", "Fasta DB " + fastaDbToEdit.getName() + " was stored successfully!", JOptionPane.INFORMATION_MESSAGE);
                eventBus.post(messageEvent);

                fastaDbManagementController.showOverviewPanel();
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
        headerParseRuleList.forEach(parseRule -> {
            if (parseRule.containsKey(fastaDb.getHeaderParseRule())) {
                fastaDbSaveUpdatePanel.getHeaderParseRuleComboBox().getModel().setSelectedItem(parseRule.get(fastaDb.getHeaderParseRule()));
            }
        });
        fastaDbSaveUpdatePanel.getDatabaseComboBox().getModel().setSelectedItem(fastaDb.getDatabaseName());
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

        int taxonomyIndex = fastaDbSaveUpdatePanel.getTaxomomyComboBox().getSelectedIndex();
        if (taxonomyIndex == 0) {
            fastaDbToEdit.setTaxonomy(null);
        } else {
            fastaDbToEdit.setTaxonomy((TaxonomyCvParam) taxonomyBindingList.get(taxonomyIndex));
        }

        int parseRuleIndex = fastaDbSaveUpdatePanel.getHeaderParseRuleComboBox().getSelectedIndex();
        if (parseRuleIndex == 0) {
            fastaDbToEdit.setHeaderParseRule(null);
        } else {
            headerParseRuleList.get(parseRuleIndex).forEach((k, v) -> fastaDbToEdit.setHeaderParseRule(k));
        }
        int databaseIndex = fastaDbSaveUpdatePanel.getDatabaseComboBox().getSelectedIndex();
        if (databaseIndex == 0) {
            fastaDbToEdit.setDatabaseName(null);
        } else {
            fastaDbToEdit.setDatabaseName(databaseList.get(databaseIndex));
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
        fastaDbSaveUpdatePanel.getHeaderParseRuleComboBox().setSelectedIndex(0);
        fastaDbSaveUpdatePanel.getDatabaseComboBox().setSelectedIndex(0);
    }

    /**
     * Populate the Header Parse Rule Combo Box.
     */
    private void populateHeaderParseRuleComboBox() {
        headerParseRuleBindingList.clear();
        headerParseRuleList.clear();
        headerParseRuleBindingList.add("Please select one parse rule..");
        Map<String, String> defaultParseRule = new HashMap<>();
        defaultParseRule.put("none", "none");
        headerParseRuleList.add(defaultParseRule);
        // map to hold parse rule and explanation of the rule (key: rule ; value : description).
        // this map is used to combine all explanations of the same rule.
        Map<String, String> parseRulesWithExp = new HashMap<>();

        Iterator<String> keys = config.getKeys();
        while (keys.hasNext()) {
            String key = keys.next();
            if (!parseRulesWithExp.containsKey(config.getString(key))) {
                parseRulesWithExp.put(config.getString(key), key);
            } else {
                parseRulesWithExp.put(config.getString(key), parseRulesWithExp.get(config.getString(key)) + ", " + key);
            }
        }
        List<String> parseRulesFromDb = fastaDbService.getAllParseRules();
        parseRulesFromDb.stream().filter(p -> !parseRulesWithExp.containsKey(p)).forEach(p -> parseRulesWithExp.put(p, "from Db"));
        parseRulesWithExp.entrySet().stream().forEach(entry -> {
            headerParseRuleBindingList.add(entry.getKey() + " [" + entry.getValue() + "]");
            // parse rule map. (key: parse rule, value: rule with explanation)
            Map<String, String> parseRule = new HashMap<>();
            parseRule.put(entry.getKey(), entry.getKey() + " [" + entry.getValue() + "]");
            headerParseRuleList.add(parseRule);
        });

    }

    /**
     * Populate the Database Combo Box.
     */
    private void populateDatabaseComboBox() {
        databaseBindingList.clear();
        databaseList.clear();
        databaseBindingList.add("Please select database..");
        databaseBindingList.add("Not in the EMBL-EBI list");
        databaseList.add("none");
        databaseList.add("N/A");

        databaseSet.forEach(db -> {
            databaseList.add((String) db);
            databaseBindingList.add((String) db);
        });

    }
}

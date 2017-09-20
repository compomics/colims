/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.client.controller.admin;

import com.compomics.colims.client.compoment.TableColumnAdjuster;
import com.compomics.colims.client.controller.AnalyticalRunsAdditionController;
import com.compomics.colims.client.controller.Controllable;
import com.compomics.colims.client.event.EntityChangeEvent;
import com.compomics.colims.client.event.admin.CvParamChangeEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.model.table.model.HeaderParseRuleTestTableModel;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.fasta.FastaDbSaveUpdatePanel;
import com.compomics.colims.client.view.fasta.HeaderParseRuleAdditionDialog;
import com.compomics.colims.client.view.fasta.HeaderParseRuleTestDialog;
import com.compomics.colims.core.distributed.model.enums.PersistType;
import com.compomics.colims.core.io.fasta.FastaDbParser;
import com.compomics.colims.core.service.CvParamService;
import com.compomics.colims.core.service.FastaDbService;
import com.compomics.colims.core.util.PathUtils;
import com.compomics.colims.core.util.PropertiesUtil;
import com.compomics.colims.core.util.ResourceUtils;
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

import static com.compomics.colims.client.controller.admin.FastaDbManagementController.DATABASE_NAME_NOT_PRESENT;
import static com.compomics.colims.client.controller.admin.FastaDbManagementController.NONE;

/**
 * @author demet
 * @author niels
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
     * The list of {@link HeaderParseRule} instances.
     */
    private final List<HeaderParseRule> headerParseRules = new ArrayList<>();
    /**
     * The list of database names.
     */
    private final List<String> databaseNames = new ArrayList<>();
    /**
     * The properties configuration to read properties file.
     */
    private PropertiesConfiguration config;
    /**
     * The FASTAs location as provided in the client properties file.
     */
    @Value("${fastas.path}")
    private String fastasPath = "";
    //model
    private BindingGroup bindingGroup;
    private ObservableList<CvParam> taxonomyBindingList;
    private ObservableList<HeaderParseRule> headerParseRuleBindingList;
    private ObservableList<String> databaseNamesBindingList;
    private boolean saveUpdate = false;
    private FastaDb fastaDbToEdit;
    private final HeaderParseRuleTestTableModel headerParseRuleTestTableModel = new HeaderParseRuleTestTableModel();
    private TableColumnAdjuster tableColumnAdjuster;
    //view
    private FastaDbSaveUpdatePanel fastaDbSaveUpdatePanel;
    private HeaderParseRuleAdditionDialog headerParseRuleAdditionDialog;
    private HeaderParseRuleTestDialog headerParseRuleTestDialog;
    //services
    @Autowired
    private EventBus eventBus;
    @Autowired
    private CvParamService cvParamService;
    @Autowired
    private FastaDbService fastaDbService;
    @Autowired
    private FastaDbParser fastaDbParser;
    @Autowired
    private AnalyticalRunsAdditionController analyticalRunsAdditionController;
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
        headerParseRuleTestDialog = new HeaderParseRuleTestDialog(fastaDbManagementController.getFastaDbManagementDialog(), true);
        taxonomyBindingList = ObservableCollections.observableList(new ArrayList<>());
        taxonomyBindingList.add(TAXONOMY_CV_PARAM_NONE);
        taxonomyBindingList.addAll(cvParamService.findByCvParamByClass(TaxonomyCvParam.class));

        //init binding
        bindingGroup = new BindingGroup();

        JComboBoxBinding taxonomyComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, taxonomyBindingList, fastaDbSaveUpdatePanel.getTaxomomyComboBox());
        bindingGroup.addBinding(taxonomyComboBoxBinding);

        headerParseRuleBindingList = ObservableCollections.observableList(headerParseRules);
        try {
            config = PropertiesUtil.parsePropertiesFile("config/header-parse-rule.properties");
        } catch (IOException | ConfigurationException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        populateHeaderParseRuleComboBox();

        JComboBoxBinding parseRuleComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, headerParseRuleBindingList, fastaDbSaveUpdatePanel.getHeaderParseRuleComboBox());
        bindingGroup.addBinding(parseRuleComboBoxBinding);

        databaseNamesBindingList = ObservableCollections.observableList(databaseNames);
        Properties allProperties = new Properties();
        TreeSet<String> dbNames = new TreeSet<>();
        try {
            allProperties.load(ResourceUtils.getResourceByRelativePath("config/embl-ebi-database.properties").getInputStream());
            dbNames = new TreeSet(allProperties.keySet());
        } catch (IOException ex) {
            MessageEvent messageEvent = new MessageEvent("Database names loading problem", "The database names could not be read from the properties file.", JOptionPane.WARNING_MESSAGE);
            eventBus.post(messageEvent);
        }

        populateDatabaseComboBox(dbNames);

        JComboBoxBinding databaseComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, databaseNamesBindingList, fastaDbSaveUpdatePanel.getDatabaseComboBox());
        bindingGroup.addBinding(databaseComboBoxBinding);
        bindingGroup.bind();

        Path fastasDirectory = Paths.get(fastasPath);
        if (!Files.exists(fastasDirectory)) {
            throw new IllegalArgumentException("The FASTA DB files directory defined in the client properties file " + fastasPath + " doesn't exist.");
        }

        //init FASTA file selection
        //disable select multiple files
        fastaDbSaveUpdatePanel.getFastaFileChooser().setMultiSelectionEnabled(false);
        //set FASTA file filter
        fastaDbSaveUpdatePanel.getFastaFileChooser().setFileFilter(new FastaFileFilter());

        fastaDbSaveUpdatePanel.getFastaFileChooser().setCurrentDirectory(fastasDirectory.toFile());

        //set table model for the headers table
        headerParseRuleTestDialog.getHeadersTable().setModel(headerParseRuleTestTableModel);
        tableColumnAdjuster = new TableColumnAdjuster(headerParseRuleTestDialog.getHeadersTable());

        //add listeners
        fastaDbSaveUpdatePanel.getBrowseTaxonomyButton().addActionListener(e -> {
            List<CvParam> cvParams = cvParamService.findByCvParamByClass(TaxonomyCvParam.class);
            //update the CV param list
            cvParamManagementController.updateDialog("FASTA DB taxonomy", TaxonomyCvParam.class, PRESELECTED_ONTOLOGY_NAMESPACES, cvParams);

            cvParamManagementController.showView();
        });

        fastaDbSaveUpdatePanel.getAddHeaderParseRuleButtton().addActionListener(e -> {
            headerParseRuleAdditionDialog.getParseRuleTextField().setText("");
            headerParseRuleAdditionDialog.getDescriptionTextField().setText("");

            GuiUtils.centerDialogOnComponent(fastaDbSaveUpdatePanel, headerParseRuleAdditionDialog);
            headerParseRuleAdditionDialog.setVisible(true);
        });

        fastaDbSaveUpdatePanel.getTestHeaderParseRuleButtton().addActionListener(e -> {
            updateFastaToEdit();
            //parse the first ten headers of the FASTA file
            Path absoluteFastaDbPath = fastasDirectory.resolve(fastaDbToEdit.getFilePath());
            HeaderParseRule selectedHeaderParseRule = headerParseRules.get(fastaDbSaveUpdatePanel.getHeaderParseRuleComboBox().getSelectedIndex());
            try {
                Map<String, String> headers = fastaDbParser.testParseRule(absoluteFastaDbPath, selectedHeaderParseRule.getParseRule(), 10);
                headerParseRuleTestTableModel.setParsedAccessions(headers);
                headerParseRuleTestTableModel.fireTableDataChanged();
                tableColumnAdjuster.adjustColumns();

                GuiUtils.centerDialogOnComponent(fastaDbSaveUpdatePanel, headerParseRuleTestDialog);
                headerParseRuleTestDialog.setVisible(true);
            } catch (IOException e1) {
                LOGGER.error(e1.getMessage(), e1);
                MessageEvent messageEvent = new MessageEvent("Cannot parse FASTA file", "The FASTA file " + absoluteFastaDbPath + " cannot be parsed.", JOptionPane.WARNING_MESSAGE);
                eventBus.post(messageEvent);
            }
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

        fastaDbSaveUpdatePanel.getBackButton().addActionListener(e -> {
            if (!saveUpdate) {
                fastaDbManagementController.setSelectedFasta(-1);
            }
            saveUpdate = false;
            fastaDbManagementController.showOverviewPanel();
        });

        headerParseRuleAdditionDialog.getSaveParseRuleButton().addActionListener(e -> {
            try {
                config = PropertiesUtil.addProperty(config, headerParseRuleAdditionDialog.getDescriptionTextField().getText(), headerParseRuleAdditionDialog.getParseRuleTextField().getText());
            } catch (ConfigurationException ex) {
                LOGGER.error(ex.getMessage(), ex);
                MessageEvent messageEvent = new MessageEvent("Header parse rule save problem", "Something went wrong while trying to save the header parse rule to the properties file.", JOptionPane.WARNING_MESSAGE);
                eventBus.post(messageEvent);
            }
            populateHeaderParseRuleComboBox();
            headerParseRuleAdditionDialog.dispose();
        });

        headerParseRuleAdditionDialog.getCloseButton().addActionListener(e -> headerParseRuleAdditionDialog.dispose());

        headerParseRuleTestDialog.getCloseButton().addActionListener(e -> headerParseRuleTestDialog.dispose());
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
        if (fastaDb.getVersion() != null) {
            fastaDbSaveUpdatePanel.getVersionTextField().setText(fastaDbToEdit.getVersion());
        } else {
            fastaDbSaveUpdatePanel.getVersionTextField().setText("");
        }

        boolean enableHeaderParseRules = analyticalRunsAdditionController.getSelectedStorageType().equals(PersistType.MAX_QUANT);
        if (enableHeaderParseRules) {
            HeaderParseRule headerParseRule = new HeaderParseRule(fastaDb.getHeaderParseRule());
            if (headerParseRules.contains(headerParseRule)) {
                fastaDbSaveUpdatePanel.getHeaderParseRuleComboBox().getModel().setSelectedItem(headerParseRules.get(headerParseRules.indexOf(headerParseRule)));
            }
        }
        fastaDbSaveUpdatePanel.getHeaderParseRuleComboBox().setEnabled(enableHeaderParseRules);
        fastaDbSaveUpdatePanel.getTestHeaderParseRuleButtton().setEnabled(enableHeaderParseRules);
        fastaDbSaveUpdatePanel.getAddHeaderParseRuleButtton().setEnabled(enableHeaderParseRules);

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
                default:
                    throw new IllegalStateException("Should not be able to get here.");
            }
        }
    }

    /**
     * Update the instance fields of the selected {@link FastaDb} instance in
     * the fastaDb management dialog.
     */
    private void updateFastaToEdit() {
        fastaDbToEdit.setName(fastaDbSaveUpdatePanel.getNameTextField().getText());
        fastaDbToEdit.setFileName(fastaDbSaveUpdatePanel.getFileNameTextField().getText());
        fastaDbToEdit.setFilePath(fastaDbSaveUpdatePanel.getFilePathTextField().getText());

        if (fastaDbSaveUpdatePanel.getVersionTextField().getText().isEmpty() || fastaDbSaveUpdatePanel.getVersionTextField().getText().equals(NONE)) {
            fastaDbToEdit.setVersion(null);
        } else {
            fastaDbToEdit.setVersion(fastaDbSaveUpdatePanel.getVersionTextField().getText());
        }

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
            fastaDbToEdit.setHeaderParseRule(headerParseRules.get(parseRuleIndex).getParseRule());
        }

        int databaseIndex = fastaDbSaveUpdatePanel.getDatabaseComboBox().getSelectedIndex();
        if (databaseIndex == 0) {
            fastaDbToEdit.setDatabaseName(null);

        } else {
            fastaDbToEdit.setDatabaseName(databaseNames.get(databaseIndex));
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
        headerParseRules.clear();
        headerParseRules.add(HeaderParseRule.NONE_RULE);

        //map to hold parse rule and explanation of the rule (key: rule ; value : description).
        //this map is used to combine all explanations of the same rule.
        Map<String, String> parseRuleWithExplanations = new HashMap<>();

        //get header parse rules from the properties file
        Iterator<String> keys = config.getKeys();
        while (keys.hasNext()) {
            String key = keys.next();
            if (!parseRuleWithExplanations.containsKey(config.getString(key))) {
                parseRuleWithExplanations.put(config.getString(key), key);
            } else {
                parseRuleWithExplanations.put(config.getString(key), parseRuleWithExplanations.get(config.getString(key)) + ", " + key);
            }
        }
        //get header parse rules from the database
        List<String> parseRulesFromDb = fastaDbService.getAllParseRules();
        parseRulesFromDb.stream().filter(dbParseRule -> !parseRuleWithExplanations.containsKey(dbParseRule) && dbParseRule != null).forEach(dbParseRule -> parseRuleWithExplanations.put(dbParseRule, "from DB"));

        //add the mapp entries to the headerParseRules list
        parseRuleWithExplanations.entrySet().stream().forEach(entry -> {
            HeaderParseRule headerParseRule = new HeaderParseRule(entry.getKey(), entry.getValue());
            headerParseRules.add(headerParseRule);
        });

    }

    /**
     * Populate the Database Combo Box.
     *
     * @param dbNames the set of database names as read from the properties
     * files
     */
    private void populateDatabaseComboBox(TreeSet<String> dbNames) {
        databaseNamesBindingList.add(DATABASE_NAME_NOT_PRESENT);

        databaseNames.addAll(dbNames);
    }
}

/**
 * Holder class for a parse rule + explanation.
 */
class HeaderParseRule {

    public static final HeaderParseRule NONE_RULE = new HeaderParseRule("none", "none");

    /**
     * The parse rule.
     */
    private final String parseRule;
    /**
     * The explanation.
     */
    private String explanation;

    /**
     * Constructor.
     *
     * @param parseRule the parse rule
     */
    public HeaderParseRule(String parseRule) {
        if (parseRule != null) {
            this.parseRule = parseRule;
        } else {
            this.parseRule = NONE_RULE.parseRule;
            this.explanation = NONE_RULE.explanation;
        }
    }

    /**
     * Constructor.
     *
     * @param parseRule the parse rule
     * @param explanation the explanation
     */
    public HeaderParseRule(String parseRule, String explanation) {
        this.parseRule = parseRule;
        this.explanation = explanation;
    }

    public String getParseRule() {
        return parseRule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        HeaderParseRule that = (HeaderParseRule) o;

        return parseRule != null ? parseRule.equals(that.parseRule) : that.parseRule == null;
    }

    @Override
    public int hashCode() {
        return parseRule != null ? parseRule.hashCode() : 0;
    }

    @Override
    public String toString() {
        return parseRule + " [" + explanation + "]";
    }
}

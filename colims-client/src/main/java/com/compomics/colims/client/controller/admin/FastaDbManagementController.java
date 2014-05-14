package com.compomics.colims.client.controller.admin;

import com.compomics.colims.client.controller.AnalyticalRunSetupController;
import com.compomics.colims.client.controller.Controllable;
import com.compomics.colims.client.event.message.DbConstraintMessageEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.admin.FastaDbManagementDialog;
import com.compomics.colims.core.service.FastaDbService;
import com.compomics.colims.model.FastaDb;
import com.compomics.util.io.filefilters.FastaFileFilter;
import com.google.common.eventbus.EventBus;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import no.uib.olsdialog.OLSDialog;
import static no.uib.olsdialog.OLSDialog.OLS_DIALOG_TERM_ID_SEARCH;
import no.uib.olsdialog.OLSInputable;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("fastaDbManagementController")
public class FastaDbManagementController implements Controllable, OLSInputable {

    private static final Logger LOGGER = Logger.getLogger(FastaDbManagementController.class);

    //model
    private BindingGroup bindingGroup;
    private ObservableList<FastaDb> fastaDbBindingList;
    //view
    private FastaDbManagementDialog fastaDbManagementDialog;
    //parent controller
    @Autowired
    private AnalyticalRunSetupController analyticalRunSetupController;
    //services
    @Autowired
    private EventBus eventBus;
    @Autowired
    private FastaDbService fastaDbService;

    @Override
    public void init() {
        //init view
        fastaDbManagementDialog = new FastaDbManagementDialog(analyticalRunSetupController.getAnalyticalRunSetupDialog(), true);

        //register to event bus
        eventBus.register(this);

        //init binding
        bindingGroup = new BindingGroup();

        fastaDbBindingList = ObservableCollections.observableList(new ArrayList<FastaDb>());
        JListBinding fastaDbListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, fastaDbBindingList, fastaDbManagementDialog.getFastaDbList());
        bindingGroup.addBinding(fastaDbListBinding);

        //user bindings
        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, fastaDbManagementDialog.getFastaDbList(), BeanProperty.create("selectedElement.name"), fastaDbManagementDialog.getNameTextField(), ELProperty.create("${text}"), "nameBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, fastaDbManagementDialog.getFastaDbList(), BeanProperty.create("selectedElement.fileName"), fastaDbManagementDialog.getFileNameTextField(), ELProperty.create("${text}"), "fileNameBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, fastaDbManagementDialog.getFastaDbList(), BeanProperty.create("selectedElement.filePath"), fastaDbManagementDialog.getFilePathTextField(), ELProperty.create("${text}"), "filePathBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, fastaDbManagementDialog.getFastaDbList(), BeanProperty.create("selectedElement.version"), fastaDbManagementDialog.getVersionTextField(), ELProperty.create("${text}"), "versionBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, fastaDbManagementDialog.getFastaDbList(), BeanProperty.create("selectedElement.taxonomyAccession"), fastaDbManagementDialog.getTaxonomyTextField(), ELProperty.create("${text}"), "taxonomyBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, fastaDbManagementDialog.getFastaDbList(), BeanProperty.create("selectedElement.species"), fastaDbManagementDialog.getSpeciesTextField(), ELProperty.create("${text}"), "speciesBinding");
        bindingGroup.addBinding(binding);

        bindingGroup.bind();

        //add listeners
        fastaDbManagementDialog.getBrowseTaxonomyButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                showOlsDialog();
            }
        });

        //init fasta file selection
        //disable select multiple files
        fastaDbManagementDialog.getFastaFileChooser().setMultiSelectionEnabled(false);
        //set fasta file filter
        fastaDbManagementDialog.getFastaFileChooser().setFileFilter(new FastaFileFilter());

        fastaDbManagementDialog.getBrowseFastaButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //in response to the button click, show open dialog 
                int returnVal = fastaDbManagementDialog.getFastaFileChooser().showOpenDialog(fastaDbManagementDialog);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File fastaFile = fastaDbManagementDialog.getFastaFileChooser().getSelectedFile();

                    //show fasta file name and path in textfields
                    fastaDbManagementDialog.getFileNameTextField().setText(fastaFile.getName());
                    fastaDbManagementDialog.getFilePathTextField().setText(fastaFile.getAbsolutePath());
                }
            }
        });

        fastaDbManagementDialog.getFastaDbList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (fastaDbManagementDialog.getFastaDbList().getSelectedIndex() != -1) {
                        FastaDb fastaDb = getSelectedFastaDb();

                        //enable save and delete button
                        fastaDbManagementDialog.getSaveOrUpdateButton().setEnabled(true);
                        fastaDbManagementDialog.getDeleteButton().setEnabled(true);

                        //check if the fasta DB has an ID.
                        //If so, disable the name text field and change the save button label.
                        if (fastaDb.getId() != null) {
                            fastaDbManagementDialog.getNameTextField().setEnabled(false);
                            fastaDbManagementDialog.getSaveOrUpdateButton().setText("update");
                            fastaDbManagementDialog.getFastaDbStateInfoLabel().setText("");
                        } else {
                            fastaDbManagementDialog.getNameTextField().setEnabled(true);
                            fastaDbManagementDialog.getSaveOrUpdateButton().setText("save");
                            fastaDbManagementDialog.getFastaDbStateInfoLabel().setText("This fasta DB hasn't been persisted to the database.");
                        }
                    } else {
                        fastaDbManagementDialog.getSaveOrUpdateButton().setEnabled(false);
                        clearFastaDbDetailFields();
                    }
                }
            }
        });

        fastaDbManagementDialog.getAddButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FastaDb newFastaDb = new FastaDb();
                newFastaDb.setName("name");
                fastaDbBindingList.add(newFastaDb);
                fastaDbManagementDialog.getNameTextField().setEnabled(true);
                fastaDbManagementDialog.getFastaDbList().setSelectedIndex(fastaDbBindingList.size() - 1);
            }
        });

        fastaDbManagementDialog.getDeleteButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fastaDbManagementDialog.getFastaDbList().getSelectedIndex() != -1) {
                    FastaDb fastaDbToDelete = getSelectedFastaDb();

                    //check if fasta DB has an id.
                    //If so, try to delete the fasta DB from the db.
                    if (fastaDbToDelete.getId() != null) {
                        try {
                            fastaDbService.delete(fastaDbToDelete);

                            fastaDbBindingList.remove(fastaDbManagementDialog.getFastaDbList().getSelectedIndex());
                            fastaDbManagementDialog.getFastaDbList().getSelectionModel().clearSelection();
                            //clearInstrumentTypeDetailFields();
                        } catch (DataIntegrityViolationException dive) {
                            //check if the instrument type can be deleted without breaking existing database relations,
                            //i.e. are there any constraints violations
                            if (dive.getCause() instanceof ConstraintViolationException) {
                                DbConstraintMessageEvent dbConstraintMessageEvent = new DbConstraintMessageEvent("instrument type", fastaDbToDelete.getName());
                                eventBus.post(dbConstraintMessageEvent);
                            } else {
                                //pass the exception
                                throw dive;
                            }
                        }
                    } else {
                        fastaDbBindingList.remove(fastaDbManagementDialog.getFastaDbList().getSelectedIndex());
                        fastaDbManagementDialog.getFastaDbList().getSelectionModel().clearSelection();
                        clearFastaDbDetailFields();
                    }
                } else {
                    eventBus.post(new MessageEvent("Fasta DB selection", "Please select a fasta DB to delete.", JOptionPane.INFORMATION_MESSAGE));
                }
            }
        });

        fastaDbManagementDialog.getSaveOrUpdateButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fastaDbManagementDialog.getFastaDbList().getSelectedIndex() != -1) {
                    FastaDb selectedFastaDb = getSelectedFastaDb();
                    //validate fasta DB
                    List<String> validationMessages = GuiUtils.validateEntity(selectedFastaDb);
                    if (validationMessages.isEmpty()) {
                        if (selectedFastaDb.getId() != null) {
                            fastaDbService.update(selectedFastaDb);
                        } else {
                            fastaDbService.save(selectedFastaDb);
                            //refresh fasta DB list
                            fastaDbManagementDialog.getFastaDbList().updateUI();
                        }
                        fastaDbManagementDialog.getNameTextField().setEnabled(false);
                        fastaDbManagementDialog.getSaveOrUpdateButton().setText("update");
                        fastaDbManagementDialog.getFastaDbStateInfoLabel().setText("");

                        MessageEvent messageEvent = new MessageEvent("Fasta DB store confirmation", "Fasta DB " + selectedFastaDb.getName() + " was stored successfully!", JOptionPane.INFORMATION_MESSAGE);
                        eventBus.post(messageEvent);
                    } else {
                        MessageEvent messageEvent = new MessageEvent("Validation failure", validationMessages, JOptionPane.WARNING_MESSAGE);
                        eventBus.post(messageEvent);
                    }
                } else {
                    eventBus.post(new MessageEvent("Fasta DB selection", "Please select a fasta DB to save or update.", JOptionPane.INFORMATION_MESSAGE));
                }
            }
        });

        fastaDbManagementDialog.getOkButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FastaDb fastaDb = getSelectedFastaDb();

                //validate before closing dialog
                List<String> validationMessages = new ArrayList<>();
                if (fastaDb == null) {
                    validationMessages.add("Please select a fasta DB from the list.");
                } else {
                    //validate user input
                    validationMessages.addAll(validate(fastaDb));
                }

                if (validationMessages.isEmpty()) {
                    fastaDbManagementDialog.dispose();
                } else {
                    MessageEvent messageEvent = new MessageEvent("Validation failure", validationMessages, JOptionPane.WARNING_MESSAGE);
                    eventBus.post(messageEvent);
                }
            }
        });

        fastaDbManagementDialog.getCancelButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fastaDbManagementDialog.dispose();
            }
        });
    }

    @Override
    public void showView() {
        //renew fasta DB list
        fastaDbBindingList.clear();
        fastaDbBindingList.addAll(fastaDbService.findAll());

        GuiUtils.centerDialogOnComponent(analyticalRunSetupController.getAnalyticalRunSetupDialog(), fastaDbManagementDialog);
        fastaDbManagementDialog.setVisible(true);
    }

    /**
     *
     * @param field
     * @param selectedValue
     * @param accession
     * @param ontologyShort
     * @param ontologyLong
     * @param modifiedRow
     * @param mappedTerm
     * @param metadata
     */
    @Override
    public void insertOLSResult(String field, String selectedValue, String accession, String ontologyShort, String ontologyLong, int modifiedRow, String mappedTerm, Map<String, String> metadata) {
        fastaDbManagementDialog.getTaxonomyTextField().setText(accession);
        fastaDbManagementDialog.getSpeciesTextField().setText(selectedValue);
    }

    /**
     *
     * @return
     */
    @Override
    public Window getWindow() {
        return fastaDbManagementDialog;
    }

    /**
     * Return the selected fasta DB.
     *
     * @return
     */
    public FastaDb getFastaDb() {
        return getSelectedFastaDb();
    }

    /**
     * Validate the user input and return a list of validation messages.
     *
     * @param fastaDb
     * @return
     */
    private List<String> validate(FastaDb fastaDb) {
        List<String> validationMessages = new ArrayList();

        if (fastaDb.getId() == null) {
            validationMessages.add("You need to save the selected fasta DB before using it.");
        }

        return validationMessages;
    }

    /**
     * Show the OLS dialog.
     *
     */
    private void showOlsDialog() {
        String ontology = "NEWT UniProt Taxonomy Database [NEWT]";

        Map<String, List<String>> preselectedOntologies = new HashMap<>();
        preselectedOntologies.put("NEWT", null);

        String field = "";

        fastaDbManagementDialog.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

        //show new OLS dialog
        new OLSDialog(fastaDbManagementDialog, this, true, field, ontology, -1, null, null, null, OLS_DIALOG_TERM_ID_SEARCH, preselectedOntologies);

        fastaDbManagementDialog.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }

    /**
     * Get the selected fasta DB in the fasta DB JList.
     *
     * @return the selected fasta DB
     */
    private FastaDb getSelectedFastaDb() {
        int selectedIndex = fastaDbManagementDialog.getFastaDbList().getSelectedIndex();
        FastaDb selectedFastaDb = (selectedIndex != -1) ? fastaDbBindingList.get(selectedIndex) : null;
        return selectedFastaDb;
    }

    /**
     * Clear the fasta DB detail fields
     */
    private void clearFastaDbDetailFields() {
        fastaDbManagementDialog.getNameTextField().setText("");
        fastaDbManagementDialog.getFileNameTextField().setText("");
        fastaDbManagementDialog.getFilePathTextField().setText("");
        fastaDbManagementDialog.getVersionTextField().setText("");
        fastaDbManagementDialog.getTaxonomyTextField().setText("");
        fastaDbManagementDialog.getSpeciesTextField().setText("");
    }

}

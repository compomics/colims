package com.compomics.colims.client.controller.admin;

import com.compomics.colims.client.controller.Controllable;
import com.compomics.colims.client.controller.MainController;
import com.compomics.colims.client.event.admin.TypedCvParamChangeEvent;
import com.compomics.colims.client.event.message.DbConstraintMessageEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.model.table.model.TypedCvParamTableModel2;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.admin.CvParamManagementDialog;
import com.compomics.colims.core.ontology.ols.OntologyTerm;
import com.compomics.colims.core.service.AuditableTypedCvParamService;
import com.compomics.colims.core.service.OlsService;
import com.compomics.colims.model.cv.AuditableTypedCvParam;
import com.compomics.colims.model.enums.CvParamType;
import com.compomics.colims.model.factory.CvParamFactory;
import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.io.IOException;
import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Component("typedCvParamManagementController")
@Lazy
public class TypedCvParamManagementController implements Controllable {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TypedCvParamManagementController.class);

    private static final String DIALOG_TITLE_SUFFIX = " ontology terms management";

    //model
    private TypedCvParamTableModel2 typeCvParamTableModel2;
    /**
     * The cvParamType of the CV params in the table model.
     */
    private CvParamType cvParamType;
    /**
     * Boolean that keeps track of the CV param state that is passed to the OLS
     * dialog. It can be a new CV param (true) or one that needs to be updated
     * (false).
     */
    private boolean newCvParam;
    /**
     * The list namespaces of ontologies that will be preselected in the OLS
     * dialog.
     */
    private List<String> preselectedOntologyNamespaces;
    //view
    private CvParamManagementDialog cvParamManagementDialog;
    //parent controller
    @Autowired
    private MainController mainController;
    //child controller
    @Autowired
    private OlsController olsController;
    @Autowired
    private EventBus eventBus;
    //services
    @Autowired
    private AuditableTypedCvParamService cvParamService;
    @Autowired
    private OlsService olsService;

    @Override
    @PostConstruct
    public void init() {
        //init view
        cvParamManagementDialog = new CvParamManagementDialog(mainController.getMainFrame(), true);

        //init and set table model
        typeCvParamTableModel2 = new TypedCvParamTableModel2();
        cvParamManagementDialog.getCvParamTable().setModel(typeCvParamTableModel2);

        //add listeners
        cvParamManagementDialog.getCvParamTable().getSelectionModel().addListSelectionListener(lse -> {
            if (!lse.getValueIsAdjusting()) {
                int selectedRow = cvParamManagementDialog.getCvParamTable().getSelectedRow();
                if (selectedRow != -1 && !typeCvParamTableModel2.getCvParams().isEmpty()) {
                    AuditableTypedCvParam selectedCvParam = typeCvParamTableModel2.getCvParams().get(selectedRow);

                    //check if the CV param has an ID.
                    //If so, change the save button text and the info state label.
                    if (selectedCvParam.getId() != null) {
                        cvParamManagementDialog.getSaveOrUpdateButton().setText("update");
                        cvParamManagementDialog.getCvParamStateInfoLabel().setText("");
                    } else {
                        cvParamManagementDialog.getSaveOrUpdateButton().setText("save");
                        cvParamManagementDialog.getCvParamStateInfoLabel().setText("This CV param hasn't been stored in the database.");
                    }

                    //set details fields
                    cvParamManagementDialog.getOntologyLabelTextField().setText(selectedCvParam.getLabel());
                    cvParamManagementDialog.getAccessionTextField().setText(selectedCvParam.getAccession());
                    cvParamManagementDialog.getNameTextField().setText(selectedCvParam.getName());

                    //reset definition text area
                    cvParamManagementDialog.getDefinitionTextArea().setText("");
                    try {
                        //get the descripton from the OLS service
                        cvParamManagementDialog.getDefinitionTextArea().setText(
                                olsService.getTermDescriptionByOboId(selectedCvParam.getLabel(), selectedCvParam.getAccession()));
                    } catch (RestClientException | IOException ex) {
                        LOGGER.error(ex.getMessage(), ex);
                        cvParamManagementDialog.getDefinitionTextArea().setText("");
                    }
                } else {
                    clearCvParamDetailFields();
                }
            }
        });

        cvParamManagementDialog.getAddCvParamButton().addActionListener(e -> {
            newCvParam = true;
            showOlsDialog();
        });

        cvParamManagementDialog.getSaveOrUpdateButton().addActionListener(e -> {
            if (cvParamManagementDialog.getCvParamTable().getSelectedRow() != -1) {
                AuditableTypedCvParam selectedCvParam = getSelectedCvParam();
                //validate CV param
                List<String> validationMessages = GuiUtils.validateEntity(selectedCvParam);
                //check for a new CV param if the accession already exists in the db
                if (selectedCvParam.getId() == null && isExistingCvParamAccession(selectedCvParam)) {
                    validationMessages.add(selectedCvParam.getAccession() + " already exists in the database.");
                }
                if (validationMessages.isEmpty()) {
                    if (selectedCvParam.getId() != null) {
                        selectedCvParam = cvParamService.merge(selectedCvParam);
                    } else {
                        cvParamService.persist(selectedCvParam);
                    }
                    cvParamManagementDialog.getSaveOrUpdateButton().setText("update");
                    cvParamManagementDialog.getCvParamStateInfoLabel().setText("");

                    MessageEvent messageEvent = new MessageEvent("CV param store confirmation", "CV param " + selectedCvParam.getName() + " was stored successfully!", JOptionPane.INFORMATION_MESSAGE);
                    eventBus.post(messageEvent);

                    eventBus.post(new TypedCvParamChangeEvent());
                } else {
                    MessageEvent messageEvent = new MessageEvent("Validation failure", validationMessages, JOptionPane.WARNING_MESSAGE);
                    eventBus.post(messageEvent);
                }
            } else {
                eventBus.post(new MessageEvent("CV param selection", "Please select a CV param to save.", JOptionPane.INFORMATION_MESSAGE));
            }
        });

        cvParamManagementDialog.getDeleteCvParamButton().addActionListener(e -> {
            int selectedIndex = cvParamManagementDialog.getCvParamTable().getSelectedRow();

            if (selectedIndex != -1) {
                AuditableTypedCvParam cvparamToDelete = getSelectedCvParam();
                //check if instrument type has an id.
                //If so, try to delete the permission from the db.
                if (cvparamToDelete.getId() != null) {
                    try {
                        cvParamService.remove(cvparamToDelete);

                        typeCvParamTableModel2.removeCvParam(selectedIndex);
                        cvParamManagementDialog.getCvParamTable().getSelectionModel().clearSelection();

                        eventBus.post(new TypedCvParamChangeEvent());
                    } catch (DataIntegrityViolationException dive) {
                        //check if the CV param can be deleted without breaking existing database relations,
                        //i.e. are there any constraints violations
                        if (dive.getCause() instanceof ConstraintViolationException) {
                            DbConstraintMessageEvent dbConstraintMessageEvent = new DbConstraintMessageEvent("CV term", cvparamToDelete.getName());
                            eventBus.post(dbConstraintMessageEvent);
                        } else {
                            //pass the exception
                            throw dive;
                        }
                    }
                } else {
                    typeCvParamTableModel2.removeCvParam(selectedIndex);
                    cvParamManagementDialog.getCvParamTable().getSelectionModel().clearSelection();
                }
            } else {
                eventBus.post(new MessageEvent("CV param selection", "Please select a CV param to delete.", JOptionPane.INFORMATION_MESSAGE));
            }
        });

        cvParamManagementDialog.getEditUsingOlsCvParamButton().addActionListener(e -> {
            if (cvParamManagementDialog.getCvParamTable().getSelectedRow() != -1) {
                newCvParam = false;
                showOlsDialog();
            } else {
                eventBus.post(new MessageEvent("CV param selection", "Please select a CV param to edit.", JOptionPane.INFORMATION_MESSAGE));
            }
        });

        cvParamManagementDialog.getCloseButton().addActionListener(e -> cvParamManagementDialog.dispose());
    }

    @Override
    public void showView() {
        //clear selection
        cvParamManagementDialog.getCvParamTable().getSelectionModel().clearSelection();

        GuiUtils.centerDialogOnComponent(mainController.getMainFrame(), cvParamManagementDialog);
        cvParamManagementDialog.setVisible(true);
    }

    /**
     * Update the CV param list and set the current cvParamType.
     *
     * @param cvParamParent the CV params' parent
     * @param cvParamType the cvParamType of the CV params in the list
     * @param preselectedOntologyNamespaces the list of preselected ontology
     * namespaces
     * @param cvParams the list of CV params
     */
    public void updateDialog(final String cvParamParent, final CvParamType cvParamType, final List<String> preselectedOntologyNamespaces, final List<AuditableTypedCvParam> cvParams) {
        this.cvParamType = cvParamType;
        this.preselectedOntologyNamespaces = preselectedOntologyNamespaces;

        typeCvParamTableModel2.setCvParams(cvParams);

        //set the title
        cvParamManagementDialog.setTitle(cvParamParent + " " + cvParamType.name() + DIALOG_TITLE_SUFFIX);

        //clear selection
        cvParamManagementDialog.getCvParamTable().getSelectionModel().clearSelection();
    }

    /**
     * Check if the CV param with the accession exists in the database.
     *
     * @param cvParam the selected CV param
     * @return the does exist boolean
     */
    private boolean isExistingCvParamAccession(final AuditableTypedCvParam cvParam) {
        boolean isExistingCvParamAccession = true;
        AuditableTypedCvParam foundCvParam = cvParamService.findByAccession(cvParam.getAccession(), cvParam.getCvParamType());
        if (foundCvParam == null) {
            isExistingCvParamAccession = false;
        }

        return isExistingCvParamAccession;
    }

    /**
     * Update the given CV param. Only the modified fields are set.
     *
     * @param cvParam the TypedCvParam
     * @param label the label
     * @param accession the accession
     * @param name the name
     */
    private void updateCvParam(final AuditableTypedCvParam cvParam, final String label, final String accession, final String name) {

        if (!cvParam.getLabel().equalsIgnoreCase(label)) {
            cvParam.setLabel(label);
        }
        if (!cvParam.getAccession().equalsIgnoreCase(accession)) {
            cvParam.setAccession(accession);
        }
        if (!cvParam.getName().equalsIgnoreCase(name)) {
            cvParam.setName(name);
        }
    }

    /**
     * Show the OLS dialog.
     */
    private void showOlsDialog() {

        /**
         * The ontology term instance that is passed to the OLS controller for
         * storing the result of the OLS search.
         */
        OntologyTerm ontologyTerm = new OntologyTerm();
        olsController.showView(ontologyTerm, preselectedOntologyNamespaces);

        if (ontologyTerm.getIri() != null && !ontologyTerm.getIri().equals(OlsController.DEREFERENCE_IRI)) {
            //check whether a CV param has to be added or updated
            if (newCvParam) {
                AuditableTypedCvParam cvParam = CvParamFactory.newAuditableTypedCvInstance(cvParamType, ontologyTerm.getOntologyNamespace(), ontologyTerm.getOboId(), ontologyTerm.getLabel());

                //add CV param to the table model
                typeCvParamTableModel2.addCvParam(cvParam);

                //set selected index to newly added CV param
                cvParamManagementDialog.getCvParamTable().getSelectionModel().setSelectionInterval(typeCvParamTableModel2.getRowCount() - 1, typeCvParamTableModel2.getRowCount() - 1);
            } else {
                //update selected CV param
                AuditableTypedCvParam selectedCvParam = getSelectedCvParam();
                updateCvParam(selectedCvParam, ontologyTerm.getOntologyPrefix(), ontologyTerm.getOboId(), ontologyTerm.getLabel());

                //update CV param in table model
                int selectedIndex = cvParamManagementDialog.getCvParamTable().getSelectedRow();
                typeCvParamTableModel2.updateCvParam(selectedCvParam, selectedIndex);

                //clear selection and set selected index again
                cvParamManagementDialog.getCvParamTable().getSelectionModel().clearSelection();
                cvParamManagementDialog.getCvParamTable().getSelectionModel().setSelectionInterval(selectedIndex, selectedIndex);
            }
        }
    }

    /**
     * Get the selected CV param in the CV param table. Returns null if none was
     * selected.
     *
     * @return the selected CV param
     */
    private AuditableTypedCvParam getSelectedCvParam() {
        int selectedCvParamIndex = cvParamManagementDialog.getCvParamTable().getSelectedRow();

        return (selectedCvParamIndex != -1) ? typeCvParamTableModel2.getCvParams().get(selectedCvParamIndex) : null;
    }

    /**
     * Clear the CV param details fields.
     */
    private void clearCvParamDetailFields() {
        cvParamManagementDialog.getCvParamStateInfoLabel().setText("");
        cvParamManagementDialog.getOntologyLabelTextField().setText("");
        cvParamManagementDialog.getAccessionTextField().setText("");
        cvParamManagementDialog.getNameTextField().setText("");
        cvParamManagementDialog.getDefinitionTextArea().setText("");
    }

}

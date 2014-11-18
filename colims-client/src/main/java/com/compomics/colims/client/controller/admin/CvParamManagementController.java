package com.compomics.colims.client.controller.admin;

import com.compomics.colims.client.controller.Controllable;
import com.compomics.colims.client.controller.ColimsController;
import com.compomics.colims.client.event.admin.CvParamChangeEvent;
import com.compomics.colims.client.event.message.DbConstraintMessageEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.model.TypedCvParamTableModel2;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.admin.CvParamManagementDialog;
import com.compomics.colims.core.service.AuditableTypedCvParamService;
import com.compomics.colims.model.cv.AuditableTypedCvParam;
import com.compomics.colims.model.enums.CvParamType;
import com.compomics.colims.model.factory.CvParamFactory;
import com.google.common.eventbus.EventBus;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import no.uib.olsdialog.OLSDialog;
import no.uib.olsdialog.OLSInputable;
import org.apache.xml.xml_soap.MapItem;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("cvParamManagementController")
public class CvParamManagementController implements Controllable, OLSInputable {

    private static final String ADD_CV_PARAM = "add";
    private static final String UPDATE_CV_PARAM = "update";
    //model
    private TypedCvParamTableModel2 typeCvParamTableModel2;
    /**
     * The cvParamType of the CV params in the table model.
     */
    private CvParamType cvParamType;
    //view
    private CvParamManagementDialog cvParamManagementDialog;
    //parent controller
    @Autowired
    private ColimsController colimsController;
    @Autowired
    private EventBus eventBus;
    //services
    @Autowired
    private AuditableTypedCvParamService cvParamService;
    @Autowired
    private uk.ac.ebi.ontology_lookup.ontologyquery.Query olsClient;

    /**
     * Get the view of this controller.
     *
     * @return the CvParamManagementDialog
     */
    public CvParamManagementDialog getCvParamManagementDialog() {
        return cvParamManagementDialog;
    }

    @Override
    public void init() {
        //init view
        cvParamManagementDialog = new CvParamManagementDialog(colimsController.getColimsFrame(), true);

        //register to event bus
        //eventBus.register(this);
        //init and set table model
        typeCvParamTableModel2 = new TypedCvParamTableModel2();
        cvParamManagementDialog.getCvParamTable().setModel(typeCvParamTableModel2);

        //add listeners
        cvParamManagementDialog.getCvParamTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(final ListSelectionEvent lse) {
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
                        cvParamManagementDialog.getOntologyTextField().setText(selectedCvParam.getOntology());
                        cvParamManagementDialog.getOntologyLabelTextField().setText(selectedCvParam.getLabel());
                        cvParamManagementDialog.getAccessionTextField().setText(selectedCvParam.getAccession());
                        cvParamManagementDialog.getNameTextField().setText(selectedCvParam.getName());

                        //reset definition text area
                        cvParamManagementDialog.getDefinitionTextArea().setText("");
                        //get param definition from ols service
                        org.apache.xml.xml_soap.Map termMetadata = olsClient.getTermMetadata(selectedCvParam.getAccession(), selectedCvParam.getLabel());
                        if (termMetadata != null && !termMetadata.getItem().isEmpty()) {
                            for (MapItem mapItem : termMetadata.getItem()) {
                                //look for definition item
                                if (mapItem.getKey().equals("definition") && mapItem.getValue() != null) {
                                    cvParamManagementDialog.getDefinitionTextArea().setText(mapItem.getValue().toString());
                                }
                            }
                        }
                    } else {
                        clearCvParamDetailFields();
                    }
                }
            }
        });

        cvParamManagementDialog.getAddCvParamButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                showOlsDialog(true);
            }
        });

        cvParamManagementDialog.getSaveOrUpdateButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
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
                            cvParamService.update(selectedCvParam);
                        } else {
                            cvParamService.save(selectedCvParam);
                        }
                        cvParamManagementDialog.getSaveOrUpdateButton().setText("update");
                        cvParamManagementDialog.getCvParamStateInfoLabel().setText("");

                        MessageEvent messageEvent = new MessageEvent("CV param store confirmation", "CV param " + selectedCvParam.getName() + " was stored successfully!", JOptionPane.INFORMATION_MESSAGE);
                        eventBus.post(messageEvent);

                        eventBus.post(new CvParamChangeEvent());
                    } else {
                        MessageEvent messageEvent = new MessageEvent("Validation failure", validationMessages, JOptionPane.WARNING_MESSAGE);
                        eventBus.post(messageEvent);
                    }
                } else {
                    eventBus.post(new MessageEvent("CV param selection", "Please select a CV param to save.", JOptionPane.INFORMATION_MESSAGE));
                }
            }
        });

        cvParamManagementDialog.getDeleteCvParamButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                int selectedIndex = cvParamManagementDialog.getCvParamTable().getSelectedRow();

                if (selectedIndex != -1) {
                    AuditableTypedCvParam cvparamToDelete = getSelectedCvParam();
                //check if instrument type has an id.
                    //If so, try to delete the permission from the db.
                    if (cvparamToDelete.getId() != null) {
                        try {
                            cvParamService.delete(cvparamToDelete);

                            typeCvParamTableModel2.removeCvParam(selectedIndex);
                            cvParamManagementDialog.getCvParamTable().getSelectionModel().clearSelection();

                            eventBus.post(new CvParamChangeEvent());
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
            }
        });

        cvParamManagementDialog.getEditUsingOlsCvParamButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (cvParamManagementDialog.getCvParamTable().getSelectedRow() != -1) {
                    showOlsDialog(false);
                } else {
                    eventBus.post(new MessageEvent("CV param selection", "Please select a CV param to edit.", JOptionPane.INFORMATION_MESSAGE));
                }
            }
        });

        cvParamManagementDialog.getCancelButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                cvParamManagementDialog.dispose();
            }
        });
    }

    @Override
    public void showView() {
        //clear selection
        cvParamManagementDialog.getCvParamTable().getSelectionModel().clearSelection();

        GuiUtils.centerDialogOnComponent(colimsController.getColimsFrame(), cvParamManagementDialog);
        cvParamManagementDialog.setVisible(true);
    }

    /**
     * Update the CV param list and set the current cvParamType.
     *
     * @param cvParamType the cvParamType of the CV params in the list
     * @param cvParams the list of CV params
     */
    public void updateDialog(final CvParamType cvParamType, final List<AuditableTypedCvParam> cvParams) {
        this.cvParamType = cvParamType;

        typeCvParamTableModel2.setCvParams(cvParams);

        //clear selection
        cvParamManagementDialog.getCvParamTable().getSelectionModel().clearSelection();
    }

    @Override
    public void insertOLSResult(final String field, final String selectedValue, final String accession, final String ontologyShort, final String ontologyLong, int modifiedRow, final String mappedTerm, final Map<String, String> metadata) {
        //check wether a CV param has to be added or updated
        if (field.equals(ADD_CV_PARAM)) {
            AuditableTypedCvParam cvParam = CvParamFactory.newAuditableTypeCvInstance(cvParamType, ontologyLong, ontologyShort, accession, selectedValue);

            //add CV param to the table model
            typeCvParamTableModel2.addCvParam(cvParam);

            //set selected index to newly added CV param
            cvParamManagementDialog.getCvParamTable().getSelectionModel().setSelectionInterval(typeCvParamTableModel2.getRowCount() - 1, typeCvParamTableModel2.getRowCount() - 1);
        } else {
            //update selected CV param
            AuditableTypedCvParam selectedCvParam = getSelectedCvParam();
            updateCvParam(selectedCvParam, ontologyLong, ontologyShort, accession, selectedValue);

            //update CV param in table model
            int selectedIndex = cvParamManagementDialog.getCvParamTable().getSelectedRow();
            typeCvParamTableModel2.updateCvParam(selectedCvParam, selectedIndex);

            //clear selection and set selected index again
            cvParamManagementDialog.getCvParamTable().getSelectionModel().clearSelection();
            cvParamManagementDialog.getCvParamTable().getSelectionModel().setSelectionInterval(selectedIndex, selectedIndex);
        }
    }

    @Override
    public Window getWindow() {
        return cvParamManagementDialog;
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
     * @param ontology the ontology
     * @param label the label
     * @param accession the accession
     * @param name the name
     */
    private void updateCvParam(final AuditableTypedCvParam cvParam, final String ontology, final String label, final String accession, final String name) {
        if (!cvParam.getOntology().equalsIgnoreCase(ontology)) {
            cvParam.setOntology(ontology);
        }
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
     *
     * @param isNewCvParam is the CV param new or did already exist in the DB
     */
    private void showOlsDialog(final boolean isNewCvParam) {
        String ontology = "PSI Mass Spectrometry Ontology [MS]";

        Map<String, List<String>> preselectedOntologies = new HashMap<>();
        preselectedOntologies.put("MS", null);

        String field;
        String param = null;

        if (isNewCvParam) {
            field = ADD_CV_PARAM;
        } else {
            field = UPDATE_CV_PARAM;
            param = getSelectedCvParam().getName();
        }

        cvParamManagementDialog.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

        //show new OLS dialog
        new OLSDialog(cvParamManagementDialog, this, true, field, ontology, param, preselectedOntologies);

        cvParamManagementDialog.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }

    /**
     * Get the selected CV param in the CV param table. Returns null if none was
     * selected.
     *
     * @return the selected CV param
     */
    private AuditableTypedCvParam getSelectedCvParam() {
        int selectedCvParamIndex = cvParamManagementDialog.getCvParamTable().getSelectedRow();
        AuditableTypedCvParam selectedCvParam = (selectedCvParamIndex != -1) ? typeCvParamTableModel2.getCvParams().get(selectedCvParamIndex) : null;

        return selectedCvParam;
    }

    /**
     * Clear the CV param details fields.
     */
    private void clearCvParamDetailFields() {
        cvParamManagementDialog.getCvParamStateInfoLabel().setText("");
        cvParamManagementDialog.getOntologyTextField().setText("");
        cvParamManagementDialog.getOntologyLabelTextField().setText("");
        cvParamManagementDialog.getAccessionTextField().setText("");
        cvParamManagementDialog.getNameTextField().setText("");
        cvParamManagementDialog.getDefinitionTextArea().setText("");
    }

}

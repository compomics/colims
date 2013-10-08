package com.compomics.colims.client.controller.admin;

import com.compomics.colims.client.controller.MainController;
import com.compomics.colims.client.event.MessageEvent;
import com.compomics.colims.client.model.CvTermWithoutTypeTableModel;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.admin.CvTermManagementDialog;
import com.compomics.colims.core.service.CvTermService;
import com.compomics.colims.model.CvTerm;
import com.compomics.colims.model.enums.CvTermType;
import com.compomics.colims.model.factory.CvTermFactory;
import com.google.common.eventbus.EventBus;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import no.uib.olsdialog.OLSDialog;
import no.uib.olsdialog.OLSInputable;
import org.apache.xml.xml_soap.MapItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ebi.ontology_lookup.ontologyquery.Query;

/**
 *
 * @author Niels Hulstaert
 */
@Component("cvTermManagementController")
public class CvTermManagementController implements OLSInputable {

    private static final String ADD_CV_TERM = "add";
    private static final String UPDATE_CV_TERM = "update";
    //model
    private CvTermWithoutTypeTableModel cvTermWithoutTypeTableModel;
    /**
     * The cvTermType of the CV terms in the table model.
     */
    private CvTermType cvTermType;
    //view
    private CvTermManagementDialog cvTermManagementDialog;
    //parent controller
    @Autowired
    private MainController mainController;
    @Autowired
    private EventBus eventBus;
    //services
    @Autowired
    private CvTermService cvTermService;
    @Autowired
    private Query olsClient;

    public CvTermManagementDialog getCvTermManagementDialog() {
        return cvTermManagementDialog;
    }

    public void init() {
        //get view
        cvTermManagementDialog = new CvTermManagementDialog(mainController.getMainFrame(), true);

        //register to event bus
        eventBus.register(this);

        //init and set table model
        cvTermWithoutTypeTableModel = new CvTermWithoutTypeTableModel();
        cvTermManagementDialog.getCvTermTable().setModel(cvTermWithoutTypeTableModel);

        //add listeners
        cvTermManagementDialog.getCvTermTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (!lse.getValueIsAdjusting()) {
                    int selectedRow = cvTermManagementDialog.getCvTermTable().getSelectedRow();
                    if (selectedRow != -1 && !cvTermWithoutTypeTableModel.getCvTerms().isEmpty()) {
                        CvTerm selectedCvTerm = cvTermWithoutTypeTableModel.getCvTerms().get(selectedRow);

                        //check if the CV term has an ID.
                        //If so, change the save button text and the info state label.
                        if (selectedCvTerm.getId() != null) {
                            cvTermManagementDialog.getSaveOrUpdateButton().setText("update");
                            cvTermManagementDialog.getCvTermStateInfoLabel().setText("");
                        } else {
                            cvTermManagementDialog.getSaveOrUpdateButton().setText("save");
                            cvTermManagementDialog.getCvTermStateInfoLabel().setText("This CV term hasn't been persisted to the database.");
                        }

                        //set details fields
                        cvTermManagementDialog.getOntologyTextField().setText(selectedCvTerm.getOntology());
                        cvTermManagementDialog.getOntologyLabelTextField().setText(selectedCvTerm.getLabel());
                        cvTermManagementDialog.getAccessionTextField().setText(selectedCvTerm.getAccession());
                        cvTermManagementDialog.getNameTextField().setText(selectedCvTerm.getName());

                        //get term definition from ols service
                        org.apache.xml.xml_soap.Map termMetadata = olsClient.getTermMetadata(selectedCvTerm.getAccession(), selectedCvTerm.getLabel());
                        if (termMetadata != null && !termMetadata.getItem().isEmpty()) {
                            for (MapItem mapItem : termMetadata.getItem()) {
                                //look for definition item
                                if (mapItem.getKey().equals("definition")) {
                                    cvTermManagementDialog.getDefinitionTextArea().setText(mapItem.getValue().toString());
                                }
                            }
                        } else {
                            clearCvTermDetailFields();
                        }
                    }
                }
            }
        });

        cvTermManagementDialog.getAddCvTermButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showOlsDialog(true);
            }
        });

        cvTermManagementDialog.getDeleteCvTermButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CvTerm selectedCvTerm = getSelectedCvTerm();
                
                cvTermService.delete(selectedCvTerm);
            }
        });

        cvTermManagementDialog.getSaveOrUpdateButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CvTerm selectedCvTerm = getSelectedCvTerm();
                //validate CV term
                List<String> validationMessages = GuiUtils.validateEntity(selectedCvTerm);
                //check for a new CV term if the accession already exists in the db                
                if (selectedCvTerm.getId() == null && isExistingCvTermAccession(selectedCvTerm)) {
                    validationMessages.add(selectedCvTerm.getAccession() + " already exists in the database.");
                }
                if (validationMessages.isEmpty()) {
                    if (selectedCvTerm.getId() != null) {
                        cvTermService.update(selectedCvTerm);
                    } else {
                        cvTermService.save(selectedCvTerm);
                    }
                    cvTermManagementDialog.getSaveOrUpdateButton().setText("update");
                    cvTermManagementDialog.getCvTermStateInfoLabel().setText("");

                    MessageEvent messageEvent = new MessageEvent("Cv term save confirmation", "CV term " + selectedCvTerm.getName() + " was persisted successfully!", JOptionPane.INFORMATION_MESSAGE);
                    eventBus.post(messageEvent);
                } else {
                    MessageEvent messageEvent = new MessageEvent("Validation failure", validationMessages, JOptionPane.ERROR_MESSAGE);
                    eventBus.post(messageEvent);
                }
            }
        });

        cvTermManagementDialog.getEditUsingOlsCvTermButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showOlsDialog(false);
            }
        });
    }

    /**
     * Update the CV term list and set the current cvTermType.
     *
     * @param cvTermType the cvTermType of the CV terms in the list
     * @param cvTerms the list of CV terms
     */
    public void updateDialog(CvTermType cvTermType, List<CvTerm> cvTerms) {
        this.cvTermType = cvTermType;

        //clear table model and detail field
        cvTermWithoutTypeTableModel.setCvTerms(new ArrayList<CvTerm>());

        clearCvTermDetailFields();

        cvTermWithoutTypeTableModel.setCvTerms(cvTerms);
    }

    @Override
    public void insertOLSResult(String field, String selectedValue, String accession, String ontologyShort, String ontologyLong, int modifiedRow, String mappedTerm, Map<String, String> metadata) {
        //check wether a CV term has to be added or updated
        if (field.equals(ADD_CV_TERM)) {
            CvTerm cvTerm = CvTermFactory.newInstance(cvTermType, ontologyLong, ontologyShort, accession, selectedValue);

            //add CV term to the table model
            cvTermWithoutTypeTableModel.addCvTerm(cvTerm);

            //set selected index to newly added CV term
            cvTermManagementDialog.getCvTermTable().getSelectionModel().setSelectionInterval(cvTermWithoutTypeTableModel.getRowCount() - 1, cvTermWithoutTypeTableModel.getRowCount() - 1);
        } else {
            //update selected CV term
            CvTerm selectedCvTerm = getSelectedCvTerm();
            updateCvTerm(selectedCvTerm, ontologyLong, ontologyShort, accession, selectedValue);

            //update CV term in table model
            int selectedIndex = cvTermManagementDialog.getCvTermTable().getSelectedRow();
            cvTermWithoutTypeTableModel.updateCvTerm(selectedCvTerm, selectedIndex);

            //clear selection and set selected index again
            cvTermManagementDialog.getCvTermTable().getSelectionModel().clearSelection();
            cvTermManagementDialog.getCvTermTable().getSelectionModel().setSelectionInterval(selectedIndex, selectedIndex);
        }

    }

    @Override
    public Window getWindow() {
        return cvTermManagementDialog;
    }

    /**
     * Check if the CV term with the accession exists in the database
     *
     * @param cvTerm the selected CV term
     * @return the does exist boolean
     */
    private boolean isExistingCvTermAccession(CvTerm cvTerm) {
        boolean isExistingCvTermAccession = true;
        CvTerm foundCvTerm = cvTermService.findByAccession(cvTerm.getAccession(), cvTerm.getcvTermType());
        if (foundCvTerm == null) {
            isExistingCvTermAccession = false;
        }

        return isExistingCvTermAccession;
    }

    /**
     * Update the given CV term. Only the modifified fields are set.
     *
     * @param cvTerm
     * @param ontology
     * @param label
     * @param accession
     * @param name
     */
    private void updateCvTerm(CvTerm cvTerm, String ontology, String label, String accession, String name) {
        if (!cvTerm.getOntology().equalsIgnoreCase(ontology)) {
            cvTerm.setOntology(ontology);
        }
        if (!cvTerm.getLabel().equalsIgnoreCase(label)) {
            cvTerm.setLabel(label);
        }
        if (!cvTerm.getAccession().equalsIgnoreCase(accession)) {
            cvTerm.setAccession(accession);
        }
        if (!cvTerm.getName().equalsIgnoreCase(name)) {
            cvTerm.setName(name);
        }
    }

    /**
     * Show the OLS dialog.
     *
     * @param isNewCvTerm is the CV term new or did already exist in the DB
     */
    private void showOlsDialog(boolean isNewCvTerm) {
        String ontology = "PSI Mass Spectrometry Ontology [MS]";

        Map<String, List<String>> preselectedOntologies = new HashMap<>();
        preselectedOntologies.put("MS", null);

        String field = (isNewCvTerm) ? ADD_CV_TERM : UPDATE_CV_TERM;

        //show new OLS dialog
        new OLSDialog(cvTermManagementDialog, this, true, field, ontology, null, preselectedOntologies);
    }

    /**
     * Get the selected CV term in the CV term table.
     *
     * @return the selected CV term
     */
    private CvTerm getSelectedCvTerm() {
        int selectedCvTermIndex = cvTermManagementDialog.getCvTermTable().getSelectedRow();
        CvTerm selectedCvTerm = (selectedCvTermIndex != -1) ? cvTermWithoutTypeTableModel.getCvTerms().get(selectedCvTermIndex) : null;

        return selectedCvTerm;
    }

    /**
     * Clear the dialog; clear the detail fields and the selected row in the
     * overview table.
     *
     */
    private void clearCvTermDetailFields() {


        cvTermManagementDialog.getOntologyTextField().setText("");
        cvTermManagementDialog.getOntologyLabelTextField().setText("");
        cvTermManagementDialog.getAccessionTextField().setText("");
        cvTermManagementDialog.getNameTextField().setText("");
        cvTermManagementDialog.getDefinitionTextArea().setText("");
    }
}

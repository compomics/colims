package com.compomics.colims.client.controller.admin;

import com.compomics.colims.client.controller.Controllable;
import com.compomics.colims.client.controller.ColimsController;
import com.compomics.colims.client.event.admin.CvTermChangeEvent;
import com.compomics.colims.client.event.message.DbConstraintMessageEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.model.TypedCvTermTableModel2;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.admin.CvTermManagementDialog;
import com.compomics.colims.core.service.CvTermService;
import com.compomics.colims.model.AuditableTypedCvTerm;
import com.compomics.colims.model.enums.CvTermType;
import com.compomics.colims.model.factory.CvTermFactory;
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
import uk.ac.ebi.ontology_lookup.ontologyquery.Query;

/**
 *
 * @author Niels Hulstaert
 */
@Component("cvTermManagementController")
public class CvTermManagementController implements Controllable, OLSInputable {

    private static final String ADD_CV_TERM = "add";
    private static final String UPDATE_CV_TERM = "update";
    //model
    private TypedCvTermTableModel2 typeCvTermTableModel2;
    /**
     * The cvTermType of the CV terms in the table model.
     */
    private CvTermType cvTermType;
    //view
    private CvTermManagementDialog cvTermManagementDialog;
    //parent controller
    @Autowired
    private ColimsController colimsController;
    @Autowired
    private EventBus eventBus;
    //services
    @Autowired
    private CvTermService cvTermService;
    @Autowired
    private Query olsClient;

    /**
     *
     * @return
     */
    public CvTermManagementDialog getCvTermManagementDialog() {
        return cvTermManagementDialog;
    }

    @Override
    public void init() {
        //init view
        cvTermManagementDialog = new CvTermManagementDialog(colimsController.getColimsFrame(), true);

        //register to event bus
        //eventBus.register(this);
        //init and set table model
        typeCvTermTableModel2 = new TypedCvTermTableModel2();
        cvTermManagementDialog.getCvTermTable().setModel(typeCvTermTableModel2);

        //add listeners
        cvTermManagementDialog.getCvTermTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (!lse.getValueIsAdjusting()) {
                    int selectedRow = cvTermManagementDialog.getCvTermTable().getSelectedRow();
                    if (selectedRow != -1 && !typeCvTermTableModel2.getCvTerms().isEmpty()) {
                        AuditableTypedCvTerm selectedCvTerm = typeCvTermTableModel2.getCvTerms().get(selectedRow);

                        //check if the CV term has an ID.
                        //If so, change the save button text and the info state label.
                        if (selectedCvTerm.getId() != null) {
                            cvTermManagementDialog.getSaveOrUpdateButton().setText("update");
                            cvTermManagementDialog.getCvTermStateInfoLabel().setText("");
                        } else {
                            cvTermManagementDialog.getSaveOrUpdateButton().setText("save");
                            cvTermManagementDialog.getCvTermStateInfoLabel().setText("This CV term hasn't been stored in the database.");
                        }

                        //set details fields
                        cvTermManagementDialog.getOntologyTextField().setText(selectedCvTerm.getOntology());
                        cvTermManagementDialog.getOntologyLabelTextField().setText(selectedCvTerm.getLabel());
                        cvTermManagementDialog.getAccessionTextField().setText(selectedCvTerm.getAccession());
                        cvTermManagementDialog.getNameTextField().setText(selectedCvTerm.getName());

                        //reset definition text area
                        cvTermManagementDialog.getDefinitionTextArea().setText("");
                        //get term definition from ols service
                        org.apache.xml.xml_soap.Map termMetadata = olsClient.getTermMetadata(selectedCvTerm.getAccession(), selectedCvTerm.getLabel());
                        if (termMetadata != null && !termMetadata.getItem().isEmpty()) {
                            for (MapItem mapItem : termMetadata.getItem()) {
                                //look for definition item
                                if (mapItem.getKey().equals("definition") && mapItem.getValue() != null) {
                                    cvTermManagementDialog.getDefinitionTextArea().setText(mapItem.getValue().toString());
                                }
                            }
                        }
                    } else {
                        clearCvTermDetailFields();
                    }
                }
            }
        });

        cvTermManagementDialog.getAddCvTermButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                showOlsDialog(true);
            }
        });

        cvTermManagementDialog.getSaveOrUpdateButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                AuditableTypedCvTerm selectedCvTerm = getSelectedCvTerm();
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

                    MessageEvent messageEvent = new MessageEvent("CV term store confirmation", "CV term " + selectedCvTerm.getName() + " was stored successfully!", JOptionPane.INFORMATION_MESSAGE);
                    eventBus.post(messageEvent);

                    eventBus.post(new CvTermChangeEvent());
                } else {
                    MessageEvent messageEvent = new MessageEvent("Validation failure", validationMessages, JOptionPane.WARNING_MESSAGE);
                    eventBus.post(messageEvent);
                }
            }
        });

        cvTermManagementDialog.getDeleteCvTermButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                AuditableTypedCvTerm cvTermToDelete = getSelectedCvTerm();
                int selectedIndex = cvTermManagementDialog.getCvTermTable().getSelectedRow();

                //check if instrument type has an id.
                //If so, try to delete the permission from the db.
                if (cvTermToDelete.getId() != null) {
                    try {
                        cvTermService.delete(cvTermToDelete);

                        typeCvTermTableModel2.removeCvTerm(selectedIndex);
                        cvTermManagementDialog.getCvTermTable().getSelectionModel().clearSelection();

                        eventBus.post(new CvTermChangeEvent());
                    } catch (DataIntegrityViolationException dive) {
                        //check if the CV term can be deleted without breaking existing database relations,
                        //i.e. are there any constraints violations
                        if (dive.getCause() instanceof ConstraintViolationException) {
                            DbConstraintMessageEvent dbConstraintMessageEvent = new DbConstraintMessageEvent("CV term", cvTermToDelete.getName());
                            eventBus.post(dbConstraintMessageEvent);
                        } else {
                            //pass the exception
                            throw dive;
                        }
                    }
                } else {
                    typeCvTermTableModel2.removeCvTerm(selectedIndex);
                    cvTermManagementDialog.getCvTermTable().getSelectionModel().clearSelection();
                }
            }
        });

        cvTermManagementDialog.getEditUsingOlsCvTermButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                showOlsDialog(false);
            }
        });

        cvTermManagementDialog.getCancelButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                cvTermManagementDialog.dispose();
            }
        });
    }

    @Override
    public void showView() {
        //clear selection
        cvTermManagementDialog.getCvTermTable().getSelectionModel().clearSelection();

        GuiUtils.centerDialogOnComponent(colimsController.getColimsFrame(), cvTermManagementDialog);
        cvTermManagementDialog.setVisible(true);
    }

    /**
     * Update the CV term list and set the current cvTermType.
     *
     * @param cvTermType the cvTermType of the CV terms in the list
     * @param cvTerms the list of CV terms
     */
    public void updateDialog(final CvTermType cvTermType, final List<AuditableTypedCvTerm> cvTerms) {
        this.cvTermType = cvTermType;

        typeCvTermTableModel2.setCvTerms(cvTerms);

        //clear selection
        cvTermManagementDialog.getCvTermTable().getSelectionModel().clearSelection();
    }

    @Override
    public void insertOLSResult(final String field, final String selectedValue, final String accession, final String ontologyShort, final String ontologyLong, int modifiedRow, final String mappedTerm, final Map<String, String> metadata) {
        //check wether a CV term has to be added or updated
        if (field.equals(ADD_CV_TERM)) {
            AuditableTypedCvTerm cvTerm = CvTermFactory.newInstance(cvTermType, ontologyLong, ontologyShort, accession, selectedValue);

            //add CV term to the table model
            typeCvTermTableModel2.addCvTerm(cvTerm);

            //set selected index to newly added CV term
            cvTermManagementDialog.getCvTermTable().getSelectionModel().setSelectionInterval(typeCvTermTableModel2.getRowCount() - 1, typeCvTermTableModel2.getRowCount() - 1);
        } else {
            //update selected CV term
            AuditableTypedCvTerm selectedCvTerm = getSelectedCvTerm();
            updateCvTerm(selectedCvTerm, ontologyLong, ontologyShort, accession, selectedValue);

            //update CV term in table model
            int selectedIndex = cvTermManagementDialog.getCvTermTable().getSelectedRow();
            typeCvTermTableModel2.updateCvTerm(selectedCvTerm, selectedIndex);

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
     * Check if the CV term with the accession exists in the database.
     *
     * @param cvTerm the selected CV term
     * @return the does exist boolean
     */
    private boolean isExistingCvTermAccession(final AuditableTypedCvTerm cvTerm) {
        boolean isExistingCvTermAccession = true;
        AuditableTypedCvTerm foundCvTerm = cvTermService.findByAccession(cvTerm.getAccession(), cvTerm.getcvTermType());
        if (foundCvTerm == null) {
            isExistingCvTermAccession = false;
        }

        return isExistingCvTermAccession;
    }

    /**
     * Update the given CV term. Only the modified fields are set.
     *
     * @param cvTerm the TypedCvTerm
     * @param ontology the ontology
     * @param label the label
     * @param accession the accession
     * @param name the name
     */
    private void updateCvTerm(final AuditableTypedCvTerm cvTerm, final String ontology, final String label, final String accession, final String name) {
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
    private void showOlsDialog(final boolean isNewCvTerm) {
        String ontology = "PSI Mass Spectrometry Ontology [MS]";

        Map<String, List<String>> preselectedOntologies = new HashMap<>();
        preselectedOntologies.put("MS", null);

        String field;
        String term = null;

        if (isNewCvTerm) {
            field = ADD_CV_TERM;
        } else {
            field = UPDATE_CV_TERM;
            term = getSelectedCvTerm().getName();
        }

        cvTermManagementDialog.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

        //show new OLS dialog
        new OLSDialog(cvTermManagementDialog, this, true, field, ontology, term, preselectedOntologies);

        cvTermManagementDialog.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }

    /**
     * Get the selected CV term in the CV term table.
     *
     * @return the selected CV term
     */
    private AuditableTypedCvTerm getSelectedCvTerm() {
        int selectedCvTermIndex = cvTermManagementDialog.getCvTermTable().getSelectedRow();
        AuditableTypedCvTerm selectedCvTerm = (selectedCvTermIndex != -1) ? typeCvTermTableModel2.getCvTerms().get(selectedCvTermIndex) : null;

        return selectedCvTerm;
    }

    /**
     * Clear the CV term details fields.
     */
    private void clearCvTermDetailFields() {
        cvTermManagementDialog.getCvTermStateInfoLabel().setText("");
        cvTermManagementDialog.getOntologyTextField().setText("");
        cvTermManagementDialog.getOntologyLabelTextField().setText("");
        cvTermManagementDialog.getAccessionTextField().setText("");
        cvTermManagementDialog.getNameTextField().setText("");
        cvTermManagementDialog.getDefinitionTextArea().setText("");
    }

}

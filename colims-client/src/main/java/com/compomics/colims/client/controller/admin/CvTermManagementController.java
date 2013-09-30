package com.compomics.colims.client.controller.admin;

import com.compomics.colims.client.compoment.DualList;
import com.compomics.colims.client.controller.Controllable;
import com.compomics.colims.client.controller.MainController;
import com.compomics.colims.client.event.EntityChangeEvent;
import static com.compomics.colims.client.event.EntityChangeEvent.Type.CREATED;
import static com.compomics.colims.client.event.EntityChangeEvent.Type.DELETED;
import static com.compomics.colims.client.event.EntityChangeEvent.Type.UPDATED;
import com.compomics.colims.client.event.GroupChangeEvent;
import com.compomics.colims.client.event.MessageEvent;
import com.compomics.colims.client.event.RoleChangeEvent;
import com.compomics.colims.client.event.UserChangeEvent;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.admin.CvTermManagementDialog;
import com.compomics.colims.client.view.admin.UserManagementDialog;
import com.compomics.colims.core.service.CvTermService;
import com.compomics.colims.core.service.GroupService;
import com.compomics.colims.core.service.RoleService;
import com.compomics.colims.model.CvTerm;
import com.compomics.colims.model.Group;
import com.compomics.colims.model.InstrumentCvTerm;
import com.compomics.colims.model.Role;
import com.compomics.colims.model.User;
import com.compomics.colims.model.enums.CvTermType;
import com.compomics.colims.model.factory.CvTermFactory;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import no.uib.olsdialog.OLSDialog;
import no.uib.olsdialog.OLSInputable;
import static no.uib.olsdialog.example.OLS_Example.getOntologyFromCvTerm;
import org.apache.xml.xml_soap.MapItem;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ebi.ontology_lookup.ontologyquery.Query;

/**
 *
 * @author Niels Hulstaert
 */
@Component("cvTermManagementController")
public class CvTermManagementController implements OLSInputable {

    private static final String SAVE_CV_TERM = "save";
    private static final String UPDATE_CV_TERM = "update";
    //model    
    private ObservableList<CvTerm> cvTermBindingList;
    /**
     * The cvTermType of the CV terms in the binding list.
     */
    private CvTermType cvTermType;
    private BindingGroup bindingGroup;
    private JTableBinding cvTermTableBinding;
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

        //init bindings
        bindingGroup = new BindingGroup();

        cvTermBindingList = ObservableCollections.observableList(new ArrayList());

        //table binding
        cvTermTableBinding = SwingBindings.createJTableBinding(AutoBinding.UpdateStrategy.READ_WRITE, cvTermBindingList, cvTermManagementDialog.getCvTermTable());

        //Add column bindings
        JTableBinding.ColumnBinding columnBinding = cvTermTableBinding.addColumnBinding(ELProperty.create("${label}"));
        columnBinding.setColumnName("Ontology");
        columnBinding.setEditable(Boolean.FALSE);
        columnBinding.setColumnClass(String.class);

        columnBinding = cvTermTableBinding.addColumnBinding(ELProperty.create("${accession}"));
        columnBinding.setColumnName("Accession");
        columnBinding.setEditable(Boolean.FALSE);
        columnBinding.setColumnClass(String.class);

        columnBinding = cvTermTableBinding.addColumnBinding(ELProperty.create("${name}"));
        columnBinding.setColumnName("Name");
        columnBinding.setEditable(Boolean.FALSE);
        columnBinding.setColumnClass(String.class);

        bindingGroup.addBinding(cvTermTableBinding);

        bindingGroup.bind();

        //add listeners
        cvTermManagementDialog.getCvTermTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (!lse.getValueIsAdjusting()) {
                    int selectedRow = cvTermManagementDialog.getCvTermTable().getSelectedRow();
                    if (selectedRow != -1 && !cvTermBindingList.isEmpty()) {
                        CvTerm selectedCvTerm = cvTermBindingList.get(selectedRow);

                        //check if the CV term is found in the db.
                        //If so, disable the name text field and change the save button label.
                        if (selectedCvTerm.getId() != null) {
                            cvTermManagementDialog.getSaveOrUpdateButton().setText("update");
                            cvTermManagementDialog.getCvTermStateInfoLabel().setText("");
                        } else {
                            cvTermManagementDialog.getSaveOrUpdateButton().setText("save");
                            cvTermManagementDialog.getCvTermStateInfoLabel().setText("This CV term hasn't been saved to the database.");
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
                            cvTermManagementDialog.getDefinitionTextArea().setText("");
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
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

                    MessageEvent messageEvent = new MessageEvent("Cv term save confirmation", "CV term " + selectedCvTerm.getName() + " was saved successfully!", JOptionPane.INFORMATION_MESSAGE);
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

        clearDialog();

        cvTermBindingList.addAll(cvTerms);
    }

    @Override
    public void insertOLSResult(String field, String selectedValue, String accession, String ontologyShort, String ontologyLong, int modifiedRow, String mappedTerm, Map<String, String> metadata) {
        if (field.equals(SAVE_CV_TERM)) {
            CvTerm cvTerm = CvTermFactory.newInstance(cvTermType, ontologyLong, ontologyShort, accession, selectedValue);

            //add CV term to binding list
            cvTermBindingList.add(cvTerm);

            //set selected index to newly added CV term
            cvTermManagementDialog.getCvTermTable().getSelectionModel().setSelectionInterval(cvTermBindingList.size() - 1, cvTermBindingList.size() - 1);
        } else {
            //update selected CV term
            CvTerm selectedCvTerm = getSelectedCvTerm();
            updateCvTerm(selectedCvTerm, ontologyLong, ontologyShort, accession, selectedValue);

            //update observable list
            int selectedIndex = cvTermManagementDialog.getCvTermTable().getSelectedRow();
            cvTermBindingList.remove(selectedIndex);
            cvTermBindingList.add(selectedIndex, selectedCvTerm);
            cvTermManagementDialog.getCvTermTable().getSelectionModel().setSelectionInterval(selectedIndex, selectedIndex);
        }

    }

    @Override
    public Window getWindow() {
        return cvTermManagementDialog;
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

        String field = (isNewCvTerm) ? SAVE_CV_TERM : UPDATE_CV_TERM;

        new OLSDialog(cvTermManagementDialog, this, true, field, ontology, null, preselectedOntologies);
    }

    /**
     * Get the selected CV term in the CV term table.
     *
     * @return the selected CV term
     */
    private CvTerm getSelectedCvTerm() {
        int selectedCvTermIndex = cvTermManagementDialog.getCvTermTable().getSelectedRow();
        CvTerm selectedCvTerm = (selectedCvTermIndex != -1) ? cvTermBindingList.get(selectedCvTermIndex) : null;

        return selectedCvTerm;
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
     * Update the given CV term. Checks wich fields have been modified.
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
     * Clear the dialog; clear the detail fields and the selected row in the
     * overview table.
     *
     */
    private void clearDialog() {
        //clear binding list
        cvTermBindingList.clear();

        cvTermManagementDialog.getCvTermTable().clearSelection();

        cvTermManagementDialog.getOntologyTextField().setText("");
        cvTermManagementDialog.getOntologyLabelTextField().setText("");
        cvTermManagementDialog.getAccessionTextField().setText("");
        cvTermManagementDialog.getNameTextField().setText("");
        cvTermManagementDialog.getDefinitionTextArea().setText("");
    }
}

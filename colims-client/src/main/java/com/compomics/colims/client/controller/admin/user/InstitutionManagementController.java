package com.compomics.colims.client.controller.admin.user;

import com.compomics.colims.client.controller.Controllable;
import com.compomics.colims.client.event.message.DbConstraintMessageEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.admin.InstitutionManagementDialog;
import com.compomics.colims.core.service.InstitutionService;
import com.compomics.colims.model.Institution;
import com.google.common.eventbus.EventBus;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
@Component("institutionManagementController")
public class InstitutionManagementController implements Controllable {

    private static final Logger LOGGER = Logger.getLogger(InstitutionManagementController.class);

    //model
    private BindingGroup bindingGroup;
    private ObservableList<Institution> institutionBindingList;
    //view
    private InstitutionManagementDialog institutionManagementDialog;
    //parent controller
    @Autowired
    private UserManagementController userManagementController;
    //services
    @Autowired
    private EventBus eventBus;
    @Autowired
    private InstitutionService institutionService;

    @Override
    public void init() {
        //init view
        institutionManagementDialog = new InstitutionManagementDialog(userManagementController.getUserManagementParentController().getUserManagementDialog(), true);

        //register to event bus
        eventBus.register(this);

        //init binding
        bindingGroup = new BindingGroup();

        institutionBindingList = userManagementController.getInstitutionBindingList();
        JListBinding userListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, institutionBindingList, institutionManagementDialog.getInstitutionList());
        bindingGroup.addBinding(userListBinding);

        //user bindings
        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, institutionManagementDialog.getInstitutionList(), BeanProperty.create("selectedElement.name"), institutionManagementDialog.getNameTextField(), ELProperty.create("${text}"), "nameBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, institutionManagementDialog.getInstitutionList(), BeanProperty.create("selectedElement.abbreviation"), institutionManagementDialog.getAbbreviationTextField(), ELProperty.create("${text}"), "abbreviationBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, institutionManagementDialog.getInstitutionList(), BeanProperty.create("selectedElement.street"), institutionManagementDialog.getStreetTextField(), ELProperty.create("${text}"), "streetBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, institutionManagementDialog.getInstitutionList(), BeanProperty.create("selectedElement.number"), institutionManagementDialog.getNumberTextField(), ELProperty.create("${text}"), "numberBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, institutionManagementDialog.getInstitutionList(), BeanProperty.create("selectedElement.city"), institutionManagementDialog.getCityTextField(), ELProperty.create("${text}"), "cityBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, institutionManagementDialog.getInstitutionList(), BeanProperty.create("selectedElement.postalCode"), institutionManagementDialog.getPostalCodeTextField(), ELProperty.create("${text}"), "postalCodeBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, institutionManagementDialog.getInstitutionList(), BeanProperty.create("selectedElement.country"), institutionManagementDialog.getCountryTextField(), ELProperty.create("${text}"), "countryBinding");
        bindingGroup.addBinding(binding);

        bindingGroup.bind();

        //add listeners       
        institutionManagementDialog.getInstitutionList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (institutionManagementDialog.getInstitutionList().getSelectedIndex() != -1) {
                        Institution institution = getSelectedInstitution();

                        //enable save and delete button
                        institutionManagementDialog.getSaveOrUpdateButton().setEnabled(true);
                        institutionManagementDialog.getDeleteButton().setEnabled(true);

                        //check if the fasta DB has an ID.
                        //If so, change the save button label.
                        if (institution.getId() != null) {
                            institutionManagementDialog.getSaveOrUpdateButton().setText("update");
                            institutionManagementDialog.getInstitutionStateInfoLabel().setText("");
                        } else {
                            institutionManagementDialog.getSaveOrUpdateButton().setText("save");
                            institutionManagementDialog.getInstitutionStateInfoLabel().setText("This institution hasn't been stored to the database.");
                        }
                    } else {
                        institutionManagementDialog.getSaveOrUpdateButton().setEnabled(false);
                        clearInstitutionDetailFields();
                    }
                }
            }
        });

        institutionManagementDialog.getAddButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Institution newInstitution = new Institution();
                newInstitution.setName("name");
                institutionBindingList.add(newInstitution);
                institutionManagementDialog.getInstitutionList().setSelectedIndex(institutionBindingList.size() - 1);
            }
        });

        institutionManagementDialog.getDeleteButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (institutionManagementDialog.getInstitutionList().getSelectedIndex() != -1) {
                    Institution institutionToDelete = getSelectedInstitution();

                    //check if fasta DB has an id.
                    //If so, try to delete the fasta DB from the db.
                    if (institutionToDelete.getId() != null) {
                        try {
                            institutionService.delete(institutionToDelete);

                            institutionBindingList.remove(institutionManagementDialog.getInstitutionList().getSelectedIndex());
                            institutionManagementDialog.getInstitutionList().getSelectionModel().clearSelection();
                            //clearInstrumentTypeDetailFields();
                        } catch (DataIntegrityViolationException dive) {
                            //check if the instrument type can be deleted without breaking existing database relations,
                            //i.e. are there any constraints violations
                            if (dive.getCause() instanceof ConstraintViolationException) {
                                DbConstraintMessageEvent dbConstraintMessageEvent = new DbConstraintMessageEvent("institution", institutionToDelete.getName());
                                eventBus.post(dbConstraintMessageEvent);
                            } else {
                                //pass the exception
                                throw dive;
                            }
                        }
                    } else {
                        institutionBindingList.remove(institutionManagementDialog.getInstitutionList().getSelectedIndex());
                        institutionManagementDialog.getInstitutionList().getSelectionModel().clearSelection();
                        clearInstitutionDetailFields();
                    }
                } else {
                    eventBus.post(new MessageEvent("Fasta DB selection", "Please select a fasta DB to delete.", JOptionPane.INFORMATION_MESSAGE));
                }
            }
        });

        institutionManagementDialog.getSaveOrUpdateButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (institutionManagementDialog.getInstitutionList().getSelectedIndex() != -1) {
                    Institution selectedInstitution = getSelectedInstitution();
                    //validate institution
                    List<String> validationMessages = validateNumbers(selectedInstitution);
                    validationMessages.addAll(GuiUtils.validateEntity(selectedInstitution));
                    if (validationMessages.isEmpty()) {
                        if (selectedInstitution.getId() != null) {
                            institutionService.update(selectedInstitution);
                        } else {
                            institutionService.save(selectedInstitution);
                            //refresh fasta DB list
                            institutionManagementDialog.getInstitutionList().updateUI();
                        }
                        institutionManagementDialog.getSaveOrUpdateButton().setText("update");
                        institutionManagementDialog.getInstitutionStateInfoLabel().setText("");

                        MessageEvent messageEvent = new MessageEvent("Institution store confirmation", "Institution " + selectedInstitution.getName() + " was stored successfully!", JOptionPane.INFORMATION_MESSAGE);
                        eventBus.post(messageEvent);
                    } else {
                        MessageEvent messageEvent = new MessageEvent("Validation failure", validationMessages, JOptionPane.WARNING_MESSAGE);
                        eventBus.post(messageEvent);
                    }
                } else {
                    eventBus.post(new MessageEvent("Fasta DB selection", "Please select an institution to save or update.", JOptionPane.INFORMATION_MESSAGE));
                }
            }
        });

        institutionManagementDialog.getCancelButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                institutionManagementDialog.dispose();
            }
        });
    }

    @Override
    public void showView() {
        //renew institution list
        institutionBindingList.clear();
        institutionBindingList.addAll(institutionService.findAll());
        
        institutionManagementDialog.getInstitutionStateInfoLabel().setText("");

        GuiUtils.centerDialogOnComponent(userManagementController.getUserManagementParentController().getUserManagementDialog(), institutionManagementDialog);
        institutionManagementDialog.setVisible(true);
    }

    /**
     * Return the selected institution.
     *
     * @return
     */
    public Institution getInstitution() {
        return getSelectedInstitution();
    }

    /**
     * Validate the user input for number and postal code.
     *
     * @param institution
     * @return
     */
    private List<String> validateNumbers(Institution institution) {
        List<String> validationMessages = new ArrayList();

        //catch NumberFormatExceptions
        try {
            Integer number = Integer.parseInt(institutionManagementDialog.getNumberTextField().getText());
        } catch (NumberFormatException nfe) {
            validationMessages.add("The street number must be a number.");
        }
        String postalCodeString = institutionManagementDialog.getPostalCodeTextField().getText();
        if (!postalCodeString.isEmpty()) {
            try {
                Integer postalCode = Integer.parseInt(postalCodeString);
            } catch (NumberFormatException nfe) {
                validationMessages.add("The postal code must be a number.");
            }
        }

        return validationMessages;
    }

    /**
     * Get the selected institution in the institution JList.
     *
     * @return the selected institution
     */
    private Institution getSelectedInstitution() {
        int selectedIndex = institutionManagementDialog.getInstitutionList().getSelectedIndex();
        Institution selectedInstitution = (selectedIndex != -1) ? institutionBindingList.get(selectedIndex) : null;
        return selectedInstitution;
    }

    /**
     * Clear the institution detail fields
     */
    private void clearInstitutionDetailFields() {
        institutionManagementDialog.getNameTextField().setText("");
        institutionManagementDialog.getAbbreviationTextField().setText("");
        institutionManagementDialog.getStreetTextField().setText("");
        institutionManagementDialog.getNumberTextField().setText("");
        institutionManagementDialog.getCityTextField().setText("");
        institutionManagementDialog.getPostalCodeTextField().setText("");
        institutionManagementDialog.getCountryTextField().setText("");
    }

}

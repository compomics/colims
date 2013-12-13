package com.compomics.colims.client.controller;

import com.compomics.colims.client.compoment.DualList;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.ExperimentEditDialog;
import com.compomics.colims.client.view.ProjectEditDialog;
import com.compomics.colims.core.service.ProjectService;
import com.compomics.colims.core.service.ProtocolService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.Experiment;
import com.compomics.colims.model.Project;
import com.compomics.colims.model.Protocol;
import com.compomics.colims.model.User;
import com.compomics.colims.model.comparator.UserNameComparator;
import com.google.common.eventbus.EventBus;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("experimentEditController")
public class ExperimentEditController implements Controllable {

    private static final Logger LOGGER = Logger.getLogger(ExperimentEditController.class);
    //model   
    private BindingGroup bindingGroup;
    private ObservableList<Protocol> protocolBindingList;
    private Experiment experimentToEdit;
    //view
    private ExperimentEditDialog experimentEditDialog;
    //private 
    //parent controller
    @Autowired
    private ProjectManagementController projectManagementController;
    @Autowired
    private ColimsController colimsController;
    //services
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProtocolService protocolService;
    @Autowired
    private EventBus eventBus;

    public ExperimentEditDialog getExperimentEditDialog() {
        return experimentEditDialog;
    }

    public void init() {
//        //register to event bus
//        eventBus.register(this);
//
//        //init view
//        experimentEditDialog = new ExperimentEditDialog(colimsController.getColimsFrame(), true);
//
//        bindingGroup = new BindingGroup();
//
//        //add binding
//        protocolBindingList = ObservableCollections.observableList(protocolService.findAll());
//
//        JComboBoxBinding instrumentTypeComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, protocolBindingList, experimentEditDialog.getProtocolComboBox());
//        bindingGroup.addBinding(instrumentTypeComboBoxBinding);
//
//        bindingGroup.bind();
//
//        //add action listeners                
//        experimentEditDialog.getUserDualList().addPropertyChangeListener(DualList.CHANGED, new PropertyChangeListener() {
//            @Override
//            public void propertyChange(PropertyChangeEvent evt) {
//                List<User> addedUsers = (List<User>) evt.getNewValue();
//
//                experimentToEdit.setUsers(addedUsers);
//            }
//        });
//
//        experimentEditDialog.getSaveOrUpdateButton().addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                //update projectToEdit with dialog input
//                updateExperimentToEdit();
//
//                //validate project
//                List<String> validationMessages = GuiUtils.validateEntity(experimentToEdit);
//                //check for a new project if the project title already exists in the db                
//                if (experimentToEdit.getId() == null && isExistingProjectTitle(experimentToEdit)) {
//                    validationMessages.add(experimentToEdit.getTitle() + " already exists in the database,"
//                            + "\n" + "please choose another project title.");
//                }
//                int index = 0;
//                if (validationMessages.isEmpty()) {
//                    if (experimentToEdit.getId() != null) {
//                        projectService.update(experimentToEdit);
//                        index = projectManagementController.getSelectedProjectIndex();
//                    } else {
//                        projectService.save(experimentToEdit);
//                        //add project to overview table
//                        projectManagementController.addProject(experimentToEdit);
//                        index = projectManagementController.getProjectsSize() - 1;
//                    }
//                    experimentEditDialog.getSaveOrUpdateButton().setText("update");
//
//                    MessageEvent messageEvent = new MessageEvent("project persist confirmation", "Project " + experimentToEdit.getLabel() + " was persisted successfully!", JOptionPane.INFORMATION_MESSAGE);
//                    eventBus.post(messageEvent);
//
//                    //refresh selection in project list in management overview dialog
//                    projectManagementController.setSelectedProject(index);
//                } else {
//                    MessageEvent messageEvent = new MessageEvent("validation failure", validationMessages, JOptionPane.WARNING_MESSAGE);
//                    eventBus.post(messageEvent);
//                }
//            }
//        });
//
//        experimentEditDialog.getCancelButton().addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                experimentEditDialog.dispose();
//            }
//        });
    }

    @Override
    public void showView() {
        experimentEditDialog.setLocationRelativeTo(null);
        experimentEditDialog.setVisible(true);
    }

    /**
     * Update the project edit dialog with the selected project in the project
     * overview table.
     */
    public void updateView(Project project) {
//        experimentToEdit = project;
//
//        if (experimentToEdit.getId() != null) {
//            experimentEditDialog.getSaveOrUpdateButton().setText("update");
//        } else {
//            experimentEditDialog.getSaveOrUpdateButton().setText("save");
//        }
//
//        experimentEditDialog.getTitleTextField().setText(experimentToEdit.getTitle());
//        experimentEditDialog.getNumberTextField().setText(experimentToEdit.get);
//
//        //set the selected item in the owner combobox        
//        experimentEditDialog.getOwnerComboBox().setSelectedItem(experimentToEdit.getOwner());
//        experimentEditDialog.getDescriptionTextArea().setText(experimentToEdit.getDescription());
//        //populate user dual list
//        experimentEditDialog.getUserDualList().populateLists(protocolService.findAll(), experimentToEdit.getUsers());
//
//        showView();
    }

    /**
     * Update the instance fields of the selected experiment in the experiments table
     *
     */
    private void updateExperimentToEdit() {
        experimentToEdit.setTitle(experimentEditDialog.getTitleTextField().getText());
        //experimentToEdit.setNumber(experimentEditDialog.getNumberTextField().getText());        
        experimentToEdit.setDescription(experimentEditDialog.getDescriptionTextArea().getText());
        //the users have been updated by the duallist listener
    }

    /**
     * Check if a project with the given project title exists in the database.
     *
     * @param project the project
     * @return does the project title exist
     */
    private boolean isExistingProjectTitle(Project project) {
        boolean isExistingProjectTitle = true;
        Project foundProject = projectService.findByTitle(project.getTitle());
        if (foundProject == null) {
            isExistingProjectTitle = false;
        }

        return isExistingProjectTitle;
    }
}

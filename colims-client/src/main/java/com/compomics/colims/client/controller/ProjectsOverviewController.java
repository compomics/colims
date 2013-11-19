package com.compomics.colims.client.controller;

import com.compomics.colims.client.view.ProjectsOverviewPanel;
import com.compomics.colims.core.service.ProjectService;
import com.compomics.colims.model.Experiment;
import com.compomics.colims.model.Project;
import java.util.ArrayList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
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
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("projectsOverviewController")
public class ProjectsOverviewController {

    private static final Logger LOGGER = Logger.getLogger(ProjectsOverviewController.class);
    //model
    private BindingGroup bindingGroup;
    private ObservableList<Project> projectBindingList;
    private ObservableList<Experiment> experimentBindingList;
    //view
    private ProjectsOverviewPanel projectsOverviewPanel;
    //parent controller
    @Autowired
    private MainController mainController;
    //services
    @Autowired
    private ProjectService projectService;

    public ProjectsOverviewPanel getProjectsOverviewPanel() {
        return projectsOverviewPanel;
    }

    public void init() {
        projectsOverviewPanel = new ProjectsOverviewPanel();

        //init bindings
        bindingGroup = new BindingGroup();

        projectBindingList = ObservableCollections.observableList(projectService.findAllWithEagerFetching());
        JListBinding projectJListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, projectBindingList, projectsOverviewPanel.getProjectList());
        bindingGroup.addBinding(projectJListBinding);
        experimentBindingList = ObservableCollections.observableList(new ArrayList<Experiment>());
        JListBinding experimentJListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, experimentBindingList, projectsOverviewPanel.getExperimentList());
        bindingGroup.addBinding(experimentJListBinding);

        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, projectsOverviewPanel.getProjectList(), BeanProperty.create("selectedElement.title"), projectsOverviewPanel.getProjectTitleTextField(), ELProperty.create("${text}"), "projectTitleBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, projectsOverviewPanel.getProjectList(), BeanProperty.create("selectedElement.label"), projectsOverviewPanel.getProjectLabelTextField(), ELProperty.create("${text}"), "projectLabelBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, projectsOverviewPanel.getProjectList(), BeanProperty.create("selectedElement.owner.name"), projectsOverviewPanel.getProjectOwnerTextField(), ELProperty.create("${text}"), "projectOwnerBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, projectsOverviewPanel.getProjectList(), BeanProperty.create("selectedElement.description"), projectsOverviewPanel.getProjectDescriptionTextArea(), ELProperty.create("${text}"), "projectDescriptionBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, projectsOverviewPanel.getExperimentList(), BeanProperty.create("selectedElement.title"), projectsOverviewPanel.getExperimentTitleTextField(), ELProperty.create("${text}"), "experimentTitleBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, projectsOverviewPanel.getExperimentList(), BeanProperty.create("selectedElement.number"), projectsOverviewPanel.getExperimentNumberTextField(), ELProperty.create("${text}"), "experimentNumberBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, projectsOverviewPanel.getExperimentList(), BeanProperty.create("selectedElement.description"), projectsOverviewPanel.getExperimentDescriptionTextArea(), ELProperty.create("${text}"), "experimentDescriptionBinding");
        bindingGroup.addBinding(binding);

        bindingGroup.bind();

        //add action listeners
        projectsOverviewPanel.getProjectList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedProjectIndex = projectsOverviewPanel.getProjectList().getSelectedIndex();
                    if (selectedProjectIndex != -1) {
                        Project selectedProject = projectBindingList.get(selectedProjectIndex);
                        experimentBindingList.clear();
                        if (!selectedProject.getExperiments().isEmpty()) {
                            experimentBindingList.addAll(selectedProject.getExperiments());
                        } else {
                            clearExperimentDetails();
                        }
                    }
                }
            }
        });
    }

    private void clearExperimentDetails() {
        projectsOverviewPanel.getExperimentTitleTextField().setText("");
        projectsOverviewPanel.getExperimentNumberTextField().setText("");
        projectsOverviewPanel.getExperimentDescriptionTextArea().setText("");
    }
}

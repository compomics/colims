package com.compomics.colims.client.controller;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.AdvancedTableModel;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.GlazedListsSwing;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import com.compomics.colims.client.model.ProjectOverviewTableFormat;
import com.compomics.colims.client.view.ProjectsOverviewPanel;
import com.compomics.colims.client.view.ProjectsOverviewPanel_1;
import com.compomics.colims.core.service.ProjectService;
import com.compomics.colims.model.Experiment;
import com.compomics.colims.model.Project;
import com.compomics.colims.model.comparator.ProjectIdComparator;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
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
    private EventList<Project> projects = new BasicEventList<>();
    private EventList<Experiment> experiments;
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

        //fill projects table
        projects.addAll(projectService.findAllWithEagerFetching());
        SortedList<Project> sortedProjects = new SortedList<>(projects, new ProjectIdComparator());
        DefaultEventSelectionModel eventSelectionModel = new DefaultEventSelectionModel<>(projects);
        AdvancedTableModel<Project> projectTableModel = GlazedListsSwing.eventTableModel(sortedProjects, new ProjectOverviewTableFormat());
        projectsOverviewPanel.getProjectsTable().setModel(projectTableModel);
        
        //use MULTIPLE_COLUMN_MOUSE to allow sorting by multiple columns
        TableComparatorChooser tableSorter = TableComparatorChooser.install(
                projectsOverviewPanel.getProjectsTable(), sortedProjects, TableComparatorChooser.SINGLE_COLUMN);

        //add action listeners
        projectsOverviewPanel.getProjectsTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (!lse.getValueIsAdjusting()) {
                    int selectedRowIndex = projectsOverviewPanel.getProjectsTable().getSelectedRow();
                    if (selectedRowIndex != -1 && !projects.isEmpty()) {
                        Project selectedProject = projects.get(projectsOverviewPanel.getProjectsTable().convertRowIndexToModel(selectedRowIndex));
                        System.out.println("test");
                    }
                }
            }
        });

        projectsOverviewPanel.getAddProjectButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });

        projectsOverviewPanel.getEditProjectButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });

        projectsOverviewPanel.getAddExperimentButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });

        projectsOverviewPanel.getEditExperimentButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
    }
    
}

package com.compomics.colims.client.controller;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.AdvancedTableModel;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.GlazedListsSwing;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import com.compomics.colims.client.model.ExperimentOverviewTableFormat;
import com.compomics.colims.client.model.ProjectOverviewTableFormat;
import com.compomics.colims.client.view.ProjectsOverviewPanel;
import com.compomics.colims.core.service.ProjectService;
import com.compomics.colims.model.Experiment;
import com.compomics.colims.model.Project;
import com.compomics.colims.model.comparator.IdComparator;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
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
    private AdvancedTableModel<Project> projectsTableModel;
    private EventList<Experiment> experiments = new BasicEventList<>();
    private AdvancedTableModel<Experiment> experimentsTableModel;
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

        //init projects and experiments table
        projects.addAll(projectService.findAllWithEagerFetching());
        SortedList<Project> sortedProjects = new SortedList<>(projects, new IdComparator());
        projectsTableModel = GlazedListsSwing.eventTableModel(sortedProjects, new ProjectOverviewTableFormat());
        projectsOverviewPanel.getProjectsTable().setModel(projectsTableModel);

        SortedList<Experiment> sortedExperiments = new SortedList<>(experiments, new IdComparator());
        experimentsTableModel = GlazedListsSwing.eventTableModel(sortedExperiments, new ExperimentOverviewTableFormat());
        projectsOverviewPanel.getExperimentsTable().setModel(experimentsTableModel);

        //use MULTIPLE_COLUMN_MOUSE to allow sorting by multiple columns
        TableComparatorChooser projectsTableSorter = TableComparatorChooser.install(
                projectsOverviewPanel.getProjectsTable(), sortedProjects, TableComparatorChooser.SINGLE_COLUMN);
        TableComparatorChooser experimentsTableSorter = TableComparatorChooser.install(
                projectsOverviewPanel.getExperimentsTable(), sortedExperiments, TableComparatorChooser.SINGLE_COLUMN);

        //add action listeners
        projectsOverviewPanel.getProjectsTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (!lse.getValueIsAdjusting()) {
                    //clear project experiments table
                    experiments.clear();

                    int selectedProjectIndex = projectsOverviewPanel.getProjectsTable().getSelectedRow();
                    if (selectedProjectIndex != -1 && !projects.isEmpty()) {
                        Project selectedProject = projectsTableModel.getElementAt(selectedProjectIndex);

                        //fill project experiments table                        
                        experiments.addAll(selectedProject.getExperiments());
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

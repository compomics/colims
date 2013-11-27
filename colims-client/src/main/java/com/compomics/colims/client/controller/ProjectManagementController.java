package com.compomics.colims.client.controller;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.AdvancedTableModel;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.GlazedListsSwing;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import com.compomics.colims.client.model.ExperimentsOverviewTableFormat;
import com.compomics.colims.client.model.ProjectsOverviewTableFormat;
import com.compomics.colims.client.view.ProjectManagementPanel;
import com.compomics.colims.core.service.ProjectService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.Experiment;
import com.compomics.colims.model.Project;
import com.compomics.colims.model.comparator.IdComparator;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("projectManagementController")
public class ProjectManagementController {

    private static final Logger LOGGER = Logger.getLogger(ProjectManagementController.class);
    //model
    private EventList<Project> projects = new BasicEventList<>();
    private AdvancedTableModel<Project> projectsTableModel;
    private DefaultEventSelectionModel<Project> projectsSelectionModel;
    private EventList<Experiment> experiments = new BasicEventList<>();
    private AdvancedTableModel<Experiment> experimentsTableModel;
    private DefaultEventSelectionModel<Experiment> experimentsSelectionModel;
    private Project projectToEdit;
    //view
    private ProjectManagementPanel projectManagementPanel;
    //parent controller
    @Autowired
    private MainController mainController;
    //services
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserService userService;

    public ProjectManagementPanel getProjectsOverviewPanel() {
        return projectManagementPanel;
    }

    public void init() {
        projectManagementPanel = new ProjectManagementPanel();

        //init projects table
        projects.addAll(projectService.findAllWithEagerFetching());
        SortedList<Project> sortedProjects = new SortedList<>(projects, new IdComparator());
        projectsTableModel = GlazedListsSwing.eventTableModel(sortedProjects, new ProjectsOverviewTableFormat());
        projectManagementPanel.getProjectsTable().setModel(projectsTableModel);
        projectsSelectionModel = new DefaultEventSelectionModel<>(sortedProjects);
        projectManagementPanel.getProjectsTable().setSelectionModel(projectsSelectionModel);

        //init projects experiment table
        SortedList<Experiment> sortedExperiments = new SortedList<>(experiments, new IdComparator());
        experimentsTableModel = GlazedListsSwing.eventTableModel(sortedExperiments, new ExperimentsOverviewTableFormat());
        projectManagementPanel.getExperimentsTable().setModel(experimentsTableModel);
        experimentsSelectionModel = new DefaultEventSelectionModel<>(sortedExperiments);
        projectManagementPanel.getExperimentsTable().setSelectionModel(experimentsSelectionModel);

        //use MULTIPLE_COLUMN_MOUSE to allow sorting by multiple columns
        TableComparatorChooser projectsTableSorter = TableComparatorChooser.install(
                projectManagementPanel.getProjectsTable(), sortedProjects, TableComparatorChooser.SINGLE_COLUMN);
        TableComparatorChooser experimentsTableSorter = TableComparatorChooser.install(
                projectManagementPanel.getExperimentsTable(), sortedExperiments, TableComparatorChooser.SINGLE_COLUMN);

        //add action listeners
        projectsSelectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (!lse.getValueIsAdjusting()) {
                    EventList<Project> selectedProjects = projectsSelectionModel.getSelected();
                    if (!selectedProjects.isEmpty()) {
                        Project selectedProject = selectedProjects.get(0);
                        //fill project experiments table                        
                        GlazedLists.replaceAll(experiments, selectedProject.getExperiments(), false);
                    } else {
                        GlazedLists.replaceAll(experiments, new ArrayList<Experiment>(), false);
                    }
                }
            }
        });

        experimentsSelectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (!lse.getValueIsAdjusting()) {
                    EventList<Experiment> selectedExperiments = experimentsSelectionModel.getSelected();
                    if (!selectedExperiments.isEmpty()) {
                        //for the moment, do nothing
                    }
                }
            }
        });

        projectManagementPanel.getAddProjectButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //show project edit dialog
                EventList<Project> selectedProjects = projectsSelectionModel.getSelected();
                if (!selectedProjects.isEmpty()) {
                    projectToEdit = selectedProjects.get(0);
                    
                }
            }
        });

        projectManagementPanel.getEditProjectButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });

        projectManagementPanel.getAddExperimentButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });

        projectManagementPanel.getEditExperimentButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
    }
    
    /**
     * Create a default project, with some default properties.
     *
     * @return the default project
     */
    private Project createDefaultProject() {
        Project defaultProject = new Project();
        
        defaultProject.setTitle("default project title");
        defaultProject.setLabel("def_label");
        
        //set default owner, i.e. the user with the most projects
        return defaultProject;
        
//        //find instrument types
//        List<InstrumentType> instrumentTypes = instrumentTypeService.findAll();
//        if (!instrumentTypes.isEmpty()) {
//            defaultInstrument.setInstrumentType(instrumentTypes.get(0));
//        }
//        defaultInstrument.setInstrumentType(instrumentTypes.get(0));
//        //find sources
//        List<InstrumentCvTerm> sources = cvTermService.findByCvTermByType(InstrumentCvTerm.class, CvTermType.SOURCE);
//        if (!sources.isEmpty()) {
//            defaultInstrument.setSource(sources.get(0));
//        }
//        //find detectors
//        List<InstrumentCvTerm> detectors = cvTermService.findByCvTermByType(InstrumentCvTerm.class, CvTermType.DETECTOR);
//        if (!detectors.isEmpty()) {
//            defaultInstrument.setDetector(detectors.get(0));
//        }
//        //find analyzers
//        List<InstrumentCvTerm> analyzers = cvTermService.findByCvTermByType(InstrumentCvTerm.class, CvTermType.ANALYZER);
//        if (!analyzers.isEmpty()) {
//            List<InstrumentCvTerm> defaultAnalyzers = new ArrayList<>();
//            defaultAnalyzers.add(analyzers.get(0));
//            defaultInstrument.setAnalyzers(defaultAnalyzers);
//        }
//        return defaultInstrument;
    }
}

package com.compomics.colims.client.controller;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import com.compomics.colims.client.controller.admin.InstrumentManagementController;
import com.compomics.colims.client.controller.admin.MaterialManagementController;
import com.compomics.colims.client.controller.admin.ProtocolManagementController;
import com.compomics.colims.client.controller.admin.user.UserManagementParentController;
import com.compomics.colims.client.distributed.QueueManager;
import com.compomics.colims.client.event.*;
import com.compomics.colims.client.event.admin.InstrumentChangeEvent;
import com.compomics.colims.client.event.admin.MaterialChangeEvent;
import com.compomics.colims.client.event.admin.ProtocolChangeEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.event.message.StorageQueuesConnectionErrorMessageEvent;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.MainFrame;
import com.compomics.colims.client.view.MainHelpDialog;
import com.compomics.colims.client.view.UserLoginDialog;
import com.compomics.colims.core.permission.PermissionException;
import com.compomics.colims.core.distributed.model.CompletedDbTask;
import com.compomics.colims.core.distributed.model.DeleteDbTask;
import com.compomics.colims.core.distributed.model.PersistDbTask;
import com.compomics.colims.core.service.*;
import com.compomics.colims.model.*;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.ELProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;

/**
 * The swing user interface main controller.
 *
 * @author Niels Hulstaert
 */
@Component("mainController")
public class MainController implements Controllable, ActionListener {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(MainController.class);

    private final GridBagConstraints gridBagConstraints;

    //model
    @Value("${colims-client.version}")
    private String version = "unknown";
    @Autowired
    private UserBean userBean;
    /**
     * The project EventList that is used as table model in the project
     * management and overview tabs.
     */
    private final EventList<Project> projects = new BasicEventList<>();
    //views
    private MainFrame mainFrame;
    private UserLoginDialog userLoginDialog;
    private MainHelpDialog mainHelpDialog;
    //child controllers
    @Autowired
    private ProjectManagementController projectManagementController;
    @Autowired
    private ProteinOverviewController proteinOverviewController;
    @Autowired
    private UserQueryController userQueryController;
    @Autowired
    private TaskManagementController taskManagementController;
    @Autowired
    @Lazy
    private ProtocolManagementController protocolManagementController;
    @Autowired
    @Lazy
    private UserManagementParentController userManagementParentController;
    @Autowired
    @Lazy
    private InstrumentManagementController instrumentManagementController;
    @Autowired
    @Lazy
    private MaterialManagementController materialManagementController;
    //services
    @Autowired
    private UserService userService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ExperimentService experimentService;
    @Autowired
    private SampleService sampleService;
    @Autowired
    private AnalyticalRunService analyticalRunService;
    @Autowired
    private EventBus eventBus;
    @Autowired
    private QueueManager queueManager;

    /**
     * No-arg constructor.
     */
    public MainController() {
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
    }

    /**
     * Get the main view of this controller.
     *
     * @return the MainFrame instance
     */
    public MainFrame getMainFrame() {
        return mainFrame;
    }

    /**
     * Get the list of loaded projects.
     *
     * @return the projects EventList
     */
    public EventList<Project> getProjects() {
        return projects;
    }

    @Override
    public void init() {
        //set uncaught exception handler
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            LOGGER.error(e.getMessage(), e);
            //check for permission exceptions
            if (e instanceof PermissionException) {
                showPermissionErrorDialog(e.getMessage());
            } else {
                showUnexpectedErrorDialog(e.getMessage());
                System.exit(0);
            }
        });

        //register to event bus
        eventBus.register(this);

        //init views
        mainFrame = new MainFrame();
        try {
            mainFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(new ClassPathResource("/icons/colims_icon.png").getURL()));
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new IllegalStateException();
        }
        mainFrame.setTitle("Colims " + version);
        userLoginDialog = new UserLoginDialog(mainFrame, true);
        mainHelpDialog = new MainHelpDialog(mainFrame, true);

        //workaround for better beansbinding logging issue
        org.jdesktop.beansbinding.util.logging.Logger.getLogger(ELProperty.class.getName()).setLevel(Level.SEVERE);

        //set close behaviour of main frame
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //find all projects
        projects.addAll(projectService.findAllWithEagerFetching());

        //init child controllers
        projectManagementController.init();
        proteinOverviewController.init();
        taskManagementController.init();

        //add panel components
        mainFrame.getProjectsManagementParentPanel().add(projectManagementController.getProjectManagementPanel(), gridBagConstraints);
        mainFrame.getProteinsParentPanel().add(proteinOverviewController.getProteinOverviewPanel(), gridBagConstraints);
        mainFrame.getTasksManagementParentPanel().add(taskManagementController.getTaskManagementPanel(), gridBagConstraints);

        //add action listeners
        //add menu item action listener
        mainFrame.getExitMenuItem().addActionListener(this);
        mainFrame.getProjectsManagementMenuItem().addActionListener(this);
        mainFrame.getProjectsOverviewMenuItem().addActionListener(this);
        mainFrame.getHelpMenuItem().addActionListener(this);

        userLoginDialog.getLoginButton().addActionListener(e -> {
            if (!userLoginDialog.getUserNameTextField().getText().isEmpty() && userLoginDialog.getUserPasswordTextField().getPassword().length != 0) {
                onLogin();
            } else {
                showMessageDialog("Login validation fail", "Please provide a user name and password.", JOptionPane.WARNING_MESSAGE);
            }
        });

        userLoginDialog.getCancelButton().addActionListener(e -> {
            userLoginDialog.dispose();
            System.exit(0);
        });

        userLoginDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent we) {
                System.exit(0);
            }
        });

        userLoginDialog.getUserPasswordTextField().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);

                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    userLoginDialog.getLoginButton().doClick();
                }
            }
        });

        //add change listener to tabbed pane
        mainFrame.getMainTabbedPane().addChangeListener(e -> {
            if (getSelectedTabTitle().equals(MainFrame.TASKS_TAB_TITLE)) {
                //check connection to distributed queues
                if (queueManager.isReachable()) {
                    taskManagementController.updateMonitoringTables();

                    taskManagementController.getTaskManagementPanel().setVisible(true);
                } else {
                    eventBus.post(new StorageQueuesConnectionErrorMessageEvent(queueManager.getBrokerName(), queueManager.getBrokerUrl(), queueManager.getBrokerJmxUrl()));
                }
            }
        });
    }

    @Override
    public void showView() {
        mainFrame.setExtendedState(mainFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
        //show login dialog
        userLoginDialog.setLocationRelativeTo(mainFrame);
        userLoginDialog.setVisible(true);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        String menuItemLabel = e.getActionCommand();

        if (menuItemLabel.equals(mainFrame.getExitMenuItem().getText())) {
            mainFrame.dispatchEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING));
        } else if (menuItemLabel.equals(mainFrame.getProjectsManagementMenuItem().getText())) {
            mainFrame.getMainTabbedPane().setSelectedComponent(mainFrame.getProjectsManagementParentPanel());
        } else if (menuItemLabel.equals(mainFrame.getUserManagementMenuItem().getText())) {
            userManagementParentController.showView();
        } else if (menuItemLabel.equals(mainFrame.getInstrumentManagementMenuItem().getText())) {
            instrumentManagementController.showView();
        } else if (menuItemLabel.equals(mainFrame.getMaterialManagementMenuItem().getText())) {
            materialManagementController.showView();
        } else if (menuItemLabel.equals(mainFrame.getProtocolManagementMenuItem().getText())) {
            protocolManagementController.showView();
        } else if (menuItemLabel.equals(mainFrame.getHelpMenuItem().getText())) {
            GuiUtils.centerDialogOnComponent(mainFrame, mainHelpDialog);
            mainHelpDialog.setVisible(true);
        }
    }

    /**
     * Listen to a CompletedDbTaskEvent and update the projects list and
     * different views accordingly.
     *
     * @param completedDbTaskEvent the completed database task event
     */
    @Subscribe
    public void onCompletedDbTaskEvent(final CompletedDbTaskEvent completedDbTaskEvent) {
        try {
            CompletedDbTask completedDbTask = completedDbTaskEvent.getCompletedDbTask();
            //check task type
            //if the task is a persist database task, get the sample with fetched runs
            if (completedDbTask.getDbTask() instanceof PersistDbTask) {
                PersistDbTask persistDbTask = (PersistDbTask) completedDbTask.getDbTask();
                java.util.List<AnalyticalRun> analyticalRuns = analyticalRunService.findBySampleId(persistDbTask.getEnitityId());

                //find the sample in the projects list
                Sample sample = findSampleById(persistDbTask.getEnitityId());
                if (sample != null) {
                    sample.setAnalyticalRuns(analyticalRuns);
                    eventBus.post(new SampleChangeEvent(EntityChangeEvent.Type.RUNS_ADDED, sample.getId(), analyticalRuns));
                } else {
                    //the sample was not found so another user persisted the given project/experiment/sample
                    //and this client was not updated yet
                    Object[] parentIds = sampleService.getParentIds(persistDbTask.getEnitityId());
                    //first check if the project is present in this client
                    Long projectId = (Long) parentIds[0];
                    Project project = findProjectById(projectId);
                    if (project == null) {
                        //get the project with eager fetching and add it to the projects
                        projects.add(projectService.findByIdWithEagerFetching(projectId));
                        eventBus.post(new ProjectChangeEvent(EntityChangeEvent.Type.CREATED, projectId));
                    } else {
                        //check if the experiment is present in the project management view
                        Long experimentId = (Long) parentIds[1];
                        if (isExperimentPresent(experimentId)) {
                            //add the experiment to the previously found project
                            project.getExperiments().add(experimentService.findByIdWithEagerFetching(experimentId));
                            eventBus.post(new ExperimentChangeEvent(EntityChangeEvent.Type.CREATED, experimentId));
                        }
                    }
                }
            } else {
                DeleteDbTask deleteDbTask = (DeleteDbTask) completedDbTask.getDbTask();
                removeFromProjects(deleteDbTask);
            }
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex.getCause());
        }
    }

    /**
     * Listen to a MessageEvent.
     *
     * @param messageEvent the message event
     */
    @Subscribe
    public void onMessageEvent(final MessageEvent messageEvent) {
        showMessageDialog(messageEvent.getMessageTitle(), messageEvent.getMessage(), messageEvent.getMessageType());
    }

    /**
     * In case of a permission error, show permission error dialog with the
     * error message.
     *
     * @param message the error message
     */
    public void showPermissionErrorDialog(final String message) {
        showMessageDialog("Permission warning", "A permission warning occurred: "
                + System.lineSeparator() + message
                + System.lineSeparator() + "Please contact the admin if you want to change your user permissions.", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Listen to an MaterialChangeEvent.
     *
     * @param materialChangeEvent the material change event
     */
    @Subscribe
    public void onMaterialChangeEvent(final MaterialChangeEvent materialChangeEvent) {
        updateProjects(materialChangeEvent);
    }

    /**
     * Listen to an InstrumentChangeEvent.
     *
     * @param instrumentChangeEvent the instrument change event
     */
    @Subscribe
    public void onInstrumentChangeEvent(final InstrumentChangeEvent instrumentChangeEvent) {
        updateProjects(instrumentChangeEvent);
    }

    /**
     * Listen to an ProtocolChangeEvent.
     *
     * @param protocolChangeEvent the protocol change event
     */
    @Subscribe
    public void onProtocolChangeEvent(final ProtocolChangeEvent protocolChangeEvent) {
        updateProjects(protocolChangeEvent);
    }

    /**
     * Get the selected tab title of the main tabbed pane.
     *
     * @return the selected tab title
     */
    public String getSelectedTabTitle() {
        JTabbedPane mainTabbedPane = mainFrame.getMainTabbedPane();
        return mainTabbedPane.getTitleAt(mainTabbedPane.getSelectedIndex());
    }

    /**
     * In case of an unexpected error, show error dialog with the error message.
     *
     * @param message the error message
     */
    private void showUnexpectedErrorDialog(final String message) {
        showMessageDialog("Unexpected error", "An unexpected error occured: "
                + System.lineSeparator() + message
                + System.lineSeparator() + "please try to rerun the application.", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Reload all projects from the database if necessary.
     *
     * @param entityChangeEvent the entity change event
     */
    private void updateProjects(final EntityChangeEvent entityChangeEvent) {
        if (entityChangeEvent.getType().equals(EntityChangeEvent.Type.UPDATED)) {
            projects.clear();
            projects.addAll(projectService.findAllWithEagerFetching());
        }
    }

    /**
     * Check the user credentials and init the admin section if necessary. If
     * unsuccessful, show a message dialog and reset the input fields.
     */
    private void onLogin() {
        //check if a user with given user name and password is found in the db
        LOGGER.info("Login attempt with user name: " + userLoginDialog.getUserNameTextField().getText());
        User currentUser = userService.findByLoginCredentials(userLoginDialog.getUserNameTextField().getText(), String.valueOf(userLoginDialog.getUserPasswordTextField().getPassword()));
        if (currentUser != null) {
            LOGGER.info("User " + userLoginDialog.getUserNameTextField().getText() + " successfully logged in.");
            userLoginDialog.dispose();

            //set current user in authentication bean after fetching the authentication relations
            userService.fetchAuthenticationRelations(currentUser);
            userBean.setCurrentUser(currentUser);

            //init this panel here because it depends on the userBean
            userQueryController.init();
            mainFrame.getUserQueryParentPanel().add(userQueryController.getUserQueryPanel(), gridBagConstraints);

            if (userBean.isAdmin()) {
                initAdminSection();
            } else {
                //disable admin menu
                mainFrame.getAdminMenu().setEnabled(false);
            }
        } else {
            showMessageDialog("Login fail", "No user with the given credentials could be found, please try again.", JOptionPane.ERROR_MESSAGE);
            userLoginDialog.getUserNameTextField().setText("");
            userLoginDialog.getUserPasswordTextField().setText("");
        }
    }

    /**
     * Shows a message dialog.
     *
     * @param title       the dialog title
     * @param message     the dialog message
     * @param messageType the dialog message type
     */
    private void showMessageDialog(final String title, final String message, final int messageType) {
        GuiUtils.showMessageDialog(mainFrame.getContentPane(), title, message, messageType);
    }

    /**
     * Init the admin section. This method is only called if the user is an
     * admin user.
     */
    private void initAdminSection() {
        //add action listeners
        mainFrame.getUserManagementMenuItem().addActionListener(this);
        mainFrame.getInstrumentManagementMenuItem().addActionListener(this);
        mainFrame.getMaterialManagementMenuItem().addActionListener(this);
        mainFrame.getProtocolManagementMenuItem().addActionListener(this);
    }

    /**
     * Find the project by ID in the projects list. Returns null if nothing was
     * found.
     *
     * @param projectId the project ID
     * @return the found Project instance
     */
    private Project findProjectById(Long projectId) {
        Optional<Project> foundProject = projects.stream()
                .filter(project -> project.getId().equals(projectId))
                .findFirst();
        return foundProject.orElse(null);
    }

    /**
     * Check if the experiment associated with one of the projects in the
     * projects list is present.
     *
     * @param experimentId the experiment ID
     * @return the found Project instance
     */
    private boolean isExperimentPresent(Long experimentId) {
        return projects.stream()
                .anyMatch(project -> project.getExperiments()
                        .stream()
                        .anyMatch(experiment -> experiment.getId().equals(experimentId)));
    }

    /**
     * Find the sample associated with one of the projects in the projects list
     * by ID. Returns null if nothing was found.
     *
     * @param sampleId the sample ID
     * @return the found Sample instance
     */
    private Sample findSampleById(Long sampleId) {
        Sample foundSample = null;
        outerloop:
        for (Project project : projects) {
            for (Experiment experiment : project.getExperiments()) {
                for (Sample sample : experiment.getSamples()) {
                    if (sample.getId().equals(sampleId)) {
                        foundSample = sample;
                        break outerloop;
                    }
                }
            }
        }
        return foundSample;
    }

    /**
     * Remove the entity from the projects list by class (Project, Experiment,
     * Sample and AnalyticalRun) and ID and post the appropriate event on the
     * event bus.
     *
     * @param deleteDbTask the DeleteDbTask instance
     */
    private void removeFromProjects(DeleteDbTask deleteDbTask) {
        if (deleteDbTask.getDbEntityClass().equals(Project.class)) {
            boolean removed = projects.removeIf(project -> project.getId().equals(deleteDbTask.getEnitityId()));
            if (removed) {
                eventBus.post(new ProjectChangeEvent(EntityChangeEvent.Type.DELETED, deleteDbTask.getEnitityId()));
            }
        } else if (deleteDbTask.getDbEntityClass().equals(Experiment.class)) {
            for (Project project : projects) {
                boolean removed = project.getExperiments().removeIf(experiment -> experiment.getId().equals(deleteDbTask.getEnitityId()));
                if (removed) {
                    eventBus.post(new ExperimentChangeEvent(EntityChangeEvent.Type.DELETED, deleteDbTask.getEnitityId()));
                    break;
                }
            }
        } else if (deleteDbTask.getDbEntityClass().equals(Sample.class)) {
            outerloop:
            for (Project project : projects) {
                for (Experiment experiment : project.getExperiments()) {
                    boolean removed = experiment.getSamples().removeIf(sample -> sample.getId().equals(deleteDbTask.getEnitityId()));
                    if (removed) {
                        eventBus.post(new SampleChangeEvent(EntityChangeEvent.Type.DELETED, deleteDbTask.getEnitityId()));
                        break outerloop;
                    }
                }
            }
        } else if (deleteDbTask.getDbEntityClass().equals(AnalyticalRun.class)) {
            outerloop:
            for (Project project : projects) {
                for (Experiment experiment : project.getExperiments()) {
                    for (Sample sample : experiment.getSamples()) {
                        boolean removed = sample.getAnalyticalRuns().removeIf(analyticalRun -> analyticalRun.getId().equals(deleteDbTask.getEnitityId()));
                        if (removed) {
                            eventBus.post(new AnalyticalRunChangeEvent(EntityChangeEvent.Type.DELETED, deleteDbTask.getEnitityId(), sample.getId()));
                            break outerloop;
                        }
                    }
                }
            }
        }
    }

}

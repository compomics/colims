/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.client.controller;

import com.compomics.colims.client.event.UserChangeEvent;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.ProjectSetupPanel;
import com.compomics.colims.core.io.PeptideShakerIO;
import com.compomics.colims.core.io.model.PeptideShakerImport;
import com.compomics.colims.core.io.mapper.ExperimentMapper;
import com.compomics.colims.core.service.ProjectService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.Experiment;
import com.compomics.colims.model.Project;
import com.compomics.colims.model.User;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.*;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("projectSetupController")
public class ProjectSetupController {

    private static final Logger LOGGER = Logger.getLogger(ProjectSetupController.class);
    private static final String PROJECT_META_DATA_CARD_NAME = "projectMetaDataPanel";
    private static final String IMPORT_DATA_IMPORT_CARD_NAME = "dataImportPanel";
    //model
    private Project project;
    private ObservableList<User> userBindingList;
    private BindingGroup bindingGroup;
    private Resource cpsResource;
    private Resource mgfResource;
    private Resource fastaResource;
    //view
    private ProjectSetupPanel projectSetupPanel;
    //main controller
    @Autowired
    private MainController mainController;
    @Autowired
    private EventBus eventBus;
    @Autowired
    private PeptideShakerIO peptideShakerIO;
    @Autowired
    private ExperimentMapper experimentMapper;
    //services
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserService userService;

    public ObservableList<User> getUserBindingList() {
        return userBindingList;
    }

    public ProjectSetupPanel getProjectSetupPanel() {
        return projectSetupPanel;
    }

    /**
     * Controller init method.
     */
    public void init() {
        //init view
        projectSetupPanel = new ProjectSetupPanel();

        //register to event bus
        eventBus.register(this);

        //init project
        project = new Project();

        //disable back button
        projectSetupPanel.getBackButton().setEnabled(false);
        //disable finish button
        projectSetupPanel.getFinishButton().setEnabled(false);

        //show info
        showProceedInfo("Click on proceed to import data files.");

        //init binding
        bindingGroup = new BindingGroup();

        userBindingList = ObservableCollections.observableList(userService.findAll());
        JComboBoxBinding userComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ, userBindingList, projectSetupPanel.getUserComboBox(), "userComboBoxBinding");
        bindingGroup.addBinding(userComboBoxBinding);

        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, projectSetupPanel.getTitleTextField(), ELProperty.create("${text}"), project, BeanProperty.create("title"), "titleBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, projectSetupPanel.getDescriptionTextArea(), ELProperty.create("${text}"), project, BeanProperty.create("description"), "descriptionBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, projectSetupPanel.getUserComboBox(), ELProperty.create("${selectedItem}"), project, BeanProperty.create("user"), "userBinding");
        bindingGroup.addBinding(binding);

        bindingGroup.bind();

        //get cps file chooser
        JFileChooser cpsFileChooser = projectSetupPanel.getCpsFileChooser();
        cpsFileChooser.setBackground(Color.WHITE);
        //select only files
        cpsFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        //select multiple file
        cpsFileChooser.setMultiSelectionEnabled(false);
        //set mzML filter
        cpsFileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }

                int index = f.getName().lastIndexOf(".");
                String extension = f.getName().substring(index + 1);
                if (extension != null) {
                    if (extension.equals("cps")) {
                        return true;
                    } else {
                        return false;
                    }
                }

                return false;
            }

            @Override
            public String getDescription() {
                return ("PeptideShaker .cps files");
            }
        });

        //get mgf file chooser
        JFileChooser mgfFileChooser = projectSetupPanel.getMgfFileChooser();
        mgfFileChooser.setBackground(Color.WHITE);
        //select only files
        mgfFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        //select multiple file
        mgfFileChooser.setMultiSelectionEnabled(false);
        //set mzML filter
        mgfFileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }

                int index = f.getName().lastIndexOf(".");
                String extension = f.getName().substring(index + 1);
                if (extension != null) {
                    if (extension.equals("mgf")) {
                        return true;
                    } else {
                        return false;
                    }
                }

                return false;
            }

            @Override
            public String getDescription() {
                return (".mgf files");
            }
        });

        //get fasta file chooser
        JFileChooser fastaFileChooser = projectSetupPanel.getFastaFileChooser();
        fastaFileChooser.setBackground(Color.WHITE);
        //select only files
        fastaFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        //select multiple file
        fastaFileChooser.setMultiSelectionEnabled(false);
        //set mzML filter
        fastaFileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }

                int index = f.getName().lastIndexOf(".");
                String extension = f.getName().substring(index + 1);
                if (extension != null) {
                    if (extension.equals("fasta")) {
                        return true;
                    } else {
                        return false;
                    }
                }

                return false;
            }

            @Override
            public String getDescription() {
                return (".fasta files");
            }
        });

        projectSetupPanel.getBackButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getCardLayout().previous(projectSetupPanel.getTopPanel());
                onCardSwitch();
            }
        });

        //add action listeners
        projectSetupPanel.getProceedButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (GuiUtils.getCurrentCardName(projectSetupPanel.getTopPanel()).equals(PROJECT_META_DATA_CARD_NAME)) {
                    //validate project
                    List<String> validationMessages = GuiUtils.validateEntity(project);

                    if (validationMessages.isEmpty()) {
                        getCardLayout().next(projectSetupPanel.getTopPanel());
                        onCardSwitch();
                    } else {
                        mainController.showMessageDialog("Validation failed", validationMessages, JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    getCardLayout().next(projectSetupPanel.getTopPanel());
                    onCardSwitch();
                }
            }
        });

        projectSetupPanel.getSelectCpsButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //in response to the button click, show open dialog 
                int returnVal = projectSetupPanel.getCpsFileChooser().showOpenDialog(projectSetupPanel);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    cpsResource = new FileSystemResource(projectSetupPanel.getCpsFileChooser().getSelectedFile());

                    //show file name in label
                    projectSetupPanel.getCpsFileLabel().setText(cpsResource.getFilename());
                }
            }
        });

        projectSetupPanel.getSelectMgfButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //in response to the button click, show open dialog 
                int returnVal = projectSetupPanel.getMgfFileChooser().showOpenDialog(projectSetupPanel);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    mgfResource = new FileSystemResource(projectSetupPanel.getMgfFileChooser().getSelectedFile());

                    //show file name in label
                    projectSetupPanel.getMgfFileLabel().setText(mgfResource.getFilename());
                }
            }
        });

        projectSetupPanel.getSelectFastaButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //in response to the button click, show open dialog 
                int returnVal = projectSetupPanel.getFastaFileChooser().showOpenDialog(projectSetupPanel);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    fastaResource = new FileSystemResource(projectSetupPanel.getFastaFileChooser().getSelectedFile());

                    //show file name in label
                    projectSetupPanel.getFastaFileLabel().setText(fastaResource.getFilename());
                }
            }
        });

        projectSetupPanel.getFinishButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ((cpsResource != null && cpsResource.exists()) && (mgfResource != null && mgfResource.exists()) && (fastaResource != null && fastaResource.exists())) {
                    PersistProjectSwingWorker persistProjectSwingWorker = new PersistProjectSwingWorker();
                    persistProjectSwingWorker.execute();
                } else {
                    mainController.showMessageDialog("Files missing", "Please provide the necessary files (.csp, .mgf and .fasta)", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

    }

    /**
     * Listens to an UserChangeEvent. Removes, adds or updates the user in the
     * userBindingList.
     *
     * @param userChangeEvent the UserChangeEvent
     */
    @Subscribe
    public void onUserChangeEvent(UserChangeEvent userChangeEvent) {
        User user = userChangeEvent.getUser();
        if (userBindingList.contains(user)) {
            userBindingList.remove(user);
        } else {
            userBindingList.add(user);
        }
    }

    private CardLayout getCardLayout() {
        return (CardLayout) projectSetupPanel.getTopPanel().getLayout();
    }

    private void onCardSwitch() {
        String currentCardName = GuiUtils.getCurrentCardName(projectSetupPanel.getTopPanel());
        if (currentCardName.equals(PROJECT_META_DATA_CARD_NAME)) {
            //disable back button
            projectSetupPanel.getBackButton().setEnabled(false);
            //disable finish button
            projectSetupPanel.getFinishButton().setEnabled(false);
            //enable proceed button
            projectSetupPanel.getProceedButton().setEnabled(true);
            //show info
            showProceedInfo("Click on proceed to import data files.");
        } else if (currentCardName.equals(IMPORT_DATA_IMPORT_CARD_NAME)) {
            //disable proceed button
            projectSetupPanel.getProceedButton().setEnabled(false);
            //enable back button
            projectSetupPanel.getBackButton().setEnabled(true);
            //enable finish button
            projectSetupPanel.getFinishButton().setEnabled(true);
            //show info
            showProceedInfo("Click on \"finish\" to store this project.");
        }
    }

    private void showProceedInfo(String message) {
        projectSetupPanel.getProceedInfoLabel().setText(message);
    }

    private class PersistProjectSwingWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            LOGGER.info("Start importing PeptideShaker file " + cpsResource.getFilename());
            PeptideShakerImport peptideShakerImport = peptideShakerIO.importPeptideShakerCpsArchive(cpsResource.getFile());
            LOGGER.info("Finished importing PeptideShaker file " + cpsResource.getFilename());

            //set mgf and fasta files
            List<File> mgfFiles = new ArrayList<File>();
            mgfFiles.add(mgfResource.getFile());
            peptideShakerImport.setMgfFiles(mgfFiles);
            peptideShakerImport.setFastaFile(fastaResource.getFile());

            LOGGER.info("Start mapping experiment for MSexperiment " + peptideShakerImport.getMsExperiment().getReference());
            Experiment experiment = new Experiment();
            experimentMapper.map(peptideShakerImport, experiment);
            LOGGER.info("Stop mapping experiment for MSexperiment " + peptideShakerImport.getMsExperiment().getReference());

            LOGGER.info("Start persisting project " + project.getTitle());            
            project.getExperiments().add(experiment);
            //set entity relations
            experiment.setProject(project);
            projectService.save(project);
            LOGGER.info("Finished persisting project " + project.getTitle());
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                mainController.showMessageDialog("Project save confirmation", "Project " + project.getTitle() + " was saved successfully", JOptionPane.INFORMATION_MESSAGE);
            } catch (InterruptedException ex) {
                LOGGER.error(ex.getMessage(), ex);
                mainController.showUnexpectedErrorDialog(ex.getMessage());
            } catch (ExecutionException ex) {
                LOGGER.error(ex.getMessage(), ex);
                mainController.showUnexpectedErrorDialog(ex.getMessage());
            } catch (CancellationException ex) {
                LOGGER.error(ex.getMessage(), ex);
                mainController.showUnexpectedErrorDialog(ex.getMessage());
            }
        }
    }
}

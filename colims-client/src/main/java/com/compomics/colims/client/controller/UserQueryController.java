package com.compomics.colims.client.controller;

import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.event.progress.ProgressEndEvent;
import com.compomics.colims.client.event.progress.ProgressStartEvent;
import com.compomics.colims.client.model.table.model.QueryResultsTableModel;
import com.compomics.colims.client.util.BasicFormatterImpl;
import com.compomics.colims.client.util.Functions;
import com.compomics.colims.client.view.UserQueryPanel;
import com.compomics.colims.core.service.UserQueryService;
import com.compomics.colims.model.UserBean;
import com.compomics.colims.model.enums.DefaultPermission;
import com.google.common.eventbus.EventBus;
import org.apache.log4j.Logger;
import org.hibernate.engine.jdbc.internal.Formatter;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.exception.SQLGrammarException;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * The user query view controller.
 *
 * @author Davy Maddelein
 */
@Component("userQueryController")
public class UserQueryController implements Controllable {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(UserQueryController.class);

    private static final Color SELECTED_BACKGROUND = new Color(57, 105, 138);
    private static final Color SELECTED_FOREGROUND = new Color(255, 255, 255);
    private static final Color BACKGROUND = new Color(255, 255, 255);
    private static final Color FOREGROUND = new Color(0, 0, 0);
    private static final String HTML_OPEN = "<html>";
    private static final String HTML_CLOSE = "</html>";
    private static final String HTML_BREAK = "<br>";
    private static final String EXPORT_DELIMITER = "\t";

    //model
    private final Formatter formatter;
    private BindingGroup bindingGroup;
    private ObservableList<String> queryStringBindingList;
    private ResultsExporterWorker resultsExporterWorker;
    //view
    private UserQueryPanel userQueryPanel;
    //parent controller
    @Autowired
    private MainController mainController;
    //child controller
    @Autowired
    private UserBean userBean;
    @Autowired
    private UserQueryService userQueryService;
    @Autowired
    private EventBus eventBus;

    /**
     * No-arg constructor.
     */
    public UserQueryController() {
        formatter = new BasicFormatterImpl();
    }

    /**
     * Get the view of this controller.
     *
     * @return the user query panel
     */
    public UserQueryPanel getUserQueryPanel() {
        return userQueryPanel;
    }

    @Override
    public void init() {
        //init view
        userQueryPanel = new UserQueryPanel();

        userQueryPanel.getQueryResultTable().setModel(new QueryResultsTableModel(new ArrayList<>()));

        queryStringBindingList = ObservableCollections.observableList(userQueryService.findQueriesByUserId(userBean.getCurrentUser().getId()));

        //add binding
        bindingGroup = new BindingGroup();

        JComboBoxBinding userQueryComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, queryStringBindingList, userQueryPanel.getUserQueryComboBox());
        bindingGroup.addBinding(userQueryComboBoxBinding);

        bindingGroup.bind();

        userQueryPanel.getUserQueryComboBox().setRenderer(new UserQueryComboBoxRenderer());

        userQueryPanel.getUserQueryComboBox().addActionListener(e -> {
            int selectedIndex = userQueryPanel.getUserQueryComboBox().getSelectedIndex();
            if (selectedIndex != -1) {
                String queryString = queryStringBindingList.get(selectedIndex);
                String formattedUserQueryString = getFormattedQueryString(queryString);
                userQueryPanel.getQueryInputTextArea().setText(formattedUserQueryString);
            }
        });

        userQueryPanel.getExecuteQueryButton().addActionListener(e -> {
            String queryString = userQueryPanel.getQueryInputTextArea().getText().trim();

            if (!queryString.isEmpty()) {
                if (userBean.getDefaultPermissions().get(DefaultPermission.READ)) {
                    userQueryPanel.getQueryInputTextArea().setText(formatter.format(queryString));

                    try {
                        //remove unnecessary spaces and line breaks
                        queryString = queryString.trim().replaceAll("\\s+", " ");

                        List<LinkedHashMap<String, Object>> queryResults = userQueryService.executeUserQuery(userBean.getCurrentUser(), queryString);
                        //fill table
                        if (queryResults.size() > 0) {
                            QueryResultsTableModel model = new QueryResultsTableModel(queryResults);
                            userQueryPanel.getQueryResultTable().setModel(model);

                            //add the user query to the combobox
                            if (!queryStringBindingList.contains(queryString)) {
                                queryStringBindingList.add(queryString);
                            }
                        }
                    } catch (GenericJDBCException executionException) {
                        LOGGER.error(executionException.getMessage(), executionException);
                        eventBus.post(new MessageEvent("Permission problem", "Cannot execute any commands that are not selects.", JOptionPane.ERROR_MESSAGE));
                    } catch (SQLGrammarException grammarException) {
                        LOGGER.error(grammarException.getMessage(), grammarException);
                        eventBus.post(new MessageEvent("Syntax problem", "There was a problem with your query: " + queryString + System.lineSeparator() + grammarException.getCause().getMessage(), JOptionPane.ERROR_MESSAGE));
                    }
                } else {
                    eventBus.post(new MessageEvent("Permission problem", "Your user doesn't have rights to execute this query.", JOptionPane.ERROR_MESSAGE));
                }
            } else {
                eventBus.post(new MessageEvent("Query execution", "Please insert a query to execute.", JOptionPane.ERROR_MESSAGE));
            }
        });

        userQueryPanel.getClearQueryButton().addActionListener(e -> {
            userQueryPanel.getQueryInputTextArea().setText("");
        });

        userQueryPanel.getClearResultsButton().addActionListener(e -> {
            clearQueryResultTable();
        });

        userQueryPanel.getExportResultsButton().addActionListener(e -> {
            QueryResultsTableModel model = getResultsTableModel();
            if (model.getRowCount() != 0) {
                //in response to the button click, show open dialog
                int returnVal = userQueryPanel.getExportFileChooser().showSaveDialog(userQueryPanel);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File resultsExportFile = userQueryPanel.getExportFileChooser().getSelectedFile();
                    if (!resultsExportFile.isDirectory()) {
                        ProgressStartEvent progressStartEvent = new ProgressStartEvent(mainController.getMainFrame(), true, 1, "Query results export progress. ");
                        eventBus.post(progressStartEvent);
                        resultsExporterWorker = new ResultsExporterWorker(resultsExportFile, model);
                        resultsExporterWorker.execute();
                    } else {
                        JOptionPane.showMessageDialog(userQueryPanel, "Please specify a file in the save text field, not a directory.", "Results export", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(userQueryPanel, "Something went wrong with choosing the export file.", "Results export", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(userQueryPanel, "There are no results to export.", "Results export", JOptionPane.INFORMATION_MESSAGE);
            }
        });

    }

    @Override
    public void showView() {
        //do nothing
    }

    /**
     * Clear the result table.
     */
    private void clearQueryResultTable() {
        ((QueryResultsTableModel) userQueryPanel.getQueryResultTable().getModel()).clear();
    }

    /**
     * Get the formatted query string.
     *
     * @param queryString the query string
     * @return the formatted query string
     */
    private String getFormattedQueryString(String queryString) {
        String formattedQueryString = formatter.format(queryString);

        return formattedQueryString;
    }

    /**
     * Get the formatted HTML query string.
     *
     * @param queryString the query string
     * @return the formatted HTML query string
     */
    private String getFormattedQueryHTMLString(String queryString) {
        String formattedQueryString = HTML_OPEN + formatter.format(queryString);

        //replace line breaks HTML breaks
        formattedQueryString = formattedQueryString.replaceAll(System.getProperty("line.separator"), HTML_BREAK);

        return formattedQueryString + HTML_CLOSE;
    }

    /**
     * Get the table model from the results table.
     *
     * @return the default table model
     */
    private QueryResultsTableModel getResultsTableModel() {
        return (QueryResultsTableModel) userQueryPanel.getQueryResultTable().getModel();
    }

    /**
     * Results exporter swing worker.
     */
    private class ResultsExporterWorker extends SwingWorker<Void, Void> {

        /**
         * The file where the results will be written to.
         */
        private final File resultsExportFile;
        /**
         * The query results table model.
         */
        private final QueryResultsTableModel model;

        public ResultsExporterWorker(File resultsExportFile, QueryResultsTableModel model) {
            this.resultsExportFile = resultsExportFile;
            this.model = model;
        }

        @Override
        protected Void doInBackground() throws Exception {

            LOGGER.info("Exporting results to file " + resultsExportFile.getName());
            try (FileOutputStream fos = new FileOutputStream(resultsExportFile);
                    OutputStreamWriter osw = new OutputStreamWriter(fos, Charset.forName("UTF-8").newEncoder());
                    BufferedWriter bw = new BufferedWriter(osw);
                    PrintWriter pw = new PrintWriter(bw)) {

                //write column headers
                pw.println(Arrays.stream(model.getColumnNames()).map(Functions.replaceNullWithEmptyString).collect(Collectors.joining(EXPORT_DELIMITER)));

                //write result rows
                model.getResultData().stream().forEach((row) -> {
                    pw.println(Arrays.stream(row).map(Functions.replaceNullWithEmptyString).collect(Collectors.joining(EXPORT_DELIMITER)));
                });
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
                eventBus.post(new MessageEvent("Query results export", "Something went wrong during the results export to file " + resultsExportFile.getName(), JOptionPane.ERROR_MESSAGE));
            }
            LOGGER.info("Finished exporting results to file " + resultsExportFile.getName());

            return null;
        }

        @Override
        protected void done() {
            try {
                get();
            } catch (InterruptedException | ExecutionException ex) {
                LOGGER.error(ex.getMessage(), ex);
            } catch (CancellationException ex) {
                LOGGER.info("Cancelling results export.");
            } finally {
                //hide progress dialog
                eventBus.post(new ProgressEndEvent());
            }
        }
    }

    /**
     * User queries combo box renderer.
     */
    private class UserQueryComboBoxRenderer extends BasicComboBoxRenderer {

        @Override
        public java.awt.Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            String queryString = (String) value;
            if (isSelected) {
                setBackground(SELECTED_BACKGROUND);
                setForeground(SELECTED_FOREGROUND);
                if (-1 < index) {
                    list.setToolTipText(getFormattedQueryHTMLString(queryString));
                }
            } else {
                setBackground(BACKGROUND);
                setForeground(FOREGROUND);
            }

            setFont(list.getFont());
            setText((queryString == null) ? "" : queryString);

            return this;
        }
    }
}

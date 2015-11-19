package com.compomics.colims.client.controller;

import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.util.BasicFormatterImpl;
import com.compomics.colims.client.view.UserQueryPanel;
import com.compomics.colims.core.service.UserQueryService;
import com.compomics.colims.model.UserQuery;
import com.compomics.colims.model.enums.DefaultPermission;
import com.compomics.colims.model.UserBean;
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
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

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

    //model
    private Formatter formatter;
    private BindingGroup bindingGroup;
    private ObservableList<UserQuery> userQueryBindingList;
    //view
    private UserQueryPanel userQueryPanel;
    //parent controller
    @Autowired
    private MainController mainController;
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
        //register to event bus
//        eventBus.register(this);

        //init view
        userQueryPanel = new UserQueryPanel();

        userQueryBindingList = ObservableCollections.observableList(userQueryService.findByUserId(userBean.getCurrentUser().getId()));

        //add binding
        bindingGroup = new BindingGroup();

        JComboBoxBinding userQueryComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, userQueryBindingList, userQueryPanel.getUserQueryComboBox());
        bindingGroup.addBinding(userQueryComboBoxBinding);

        bindingGroup.bind();

        userQueryPanel.getUserQueryComboBox().setRenderer(new UserQueryComboBoxRenderer());

        userQueryPanel.getUserQueryComboBox().addActionListener(e -> {
            int selectedIndex = userQueryPanel.getUserQueryComboBox().getSelectedIndex();
            if (selectedIndex != -1) {
                UserQuery userQuery = userQueryBindingList.get(selectedIndex);
                String formattedUserQueryString = getFormattedQueryString(userQuery.getQueryString());
                userQueryPanel.getQueryInputTextArea().setText(formattedUserQueryString);
            }
        });

        userQueryPanel.getExecuteQueryButton().addActionListener(e -> {
            String queryString = userQueryPanel.getQueryInputTextArea().getText();

            if (!queryString.isEmpty()) {
                if (userBean.getDefaultPermissions().get(DefaultPermission.READ)) {
                    clearResultTable();

                    userQueryPanel.getQueryInputTextArea().setText(formatter.format(queryString));

                    try {
                        //remove unnecessary spaces and line breaks
                        queryString = queryString.trim().replaceAll("\\s+", " ");

                        List<LinkedHashMap<String, Object>> results = userQueryService.executeUserQuery(userBean.getCurrentUser(), queryString);

                        //fill table
                        if (results.size() > 0) {
                            DefaultTableModel model = new DefaultTableModel();
                            results.get(0).keySet().forEach(model::addColumn);
                            Iterator<LinkedHashMap<String, Object>> iterator = results.iterator();

                            while (iterator.hasNext()) {
                                model.addRow(iterator.next().values().toArray());
                                //remove to lighten memory load
                                iterator.remove();
                            }

                            userQueryPanel.getQueryResultTable().setModel(model);

                            //look for the executed query in the user queries
//                            userQueryService.
                        }
                    } catch (GenericJDBCException executionException) {
                        LOGGER.error(executionException.getMessage(), executionException);
                        eventBus.post(new MessageEvent("Permission problem", "Cannot execute any commands that are not selects.", JOptionPane.ERROR_MESSAGE));
                    } catch (SQLGrammarException grammarException) {
                        LOGGER.error(grammarException.getMessage(), grammarException);
                        eventBus.post(new MessageEvent("Syntax problem", "There was a problem with your query: " + queryString, JOptionPane.ERROR_MESSAGE));
                    }
                } else {
                    eventBus.post(new MessageEvent("permission problem", "Your user doesn't have rights to execute this query.", JOptionPane.ERROR_MESSAGE));
                }
            } else {
                eventBus.post(new MessageEvent("Query execution", "Please insert a query to execute.", JOptionPane.ERROR_MESSAGE));
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
    private void clearResultTable() {
        DefaultTableModel model = (DefaultTableModel) userQueryPanel.getQueryResultTable().getModel();
        model.setRowCount(0);
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

    private class UserQueryComboBoxRenderer extends BasicComboBoxRenderer {

        @Override
        public java.awt.Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            UserQuery userQuery = (UserQuery) value;
            if (isSelected) {
                setBackground(SELECTED_BACKGROUND);
                setForeground(SELECTED_FOREGROUND);
                if (-1 < index) {
                    list.setToolTipText(getFormattedQueryHTMLString(userQuery.getQueryString()));
                }
            } else {
                setBackground(BACKGROUND);
                setForeground(FOREGROUND);
            }
            setText(userQuery.getQueryString());

            return this;
        }
    }
}

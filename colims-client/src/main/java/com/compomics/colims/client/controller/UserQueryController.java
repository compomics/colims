package com.compomics.colims.client.controller;

import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.view.UserQueryPanel;
import com.compomics.colims.core.service.UserQueryService;
import com.compomics.colims.model.enums.DefaultPermission;
import com.compomics.colims.repository.AuthenticationBean;
import com.google.common.base.CharMatcher;
import com.google.common.eventbus.EventBus;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.exception.SQLGrammarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;
import org.hibernate.engine.jdbc.internal.Formatter;

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

    private Formatter formatter;
    //view
    private UserQueryPanel userQueryPanel;
    //parent controller
    @Autowired
    private MainController colimsController;
    @Autowired
    private AuthenticationBean authenticationBean;
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

        userQueryPanel.getExecuteQueryButton().addActionListener(e -> {
            String queryString = userQueryPanel.getQueryInputTextArea().getText();

            if (!queryString.isEmpty()) {
                if (permissionToExecute(queryString)) {
                    String formattedQueryString = formatter.format(queryString);
                    userQueryPanel.getQueryInputTextArea().setText(formattedQueryString);

                    try {
                        List<LinkedHashMap<String, Object>> results = userQueryService.executeQuery(queryString);

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
                        } else {
                            eventBus.post(new MessageEvent("permission problem", "Your user doesn't have rights to execute this query.", JOptionPane.ERROR_MESSAGE));
                        }
                    } catch (GenericJDBCException executionException) {
                        LOGGER.error(executionException.getMessage(), executionException);
                        eventBus.post(new MessageEvent("Permission problem", "Cannot execute any commands that are not selects.", JOptionPane.ERROR_MESSAGE));
                    } catch (SQLGrammarException grammarException) {
                        LOGGER.error(grammarException.getMessage(), grammarException);
                        eventBus.post(new MessageEvent("Syntax problem", "There was a problem with your query: " + queryString, JOptionPane.ERROR_MESSAGE));
                    }
                }
                eventBus.post(new MessageEvent("Query execution", "Please insert a query to execute.", JOptionPane.ERROR_MESSAGE));
            }
        });

    }

    @Override
    public void showView() {
        //do nothing
    }

    private boolean permissionToExecute(String query) {
        return authenticationBean.getDefaultPermissions().get(DefaultPermission.READ);
    }
}

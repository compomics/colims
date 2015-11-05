package com.compomics.colims.client.controller;

import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.view.UserQueryPanel;
import com.compomics.colims.core.service.UserQueryService;
import com.compomics.colims.model.enums.DefaultPermission;
import com.compomics.colims.repository.AuthenticationBean;
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

/**
 * The user query view controller.
 *
 * @author Davy Maddelein
 */
@Component("userQueryController")
public class UserQueryController implements Controllable {

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

            if (permissionToExecute(queryString)) {
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
                    } else {
                        eventBus.post(new MessageEvent("permission problem", "Your user doesn't have rights to execute this query", JOptionPane.ERROR_MESSAGE));
                    }
                } catch (GenericJDBCException executionException) {
                    eventBus.post(new MessageEvent("permission problem", "cannot execute any commands that are not selects", JOptionPane.ERROR_MESSAGE));
                } catch (SQLGrammarException grammarException) {
                    eventBus.post(new MessageEvent("syntax problem", "there was a problem with your query: " + queryString, JOptionPane.ERROR_MESSAGE));
                }
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

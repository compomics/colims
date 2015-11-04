package com.compomics.colims.client.controller;

import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.util.LinkedAliasToEntityMapResultTransformer;
import com.compomics.colims.client.view.ManualQueryPanel;
import com.compomics.colims.model.enums.DefaultPermission;
import com.compomics.colims.repository.AuthenticationBean;
import com.google.common.eventbus.EventBus;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.exception.SQLGrammarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Davy Maddelein
 */
@Component("manualQueryPanelController")
public class ManualQueryPanelController implements Controllable {

    @Autowired
    private EventBus eventBus;
    @Autowired
    private MainController colimsController;
    @Autowired
    private AuthenticationBean authenticationBean;
    @Autowired
    private SessionFactory sessionFactory;


    private ManualQueryPanel manualQueryPanel;

    @Override
    public void init() {
        //register to event bus
//        eventBus.register(this);

        //init view
        manualQueryPanel = new ManualQueryPanel();

        manualQueryPanel.getExecuteQueryButton().addActionListener(e -> {

            String queryString = manualQueryPanel.getQueryInputArea().getText();

            if (permissionToExecute(queryString)) {

                //create and setup the return for the entered query
                SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(queryString);
                sqlQuery.setResultTransformer(LinkedAliasToEntityMapResultTransformer.INSTANCE());
                try {
                    List<LinkedHashMap<String, Object>> resultList = sqlQuery.list();

                    //fill table
                    if (resultList.size() > 0) {
                        DefaultTableModel model = new DefaultTableModel();
                        resultList.get(0).keySet().forEach(model::addColumn);
                        Iterator<LinkedHashMap<String, Object>> iter = resultList.iterator();

                        while (iter.hasNext()) {
                            model.addRow(iter.next().values().toArray());
                            //remove to lighten memory load
                            iter.remove();
                        }

                        manualQueryPanel.getResultTable().setModel(model);
                    } else {
                       eventBus.post(new MessageEvent("permission problem","Your user doesn't have rights to execute this query", JOptionPane.ERROR_MESSAGE));
                    }
                } catch (GenericJDBCException executionException) {
                    eventBus.post(new MessageEvent("permission problem","cannot execute any commands that are not selects", JOptionPane.ERROR_MESSAGE));

                } catch (SQLGrammarException grammarException){
                    eventBus.post(new MessageEvent("syntax problem","there was a problem with your query: " + queryString, JOptionPane.ERROR_MESSAGE));
                }
            }
        });

    }

    @Override
    public void showView() {
        //do nothing
    }

    public ManualQueryPanel getManualQueryPanel() {
        return manualQueryPanel;
    }

    private boolean permissionToExecute(String query) {
        return authenticationBean.getDefaultPermissions().get(DefaultPermission.READ);
    }
}

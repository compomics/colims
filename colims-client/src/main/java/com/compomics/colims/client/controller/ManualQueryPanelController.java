package com.compomics.colims.client.controller;

import com.compomics.colims.client.view.ManualQueryPanel;
import com.compomics.colims.model.enums.DefaultPermission;
import com.compomics.colims.repository.AuthenticationBean;
import com.google.common.eventbus.EventBus;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Locale;

/**
 * @author Davy Maddelein
 */

@Component("manualQueryPanelController")
public class ManualQueryPanelController implements Controllable {


    @Autowired
    private EventBus eventBus;
    @Autowired
    private MainController mainController;
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


        manualQueryPanel.getExecuteQueryButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                String query = manualQueryPanel.getQueryInputArea().getText();

                if (permissionToExecute(query)) {

                    SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(query);
                    List<Object[]> resultList = sqlQuery.list();
                    if (resultList.size() > 0) {
                        DefaultTableModel model = new DefaultTableModel(sqlQuery.getReturnAliases(), resultList.size());
                        for (Object[] returned : resultList) {
                            model.addRow(returned);
                        }
                        manualQueryPanel.getResultTable().setModel(model);
                    } else {
                        mainController.showPermissionErrorDialog("Your user doesn't have rights to execute this query");
                    }
                }
            }
        });

    }

    @Override
    public void showView() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ManualQueryPanel getManualQueryPanel() {
        return manualQueryPanel;
    }

    private boolean permissionToExecute(String query) {
        query = query.toUpperCase(Locale.UK);
        return authenticationBean.getDefaultPermissions().get(DefaultPermission.READ)
                || query.contains("DELETE") && authenticationBean.getDefaultPermissions().get(DefaultPermission.DELETE)
                || (query.contains("UPDATE") && authenticationBean.getDefaultPermissions().get(DefaultPermission.UPDATE))
                || (query.contains("INSERT") && authenticationBean.getDefaultPermissions().get(DefaultPermission.CREATE));
    }
}

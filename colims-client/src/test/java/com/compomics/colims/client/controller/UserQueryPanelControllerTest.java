package com.compomics.colims.client.controller;

import com.compomics.colims.client.controller.admin.FastaDbSaveUpdateController;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.model.table.model.QueryResultsTableModel;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.User;
import com.compomics.colims.model.UserBean;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.io.IOException;
import junit.framework.TestCase;
import org.apache.commons.configuration.ConfigurationException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Davy Maddelein on 30/07/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-client-context.xml", "classpath:colims-client-test-context.xml"})
@Rollback
@Transactional
public class UserQueryPanelControllerTest extends TestCase {

    private String message = "";

    @Autowired
    private UserService userService;
    @Autowired
    private UserBean userBean;
    @Autowired
    private UserQueryController userQueryController;
    @Autowired
    private EventBus eventBus;

    @Subscribe
    public void getMessagesFromEventBus(MessageEvent e) {
        message = e.getMessage();
    }

    @Before
    public void initTests() {
        eventBus.register(this);

        User user = userService.findByName("admin");
        userService.fetchAuthenticationRelations(user);
        userBean.setCurrentUser(user);

        message = "";

        userQueryController.init();
    }

    @Test
    public void testInit() throws Exception {
        QueryResultsTableModel queryResultsTableModel = (QueryResultsTableModel) userQueryController.getUserQueryPanel().getQueryResultTable().getModel();
        queryResultsTableModel.clear();

        userQueryController.getUserQueryPanel().getQueryInputTextArea().setText("select * from experiment");
        userQueryController.getUserQueryPanel().getExecuteQueryButton().doClick();

        assertThat(userQueryController.getUserQueryPanel().getQueryResultTable().getColumnCount(), is(9));
        assertThat(userQueryController.getUserQueryPanel().getQueryResultTable().getRowCount(), is(2));
        assertThat(userQueryController.getUserQueryPanel().getQueryResultTable().getModel().getValueAt(0, 3), is("admin"));
    }

    @Test
    public void testPermissionNotAllowed() {
        QueryResultsTableModel queryResultsTableModel = (QueryResultsTableModel) userQueryController.getUserQueryPanel().getQueryResultTable().getModel();
        queryResultsTableModel.clear();

        User user = userService.findByName("collab1");
        userService.fetchAuthenticationRelations(user);
        userBean.setCurrentUser(user);

        userQueryController.getUserQueryPanel().getQueryInputTextArea().setText("select * from experiment");
        userQueryController.getUserQueryPanel().getExecuteQueryButton().doClick();

        assertThat(userQueryController.getUserQueryPanel().getQueryResultTable().getRowCount(), is(2));

        userQueryController.getUserQueryPanel().getQueryInputTextArea().setText("INSERT INTO protein (id, protein_sequence) VALUES (1, 'AAAAAAAAAAAAAAAAAAAAAAABLENNARTMAAAAAAAAAAAAA')");
        userQueryController.getUserQueryPanel().getExecuteQueryButton().doClick();

        assertThat(message, is("Cannot execute any commands that are not selects."));
    }

    @Test
    public void testFaultyQuery() {
        QueryResultsTableModel queryResultsTableModel = (QueryResultsTableModel) userQueryController.getUserQueryPanel().getQueryResultTable().getModel();
        queryResultsTableModel.clear();

        userQueryController.getUserQueryPanel().getQueryInputTextArea().setText("select * this is totally not correct syntax");
        userQueryController.getUserQueryPanel().getExecuteQueryButton().doClick();

        Assert.assertTrue(message.contains("There was a problem with your query:"));
    }

    @Test
    public void testSQLInjection() {
        QueryResultsTableModel queryResultsTableModel = (QueryResultsTableModel) userQueryController.getUserQueryPanel().getQueryResultTable().getModel();
        queryResultsTableModel.clear();

        userQueryController.getUserQueryPanel().getQueryInputTextArea().setText(
                "DELETE FROM experiment WHERE 1 or user_name = ' '");
        userQueryController.getUserQueryPanel().getExecuteQueryButton().doClick();

        assertThat(userQueryController.getUserQueryPanel().getQueryResultTable().getRowCount(), is(0));
        assertThat(message, is("Cannot execute any commands that are not selects."));

        userQueryController.getUserQueryPanel().getQueryInputTextArea().setText("select * from experiment");
        userQueryController.getUserQueryPanel().getExecuteQueryButton().doClick();

        assertThat(userQueryController.getUserQueryPanel().getQueryResultTable().getRowCount(), is(2));
    }
}

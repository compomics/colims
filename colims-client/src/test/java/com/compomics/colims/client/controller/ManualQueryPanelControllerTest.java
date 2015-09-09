package com.compomics.colims.client.controller;

import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.User;
import com.compomics.colims.repository.AuthenticationBean;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Davy Maddelein on 30/07/2015.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-client-context.xml", "classpath:colims-client-test-context.xml"})
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class ManualQueryPanelControllerTest extends TestCase {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationBean authenticationBean;
    @Autowired
    private ManualQueryPanelController manualQueryPanelController;
    @Autowired
    private EventBus eventBus;

    String message = "";

    @Subscribe
    public void getMessagesFromEventBus(MessageEvent e) {
        message = e.getMessage();
    }

    @Before
    public void initTests() {
        eventBus.register(this);

        User user = userService.findByName("admin");
        userService.fetchAuthenticationRelations(user);
        authenticationBean.setCurrentUser(user);

        message = "";

        manualQueryPanelController.init();
    }

    @Test
    public void testInit() throws Exception {
        manualQueryPanelController.getManualQueryPanel().getQueryInputArea().setText("select * from experiment");

        manualQueryPanelController.getManualQueryPanel().getExecuteQueryButton().doClick();

        assertThat(manualQueryPanelController.getManualQueryPanel().getResultTable().getColumnCount(), is(9));

        assertThat(manualQueryPanelController.getManualQueryPanel().getResultTable().getRowCount(), is(2));

        assertThat(manualQueryPanelController.getManualQueryPanel().getResultTable().getModel().getValueAt(0, 3), is("admin"));
    }

    @Test
    public void testPermissionNotAllowed() {
        User user = userService.findByName("collab1");
        userService.fetchAuthenticationRelations(user);
        authenticationBean.setCurrentUser(user);

        manualQueryPanelController.getManualQueryPanel().getQueryInputArea().setText("select * from experiment");

        manualQueryPanelController.getManualQueryPanel().getExecuteQueryButton().doClick();

        assertThat(manualQueryPanelController.getManualQueryPanel().getResultTable().getRowCount(), is(2));

        manualQueryPanelController.getManualQueryPanel().getQueryInputArea().setText(
                "INSERT INTO colims_user (id, creation_date, modification_date, user_name, email, first_name, last_name, name, password, l_institution_id) " +
                        "VALUES (10,'2012-06-27 14:42:16','2012-06-27 14:49:46','new_user','admin11@test.com','admin1_first_name','admin1_last_name','new_user','/VcrldJXLdkuNRe5JHMtO4S/0plRymxt',1)");

        manualQueryPanelController.getManualQueryPanel().getExecuteQueryButton().doClick();

        assertThat(message, is("cannot execute any commands that are not selects"));
    }

    @Test
    public void testFaultyQuery() {
        manualQueryPanelController.getManualQueryPanel().getQueryInputArea().setText("select * this is totally not correct syntax");

        manualQueryPanelController.getManualQueryPanel().getExecuteQueryButton().doClick();

        assertThat(message, is("there was a problem with your query: select * this is totally not correct syntax"));
    }

    @Test
    public void testSQLInjection() {
        manualQueryPanelController.getManualQueryPanel().getQueryInputArea().setText(
                "SELECT * FROM colims_user WHERE username = ' '; DELETE FROM experiment WHERE 1 or username = ' '");

        manualQueryPanelController.getManualQueryPanel().getExecuteQueryButton().doClick();

        assertThat(manualQueryPanelController.getManualQueryPanel().getResultTable().getRowCount(), is(0));

        assertThat(message, is("there was a problem with your query: SELECT * FROM colims_user WHERE username = ' '; DELETE FROM experiment WHERE 1 or username = ' '"));

        manualQueryPanelController.getManualQueryPanel().getQueryInputArea().setText("select * from experiment");

        manualQueryPanelController.getManualQueryPanel().getExecuteQueryButton().doClick();

        assertThat(manualQueryPanelController.getManualQueryPanel().getResultTable().getRowCount(), is(2));
    }
}
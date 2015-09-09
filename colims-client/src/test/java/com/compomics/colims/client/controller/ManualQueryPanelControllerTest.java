package com.compomics.colims.client.controller;

import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.User;
import com.compomics.colims.repository.AuthenticationBean;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
@Transactional
public class ManualQueryPanelControllerTest extends TestCase {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationBean authenticationBean;
    @Autowired
    private ManualQueryPanelController manualQueryPanelController;

    @Test
    public void testInit() throws Exception {
        User user = userService.findByName("admin");
        userService.fetchAuthenticationRelations(user);
        authenticationBean.setCurrentUser(user);

        manualQueryPanelController.init();

        manualQueryPanelController.getManualQueryPanel().getQueryInputArea().setText("select * from experiment");

        manualQueryPanelController.getManualQueryPanel().getExecuteQueryButton().doClick();

        assertThat(manualQueryPanelController.getManualQueryPanel().getResultTable().getColumnCount(), is(9));

        assertThat(manualQueryPanelController.getManualQueryPanel().getResultTable().getRowCount(), is(2));
    }

    @Test
    public void testPermissionNotAllowed() {
        User user = userService.findByName("collab1");
        userService.fetchAuthenticationRelations(user);
        authenticationBean.setCurrentUser(user);

        manualQueryPanelController.init();

        manualQueryPanelController.getManualQueryPanel().getQueryInputArea().setText("select * from experiment");

        manualQueryPanelController.getManualQueryPanel().getExecuteQueryButton().doClick();

        assertThat(manualQueryPanelController.getManualQueryPanel().getResultTable().getRowCount(), is(4));

        manualQueryPanelController.getManualQueryPanel().getQueryInputArea().setText("" +
                "INSERT INTO colims_user (id, creation_date, modification_date, user_name, email, first_name, last_name, name, password, l_institution_id) " +
                "VALUES (10,'2012-06-27 14:42:16','2012-06-27 14:49:46','new_user','admin11@test.com','admin1_first_name','admin1_last_name','new_user','/VcrldJXLdkuNRe5JHMtO4S/0plRymxt',1)");

        manualQueryPanelController.getManualQueryPanel().getExecuteQueryButton().doClick();

        manualQueryPanelController.getManualQueryPanel().getQueryInputArea().setText("select * from experiment");

        manualQueryPanelController.getManualQueryPanel().getExecuteQueryButton().doClick();

        assertThat(manualQueryPanelController.getManualQueryPanel().getResultTable().getRowCount(), is(4));
    }
}
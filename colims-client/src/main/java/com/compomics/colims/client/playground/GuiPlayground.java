/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.client.playground;

import com.compomics.colims.client.view.gui.ColimsViewer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Kenneth
 */
public class GuiPlayground {

    public static void main(String[] args) {
        new GuiPlayground().runGui();
    }

    public void runGui() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("colims-client-context.xml");
        ColimsViewer viewer = (ColimsViewer) applicationContext.getBean("colimsViewer");
        viewer.init();
    }
}

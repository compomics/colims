package com.compomics.colims.client.controller;

import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.model.ErrorQueueTableModel;
import com.compomics.colims.client.model.StorageQueueTableModel;
import com.compomics.colims.client.storage.QueueMonitor;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.StorageMonitoringDialog;
import com.compomics.colims.distributed.model.StorageError;
import com.compomics.colims.distributed.model.StorageTask;
import com.google.common.eventbus.EventBus;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.jms.JMSException;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("storageMonitoringController")
public class StorageMonitoringController implements Controllable {

    private static final Logger LOGGER = Logger.getLogger(StorageMonitoringController.class);

    //model
    @Value("${distributed.queue.storage}")
    private String storageQueueName;
    @Value("${distributed.queue.error}")
    private String errorQueueName;
    private StorageQueueTableModel storageQueueTableModel;
    private ErrorQueueTableModel errorQueueTableModel;
    //view
    private StorageMonitoringDialog storageMonitoringDialog;
    //parent controller
    @Autowired
    private ColimsController colimsController;
    //services
    @Autowired
    private QueueMonitor queueMonitor;
    @Autowired
    private EventBus eventBus;

    public StorageMonitoringDialog getStorageMonitoringDialog() {
        return storageMonitoringDialog;
    }

    @Override
    public void init() {
        //register to event bus
        eventBus.register(this);

        //init view
        storageMonitoringDialog = new StorageMonitoringDialog(colimsController.getColimsFrame(), true);

        //init and set table models
        storageQueueTableModel = new StorageQueueTableModel();
        storageMonitoringDialog.getStorageQueueTable().setModel(storageQueueTableModel);
        errorQueueTableModel = new ErrorQueueTableModel();
        storageMonitoringDialog.getErrorQueueTable().setModel(errorQueueTableModel);
        
        //add action listeners
        storageMonitoringDialog.getRefreshButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateMonitoringTables();
            }
        });

        storageMonitoringDialog.getCloseButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                storageMonitoringDialog.dispose();
            }
        });
    }

    @Override
    public void showView() {
        updateMonitoringTables();

        GuiUtils.centerDialogOnComponent(colimsController.getColimsFrame(), storageMonitoringDialog);
        storageMonitoringDialog.setVisible(true);
    }

    /**
     * Update the monitoring tables; fetch the messages currently residing on
     * the queues.
     */
    private void updateMonitoringTables() {
        try {
            List<StorageTask> storageTaskMessages = queueMonitor.monitorQueue(storageQueueName);
            storageQueueTableModel.setMessages(storageTaskMessages);
            
            List<StorageError> storageErrorMessages = queueMonitor.monitorQueue(errorQueueName);
            errorQueueTableModel.setMessages(storageErrorMessages);
        } catch (JMSException ex) {
            LOGGER.error(ex.getMessage(), ex);
            eventBus.post(new MessageEvent("connection error", "The storage module could not be reached.", JOptionPane.ERROR_MESSAGE));
        }
    }

}

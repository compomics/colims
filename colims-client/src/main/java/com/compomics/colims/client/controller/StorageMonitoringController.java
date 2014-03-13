package com.compomics.colims.client.controller;

import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.model.ErrorQueueTableModel;
import com.compomics.colims.client.model.StorageQueueTableModel;
import com.compomics.colims.client.model.StoredQueueTableModel;
import com.compomics.colims.client.storage.QueueManager;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.UncategorizedJmsException;
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
    @Value("${distributed.queue.stored}")
    private String storedQueueName;
    @Value("${distributed.queue.error}")
    private String errorQueueName;
    private StorageQueueTableModel storageQueueTableModel;
    private StoredQueueTableModel storedQueueTableModel;
    private ErrorQueueTableModel errorQueueTableModel;
    //view
    private StorageMonitoringDialog storageMonitoringDialog;
    //parent controller
    @Autowired
    private ColimsController colimsController;
    //services
    @Autowired
    private QueueManager queueManager;
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
        storedQueueTableModel = new StoredQueueTableModel();
        storageMonitoringDialog.getStoredQueueTable().setModel(storedQueueTableModel);
        errorQueueTableModel = new ErrorQueueTableModel();
        storageMonitoringDialog.getErrorQueueTable().setModel(errorQueueTableModel);

        //add action listeners
        storageMonitoringDialog.getErrorQueueTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (!lse.getValueIsAdjusting()) {
                    int selectedRowIndex = storageMonitoringDialog.getErrorQueueTable().getSelectedRow();
                    if (selectedRowIndex != -1 && errorQueueTableModel.getRowCount() != 0) {
                        StorageError storageError = errorQueueTableModel.getMessages().get(selectedRowIndex);

                        storageMonitoringDialog.getErrorDetailTextArea().setText(storageError.getCause().getMessage());
                    } else {
                        storageMonitoringDialog.getErrorDetailTextArea().setText("");
                    }
                }
            }
        });

        storageMonitoringDialog.getResendStorageErrorButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRowIndex = storageMonitoringDialog.getErrorQueueTable().getSelectedRow();
                if (selectedRowIndex != -1 && errorQueueTableModel.getRowCount() != 0) {
                    try {
                        StorageError storageError = errorQueueTableModel.getMessages().get(selectedRowIndex);

                        queueManager.redirectStorageError(storageQueueName, storageError);

                        updateMonitoringTables();
                    } catch (JMSException jMSException) {
                        LOGGER.error(jMSException.getMessage(), jMSException);
                    }
                }
            }
        });

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
            List<StorageTask> storageTaskMessages = queueManager.monitorQueue(storageQueueName);
            storageQueueTableModel.setMessages(storageTaskMessages);

            List<StorageError> storageErrorMessages = queueManager.monitorQueue(errorQueueName);
            errorQueueTableModel.setMessages(storageErrorMessages);

            //clear selections
            storageMonitoringDialog.getStorageQueueTable().getSelectionModel().clearSelection();
            storageMonitoringDialog.getStoredQueueTable().getSelectionModel().clearSelection();
            storageMonitoringDialog.getErrorQueueTable().getSelectionModel().clearSelection();

            storageMonitoringDialog.getStorageQueueTable().updateUI();
            storageMonitoringDialog.getErrorQueueTable().updateUI();
        } catch (UncategorizedJmsException | JMSException ex) {
            LOGGER.error(ex.getMessage(), ex);
            eventBus.post(new MessageEvent("connection error", "The storage module could not be reached:"
                    + "\n" + ex.getMessage(), JOptionPane.ERROR_MESSAGE));
        }
    }

}

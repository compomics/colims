package com.compomics.colims.client.controller;

import com.compomics.colims.client.event.EntityChangeEvent;
import com.compomics.colims.client.event.InstrumentChangeEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.event.message.StorageQueuesConnectionErrorMessageEvent;
import com.compomics.colims.client.model.DbTaskErrorQueueTableModel;
import com.compomics.colims.client.model.DbTaskQueueTableModel;
import com.compomics.colims.client.model.CompletedDbTaskQueueTableModel;
import com.compomics.colims.client.storage.QueueManager;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.TaskMonitoringDialog;
import com.compomics.colims.distributed.model.DbTaskError;
import com.compomics.colims.distributed.model.PersistDbTask;
import com.compomics.colims.distributed.model.CompletedDbTask;
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
    private static final String ERROR_DETAIL_NOT_AVAILABLE = "not available";

    //model
    @Value("${distributed.queue.dbtask}")
    private String storageQueueName;
    @Value("${distributed.queue.completed}")
    private String storedQueueName;
    @Value("${distributed.queue.error}")
    private String errorQueueName;
    private DbTaskQueueTableModel storageQueueTableModel;
    private CompletedDbTaskQueueTableModel storedQueueTableModel;
    private DbTaskErrorQueueTableModel errorQueueTableModel;
    //view
    private TaskMonitoringDialog storageMonitoringDialog;
    //parent controller
    @Autowired
    private ColimsController colimsController;
    //services
    @Autowired
    private QueueManager queueManager;
    @Autowired
    private EventBus eventBus;

    public TaskMonitoringDialog getStorageMonitoringDialog() {
        return storageMonitoringDialog;
    }

    @Override
    public void init() {
        //register to event bus
        eventBus.register(this);

        //init view
        storageMonitoringDialog = new TaskMonitoringDialog(colimsController.getColimsFrame(), true);

        //init and set table models
        storageQueueTableModel = new DbTaskQueueTableModel();
        storageMonitoringDialog.getTaskQueueTable().setModel(storageQueueTableModel);
        storedQueueTableModel = new CompletedDbTaskQueueTableModel();
        storageMonitoringDialog.getCompletedTaskQueueTable().setModel(storedQueueTableModel);
        errorQueueTableModel = new DbTaskErrorQueueTableModel();
        storageMonitoringDialog.getTaskErrorQueueTable().setModel(errorQueueTableModel);

        //add action listeners        
        storageMonitoringDialog.getDeleteDbTaskButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRowIndex = storageMonitoringDialog.getTaskQueueTable().getSelectedRow();
                if (selectedRowIndex != -1 && storageQueueTableModel.getRowCount() != 0) {
                    try {
                        PersistDbTask storageTask = storageQueueTableModel.getMessages().get(selectedRowIndex);

                        queueManager.deleteMessage(storageQueueName, storageTask.getMessageId());

                        updateMonitoringTables();
                    } catch (JMSException ex) {
                        LOGGER.error(ex.getMessage(), ex);
                        postConnectionErrorMessage(ex.getMessage());
                    } catch (Exception ex) {
                        LOGGER.error(ex.getMessage(), ex);
                        postConnectionErrorMessage(ex.getMessage());
                    }
                }
            }
        });

        storageMonitoringDialog.getTaskErrorQueueTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (!lse.getValueIsAdjusting()) {
                    int selectedRowIndex = storageMonitoringDialog.getTaskErrorQueueTable().getSelectedRow();
                    if (selectedRowIndex != -1 && errorQueueTableModel.getRowCount() != 0) {
                        DbTaskError storageError = errorQueueTableModel.getMessages().get(selectedRowIndex);

                        if (storageError.getCause().getMessage() != null) {
                            storageMonitoringDialog.getErrorDetailTextArea().setText(storageError.getCause().getMessage());
                        } else {
                            storageMonitoringDialog.getErrorDetailTextArea().setText(ERROR_DETAIL_NOT_AVAILABLE);
                        }
                    } else {
                        storageMonitoringDialog.getErrorDetailTextArea().setText("");
                    }
                }
            }
        });

        storageMonitoringDialog.getResendTaskErrorButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRowIndex = storageMonitoringDialog.getTaskErrorQueueTable().getSelectedRow();
                if (selectedRowIndex != -1 && errorQueueTableModel.getRowCount() != 0) {
                    try {
                        DbTaskError storageError = errorQueueTableModel.getMessages().get(selectedRowIndex);

                        queueManager.redirectStorageError(storageQueueName, storageError);

                        updateMonitoringTables();
                    } catch (JMSException ex) {
                        LOGGER.error(ex.getMessage(), ex);
                        postConnectionErrorMessage(ex.getMessage());
                    } catch (Exception ex) {
                        LOGGER.error(ex.getMessage(), ex);
                        postConnectionErrorMessage(ex.getMessage());
                    }
                }
            }
        });

        storageMonitoringDialog.getDeleteTaskErrorButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRowIndex = storageMonitoringDialog.getTaskErrorQueueTable().getSelectedRow();
                if (selectedRowIndex != -1 && errorQueueTableModel.getRowCount() != 0) {
                    try {
                        DbTaskError storageError = errorQueueTableModel.getMessages().get(selectedRowIndex);

                        queueManager.deleteMessage(errorQueueName, storageError.getMessageId());

                        errorQueueTableModel.remove(selectedRowIndex);
                    } catch (JMSException ex) {
                        LOGGER.error(ex.getMessage(), ex);
                        postConnectionErrorMessage(ex.getMessage());
                    } catch (Exception ex) {
                        LOGGER.error(ex.getMessage(), ex);
                        postConnectionErrorMessage(ex.getMessage());
                    }
                }
            }
        });

        storageMonitoringDialog.getPurgeTaskErrorsButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    queueManager.purgeMessages(errorQueueName);

                    errorQueueTableModel.removeAll();
                } catch (Exception ex) {
                    LOGGER.error(ex.getMessage(), ex);
                    postConnectionErrorMessage(ex.getMessage());
                }
            }
        });

        storageMonitoringDialog.getDeleteCompletedTaskButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRowIndex = storageMonitoringDialog.getCompletedTaskQueueTable().getSelectedRow();
                if (selectedRowIndex != -1 && storedQueueTableModel.getRowCount() != 0) {
                    try {
                        CompletedDbTask storedTask = storedQueueTableModel.getMessages().get(selectedRowIndex);

                        queueManager.deleteMessage(storedQueueName, storedTask.getMessageId());

                        storedQueueTableModel.remove(selectedRowIndex);
                    } catch (JMSException ex) {
                        LOGGER.error(ex.getMessage(), ex);
                        postConnectionErrorMessage(ex.getMessage());
                    } catch (Exception ex) {
                        LOGGER.error(ex.getMessage(), ex);
                        postConnectionErrorMessage(ex.getMessage());
                    }
                }
            }
        });

        storageMonitoringDialog.getPurgeCompletedTasksButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    queueManager.purgeMessages(storedQueueName);

                    storedQueueTableModel.removeAll();
                } catch (Exception ex) {
                    LOGGER.error(ex.getMessage(), ex);
                    postConnectionErrorMessage(ex.getMessage());
                }
            }
        });

        storageMonitoringDialog.getRefreshButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateMonitoringTables();
            }
        });

        storageMonitoringDialog.getCancelButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                storageMonitoringDialog.dispose();
            }
        });
    }

    @Override
    public void showView() {
        
        //check connection to distributed queues
        if (queueManager.testConnection()) {
            updateMonitoringTables();

            GuiUtils.centerDialogOnComponent(colimsController.getColimsFrame(), storageMonitoringDialog);
            storageMonitoringDialog.setVisible(true);
        } else {
            eventBus.post(new StorageQueuesConnectionErrorMessageEvent(queueManager.getBrokerName(), queueManager.getBrokerUrl(), queueManager.getBrokerJmxUrl()));
        }
    }

    /**
     * Update the monitoring tables; fetch the messages currently residing on
     * the queues.
     */
    private void updateMonitoringTables() {
        try {
            List<PersistDbTask> storageTaskMessages = queueManager.monitorQueue(storageQueueName);
            storageQueueTableModel.setMessages(storageTaskMessages);

            List<CompletedDbTask> storedTaskMessages = queueManager.monitorQueue(storedQueueName);
            storedQueueTableModel.setMessages(storedTaskMessages);

            List<DbTaskError> storageErrorMessages = queueManager.monitorQueue(errorQueueName);
            errorQueueTableModel.setMessages(storageErrorMessages);

            //clear selections
            storageMonitoringDialog.getTaskQueueTable().getSelectionModel().clearSelection();
            storageMonitoringDialog.getCompletedTaskQueueTable().getSelectionModel().clearSelection();
            storageMonitoringDialog.getTaskErrorQueueTable().getSelectionModel().clearSelection();

//            storageMonitoringDialog.getStorageQueueTable().updateUI();
//            storageMonitoringDialog.getStoredQueueTable().updateUI();
//            storageMonitoringDialog.getErrorQueueTable().updateUI();
        } catch (UncategorizedJmsException | JMSException ex) {
            LOGGER.error(ex.getMessage(), ex);
            postConnectionErrorMessage(ex.getMessage());
        }
    }

    /**
     * Post a connection error message on the event bus.
     *
     * @param message the error message
     */
    private void postConnectionErrorMessage(String message) {
        eventBus.post(new MessageEvent("connection error", "The storage module cannot be reached:"
                + "\n" + message, JOptionPane.ERROR_MESSAGE));
    }

}

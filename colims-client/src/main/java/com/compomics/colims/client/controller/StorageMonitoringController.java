package com.compomics.colims.client.controller;

import com.compomics.colims.client.event.EntityChangeEvent;
import com.compomics.colims.client.event.InstrumentChangeEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.event.message.StorageQueuesConnectionErrorMessageEvent;
import com.compomics.colims.client.model.DbTaskErrorQueueTableModel;
import com.compomics.colims.client.model.DbTaskQueueTableModel;
import com.compomics.colims.client.model.CompletedDbTaskQueueTableModel;
import com.compomics.colims.client.model.tableformat.SampleManagementTableFormat;
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
    private DbTaskQueueTableModel dbTaskQueueTableModel;
    private CompletedDbTaskQueueTableModel completedDbTaskQueueTableModel;
    private DbTaskErrorQueueTableModel dbTaskErrorQueueTableModel;
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

    /**
     *
     * @return
     */
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
        dbTaskQueueTableModel = new DbTaskQueueTableModel();
        storageMonitoringDialog.getTaskQueueTable().setModel(dbTaskQueueTableModel);
        completedDbTaskQueueTableModel = new CompletedDbTaskQueueTableModel();
        storageMonitoringDialog.getCompletedTaskQueueTable().setModel(completedDbTaskQueueTableModel);
        dbTaskErrorQueueTableModel = new DbTaskErrorQueueTableModel();
        storageMonitoringDialog.getTaskErrorQueueTable().setModel(dbTaskErrorQueueTableModel);
        
        //set column widths
        storageMonitoringDialog.getTaskQueueTable().getColumnModel().getColumn(DbTaskQueueTableModel.QUEUE_INDEX).setPreferredWidth(40);
        storageMonitoringDialog.getTaskQueueTable().getColumnModel().getColumn(DbTaskQueueTableModel.QUEUE_INDEX).setMaxWidth(40);
        storageMonitoringDialog.getTaskQueueTable().getColumnModel().getColumn(DbTaskQueueTableModel.QUEUE_INDEX).setMinWidth(40);
        storageMonitoringDialog.getTaskQueueTable().getColumnModel().getColumn(DbTaskQueueTableModel.TYPE_INDEX).setPreferredWidth(100);
        storageMonitoringDialog.getTaskQueueTable().getColumnModel().getColumn(DbTaskQueueTableModel.SUBMITTED_INDEX).setPreferredWidth(100);
        storageMonitoringDialog.getTaskQueueTable().getColumnModel().getColumn(DbTaskQueueTableModel.DESCRIPTION_INDEX).setPreferredWidth(100);
        storageMonitoringDialog.getTaskQueueTable().getColumnModel().getColumn(DbTaskQueueTableModel.USER_INDEX).setPreferredWidth(100);
        
        storageMonitoringDialog.getCompletedTaskQueueTable().getColumnModel().getColumn(CompletedDbTaskQueueTableModel.QUEUE_INDEX).setPreferredWidth(40);
        storageMonitoringDialog.getCompletedTaskQueueTable().getColumnModel().getColumn(CompletedDbTaskQueueTableModel.QUEUE_INDEX).setMaxWidth(40);
        storageMonitoringDialog.getCompletedTaskQueueTable().getColumnModel().getColumn(CompletedDbTaskQueueTableModel.QUEUE_INDEX).setMinWidth(40);
        storageMonitoringDialog.getCompletedTaskQueueTable().getColumnModel().getColumn(CompletedDbTaskQueueTableModel.TYPE_INDEX).setPreferredWidth(100);
        storageMonitoringDialog.getCompletedTaskQueueTable().getColumnModel().getColumn(CompletedDbTaskQueueTableModel.SUBMITTED_INDEX).setPreferredWidth(100);
        storageMonitoringDialog.getCompletedTaskQueueTable().getColumnModel().getColumn(CompletedDbTaskQueueTableModel.DESCRIPTION_INDEX).setPreferredWidth(100);
        storageMonitoringDialog.getCompletedTaskQueueTable().getColumnModel().getColumn(CompletedDbTaskQueueTableModel.USER_INDEX).setPreferredWidth(100);
        storageMonitoringDialog.getCompletedTaskQueueTable().getColumnModel().getColumn(CompletedDbTaskQueueTableModel.START_INDEX).setPreferredWidth(100);
        storageMonitoringDialog.getCompletedTaskQueueTable().getColumnModel().getColumn(CompletedDbTaskQueueTableModel.DURATION_INDEX).setPreferredWidth(100);
        
        storageMonitoringDialog.getTaskErrorQueueTable().getColumnModel().getColumn(DbTaskErrorQueueTableModel.QUEUE_INDEX).setPreferredWidth(40);
        storageMonitoringDialog.getTaskErrorQueueTable().getColumnModel().getColumn(DbTaskErrorQueueTableModel.QUEUE_INDEX).setMaxWidth(40);
        storageMonitoringDialog.getTaskErrorQueueTable().getColumnModel().getColumn(DbTaskErrorQueueTableModel.QUEUE_INDEX).setMinWidth(40);
        storageMonitoringDialog.getTaskErrorQueueTable().getColumnModel().getColumn(DbTaskErrorQueueTableModel.TYPE_INDEX).setPreferredWidth(100);
        storageMonitoringDialog.getTaskErrorQueueTable().getColumnModel().getColumn(DbTaskErrorQueueTableModel.SUBMITTED_INDEX).setPreferredWidth(100);
        storageMonitoringDialog.getTaskErrorQueueTable().getColumnModel().getColumn(DbTaskErrorQueueTableModel.DESCRIPTION_INDEX).setPreferredWidth(100);
        storageMonitoringDialog.getTaskErrorQueueTable().getColumnModel().getColumn(DbTaskErrorQueueTableModel.USER_INDEX).setPreferredWidth(100);
        storageMonitoringDialog.getTaskErrorQueueTable().getColumnModel().getColumn(DbTaskErrorQueueTableModel.ERROR_INDEX).setPreferredWidth(100);
        
        //add action listeners        
        storageMonitoringDialog.getDeleteDbTaskButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRowIndex = storageMonitoringDialog.getTaskQueueTable().getSelectedRow();
                if (selectedRowIndex != -1 && dbTaskQueueTableModel.getRowCount() != 0) {
                    try {
                        PersistDbTask storageTask = dbTaskQueueTableModel.getMessages().get(selectedRowIndex);

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
                    if (selectedRowIndex != -1 && dbTaskErrorQueueTableModel.getRowCount() != 0) {
                        DbTaskError storageError = dbTaskErrorQueueTableModel.getMessages().get(selectedRowIndex);

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
                if (selectedRowIndex != -1 && dbTaskErrorQueueTableModel.getRowCount() != 0) {
                    try {
                        DbTaskError storageError = dbTaskErrorQueueTableModel.getMessages().get(selectedRowIndex);

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
                if (selectedRowIndex != -1 && dbTaskErrorQueueTableModel.getRowCount() != 0) {
                    try {
                        DbTaskError storageError = dbTaskErrorQueueTableModel.getMessages().get(selectedRowIndex);

                        queueManager.deleteMessage(errorQueueName, storageError.getMessageId());

                        dbTaskErrorQueueTableModel.remove(selectedRowIndex);
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

                    dbTaskErrorQueueTableModel.removeAll();
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
                if (selectedRowIndex != -1 && completedDbTaskQueueTableModel.getRowCount() != 0) {
                    try {
                        CompletedDbTask storedTask = completedDbTaskQueueTableModel.getMessages().get(selectedRowIndex);

                        queueManager.deleteMessage(storedQueueName, storedTask.getMessageId());

                        completedDbTaskQueueTableModel.remove(selectedRowIndex);
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

                    completedDbTaskQueueTableModel.removeAll();
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
            dbTaskQueueTableModel.setMessages(storageTaskMessages);

            List<CompletedDbTask> storedTaskMessages = queueManager.monitorQueue(storedQueueName);
            completedDbTaskQueueTableModel.setMessages(storedTaskMessages);

            List<DbTaskError> storageErrorMessages = queueManager.monitorQueue(errorQueueName);
            dbTaskErrorQueueTableModel.setMessages(storageErrorMessages);

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

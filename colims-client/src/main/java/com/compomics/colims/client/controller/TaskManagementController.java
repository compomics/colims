package com.compomics.colims.client.controller;

import com.compomics.colims.client.distributed.QueueManager;
import com.compomics.colims.client.distributed.consumer.NotificationConsumer;
import com.compomics.colims.client.event.NotificationEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.model.CompletedDbTaskQueueTableModel;
import com.compomics.colims.client.model.DbTaskErrorQueueTableModel;
import com.compomics.colims.client.model.DbTaskQueueTableModel;
import com.compomics.colims.client.view.TaskManagementPanel;
import com.compomics.colims.distributed.model.CompletedDbTask;
import com.compomics.colims.distributed.model.DbTask;
import com.compomics.colims.distributed.model.DbTaskError;
import com.compomics.colims.distributed.model.PersistDbTask;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
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
@Component("taskManagementController")
public class TaskManagementController implements Controllable {

    private static final Logger LOGGER = Logger.getLogger(TaskManagementController.class);
    private static final String ERROR_DETAIL_NOT_AVAILABLE = "not available";
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm");

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
    private TaskManagementPanel taskManagementPanel;
    //parent controller
    //services
    @Autowired
    private QueueManager queueManager;
    @Autowired
    private EventBus eventBus;

    /**
     *
     * @return
     */
    public TaskManagementPanel getTaskManagementPanel() {
        return taskManagementPanel;
    }

    @Override
    public void init() {
        //register to event bus
        eventBus.register(this);

        //init view
        taskManagementPanel = new TaskManagementPanel();

        //init and set table models
        dbTaskQueueTableModel = new DbTaskQueueTableModel();
        taskManagementPanel.getTaskQueueTable().setModel(dbTaskQueueTableModel);
        completedDbTaskQueueTableModel = new CompletedDbTaskQueueTableModel();
        taskManagementPanel.getCompletedTaskQueueTable().setModel(completedDbTaskQueueTableModel);
        dbTaskErrorQueueTableModel = new DbTaskErrorQueueTableModel();
        taskManagementPanel.getTaskErrorQueueTable().setModel(dbTaskErrorQueueTableModel);

        //set column widths
        taskManagementPanel.getTaskQueueTable().getColumnModel().getColumn(DbTaskQueueTableModel.QUEUE_INDEX).setPreferredWidth(50);
        taskManagementPanel.getTaskQueueTable().getColumnModel().getColumn(DbTaskQueueTableModel.QUEUE_INDEX).setMaxWidth(50);
        taskManagementPanel.getTaskQueueTable().getColumnModel().getColumn(DbTaskQueueTableModel.QUEUE_INDEX).setMinWidth(50);
        taskManagementPanel.getTaskQueueTable().getColumnModel().getColumn(DbTaskQueueTableModel.TYPE_INDEX).setPreferredWidth(100);
        taskManagementPanel.getTaskQueueTable().getColumnModel().getColumn(DbTaskQueueTableModel.SUBMITTED_INDEX).setPreferredWidth(100);
        taskManagementPanel.getTaskQueueTable().getColumnModel().getColumn(DbTaskQueueTableModel.DESCRIPTION_INDEX).setPreferredWidth(100);
        taskManagementPanel.getTaskQueueTable().getColumnModel().getColumn(DbTaskQueueTableModel.USER_INDEX).setPreferredWidth(100);

        taskManagementPanel.getCompletedTaskQueueTable().getColumnModel().getColumn(CompletedDbTaskQueueTableModel.QUEUE_INDEX).setPreferredWidth(50);
        taskManagementPanel.getCompletedTaskQueueTable().getColumnModel().getColumn(CompletedDbTaskQueueTableModel.QUEUE_INDEX).setMaxWidth(50);
        taskManagementPanel.getCompletedTaskQueueTable().getColumnModel().getColumn(CompletedDbTaskQueueTableModel.QUEUE_INDEX).setMinWidth(50);
        taskManagementPanel.getCompletedTaskQueueTable().getColumnModel().getColumn(CompletedDbTaskQueueTableModel.TYPE_INDEX).setPreferredWidth(100);
        taskManagementPanel.getCompletedTaskQueueTable().getColumnModel().getColumn(CompletedDbTaskQueueTableModel.SUBMITTED_INDEX).setPreferredWidth(100);
        taskManagementPanel.getCompletedTaskQueueTable().getColumnModel().getColumn(CompletedDbTaskQueueTableModel.DESCRIPTION_INDEX).setPreferredWidth(100);
        taskManagementPanel.getCompletedTaskQueueTable().getColumnModel().getColumn(CompletedDbTaskQueueTableModel.USER_INDEX).setPreferredWidth(100);
        taskManagementPanel.getCompletedTaskQueueTable().getColumnModel().getColumn(CompletedDbTaskQueueTableModel.START_INDEX).setPreferredWidth(100);
        taskManagementPanel.getCompletedTaskQueueTable().getColumnModel().getColumn(CompletedDbTaskQueueTableModel.DURATION_INDEX).setPreferredWidth(100);

        taskManagementPanel.getTaskErrorQueueTable().getColumnModel().getColumn(DbTaskErrorQueueTableModel.QUEUE_INDEX).setPreferredWidth(50);
        taskManagementPanel.getTaskErrorQueueTable().getColumnModel().getColumn(DbTaskErrorQueueTableModel.QUEUE_INDEX).setMaxWidth(50);
        taskManagementPanel.getTaskErrorQueueTable().getColumnModel().getColumn(DbTaskErrorQueueTableModel.QUEUE_INDEX).setMinWidth(50);
        taskManagementPanel.getTaskErrorQueueTable().getColumnModel().getColumn(DbTaskErrorQueueTableModel.TYPE_INDEX).setPreferredWidth(100);
        taskManagementPanel.getTaskErrorQueueTable().getColumnModel().getColumn(DbTaskErrorQueueTableModel.SUBMITTED_INDEX).setPreferredWidth(100);
        taskManagementPanel.getTaskErrorQueueTable().getColumnModel().getColumn(DbTaskErrorQueueTableModel.DESCRIPTION_INDEX).setPreferredWidth(100);
        taskManagementPanel.getTaskErrorQueueTable().getColumnModel().getColumn(DbTaskErrorQueueTableModel.USER_INDEX).setPreferredWidth(100);
        taskManagementPanel.getTaskErrorQueueTable().getColumnModel().getColumn(DbTaskErrorQueueTableModel.ERROR_INDEX).setPreferredWidth(100);

        //add action listeners  
        taskManagementPanel.getClearNotificationsButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                taskManagementPanel.getNotificationTextArea().setText("");
            }
        });

        taskManagementPanel.getDeleteDbTaskButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRowIndex = taskManagementPanel.getTaskQueueTable().getSelectedRow();
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

        taskManagementPanel.getTaskErrorQueueTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (!lse.getValueIsAdjusting()) {
                    int selectedRowIndex = taskManagementPanel.getTaskErrorQueueTable().getSelectedRow();
                    if (selectedRowIndex != -1 && dbTaskErrorQueueTableModel.getRowCount() != 0) {
                        DbTaskError storageError = dbTaskErrorQueueTableModel.getMessages().get(selectedRowIndex);

                        if (storageError.getCause().getMessage() != null) {
                            taskManagementPanel.getErrorDetailTextArea().setText(storageError.getCause().getMessage());
                        } else {
                            taskManagementPanel.getErrorDetailTextArea().setText(ERROR_DETAIL_NOT_AVAILABLE);
                        }
                    } else {
                        taskManagementPanel.getErrorDetailTextArea().setText("");
                    }
                }
            }
        });

        taskManagementPanel.getResendTaskErrorButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRowIndex = taskManagementPanel.getTaskErrorQueueTable().getSelectedRow();
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

        taskManagementPanel.getDeleteTaskErrorButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRowIndex = taskManagementPanel.getTaskErrorQueueTable().getSelectedRow();
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

        taskManagementPanel.getPurgeTaskErrorsButton().addActionListener(new ActionListener() {
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

        taskManagementPanel.getDeleteCompletedTaskButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRowIndex = taskManagementPanel.getCompletedTaskQueueTable().getSelectedRow();
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

        taskManagementPanel.getPurgeCompletedTasksButton().addActionListener(new ActionListener() {
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

//        taskManagementPanel.getRefreshButton().addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                updateMonitoringTables();
//            }
//        });
    }

    @Override
    public void showView() {
        //do nothing
    }

    /**
     * Update the monitoring tables; fetch the messages currently residing on
     * the queues.
     */
    public void updateMonitoringTables() {
        try {
            List<PersistDbTask> storageTaskMessages = queueManager.monitorQueue(storageQueueName);
            dbTaskQueueTableModel.setMessages(storageTaskMessages);

            List<CompletedDbTask> storedTaskMessages = queueManager.monitorQueue(storedQueueName);
            completedDbTaskQueueTableModel.setMessages(storedTaskMessages);

            List<DbTaskError> storageErrorMessages = queueManager.monitorQueue(errorQueueName);
            dbTaskErrorQueueTableModel.setMessages(storageErrorMessages);

            //clear selections
            taskManagementPanel.getTaskQueueTable().getSelectionModel().clearSelection();
            taskManagementPanel.getCompletedTaskQueueTable().getSelectionModel().clearSelection();
            taskManagementPanel.getTaskErrorQueueTable().getSelectionModel().clearSelection();

//            storageMonitoringDialog.getStorageQueueTable().updateUI();
//            storageMonitoringDialog.getStoredQueueTable().updateUI();
//            storageMonitoringDialog.getErrorQueueTable().updateUI();
        } catch (UncategorizedJmsException | JMSException ex) {
            LOGGER.error(ex.getMessage(), ex);
            postConnectionErrorMessage(ex.getMessage());
        }
    }

    /**
     * Listen to a NotificationEvent.
     *
     * @param notificationEvent the notification event
     */
    @Subscribe
    public void onNotificationEvent(NotificationEvent notificationEvent) { 
        DbTask dbTask = notificationEvent.getNotification().getDbTask();                      
        taskManagementPanel.getNotificationTextArea().append(activityMessage);
        taskManagementPanel.getNotificationTextArea().updateUI();
    }

    /**
     * Post a connection error message on the event bus.
     *
     * @param message the error message
     */
    private void postConnectionErrorMessage(String message) {
        eventBus.post(new MessageEvent("Connection error", "The storage module cannot be reached:"
                + "\n" + message, JOptionPane.ERROR_MESSAGE));
    }
    
    /**
     * Construct the activity message.
     * 
     * @param dbTask the DbTaks object
     * @return 
     */
    private String constructActivityMessage(DbTask dbTask){
        StringBuilder message = new StringBuilder("\t" + DATE_TIME_FORMAT.format(new Date()) + ": ");
        if(dbTask instanceof PersistDbTask){
            message.append("starting storage of task " + dbTask.getMessageId());
        }  
        else if(dbTask instanceof CompletedDbTask){
            message.append("finished storage of task " + dbTask.getMessageId());
        }
        
        return message.toString();
    }

}

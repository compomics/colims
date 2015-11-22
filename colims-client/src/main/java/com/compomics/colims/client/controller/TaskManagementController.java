package com.compomics.colims.client.controller;

import com.compomics.colims.client.distributed.QueueManager;
import com.compomics.colims.client.event.NotificationEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.model.table.model.CompletedDbTaskQueueTableModel;
import com.compomics.colims.client.model.table.model.DbTaskErrorQueueTableModel;
import com.compomics.colims.client.model.table.model.DbTaskQueueTableModel;
import com.compomics.colims.client.view.MainFrame;
import com.compomics.colims.client.view.TaskManagementPanel;
import com.compomics.colims.core.distributed.model.*;
import com.compomics.colims.core.distributed.model.enums.NotificationType;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.UncategorizedJmsException;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * The task management view controller.
 *
 * @author Niels Hulstaert
 */
@Component("taskManagementController")
public class TaskManagementController implements Controllable {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(TaskManagementController.class);

    private static final String ERROR_DETAIL_NOT_AVAILABLE = "not available";
    private static final String DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm";
    private static final String STARTED_MESSAGE = "started processing task ";
    private static final String FINISHED_MESSAGE = "finished processing task ";

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
    @Autowired
    private MainController mainController;
    //services
    @Autowired
    private QueueManager queueManager;
    @Autowired
    private EventBus eventBus;

    /**
     * Get the view of this controller.
     *
     * @return the TaskManagementPanel
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
        taskManagementPanel.getClearNotificationsButton().addActionListener(e -> taskManagementPanel.getNotificationTextArea().setText("..."));

        taskManagementPanel.getDeleteDbTaskButton().addActionListener(e -> {
            int selectedRowIndex = taskManagementPanel.getTaskQueueTable().getSelectedRow();
            if (selectedRowIndex != -1 && dbTaskQueueTableModel.getRowCount() != 0) {
                int result = JOptionPane.showConfirmDialog(taskManagementPanel, "This can't be undone."
                        + System.lineSeparator() + "Are you sure?", "Delete task", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    try {
                        DbTask dbTask = dbTaskQueueTableModel.getMessages().get(selectedRowIndex);

                        queueManager.deleteMessage(storageQueueName, dbTask.getMessageId());

                        updateMonitoringTables();
                    } catch (Exception ex) {
                        LOGGER.error(ex.getMessage(), ex);
                        postConnectionErrorMessage(ex.getMessage());
                    }
                }
            } else {
                eventBus.post(new MessageEvent("Task selection", "Please select a task to remove.", JOptionPane.INFORMATION_MESSAGE));
            }
        });

        taskManagementPanel.getTaskErrorQueueTable().getSelectionModel().addListSelectionListener(lse -> {
            if (!lse.getValueIsAdjusting()) {
                int selectedRowIndex = taskManagementPanel.getTaskErrorQueueTable().getSelectedRow();
                if (selectedRowIndex != -1 && dbTaskErrorQueueTableModel.getRowCount() != 0) {
                    DbTaskError storageError = dbTaskErrorQueueTableModel.getMessages().get(selectedRowIndex);

                    if (storageError.getErrorDescription() != null) {
                        taskManagementPanel.getErrorDetailTextArea().setText(storageError.getErrorDescription());
                    } else {
                        taskManagementPanel.getErrorDetailTextArea().setText(ERROR_DETAIL_NOT_AVAILABLE);
                    }
                } else {
                    taskManagementPanel.getErrorDetailTextArea().setText("");
                }
            }
        });

        taskManagementPanel.getResendTaskErrorButton().addActionListener(e -> {
            int selectedRowIndex = taskManagementPanel.getTaskErrorQueueTable().getSelectedRow();
            if (selectedRowIndex != -1 && dbTaskErrorQueueTableModel.getRowCount() != 0) {
                try {
                    DbTaskError storageError = dbTaskErrorQueueTableModel.getMessages().get(selectedRowIndex);

                    queueManager.redirectStorageError(storageQueueName, storageError);

                    updateMonitoringTables();
                } catch (Exception ex) {
                    LOGGER.error(ex.getMessage(), ex);
                    postConnectionErrorMessage(ex.getMessage());
                }
            } else {
                eventBus.post(new MessageEvent("Task error selection", "Please select a task to resend.", JOptionPane.INFORMATION_MESSAGE));
            }
        });

        taskManagementPanel.getDeleteTaskErrorButton().addActionListener(e -> {
            int selectedRowIndex = taskManagementPanel.getTaskErrorQueueTable().getSelectedRow();
            if (selectedRowIndex != -1 && dbTaskErrorQueueTableModel.getRowCount() != 0) {
                try {
                    DbTaskError storageError = dbTaskErrorQueueTableModel.getMessages().get(selectedRowIndex);

                    queueManager.deleteMessage(errorQueueName, storageError.getMessageId());

                    dbTaskErrorQueueTableModel.remove(selectedRowIndex);
                } catch (Exception ex) {
                    LOGGER.error(ex.getMessage(), ex);
                    postConnectionErrorMessage(ex.getMessage());
                }
            } else {
                eventBus.post(new MessageEvent("Task error selection", "Please select a task error to delete.", JOptionPane.INFORMATION_MESSAGE));
            }
        });

        taskManagementPanel.getPurgeTaskErrorsButton().addActionListener(e -> {
            try {
                queueManager.purgeMessages(errorQueueName);

                dbTaskErrorQueueTableModel.removeAll();
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
                postConnectionErrorMessage(ex.getMessage());
            }
        });

        taskManagementPanel.getDeleteCompletedTaskButton().addActionListener(e -> {
            int selectedRowIndex = taskManagementPanel.getCompletedTaskQueueTable().getSelectedRow();
            if (selectedRowIndex != -1 && completedDbTaskQueueTableModel.getRowCount() != 0) {
                try {
                    CompletedDbTask storedTask = completedDbTaskQueueTableModel.getMessages().get(selectedRowIndex);

                    queueManager.deleteMessage(storedQueueName, storedTask.getMessageId());

                    completedDbTaskQueueTableModel.remove(selectedRowIndex);
                } catch (Exception ex) {
                    LOGGER.error(ex.getMessage(), ex);
                    postConnectionErrorMessage(ex.getMessage());
                }
            } else {
                eventBus.post(new MessageEvent("Completed task selection", "Please select a completed task to remove.", JOptionPane.INFORMATION_MESSAGE));
            }
        });

        taskManagementPanel.getPurgeCompletedTasksButton().addActionListener(e -> {
            try {
                queueManager.purgeMessages(storedQueueName);

                completedDbTaskQueueTableModel.removeAll();
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
                postConnectionErrorMessage(ex.getMessage());
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
     * Update the monitoring tables; fetch the messages currently residing on the queues.
     */
    public void updateMonitoringTables() {
        try {
            List<DbTask> storageTaskMessages = queueManager.monitorQueue(storageQueueName, DbTask.class);
            dbTaskQueueTableModel.setMessages(storageTaskMessages);

            List<CompletedDbTask> storedTaskMessages = queueManager.monitorQueue(storedQueueName, CompletedDbTask.class);
            completedDbTaskQueueTableModel.setMessages(storedTaskMessages);

            List<DbTaskError> storageErrorMessages = queueManager.monitorQueue(errorQueueName, DbTaskError.class);
            dbTaskErrorQueueTableModel.setMessages(storageErrorMessages);

            //clear selections
            taskManagementPanel.getTaskQueueTable().getSelectionModel().clearSelection();
            taskManagementPanel.getCompletedTaskQueueTable().getSelectionModel().clearSelection();
            taskManagementPanel.getTaskErrorQueueTable().getSelectionModel().clearSelection();
        } catch (UncategorizedJmsException ex) {
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
    public void onNotificationEvent(final NotificationEvent notificationEvent) {
        Notification notification = notificationEvent.getNotification();
        String activityMessage = System.lineSeparator() + new SimpleDateFormat(DATE_TIME_FORMAT).format(new Date()) + ": ";
        if (notification.getType().equals(NotificationType.STARTED)) {
            activityMessage += STARTED_MESSAGE;
        } else if (notification.getType().equals(NotificationType.FINISHED)) {
            activityMessage += FINISHED_MESSAGE;

            //update tables if the task management tab is visible
            if (mainController.getSelectedTabTitle().equals(MainFrame.TASKS_TAB_TITLE)) {
                updateMonitoringTables();
            }
        }
        activityMessage += notification.getDbTaskMessageId();
        taskManagementPanel.getNotificationTextArea().append(activityMessage);
    }

    /**
     * Post a connection error message on the event bus.
     *
     * @param message the error message
     */
    private void postConnectionErrorMessage(final String message) {
        eventBus.post(new MessageEvent("Connection error", "The storage module cannot be reached:"
                + System.lineSeparator() + System.lineSeparator() + message, JOptionPane.ERROR_MESSAGE));
    }

}

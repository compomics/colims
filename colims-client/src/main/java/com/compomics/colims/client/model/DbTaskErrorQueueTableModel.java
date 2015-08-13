package com.compomics.colims.client.model;

import com.compomics.colims.core.config.ApplicationContextProvider;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.core.distributed.model.DbTask;
import com.compomics.colims.core.distributed.model.DbTaskError;
import com.compomics.colims.core.distributed.model.PersistDbTask;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Niels Hulstaert
 */
public class DbTaskErrorQueueTableModel extends AbstractTableModel {

    private static final String NOT_APPLICABLE = "N/A";
    private static final String DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm";
    private final String[] columnNames = {"index", "ID","type", "submitted on", "description", "user", "error"};
    private static final String PERSIST = "store ";
    private static final String DELETE = "delete ";
    public static final int QUEUE_INDEX = 0;
    public static final int ID = 1;
    public static final int TYPE_INDEX = 2;
    public static final int SUBMITTED_INDEX = 3;
    public static final int DESCRIPTION_INDEX = 4;
    public static final int USER_INDEX = 5;
    public static final int ERROR_INDEX = 6;
    private List<DbTaskError> messages;
    private UserService userService;

    public DbTaskErrorQueueTableModel() {
        messages = new ArrayList<>();
        userService = ApplicationContextProvider.getInstance().getBean("userService");
    }

    public DbTaskErrorQueueTableModel(List<DbTaskError> messages) {
        this.messages = messages;
    }

    public List<DbTaskError> getMessages() {
        return messages;
    }

    public void setMessages(List<DbTaskError> messages) {
        this.messages = messages;
        this.fireTableDataChanged();
    }

    /**
     * Remove the storage error with the given index.
     *
     * @param index the index of the storage error that needs to be removed.
     */
    public void remove(int index) {
        messages.remove(index);
        this.fireTableDataChanged();
    }

    /**
     * Remove all messages.
     */
    public void removeAll() {
        messages.clear();
        this.fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return messages.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        DbTaskError dbTaskError = messages.get(rowIndex);
        DbTask dbTask = dbTaskError.getDbTask();

        switch (columnIndex) {
            case QUEUE_INDEX:
                return rowIndex;
            case ID:
                return dbTask.getMessageId();
            case TYPE_INDEX:
                if (dbTask instanceof PersistDbTask) {
                    return PERSIST + dbTask.getDbEntityClass().getSimpleName();
                } else {
                    return DELETE + dbTask.getDbEntityClass().getSimpleName();
                }
            case SUBMITTED_INDEX:
                return new SimpleDateFormat(DATE_TIME_FORMAT).format(new Date(dbTask.getSubmissionTimestamp()));
            case DESCRIPTION_INDEX:
                if (dbTask instanceof PersistDbTask) {
                    return ((PersistDbTask) dbTask).getPersistMetadata().getDescription();
                } else {
                    return NOT_APPLICABLE;
                }
            case USER_INDEX:
                return userService.findUserNameById(dbTask.getUserId());
            case ERROR_INDEX:
                return dbTaskError.getCause().getClass().getSimpleName();
            default:
                throw new IllegalArgumentException("Invalid column index: " + columnIndex);
        }

    }
}

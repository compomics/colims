package com.compomics.colims.client.model;

import com.compomics.colims.distributed.model.StorageError;
import com.compomics.colims.distributed.model.StorageMetadata;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Niels Hulstaert
 */
public class ErrorQueueTableModel extends AbstractTableModel {

    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    private final String[] columnNames = {"index", "type", "submitted on", "description", "user", "sample", "error"};
    private static final int QUEUE_INDEX = 0;
    private static final int TYPE_INDEX = 1;
    private static final int SUBMITTED_INDEX = 2;
    private static final int DESCRIPTION_INDEX = 3;
    private static final int USER_INDEX = 4;
    private static final int SAMPLE_INDEX = 5;
    private static final int ERROR_INDEX = 6;
    private List<StorageError> messages;

    public ErrorQueueTableModel() {
        messages = new ArrayList<>();
    }

    public ErrorQueueTableModel(List<StorageError> messages) {
        this.messages = messages;
    }

    public List<StorageError> getMessages() {
        return messages;
    }

    public void setMessages(List<StorageError> messages) {
        this.messages = messages;
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
        StorageError storageError = messages.get(rowIndex);
        StorageMetadata storageMetadata = storageError.getStorageTask().getStorageMetadata();

        switch (columnIndex) {
            case QUEUE_INDEX:
                return rowIndex;
            case TYPE_INDEX:
                return storageMetadata.getStorageType().userFriendlyName();
            case SUBMITTED_INDEX:
                return DATE_TIME_FORMAT.format(new Date(storageMetadata.getSubmissionTimestamp()));
            case DESCRIPTION_INDEX:
                return storageMetadata.getDescription();
            case USER_INDEX:
                return storageMetadata.getUserName();
            case SAMPLE_INDEX:
                return storageMetadata.getSample().getName();
            case ERROR_INDEX:
                return storageError.getCause().getClass().getSimpleName();
            default:
                throw new IllegalArgumentException("Invalid column index: " + columnIndex);
        }
    }
}

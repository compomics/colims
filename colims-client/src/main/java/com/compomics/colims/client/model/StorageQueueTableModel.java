package com.compomics.colims.client.model;

import com.compomics.colims.distributed.model.StorageMetadata;
import com.compomics.colims.distributed.model.StorageTask;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Niels Hulstaert
 */
public class StorageQueueTableModel extends AbstractTableModel {

    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    private final String[] columnNames = {"index", "type", "submitted on", "description", "user", "instrument", "sample"};
    private static final int QUEUE_INDEX = 0;
    private static final int TYPE_INDEX = 1;
    private static final int SUBMITTED_INDEX = 2;
    private static final int DESCRIPTION_INDEX = 3;
    private static final int USER_INDEX = 4;
    private static final int INSTRUMENT_INDEX = 5;
    private static final int SAMPLE_INDEX = 6;
    private List<StorageTask> messages;

    public StorageQueueTableModel() {
        messages = new ArrayList<>();
    }

    public StorageQueueTableModel(List<StorageTask> messages) {
        this.messages = messages;
    }

    public List<StorageTask> getMessages() {
        return messages;
    }

    public void setMessages(List<StorageTask> messages) {
        this.messages = messages;
        this.fireTableDataChanged();
    }
    
    /**
     * Remove the storage task with the given index.
     *
     * @param index the index of the storage task that needs to be removed.
     */
    public void remove(int index) {
        messages.remove(index);
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
        StorageMetadata storageMetadata = messages.get(rowIndex).getStorageMetadata();

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
            case INSTRUMENT_INDEX:
                return storageMetadata.getInstrument().toString();
            case SAMPLE_INDEX:
                return storageMetadata.getSample().getName();
            default:
                throw new IllegalArgumentException("Invalid column index: " + columnIndex);
        }
    }
}

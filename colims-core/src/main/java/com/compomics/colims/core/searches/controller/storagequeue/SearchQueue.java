/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.searches.controller.storagequeue;

import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.core.exception.PeptideShakerIOException;
import com.compomics.colims.core.storage.enums.StorageState;
import com.compomics.colims.core.storage.processing.colimsimport.ColimsFileImporter;
import com.compomics.colims.core.storage.processing.colimsimport.factory.ColimsImporterFactory;
import com.compomics.colims.core.storage.processing.controller.storagequeue.storagetask.StorageTask;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.PriorityQueue;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kenneth Verheggen
 */
@Component("storageQueue")
public class SearchQueue extends PriorityQueue<StorageTask> implements Runnable {

    @Autowired
    ColimsImporterFactory colimsImporterFactory;

    private static Connection c;
    private static boolean connectionLocked = false;
    private static File adress;
    private static final Logger LOGGER = Logger.getLogger(SearchQueue.class);

    private SearchQueue() {
        this.adress = new File(System.getProperty("user.home") + "/.compomics/ColimsController/");
        setUpTables();
    }

    private SearchQueue(String dbAddress) {
        this.adress = new File(dbAddress);
        setUpTables();
    }

    @Override
    public boolean offer(StorageTask task) {
        return super.offer(task);
    }

    @Override
    public StorageTask poll() {
        StorageTask nextTask = super.poll();
        if (nextTask != null) {
            updateTask(nextTask, StorageState.PROGRESS);
        }
        return nextTask;
    }

    @Override
    public void run() {
        while (true) {
            //STORE TO COLIMS !!!!
            StorageTask taskToStore = poll();
            if (taskToStore != null) {
                updateTask(taskToStore, StorageState.PROGRESS);
                try {
                    LOGGER.debug("Storing " + taskToStore.getFileLocation() + " to colims");
                    File fileToStore = new File(taskToStore.getFileLocation());
                    ColimsFileImporter colimsFileImporter = colimsImporterFactory.getImporter(fileToStore);
                    if (colimsFileImporter.validate(fileToStore.getParentFile())) {
                        colimsFileImporter.storeFile(taskToStore.getUserName(), 
                                fileToStore.getParentFile(), 
                                taskToStore.getSampleID(),
                                taskToStore.getInstrumentId());
                        updateTask(taskToStore, StorageState.STORED);
                    } else {
                        updateTask(taskToStore, StorageState.ERROR);
                    }
                } catch (IOException | PeptideShakerIOException | MappingException ex) {
                    updateTask(taskToStore, StorageState.ERROR);
                } finally {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        LOGGER.error(ex);
                    }
                }
            } else {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    LOGGER.error(ex);
                }
            }
        }
    }

    private Connection getConnection() {
        while (SearchQueue.connectionLocked == true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                LOGGER.error(ex);
            }
        }
        if (c == null) {
            try {
                if (!adress.exists()) {
                    adress.mkdirs();
                }
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection("jdbc:sqlite:" + adress.getAbsolutePath() + "/colimsController.db");
            } catch (ClassNotFoundException | SQLException ex) {
                LOGGER.error(ex);
            }
        }
        SearchQueue.connectionLocked = true;
        return c;
    }

    private void releaseConnection() {
        SearchQueue.connectionLocked = false;
    }

    public void disconnect() throws SQLException {
        if (!c.isClosed()) {
            c.close();
        }
    }

    private void setUpTables() {
        LOGGER.debug("Setting up database tables");
        c = getConnection();
        try (Statement stmt = c.createStatement()) {
            String sql
                    = "CREATE TABLE IF NOT EXISTS STORAGETASKS "
                    + "(TASKID INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + " FILELOCATION TEXT NULL, "
                    + " USERNAME TEXT NULL, "
                    + " SAMPLEID INTEGER NULL,"
                    + " INSTRUMENTNAME TEXT NULL,"
                    + " STATE TEXT)";
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            LOGGER.error(e);
        } finally {
            releaseConnection();
            resetTasks();
            loadTasks();
        }
    }

    private void resetTasks() {
        LOGGER.debug("Resetting tasks that were still running on boot");
        c = getConnection();
        try (Statement stmt = c.createStatement()) {
            String sql = "UPDATE STORAGETASKS SET STATE ='WAITING' WHERE (STATE !='COMPLETED' AND STATE !='ERROR')";
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            LOGGER.error(e);
        } finally {
            releaseConnection();
        }
    }

    private void loadTasks() {
        LOGGER.debug("Loading tasks from file into queue");
        c = getConnection();
        try (Statement stmt = c.createStatement()) {
            String sql = "SELECT * FROM STORAGETASKS WHERE STATE ='WAITING'";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                long taskId = rs.getLong("TASKID");
                long sampleId = rs.getLong("SAMPLEID");
                String instrumentId = rs.getString("INSTRUMENTNAME");
                String fileLocation = rs.getString("FILELOCATION");
                String userName = rs.getString("USERNAME");
                this.offer(new StorageTask(taskId, fileLocation, userName, sampleId, instrumentId));
            }
        } catch (Exception e) {
            LOGGER.error(e);
        } finally {
            releaseConnection();
        }
    }

    /**
     *
     * @param task the storagetask that needs to be updated
     * @param storageState the state the storagetask will be updated to
     */
    public void updateTask(StorageTask task, StorageState storageState) {
        task.setState(storageState);
        c = getConnection();
        long taskID = task.getTaskID();
        String state = task.getState().toString();
        String sql = "UPDATE STORAGETASKS SET STATE =? WHERE TASKID=?";
        try (PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setString(1, state);
            stmt.setLong(2, taskID);
            stmt.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e);
        } finally {
            releaseConnection();
        }
    }

    /**
     *
     * @param taskID the taskID that needs to be searched
     * @param fromDatabase boolean to flag retrieval from database or trackermap
     * @return a storagetask object
     */
    public StorageTask getTask(long taskID) {
        StorageTask task = null;
        c = getConnection();
        String sql = "SELECT * FROM STORAGETASKS WHERE TASKID =?";
        try (PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setLong(1, taskID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                task = new StorageTask(taskID,
                        rs.getString("FILELOCATION"),
                        rs.getString("USERNAME"),
                        rs.getLong("SAMPLEID"),
                        rs.getString("INSTRUMENTNAME"));
                task.setState(StorageState.valueOf(rs.getString("STATE")));
            }
        } catch (Exception e) {
            LOGGER.error(e);
        } finally {
            releaseConnection();

        }
        return task;
    }

    /**
     *
     * @param fileLocation the path to the file that needs to be stored. This
     * filepath has to be visible for the controller!
     * @return a generated StorageTask Object that has already been stored in
     * both the queue and the underlying database
     */
    public StorageTask addNewTask(String fileLocation, String userName, long sampleID, String instrumentID) {
        long key = -1L;
        c = getConnection();
        try (PreparedStatement stmt = c.prepareStatement("INSERT INTO STORAGETASKS(STATE,FILELOCATION,USERNAME,SAMPLEID,INSTRUMENTNAME) VALUES(?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, "WAITING");
            stmt.setString(2, fileLocation);
            stmt.setString(3, userName);
            stmt.setLong(4, sampleID);
            stmt.setString(5, instrumentID);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs != null && rs.next()) {
                key = rs.getLong(1);
            }
            stmt.close();
        } catch (SQLException e) {
            LOGGER.error(e);
            e.printStackTrace();
        } finally {
            releaseConnection();
            StorageTask task = new StorageTask(key, fileLocation, userName, sampleID, instrumentID);
            offer(task);
            return task;
        }
    }

}

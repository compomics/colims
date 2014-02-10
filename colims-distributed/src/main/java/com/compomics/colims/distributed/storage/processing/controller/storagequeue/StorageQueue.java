/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.distributed.storage.processing.controller.storagequeue;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.distributed.spring.ApplicationContextProvider;
import com.compomics.colims.distributed.storage.enums.StorageState;
import com.compomics.colims.distributed.storage.enums.StorageType;
import com.compomics.colims.distributed.storage.processing.colimsimport.ColimsFileImporter;
import com.compomics.colims.distributed.storage.processing.colimsimport.factory.ColimsImporterFactory;
import com.compomics.colims.distributed.storage.processing.controller.storagequeue.storagetask.StorageTask;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.logging.Level;
import javax.naming.AuthenticationException;
import org.apache.log4j.Logger;

/**
 *
 * @author Kenneth Verheggen
 */
public class StorageQueue extends PriorityQueue<StorageTask> implements Runnable {

    private static Connection connection;
    private static boolean connectionLocked = false;
    private static File adress;
    private static final Logger LOGGER = Logger.getLogger(StorageQueue.class);
    private static StorageQueue storageQueue;

    public static StorageQueue getInstance() {
        if (storageQueue == null) {
            storageQueue = new StorageQueue();
        }
        return storageQueue;
    }

    public static StorageQueue getInstance(String dbAddress) {
        if (storageQueue == null) {
            storageQueue = new StorageQueue(dbAddress);
        }
        return storageQueue;
    }

    private StorageQueue() {
        this.adress = new File(System.getProperty("user.home") + "/.compomics/ColimsController/StorageController/");
        setUpTables();
    }

    private StorageQueue(String dbAddress) {
        this.adress = new File(dbAddress);
        setUpTables();
    }

    @Override
    public boolean offer(StorageTask task) {
        return addNewTask(task.getFileLocation(), task.getUserName(), task.getSampleID(), task.getInstrumentId(), task.getType().toString()) != null;
    }

    public long offerAndGetTaskID(StorageTask task) {
        return addNewTask(task.getFileLocation(), task.getUserName(), task.getSampleID(), task.getInstrumentId(), task.getType().toString()).getTaskID();
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
                    LOGGER.info("Storing " + taskToStore.getFileLocation() + " to colims");
                    File fileToStore = new File(taskToStore.getFileLocation());
                    ColimsImporterFactory colimsImporterFactory = (ColimsImporterFactory) ApplicationContextProvider.getInstance().getApplicationContext().getBean("colimsImporterFactory");
                    ColimsFileImporter colimsFileImporter = colimsImporterFactory.getImporter(taskToStore.getType());
                    if (colimsFileImporter.validate(fileToStore)) {
                        LOGGER.info("ImportingFile is validated");
                        colimsFileImporter.storeFile(taskToStore.getUserName(),
                                fileToStore.getParentFile(),
                                taskToStore.getSampleID(),
                                taskToStore.getInstrumentId()
                        );
                        updateTask(taskToStore, StorageState.STORED);
                    } else {
                        updateTask(taskToStore, StorageState.ERROR);
                    }
                } catch (IOException | AuthenticationException | MappingException ex) {
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
        while (StorageQueue.connectionLocked == true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                LOGGER.error(ex);
            }
        }
        if (connection == null) {
            try {
                if (!adress.exists()) {
                    adress.mkdirs();
                }
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection("jdbc:sqlite:" + adress.getAbsolutePath() + "/StorageController.db");
            } catch (ClassNotFoundException | SQLException ex) {
                LOGGER.error(ex);
            }
        }
        StorageQueue.connectionLocked = true;
        return connection;
    }

    private void releaseConnection() {
        StorageQueue.connectionLocked = false;
    }

    public void disconnect() throws SQLException {
        if (!connection.isClosed()) {
            connection.close();
        }
        connection = null;
    }

    private void setUpTables() {
        LOGGER.debug("Setting up database tables");
        connection = getConnection();
        try (Statement stmt = connection.createStatement()) {
            String sql
                    = "CREATE TABLE IF NOT EXISTS STORAGETASKS "
                    + "(TASKID INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + " FILELOCATION TEXT NULL, "
                    + " USERNAME TEXT NULL, "
                    + " SAMPLEID INTEGER NULL,"
                    + " INSTRUMENTNAME TEXT NULL,"
                    + " TYPE TEXT NULL,"
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
        connection = getConnection();
        try (Statement stmt = connection.createStatement()) {
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
        connection = getConnection();
        List<StorageTask> tasksToStore = new ArrayList<StorageTask>();
        try (Statement stmt = connection.createStatement()) {
            String sql = "SELECT * FROM STORAGETASKS WHERE STATE ='WAITING'";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                long taskId = rs.getLong("TASKID");
                long sampleId = rs.getLong("SAMPLEID");
                String instrumentId = rs.getString("INSTRUMENTNAME");
                String fileLocation = rs.getString("FILELOCATION");
                String userName = rs.getString("USERNAME");
                StorageType type = StorageType.valueOf(rs.getString("TYPE"));
                tasksToStore.add(new StorageTask(taskId, fileLocation, userName, sampleId, instrumentId, type));
            }
        } catch (Exception e) {
            LOGGER.error(e);
        } finally {
            releaseConnection();
            //put the tasks back
            for (StorageTask aTask : tasksToStore) {
                this.offer(aTask);
            }
        }
    }

    /**
     *
     * @param task the storagetask that needs to be updated
     * @param storageState the state the storagetask will be updated to
     */
    public void updateTask(StorageTask task, StorageState storageState) {
        task.setState(storageState);
        connection = getConnection();
        long taskID = task.getTaskID();
        String state = task.getState().toString();
        String sql = "UPDATE STORAGETASKS SET STATE =? WHERE TASKID=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
     * @param taskID the taskID that needs to be searconnectionhed
     * @param fromDatabase boolean to flag retrieval from database or
     * traconnectionkermap
     * @return a storagetask objeconnectiont
     * @throws java.sql.SQLException
     */
    public StorageTask getTask(long taskID) throws SQLException {
        StorageTask task = null;
        connection = getConnection();
        String sql = "SELECT * FROM STORAGETASKS WHERE TASKID =?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, taskID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                task = new StorageTask(taskID,
                        rs.getString("FILELOCATION"),
                        rs.getString("USERNAME"),
                        rs.getLong("SAMPLEID"),
                        rs.getString("INSTRUMENTNAME"),
                        StorageType.valueOf(rs.getString("TYPE").toUpperCase()));
                task.setState(StorageState.valueOf(rs.getString("STATE")));
            }
        } finally {
            releaseConnection();
        }
        return task;
    }

    public StorageTask getTask(String userName, String fileLocation) throws SQLException {
        StorageTask task = null;
        connection = getConnection();
        String sql = "SELECT * FROM STORAGETASKS WHERE FILELOCATION =? AND USER=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, fileLocation);
            stmt.setString(2, userName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                task = new StorageTask(
                        rs.getLong("TASKID"),
                        rs.getString("FILELOCATION"),
                        rs.getString("USERNAME"),
                        rs.getLong("SAMPLEID"),
                        rs.getString("INSTRUMENTNAME"),
                        StorageType.valueOf(rs.getString("TYPE").toUpperCase()));
                task.setState(StorageState.valueOf(rs.getString("STATE")));
            }
        } finally {
            releaseConnection();

        }
        return task;
    }

    /**
     *
     * @param fileLocation the path to the file that needs to be stored. This
     * filepath has to be visible for the connectionontroller!
     * @return a generated StorageTask Objeconnectiont that has already been
     * stored in both the queue and the underlying database
     */
    public StorageTask addNewTask(String fileLocation, String userName, long sampleID, String instrumentID, String type) {
        long key = -1L;
        connection = getConnection();
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO STORAGETASKS(STATE,FILELOCATION,USERNAME,SAMPLEID,INSTRUMENTNAME,TYPE) VALUES(?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, "WAITING");
            stmt.setString(2, fileLocation);
            stmt.setString(3, userName);
            stmt.setLong(4, sampleID);
            stmt.setString(5, instrumentID);
            stmt.setString(6, type);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs != null && rs.next()) {
                key = rs.getLong(1);
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.error(e);
        } finally {
            releaseConnection();
            StorageTask task = new StorageTask(key, fileLocation, userName, sampleID, instrumentID, StorageType.valueOf(type.toUpperCase()));
            super.offer(task);
            return task;
        }
    }

    @Override
    public void clear() {
        connection = getConnection();
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM STORAGETASKS WHERE 1=1");
            stmt.close();
        } catch (SQLException e) {
            LOGGER.error(e);
        } catch (Exception e) {
            LOGGER.error(e);
        } finally {
            releaseConnection();
        }
        super.clear();
    }

}

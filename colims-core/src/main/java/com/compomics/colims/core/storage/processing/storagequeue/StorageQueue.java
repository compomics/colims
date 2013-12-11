/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.storage.processing.storagequeue;

import com.compomics.colims.core.storage.processing.storagequeue.storagetask.StorageTask;
import com.compomics.colims.core.storage.enums.StorageState;
import com.compomics.colims.core.storage.processing.colimsimport.ColimsCpsImporter;
import com.compomics.colims.core.storage.processing.colimsimport.ColimsFileImporter;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.PriorityQueue;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kenneth Verheggen
 */
@Component("storageQueue")
public class StorageQueue extends PriorityQueue<StorageTask> implements Runnable {

    private static Connection c;
    private static StorageQueue dao;
    private static boolean connectionLocked = false;
    private static File adress;
    private static final Logger LOGGER = Logger.getLogger(StorageQueue.class);
    private static final HashMap<Long, StorageTask> trackerMap = new HashMap<Long, StorageTask>();

    @Autowired
    ColimsFileImporter colimsFileImporter;

    private StorageQueue() {
        this.adress = new File(System.getProperty("user.home") + "/.compomics/ColimsController/");
        setUpTables();
    }

    private StorageQueue(String dbAddress) {
        this.adress = new File(dbAddress);
        setUpTables();
    }

    @Override
    public boolean offer(StorageTask task) {
        trackerMap.put(task.getTaskID(), task);
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
                //TODO ACTUALLY STORE THIS!!!!
                try {
                    LOGGER.debug("Storing " + taskToStore.getFileLocation() + " to colims");
                    if (taskToStore.getFileLocation().toLowerCase().endsWith(".cps")) {
                        colimsFileImporter = new ColimsCpsImporter();
                    }
                    colimsFileImporter.storeFile(taskToStore.getUserName(), new File(taskToStore.getFileLocation()).getParentFile());
                    updateTask(taskToStore, StorageState.STORED);
                } catch (Throwable e) {
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

    /**
     *
     * @returns an instance of the Storagequeue that will connect to the
     * specified db-file
     */
    public static StorageQueue getInstance(String dbAddress) {
        if (dao == null) {
            dao = new StorageQueue(dbAddress);
            LOGGER.debug("Connected to " + adress.getAbsolutePath());
        }
        return dao;
    }

    /**
     *
     * @returns an instance of the Storagequeue that will connect to the default
     * db-file
     */
    public static StorageQueue getInstance() {
        if (dao == null) {
            dao = new StorageQueue();
            LOGGER.debug("Connected to " + adress.getAbsolutePath());
        }
        return dao;
    }

    private Connection getConnection() {
        while (StorageQueue.connectionLocked == true) {
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
        StorageQueue.connectionLocked = true;
        return c;
    }

    private void releaseConnection() {
        StorageQueue.connectionLocked = false;
    }

    private void setUpTables() {
        LOGGER.debug("Setting up database tables");
        c = getConnection();
        try (Statement stmt = c.createStatement()) {
            String sql
                    = "CREATE TABLE STORAGETASKS "
                    + "(TASKID INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + " FILELOCATION TEXT NULL, "
                    + " USERNAME TEXT NULL, "
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
            String sql = "UPDATE STORAGETASKS SET STATE ='NEW' WHERE (STATE !='COMPLETED' AND STATE !='ERROR')";
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
            String sql = "SELECT * FROM STORAGETASKS WHERE STATE ='NEW'";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                long taskId = rs.getLong("TASKID");
                String fileLocation = rs.getString("FILELOCATION");
                String userName = rs.getString("USERNAME");
                this.offer(new StorageTask(taskId, fileLocation, userName));
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
    public StorageTask getTask(long taskID, boolean fromDatabase) {
        StorageTask task = null;
        if (fromDatabase) {
            c = getConnection();
            String sql = "SELECT * FROM STORAGETASKS WHERE TASKID =?";
            try (PreparedStatement stmt = c.prepareStatement(sql)) {
                stmt.setLong(1, taskID);
                ResultSet rs = stmt.executeQuery(sql);
                if (rs.next()) {
                    task = new StorageTask(taskID, rs.getString("FILELOCATION"), rs.getString("USERNAME"));
                    task.setState(StorageState.valueOf(rs.getString("STATE")));
                }
            } catch (Exception e) {
                LOGGER.error(e);
                return getTask(taskID, false);
            } finally {
                releaseConnection();

            }
        } else {
            task = trackerMap.get(taskID);
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
    public StorageTask addNewTask(String fileLocation, String userName) {
        long key = -1L;
           c = getConnection();
        try (PreparedStatement stmt = c.prepareStatement("INSERT INTO STORAGETASKS(STATE,FILELOCATION,USERNAME) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, "NEW");
            stmt.setString(2, fileLocation);
            stmt.setString(3, userName);
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
            StorageTask task = new StorageTask(key, fileLocation, userName);
            offer(task);
            return task;
        }
    }

}

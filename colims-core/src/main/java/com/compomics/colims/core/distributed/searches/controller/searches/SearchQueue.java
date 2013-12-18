/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.distributed.searches.controller.searches;

import com.compomics.colims.core.distributed.searches.controller.searches.searchtask.SearchTask;
import com.compomics.colims.core.distributed.searches.respin.model.enums.RespinState;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.PriorityQueue;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kenneth Verheggen
 */
@Component("searchQueue")
public class SearchQueue extends PriorityQueue<SearchTask> implements Runnable {

    private Connection connection;
    private boolean connectionLocked = false;
    private File adress;
    private final Logger LOGGER = Logger.getLogger(SearchQueue.class);
    private File outputDir;

    private SearchQueue() {
        this.adress = new File(System.getProperty("user.home") + "/.compomics/ColimsController/SearchController/");
        setUpTables();
    }

    private SearchQueue(String dbAddress) {
        this.adress = new File(dbAddress);
        setUpTables();
    }

    @Override
    public boolean offer(SearchTask task) {
        return super.offer(task);
    }

    @Override
    public SearchTask poll() {
        SearchTask nextTask = super.poll();
        if (nextTask != null) {
            updateTask(nextTask, RespinState.STARTUP);
        }
        return nextTask;
    }

    private Connection getConnection() {
        while (this.connectionLocked == true) {
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
                connection = DriverManager.getConnection("jdbc:sqlite:" + adress.getAbsolutePath() + "/SearchController.db");
            } catch (ClassNotFoundException | SQLException ex) {
                LOGGER.error(ex);
            }
        }
        this.connectionLocked = true;
        return connection;
    }

    private void releaseConnection() {
        this.connectionLocked = false;
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
                    = "CREATE TABLE IF NOT EXISTS SEARCHTASKS "
                    + "(TASKID INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + " STATE TEXT NULL, "
                    + " MGFLOCATION TEXT NULL, "
                    + " PARAMLOCATION TEXT NULL, "
                    + " FASTALOCATION INTEGER NULL,"
                    + " USERNAME TEXT NULL,"
                    + " INSTRUMENT TEXT NULL,"
                    + " SAMPLEID INT NULL,"
                    + " SEARCHNAME TEXT NULL)";
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
            String sql = "UPDATE SEARCHTASKS SET STATE ='NEW' WHERE (STATE !='COMPLETED' AND STATE !='ERROR')";
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
        try (Statement stmt = connection.createStatement()) {
            String sql = "SELECT * FROM SEARCHTASKS WHERE STATE ='NEW'";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                long taskId = rs.getLong("TASKID");
                String mgfLocation = rs.getString("MGFLOCATION");
                String parameterLocation = rs.getString("PARAMLOCATION");
                String fastaLocation = rs.getString("FASTALOCATION");
                String userName = rs.getString("USERNAME");
                String searchName = rs.getString("SEARCHNAME");
                String instrumentName = rs.getString("INSTRUMENT");
                long sampleID = rs.getLong("SAMPLEID");
                this.offer(new SearchTask(taskId, mgfLocation, parameterLocation, fastaLocation, userName, searchName, instrumentName, sampleID));
            }
        } catch (Exception e) {
            LOGGER.error(e);
        } finally {
            releaseConnection();
        }
    }

    public void updateTask(SearchTask task, RespinState respinState) {
        task.setState(respinState);
        connection = getConnection();
        long taskID = task.getTaskID();
        String state = task.getState().toString();
        String sql = "UPDATE SEARCHTASKS SET STATE =? WHERE TASKID=?";
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

    public SearchTask getTask(long taskID) {
        SearchTask task = null;
        connection = getConnection();
        String sql = "SELECT * FROM STORAGETASKS WHERE TASKID =?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, taskID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                task = new SearchTask(taskID,
                        rs.getString("MGFLOCATION"),
                        rs.getString("PARAMLOCATION"),
                        rs.getString("FASTALOCATION"),
                        rs.getString("USERNAME"),
                        rs.getString("SEARCHNAME"),
                        rs.getString("INSTRUMENT"),
                        rs.getLong("SAMPLEID"));
                task.setState(RespinState.valueOf(rs.getString("STATE")));
            }
        } catch (Exception e) {
            LOGGER.error(e);
        } finally {
            releaseConnection();

        }
        return task;
    }

    public SearchTask addNewTask(String mgfFileLocation, String paramFileLocation, String fastaFileLocation, String userName, String searchName, String instrumentName, long sampleID) {
        long key = -1L;
        connection = getConnection();
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO SEARCHTASKS(STATE,MGFLOCATION,PARAMLOCATION,FASTALOCATION,USERNAME,SEARCHNAME,INSTRUMENT,SAMPLEID) VALUES(?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, "NEW");
            stmt.setString(2, mgfFileLocation);
            stmt.setString(3, paramFileLocation);
            stmt.setString(4, fastaFileLocation);
            stmt.setString(5, userName);
            stmt.setString(6, searchName);
            stmt.setString(7, instrumentName);
            stmt.setLong(8, sampleID);
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
            SearchTask task = new SearchTask(key, mgfFileLocation, paramFileLocation, fastaFileLocation, userName, searchName, instrumentName, sampleID);
            offer(task);
            return task;
        }
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

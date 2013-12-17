/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.searches.controller.workers;

import com.compomics.colims.core.searches.controller.searches.searchqueue.SearchQueue;
import com.compomics.colims.core.searches.controller.searches.searchqueue.searchtask.SearchTask;
import com.compomics.colims.core.searches.controller.workers.worker.Worker;
import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kenneth Verheggen
 */
@Component("workerQueue")
public class WorkerQueue extends PriorityQueue<Worker> implements Runnable {

    @Autowired
    SearchQueue searchQueue;

    private final Logger LOGGER = Logger.getLogger(WorkerQueue.class);
    private final List<Worker> connectedInThisSession = new ArrayList<>();

    private WorkerQueue() {
    }

    @Override
    public boolean offer(Worker task) {
        if (connectedInThisSession.contains(task)) {
            task.setState(WorkerState.AVAILABLE);
        } else {
            connectedInThisSession.add(task);
        }
        return super.offer(task);

    }

    @Override
    public Worker poll() {
        Worker nextTask = super.poll();
        if (nextTask != null) {
            nextTask.setState(WorkerState.BUSY);
        }
        return nextTask;
    }

    @Override
    public void run() {
        while (true) {
            //RUN FROM COLIMS !!!!
            Worker currentWorker = poll();
            if (currentWorker != null) {
                SearchTask taskToRun = searchQueue.poll();
                if (taskToRun != null) {
                    sendToWorker(taskToRun, currentWorker);
                } else {
                    currentWorker.setState(WorkerState.AVAILABLE);
                    offer(currentWorker);
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    LOGGER.error(ex);
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

    private void sendToWorker(SearchTask task, Worker worker) {
        PrintWriter writer = new PrintWriter(worker.getOutputStream());
        writer.println(task.getUserName() + ">.<"
                + task.getSearchName() + ">.<"
                + task.getMgfLocation() + ">.<"
                + task.getParameterLocation() + ">.<"
                + task.getFastaLocation());
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.compomics.colims.client.event;

import com.compomics.colims.core.distributed.model.CompletedDbTask;

/**
 * Event generated when a CompletedDbTask from the distributed module is received.
 *
 * @author Niels Hulstaert
 */
public class CompletedDbTaskEvent {

    private final CompletedDbTask completedDbTask;

    /**
     * Constructor.
     *
     * @param completedDbTask the CompletedDbTask instance
     */
    public CompletedDbTaskEvent(CompletedDbTask completedDbTask) {
        this.completedDbTask = completedDbTask;
    }

    public CompletedDbTask getCompletedDbTask() {
        return completedDbTask;
    }

}

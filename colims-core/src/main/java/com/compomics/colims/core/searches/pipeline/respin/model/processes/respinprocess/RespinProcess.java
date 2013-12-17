/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.searches.pipeline.respin.model.processes.respinprocess;

import com.compomics.colims.core.searches.pipeline.respin.model.enums.RespinState;
import com.compomics.colims.core.searches.pipeline.respin.model.exception.RespinException;
import org.apache.log4j.Logger;

/**
 *
 * @author Kenneth
 */
public class RespinProcess {

    private RespinCommandLine command;
    private static Logger LOGGER = Logger.getLogger(RespinProcess.class);
    public RespinState state = RespinState.STARTUP;

    public RespinProcess(RespinCommandLine command) {
        System.out.println("COMMAND = NULL " + command == null);
        this.command = command;
        RespinState.setRespinCommandLine(command);
    }

    public RespinState getState() {
        return state;
    }

    public void setState(RespinState state) {
        this.state = state;
    }

    public void proceed() throws RespinException {
        this.state.prceed(this);
    }

    public void end() {
        command.cleanUp();
        this.state = RespinState.CLOSED;
    }

    public RespinCommandLine getCommandLine() {
        return command;
    }
}

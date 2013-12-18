/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.distributed.searches.respin.model.enums;

import com.compomics.colims.core.distributed.searches.respin.model.processes.respinprocess.RespinProcess;
import com.compomics.colims.core.distributed.searches.respin.model.exception.RespinException;
import com.compomics.colims.core.distributed.searches.respin.model.processes.respinprocess.RespinCommandLine;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

/**
 *
 * @author Kenneth
 */
public enum RespinState {

    ERROR {
                @Override
                public void prceed(RespinProcess entity) throws RespinException {

                }

                @Override
                public String toString() {
                    return "Failed";
                }
            },
    NEW {
                @Override
                public void prceed(RespinProcess entity) throws RespinException {
                    entity.state = STARTUP;
                }

                @Override
                public String toString() {
                    return "Waiting for start of search";
                }
            },
    STARTUP {
                @Override
                public void prceed(RespinProcess entity) throws RespinException {
                    try {
                        command.init();
                    } catch (Exception ex) {
                        entity.end();
                        throw (new RespinException(ex.getMessage()));
                    }
                    entity.state = EVALUATING_SEARCHPARAMETERS;
                }

                @Override
                public String toString() {
                    return "Initiating search";
                }
            },
    EVALUATING_SEARCHPARAMETERS {
                @Override
                public void prceed(RespinProcess entity) throws RespinException {
                    try {
                        command.adjustParametersToUserSpecs();
                    } catch (IOException | ClassNotFoundException ex) {
                        LOGGER.error(ex);
                        entity.end();
                        throw (new RespinException(ex.getMessage()));
                    }
                    entity.state = RUN_SEARCHGUI;
                }

                @Override
                public String toString() {
                    return "Checking searchparameters";
                }
            },
    RUN_SEARCHGUI {
                @Override
                public void prceed(RespinProcess entity) throws RespinException {
                    try {
                        command.runSearchGUI();
                    } catch (FileNotFoundException ex) {
                        entity.end();
                        throw (new RespinException(ex.getMessage()));
                    } catch (IOException ex) {
                        entity.end();
                        throw (new RespinException(ex.getMessage()));
                    } catch (ConfigurationException | InterruptedException ex) {
                        entity.end();
                        throw (new RespinException(ex.getMessage()));
                    }
                    entity.state = RUN_PEPTIDESHAKER;
                }

                @Override
                public String toString() {
                    return "Running SearchGUI";
                }
            },
    RUN_PEPTIDESHAKER {
                @Override
                public void prceed(RespinProcess entity) throws RespinException {
                    try {
                        command.runPeptideShaker();
                    } catch (FileNotFoundException ex) {
                        entity.end();
                        throw (new RespinException(ex.getMessage()));
                    } catch (IOException ex) {
                        entity.end();
                        throw (new RespinException(ex.getMessage()));
                    } catch (ConfigurationException | InterruptedException ex) {
                        entity.end();
                        throw (new RespinException(ex.getMessage()));
                    }
                    entity.state = CLEAN_UP;
                }

                @Override
                public String toString() {
                    return "Running PeptideShaker";
                }
            },
    STORE_RESULTS {
                @Override
                public void prceed(RespinProcess entity) throws RespinException {
                    try {
                        command.storeResults();
                    } catch (IOException ex) {
                        entity.end();
                        throw (new RespinException(ex.getMessage()));
                    }
                    entity.state = CLEAN_UP;
                }

                @Override
                public String toString() {
                    return "Storing results";
                }
            },
    CLEAN_UP {
                @Override
                public void prceed(RespinProcess entity) {
                    try {
                        command.cleanUp();
                    } catch (Exception e) {
                        LOGGER.error(e);
                    }
                    entity.state = CLOSED;
                }

                @Override
                public String toString() {
                    return "Deleting temporary files";
                }
            },
    CLOSED {
                @Override
                public void prceed(RespinProcess entity) {
                    entity.state = CLOSED;
                }

                @Override
                public String toString() {
                    return "FINISHING";
                }
            };

    private static void sendNotifcation(String message) {
        System.out.println(message);
    }
    private static final Logger LOGGER = Logger.getLogger(RespinState.class);

    public abstract void prceed(RespinProcess entity) throws RespinException;
    //
    private static RespinCommandLine command;

    public static void setRespinCommandLine(RespinCommandLine command) {
        RespinState.command = command;
    }
}

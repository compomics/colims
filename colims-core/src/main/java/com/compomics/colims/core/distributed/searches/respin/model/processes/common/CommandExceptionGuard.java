/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.distributed.searches.respin.model.processes.common;

import com.compomics.colims.core.distributed.searches.respin.model.memory.MemoryManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Kenneth
 */
public class CommandExceptionGuard implements Runnable {

    /**
     * a plain LOGGER
     */
    Logger LOGGER = Logger.getLogger(CommandExceptionGuard.class);
    /**
     * process regular inputstream = process outputstream
     */
    private InputStream ois;
    /**
     * process error-related inputstream = process outputstream
     */
    private InputStream eis;
    /**
     * Type of the detected problem
     */
    private String type;
    /**
     * The process that has been hooked by this CommandExceptionGuard
     */
    private Process process;
    /**
     * Keywords that need to be monitored
     */
    private static final List<String> keyWords = new ArrayList<String>();

    public CommandExceptionGuard(Process processus) {

        this.process = processus;
        this.ois = processus.getInputStream();
        this.eis = processus.getErrorStream();
        this.type = "ERROR";
        keyWords.add("ERROR");
        keyWords.add("FATAL");
        keyWords.add("PLEASE CONTACT");
        keyWords.add("EXCEPTION");
        keyWords.add("SEARCH COMPLETED");
        keyWords.add("COMPOMICSERROR");
        keyWords.add("NO IDENTIFICATIONS RETAINED");
    }

    @Override
    public void run() {
        InputStream mergedInputStream = new SequenceInputStream(ois, eis);
        try {
            BufferedReader processOutputStream = new BufferedReader(new InputStreamReader(mergedInputStream));
            String line;
            LOGGER.debug("An errorguard was hooked to the process.");
            while ((line = processOutputStream.readLine()) != null) {
                try {
                    LOGGER.debug(line);
                    for (String aKeyword : keyWords) {
                        if (line.toUpperCase().contains(aKeyword)) {
                            while ((line = processOutputStream.readLine()) != null) {
                                LOGGER.debug(line);
                                process.destroy();
                                break;
                            }
                            mergedInputStream.close();
                        }
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        } catch (NullPointerException | IOException ex) {
            LOGGER.error(ex);
        }
    }
}
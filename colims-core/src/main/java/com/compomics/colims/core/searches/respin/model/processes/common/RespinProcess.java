/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.searches.respin.model.processes.common;

import java.io.IOException;
import java.util.List;
import org.apache.commons.configuration.ConfigurationException;

/**
 *
 * @author Kenneth
 */
public interface RespinProcess {

    List<String> generateCommand() throws IOException, ConfigurationException, NullPointerException;
}

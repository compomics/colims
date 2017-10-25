package com.compomics.colims.client;

import com.compomics.software.CompomicsWrapper;
import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Davy
 */
public class ColimsClientStarterWrapper extends CompomicsWrapper {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ColimsClientStarterWrapper.class);

    /**
     * Starts the launcher by calling the launch method. Use this as the main
     * class in the jar file.
     *
     * @param args the arguments to pass to the tool
     */
    public ColimsClientStarterWrapper(final String[] args) {
        try {
            File jarFile = new File(ColimsClientStarterWrapper.class.getProtectionDomain().getCodeSource().getLocation().toURI());

            String mainClass = "com.compomics.colims.client.ColimsClientStarter";

            String[] argsAddedTo = Arrays.copyOf(args, args.length + 1);
            System.out.println(argsAddedTo.length);
            argsAddedTo[argsAddedTo.length - 1] = "";
            launchTool("Colims-client", jarFile, null, mainClass, argsAddedTo);
        } catch (URISyntaxException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * Starts the launcher by calling the launch method. Use this as the main
     * class in the jar file.
     *
     * @param args the main method String argument Array
     */
    public static void main(final String[] args) {
        new ColimsClientStarterWrapper(args);
    }
}

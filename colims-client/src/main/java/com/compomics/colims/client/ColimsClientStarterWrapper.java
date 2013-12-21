package com.compomics.colims.client;

import com.compomics.software.CompomicsWrapper;
import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;
import org.apache.log4j.Logger;

/**
 *
 * @author Davy
 */
public class ColimsClientStarterWrapper extends CompomicsWrapper {

    // Class specific log4j logger for ParserStarter instances.
    private static Logger logger = Logger.getLogger(ColimsClientStarterWrapper.class);

    /**
     * Starts the launcher by calling the launch method. Use this as the main
     * class in the jar file.
     *
     * @param args the arguments to pass to the tool
     */
    public ColimsClientStarterWrapper(String[] args) {
        try {
            File jarFile = new File(ColimsClientStarterWrapper.class.getProtectionDomain().getCodeSource().getLocation().toURI());

            String mainClass = "com.compomics.colims.client.ColimsClientStarter";

            StringBuilder fileLocations = new StringBuilder();
            String[] argsAddedTo = Arrays.copyOf(args, args.length + 1);
            System.out.println(argsAddedTo.length);
            argsAddedTo[argsAddedTo.length - 1] = fileLocations.toString();
            launchTool("Colims-client", jarFile, null, mainClass, argsAddedTo);
        } catch (URISyntaxException ex) {
            logger.error(ex);
        }
    }

    /**
     * Starts the launcher by calling the launch method. Use this as the main
     * class in the jar file.
     *
     * @param args
     */
    public static void main(String[] args) {
        new ColimsClientStarterWrapper(args);
    }
}

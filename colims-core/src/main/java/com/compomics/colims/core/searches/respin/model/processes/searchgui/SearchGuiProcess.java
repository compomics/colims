/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.searches.respin.model.processes.searchgui;

import com.compomics.colims.core.searches.respin.control.configuration.RespinProperties;
import com.compomics.colims.core.searches.respin.model.memory.MemoryManager;
import com.compomics.colims.core.searches.respin.model.processes.common.RespinProcess;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

/**
 *
 * @author Kenneth
 */
public class SearchGuiProcess implements RespinProcess {

    private final File mgf;
    private final File searchParam;
    private static final Logger LOGGER = Logger.getLogger(SearchGuiProcess.class);
    private static RespinProperties respinProps;
    private static File outputFolder;

    public SearchGuiProcess(File mgf, File searchParam) {
        this.mgf = mgf;
        this.searchParam = searchParam;
        try {
            respinProps = RespinProperties.getInstance();
            outputFolder = respinProps.getTempResultDirectory();
        } catch (IOException ex) {
            LOGGER.error(ex);
        }
    }

    @Override
    public List<String> generateCommand() throws IOException, ConfigurationException, NullPointerException {
        List<String> searchGUICommandLine = new ArrayList<String>();
        searchGUICommandLine.add("java");
        searchGUICommandLine.add("-Xmx" + MemoryManager.getAllowedRam() + "M");
        searchGUICommandLine.add("-cp");
        searchGUICommandLine.add(getSearchGuiJar());
        searchGUICommandLine.add("eu.isas.searchgui.cmd.SearchCLI");
        searchGUICommandLine.add("-spectrum_files");
        searchGUICommandLine.add(mgf.getAbsolutePath());
        searchGUICommandLine.add("-output_folder");
        searchGUICommandLine.add(outputFolder.getAbsolutePath());
        searchGUICommandLine.add("-search_params");
        searchGUICommandLine.add(searchParam.getAbsolutePath());
        searchGUICommandLine.add("-ppm");
        searchGUICommandLine.add("2");
        searchGUICommandLine.add("-omssa_folder");
        searchGUICommandLine.add('"' + respinProps.getOMSSALocation() + '"');
        searchGUICommandLine.add("-xtandem_folder");
        searchGUICommandLine.add('"' + respinProps.getXTandemLocation() + '"');

        //optionals
        System.out.println();
        for (String aCommand : searchGUICommandLine) {
            System.out.print(aCommand + " ");
        }
        System.out.println();
        return searchGUICommandLine;
    }

    public void setOutputFolder(File outputFolder) {
        this.outputFolder = outputFolder;
    }

    public String getSearchGuiJar() {
        return respinProps.getSearchGUIJarPath();
    }
    /* public String getSearchGuiJar() {
     File[] searchGuiJar = ProcessFile.searchGUIFolder.getFile().listFiles(new FilenameFilter() {
     @Override
     public boolean accept(File dir, String name) {
     if (name.toLowerCase().startsWith("searchgui")) {
     return true;
     } else {
     return false;
     }
     }
     });
     if (searchGuiJar.length == 0) {
     return null;
     } else {
     return searchGuiJar[0].getAbsolutePath();
     }
     }*/
    /**
     * String protease = trypsin;
     *
     *
     * BuilderObject ibuldsomting = new
     * BuilderObject(name,adress).andPostnummer(postNummer); BuilderObject
     * ibuldsomting = new
     * BuilderObject(name,adress).andPostnummer(postNummer).andCountry(country);
     *
     *
     * public BuilderObject(String name,String adress){ this.name = name
     * this.adres = adres }
     *
     * public BuilderObject andPostnummer(int postNummer){ this.postNummer =
     * postNummer; }
     */
}

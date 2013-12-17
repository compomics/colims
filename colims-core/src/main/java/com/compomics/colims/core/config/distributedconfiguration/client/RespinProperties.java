/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.config.distributedconfiguration.client;

import com.compomics.colims.core.config.distributedconfiguration.DistributedProperties;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 *
 * @author Kenneth
 */
public class RespinProperties implements DistributedProperties {

    private static PropertiesConfiguration properties = new PropertiesConfiguration();
    private static final File respinDirectory = new File(System.getProperty("user.home") + "/.compomics/respin/");
    private static File propertiesFile;
    private static final Logger LOGGER = Logger.getLogger(RespinProperties.class);
    private static RespinProperties respinProps;

    public static void setPropertiesFile(File propertiesFile) {
        RespinProperties.propertiesFile = propertiesFile;
    }

    public static void reload() throws FileNotFoundException {
        try {
            properties.load(new FileInputStream(propertiesFile));
        } catch (ConfigurationException ex) {
            LOGGER.error(ex);
        }
    }

    private void initiate() throws IOException {

        if (!propertiesFile.exists()) {
            propertiesFile.getParentFile().mkdirs();
            propertiesFile.createNewFile();
            setDefaultProperties();
        } else {
            try {
                properties.load(new FileInputStream(propertiesFile));
            } catch (ConfigurationException ex) {
                LOGGER.error(ex);
            }
        }
    }

    private RespinProperties() {
    }

    public static RespinProperties getInstance() throws IOException {
        if (respinProps == null) {
            respinProps = new RespinProperties();
            respinProps.initiate();
        }
        return respinProps;
    }

    public double getFixedThreshold() {
        return 0.85;
    }

    public double getConsideredThreshold() {
        return 0.0;
    }

    public void save() {
        try {
            properties.save(propertiesFile);
        } catch (ConfigurationException ex) {
            LOGGER.error(ex);
        }
    }

    @Override
    public void setDefaultProperties() {
        setSearchGUIJarPath("");
        setPeptideShakerJarPath("");
        setSearchGUIFolder(new File(getSearchGUIJarPath()).getParent());
        setOMSSALocation(getSearchGUIFolder() + "/resources/OMSSA");
        setXTandemLocation(getSearchGUIFolder() + "/resources/XTandem");
        setEnzymeFile(getSearchGUIFolder() + "/resources/conf/searchGUI_enzymes.xml");
        setModFile(getSearchGUIFolder() + "/resources/conf/searchGUI_mods.xml");
        setUserModFile(getSearchGUIFolder() + "/resources/conf/searchGUI_usermods.xml");
        save();
    }

    public void setSearchGUIJarPath(String path) {
        //find what version of omssa/xtandem should be used?
        String arch = System.getProperty("os.arch").toLowerCase();
        String osname = System.getProperty("os.name").toLowerCase();
        String OMSSAversion = null;
        String XTandemVersion = null;
        if (osname.contains("win")) {
            OMSSAversion = "omssa-2.1.9.win32";
            if (arch.toString().toLowerCase().contains("64")) {
                XTandemVersion = "windows 64bits";
            } else {
                XTandemVersion = "windows 32bits";
            }
        } else if (osname.contains("mac")) {
            OMSSAversion = "omssa-2.1.9.macos";
            XTandemVersion = "osx";
        } else if (osname.contains("lin")) {
            OMSSAversion = "omssa-2.1.9.linux";
            if (arch.toString().toLowerCase().contains("64")) {
                XTandemVersion = "linux_64bit";
            } else {
                XTandemVersion = "linux_32bit";
            }
        }
        properties.setProperty("searchgui.jar.path", path);
        setSearchGUIFolder(new File(getSearchGUIJarPath()).getParent());
        System.out.println(new File(getSearchGUIJarPath()).getParentFile().getAbsolutePath());
        setOMSSALocation(new File(getSearchGUIJarPath()).getParent() + "/resources/OMSSA/" + OMSSAversion);
        System.out.println("/resources/OMSSA/" + OMSSAversion);
        setXTandemLocation(new File(getSearchGUIJarPath()).getParent() + "/resources/XTandem/" + XTandemVersion);
        System.out.println("/resources/XTandem/" + XTandemVersion);
        setEnzymeFile(getSearchGUIFolder() + "/resources/conf/searchGUI_enzymes.xml");
        System.out.println("/resources/conf/searchGUI_enzymes.xml");
        setModFile(getSearchGUIFolder() + "/resources/conf/searchGUI_mods.xml");
        System.out.println("/resources/conf/searchGUI_mods.xml");
        setUserModFile(getSearchGUIFolder() + "/resources/conf/searchGUI_usermods.xml");
        System.out.println("/resources/conf/searchGUI_usermods.xml");
        save();
    }

    public String getSearchGUIJarPath() {
        return properties.getString("searchgui.jar.path");
    }

    public void setPeptideShakerJarPath(String path) {
        properties.setProperty("peptideshaker.jar.path", path);
        save();
    }

    public String getPeptideShakerJarPath() {
        return properties.getString("peptideshaker.jar.path");
    }

    public void setOMSSALocation(String path) {
        properties.setProperty("searchgui.omssa.path", path);
        save();
    }

    public String getOMSSALocation() {
        return properties.getString("searchgui.omssa.path");
    }

    public void setXTandemLocation(String path) {
        properties.setProperty("searchgui.xtandem.path", path);
        save();
    }

    public String getXTandemLocation() {
        return properties.getString("searchgui.xtandem.path");
    }

    public void setLocalFastaRepository(String absolutePath) {
        properties.setProperty("repository.fasta.local", absolutePath);
        save();
    }

    public String getLocalFastaRepository() {
        return properties.getString("repository.fasta.local");
    }

    public void setLocalPrideXMLRepository(String absolutePath) {
        properties.setProperty("repository.pridexml.local", absolutePath);
        save();
    }

    public String getLocalPrideXMLRepository() {
        return properties.getString("repository.pridexml.local");
    }

    public void setRemoteFastaRepository(String absolutePath) {
        properties.setProperty("repository.fasta.remote", absolutePath);
        save();
    }

    public String getRemoteFastaRepository() {
        return properties.getString("repository.fasta.remote");
    }

    public void setRemotePrideXMLRepository(String absolutePath) {
        properties.setProperty("repository.pridexml.remote", absolutePath);
        save();
    }

    public String getRemotePrideXMLRepository() {
        return properties.getString("repository.pridexml.remote");
    }

    public void setEnzymeFile(String path) {
        properties.setProperty("searchgui.enzyme.file", path);
        save();
    }

    public File getEnzymeFile() {
        return new File(properties.getString("searchgui.enzyme.file"));
    }

    private void setModFile(String string) {
        properties.setProperty("searchgui.mod.file", string);
        save();
    }

    private void setUserModFile(String string) {
        properties.setProperty("searchgui.usermod.file", string);
        save();
    }

    public File geModFile() {
        return new File(properties.getString("searchgui.mod.file"));
    }

    public File getUserModFile() {
        return new File(properties.getString("searchgui.usermod.file"));
    }

    public File getRespinDirectory() {
        return new File(System.getProperty("user.home") + "/.compomics/respin/");
    }

    public File getTempResultDirectory() {
        return new File(System.getProperty("user.home") + "/.compomics/respin/temp_results/");
    }

    public File getTempSearchParameters() {
        return new File(System.getProperty("user.home") + "/.compomics/respin/temp_results/SearchGUI.parameters");
    }

    private void setSearchGUIFolder(String parent) {
        properties.setProperty("searchgui.parent.folder", parent);
        save();
    }

    public String getSearchGUIFolder() {
        return properties.getString("searchgui.parent.folder");
    }
}

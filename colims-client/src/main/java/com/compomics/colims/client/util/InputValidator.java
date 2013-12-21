/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.client.util;

import com.compomics.colims.distributed.config.distributedconfiguration.client.DistributedProperties;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;

/**
 *
 * @author Kenneth
 */
public class InputValidator {

    private static final Logger LOGGER = Logger.getLogger(InputValidator.class);

    public File putFilesInTempFolder(List<File> fileList, String username) throws IOException, URISyntaxException {
        init();
        File tempFolder = createTempFolder(username);
        for (File aFile : fileList) {
            FileUtils.copyFile(aFile, new File(tempFolder, aFile.getName()));
        }
        return tempFolder;
    }

    private void init() throws IOException {
        File distributedPropFile = new ClassPathResource("distributed/config/distribute.properties").getFile();
        DistributedProperties.setPropertiesFile(distributedPropFile);
        DistributedProperties.reload();
    }

    private File createTempFolder(String username) throws IOException, URISyntaxException {
        String tempFolderPath = DistributedProperties.getInstance().getStoragePath() + "/" + username + "/colims_temp";
        Files.createTempDirectory(DistributedProperties.getInstance().getStoragePath() + "/colims_temp");
        return new File(tempFolderPath);
    }

}

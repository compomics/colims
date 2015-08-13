package com.compomics.colims.distributed.io;

import com.compomics.colims.core.service.QuantificationSettingsService;
import com.compomics.colims.core.util.IOUtils;
import com.compomics.colims.model.*;
import com.compomics.colims.model.enums.BinaryFileType;
import com.compomics.colims.model.enums.QuantificationEngineType;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("quantificationSettingsMapper")
public class QuantificationSettingsMapper {

    @Autowired
    private QuantificationSettingsService quantificationSettingsService;

    public QuantificationSettings map(QuantificationEngineType quantEngineType, String version, List<File> quantFiles, QuantificationParameters quantParams) throws IOException {
        QuantificationSettings quantSettings = new QuantificationSettings();

        quantSettings.setQuantificationEngine(quantificationSettingsService.getQuantificationEngine(quantEngineType, version));

        quantSettings.setQuantificationParameterSettings(quantParams);

        for (File file : quantFiles) {
            QuantificationFile quantFile = new QuantificationFile();

            quantFile.setFileName(file.getName());
            quantFile.setFilePath(file.getCanonicalPath());
            quantFile.setContent(IOUtils.readAndZip(file)); // do we always want to store the file? idk
            quantFile.setBinaryFileType(BinaryFileType.TEXT);

            quantSettings.getQuantificationFiles().add(quantFile);
        }

        return quantSettings;
    }
}

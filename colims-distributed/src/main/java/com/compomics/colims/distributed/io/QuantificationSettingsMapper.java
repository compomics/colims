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

    public QuantificationSettings map(QuantificationEngineType quantEngineType, String version, QuantificationMethodCvParam quantParams) throws IOException {
        QuantificationSettings quantSettings = new QuantificationSettings();

        quantSettings.setQuantificationEngine(quantificationSettingsService.getQuantificationEngine(quantEngineType, version));

        quantSettings.setQuantificationMethodCvParam(quantParams);

        return quantSettings;
    }
}

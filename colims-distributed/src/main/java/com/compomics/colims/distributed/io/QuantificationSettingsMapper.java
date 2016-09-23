package com.compomics.colims.distributed.io;

import com.compomics.colims.core.service.QuantificationSettingsService;
import com.compomics.colims.model.*;
import com.compomics.colims.model.enums.QuantificationEngineType;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("quantificationSettingsMapper")
public class QuantificationSettingsMapper {

    private final QuantificationSettingsService quantificationSettingsService;

    @Autowired
    public QuantificationSettingsMapper(QuantificationSettingsService quantificationSettingsService) {
        this.quantificationSettingsService = quantificationSettingsService;
    }

    public QuantificationSettings map(QuantificationEngineType quantEngineType, String version, QuantificationMethodCvParam quantParams) throws IOException {
        QuantificationSettings quantSettings = new QuantificationSettings();

        quantSettings.setQuantificationEngine(quantificationSettingsService.getQuantificationEngine(quantEngineType, version));

        quantSettings.setQuantificationMethodCvParam(quantParams);

        return quantSettings;
    }
}

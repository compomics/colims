package com.compomics.colims.distributed.io.maxquant.headers;

import java.io.IOException;
import java.util.EnumMap;

/**
 * Created by Iain on 03/12/2014.
 */
public class ParametersHeaders extends MaxQuantHeaders<ParametersHeader> {

    public enum Headers {
        VERSION
    }

    /**
     * No-arg constructor.
     */
    public ParametersHeaders() throws IOException {
        super(ParametersHeader.class, new EnumMap<>(ParametersHeader.class), "maxquant/parameter_headers.json");
    }
}

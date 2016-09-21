package com.compomics.colims.distributed.io.maxquant.headers;

import java.io.IOException;
import java.util.EnumMap;

/**
 * Created by niels.
 */
public class MqParHeaders extends MaxQuantHeaders<MqParHeader> {

    /**
     * No-arg constructor.
     */
    public MqParHeaders() throws IOException {
        super(MqParHeader.class, new EnumMap<>(MqParHeader.class), "maxquant/mqpar_headers.json", false);
    }
}

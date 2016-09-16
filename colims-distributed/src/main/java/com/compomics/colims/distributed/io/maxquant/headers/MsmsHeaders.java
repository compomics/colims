package com.compomics.colims.distributed.io.maxquant.headers;

import java.io.IOException;
import java.util.EnumMap;

/**
 * Created by Iain on 03/12/2014.
 */
public class MsmsHeaders extends MaxQuantHeaders<MsmsHeader> {

    /**
     * No-arg constructor.
     */
    public MsmsHeaders() throws IOException {
        super(MsmsHeader.class, new EnumMap<>(MsmsHeader.class), "maxquant/msms_headers.json");
    }
}

package com.compomics.colims.distributed.io.maxquant.headers;

import java.util.EnumMap;

/**
 * Created by Iain on 03/12/2014.
 */
public class SummaryHeaders extends MaxQuantHeaders<SummaryHeader> {

    /**
     * No-arg constructor.
     */
    public SummaryHeaders() {
        super(SummaryHeader.class, new EnumMap<>(SummaryHeader.class), "maxquant/summary_headers.txt");
    }
}

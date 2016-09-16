package com.compomics.colims.distributed.io.maxquant.headers;

import java.util.EnumMap;

/**
 * Created by Iain on 02/12/2014.
 */
public class EvidenceHeaders extends MaxQuantHeaders<EvidenceHeader> {

    /**
     * No-arg constructor.
     */
    public EvidenceHeaders() {
        super(EvidenceHeader.class, new EnumMap<>(EvidenceHeader.class), "maxquant/evidence_headers.txt");
    }

}

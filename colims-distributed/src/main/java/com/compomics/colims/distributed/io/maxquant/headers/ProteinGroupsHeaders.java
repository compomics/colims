package com.compomics.colims.distributed.io.maxquant.headers;

import java.io.IOException;
import java.util.EnumMap;

/**
 * Created by Iain on 03/03/2015.
 */
public class ProteinGroupsHeaders extends MaxQuantHeaders<ProteinGroupsHeader> {

    /**
     * No-arg constructor.
     */
    public ProteinGroupsHeaders() throws IOException {
        super(ProteinGroupsHeader.class, new EnumMap<>(ProteinGroupsHeader.class), "maxquant/protein_groups_headers.json");
    }
}

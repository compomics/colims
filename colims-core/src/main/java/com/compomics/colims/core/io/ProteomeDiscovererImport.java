package com.compomics.colims.core.io;

import com.compomics.colims.model.enums.FastaDbType;

import java.io.File;
import java.util.EnumMap;

/**
 * Created by Davy Maddelein on 23/01/2016.
 */
public class ProteomeDiscovererImport extends DataImport {


    private File proteomeDiscovererDatabase;

    public ProteomeDiscovererImport(File proteomeDiscovererDatabase,EnumMap<FastaDbType, Long> fastaDbIds) {
        super(fastaDbIds);
        this.proteomeDiscovererDatabase = proteomeDiscovererDatabase;
    }

    public File getProteomeDiscovererDatabase() {
        return proteomeDiscovererDatabase;
    }
}

package com.compomics.colims.distributed.io.proteomediscoverer.parsers;

import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.enums.FastaDbType;
import com.compomics.thermo_msf_parser_API.lowmeminstance.model.MsfFile;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.File;
import java.sql.SQLException;
import java.util.EnumMap;

/**
 * Created by Davy Maddelein on 23/01/2016.
 */

@Service("ProteomeDiscovererParser")
public class ProteomeDiscovererParser {

    private static final Logger LOGGER = Logger.getLogger(ProteomeDiscovererParser.class);


    public boolean parseDatabase(File ProteomeDiscovererDatabase, EnumMap<FastaDbType, FastaDb> fastaDbs) throws SQLException, ClassNotFoundException {
        // step 0: preparations to start parsing

            MsfFile dbToParse = new MsfFile(ProteomeDiscovererDatabase);

            dbToParse.getVersion();

        // step 1: retrieve analytical runs



        return true;
    }
}

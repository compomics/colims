/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.distributed.storage.processing.colimsimport.factory;

import com.compomics.colims.distributed.storage.enums.StorageType;
import com.compomics.colims.distributed.storage.processing.colimsimport.impl.ColimsMaxQuantImporter;
import com.compomics.colims.distributed.storage.processing.colimsimport.impl.ColimsCpsImporter;
import com.compomics.colims.distributed.storage.processing.colimsimport.ColimsFileImporter;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kenneth Verheggen
 */
@Component("colimsImporterFactory")
public class ColimsImporterFactory {

    @Autowired
    ColimsCpsImporter colimsCpsImporter;
    @Autowired
    ColimsMaxQuantImporter colimsMaxQuantImporter;

    public ColimsFileImporter getImporter(StorageType type) throws IOException {
        ColimsFileImporter importer = null;
        if (type.equals(StorageType.PEPTIDESHAKER)) {
            importer = colimsCpsImporter;
        }
        if (type.equals(StorageType.MAX_QUANT)) {
            importer = colimsMaxQuantImporter;
        }
        return importer;
    }

}

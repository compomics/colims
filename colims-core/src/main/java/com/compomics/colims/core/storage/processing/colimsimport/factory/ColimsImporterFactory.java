/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.storage.processing.colimsimport.factory;

import com.compomics.colims.core.storage.processing.colimsimport.impl.ColimsMaxQuantImporter;
import com.compomics.colims.core.storage.processing.colimsimport.impl.ColimsCpsImporter;
import com.compomics.colims.core.storage.processing.colimsimport.ColimsFileImporter;
import java.io.File;
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

    public ColimsFileImporter getImporter(File fileToStore) throws IOException {
        ColimsFileImporter importer = null;
        if (!fileToStore.exists()) {
            throw new IOException("The file to be stored could not be located");
        }
        if (fileToStore.getName().endsWith(".cps") || fileToStore.getName().endsWith(".cps.gz")) {
            importer = colimsCpsImporter;
        }
        if (fileToStore.getName().endsWith("quant") || fileToStore.getName().endsWith(".gz")) {
            importer = colimsMaxQuantImporter;
        }
        return importer;
    }

}

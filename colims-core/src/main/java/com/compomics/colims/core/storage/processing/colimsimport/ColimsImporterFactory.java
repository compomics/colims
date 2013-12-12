/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.storage.processing.colimsimport;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Kenneth Verheggen
 */
public class ColimsImporterFactory {

    public static ColimsFileImporter getImporter(File fileToStore) throws IOException {
        ColimsFileImporter importer = null;
        if (!fileToStore.exists()) {
            throw new IOException("The file to be stored could not be located");
        }
        if (fileToStore.getName().endsWith(".cps") || fileToStore.getName().endsWith(".cps.gz")) {
            importer = new ColimsCpsImporter();
        }
        if (fileToStore.getName().endsWith("quant") || fileToStore.getName().endsWith(".cps.gz")) {
            importer = new ColimsMaxQuantImporter();
        }
        return importer;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.storage.processing.colimsimport;

import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.core.exception.PeptideShakerIOException;
import java.io.File;

/**
 *
 * @author Kenneth Verheggen
 */
public interface ColimsFileImporter {

    public void storeFile(String username, File fileFolder,long sampleId) throws PeptideShakerIOException, MappingException;
    
    public boolean validate(File fileFolder);
}

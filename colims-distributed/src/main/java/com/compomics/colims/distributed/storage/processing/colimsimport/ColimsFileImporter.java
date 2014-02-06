/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.distributed.storage.processing.colimsimport;

import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.core.dataio.peptideshaker.PeptideShakerIOException;
import java.io.File;
import javax.naming.AuthenticationException;

/**
 *
 * @author Kenneth Verheggen
 */
public interface ColimsFileImporter {

    public void storeFile(String username, File fileFolder, long sampleId, String instrumentName) throws PeptideShakerIOException, MappingException, AuthenticationException;

    public boolean validate(File fileFolder);
}

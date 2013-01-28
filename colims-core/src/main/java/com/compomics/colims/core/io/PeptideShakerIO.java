package com.compomics.colims.core.io;

import com.compomics.colims.core.exception.PeptideShakerIOException;
import com.compomics.colims.core.io.model.PeptideShakerImport;
import java.io.File;

/**
 *
 * @author niels
 */
public interface PeptideShakerIO {
    
    /**
     * Import a PeptideShaker .cps file and return a MsExperiment instance
     * 
     * @param peptideShakerCpsArchive the PepitideShaker .cps file
     * @return 
     */    
    PeptideShakerImport importPeptideShakerCpsArchive(File peptideShakerCpsArchive) throws PeptideShakerIOException;
    
}

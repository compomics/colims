package com.compomics.colims.core.io.maxquant;

import com.compomics.colims.core.io.DataImport;
import com.compomics.colims.model.FastaDb;
import java.io.File;

/**
 *
 * @author Davy
 */
public class MaxQuantImport extends DataImport {
 
    private File maxQuantDirectory;
    
    /**
     * Constructor
     * 
     * @param maxQuantDirectory
     * @param fastaDb
     */
    public MaxQuantImport(final File maxQuantDirectory, final FastaDb fastaDb){
        super(fastaDb);
        this.maxQuantDirectory = maxQuantDirectory;
    }

    public File getMaxQuantDirectory() {
        return maxQuantDirectory;
    }

    public void setMaxQuantDirectory(final File maxQuantDirectory) {
        this.maxQuantDirectory = maxQuantDirectory;
    }
      
}

package com.compomics.colims.core.io.maxquant;

import com.compomics.colims.core.io.DataImport;
import java.io.File;

/**
 *
 * @author Davy
 */
public class MaxQuantDataImport extends DataImport {

    private File maxQuantDirectory;
    
    /**
     * Constructor
     * 
     * @param maxQuantDirectory
     * @param fastaFile
     */
    public MaxQuantDataImport(final File maxQuantDirectory, final File fastaFile){
        super(fastaFile);
        this.maxQuantDirectory = maxQuantDirectory;
    }

    public File getMaxQuantDirectory() {
        return maxQuantDirectory;
    }

    public void setMaxQuantDirectory(final File maxQuantDirectory) {
        this.maxQuantDirectory = maxQuantDirectory;
    }

}

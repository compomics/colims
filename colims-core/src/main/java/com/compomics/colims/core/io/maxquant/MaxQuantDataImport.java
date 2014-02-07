package com.compomics.colims.core.io.maxquant;

import com.compomics.colims.core.io.DataImport;
import java.io.File;

/**
 *
 * @author Davy
 */
public class MaxQuantDataImport extends DataImport {

    private File maxQuantFolder;
    
    public MaxQuantDataImport(final File aMaxQuantFolder, final File aFastaFile){
        super(aFastaFile);
        this.maxQuantFolder = aMaxQuantFolder;
    }

    public File getMaxQuantFolder() {
        return maxQuantFolder;
    }

    public void setMaxQuantFolder(final File maxQuantFolder) {
        this.maxQuantFolder = maxQuantFolder;
    }

}

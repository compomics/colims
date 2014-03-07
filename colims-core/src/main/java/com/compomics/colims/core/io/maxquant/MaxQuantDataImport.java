package com.compomics.colims.core.io.maxquant;

import com.compomics.colims.core.io.DataImport;
import java.io.File;
import org.springframework.core.io.Resource;

/**
 *
 * @author Davy
 */
public class MaxQuantDataImport extends DataImport {

    private File maxQuantFolder;
    
    public MaxQuantDataImport(final File aMaxQuantFolder, final Resource fastaResource){
        super(fastaResource);
        this.maxQuantFolder = aMaxQuantFolder;
    }

    public File getMaxQuantFolder() {
        return maxQuantFolder;
    }

    public void setMaxQuantFolder(final File maxQuantFolder) {
        this.maxQuantFolder = maxQuantFolder;
    }

}

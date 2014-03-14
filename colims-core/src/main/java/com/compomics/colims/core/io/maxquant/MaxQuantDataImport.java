package com.compomics.colims.core.io.maxquant;

import com.compomics.colims.core.io.DataImport;
import java.io.File;
import java.util.Map;

/**
 *
 * @author Davy
 */
public class MaxQuantDataImport extends DataImport {

    private static final String MAXQUANT_DIRECTORY_PATH = "cps_file_path";
    
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

    @Override
    public Map<String, String> asMap() {
        Map<String, String> properties = super.asMap();
        
        properties.put(MAXQUANT_DIRECTORY_PATH, maxQuantDirectory.getAbsolutePath());
        
        return properties;
    }
        
}

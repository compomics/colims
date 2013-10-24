
package com.compomics.colims.core.event;

/**
 *
 * @author Niels Hulstaert
 */
public class ProgressEvent {
    
    private String description;
    private double progress;

    public ProgressEvent(double progress, String description) {
        this.progress = progress;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }    
    
    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }        

}

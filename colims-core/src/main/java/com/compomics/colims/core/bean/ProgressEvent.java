
package com.compomics.colims.core.bean;

/**
 *
 * @author Niels Hulstaert
 */
public class ProgressEvent {

    private String description;
    private double progress;

    public ProgressEvent(final double progress, final String description) {
        this.progress = progress;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(final double progress) {
        this.progress = progress;
    }

}

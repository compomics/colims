/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.client.event.progress;

/**
 * Event thrown when a progress dialog should start.
 *
 * @author Niels Hulstaert
 */
public class ProgressStartEvent {

    /**
     * Is the progress dialog indeterminate.
     */
    private boolean isIndeterminate;
    /**
     * The number of progress steps.
     */
    private Integer numberOfSteps;
    /**
     * The (initial) progress header text.
     */
    private String headerText;

    /**
     * Constructor.
     *
     * @param isIndeterminate whether the progress should be indeterminate or
     * not
     * @param numberOfSteps the number of progress steps
     * @param headerText the progress headerText
     */
    public ProgressStartEvent(boolean isIndeterminate, Integer numberOfSteps, String headerText) {
        this.isIndeterminate = isIndeterminate;
        this.numberOfSteps = numberOfSteps;
        this.headerText = headerText;
    }

    public boolean isIsIndeterminate() {
        return isIndeterminate;
    }

    public void setIsIndeterminate(boolean isIndeterminate) {
        this.isIndeterminate = isIndeterminate;
    }

    public Integer getNumberOfSteps() {
        return numberOfSteps;
    }

    public void setNumberOfSteps(Integer numberOfSteps) {
        this.numberOfSteps = numberOfSteps;
    }

    public String getHeaderText() {
        return headerText;
    }

    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }

}

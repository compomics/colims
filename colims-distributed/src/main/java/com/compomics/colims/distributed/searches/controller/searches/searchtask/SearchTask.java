/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.distributed.searches.controller.searches.searchtask;

import com.compomics.colims.distributed.searches.respin.model.enums.RespinState;
import com.compomics.util.experiment.identification.SearchParameters;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Kenneth
 */
public class SearchTask implements Comparable {

    private long taskID;
    private String mgfLocation;
    private String fastaLocation;
    private String parameterLocation;
    private String userName;
    private String searchName;
    private RespinState state = RespinState.NEW;
    private String instrument;
    private long sampleID;

    public SearchTask(long taskID, String mgfLocation, String fastaLocation, String parameterLocation, String userName, String searchName, String instrument, long sampleID) {
        this.taskID = taskID;
        this.mgfLocation = mgfLocation;
        this.fastaLocation = fastaLocation;
        this.parameterLocation = parameterLocation;
        this.userName = userName;
        this.searchName = searchName;
        this.instrument = instrument;
        this.sampleID = sampleID;
    }

    public SearchTask(long taskID, String mgfLocation, String parameterLocation, String userName, String searchName, String instrument, long sampleID) throws ClassNotFoundException, IOException {
        this.taskID = taskID;
        this.mgfLocation = mgfLocation;
        this.fastaLocation = SearchParameters.getIdentificationParameters(new File(parameterLocation)).getFastaFile().getAbsolutePath();
        this.parameterLocation = parameterLocation;
        this.userName = userName;
        this.searchName = searchName;
        this.instrument = instrument;
        this.sampleID = sampleID;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public long getSampleID() {
        return sampleID;
    }

    public void setSampleID(long sampleID) {
        this.sampleID = sampleID;
    } 
    
    public long getTaskID() {
        return taskID;
    }

    public void setTaskID(long taskID) {
        this.taskID = taskID;
    }

    public String getMgfLocation() {
        return mgfLocation;
    }

    public void setMgfLocation(String mgfLocation) {
        this.mgfLocation = mgfLocation;
    }

    public String getFastaLocation() {
        return fastaLocation;
    }

    public void setFastaLocation(String fastaLocation) {
        this.fastaLocation = fastaLocation;
    }

    public String getParameterLocation() {
        return parameterLocation;
    }

    public void setParameterLocation(String parameterLocation) {
        this.parameterLocation = parameterLocation;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }

    public void setState(RespinState state) {
        this.state = state;
    }

    public RespinState getState() {
        return this.state;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof SearchTask) {
            SearchTask otherTask = (SearchTask) o;
            if (getTaskID() > otherTask.getTaskID()) {
                return 1;
            } else if (getTaskID() == otherTask.getTaskID()) {
                return 0;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

}

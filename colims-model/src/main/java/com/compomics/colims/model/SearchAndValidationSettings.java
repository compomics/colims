/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "search_and_validation_settings")
@Entity
public class SearchAndValidationSettings extends DatabaseEntity {

    private static final long serialVersionUID = 1L;

    @OneToMany(mappedBy = "searchAndValidationSettings")
    private List<IdentificationFile> identificationFiles = new ArrayList<>();
    @JoinColumn(name = "l_experiment_id", referencedColumnName = "id")
    @ManyToOne
    private Experiment experiment;
    @JoinColumn(name = "l_search_engine_id", referencedColumnName = "id")
    @ManyToOne
    private SearchEngine searchEngine;
    @JoinColumn(name = "l_fasta_db_id", referencedColumnName = "id")
    @ManyToOne
    private FastaDb fastaDb;
    @JoinColumn(name = "l_search_param_settings_id", referencedColumnName = "id")
    @ManyToOne
    private SearchParameterSettings searchParameterSettings;    

    public List<IdentificationFile> getIdentificationFiles() {
        return identificationFiles;
    }

    public void setIdentificationFiles(List<IdentificationFile> identificationFiles) {
        this.identificationFiles = identificationFiles;
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    public SearchEngine getSearchEngine() {
        return searchEngine;
    }

    public void setSearchEngine(SearchEngine searchEngine) {
        this.searchEngine = searchEngine;
    }

    public FastaDb getFastaDb() {
        return fastaDb;
    }

    public void setFastaDb(FastaDb fastaDb) {
        this.fastaDb = fastaDb;
    }

    public SearchParameterSettings getSearchParameterSettings() {
        return searchParameterSettings;
    }

    public void setSearchParameterSettings(SearchParameterSettings searchParameterSettings) {
        this.searchParameterSettings = searchParameterSettings;
    }    

}

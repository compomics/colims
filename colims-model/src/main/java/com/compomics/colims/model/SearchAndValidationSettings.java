/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
public class SearchAndValidationSettings extends AbstractDatabaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @JoinColumn(name = "l_experiment_id", referencedColumnName = "id")
    @ManyToOne
    private Experiment experiment;
    @OneToMany(mappedBy="searchAndValidationSettings")
    private List<SearchAndValSetHasSearchEngine> searchAndValSetHasSearchEngines = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    public List<SearchAndValSetHasSearchEngine> getSearchAndValSetHasSearchEngines() {
        return searchAndValSetHasSearchEngines;
    }

    public void setSearchAndValSetHasSearchEngines(List<SearchAndValSetHasSearchEngine> searchAndValSetHasSearchEngines) {
        this.searchAndValSetHasSearchEngines = searchAndValSetHasSearchEngines;
    }       
        
}

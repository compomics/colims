/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "search_and_val_set_has_search_engine")
@Entity 
public class SearchAndValSetHasSearchEngine extends AbstractDatabaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @OneToMany(mappedBy="searchAndValSetHasSearchEngine")
    private List<SearchParameterSettings> searchParametersSettings = new ArrayList<>();
    @JoinColumn(name = "l_search_engine_id", referencedColumnName = "id")
    @ManyToOne
    private SearchEngine searchEngine;
    @JoinColumn(name = "l_s_and_val_set_id", referencedColumnName = "id")
    @ManyToOne
    private SearchAndValidationSettings searchAndValidationSettings;

    public SearchAndValidationSettings getSearchAndValidationSettings() {
        return searchAndValidationSettings;
    }

    public void setSearchAndValidationSettings(SearchAndValidationSettings searchAndValidationSettings) {
        this.searchAndValidationSettings = searchAndValidationSettings;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }    
    
    public SearchEngine getSearchEngine() {
        return searchEngine;
    }

    public void setSearchEngine(SearchEngine searchEngine) {
        this.searchEngine = searchEngine;
    }

    public List<SearchParameterSettings> getSearchParametersSettings() {
        return searchParametersSettings;
    }

    public void setSearchParametersSettings(List<SearchParameterSettings> searchParametersSettings) {
        this.searchParametersSettings = searchParametersSettings;
    }
        
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "search_engine")
@Entity
public class SearchEngine extends AbstractDatabaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @OneToMany(mappedBy = "searchEngine")
    private List<SearchAndValSetHasSearchEngine> searchAndValSetHasSearchEngines;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }    

    public List<SearchAndValSetHasSearchEngine> getSearchAndValSetHasSearchEngines() {
        return searchAndValSetHasSearchEngines;
    }

    public void setSearchAndValSetHasSearchEngines(List<SearchAndValSetHasSearchEngine> searchAndValSetHasSearchEngines) {
        this.searchAndValSetHasSearchEngines = searchAndValSetHasSearchEngines;
    }
}

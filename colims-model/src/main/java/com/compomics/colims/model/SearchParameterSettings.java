/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "search_parameter_settings")
@Entity
public class SearchParameterSettings extends AbstractDatabaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @JoinColumn(name = "l_s_and_val_set_has_s_eng_id", referencedColumnName = "id")
    @ManyToOne
    private SearchAndValSetHasSearchEngine searchAndValSetHasSearchEngine;

    public SearchAndValSetHasSearchEngine getSearchAndValSetHasSearchEngine() {
        return searchAndValSetHasSearchEngine;
    }

    public void setSearchAndValSetHasSearchEngine(SearchAndValSetHasSearchEngine searchAndValSetHasSearchEngine) {
        this.searchAndValSetHasSearchEngine = searchAndValSetHasSearchEngine;
    }    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import javax.persistence.Entity;
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
            
    @JoinColumn(name = "l_s_and_val_set_has_s_eng_id", referencedColumnName = "id")
    @ManyToOne
    private SearchAndValSetHasSearchEngine searchAndValSetHasSearchEngine;
    @JoinColumn(name = "l_fasta_db_id", referencedColumnName = "id")
    @ManyToOne
    private FastaDb fastaDb;

    public SearchAndValSetHasSearchEngine getSearchAndValSetHasSearchEngine() {
        return searchAndValSetHasSearchEngine;
    }

    public void setSearchAndValSetHasSearchEngine(SearchAndValSetHasSearchEngine searchAndValSetHasSearchEngine) {
        this.searchAndValSetHasSearchEngine = searchAndValSetHasSearchEngine;
    } 

    public FastaDb getFastaDb() {
        return fastaDb;
    }

    public void setFastaDb(FastaDb fastaDb) {
        this.fastaDb = fastaDb;
    }        
    
}

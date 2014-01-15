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
@Table(name = "identification_file")
@Entity
public class IdentificationFile extends DatabaseEntity {

    private static final long serialVersionUID = 1L;

    @JoinColumn(name = "l_search_and_val_set_id", referencedColumnName = "id")
    @ManyToOne
    private SearchAndValidationSettings searchAndValidationSettings;
    @OneToMany(mappedBy = "identificationFile")
    private List<Peptide> peptides = new ArrayList<>();

    public SearchAndValidationSettings getSearchAndValidationSettings() {
        return searchAndValidationSettings;
    }

    public void setSearchAndValidationSettings(SearchAndValidationSettings searchAndValidationSettings) {
        this.searchAndValidationSettings = searchAndValidationSettings;
    }

    public List<Peptide> getPeptides() {
        return peptides;
    }

    public void setPeptides(List<Peptide> peptides) {
        this.peptides = peptides;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Table(name = "search_modification")
@Entity
public class SearchModification extends AbstractModification {

    private static final long serialVersionUID = -2832229647167375630L;

    /**
     * The PeptideHasModification instances from the join table between the peptide and modification tables.
     */
    @OneToMany(mappedBy = "searchModification")
    private List<SearchParametersHasModification> searchParametersHasModifications = new ArrayList<>();

    /**
     * No-arg constructor.
     */
    public SearchModification() {
    }

    /**
     * Constructor.
     *
     * @param name the modification name.
     */
    public SearchModification(String name) {
        super(name);
    }

    /**
     * Constructor.
     *
     * @param accession the modification accession
     * @param name      the modification name
     */
    public SearchModification(String accession, String name) {
        super(accession, name);
    }

    public List<SearchParametersHasModification> getSearchParametersHasModifications() {
        return searchParametersHasModifications;
    }

    public void setSearchParametersHasModifications(List<SearchParametersHasModification> searchParametersHasModifications) {
        this.searchParametersHasModifications = searchParametersHasModifications;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "search_modification")
@Entity
public class SearchModification extends AbstractModification {

    private static final long serialVersionUID = -2832229647167375630L;

    public SearchModification(String name) {
        super(name);
    }

    public SearchModification(String accession, String name) {
        super(accession, name);
    }

}

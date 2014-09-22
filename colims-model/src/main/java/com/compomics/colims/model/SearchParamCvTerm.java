/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.compomics.colims.model;

import com.compomics.colims.model.enums.CvTermType;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "search_param_cv_term")
@Entity
public class SearchParamCvTerm extends TypedCvTerm {

    private static final long serialVersionUID = -2536095044338751914L;

    public SearchParamCvTerm() {
    }

    public SearchParamCvTerm(final CvTermType cvTermType, final String ontology, final String label, final String accession, final String name) {
        super(cvTermType, ontology, label, accession, name);
    }

    @Override
    public String toString() {
        return super.toString();
    }

}

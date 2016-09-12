/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.ontology.ols;

import java.util.Comparator;

/**
 *
 * @author Niels Hulstaert
 */
public class OntologyTitleComparator implements Comparator<Ontology> {

    @Override
    public int compare(Ontology o1, Ontology o2) {
        return o1.getTitle().compareToIgnoreCase(o2.getTitle());
    }

}

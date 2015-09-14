package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * This class represents a modification entity in the database. This
 * modification table is part of the identification results. There is another
 * modification table (search_modification), part of the search settings.
 *
 * @author Niels Hulstaert
 */
@Table(name = "modification")
@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Modification extends AbstractModification {

    private static final long serialVersionUID = 497141602900321901L;

    /**
     * The PeptideHasModification instances from the join table between the
     * peptide and modification tables.
     */
    @OneToMany(mappedBy = "modification")
    private List<PeptideHasModification> peptideHasModifications = new ArrayList<>();

    /**
     * No-arg constructor.
     */
    public Modification() {
    }

    /**
     * Constructor.
     *
     * @param name the modification name.
     */
    public Modification(String name) {
        super(name);
    }

    /**
     * Constructor.
     *
     * @param accession the modification accession
     * @param name      the modification name
     */
    public Modification(String accession, String name) {
        super(accession, name);
    }

    public List<PeptideHasModification> getPeptideHasModifications() {
        return peptideHasModifications;
    }

    public void setPeptideHasModifications(List<PeptideHasModification> peptideHasModifications) {
        this.peptideHasModifications = peptideHasModifications;
    }

}
